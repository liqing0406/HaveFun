package com.hebtu.havefun.dao;

import com.hebtu.havefun.entity.User.User;
import com.hebtu.havefun.entity.User.UserPublishActivity;
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
public interface UserPublishActivityDao extends JpaRepository<UserPublishActivity, Integer>, JpaSpecificationExecutor<UserPublishActivity> {
    UserPublishActivity findUserPublishActivitiesByUserAndActivity(User user, Activity activity);

}
