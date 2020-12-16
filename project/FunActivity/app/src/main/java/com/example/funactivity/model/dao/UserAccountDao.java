package com.example.funactivity.model.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.funactivity.model.bean.UserInfo;
import com.example.funactivity.model.db.UserAccountDB;


public class UserAccountDao {

    private final UserAccountDB mHelper;

    public UserAccountDao(Context context) {
        mHelper = new UserAccountDB(context);
    }

    public void addAccount(UserInfo userInfo) {
        //获取数据库对象
        SQLiteDatabase db = mHelper.getReadableDatabase();

        //执行添加操作
        ContentValues values = new ContentValues();
        values.put(UserAccountTable.COL_HXID, userInfo.getHxid());
        values.put(UserAccountTable.COL_NAME, userInfo.getName());
        values.put(UserAccountTable.COL_NICK, userInfo.getNick());
        values.put(UserAccountTable.COL_PHOTO, userInfo.getPhoto());

        //添加是为了保存在本地好登录的,所以要用replace
        //如果有就替换,没有就添加
        db.replace(UserAccountTable.TAB_NAME, null, values);
    }

    public UserInfo getAccountByHxId(String hxid) {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        String sql = "select * from " + UserAccountTable.TAB_NAME + " where " +
                UserAccountTable.COL_HXID + "=?";
        Cursor cursor = db.rawQuery(sql, new String[]{hxid});
        UserInfo mUserInfo = null;
        if (cursor.moveToNext()) {
            mUserInfo = new UserInfo();
            mUserInfo.setHxid(cursor.getString(cursor.getColumnIndex(UserAccountTable.COL_HXID)));
            mUserInfo.setName(cursor.getString(cursor.getColumnIndex(UserAccountTable.COL_NAME)));
            mUserInfo.setNick(cursor.getString(cursor.getColumnIndex(UserAccountTable.COL_NICK)));
            mUserInfo.setPhoto(cursor.getString(cursor.getColumnIndex(UserAccountTable.COL_PHOTO)));
        }
        cursor.close();

        return mUserInfo;
    }


}
