package com.hebtu.havefun.entity.User;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author PengHuAnZhi
 * @createTime 2020/12/2 14:28
 * @projectName HaveFun
 * @className UserId.java
 * @description TODO
 */
@Embeddable
public class UserPK implements Serializable {
    //我的关注
    private Integer followUserId;
    //我的粉丝
    private Integer followedUserId;

    public UserPK() {
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

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
}
