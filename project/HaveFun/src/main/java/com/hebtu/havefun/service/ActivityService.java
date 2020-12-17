package com.hebtu.havefun.service;

import com.alibaba.fastjson.JSON;
import com.hebtu.havefun.dao.*;
import com.hebtu.havefun.entity.Constant;
import com.hebtu.havefun.entity.User.User;
import com.hebtu.havefun.entity.User.UserCollectActivity;
import com.hebtu.havefun.entity.User.UserEnterActivity;
import com.hebtu.havefun.entity.User.UserPublishActivity;
import com.hebtu.havefun.entity.activity.*;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
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
    ActivityDetailDao activityDetailDao;
    @Resource
    UserDao userDao;
    @Resource
    UserCollectActivityDao userCollectActivityDao;
    @Resource
    UserEnterActivityDao userEnterActivityDao;
    @Resource
    UserPublishActivityDao userPublishActivityDao;
    @Resource
    TypeOfKindDao typeOfKindDao;
    @Resource
    ActivityKindDao activityKindDao;
    @Resource
    ActivityLocationDao activityLocationDao;
    @Resource
    PicturesDao picturesDao;
    @Resource
    Constant constant;
    @Resource
    RedisTemplate<String, Object> redisTemplate;

    /**
     * @return 返回一个List<String>类型的Json，为轮播图的图片资源地址
     * @description 这个地方返回的本身可以用字符串数组，因为业务逻辑都是返回的Json串，
     * 而且json不能直接对字符串数组进行序列化，所以这个地方使用String的集合类型
     */
    @Cacheable(value = "constant", key = "'getRotationChartPictures'")
    public String getRotationChartPictures() {
        List<String> list = new ArrayList<>();
        list.add(constant.getServerUrl() + "localPictures/1.png");
        list.add(constant.getServerUrl() + "localPictures/2.png");
        list.add(constant.getServerUrl() + "localPictures/3.png");
        list.add(constant.getServerUrl() + "localPictures/4.png");
        return JSON.toJSONString(list);
    }

    /**
     * @param activityKind 需要获取哪个形式的活动列表，1表示热门活动，2表示近期活动
     * @param pageNum      页码
     * @param pageSize     页大小
     * @return 返回符合条件的List<Activity>集合的Json
     */
    @Cacheable(value = "activities", key = "'getActivityList'+#activityKind+','+#city+','+#pageNum+','+#pageSize")
    public String getActivityList(Integer activityKind, String city, Integer pageNum, Integer pageSize) {
        List<Activity> content = new ArrayList<>();
        Sort sort;
        Pageable pageable;
        Page<Activity> page;
        Specification<Activity> specification = (Specification<Activity>) (root, criteriaQuery, criteriaBuilder) -> {
            //所有能够返回给客户端用户业务需要的地方都需要筛选出还未开始的活动,展示用户发布的活动不需要筛选
            Predicate timePredicate = criteriaBuilder.greaterThanOrEqualTo(root.get("activityTime"), new Date());
            //筛选出有效的活动，即1表示有效
            Predicate statusPredicate = criteriaBuilder.equal(root.get("status"), 1);
            Predicate locationPredicate = criteriaBuilder.equal(root.get("activityLocation").get("city"), city);
            return criteriaBuilder.and(timePredicate, statusPredicate, locationPredicate);
        };
        switch (activityKind) {
            case 1://热门活动
                //根据活动收藏数量排序
                sort = Sort.by(Sort.Direction.DESC, "collectNum");
                //分页显示
                pageable = PageRequest.of(pageNum - 1, pageSize, sort);
                page = activityDao.findAll(specification, pageable);
                content = page.getContent();
                break;
            case 2://近期活动
                //根据活动的时间排序
                sort = Sort.by(Sort.Direction.DESC, "activityTime");
                //分页显示
                pageable = PageRequest.of(pageNum - 1, pageSize, sort);
                page = activityDao.findAll(specification, pageable);
                content = page.getContent();
                break;
        }
        return content.size() != 0 ? JSON.toJSONString(content) : "empty";
    }

    /**
     * @param activityId 活动的id
     * @return 返回ActivityDetail的json串
     * @description 获取活动详细信息
     */
    @Cacheable(value = "activity", key = "#activityId+'_getActivityDetail'")
    public String getActivityDetail(Integer activityId) {
        Activity activity = activityDao.getOne(activityId);
        ActivityDetail activityDetail = activityDetailDao.findActivityDetailByActivity(activity);
        return JSON.toJSONString(activityDetail);
    }

    /**
     * @param userId     用户id,注意不是getUserId,是getId
     * @param activityId 活动的id
     * @return 返回true或者false的字符串
     * @description 判断是否收藏
     */
    @Cacheable(value = "activity-collect", key = "#userId+'_'+#activityId+'judgeCollected'")
    public boolean judgeCollected(Integer userId, Integer activityId) {
        Object object = activityDao.judgeCollectedActivity(userId, activityId);
        return object != null;
    }

    /**
     * @param id       用户id,注意不是getUserId,是getId
     * @param pageNum  页码
     * @param pageSize 页大小
     * @return 返回List<UserCollectActivity>集合的Json
     * @description 获取收藏的活动列表
     */
    @Cacheable(value = "activity-collect", key = "#id+'_'+'getCollectedActivities'+','+#pageNum+','+#pageSize")
    public String getCollectedActivities(Integer id, Integer pageNum, Integer pageSize) {
        User user = userDao.getOne(id);
        Specification<UserCollectActivity> spec = (Specification<UserCollectActivity>) (root, criteriaQuery, criteriaBuilder) -> {
            //用户为user
            Predicate userPredicate = criteriaBuilder.equal(root.get("user"), user);
            //有效活动
            Predicate activityPredicate = criteriaBuilder.equal(root.get("activity").get("status"), 1);
            return criteriaBuilder.and(activityPredicate, userPredicate);
        };
        //分页
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        Page<UserCollectActivity> page = userCollectActivityDao.findAll(spec, pageable);
        List<UserCollectActivity> content = page.getContent();
        return content.size() == 0 ? "empty" : JSON.toJSONString(content);
    }

    /**
     * @param files              客户端传递若干MultipartFile文件，要求请求的参数名字都
     *                           是file（就是要重复），这边接收就是自动加入List<MultipartFile>中
     * @param activityDetailJson 客户端将封装好的ActivityDetail类转换为Json串发送过来，
     *                           注意ActivityDetail里面的Activity类也得把数据都封装好，
     *                           添加操作，不需要设置id值，因为数据库id是自增的
     * @return 返回一个添加成功或者失败"true"或者"false"
     * @description 添加活动，接受一个ActivityDetail类的Json串数据，且参数名称为activityJson,本身可以接收一个file数组，但是业务被砍了，这里也不影响，所以保留
     */
    @Transactional
    @Rollback(value = false)
    public boolean addActivity(List<MultipartFile> files, String activityDetailJson) {
        //将activityDetailJson反序列化为activityDetail
        ActivityDetail activityDetail = JSON.parseObject(activityDetailJson, ActivityDetail.class);
        //获取activityDetail中的activity
        Activity activity = activityDetail.getActivity();
        //根据活动中的小类名称找到TypeOfKind
        TypeOfKind typeOfKind = typeOfKindDao.findTypeOfKindByTypeName(activity.getTypeOfKind().getTypeName());
        //根据活动的大类找到ActivityKind
        ActivityKind activityKind = activityKindDao.findActivityKindByKindName(activity.getTypeOfKind().getActivityKind().getKindName());
        //初始化一个活动位置对象
        ActivityLocation activityLocation = new ActivityLocation();
        //获取活动位置对象需要的省市区信息
        String province = activityDetail.getActivity().getActivityLocation().getProvince();
        String city = activityDetail.getActivity().getActivityLocation().getCity();
        String county = activityDetail.getActivity().getActivityLocation().getCounty();
        //对活动位置对象进行赋值
        if (province != null && !"".equals(province)) {
            activityLocation.setProvince(province);
        }
        if (city != null && !"".equals(city)) {
            activityLocation.setCity(city);
        }
        if (county != null && !"".equals(county)) {
            activityLocation.setCounty(county);
        }
        //执行保存操作
        activityLocation.setActivity(activity);
        typeOfKind.setActivityKind(activityKind);
        activity.setTypeOfKind(typeOfKind);
        activity.setCollectNum(0);
        activity.setStatus(1);
        activity.setSignUpNum(0);
        activity.setForwardNum(0);
        activity.setActivityLocation(activityLocation);
        activity.setReleaseTime(new Date());
        activity.setActivityTime(activityDetail.getActivity().getActivityTime());
        activityDao.save(activity);
        activityDetail.setActivity(activity);
        activityDetailDao.save(activityDetail);
        activityDetail = activityDetailDao.getOne(activityDetail.getActivityDetailId());
        Set<Picture> pictures = new HashSet<>();

        boolean flag;
        for (MultipartFile file : files) {
            String fileName = "front.png";
            File dest = new File(constant.getUploadPath() + "activity_pictures/" + activity.getActivityId() + "/" + fileName);
            if (!dest.getParentFile().exists()) { //判断文件父目录是否存在
                flag = dest.getParentFile().mkdir();
            } else {
                flag = true;
            }
            if (flag) {
                try {
                    file.transferTo(dest); //保存文件
                    Picture picture = new Picture();
                    picture.setActivityDetail(activityDetail);
                    picture.setActivity(activityDetail.getActivity());
                    //保存图片路径
                    picture.setPictureName("activity_pictures/" + activityDetail.getActivity().getActivityId() + "/" + fileName);
                    pictures.add(picture);
                    picturesDao.save(picture);
                } catch (IllegalStateException | IOException e) {
                    e.printStackTrace();
                }
            }
        }
        //将图片对象保存到activity Detail中
        activityDetail.setActivityPictures(pictures);
        activityDetailDao.save(activityDetail);
        //保存到用户发布活动的表里面
        UserPublishActivity userPublish = new UserPublishActivity();
        userPublish.setActivity(activity);
        userPublish.setUser(activity.getUser());
        userPublish.setPublishTime(new Date());
        userPublishActivityDao.save(userPublish);
        //用户发布了报名的活动信息，随之将和该用户下的关于报名活动的部分和总的活动列表缓存删除
        Set<String> userPublishActivity = redisTemplate.keys("activity-enter::" + activity.getUser().getId() + "_*");
        Set<String> activityList = redisTemplate.keys("activities*");
        if (!CollectionUtils.isEmpty(userPublishActivity)) {
            redisTemplate.delete(userPublishActivity);
        }
        if (!CollectionUtils.isEmpty(activityList)) {
            redisTemplate.delete(activityList);
        }
        return true;
    }

    /**
     * @param id       用户id,注意不是getUserId,是getId
     * @param pageNum  页码
     * @param pageSize 页大小
     * @return 返回List<UserCollectActivity>集合的Json
     * @description 获取报名的活动列表
     */
    @Cacheable(value = "activity-enter", key = "#id+'_'+'getEnterActivities'+','+#pageNum+','+#pageSize")
    public String getEnterActivities(Integer id, Integer pageNum, Integer pageSize) {
        User user = userDao.getOne(id);
        Specification<UserEnterActivity> spec = (Specification<UserEnterActivity>) (root, criteriaQuery, criteriaBuilder) -> {
            Predicate userPredicate = criteriaBuilder.equal(root.get("user"), user);
            Predicate activityPredicate = criteriaBuilder.equal(root.get("activity").get("status"), 1);
            return criteriaBuilder.and(activityPredicate, userPredicate);
        };
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        Page<UserEnterActivity> page = userEnterActivityDao.findAll(spec, pageable);
        List<UserEnterActivity> content = page.getContent();
        return content.size() == 0 ? "empty" : JSON.toJSONString(content);
    }

    /**
     * @param id       用户id,注意不是getUserId,是getId
     * @param pageNum  页码
     * @param pageSize 页大小
     * @return 返回List<UserCollectActivity>集合的json
     * @description 获取发布的活动列表
     */
    @Cacheable(value = "activity-publish", key = "#id+'_'+'getPublishActivities'+','+#pageNum+','+#pageSize")
    public String getPublishActivities(Integer id, Integer pageNum, Integer pageSize) {
        User user = userDao.getOne(id);
        Specification<UserPublishActivity> spec = (Specification<UserPublishActivity>) (root, criteriaQuery, criteriaBuilder) -> {
            Predicate userPredicate = criteriaBuilder.equal(root.get("user"), user);
            Predicate activityPredicate = criteriaBuilder.equal(root.get("activity").get("status"), 1);
            return criteriaBuilder.and(activityPredicate, userPredicate);
        };
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        Page<UserPublishActivity> page = userPublishActivityDao.findAll(spec, pageable);
        List<UserPublishActivity> content = page.getContent();
        return content.size() == 0 ? "empty" : JSON.toJSONString(content);
    }

    /**
     * @param id         用户id
     * @param activityId 活动id
     * @return 返回已报名"true"，未报名"false"
     * @description 根据用户id和活动id查询用户是否报名这个活动
     */
    @Cacheable(value = "activity-enter", key = "#id+'_'+#activityId+'judgeEnterActivity'")
    public String judgeEnterActivity(Integer id, Integer activityId) {
        User user = userDao.getOne(id);
        Activity activity = activityDao.getOne(activityId);
        UserEnterActivity userEnterActivitiesByUserAndActivity = userEnterActivityDao.findUserEnterActivitiesByUserAndActivity(user, activity);
        if (userEnterActivitiesByUserAndActivity != null) {
            return "true";
        } else {
            return "false";
        }
    }

    /**
     * @param activityDetailJson 修改后的ActivityDetail对象的JSON串
     * @return 返回修改成功"true"
     * @description 修改活动信息
     */
    @Transactional
    @Rollback(value = false)
    public String modifyActivity(String activityDetailJson) {
        //新的activityDetail对象反序列化
        ActivityDetail newActivityDetail = JSON.parseObject(activityDetailJson, ActivityDetail.class);
        //将老的activityDetail对象获取出来
        ActivityDetail activityDetail = activityDetailDao.getOne(newActivityDetail.getActivityDetailId());
        //获取老的activity
        Activity activity = activityDetail.getActivity();
        //对所有修改的数据进行更新
        activity.setActivityTile(newActivityDetail.getActivity().getActivityTile());
        activity.setActivityTime(newActivityDetail.getActivity().getActivityTime());
        activity.setActivityCost(newActivityDetail.getActivity().getActivityCost());
        ActivityLocation activityLocation = activityLocationDao.getOne(newActivityDetail.getActivity().getActivityLocation().getLocationId());
        activity.setActivityLocation(activityLocation);
        activity.setActivityContact(newActivityDetail.getActivity().getActivityContact());
        activityDetail.setActivity(activity);
        activityDetail.setActivityInfo(newActivityDetail.getActivityInfo());
        activityDetail.setOtherInfo(newActivityDetail.getOtherInfo());
        activityDetailDao.save(activityDetail);
        //用户更新了活动信息，随之将和该用户下的关于报名活动的部分和总的活动列表缓存删除
        releaseCache(activity.getUser().getId());
        Set<String> activityDetailCache = redisTemplate.keys("activity::" + activity.getActivityId() + "_getActivityDetail*");
        if (!CollectionUtils.isEmpty(activityDetailCache)) {
            redisTemplate.delete(activityDetailCache);
        }
        return "true";
    }

    /**
     * @param activityId 活动id
     * @return 返回删除成功"true"或者"false"
     * @description 删除活动，数据库状态字段置0
     */
    @Transactional
    @Rollback(value = false)
    public String deleteActivity(Integer activityId) {
        Activity activity = activityDao.getOne(activityId);
        //在这个数据的是资源的时代，删除数据不是很理想，直接把状态字段置为0
        activity.setStatus(0);
        activityDao.save(activity);
        //用户删除了活动信息，随之将和该用户下的关于报名活动的部分和总的活动列表缓存删除
        releaseCache(activity.getUser().getId());
        return "true";
    }

    //根据用户id删除用户下面发布的活动，和所有的活动列表
    public void releaseCache(Integer id) {
        Set<String> userPublishActivity = redisTemplate.keys("activity-publish::" + id + "_*");
        Set<String> activityList = redisTemplate.keys("activities*");
        if (!CollectionUtils.isEmpty(userPublishActivity)) {
            redisTemplate.delete(userPublishActivity);
        }
        if (!CollectionUtils.isEmpty(activityList)) {
            redisTemplate.delete(activityList);
        }
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
    //筛选的可能性太多了，不做缓存
    public String screenActivities(Integer howManyDays, String typeName, Integer lowCost, Integer highCost, String city, String county, Integer pageNum, Integer pageSize) {
        TypeOfKind typeOfKind = null;
        if (!"empty".equals(typeName)) {
            typeOfKind = typeOfKindDao.findTypeOfKindByTypeName(typeName);
        }
        TypeOfKind finalTypeOfKind = typeOfKind;
        Specification<Activity> specification = (Specification<Activity>) (root, criteriaQuery, criteriaBuilder) -> {
            //时间的筛选条件
            //恒真条件
            Predicate timePredicate = criteriaBuilder.equal(criteriaBuilder.literal(1), 1);
            //如果用户对时间进行过筛选，则替换恒真条件为用户提供的时间
            if (howManyDays != -1) {
                Date date = new Date();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.add(Calendar.DATE, howManyDays);
                //又要在用户给定的时间范围内，还要保证活动还未开始
                timePredicate = criteriaBuilder.and(criteriaBuilder.lessThanOrEqualTo(root.get("activityTime"), calendar.getTime()), criteriaBuilder.greaterThanOrEqualTo(root.get("activityTime"), date));
            }
            //种类的筛选
            //恒真条件
            Predicate typePredicate = criteriaBuilder.equal(criteriaBuilder.literal(1), 1);
            //如果用户对种类进行过筛选，则替换恒真条件为用户提供的种类
            if (finalTypeOfKind != null) {
                typePredicate = criteriaBuilder.equal(root.get("typeOfKind"), finalTypeOfKind);
            }
            //价格的筛选
            //恒真条件
            Predicate lowPredicate = criteriaBuilder.equal(criteriaBuilder.literal(1), 1);
            //如果用户对价格进行过筛选，则替换恒真条件为用户提供的价格
            if (highCost != -1) {
                lowPredicate = criteriaBuilder.le(root.get("activityCost").as(Integer.class), highCost);
            }
            //恒真条件
            Predicate highPredicate = criteriaBuilder.equal(criteriaBuilder.literal(1), 1);
            //如果用户对价格进行过筛选，则替换恒真条件为用户提供的价格
            if (lowCost != -1) {
                highPredicate = criteriaBuilder.ge(root.get("activityCost").as(Integer.class), lowCost);
            }
            //位置的筛选
            //恒真条件
            Predicate locationPredicate = criteriaBuilder.equal(criteriaBuilder.literal(1), 1);
            //如果用户对位置进行过筛选，则替换恒真条件为用户提供的位置
            if (!"empty".equals(city) || !"empty".equals(county)) {
                locationPredicate = criteriaBuilder.and(criteriaBuilder.equal(root.get("activityLocation").get("city"), city), criteriaBuilder.equal(root.get("activityLocation").get("county"), county));
            }
            //有效活动
            Predicate activityPredicate = criteriaBuilder.equal(root.get("status"), 1);
            return criteriaBuilder.and(locationPredicate, highPredicate, lowPredicate, typePredicate, timePredicate, activityPredicate);
        };
        List<Activity> activityList = activityDao.findAll(specification, PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "activityTime"))).getContent();
        return activityList.size() != 0 ? JSON.toJSONString(activityList) : "empty";
    }
}