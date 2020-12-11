package com.example.funactivity.model.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import com.example.funactivity.model.bean.GroupInfo;
import com.example.funactivity.model.bean.InvitationInfo;
import com.example.funactivity.model.bean.UserInfo;
import com.example.funactivity.model.db.DBHelper;

import java.util.ArrayList;
import java.util.List;

public class InviteTableDao {
    DBHelper mDBHelper;

    public InviteTableDao(DBHelper dbHelper) {
        mDBHelper = dbHelper;
    }

    //添加邀请
    public void addInvitation(InvitationInfo invitationInfo) {
        //获取数据库连接
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        //执行添加语句
        ContentValues values = new ContentValues();

        values.put(InviteTable.COL_REASON, invitationInfo.getReason());
        values.put(InviteTable.COL_STATUS, invitationInfo.getStatus().ordinal());//枚举的序号

        UserInfo userInfo = invitationInfo.getUserInfo();

        if (userInfo != null) {//获取到了就是联系人
            values.put(InviteTable.COL_USER_HXID, invitationInfo.getUserInfo().getHxid());
            values.put(InviteTable.COL_USER_NAME, invitationInfo.getUserInfo().getName());
        } else {//否则就是群组
            values.put(InviteTable.COL_GROUP_HXID, invitationInfo.getGroupInfo().getGroupId());
            values.put(InviteTable.COL_GROUP_NAME, invitationInfo.getGroupInfo().getGroupName());
            //不能没有主键,所以加一个
            values.put(InviteTable.COL_USER_HXID, invitationInfo.getGroupInfo().getInvitePerson());
        }

        db.replace(InviteTable.TAB_NAME, null, values);

    }

    //获取所有邀请信息
    public List<InvitationInfo> getInvitations() {

        SQLiteDatabase db = mDBHelper.getReadableDatabase();

        String sql = "select * from " + InviteTable.TAB_NAME;
        List<InvitationInfo> invitationInfoList = new ArrayList<>();

        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            InvitationInfo invitationInfo = new InvitationInfo();
            invitationInfo.setReason(cursor.getString(cursor.getColumnIndex(InviteTable.COL_REASON)));
            invitationInfo.setStatus(int2InviteStatus(cursor.getInt(cursor.getColumnIndex(InviteTable.COL_STATUS))));

            //是否为群邀请
            String groupId = cursor.getString(cursor.getColumnIndex(InviteTable.COL_GROUP_HXID));
            if (groupId != null) {//不空则为群邀请
                GroupInfo groupInfo = new GroupInfo();
                groupInfo.setGroupId(cursor.getString(cursor.getColumnIndex(InviteTable.COL_GROUP_HXID)));
                groupInfo.setGroupName(cursor.getString(cursor.getColumnIndex(InviteTable.COL_GROUP_NAME)));
                groupInfo.setInvitePerson(cursor.getString(cursor.getColumnIndex(InviteTable.COL_USER_HXID)));
                invitationInfo.setGroupInfo(groupInfo);
            } else {
                UserInfo userInfo = new UserInfo();
                userInfo.setHxid(cursor.getString(cursor.getColumnIndex(InviteTable.COL_USER_HXID)));
                userInfo.setName(cursor.getString(cursor.getColumnIndex(InviteTable.COL_USER_NAME)));
                userInfo.setNick(cursor.getString(cursor.getColumnIndex(InviteTable.COL_USER_NAME)));
                invitationInfo.setUserInfo(userInfo);
            }
            invitationInfoList.add(invitationInfo);
        }
        //关闭
        cursor.close();

        return invitationInfoList;
    }

    //将int类型状态转换为邀请的状态
    private InvitationInfo.InvitationStatus int2InviteStatus(int intStatus) {

        if (intStatus == InvitationInfo.InvitationStatus.NEW_INVITE.ordinal()) {
            return InvitationInfo.InvitationStatus.NEW_INVITE;
        }
        if (intStatus == InvitationInfo.InvitationStatus.INVITE_ACCEPT.ordinal()) {
            return InvitationInfo.InvitationStatus.INVITE_ACCEPT;
        }
        if (intStatus == InvitationInfo.InvitationStatus.INVITE_ACCEPT_BY_PEER.ordinal()) {
            return InvitationInfo.InvitationStatus.INVITE_ACCEPT_BY_PEER;
        }

        if (intStatus == InvitationInfo.InvitationStatus.NEW_GROUP_INVITE.ordinal()) {
            return InvitationInfo.InvitationStatus.NEW_GROUP_INVITE;
        }
        if (intStatus == InvitationInfo.InvitationStatus.NEW_GROUP_APPLICATION.ordinal()) {
            return InvitationInfo.InvitationStatus.NEW_GROUP_APPLICATION;
        }

        if (intStatus == InvitationInfo.InvitationStatus.GROUP_INVITE_ACCEPTED.ordinal()) {
            return InvitationInfo.InvitationStatus.GROUP_INVITE_ACCEPTED;
        }
        if (intStatus == InvitationInfo.InvitationStatus.GROUP_APPLICATION_ACCEPTED.ordinal()) {
            return InvitationInfo.InvitationStatus.GROUP_APPLICATION_ACCEPTED;
        }
        if (intStatus == InvitationInfo.InvitationStatus.GROUP_INVITE_DECLINED.ordinal()) {
            return InvitationInfo.InvitationStatus.GROUP_INVITE_DECLINED;
        }
        if (intStatus == InvitationInfo.InvitationStatus.GROUP_APPLICATION_DECLINED.ordinal()) {
            return InvitationInfo.InvitationStatus.GROUP_APPLICATION_DECLINED;
        }
        if (intStatus == InvitationInfo.InvitationStatus.GROUP_ACCEPT_INVITE.ordinal()) {
            return InvitationInfo.InvitationStatus.GROUP_ACCEPT_INVITE;
        }
        if (intStatus == InvitationInfo.InvitationStatus.GROUP_ACCEPT_APPLICATION.ordinal()) {
            return InvitationInfo.InvitationStatus.GROUP_ACCEPT_APPLICATION;
        }
        if (intStatus == InvitationInfo.InvitationStatus.GROUP_REJECT_APPLICATION.ordinal()) {
            return InvitationInfo.InvitationStatus.GROUP_REJECT_APPLICATION;
        }
        if (intStatus == InvitationInfo.InvitationStatus.GROUP_REJECT_INVITE.ordinal()) {
            return InvitationInfo.InvitationStatus.GROUP_REJECT_INVITE;
        }
        return null;
    }

    //拒绝某人的邀请
    public void removeInvitation(String hxid) {
        if (hxid == null){
            return;
        }
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        db.delete(InviteTable.TAB_NAME,InviteTable.COL_USER_HXID+"=?",new String[]{hxid});
    }

    //更新邀请状态
    public void updateInvitationStatus(InvitationInfo.InvitationStatus status, String hxid) {
        if (hxid == null){
            return;
        }
        SQLiteDatabase db = mDBHelper.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(InviteTable.COL_STATUS,status.ordinal());

        db.update(InviteTable.TAB_NAME,values,InviteTable.COL_USER_HXID+"=?",new String[]{hxid});
    }


}
