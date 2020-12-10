package com.hebtu.havefun.dao;

import com.hebtu.havefun.entity.activity.ActivityLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author PengHuAnZhi
 * @createTime 2020/12/6 10:45
 * @projectName HaveFun
 * @className ActivityLocationDao.java
 * @description TODO
 */
public interface ActivityLocationDao extends JpaSpecificationExecutor<ActivityLocation>, JpaRepository<ActivityLocation, Integer> {
}
