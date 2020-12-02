package com.hebtu.havefun.dao;

import com.hebtu.havefun.entity.Messages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author PengHuAnZhi
 * @createTime 2020/12/2 9:47
 * @projectName HaveFun
 * @className MessagesDao.java
 * @description TODO
 */
public interface MessagesDao extends JpaRepository<Messages,Integer>, JpaSpecificationExecutor<Messages> {
}
