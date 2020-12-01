package com.hebtu.havefun.entity.User;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Proxy;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author PengHuAnZhi
 * @createTime 2020/11/23 15:21
 * @projectName HaveFun
 * @className UserRelationship.java
 * @description TODO
 */
@Entity
@Table(name = "user_relationship")
@Component
@JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
@Proxy(lazy = false)
//待定，还没有好的解决方案
public class UserRelationship implements Serializable {

    @Id
    @OneToOne(targetEntity = User.class)
    @JoinColumn(name = "follow_id")
    //我的关注
    private User followUser;

    @Id
    @OneToOne(targetEntity = User.class)
    @JoinColumn(name = "followed_id")
    //我的粉丝
    private User followedUser;

    public User getFollowUser() {
        return followUser;
    }

    public void setFollowUser(User followUser) {
        this.followUser = followUser;
    }

    public User getFollowedUser() {
        return followedUser;
    }

    public void setFollowedUser(User followedUser) {
        this.followedUser = followedUser;
    }
}