package com.hebtu.havefun.entity.User;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hebtu.havefun.entity.activity.Activity;
import org.hibernate.annotations.Proxy;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author PengHuAnZhi
 * @createTime 2020/11/29 15:46
 * @projectName HaveFun
 * @className UserPublishActivity.java
 * @description TODO
 */
@Entity
@Table(name = "publish_activity")
@Component
@JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
@Proxy(lazy = false)
public class UserPublishActivity implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //数据库表自增id
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    //多对一关联用户对象
    private User user;

    @ManyToOne
    @JoinColumn(name = "activity_id")
    //多对一关联活动对象
    private Activity activity;

    @Column(name = "time")
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
