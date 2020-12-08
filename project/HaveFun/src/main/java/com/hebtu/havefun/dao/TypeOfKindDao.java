package com.hebtu.havefun.dao;

import com.hebtu.havefun.entity.activity.ActivityKind;
import com.hebtu.havefun.entity.activity.TypeOfKind;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author PengHuAnZhi
 * @createTime 2020/12/3 11:23
 * @projectName HaveFun
 * @className TypeOfKindDao.java
 * @description TODO
 */
public interface TypeOfKindDao extends JpaRepository<TypeOfKind, Integer>, JpaSpecificationExecutor<TypeOfKind> {
    TypeOfKind findTypeOfKindByActivityKind(ActivityKind activityKind);

    TypeOfKind findTypeOfKindByTypeName(String typeName);
}
