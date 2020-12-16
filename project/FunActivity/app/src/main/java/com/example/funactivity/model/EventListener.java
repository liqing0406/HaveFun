package com.example.funactivity.model;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.funactivity.model.bean.GroupInfo;
import com.example.funactivity.model.bean.InvitationInfo;
import com.example.funactivity.model.bean.UserInfo;
import com.example.funactivity.util.Constant;
import com.example.funactivity.util.SpUtils;
import com.hyphenate.EMContactListener;
import com.hyphenate.EMGroupChangeListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMucSharedFile;

import java.util.List;

public class EventListener {
    private final LocalBroadcastManager mLocalBroadcastManager;
    private static final String TAG = "EventListener";

    public EventListener(Context context) {

        //创建一个发送广播的管理者对象
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(context);

        //注册一个联系人变化的监听
        //-----------联系人变化的监听
        //联系人添加后执行
        //数据库更新
        //发送数据库变化的广播
        //联系人删除后执行
        //删除联系人
        //删除邀请
        //发送广播
        //接收到联系人的新邀请
        //数据库更新
        //红点的显示
        //发送广播
        //同意了你的好友要求
        //数据库更新,同意了也要提示一下已同意
        //红点的处理
        //发送广播
        //拒绝了你的好友要求
        //红点的处理
        //发送广播
        EMContactListener emContactListener = new EMContactListener() {

            //联系人添加后执行
            @Override
            public void onContactAdded(String hxid) {
                //数据库更新
                Model.getInstance().getDbManager().getContactTableDao().saveContact(new UserInfo(hxid), true);

                //发送数据库变化的广播
                mLocalBroadcastManager.sendBroadcast(new Intent(Constant.CONTACT_CHANGED));

            }

            //联系人删除后执行
            @Override
            public void onContactDeleted(String hxid) {
                //删除联系人
                Model.getInstance().getDbManager().getContactTableDao().delContactByHxid(hxid);
                //删除邀请
                Model.getInstance().getDbManager().getInviteTableDao().removeInvitation(hxid);
                //发送广播
                mLocalBroadcastManager.sendBroadcast(new Intent(Constant.CONTACT_CHANGED));
            }

            //接收到联系人的新邀请
            @Override
            public void onContactInvited(String hxid, String reason) {
                //数据库更新
                InvitationInfo invitationInfo = new InvitationInfo();
                invitationInfo.setUserInfo(new UserInfo(hxid));
                invitationInfo.setReason(reason);
                invitationInfo.setStatus(InvitationInfo.InvitationStatus.NEW_INVITE);
                Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invitationInfo);
                //红点的显示
                SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);

                //发送广播
                mLocalBroadcastManager.sendBroadcast(new Intent(Constant.CONTACT_INVITE_CHANGED));
            }

