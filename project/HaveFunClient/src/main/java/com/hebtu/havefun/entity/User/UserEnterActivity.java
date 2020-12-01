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
 * @createTime 2020/11/29 15:45
 * @projectName HaveFun
 * @className UserEnterActivity.java
 * @description TODO
 */
@Entity
@Table(name = "enter_activity")
@Component
@JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
@Proxy(lazy = false)
public class UserEnterActivity implements Serializable {
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
    //多对一关联activity对象
    private Activity activity;

    @Column(name = "time")
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
