package com.hebtu.havefun.service;

import com.alibaba.fastjson.JSON;
import com.hebtu.havefun.config.ValueConfig;
import com.hebtu.havefun.dao.*;
import com.hebtu.havefun.entity.User.*;
import com.hebtu.havefun.entity.activity.Activity;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
    ActivityDao activityDao;
    @Resource
    UserEnterActivityDao userEnterActivityDao;
    @Resource
    UserRelationshipDao userRelationshipDao;
    @Resource
    UserCollectActivityDao userCollectActivityDao;

    @Cacheable(value = "user-register", key = "'judgeRegistered'+#phoneNum")
    public boolean judgeRegistered(String phoneNum) {
        User user = userDao.findUserByPhoneNum(phoneNum);
        return user != null;
    }

    @Transactional
    @Rollback(value = false)
    @CacheEvict(value = "user-register")
    public boolean register(String phoneNum, String password) {
        User user = new User();
        user.setPhoneNum(phoneNum);
        user.setPassword(password);
        user.setUserId(ValueConfig.USER_ID + Integer.parseInt(userDao.count() + ""));
        user.setHeadPortrait("man.png");
        user.setUserName("飞翔的企鹅");
        userDetail.setNumOfActivityForUser(0);
        userDetail.setSex(1);
        userDetail.setAge(0);
        userDetail.setPersonalSignature("我是一条冷酷的签名");
        user.setUserDetail(userDetail);
        userDetail.setUser(user);
        userDetailDao.save(userDetail);
        userDao.save(user);
        return true;
    }

    @Cacheable(value = "user-login", key = "'login'+#phoneNum+','+#password")
    public String login(String phoneNum, String password) {
        User user = userDao.findUserByPhoneNumAndPassword(phoneNum, password);
        return user != null ? JSON.toJSONString(user) : "";
    }

    @Transactional
    @Rollback(value = false)
    @CacheEvict(value = "user-login")
    public boolean modifyPassword(String phoneNum, String password) {
        User user = userDao.findUserByPhoneNum(phoneNum);
        user.setPassword(password);
        userDao.save(user);
        return true;
    }

    @Transactional
    @Rollback(value = false)
    @CacheEvict(value = "activity-enter")
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
            userEnterActivity.setEnterTime(new Date());
            userEnterActivityDao.save(userEnterActivity);
            return "true";
        }
    }

    @Transactional
    @Rollback(value = false)
    @CacheEvict(value = "activity-enter", allEntries = true)
    public String cancelEnrollActivity(Integer activityId, Integer id) {
        User user = userDao.getOne(id);
        Activity activity = activityDao.getOne(activityId);
        UserEnterActivity userEnterActivity = userEnterActivityDao.findUserEnterActivitiesByUserAndActivity(user, activity);
        userEnterActivityDao.delete(userEnterActivity);
        return "success";
    }

    @Transactional
    @Rollback(value = false)
    @CacheEvict(value = "activity-collect", allEntries = true)
    public boolean changeCollectActivity(Integer activityId, Integer id, boolean tag) {
        Activity activity = activityDao.getOne(activityId);
        User user = userDao.getOne(id);
        Date date = new Date();
        if (tag) {//收藏
            UserCollectActivity userCollectActivity = new UserCollectActivity();
            userCollectActivity.setUser(user);
            userCollectActivity.setActivity(activity);
            userCollectActivity.setCollectTime(date);
            userCollectActivityDao.save(userCollectActivity);
            activity.setCollectNum(activity.getCollectNum() + 1);
        } else {//取消收藏
            System.out.println(activityId + "," + id + "," + tag);
            UserCollectActivity userCollectActivity = userCollectActivityDao.findUserCollectActivitiesByUserAndActivity(user, activity);
            userCollectActivity.setUser(user);
            userCollectActivity.setActivity(activity);
            userCollectActivityDao.delete(userCollectActivity);
            activity.setCollectNum(activity.getCollectNum() - 1);
        }
        return true;
    }

    @Transactional
    @Rollback(value = false)
    @CacheEvict(value = "user-login", allEntries = true)
    public Boolean modifyPersonalSignature(Integer id, String personalSignature) {
        User user = userDao.getOne(id);
        user.getUserDetail().setPersonalSignature(personalSignature);
        userDao.save(user);
        return true;
    }

    @Transactional
    @Rollback(value = false)
    @CacheEvict(value = "user-follow")
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
    @CacheEvict(value = "user-follow")
    public boolean unFollowUser(Integer followId, Integer followedId) {
        UserRelationship userRelationship = userRelationshipDao.findUserRelationshipByFollowUserIdAndFollowedUserId(followId, followedId);
        userRelationshipDao.delete(userRelationship);
        return true;
    }

    @Cacheable(value = "user-follow", key = "'judgeFollow'+#followId+','+followedId")
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

    @Cacheable(value = "user-follow", key = "'getFollowedCount'+#id")
    public Long getFollowedCount(Integer id) {
        Specification<UserRelationship> specification = (Specification<UserRelationship>) (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("followUserId"), id);
        return userRelationshipDao.count(specification);
    }

    @Cacheable(value = "user-follow", key = "'getFollowCount'+#id")
    public Long getFollowCount(Integer id) {
        Specification<UserRelationship> specification = (Specification<UserRelationship>) (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("followedUserId"), id);
        return userRelationshipDao.count(specification);
    }

    @Cacheable(value = "user", key = "'getUser'+#id")
    public User getUser(Integer id) {
        return userDao.getOne(id);
    }

    @Cacheable(value = "user-follow", key = "'getFollowUsers'+#id")
    public List<User> getFollowUsers(Integer id) {
        Specification<UserRelationship> specification = (Specification<UserRelationship>) (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("followUserId"), id);
        return getUsers(specification);
    }

    @Cacheable(value = "user-follow", key = "'getFollowedUsers'+#id")
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
    @CacheEvict(value = "user-login", allEntries = true)
    public Boolean modifyUserHeadPortrait(MultipartFile headPortrait, Integer id) {
        User user = userDao.getOne(id);
        boolean flag;
        String fileName = "head" + (Objects.requireNonNull(headPortrait.getOriginalFilename())).substring(headPortrait.getOriginalFilename().lastIndexOf("."));
        File dest = new File(ValueConfig.UPLOAD_FOLDER + "/user/" + user.getId() + "/" + fileName);
        if (!dest.getParentFile().exists()) { //判断文件父目录是否存在
            flag = dest.getParentFile().mkdir();
        } else {
            flag = true;
        }
        if (flag) {
            try {
                headPortrait.transferTo(dest); //保存文件
                user.setHeadPortrait("localPictures/user/" + user.getId() + "/" + fileName);
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
    @CacheEvict(value = "user-login", allEntries = true)
    public boolean modifyUserName(String userName, Integer id) {
        User user = userDao.getOne(id);
        user.setUserName(userName);
        userDao.save(user);
        return true;
    }

    @Transactional
    @Rollback(value = false)
    @CacheEvict(value = "user-login", allEntries = true)
    public boolean modifyUserSex(Integer sex, Integer id) {
        User user = userDao.getOne(id);
        UserDetail userDetail = userDetailDao.findUserDetailByUser(user);
        userDetail.setSex(sex);
        userDetailDao.save(userDetail);
        return true;
    }
}