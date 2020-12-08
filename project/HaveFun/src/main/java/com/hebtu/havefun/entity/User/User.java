package com.hebtu.havefun.entity.User;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Proxy;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author PengHuAnZhi
 * @createTime 2020/11/19 11:08
 * @projectName HaveFun
 * @className User.java
 * @description TODO
 */

@Entity
@Table(name = "user")
@Component
@JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
@Proxy(lazy = false)
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    //数据库表自增id
    private Integer id;
    @Column(name = "phone_num")
    //手机号
    private String phoneNum;
    @Column(name = "password")
    //密码
    private String password;
    @Column(name = "user_id")
    //用户id
    private Integer userId;
    @Column(name = "user_name")
    //用户名称
    private String userName;
    @Column(name = "head_portrait")
    //头像路径
    private String headPortrait;
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    //一对一关联对象
    private UserDetail userDetail;

    public User() {
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getHeadPortrait() {
        return headPortrait;
    }

    public void setHeadPortrait(String headPortrait) {
        this.headPortrait = headPortrait;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public UserDetail getUserDetail() {
        return userDetail;
    }

    public void setUserDetail(UserDetail userDetail) {
        this.userDetail = userDetail;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", phoneNum='" + phoneNum + '\'' +
                ", password='" + password + '\'' +
                ", userId=" + userId +
                ", userName='" + userName + '\'' +
                ", headPortrait='" + headPortrait + '\'' +
                ", userDetail=" + userDetail +
                '}';
    }
}