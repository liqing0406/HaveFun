package com.hyphenate.easeui.User;

import com.hyphenate.easeui.activity.Activity;

import java.io.Serializable;
import java.util.Date;

public class UserPublishActivity implements Serializable {
    //数据库表自增id
    private Integer id;
    //多对一关联用户对象
    private User user;
    //多对一关联活动对象
    private Activity activity;
    //活动发布时间
    private Date publishTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public Date getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
    }

    @Override
    public String toString() {
        return "UserPublishActivity{" +
                "id=" + id +
                ", user=" + user +
                ", activity=" + activity +
                ", publishTime=" + publishTime +
                '}';
    }
}
