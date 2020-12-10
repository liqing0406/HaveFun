package com.hebtu.havefun.controller;

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
        return activityService.getRotationChartPictures();
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
     * @return 返回List<UserCollectActivity>集合的Json
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
        System.out.println(activityDetailJson);
        return activityService.addActivity(files, activityDetailJson) ? "true" : "false";
    }

    /**
     * @param id       用户id,注意不是getUserId,是getId
     * @param pageNum  页码
     * @param pageSize 页大小
     * @return 返回List<UserCollectActivity>集合的Json
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
     * @param typeName    小类活动的名称
     * @param lowCost     价格区间的低区间
     * @param highCost    价格区间的高区间
     * @param howManyDays 近多少天
     * @param city        市
     * @param county      区
     * @param pageNum     页码
     * @param pageSize    页大小
     * @return 返回一个List<Activity>集合的JSON串，如果集合为空，返回字符串"empty"
     * @description 根据若干条件筛选活动
     */
    @RequestMapping("/screenActivities")
    public String screenActivities(String typeName, Integer lowCost, Integer highCost, Integer howManyDays, String city, String county, Integer pageNum, Integer pageSize) {
        if (howManyDays == null || pageNum == null || pageSize == null || typeName == null || lowCost == null || highCost == null || city == null || county == null) {
            System.out.println("screenActivities Error");
            return "ErrorParameter";
        }
        String activityList = activityService.screenActivities(howManyDays, typeName, lowCost, highCost, city, county, pageNum, pageSize);
        return !"empty".equals(activityList) ? activityList : "empty";
    }

    /**
     * @param id         用户id
     * @param activityId 活动id
     * @return 返回已报名"true"，未报名"false"
     * @description 根据用户id和活动id查询用户是否报名这个活动
     */
    @RequestMapping("/judgeEnterActivity")
    public String judgeEnterActivity(Integer id, Integer activityId) {
        if (id == null || activityId == null) {
            System.out.println("judgeEnterActivity Error");
            return "ErrorParameter";
        }
        return activityService.judgeEnterActivity(id, activityId);
    }

    /**
     * @param activityDetailJson 修改后的ActivityDetail对象的JSON串
     * @return 返回修改成功"true"
     * @description 修改活动信息
     */
    @RequestMapping("/modifyActivity")
    public String modifyActivity(String activityDetailJson) {
        if (activityDetailJson == null) {
            System.out.println("modifyActivity Error");
            return "ErrorParameter";
        }
        return activityService.modifyActivity(activityDetailJson);
    }

    /**
     * @param activityId 活动id
     * @return 返回删除成功"true"或者"false"
     * @description 删除活动，数据库状态字段置0
     */
    @RequestMapping("/deleteActivity")
    public String deleteActivity(Integer activityId) {
        if (activityId == null) {
            System.out.println("deleteActivity Error");
            return "ErrorParameter";
        }
        return "true".equals(activityService.deleteActivity(activityId)) ? "true" : "false";
    }
}