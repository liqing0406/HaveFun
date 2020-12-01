package com.hebtu.havefun.dao;


import com.hebtu.havefun.entity.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author PengHuAnZhi
 * @createTime 2020/11/19 11:29
 * @projectName HaveFun
 * @className UserDao.java
 * @description TODO
 */
public interface UserDao extends JpaRepository<User,Integer> , JpaSpecificationExecutor<User> {

    User findUserByPhoneNum(String phoneNum);

    User findUserByPhoneNumAndPassword(String phoneNum,String password);

}
