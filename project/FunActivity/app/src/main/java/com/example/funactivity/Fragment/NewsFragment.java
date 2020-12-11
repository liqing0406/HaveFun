package com.example.funactivity.Fragment;

import android.content.Intent;


import com.example.funactivity.news.ChatActivity;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.EaseUI;
import com.hyphenate.easeui.ui.EaseConversationListFragment;

import java.util.List;

public class NewsFragment extends EaseConversationListFragment {


    @Override
    protected void initView() {
        super.initView();
        //监听会话消息
        EMClient.getInstance().chatManager().addMessageListener(mMessageListener);
        //跳转到会话详情页面
        setConversationListItemClickListener(conversation -> {
            Intent intent = new Intent(getActivity(), ChatActivity.class);
            intent.putExtra(EaseConstant.EXTRA_USER_ID,conversation.conversationId());
            if (conversation.getType() == EMConversation.EMConversationType.GroupChat) {
                intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE,EaseConstant.CHATTYPE_GROUP );
            }
            startActivity(intent);
        });
        //闪动的问题
        conversationList.clear();
    }

    private EMMessageListener mMessageListener = new EMMessageListener() {
        @Override
        public void onMessageReceived(List<EMMessage> list) {
            EaseUI.getInstance().getNotifier().notify(list);
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> list) {

        }

        @Override
        public void onMessageRead(List<EMMessage> list) {

        }

        @Override
        public void onMessageDelivered(List<EMMessage> list) {

        }

        @Override
        public void onMessageRecalled(List<EMMessage> list) {

        }

        @Override
        public void onMessageChanged(EMMessage emMessage, Object o) {

        }
    };
}
