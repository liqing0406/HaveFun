package com.hebtu.havefun.dao;

import com.hebtu.havefun.entity.User.User;
import com.hebtu.havefun.entity.User.UserCollectActivity;
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
public interface UserCollectActivityDao extends JpaSpecificationExecutor<UserCollectActivity>, JpaRepository<UserCollectActivity,Integer> {
    //通过用户和活动查询收藏活动类UserCollectActivity
    UserCollectActivity findUserCollectActivitiesByUserAndActivity(User user, Activity activity);
}
