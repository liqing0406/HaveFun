package com.example.funactivity.model.bean;

public class InvitationInfo {
    private UserInfo mUserInfo; //联系人
    private GroupInfo mGroupInfo; //群组
    private String reason; //原因
    private InvitationStatus mStatus;//邀请状态

    public InvitationInfo() {
    }

    public InvitationInfo(UserInfo userInfo, GroupInfo groupInfo, String reason, InvitationStatus status) {
        mUserInfo = userInfo;
        mGroupInfo = groupInfo;
        this.reason = reason;
        mStatus = status;
    }

    public UserInfo getUserInfo() {
        return mUserInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        mUserInfo = userInfo;
    }

    public GroupInfo getGroupInfo() {
        return mGroupInfo;
    }

    public void setGroupInfo(GroupInfo groupInfo) {
        mGroupInfo = groupInfo;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public InvitationStatus getStatus() {
        return mStatus;
    }

    public void setStatus(InvitationStatus status) {
        mStatus = status;
    }

    public enum InvitationStatus {

        //----------关于联系人--------
        //新邀请
        NEW_INVITE,
        //接收邀请
        INVITE_ACCEPT,
        //邀请被接受
        INVITE_ACCEPT_BY_PEER,

        //----------关于群--------

        //收到邀请去加入群
        NEW_GROUP_INVITE,
        //收到群加入申请
        NEW_GROUP_APPLICATION,
        //群邀请已经被对方接收
        GROUP_INVITE_ACCEPTED,
        //群申请已经被批准
        GROUP_APPLICATION_ACCEPTED,
        //接收了群邀请
        GROUP_ACCEPT_INVITE,
        //批准群加入申请
        GROUP_ACCEPT_APPLICATION,
        //拒绝了群邀请
        GROUP_REJECT_INVITE,
        //拒绝了群申请加入
        GROUP_REJECT_APPLICATION,
        //群邀请被对方拒绝
        GROUP_INVITE_DECLINED,
        //群申请被拒绝
        GROUP_APPLICATION_DECLINED
    }

    @Override
    public String toString() {
        return "InvitationInfo{" +
                "mUserInfo=" + mUserInfo +
                ", mGroupInfo=" + mGroupInfo +
                ", reason='" + reason + '\'' +
                ", mStatus=" + mStatus +
                '}';
    }
}
