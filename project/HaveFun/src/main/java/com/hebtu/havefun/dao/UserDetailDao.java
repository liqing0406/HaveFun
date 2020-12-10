package com.hebtu.havefun.dao;

import com.hebtu.havefun.entity.User.User;
import com.hebtu.havefun.entity.User.UserDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author PengHuAnZhi
 * @createTime 2020/11/25 14:15
 * @projectName HaveFun
 * @className UserDetailDao.java
 * @description TODO
 */
public interface UserDetailDao extends JpaRepository<UserDetail, Integer>, JpaSpecificationExecutor<UserDetail> {
    //通过user对象查找用户详情类UserDetail
    UserDetail findUserDetailByUser(User user);
}
