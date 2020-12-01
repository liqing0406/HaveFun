package com.hebtu.havefun.service;

import com.alibaba.fastjson.JSON;
import com.hebtu.havefun.Util.CountUserUtil;
import com.hebtu.havefun.config.ValueConfig;
import com.hebtu.havefun.dao.ActivityDao;
import com.hebtu.havefun.dao.UserDao;
import com.hebtu.havefun.dao.UserDetailDao;
import com.hebtu.havefun.dao.UserEnterActivityDao;
import com.hebtu.havefun.entity.User.User;
import com.hebtu.havefun.entity.User.UserDetail;
import com.hebtu.havefun.entity.User.UserEnterActivity;
import com.hebtu.havefun.entity.activity.Activity;
import org.springframework.stereotype.Service;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

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
}