            //同意了你的好友要求
            @Override
            public void onFriendRequestAccepted(String hxid) {
                //数据库更新,同意了也要提示一下已同意
                InvitationInfo invitationInfo = new InvitationInfo();
                invitationInfo.setUserInfo(new UserInfo(hxid));
                invitationInfo.setStatus(InvitationInfo.InvitationStatus.INVITE_ACCEPT_BY_PEER);
                Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invitationInfo);
                //红点的处理
                SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);

                //发送广播
                mLocalBroadcastManager.sendBroadcast(new Intent(Constant.CONTACT_INVITE_CHANGED));
            }

            //拒绝了你的好友要求
            @Override
            public void onFriendRequestDeclined(String s) {
                //红点的处理
                SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);

                //发送广播
                mLocalBroadcastManager.sendBroadcast(new Intent(Constant.CONTACT_INVITE_CHANGED));
            }
        };
        EMClient.getInstance().contactManager().setContactListener(emContactListener);

        EMGroupChangeListener eMGroupChangeListener = new EMGroupChangeListener() {
            //收到群邀请
            @Override
            public void onInvitationReceived(String groupId, String groupName, String inviter, String reason) {
                //数据更新
                InvitationInfo invitationInfo = new InvitationInfo();
                invitationInfo.setReason(reason);
                invitationInfo.setGroupInfo(new GroupInfo(groupName, groupId, inviter));
                invitationInfo.setStatus(InvitationInfo.InvitationStatus.NEW_GROUP_INVITE);
                Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invitationInfo);

                //红点处理
                SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);

                //发送广播 TODO
                Log.e(TAG, "onInvitationReceived: " + "发送广播");
                mLocalBroadcastManager.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
            }

            //收到群申请通知
            @Override
            public void onRequestToJoinReceived(String groupId, String groupName, String applicant, String reason) {

                //数据更新
                InvitationInfo invitationInfo = new InvitationInfo();
                invitationInfo.setReason(reason);
                invitationInfo.setGroupInfo(new GroupInfo(groupName, groupId, applicant));
                invitationInfo.setStatus(InvitationInfo.InvitationStatus.NEW_GROUP_APPLICATION);
                Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invitationInfo);

                //红点处理
                SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);

                //发送广播
                mLocalBroadcastManager.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
            }

            //收到群申请被接受
            @Override
            public void onRequestToJoinAccepted(String groupId, String groupName, String accepter) {

                //数据更新
                InvitationInfo invitationInfo = new InvitationInfo();
                invitationInfo.setGroupInfo(new GroupInfo(groupName, groupId, accepter));
                invitationInfo.setStatus(InvitationInfo.InvitationStatus.GROUP_APPLICATION_ACCEPTED);
                Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invitationInfo);

                //红点处理
                SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);

                //发送广播
                mLocalBroadcastManager.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
            }

            //收到群申请被拒绝
            @Override
            public void onRequestToJoinDeclined(String groupId, String groupName, String decliner, String reason) {
                //数据更新
                InvitationInfo invitationInfo = new InvitationInfo();
                invitationInfo.setReason(reason);
                invitationInfo.setGroupInfo(new GroupInfo(groupName, groupId, decliner));
                invitationInfo.setStatus(InvitationInfo.InvitationStatus.GROUP_APPLICATION_DECLINED);
                Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invitationInfo);

                //红点处理
                SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);

                //发送广播
                mLocalBroadcastManager.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
            }

            //收到群邀请被同意
            @Override
            public void onInvitationAccepted(String groupId, String inviter, String reason) {
                //数据更新
                InvitationInfo invitationInfo = new InvitationInfo();
                invitationInfo.setReason(reason);
                invitationInfo.setGroupInfo(new GroupInfo(groupId, groupId, inviter));
                invitationInfo.setStatus(InvitationInfo.InvitationStatus.GROUP_INVITE_ACCEPTED);
                Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invitationInfo);

                //红点处理
                SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);

                //发送广播
                mLocalBroadcastManager.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
            }

            //收到群邀请被拒绝
            @Override
            public void onInvitationDeclined(String groupId, String inviter, String reason) {
                //数据更新
                InvitationInfo invitationInfo = new InvitationInfo();
                invitationInfo.setReason(reason);
                invitationInfo.setGroupInfo(new GroupInfo(groupId, groupId, inviter));
                invitationInfo.setStatus(InvitationInfo.InvitationStatus.GROUP_INVITE_DECLINED);
                Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invitationInfo);

                //红点处理
                SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);

                //发送广播
                mLocalBroadcastManager.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
            }

            //收到群成员被拒绝
            @Override
            public void onUserRemoved(String groupId, String groupName) {

            }

            //收到群被解散
            @Override
            public void onGroupDestroyed(String groupId, String groupName) {

            }

            //收到群邀请被自动接受
            @Override
            public void onAutoAcceptInvitationFromGroup(String groupId, String inviter, String inviterMessage) {
                //数据更新
                InvitationInfo invitationInfo = new InvitationInfo();
                invitationInfo.setReason(inviterMessage);
                invitationInfo.setGroupInfo(new GroupInfo(groupId, groupId, inviter));
                invitationInfo.setStatus(InvitationInfo.InvitationStatus.GROUP_ACCEPT_INVITE);
                Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invitationInfo);

                //红点处理
                SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);

                //发送广播
                mLocalBroadcastManager.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
            }

            @Override
            public void onMuteListAdded(String s, List<String> list, long l) {

            }

            @Override
            public void onMuteListRemoved(String s, List<String> list) {

            }

            @Override
            public void onWhiteListAdded(String s, List<String> list) {

            }

            @Override
            public void onWhiteListRemoved(String s, List<String> list) {

            }

            @Override
            public void onAllMemberMuteStateChanged(String s, boolean b) {

            }

            @Override
            public void onAdminAdded(String s, String s1) {

            }

            @Override
            public void onAdminRemoved(String s, String s1) {

            }

            @Override
            public void onOwnerChanged(String s, String s1, String s2) {

            }

            @Override
            public void onMemberJoined(String s, String s1) {

            }

            @Override
            public void onMemberExited(String s, String s1) {

            }

            @Override
            public void onAnnouncementChanged(String s, String s1) {

            }

            @Override
            public void onSharedFileAdded(String s, EMMucSharedFile emMucSharedFile) {

            }

            @Override
            public void onSharedFileDeleted(String s, String s1) {

            }
        };
        EMClient.getInstance().groupManager().addGroupChangeListener(eMGroupChangeListener);
    }
}
