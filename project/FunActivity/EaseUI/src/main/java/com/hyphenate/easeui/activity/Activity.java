package com.hyphenate.easeui.activity;


import com.hyphenate.easeui.User.User;

import java.io.Serializable;
import java.util.Date;

public class Activity implements Serializable {
    //数据库表自增id
    private Integer activityId;
    //活动标题
    private String activityTile;
    //具体时间
    private Date activityTime;
    //活动类型
    private TypeOfKind typeOfKind;
    //活动花费
    private String activityCost;
    //Todo
    //活动位置
    private ActivityLocation activityLocation;
    //收藏人数
    private Integer collectNum;
    //报名人数
    private Integer signUpNum;
    //转发人数
    private Integer forwardNum;
    //首页图片
    private Picture frontPicture;
    //活动发起人
    private User user;
    //发起时间
    private Date releaseTime;
    //联系方式
    private String activityContact;
    //活动人数
    private Integer personLimit;
    //活动状态字段
    private Integer status;

    public Integer getActivityId() {
        return activityId;
    }

    public void setActivityId(Integer activityId) {
        this.activityId = activityId;
    }

    public String getActivityTile() {
        return activityTile;
    }

    public void setActivityTile(String activityTile) {
        this.activityTile = activityTile;
    }

    public Date getActivityTime() {
        return activityTime;
    }

    public void setActivityTime(Date activityTime) {
        this.activityTime = activityTime;
    }

    public TypeOfKind getTypeOfKind() {
        return typeOfKind;
    }

    public void setTypeOfKind(TypeOfKind typeOfKind) {
        this.typeOfKind = typeOfKind;
    }

    public String getActivityCost() {
        return activityCost;
    }

    public void setActivityCost(String activityCost) {
        this.activityCost = activityCost;
    }

    public ActivityLocation getActivityLocation() {
        return activityLocation;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setActivityLocation(ActivityLocation activityLocation) {
        this.activityLocation = activityLocation;
    }

    public Integer getCollectNum() {
        return collectNum;
    }

    public void setCollectNum(Integer collectNum) {
        this.collectNum = collectNum;
    }

    public Integer getSignUpNum() {
        return signUpNum;
    }

    public void setSignUpNum(Integer signUpNum) {
        this.signUpNum = signUpNum;
    }

    public Integer getForwardNum() {
        return forwardNum;
    }

    public void setForwardNum(Integer forwardNum) {
        this.forwardNum = forwardNum;
    }

    public Picture getFrontPicture() {
        return frontPicture;
    }

    public void setFrontPicture(Picture frontPicture) {
        this.frontPicture = frontPicture;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(Date releaseTime) {
        this.releaseTime = releaseTime;
    }

    public String getActivityContact() {
        return activityContact;
    }

    public void setActivityContact(String activityContact) {
        this.activityContact = activityContact;
    }

    public Integer getPersonLimit() {
        return personLimit;
    }

    public void setPersonLimit(Integer personLimit) {
        this.personLimit = personLimit;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return "Activity{" +
                "activityId=" + activityId +
                ", activityTile='" + activityTile + '\'' +
                ", activityTime=" + activityTime +
                ", typeOfKind=" + typeOfKind +
                ", activityCost='" + activityCost + '\'' +
                ", activityLocation=" + activityLocation +
                ", collectNum=" + collectNum +
                ", signUpNum=" + signUpNum +
                ", forwardNum=" + forwardNum +
                ", frontPicture=" + frontPicture +
                ", user=" + user +
                ", releaseTime=" + releaseTime +
                ", activityContact='" + activityContact + '\'' +
                ", personLimit=" + personLimit +
                ", status=" + status +
                '}';
    }

}
