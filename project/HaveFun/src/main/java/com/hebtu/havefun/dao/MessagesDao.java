package com.hebtu.havefun.dao;

import com.hebtu.havefun.entity.Messages;
import com.hebtu.havefun.entity.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author PengHuAnZhi
 * @createTime 2020/12/2 9:47
 * @projectName HaveFun
 * @className MessagesDao.java
 * @description TODO
 */
public interface MessagesDao extends JpaRepository<Messages, Integer>, JpaSpecificationExecutor<Messages> {
    Messages findFirstBySendUserAndReceiveUserOrderByTimeDesc(User sendUser, User ReceiveUser);

    Integer countBySendUserAndReceiveUserAndIsRead(User sendUser, User receiveUser, Boolean flag);
}
