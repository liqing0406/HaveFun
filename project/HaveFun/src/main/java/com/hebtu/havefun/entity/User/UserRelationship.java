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
@IdClass(UserPK.class)
//待定，还没有好的解决方案
public class UserRelationship implements Serializable {
    @Id
    @Column(name = "follow_user_id")
    private Integer followUserId;
    @Id
    @Column(name = "followed_user_id")
    private Integer followedUserId;

    public Integer getFollowUserId() {
        return followUserId;
    }

    public void setFollowUserId(Integer followUserId) {
        this.followUserId = followUserId;
    }

    public Integer getFollowedUserId() {
        return followedUserId;
    }

    public void setFollowedUserId(Integer followedUserId) {
        this.followedUserId = followedUserId;
    }

    @Override
    public String toString() {
        return "UserRelationship{" +
                "followUserId='" + followUserId + '\'' +
                ", followedUserId='" + followedUserId + '\'' +
                '}';
    }
}