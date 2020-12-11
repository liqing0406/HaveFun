package com.example.funactivity.entity.User;

import java.io.Serializable;

public class User implements Serializable {
    //数据库表自增id
    private Integer id;
    //手机号
    private String phoneNum;
    //密码
    private String password;
    //用户id
    private Integer userId;
    //用户名称
    private String userName;
    //头像路径
    private String headPortrait;
    //一对一关联对象
    private UserDetail userDetail;

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