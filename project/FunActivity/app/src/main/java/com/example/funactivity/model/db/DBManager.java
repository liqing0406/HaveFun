package com.example.funactivity.model.db;

import android.content.Context;

import com.example.funactivity.model.dao.ContactTableDao;
import com.example.funactivity.model.dao.InviteTableDao;

public class DBManager {

    private final DBHelper mDbHelper;
    private final ContactTableDao mContactTableDao;
    private final InviteTableDao mInviteTableDao;

    public DBManager(Context context, String name) {
        mDbHelper = new DBHelper(context, name);

        //创建两张表的操作类
        mContactTableDao = new ContactTableDao(mDbHelper);
        mInviteTableDao = new InviteTableDao(mDbHelper);
    }

    public ContactTableDao getContactTableDao() {
        return mContactTableDao;
    }

    public InviteTableDao getInviteTableDao() {
        return mInviteTableDao;
    }

    //关闭数据库的方法
    public void close() {
        mDbHelper.close();
    }
}
