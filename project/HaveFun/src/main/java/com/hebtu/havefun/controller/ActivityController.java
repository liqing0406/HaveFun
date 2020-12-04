package com.hebtu.havefun.controller;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.hebtu.havefun.service.ActivityService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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

    /**
     * @return 返回的是一个json串，解析出来是一个list<String>,字符串分别是图片在服务器上的地址
     * @description 获取轮播图
     */
    @RequestMapping("/getRotationChartPictures")
    public String getRotationChartPictures() {
        return new Gson().toJson(activityService.getRotationChartPictures());
    }

    /**
     * @param activityKind 热门或者近期，1或者0
     * @param pageNum      页码
     * @param pageSize     页大小
     * @return List<Activity>集合的JSON串
     * @description 获取活动列表
     */
    @RequestMapping("/getActivityList")
    public String getActivityList(Integer activityKind, Integer pageNum, Integer pageSize) {
        if (activityKind == null || pageNum == null || pageSize == null) {
            System.out.println("getActivityList Error");
            return "ErrorParameter";
        }
        String activities = activityService.getActivityList(activityKind, pageNum, pageSize);
        return "empty".equals(activities) ? "" : activities;
    }

    /**
     * @param activityId 活动的id
     * @return 返回ActivityDetail的json串
     * @description 获取活动详细信息
     */
    @RequestMapping("/getActivityDetail")
    public String getActivityDetail(Integer activityId) {
        if (activityId == null) {
            System.out.println("getActivityDetail Error");
            return "ErrorParameter";
        }
        String activityDetail = activityService.getActivityDetail(activityId);
        return activityDetail != null ? activityDetail : "";
    }

    /**
     * @param id         用户id,注意不是getUserId,是getId
     * @param activityId 活动的id
     * @return 返回true或者false的字符串
     * @description 判断是否收藏
     */
    @RequestMapping("/judgeCollected")
    public String judgeCollected(Integer id, Integer activityId) {
        if (id == null || activityId == null) {
            System.out.println("judgeCollected Error");
            return "ErrorParameter";
        }
        boolean flag = activityService.judgeCollected(id, activityId);
        return flag ? "true" : "false";
    }


    /**
     * @param id       用户id,注意不是getUserId,是getId
     * @param pageNum  页码
     * @param pageSize 页大小
     * @return 返回List<UserCollectActivity>集合
     * @description 获取收藏的活动列表
     */
    @RequestMapping("/getCollectedActivities")
    public String getCollectedActivities(Integer id, Integer pageNum, Integer pageSize) {
        if (id == null || pageNum == null || pageSize == null) {
            System.out.println("getCollectedActivities Error");
            return "ErrorParameter";
        }
        return activityService.getCollectedActivities(id, pageNum, pageSize);
    }

    /**
     * @param files              客户端传递若干MultipartFile文件，要求请求的参数名字都
     *                           是file（就是要重复），这边接收就是自动加入List<MultipartFile>中
     * @param activityDetailJson 客户端将封装好的ActivityDetail类转换为Json串发送过来，
     *                           注意ActivityDetail里面的Activity类也得把数据都封装好，
     *                           添加操作，不需要设置id值，因为数据库id是自增的
     * @return 返回一个添加成功或者失败"true"或者"false"
     * @description 添加活动，接受一个ActivityDetail类的Json串数据，且参数名称为activityJson
     */
    @RequestMapping("/addActivity")
    public String addActivity(@RequestParam("file") List<MultipartFile> files, String activityDetailJson) {
        if (activityDetailJson == null) {
            System.out.println("addActivity Error");
            return "ErrorParameter";
        }
        return activityService.addActivity(files, activityDetailJson) ? "true" : "false";
    }

    /**
     * @param id       用户id,注意不是getUserId,是getId
     * @param pageNum  页码
     * @param pageSize 页大小
     * @return 返回List<UserCollectActivity>集合
     * @description 获取报名的活动列表
     */
    @RequestMapping("/getEnterActivities")
    public String getEnterActivities(Integer id, Integer pageNum, Integer pageSize) {
        if (id == null || pageNum == null || pageSize == null) {
            System.out.println("getEnterActivities Error");
            return "ErrorParameter";
        }
        return activityService.getEnterActivities(id, pageNum, pageSize);
    }

    /**
     * @param id       用户id,注意不是getUserId,是getId
     * @param pageNum  页码
     * @param pageSize 页大小
     * @return 返回List<UserCollectActivity>集合
     * @description 获取发布的活动列表
     */
    @RequestMapping("/getPublishActivities")
    public String getPublishActivities(Integer id, Integer pageNum, Integer pageSize) {
        if (id == null || pageNum == null || pageSize == null) {
            System.out.println("getPublishActivities Error");
            return "ErrorParameter";
        }
        return activityService.getPublishActivities(id, pageNum, pageSize);
    }

    /**
     * @param howManyDays 参数为近多少天，用于筛选符合要求的时间内开始的活动
     * @return 返回一个List<Activity>集合
     * @description 根据时间筛选活动
     */
    @RequestMapping("/screenTimeActivities")
    public String screenTimeActivities(Integer howManyDays, Integer pageNum, Integer pageSize) {
        String activityList = activityService.screenTimeActivities(howManyDays, pageNum, pageSize);
        return "empty".equals(activityList) ? JSON.toJSONString(activityList) : "empty";
    }

    /**
     * @param lowCost  价格区间的小值
     * @param highCost 价格区间的大值
     * @return 返回一个List<Activity>集合
     * @description 根据花费筛选活动
     */
    @RequestMapping("/screenTypeActivities")
    public String screenTypeActivities(Integer lowCost, Integer highCost, Integer pageNum, Integer pageSize) {
        String activityList = activityService.screenCostActivities(lowCost, highCost, pageNum, pageSize);
        return "empty".equals(activityList) ? JSON.toJSONString(activityList) : "empty";
    }

    /**
     * @param tag tag为活动种类的最小划分，即小类而不是大类，值为种类的id
     * @return 返回一个List<Activity>集合
     * @description 根据活动种类筛选活动
     */
    @RequestMapping("/screenCostActivities")
    public String screenCostActivities(Integer tag, Integer pageNum, Integer pageSize) {
        String activityList = activityService.screenTypeActivities(tag, pageNum, pageSize);
        return "empty".equals(activityList) ? JSON.toJSONString(activityList) : "empty";
    }

}