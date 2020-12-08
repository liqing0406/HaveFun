package com.hebtu.havefun.entity.activity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hebtu.havefun.entity.User.User;
import org.hibernate.annotations.Proxy;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author PengHuAnZhi
 * @createTime 2020/11/20 15:53
 * @projectName HaveFun
 * @className Activity.java
 * @description TODO
 */
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Entity
@Table(name = "activity")
@JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
@Proxy(lazy = false)
public class Activity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //数据库表自增id
    private Integer activityId;
    //活动标题
    @Column(name = "activity_title")
    private String activityTile;
    //具体时间
    @Column(name = "activity_time")
    private Date activityTime;
    //活动类型
    @OneToOne
    @JoinColumn(name = "type_of_kind")
    private TypeOfKind typeOfKind;
    //活动花费
    @Column(name = "activity_cost")
    private String activityCost;
    //Todo
    //活动位置
    @OneToOne(mappedBy = "activity", cascade = CascadeType.ALL)
    private ActivityLocation activityLocation;
    //收藏人数
    @Column(name = "collect_num")
    private Integer collectNum;
    //报名人数
    @Column(name = "sign_up_num")
    private Integer signUpNum;
    //转发人数
    @Column(name = "forward_num")
    private Integer forwardNum;
    //首页图片
    @OneToOne(mappedBy = "activity", cascade = CascadeType.ALL)
    private Picture frontPicture;
    //活动发起人
    @OneToOne(targetEntity = User.class)
    private User user;
    //发起时间
    @Column(name = "release_time")
    private Date releaseTime;
    //联系方式
    @Column(name = "activity_contact")
    private String activityContact;
    //活动人数
    @Column(name = "person_limit")
    private Integer personLimit;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    public String getActivityCost() {
        return activityCost;
    }

    public void setActivityCost(String activityCost) {
        this.activityCost = activityCost;
    }

    public ActivityLocation getActivityLocation() {
        return activityLocation;
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

    public TypeOfKind getTypeOfKind() {
        return typeOfKind;
    }

    public void setTypeOfKind(TypeOfKind typeOfKind) {
        this.typeOfKind = typeOfKind;
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
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
