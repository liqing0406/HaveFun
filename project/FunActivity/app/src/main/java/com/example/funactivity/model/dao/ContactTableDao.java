package com.example.funactivity.model.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.funactivity.model.bean.UserInfo;
import com.example.funactivity.model.db.DBHelper;

import java.util.ArrayList;
import java.util.List;


public class ContactTableDao {
    private DBHelper mDBHelper;

    public ContactTableDao(DBHelper dbHelper) {
        mDBHelper = dbHelper;
    }

    //获取所有联系人,查看自己的好友
    public List<UserInfo> getContacts() {
        //获取数据库连接
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        //查询资源
        String sql = "select * from " + ContactTable.TAB_NAME + " where " + ContactTable.COL_IS_CONTACT + " =1";
        Cursor cursor = db.rawQuery(sql, null);
        List<UserInfo> userInfos = new ArrayList<>();
        while (cursor.moveToNext()) {
            UserInfo userInfo = new UserInfo();
            userInfo.setHxid(cursor.getString(cursor.getColumnIndex(ContactTable.COL_HXID)));
            userInfo.setName(cursor.getString(cursor.getColumnIndex(ContactTable.COL_NAME)));
            userInfo.setNick(cursor.getString(cursor.getColumnIndex(ContactTable.COL_NICK)));
            userInfo.setPhoto(cursor.getString(cursor.getColumnIndex(ContactTable.COL_PHOTO)));
            userInfos.add(userInfo);
        }
        //关闭资源
        cursor.close();
        //返回数据
        return userInfos;
    }

    //通过环信id获取联系人信息,查看单个人的信息
    public UserInfo getContactByHxId(String hxid) {
        if (hxid == null) {
            return null;
        }

        //获取数据库连接
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        //查询资源
        String sql = "select * from " + ContactTable.TAB_NAME + " where " + ContactTable.COL_HXID + " =?";
        Cursor cursor = db.rawQuery(sql, new String[]{hxid});
        UserInfo userInfo = null;
        if (cursor.moveToNext()) {
            userInfo = new UserInfo();
            userInfo.setHxid(cursor.getString(cursor.getColumnIndex(ContactTable.COL_HXID)));
            userInfo.setName(cursor.getString(cursor.getColumnIndex(ContactTable.COL_NAME)));
            userInfo.setNick(cursor.getString(cursor.getColumnIndex(ContactTable.COL_NICK)));
            userInfo.setPhoto(cursor.getString(cursor.getColumnIndex(ContactTable.COL_PHOTO)));
        }
        //关闭资源
        cursor.close();
        return userInfo;
    }

    //查看群成员,通过id的List来获取成员信息
    public List<UserInfo> getContactsByHxid(List<String> hxids) {
        if (hxids == null || hxids.size() == 0) {
            return null;
        }
        List<UserInfo> userInfos = new ArrayList<>();
        for (String hxid : hxids) {
            UserInfo userInfo = getContactByHxId(hxid);
            userInfos.add(userInfo);
        }
        return userInfos;
    }

    //保存单个联系人
    public void saveContact(UserInfo userInfo, boolean isMyContact) {
        if (userInfo == null) {
            return;
        }

        SQLiteDatabase db = mDBHelper.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(ContactTable.COL_HXID, userInfo.getHxid());
        values.put(ContactTable.COL_NAME, userInfo.getName());
        values.put(ContactTable.COL_NICK, userInfo.getNick());
        values.put(ContactTable.COL_PHOTO, userInfo.getPhoto());
        values.put(ContactTable.COL_IS_CONTACT, isMyContact ? 1 : 0);

        db.replace(ContactTable.TAB_NAME, null, values);
    }

    //保存多个联系人
    public void saveContacts(List<UserInfo> userInfos, boolean isMyContact) {
        if (userInfos == null || userInfos.size() == 0) {
            return;
        }

        for (UserInfo userInfo : userInfos) {
            saveContact(userInfo,isMyContact);
        }
    }

    //删除联系人信息
    public void delContactByHxid(String hxid) {
        if (hxid == null){
            return;
        }

        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        db.delete(ContactTable.TAB_NAME,ContactTable.COL_HXID+"=?",new String[]{hxid});
    }

}
