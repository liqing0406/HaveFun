package com.hebtu.havefun.dao;

import com.hebtu.havefun.entity.User.UserRelationship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author PengHuAnZhi
 * @createTime 2020/12/2 13:05
 * @projectName HaveFun
 * @className UserRelationshipDao.java
 * @description TODO
 */
public interface UserRelationshipDao extends JpaRepository<UserRelationship,Integer>, JpaSpecificationExecutor<UserRelationship> {
    UserRelationship findUserRelationshipByFollowUserIdAndFollowedUserId(Integer followUserId,Integer followedUserId);
}
