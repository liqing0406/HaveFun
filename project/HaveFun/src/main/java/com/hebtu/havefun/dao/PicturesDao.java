package com.hebtu.havefun.dao;

import com.hebtu.havefun.entity.activity.Picture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author PengHuAnZhi
 * @createTime 2020/12/7 9:05
 * @projectName HaveFun
 * @className PicturesDao.java
 * @description TODO
 */
public interface PicturesDao extends JpaSpecificationExecutor<Picture>, JpaRepository<Picture,Integer> {
}
