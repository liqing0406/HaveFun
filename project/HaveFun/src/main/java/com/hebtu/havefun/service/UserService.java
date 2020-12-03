package com.hebtu.havefun.service;

import com.alibaba.fastjson.JSON;
import com.hebtu.havefun.Util.CountUserUtil;
import com.hebtu.havefun.config.ValueConfig;
import com.hebtu.havefun.dao.*;
import com.hebtu.havefun.entity.Messages;
import com.hebtu.havefun.entity.User.User;
import com.hebtu.havefun.entity.User.UserDetail;
import com.hebtu.havefun.entity.User.UserEnterActivity;
import com.hebtu.havefun.entity.User.UserRelationship;
import com.hebtu.havefun.entity.activity.Activity;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.persistence.criteria.Predicate;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author PengHuAnZhi
 * @createTime 2020/11/19 11:29
 * @projectName HaveFun
 * @className UserService.java
 * @description TODO
 */
@Service
public class UserService {
    @Resource
    UserDao userDao;
    @Resource
    UserDetail userDetail;
    @Resource
    UserDetailDao userDetailDao;
    @Resource
    CountUserUtil countUserUtil;
    @Resource
    ActivityDao activityDao;
    @Resource
    UserEnterActivityDao userEnterActivityDao;
    @Resource
    MessagesDao messagesDao;
    @Resource
    UserRelationshipDao userRelationshipDao;

    public boolean judgeRegistered(String phoneNum) {
        User user = userDao.findUserByPhoneNum(phoneNum);
        return user != null;
    }

    @Transactional
    @Rollback(value = false)
    public boolean register(String phoneNum, String password) {
        User user = new User();
        user.setPhoneNum(phoneNum);
        user.setPassword(password);
        user.setUserId(ValueConfig.USER_ID + Integer.parseInt(countUserUtil.CountUser() + ""));
        user.setHeadPortrait("localPictures/man.png");
        user.setUserName("飞翔的企鹅");
        userDetail.setNumOfActivityForUser(0);
        userDetail.setSex(1);
        userDetail.setAge(0);
        userDetail.setPersonalSignature("我是一条冷酷的签名");
        user.setUserDetail(userDetail);
        userDetail.setUser(user);
        userDetailDao.save(userDetail);
        return true;
    }

    public String login(String phoneNum, String password) {
        User user = userDao.findUserByPhoneNumAndPassword(phoneNum, password);
        return user != null ? JSON.toJSONString(user) : "";
    }

    @Transactional
    @Rollback(value = false)
    public boolean modifyPassword(String phoneNum, String password) {
        User user = userDao.findUserByPhoneNum(phoneNum);
        user.setPassword(password);
        userDao.save(user);
        return true;
    }

    @Transactional
    @Rollback(value = false)
    public String enrollActivity(Integer activityId, Integer id) {
        User user = userDao.getOne(id);
        Activity activity = activityDao.getOne(activityId);
        UserEnterActivity userEnterActivity = userEnterActivityDao.findUserEnterActivitiesByUserAndActivity(user, activity);
        if (userEnterActivity != null) {
            return "exists";
        } else {
            userEnterActivity = new UserEnterActivity();
            userEnterActivity.setUser(user);
            userEnterActivity.setActivity(activity);
            userEnterActivityDao.save(userEnterActivity);
            return "true";
        }
    }

    @Transactional
    @Rollback(value = false)
    public Boolean modifyPersonalSignature(Integer id, String personalSignature) {
        User user = userDao.getOne(id);
        user.getUserDetail().setPersonalSignature(personalSignature);
        userDao.save(user);
        return true;
    }

    @Transactional
    @Rollback(value = false)
    public Messages addMsg(Integer sender, Integer receiver, String msg) {
        Messages messages = new Messages();
        User sendUser = userDao.getOne(sender);
        User receiveUser = userDao.getOne(receiver);
        messages.setSendUser(sendUser);
        messages.setReceiveUser(receiveUser);
        messages.setTime(new Date());
        messages.setRead(false);
        messagesDao.save(messages);
        return messages;
    }

