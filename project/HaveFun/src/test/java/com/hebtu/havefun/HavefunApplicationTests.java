package com.hebtu.havefun;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.gson.JsonArray;
import com.hebtu.havefun.config.ValueConfig;
import com.hebtu.havefun.dao.ActivityDao;
import com.hebtu.havefun.dao.UserDao;
import com.hebtu.havefun.dao.UserDetailDao;
import com.hebtu.havefun.entity.activity.Activity;
import com.hebtu.havefun.entity.activity.Picture;
import com.hebtu.havefun.service.ActivityService;
import com.hebtu.havefun.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@SpringBootTest
class HavefunApplicationTests {
    @Resource
    UserDao userDao;
    @Resource
    UserDetailDao userDetailDao;
    @Resource
    UserService userService;
    @Resource
    ActivityDao activityDao;
    @Resource
    ActivityService activityService;

    @Test
    void contextLoads() throws InterruptedException {
//        for (int i = 0; i < 10; i++) {
//            Activity activity = new Activity();
//            activity.setSignUpNum(i);
//            activity.setActivityTile("title_"+i);
//            activity.setActivityTile("time_"+i);
//            activity.setActivityLocation("location_"+i);
//            activity.setActivityCost("cost_"+i);
//            activity.setActivityContact("contact_"+i);
//            activity.setCollectNum(i);
//            Picture p = new Picture();
//            p.setPictureName(ValueConfig.SERVER_URL+"/activity/"+i+"/front.png");
//            activity.setFrontPicture(p);
//            activityDao.save(activity);
//        }
//        String result = JSON.toJSONString(activityService.getActivityList(1, 1, 2));
//        System.out.println(result);
//        int i = 11;
//        System.out.println((i-1)/2 + 1);
//        for(String str: activityService.getRotationChartPictures()) {
//            System.out.println(str);
//        }
//        System.out.println(activityService.judgeCollected("1", "2"));
    }
}