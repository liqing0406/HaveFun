package com.hebtu.havefun.service;

import com.alibaba.fastjson.JSON;
import com.hebtu.havefun.config.ValueConfig;
import com.hebtu.havefun.dao.*;
import com.hebtu.havefun.entity.User.User;
import com.hebtu.havefun.entity.User.UserCollectActivity;
import com.hebtu.havefun.entity.User.UserEnterActivity;
import com.hebtu.havefun.entity.User.UserPublishActivity;
import com.hebtu.havefun.entity.activity.*;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
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

    public String[] getRotationChartPictures() {
        return new String[]{ValueConfig.SERVER_URL + "localPictures/1.png",
                ValueConfig.SERVER_URL + "localPictures/2.png",
                ValueConfig.SERVER_URL + "localPictures/3.png",
                ValueConfig.SERVER_URL + "localPictures/4.png",};
    }

    @Cacheable(value = "activity", key = "'getActivityList'+#activityKind+','+#pageNum+','+#pageSize")
    public String getActivityList(Integer activityKind, Integer pageNum, Integer pageSize) {
        List<Activity> content = new ArrayList<>();
        Sort sort;
        Pageable pageable;
        Page<Activity> page;
        Specification<Activity> specification = (Specification<Activity>) (root, criteriaQuery, criteriaBuilder) -> {
            Predicate timePredicate = criteriaBuilder.greaterThanOrEqualTo(root.get("activityTime"), new Date());
            Predicate statusPredicate = criteriaBuilder.equal(root.get("status"), 1);
            return criteriaBuilder.and(timePredicate, statusPredicate);
        };
        switch (activityKind) {
            case 1://热门活动
                sort = Sort.by(Sort.Direction.DESC, "collectNum");
                pageable = PageRequest.of(pageNum - 1, pageSize, sort);
                page = activityDao.findAll(specification, pageable);
                content = page.getContent();
                break;
            case 2://近期活动
                sort = Sort.by(Sort.Direction.DESC, "activityTime");
                pageable = PageRequest.of(pageNum - 1, pageSize, sort);
                page = activityDao.findAll(specification, pageable);
                content = page.getContent();
                break;
        }
        return content.size() != 0 ? JSON.toJSONString(content) : "empty";
    }

    @Cacheable(value = "activity", key = "'getActivityDetail'+#activityId")
    public String getActivityDetail(Integer activityId) {
        ActivityDetail activityDetail = activityDetailDao.getOne(activityId);
        return JSON.toJSONString(activityDetail);
    }

    @Cacheable(value = "activity-collect", key = "'judgeCollected'+#userId+','+#activityId")
    public boolean judgeCollected(Integer userId, Integer activityId) {
        Object object = activityDao.judgeCollectedActivity(userId, activityId);
        return object != null;
    }


    @Cacheable(value = "activity-collect", key = "'getCollectedActivities'+#id+','+#pageNum+','+#pageSize")
    public String getCollectedActivities(Integer id, Integer pageNum, Integer pageSize) {
        User user = userDao.getOne(id);
        Specification<UserCollectActivity> spec = (Specification<UserCollectActivity>) (root, criteriaQuery, criteriaBuilder) -> {
            Predicate userPredicate = criteriaBuilder.equal(root.get("user"), user);
            Predicate activityPredicate = criteriaBuilder.equal(root.get("activity").get("status"), 1);
            return criteriaBuilder.and(activityPredicate, userPredicate);
        };
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        Page<UserCollectActivity> page = userCollectActivityDao.findAll(spec, pageable);
        List<UserCollectActivity> content = page.getContent();
        return content.size() == 0 ? "empty" : JSON.toJSONString(content);
    }

    @Transactional
    @Rollback(value = false)
    @CacheEvict(value = "activity")
    public boolean addActivity(List<MultipartFile> files, String activityDetailJson) {
        ActivityDetail activityDetail = JSON.parseObject(activityDetailJson, ActivityDetail.class);
        Activity activity = activityDetail.getActivity();
        TypeOfKind typeOfKind = typeOfKindDao.findTypeOfKindByTypeName(activity.getTypeOfKind().getTypeName());
        ActivityKind activityKind = activityKindDao.findActivityKindByKindName(activity.getTypeOfKind().getActivityKind().getKindName());
        ActivityLocation activityLocation = new ActivityLocation();
        String province = activityDetail.getActivity().getActivityLocation().getProvince();
        String city = activityDetail.getActivity().getActivityLocation().getCity();
        String county = activityDetail.getActivity().getActivityLocation().getCounty();
        if (province != null && !"".equals(province)) {
            activityLocation.setProvince(province);
        }
        if (city != null && !"".equals(city)) {
            activityLocation.setCity(city);
        }
        if (county != null && !"".equals(county)) {
            activityLocation.setCounty(county);
        }
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
            File dest = new File(ValueConfig.UPLOAD_FOLDER + "activity_pictures/" + activity.getActivityId() + "/" + fileName);
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
                    picture.setPictureName("activity_pictures/" + activityDetail.getActivity().getActivityId() + "/" + fileName);
                    pictures.add(picture);
                    picturesDao.save(picture);
                } catch (IllegalStateException | IOException e) {
                    e.printStackTrace();
                }
            }
        }
        activityDetail.setActivityPictures(pictures);
        activityDetailDao.save(activityDetail);
        return true;
    }

    @Cacheable(value = "activity-enter", key = "'getEnterActivities'+#id+','+#pageNum+','+#pageSize")
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

    @Cacheable(value = "activity", key = "'getPublishActivities'+#id+','+#pageNum+','+#pageSize")
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

    @Cacheable(value = "activity", key = "'getTypeFromKind'+#kindId")
    public String getTypeFromKind(Integer kindId) {
        ActivityKind activityKind = activityKindDao.getOne(kindId);
        Specification<TypeOfKind> specification = (Specification<TypeOfKind>) (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("activityKind"), activityKind);
        List<TypeOfKind> typeOfKindList = typeOfKindDao.findAll(specification);
        return typeOfKindList.size() != 0 ? JSON.toJSONString(typeOfKindList) : "empty";
    }

    @Cacheable(value = "activity", key = "'getTypeFromKind'+#id+','+#activityId")
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

    @Transactional
    @Rollback(value = false)
    public String modifyActivity(String activityDetailJson) {
        ActivityDetail newActivityDetail = JSON.parseObject(activityDetailJson, ActivityDetail.class);
        ActivityDetail activityDetail = activityDetailDao.getOne(newActivityDetail.getActivityDetailId());
        activityDetail.getActivity().setActivityTile(newActivityDetail.getActivity().getActivityTile());
        activityDetail.getActivity().setActivityTime(newActivityDetail.getActivity().getActivityTime());
        activityDetail.getActivity().setActivityCost(newActivityDetail.getActivity().getActivityCost());
        ActivityLocation activityLocation = activityLocationDao.getOne(newActivityDetail.getActivity().getActivityLocation().getLocationId());
        activityDetail.getActivity().setActivityLocation(activityLocation);
        activityDetail.getActivity().setActivityContact(newActivityDetail.getActivity().getActivityContact());
        activityDetail.setActivityInfo(newActivityDetail.getActivityInfo());
        activityDetail.setOtherInfo(newActivityDetail.getOtherInfo());
        activityDetailDao.save(activityDetail);
        return "true";
    }

    @Transactional
    @Rollback(value = false)
    public String deleteActivity(Integer activityId) {
        Activity activity = activityDao.getOne(activityId);
        activity.setStatus(0);
        activityDao.save(activity);
        return "true";
    }

    public String screenActivities(Integer howManyDays, String typeName, Integer lowCost, Integer highCost, String city, String county, Integer pageNum, Integer pageSize) {
        TypeOfKind typeOfKind = null;
        if (!"empty".equals(typeName)) {
            typeOfKind = typeOfKindDao.findTypeOfKindByTypeName(typeName);
        }
        ActivityLocation activityLocation = null;
        if (!"empty".equals(city) && !"empty".equals(county)) {
            activityLocation = activityLocationDao.findActivityLocationByCityAndCounty(city, county);
        }
        TypeOfKind finalTypeOfKind = typeOfKind;
        ActivityLocation finalActivityLocation = activityLocation;
        Specification<Activity> specification = (Specification<Activity>) (root, criteriaQuery, criteriaBuilder) -> {
            //时间的筛选条件
            Predicate timePredicate = criteriaBuilder.notEqual(root.get("activityTime"), null);
            if (howManyDays != -1) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                calendar.add(Calendar.DATE, howManyDays * (-1));
                timePredicate = criteriaBuilder.greaterThanOrEqualTo(root.get("activityTime"), calendar.getTime());
            }
            //种类的筛选
            Predicate typePredicate = criteriaBuilder.notEqual(root.get("typeOfKind"), null);
            if (finalTypeOfKind != null) {
                typePredicate = criteriaBuilder.equal(root.get("typeOfKind"), finalTypeOfKind);
            }
            //价格的筛选
            Predicate lowPredicate = criteriaBuilder.notEqual(root.get("activityCost"), null);
            if (highCost != -1) {
                lowPredicate = criteriaBuilder.le(root.get("activityCost").as(Integer.class), highCost);
            }
            Predicate highPredicate = criteriaBuilder.notEqual(root.get("activityCost"), null);
            if (lowCost != -1) {
                highPredicate = criteriaBuilder.ge(root.get("activityCost").as(Integer.class), lowCost);
            }
            //位置的筛选
            Predicate locationPredicate = criteriaBuilder.notEqual(root.get("activityLocation"), null);
            if (finalActivityLocation != null) {
                locationPredicate = criteriaBuilder.equal(root.get("activityLocation"), finalActivityLocation);
            }
            return criteriaBuilder.and(locationPredicate, highPredicate, lowPredicate, typePredicate, timePredicate);
        };
        List<Activity> activityList = activityDao.findAll(specification, PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "activityTime"))).getContent();
        return activityList.size() != 0 ? JSON.toJSONString(activityList) : "empty";
    }
}