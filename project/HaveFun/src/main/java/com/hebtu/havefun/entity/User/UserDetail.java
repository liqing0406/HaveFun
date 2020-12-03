package com.hebtu.havefun.entity.User;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hebtu.havefun.entity.activity.Activity;
import org.hibernate.annotations.Proxy;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

/**
 * @author PengHuAnZhi
 * @createTime 2020/11/23 14:18
 * @projectName HaveFun
 * @className UserDetail.java
 * @description TODO
 */
@Entity
@Table(name = "user_detail")
@Component
@JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
@Proxy(lazy = false)
public class UserDetail implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_detail_id")
    //数据库表自增id
    private Integer userDetailId;
    @Column(name = "user_sex")
    //性别
    private Integer sex;
    @Column(name = "user_age")
    //年龄
    private Integer age;
    @Column(name = "user_personal_signature")
    //个性签名
    private String personalSignature;
    @Column(name = "user_activity_num")
    //用户参加活动次数
    private Integer numOfActivityForUser;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    //一对一关联用户对象
    private User user;

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
}