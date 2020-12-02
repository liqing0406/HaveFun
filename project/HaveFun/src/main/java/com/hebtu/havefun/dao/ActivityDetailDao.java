package com.hebtu.havefun.dao;

import com.hebtu.havefun.entity.activity.ActivityDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author PengHuAnZhi
 * @createTime 2020/11/28 10:23
 * @projectName HaveFun
 * @className ActivityDetailDao.java
 * @description TODO
 */
public interface ActivityDetailDao extends JpaRepository<ActivityDetail,Integer>, JpaSpecificationExecutor<ActivityDetail> {
}
