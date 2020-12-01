package com.hebtu.havefun.controller;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.hebtu.havefun.entity.activity.Activity;
import com.hebtu.havefun.entity.activity.ActivityDetail;
import com.hebtu.havefun.service.ActivityService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author PengHuAnZhi
 * @createTime 2020/11/21 15:23
 * @projectName HaveFun
 * @className ActivityController.java
 * @description TODO
 */
@RestController
@RequestMapping("activity")
public class ActivityController {

    @Resource
    ActivityService activityService;

    @RequestMapping("/getRotationChartPictures")
    public String getRotationChartPictures() {
        Gson gson = new Gson();
        return gson.toJson(activityService.getRotationChartPictures());
    }

    @RequestMapping("/getActivityList")
    public String getActivityList(Integer activityKind, Integer pageNum, Integer pageSize) {
        List<Activity> activities = activityService.getActivityList(activityKind, pageNum, pageSize);
        return activities != null ? JSON.toJSONString(activities) : "";
    }

    @RequestMapping("/getActivityDetail")
    public String getActivityDetail(Integer activityId) {
        ActivityDetail activityDetail = activityService.getActivityDetail(activityId);
        return activityDetail != null ? JSON.toJSONString(activityDetail) : "";
    }

    @RequestMapping("/judgeCollected")
    public String judgeCollected(String userId,String activityId){
        boolean flag = activityService.judgeCollected(userId,activityId);
        return flag ? "true" : "false";
    }

    @RequestMapping("/changeCollectActivity")
    public String changeCollectActivity(Integer activityId,Integer id,boolean collect){
        boolean flag = activityService.changeCollectActivity(activityId,id,collect);
        return flag ? "true" : "false";
    }

    @RequestMapping("/getCollectedActivities")
    public String getCollectedActivities(Integer id,Integer pageNum,Integer pageSize){
        return activityService.getCollectedActivities(id,pageNum,pageSize);
    }
}