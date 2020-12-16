package com.hyphenate.easeui.User;


import com.hyphenate.easeui.activity.Activity;

import java.io.Serializable;
import java.util.Date;

public class UserEnterActivity implements Serializable {
    //数据库表自增id
    private Integer id;
    //多对一关联用户对象
    private User user;
    //多对一关联activity对象
    private Activity activity;
    //报名时间
    private Date enterTime;

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


    public Date getEnterTime() {
        return enterTime;
    }

    public void setEnterTime(Date enterTime) {
        this.enterTime = enterTime;
    }

    @Override
    public String toString() {
        return "UserEnterActivity{" +
                "id=" + id +
                ", user=" + user +
                ", activity=" + activity +
                ", enterTime=" + enterTime +
                '}';
    }
}
