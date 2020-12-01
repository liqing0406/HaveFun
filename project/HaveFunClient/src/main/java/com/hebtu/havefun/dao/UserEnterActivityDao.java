package com.hebtu.havefun.dao;

import com.hebtu.havefun.entity.User.User;
import com.hebtu.havefun.entity.User.UserEnterActivity;
import com.hebtu.havefun.entity.activity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author PengHuAnZhi
 * @createTime 2020/11/29 15:58
 * @projectName HaveFun
 * @className UserCollectActivityDao.java
 * @description TODO
 */
public interface UserEnterActivityDao extends JpaRepository<UserEnterActivity, Integer>, JpaSpecificationExecutor<UserEnterActivity> {
    UserEnterActivity findUserEnterActivitiesByUserAndActivity(User user, Activity activity);
}
