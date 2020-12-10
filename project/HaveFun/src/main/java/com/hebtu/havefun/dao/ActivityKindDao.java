package com.hebtu.havefun.dao;

import com.hebtu.havefun.entity.activity.ActivityKind;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author PengHuAnZhi
 * @createTime 2020/12/6 10:23
 * @projectName HaveFun
 * @className ActivityKindDao.java
 * @description TODO
 */
public interface ActivityKindDao extends JpaRepository<ActivityKind, Integer>, JpaSpecificationExecutor<ActivityKind> {
    //通过活动大类名称查找ActivityKind
    ActivityKind findActivityKindByKindName(String kindName);
}
