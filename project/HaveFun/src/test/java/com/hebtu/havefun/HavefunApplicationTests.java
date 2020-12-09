package com.hebtu.havefun;

import com.alibaba.druid.pool.DruidDataSource;
import com.hebtu.havefun.dao.ActivityDao;
import com.hebtu.havefun.dao.UserDao;
import com.hebtu.havefun.dao.UserDetailDao;
import com.hebtu.havefun.entity.Constant;
import com.hebtu.havefun.service.ActivityService;
import com.hebtu.havefun.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.SQLException;

@SpringBootTest
class HavefunApplicationTests {
    @Resource
    UserDao userDao;
    @Resource
    UserDetailDao userDetailDao;
    @Resource
    UserService userService;
    @Resource
    ActivityDao activityDao;
    @Resource
    ActivityService activityService;
    @Resource
    Constant constant;
    @Resource
    DruidDataSource dataSource;

    @Test
    void contextLoads() {
        System.out.println(dataSource.getMaxWait());
        System.out.println(dataSource.getDriverClassName());
        System.out.println(dataSource.getUrl());
    }
}