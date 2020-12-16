package com.hyphenate.easeui.User;


import com.hyphenate.easeui.activity.Activity;

import java.io.Serializable;
import java.util.Date;

/**
 * @author PengHuAnZhi
 * @createTime 2020/11/29 15:46
 * @projectName HaveFun
 * @className UserCollectActivity.java
 * @description TODO
 */

public class UserCollectActivity implements Serializable {
    //数据库表自增id
    private Integer id;
    //多对一关联用户对象
    private User user;
    //多对一关联活动对象
    private Activity activity;
    //收藏时间
    private Date collectTime;

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

    public Date getCollectTime() {
        return collectTime;
    }

    public void setCollectTime(Date collectTime) {
        this.collectTime = collectTime;
    }

    @Override
    public String toString() {
        return "UserCollectActivity{" +
                "id=" + id +
                ", user=" + user +
                ", activity=" + activity +
                ", collectTime=" + collectTime +
                '}';
    }
}
