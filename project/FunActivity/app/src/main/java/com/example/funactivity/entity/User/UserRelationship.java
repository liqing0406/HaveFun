package com.example.funactivity.entity.User;




import java.io.Serializable;

/**
 * @author PengHuAnZhi
 * @createTime 2020/11/23 15:21
 * @projectName HaveFun
 * @className UserRelationship.java
 * @description TODO
 */

//待定，还没有好的解决方案
public class UserRelationship implements Serializable {
    private Integer followUserId;

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