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
import com.hebtu.havefun.entity.activity.Picture;
import com.hebtu.havefun.entity.activity.TypeOfKind;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
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
    @Resource
    TypeOfKindDao typeOfKindDao;

//    @Cacheable(value = "activity_constant",key = "'getRotationChartPictures'")
    public String[] getRotationChartPictures() {
        System.out.println("进来了");
        return new String[]{ValueConfig.SERVER_URL + "localPictures/1.png",
                ValueConfig.SERVER_URL + "localPictures/2.png",
                ValueConfig.SERVER_URL + "localPictures/3.png",
                ValueConfig.SERVER_URL + "localPictures/4.png",};
    }

//    @Cacheable(value = "activity",key = "'getActivityList'+#activityKind+','+#pageNum+','+#pageSize")
    public List<Activity> getActivityList(Integer activityKind, Integer pageNum, Integer pageSize) {
        Long num = countActivityUtil.getActivityNum();
        if ((num - 1) / pageSize + 1 < pageNum) {
            return null;
        }
        List<Activity> content = new ArrayList<>();
        Sort sort;
        Pageable pageable;
        Page<Activity> page;
        Specification<Activity> specification = (Specification<Activity>) (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("activityTime"), new Date());
        switch (activityKind) {
            case 1://热门活动
                sort = Sort.by(Sort.Direction.DESC, "collectNum");
                pageable = PageRequest.of(pageNum - 1, pageSize+1, sort);
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
        return content;
    }
//    @Cacheable(value = "activity",key = "'getActivityDetail'+#activityId")
    public ActivityDetail getActivityDetail(Integer activityId) {
        return activityDetailDao.getOne(activityId);
    }
//    @Cacheable(value = "activity-collect",key = "'judgeCollected'+#userId+','+#activityId")
    public boolean judgeCollected(String userId, String activityId) {
        Object object = activityDao.judgeCollectedActivity(userId, activityId);
        return object != null;
    }

    @Transactional
    @Rollback(value = false)
//    @CacheEvict(value = "activity-collect")
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

    @Cacheable(value = "activity-collect",key = "'getCollectedActivities'+#id+','+#pageNum+','+pageSize")
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
    @CacheEvict(value = "activity")
    public boolean addActivity(@RequestParam(value = "file", required = false) List<MultipartFile> files, String activityDetailJson) {
        ActivityDetail activityDetail = JSON.parseObject(activityDetailJson, ActivityDetail.class);
        Set<Picture> pictures = new HashSet<>();
        int count = 0;
        boolean flag;
        for (MultipartFile file : files) {
            count++;
            String fileName = file.getOriginalFilename() + count + Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf("."));
            File dest = new File(ValueConfig.UPLOAD_FOLDER + "activity_pictures/" + fileName);
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
                    picture.setPictureName(ValueConfig.SERVER_URL + "localPictures/activity_pictures/" + activityDetail.getActivity().getActivityId() + "/" + fileName);
                    pictures.add(picture);
                } catch (IllegalStateException | IOException e) {
                    e.printStackTrace();
                }
            }
        }
        activityDetail.setActivityPictures(pictures);
        activityDetailDao.save(activityDetail);
        return true;
    }

//    @Cacheable(value = "activity-enter",key = "'getEnterActivities'+#id+','+#pageNum+','+pageSize")
    public String getEnterActivities(Integer id, Integer pageNum, Integer pageSize) {
        User user = userDao.getOne(id);
        Specification<UserEnterActivity> spec = (Specification<UserEnterActivity>) (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("user"), user);
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        Page<UserEnterActivity> page = userEnterActivityDao.findAll(spec, pageable);
        List<UserEnterActivity> content = page.getContent();
        return content.size() == 0 ? "empty" : JSON.toJSONString(content);
    }

//    @Cacheable(value = "activity",key = "'getPublishActivities'+#id+','+#pageNum+','+pageSize")
    public String getPublishActivities(Integer id, Integer pageNum, Integer pageSize) {
        User user = userDao.getOne(id);
        Specification<UserPublishActivity> spec = (Specification<UserPublishActivity>) (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("user"), user);
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        Page<UserPublishActivity> page = userPublishActivityDao.findAll(spec, pageable);
        List<UserPublishActivity> content = page.getContent();
        return content.size() == 0 ? "empty" : JSON.toJSONString(content);
    }

    public List<Activity> screenTimeActivities(Integer howManyDays, Integer pageNum, Integer pageSize) {
        Specification<Activity> specification = (Specification<Activity>) (root, criteriaQuery, criteriaBuilder) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DATE, howManyDays * (-1));
            return criteriaBuilder.greaterThanOrEqualTo(root.get("activityTime"), calendar.getTime());
        };
        return activityDao.findAll(specification, PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "activityTime"))).getContent();
    }

//    @Cacheable(value = "activity",key = "'screenTypeActivities'+#tag+','+#pageNum+','+pageSize")
    public List<Activity> screenTypeActivities(Integer tag, Integer pageNum, Integer pageSize) {
        TypeOfKind typeOfKind = typeOfKindDao.getOne(tag);
        Specification<Activity> specification = (Specification<Activity>) (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("typeOfKind"), typeOfKind);
        return activityDao.findAll(specification, PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "activityTime"))).getContent();
    }

//    @Cacheable(value = "activity",key = "'screenCostActivities'+#lowCost+','+#highCost+','+#pageNum+','+#pageSize")
    public List<Activity> screenCostActivities(Integer lowCost, Integer highCost, Integer pageNum, Integer pageSize) {
        Specification<Activity> specification = (Specification<Activity>) (root, criteriaQuery, criteriaBuilder) -> {
            Predicate lowPredicate = criteriaBuilder.le(root.get("activityCost").as(Integer.class), highCost);
            Predicate highPredicate = criteriaBuilder.ge(root.get("activityCost").as(Integer.class), lowCost);
            return criteriaBuilder.and(highPredicate, lowPredicate);
        };
        return activityDao.findAll(specification, PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "activityTime"))).getContent();
    }
}