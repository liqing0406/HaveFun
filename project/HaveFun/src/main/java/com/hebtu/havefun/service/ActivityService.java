package com.hebtu.havefun.service;

import com.alibaba.fastjson.JSON;
import com.hebtu.havefun.Util.CountActivityUtil;
import com.hebtu.havefun.config.ValueConfig;
import com.hebtu.havefun.dao.*;
import com.hebtu.havefun.entity.User.User;
import com.hebtu.havefun.entity.User.UserCollectActivity;
import com.hebtu.havefun.entity.User.UserEnterActivity;
import com.hebtu.havefun.entity.User.UserPublishActivity;
import com.hebtu.havefun.entity.activity.Activity;
import com.hebtu.havefun.entity.activity.ActivityDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author PengHuAnZhi
 * @createTime 2020/11/21 15:29
 * @projectName HaveFun
 * @className ActivityService.java
 * @description TODO
 */
@Service
public class ActivityService {
    @Resource
    ActivityDao activityDao;
    @Resource
    CountActivityUtil countActivityUtil;
    @Resource
    ActivityDetailDao activityDetailDao;
    @Resource
    UserDao userDao;
    @Resource
    UserCollectActivityDao userCollectActivityDao;
    @Resource
    UserEnterActivityDao userEnterActivityDao;
    @Resource
    UserPublishActivityDao userPublishActivityDao;

    public String[] getRotationChartPictures() {
        return new String[]{ValueConfig.SERVER_URL + "localPictures/1.png",
                ValueConfig.SERVER_URL + "localPictures/2.png",
                ValueConfig.SERVER_URL + "localPictures/3.png",
                ValueConfig.SERVER_URL + "localPictures/4.png",};
    }

    public List<Activity> getActivityList(Integer activityKind, Integer pageNum, Integer pageSize) {
        Long num = countActivityUtil.getActivityNum();
        if ((num - 1) / pageSize + 1 < pageNum) {
            return null;
        }
        List<Activity> content = new ArrayList<>();
        Sort sort;
        Pageable pageable;
        Page<Activity> page;
        switch (activityKind) {
            case 1://热门活动
                sort = Sort.by(Sort.Direction.DESC, "collectNum");
                pageable = PageRequest.of(pageNum - 1, pageSize, sort);
                page = activityDao.findAll(pageable);
                content = page.getContent();
                break;
            case 2://近期活动
                sort = Sort.by(Sort.Direction.DESC, "activityTime");
                pageable = PageRequest.of(pageNum - 1, pageSize, sort);
                page = activityDao.findAll(pageable);
                content = page.getContent();
                break;
        }
        return content;
    }

    public ActivityDetail getActivityDetail(Integer activityId) {
        return activityDetailDao.getOne(activityId);
    }

    public boolean judgeCollected(String userId, String activityId) {
        Object object = activityDao.judgeCollectedActivity(userId, activityId);
        return object != null;
    }

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
            UserCollectActivity userCollectActivity = userCollectActivityDao.findUserCollectActivitiesByUserAndActivity(user, activity);
            userCollectActivity.setUser(user);
            userCollectActivity.setActivity(activity);
            userCollectActivityDao.delete(userCollectActivity);
            activity.setCollectNum(activity.getCollectNum() - 1);
        }
        return true;
    }

    public String getCollectedActivities(Integer id, Integer pageNum, Integer pageSize) {
        User user = userDao.getOne(id);
        Specification<UserCollectActivity> spec = (Specification<UserCollectActivity>) (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("user"), user);
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        Page<UserCollectActivity> page = userCollectActivityDao.findAll(spec, pageable);
        List<UserCollectActivity> content = page.getContent();
        return content.size() == 0 ? "empty" : JSON.toJSONString(content);
    }

    @Transactional
    @Rollback(value = false)
    public boolean addActivity(Activity activity) {
        System.out.println(activity.toString());
        activityDao.save(activity);
        return true;
    }

    public String getEnterActivities(Integer id, Integer pageNum, Integer pageSize) {
        User user = userDao.getOne(id);
        Specification<UserEnterActivity> spec = (Specification<UserEnterActivity>) (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("user"), user);
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        Page<UserEnterActivity> page = userEnterActivityDao.findAll(spec, pageable);
        List<UserEnterActivity> content = page.getContent();
        return content.size() == 0 ? "empty" : JSON.toJSONString(content);
    }

    public String getPublishActivities(Integer id, Integer pageNum, Integer pageSize) {
        User user = userDao.getOne(id);
        Specification<UserPublishActivity> spec = (Specification<UserPublishActivity>) (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("user"), user);
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        Page<UserPublishActivity> page = userPublishActivityDao.findAll(spec, pageable);
        List<UserPublishActivity> content = page.getContent();
        return content.size() == 0 ? "empty" : JSON.toJSONString(content);
    }
}