package com.example.funactivity.model;

import android.content.Context;


import com.example.funactivity.model.bean.UserInfo;
import com.example.funactivity.model.dao.UserAccountDao;
import com.example.funactivity.model.db.DBManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Model {

    private Context mContext;
    private ExecutorService mExecutorService = Executors.newCachedThreadPool();
    private UserAccountDao mUserAccountDao;
    private DBManager mDbManager;

    private Model() {
    }

    private static Model sInstance = null;

    public static Model getInstance() {
        if (sInstance == null) {
            synchronized (Model.class) {
                if (sInstance == null) {
                    sInstance = new Model();
                }
            }
        }
        return sInstance;
    }

    public void init(Context context) {
        mContext = context;
        //创建数据库操作类对象
        mUserAccountDao = new UserAccountDao(context);

        //开启全局监听
        EventListener eventListener = new EventListener(mContext);
        
    }

    //获取全局线程池
    public ExecutorService getGlobalThreadPool() {
        return mExecutorService;
    }

    //用户登录成功
    public void loginSuccess(UserInfo userInfo) {
        if (userInfo == null) {
            return;
        }

        if (mDbManager != null) {
            mDbManager.close();
        }
        mDbManager = new DBManager(mContext, userInfo.getHxid());
    }

    public DBManager getDbManager(){
        return mDbManager;
    }

    //获取用户账户的数据库的操作类对象
    public UserAccountDao getUserAccountDao() {
        return mUserAccountDao;
    }
}
