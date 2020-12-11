package com.example.funactivity.entity.User;


import com.example.funactivity.entity.activity.Activity;

import java.io.Serializable;
import java.util.Set;

public class UserDetail implements Serializable {
    //数据库表自增id
    private Integer userDetailId;
    //性别
    private Integer sex;
    //年龄
    private Integer age;
    //个性签名
    private String personalSignature;
    //用户参加活动次数
    private Integer numOfActivityForUser;
    //一对一关联用户对象
    private User user;
    //收藏的活动
    private Set<Activity> myCollectionActivity;
    //报名的活动
    private Set<Activity> myEnterActivity;
    //我发布的活动
    private Set<Activity> myPublishedActivity;
    //参加活动次数
    private Integer activityCount;

    public UserDetail() {
    }

    public Integer getUserDetailId() {
        return userDetailId;
    }

    public void setUserDetailId(Integer userDetailId) {
        this.userDetailId = userDetailId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getPersonalSignature() {
        return personalSignature;
    }

    public void setPersonalSignature(String personalSignature) {
        this.personalSignature = personalSignature;
    }

    public Integer getNumOfActivityForUser() {
        return numOfActivityForUser;
    }

    public void setNumOfActivityForUser(Integer numOfActivityForUser) {
        this.numOfActivityForUser = numOfActivityForUser;
    }

    public Set<Activity> getMyCollectionActivity() {
        return myCollectionActivity;
    }

    public void setMyCollectionActivity(Set<Activity> myCollectionActivity) {
        this.myCollectionActivity = myCollectionActivity;
    }

    public Set<Activity> getMyEnterActivity() {
        return myEnterActivity;
    }

    public void setMyEnterActivity(Set<Activity> myEnterActivity) {
        this.myEnterActivity = myEnterActivity;
    }

    public Set<Activity> getMyPublishedActivity() {
        return myPublishedActivity;
    }

    public void setMyPublishedActivity(Set<Activity> myPublishedActivity) {
        this.myPublishedActivity = myPublishedActivity;
    }

    public Integer getActivityCount() {
        return activityCount;
    }

    public void setActivityCount(Integer activityCount) {
        this.activityCount = activityCount;
    }
}
