package com.hebtu.havefun.Util;

import com.hebtu.havefun.dao.UserDao;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author PengHuAnZhi
 * @createTime 2020/11/23 10:30
 * @projectName HaveFun
 * @className CountUserUtil.java
 * @description TODO
 */
@Component
public class CountUserUtil {
    @Resource
    UserDao userDao;
    public Long CountUser(){
        return userDao.count();
    }
}
