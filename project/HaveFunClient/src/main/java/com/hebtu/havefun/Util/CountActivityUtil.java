package com.hebtu.havefun.Util;

import com.hebtu.havefun.dao.ActivityDao;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author PengHuAnZhi
 * @createTime 2020/11/26 8:53
 * @projectName HaveFun
 * @className CountActivityUtil.java
 * @description TODO
 */
@Component
public class CountActivityUtil {
    @Resource
    ActivityDao activityDao;
    public Long getActivityNum(){
        return activityDao.count();
    }
}
