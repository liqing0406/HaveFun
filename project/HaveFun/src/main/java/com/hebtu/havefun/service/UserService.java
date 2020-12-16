package com.hebtu.havefun.service;

import com.alibaba.fastjson.JSON;
import com.hebtu.havefun.dao.*;
import com.hebtu.havefun.entity.Constant;
import com.hebtu.havefun.entity.User.*;
import com.hebtu.havefun.entity.activity.Activity;
import com.hebtu.havefun.entity.exception.BizException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
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
    @Resource
    Constant constant;
    @Resource
    RedisTemplate<String, Object> redisTemplate;

    /**
     * @param phoneNum 电话号码
     * @return 返回"true"或者"false"
     * @description 判断是否注册
     */
    //将用户是否注册结果加入缓存
    @Cacheable(value = "user-register", key = "#phoneNum+'_judgeRegistered'")
    public boolean judgeRegistered(String phoneNum) {
        User user = userDao.findUserByPhoneNum(phoneNum);
        return user != null;
    }

    /**
     * @param phoneNum 手机号码
     * @param password 密码
     * @return 返回"true"或者"false"
     * @description 注册
     */
    //注册这个业务逻辑就不需要缓存
    @Transactional
    @Rollback(value = false)
    public boolean register(String phoneNum, String password) {
        User user = new User();
        //用户基本信息的录入，部分为默认数据
        user.setPhoneNum(phoneNum);
        user.setPassword(password);
        //开始想的是用一个类似于qq号的唯一表示，但是后面写着写着就忽视了，也没咋用，就是一个基数加上当前数据库中现在的用户总数
        user.setUserId(constant.getUserId() + Integer.parseInt(userDao.count() + ""));
        user.setHeadPortrait("man.png");
        user.setUserName("飞翔的企鹅");
        userDetail.setNumOfActivityForUser(0);
        userDetail.setSex(1);
        userDetail.setAge(0);
        userDetail.setResidentIdCard("未认证");
        userDetail.setPersonalSignature("我是一条冷酷的签名");
        user.setUserDetail(userDetail);
        userDetail.setUser(user);
        userDetailDao.save(userDetail);
        userDao.save(user);
        //当用户注册后，更新判断用户是否注册的缓存
        Set<String> keys = redisTemplate.keys("user-register::" + phoneNum + "_*");
        if (!CollectionUtils.isEmpty(keys)) {
            redisTemplate.delete(keys);
        }
        return true;
    }

    /**
     * @param phoneNum 手机号
     * @param password 密码
     * @return 返回的是user的对象Json串
     * @description 登录
     */
    //用户登录后把用户对象作为json串返回，这里直接放在缓存中，就不用每次都查
    @Cacheable(value = "user-login", key = "#phoneNum+'_login'")
    public String login(String phoneNum, String password) {
        User user = userDao.findUserByPhoneNumAndPassword(phoneNum, password);
        return user != null ? JSON.toJSONString(user) : "";
    }

    /**
     * @param phoneNum 电话号码
     * @param password 密码
     * @return 返回"true"或"false"
     * @description 修改密码
     */
    @Transactional
    @Rollback(value = false)
    //修改了密码不会更新用户的基本信息，所以这个地方不删除缓存
    public boolean modifyPassword(String phoneNum, String password) {
        User user = userDao.findUserByPhoneNum(phoneNum);
        user.setPassword(password);
        userDao.save(user);
        return true;
    }

    /**
     * @param activityId 活动id
     * @param id         用户id,注意不是getUserId,是getId
     * @return 返回报名成功"true",如果超出活动人数上限，返回"enough",已经报名了"exists"
     * @description 报名活动
     */
    @Transactional
    @Rollback(value = false)
    public String enrollActivity(Integer activityId, Integer id) {
        User user = userDao.getOne(id);
        Activity activity = activityDao.getOne(activityId);
        UserEnterActivity userEnterActivity = userEnterActivityDao.findUserEnterActivitiesByUserAndActivity(user, activity);
        if (userEnterActivity != null) {
            //表示已经报名了
            return "exists";
        } else {
            if (activity.getSignUpNum() >= activity.getPersonLimit()) {
                //如果这个活动的报名人数已经满了，就返回不能报名
                return "enough";
            }
            activity.setSignUpNum(activity.getSignUpNum() + 1);
            userEnterActivity = new UserEnterActivity();
            userEnterActivity.setUser(user);
            userEnterActivity.setActivity(activity);
            userEnterActivity.setEnterTime(new Date());
            userEnterActivityDao.save(userEnterActivity);
            //用户更新了报名的活动信息，随之将和该用户下的关于报名活动的部分缓存删除
            Set<String> keys = redisTemplate.keys("activity-enter::" + id + "_*");
            if (!CollectionUtils.isEmpty(keys)) {
                redisTemplate.delete(keys);
            }
            return "true";
        }
    }

    /**
     * @param activityId 活动id
     * @param id         用户id,注意不是getUserId,是getId
     * @return 返回取消报名成功"true"或者失败"false"
     * @description 取消报名活动
     */
    @Transactional
    @Rollback(value = false)
    public String cancelEnrollActivity(Integer activityId, Integer id) {
        User user = userDao.getOne(id);
        Activity activity = activityDao.getOne(activityId);
        activity.setSignUpNum(activity.getSignUpNum() - 1);
        UserEnterActivity userEnterActivity = userEnterActivityDao.findUserEnterActivitiesByUserAndActivity(user, activity);
        userEnterActivityDao.delete(userEnterActivity);
        //用户更新了报名的活动信息，随之将和该用户下的关于报名活动的部分缓存删除
        Set<String> keys = redisTemplate.keys("activity-enter::" + id + "_*");
        if (!CollectionUtils.isEmpty(keys)) {
            redisTemplate.delete(keys);
        }
        return "success";
    }

    /**
     * @param activityId 活动的id
     * @param id         用户id,注意不是getUserId,是getId
     * @param tag        是否收藏，发送给我的是"false"或者"true"
     * @return 返回是否操作成功
     * @description 收藏或者取消收藏
     */
    @Transactional
    @Rollback(value = false)
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
        //用户更新了收藏的活动信息，活动的收藏人数也会更新，这个地方也得清除，按理说不能这样频繁，但是先这样吧
        Set<String> keys = redisTemplate.keys("activit*");
        if (!CollectionUtils.isEmpty(keys)) {
            redisTemplate.delete(keys);
        }
        return true;
    }

    /**
     * @param id                用户id,注意不是getUserId,是getIds
     * @param personalSignature 新的个性签名
     * @return 返回"true"或者"false"
     * @description 修改个性签名
     */
    @Transactional
    @Rollback(value = false)
    public Boolean modifyPersonalSignature(Integer id, String personalSignature) {
        User user = userDao.getOne(id);
        user.getUserDetail().setPersonalSignature(personalSignature);
        userDao.save(user);
        //当用户更改了个人的基本信息后，删除缓存中保存的用户基本信息
        redisTemplate.keys("user-login::" + user.getPhoneNum() + "_*");
        return true;
    }

    /**
     * @param followId   当前用户的id，即关注者id，注意不是getUserId,是getId
     * @param followedId 对方id，即被关注者id，注意不是getUserId,是getId
     * @return 返回关注成功还是失败true或者false
     * @description 关注用户
     */
    @Transactional
    @Rollback(value = false)
    public boolean followUser(Integer followId, Integer followedId) {
        if (userDao.existsById(followId) && userDao.existsById(followedId)) {
            UserRelationship userRelationship = new UserRelationship();
            userRelationship.setFollowUserId(followId);
            userRelationship.setFollowedUserId(followedId);
            userRelationshipDao.save(userRelationship);
            //如果关注了用户，直接给缓存中对应判断是否关注设置true返回值
            redisTemplate.opsForValue().set("user-follow::" + followId + "," + followedId + "_judgeFollow", true);
            //关注这个业务逻辑中被关注followedId的粉丝数量自增一
            redisTemplate.opsForValue().increment("user-follow::" + followedId + "_getFollowedCount");
            //关注这个业务逻辑中关注者followedId的关注数量自增一
            redisTemplate.opsForValue().increment("user-follow::" + followId + "_getFollowCount");
            //关注后，将此人的关注列表缓存删除
            redisTemplate.delete("user-follow::" + followId + "_getFollowUsers");
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param followId   当前用户的id，即关注者id，注意不是getUserId,是getId
     * @param followedId 对方id，即被关注者id，注意不是getUserId,是getId
     * @return 返回取消关注成功还是失败true或者false
     * @description 关注用户
     */
    @Transactional
    @Rollback(value = false)
    public boolean unFollowUser(Integer followId, Integer followedId) {
        UserRelationship userRelationship = userRelationshipDao.findUserRelationshipByFollowUserIdAndFollowedUserId(followId, followedId);
        userRelationshipDao.delete(userRelationship);
        //如果取关了用户，直接给缓存中对应判断是否关注设置false返回值
        redisTemplate.opsForValue().set("user-follow::" + followId + "," + followedId + "_judgeFollow", false);
        //取消关注这个业务逻辑中被关注者followedId的粉丝数量自减一
        redisTemplate.opsForValue().decrement("user-follow::" + followedId + "_getFollowedCount");
        //取消关注这个业务逻辑中关注者followId的关注数量自减一
        redisTemplate.opsForValue().decrement("user-follow::" + followId + "_getFollowCount");
        //取消关注后，将此人的关注列表缓存删除
        redisTemplate.delete("user-follow::" + followId + "_getFollowUsers");
        return true;
    }

    /**
     * @param followId   当前用户的id，即关注者id，注意不是getUserId,是getId
     * @param followedId 对方id，即被关注者id，注意不是getUserId,是getId
     * @return 返回是否关注 "true"或者"false"
     * @description 判断是否关注
     */
    @Cacheable(value = "user-follow", key = "#followId+','+followedId+'_judgeFollow'")
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

    /**
     * @param id 当前用户的id
     * @return 返回粉丝数量
     * @description 获取用户被多少人关注，即粉丝数量
     */
    //将用户的粉丝人数存入内存
    @Cacheable(value = "user-follow", key = "#id+'_getFollowedCount'")
    public Long getFollowedCount(Integer id) {
        Specification<UserRelationship> specification = (Specification<UserRelationship>) (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("followUserId"), id);
        return userRelationshipDao.count(specification);
    }

    /**
     * @param id 当前用户的id
     * @return 返回关注人数
     * @description 获取用户关注了多少人
     */
    //将用户的关注人数存入内存
    @Cacheable(value = "user-follow", key = "#id+'_getFollowCount'")
    public Long getFollowCount(Integer id) {
        Specification<UserRelationship> specification = (Specification<UserRelationship>) (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("followedUserId"), id);
        return userRelationshipDao.count(specification);
    }

    /**
     * @param id 用户的id
     * @return 返回user对象的JSON串
     * @description 根据id获取用户的信息，用于点击某个用户头像进入个人主页
     */
    //将用户基本信息存入缓存
    @Cacheable(value = "user", key = "#id+'_getUser'")
    public User getUser(Integer id) {
        return userDao.getOne(id);
    }

    /**
     * @param id 当前用户的id
     * @return 返回一个用户列表的Json串，如果为空则返回empty
     * @description 获取当前用户的关注列表
     */
    //将这个user的关注列表存入缓存
    @Cacheable(value = "user-follow", key = "#id+'_getFollowUsers'")
    public List<User> getFollowUsers(Integer id) {
        Specification<UserRelationship> specification = (Specification<UserRelationship>) (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("followUserId"), id);
        return getUsers(specification);
    }

    /**
     * @param id 当前用户的id
     * @return 返回一个用户列表的Json串，如果为空则返回empty
     * @description 获取当前用户的粉丝列表
     */
    //将这个user的粉丝列表存入缓存
    @Cacheable(value = "user-follow", key = "#id+'_getFollowedUsers'")
    public List<User> getFollowedUsers(Integer id) {
        Specification<UserRelationship> specification = (Specification<UserRelationship>) (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("followedUserId"), id);
        return getUsers(specification);
    }

    /**
     * @param specification 条件，区分是获取关注还是粉丝列表
     * @return 返回符合条件的用户列表
     * @description 综合获取关注和粉丝列表的重复代码
     */
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

    /**
     * @param headPortrait 使用OkHttp3传输图片，类型为MultipartFile，名称为headPortrait
     * @param id           用户id
     * @return 返回修改成功还是失败
     * @description 更改用户头像
     */
    @Transactional
    @Rollback(value = false)
    @CacheEvict(value = "user-login", allEntries = true)
    public Boolean modifyUserHeadPortrait(MultipartFile headPortrait, Integer id) {
        User user = userDao.getOne(id);
        boolean flag;
        //拼接文件名称
        String fileName = "head" + (Objects.requireNonNull(headPortrait.getOriginalFilename())).substring(headPortrait.getOriginalFilename().lastIndexOf("."));
        //创建在服务器中存储的路径file对象
        File dest = new File(constant.getUploadPath() + "/user/user_" + user.getId() + "/" + fileName);
        if (!dest.getParentFile().exists()) { //判断文件父目录是否存在
            flag = dest.getParentFile().mkdir();
        } else {
            flag = true;
        }
        if (flag) {
            try {
                headPortrait.transferTo(dest); //保存文件
                user.setHeadPortrait("user/user_" + user.getId() + "/" + fileName);
            } catch (IllegalStateException | IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        return true;
    }

    /**
     * @param userName 用户新昵称，请求参数名称为userName
     * @param id       用户id
     * @return 返回修改成功还是失败
     * @description 更改用户昵称
     */
    @Transactional
    @Rollback(value = false)
    @CacheEvict(value = "user-login", allEntries = true)
    public boolean modifyUserName(String userName, Integer id) {
        User user = userDao.getOne(id);
        user.setUserName(userName);
        userDao.save(user);
        return true;
    }

    /**
     * @param sex 用户性别字段，1或者0
     * @param id  用户id
     * @return 返回修改成功还是失败
     * @description 修改用户性别
     */
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

    /**
     * @param id             用户id
     * @param residentIdCard 身份证号
     * @return 返回是否认证成功，"true"或者"false"
     * @description 根据用户id设置用户身份证号
     */
    @Transactional
    @Rollback(value = false)
    @CacheEvict(value = "user-login", allEntries = true)
    public String idCardAuthentication(Integer id, String residentIdCard, String realName) {
        User user = userDao.getOne(id);
        userDetail = userDetailDao.findUserDetailByUser(user);
        userDetail.setResidentIdCard(residentIdCard);
        userDetail.setRealName(realName);
        userDetailDao.save(userDetail);
        return "true";
    }

    @Cacheable(value = "user-login",key = "#phoneNum+'_getUserInfo'")
    public String getUserInfo(String phoneNum) {
        return JSON.toJSONString(userDao.findUserByPhoneNum(phoneNum));
    }
}