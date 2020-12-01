package com.hebtu.havefun.service;

import com.hebtu.havefun.Util.CountActivityUtil;
import com.hebtu.havefun.config.ValueConfig;
import com.hebtu.havefun.dao.*;
import com.hebtu.havefun.entity.User.User;
import com.hebtu.havefun.entity.User.UserCollectActivity;
import com.hebtu.havefun.entity.activity.Activity;
import com.hebtu.havefun.entity.activity.ActivityDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    public String[] getRotationChartPictures() {
        return new String[]{ValueConfig.SERVER_URL + "localPictures/1.png",
                ValueConfig.SERVER_URL + "localPictures/2.png",
                ValueConfig.SERVER_URL + "localPictures/3.png",
                ValueConfig.SERVER_URL + "localPictures/4.png",};
    }

    public List<Activity> getActivityList(Integer activityKind, Integer pageNum, Integer pageSize) {
        Long num = countActivityUtil.getActivityNum();
        System.out.println(num);
        System.out.println(pageNum);
        if ((num - 1) / pageSize + 1 < pageNum) {
            System.out.println("触发了");
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
                System.out.print("热门活动:");
                for (Activity activity : content) {
                    System.out.print(activity.getSignUpNum() + " ");
                }
                break;
            case 2://近期活动
                sort = Sort.by(Sort.Direction.DESC, "activityTime");
                pageable = PageRequest.of(pageNum - 1, pageSize, sort);
                page = activityDao.findAll(pageable);
                content = page.getContent();
                System.out.println("近期活动:");
                for (Activity activity : content) {
                    System.out.println(activity.getActivityTime().toString());
                }
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
    public boolean changeCollectActivity(Integer activityId, Integer id, boolean collect) {
        System.out.println(activityId+","+id+","+collect);
        Activity activity = activityDao.getOne(activityId);
        User user = userDao.getOne(id);
        System.out.println(user.toString());
        Date date = new Date();
        if (collect) {//收藏
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
}