    public List<Messages> getMsg(Integer sender, Integer receiver, Integer pageNum, Integer pageSize) {
        Sort sort = Sort.by(Sort.Direction.DESC, "time");
        if (userDao.existsById(sender) && userDao.existsById(receiver)) {
            User sendUser = userDao.getOne(sender);
            User receiveUser = userDao.getOne(receiver);
            Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sort);
            Specification<Messages> spec = (Specification<Messages>) (root, criteriaQuery, criteriaBuilder) -> {
                Predicate sendPredicate = criteriaBuilder.equal(root.get("sendUser"), sendUser);
                Predicate receivePredicate = criteriaBuilder.equal(root.get("receiveUser"), receiveUser);
                return criteriaBuilder.and(sendPredicate, receivePredicate);
            };
            Page<Messages> page = messagesDao.findAll(spec, pageable);
            List<Messages> messagesList = page.getContent();
            return messagesList.size() == 0 ? null : messagesList;
        } else {
            return null;
        }
    }

    public long getMsgNum(Integer sender, Integer receiver) {
        User sendUser = userDao.getOne(sender);
        User receiveUser = userDao.getOne(receiver);
        Specification<Messages> spec = (Specification<Messages>) (root, criteriaQuery, criteriaBuilder) -> {
            Predicate sendPredicate = criteriaBuilder.equal(root.get("sendUser"), sendUser);
            Predicate receivePredicate = criteriaBuilder.equal(root.get("receiveUser"), receiveUser);
            return criteriaBuilder.and(sendPredicate, receivePredicate);
        };
        return messagesDao.count(spec);
    }

    public List<Messages> freshMsg(Integer otherId, Integer mineId) {
        if (userDao.existsById(otherId) && userDao.existsById(mineId)) {
            User sendUser = userDao.getOne(otherId);
            User receiveUser = userDao.getOne(mineId);
            Specification<Messages> spec = (Specification<Messages>) (root, criteriaQuery, criteriaBuilder) -> {
                Predicate statusPredicate = criteriaBuilder.equal(root.get("isRead"), false);
                Predicate sendPredicate = criteriaBuilder.equal(root.get("sendUser"), sendUser);
                Predicate receivePredicate = criteriaBuilder.equal(root.get("receiveUser"), receiveUser);
                return criteriaBuilder.and(statusPredicate, sendPredicate, receivePredicate);
            };
            return messagesDao.findAll(spec);
        } else {
            return null;
        }
    }

    @Transactional
    @Rollback(value = false)
    public boolean followUser(Integer followId, Integer followedId) {
        if (userDao.existsById(followId) && userDao.existsById(followedId)) {
            UserRelationship userRelationship = new UserRelationship();
            userRelationship.setFollowUserId(followId);
            userRelationship.setFollowedUserId(followedId);
            userRelationshipDao.save(userRelationship);
            return true;
        } else {
            return false;
        }
    }

    @Transactional
    @Rollback(value = false)
    public boolean unFollowUser(Integer followId, Integer followedId) {
        UserRelationship userRelationship = userRelationshipDao.findUserRelationshipByFollowUserIdAndFollowedUserId(followId, followedId);
        userRelationshipDao.delete(userRelationship);
        return true;
    }

    public boolean judgeFollow(Integer followId, Integer followedId) {
        UserRelationship userRelationship = new UserRelationship();
        userRelationship.setFollowUserId(followId);
        userRelationship.setFollowedUserId(followedId);
        Specification<UserRelationship> specification = (Specification<UserRelationship>) (root, criteriaQuery, criteriaBuilder) -> {
            Predicate followUserPredicate = criteriaBuilder.equal(root.get("followUserId"), followId);
            Predicate followedUserPredicate = criteriaBuilder.equal(root.get("followedUserId"), followedId);
            return criteriaBuilder.and(followUserPredicate, followedUserPredicate);
        };
        List<UserRelationship> userRelationships = userRelationshipDao.findAll(specification);
        return userRelationships.size() != 0;
    }

    public Long getFollowedCount(Integer id) {
        Specification<UserRelationship> specification = (Specification<UserRelationship>) (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("followUserId"), id);
        return userRelationshipDao.count(specification);
    }

    public Long getFollowCount(Integer id) {
        Specification<UserRelationship> specification = (Specification<UserRelationship>) (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("followedUserId"), id);
        return userRelationshipDao.count(specification);
    }

    public User getUser(Integer id) {
        return userDao.getOne(id);
    }

    public List<Messages> getMessageList(Integer id) {
        User user = userDao.getOne(id);
        Specification<Messages> specification = (Specification<Messages>) (root, criteriaQuery, criteriaBuilder) -> {
            Predicate sendPredicate = criteriaBuilder.equal(root.get("sendUser"), user);
            Predicate receivePredicate = criteriaBuilder.equal(root.get("receiveUser"), user);
            return criteriaBuilder.or(sendPredicate, receivePredicate);
        };
        return messagesDao.findAll(specification);
    }

    public List<User> getFollowUsers(Integer id) {
        Specification<UserRelationship> specification = (Specification<UserRelationship>) (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("followUserId"), id);
        return getUsers(specification);
    }

    public List<User> getFollowedUsers(Integer id) {
        Specification<UserRelationship> specification = (Specification<UserRelationship>) (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("followedUserId"), id);
        return getUsers(specification);
    }

    private List<User> getUsers(Specification<UserRelationship> specification) {
        List<UserRelationship> userRelationships = userRelationshipDao.findAll(specification);
        if (userRelationships.size() == 0) {
            return null;
        } else {
            List<User> userList = new ArrayList<>();
            for (UserRelationship userRelationship : userRelationships) {
                userList.add(userDao.getOne(userRelationship.getFollowedUserId()));
            }
            return userList;
        }
    }

    @Transactional
    @Rollback(value = false)
    public Boolean modifyUserHeadPortrait(MultipartFile headPortrait, Integer id) {
        User user = userDao.getOne(id);
        boolean flag;
        String fileName = "head" + (headPortrait.getOriginalFilename()).substring(headPortrait.getOriginalFilename().lastIndexOf("."));
        File dest = new File(ValueConfig.UPLOAD_FOLDER + "/user/" + user.getUserName() + "/" + fileName);
        if (!dest.getParentFile().exists()) { //判断文件父目录是否存在
            flag = dest.getParentFile().mkdir();
        } else {
            flag = true;
        }
        if (flag) {
            try {
                headPortrait.transferTo(dest); //保存文件
                user.setHeadPortrait(ValueConfig.SERVER_URL + "localPictures/user/" + user.getUserName() + "/" + fileName);
            } catch (IllegalStateException | IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        return true;
    }


    @Transactional
    @Rollback(value = false)
    public boolean modifyUserName(String userName, Integer id) {
        User user = userDao.getOne(id);
        user.setUserName(userName);
        userDao.save(user);
        return true;
    }

    @Transactional
    @Rollback(value = false)
    public boolean modifyUserSex(Integer sex, Integer id) {
        User user = userDao.getOne(id);
        UserDetail userDetail = userDetailDao.findUserDetailByUser(user);
        userDetail.setSex(sex);
        userDetailDao.save(userDetail);
        return true;
    }
}