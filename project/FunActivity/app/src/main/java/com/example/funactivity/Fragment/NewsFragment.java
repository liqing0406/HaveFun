package com.example.funactivity.Fragment;

import android.content.Intent;
import android.util.Log;


import com.alibaba.fastjson.JSON;
import com.example.funactivity.entity.User.User;
import com.example.funactivity.news.ChatActivity;

import com.example.funactivity.util.Constant;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.EaseUI;
import com.hyphenate.easeui.ui.EaseConversationListFragment;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NewsFragment extends EaseConversationListFragment {
    private String phone;
    private String nickname;
    private String head;
    @Override
    protected void initView() {
        super.initView();
        //监听会话消息
        EMClient.getInstance().chatManager().addMessageListener(mMessageListener);
        //跳转到会话详情页面
        setConversationListItemClickListener(conversation -> {
            Connection(conversation);

        });
        //闪动的问题
        conversationList.clear();
    }
    public void Connection(EMConversation conversation) {
        phone=conversation.conversationId();
        OkHttpClient client=new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("phoneNum", phone);
        FormBody body = builder.build();
        Request request = new Request.Builder()
                .post(body)
                .url(Constant.BASE_URL + "user/getUserInfo")
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //返回报名结果
                String result=response.body().string();
                User user= JSON.parseObject(result,User.class);
                nickname=user.getUserName();
                head=user.getHeadPortrait();
                Log.e("nickname",nickname);
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra(EaseConstant.EXTRA_USER_ID,conversation.conversationId());
                Log.e("head",head);
                intent.putExtra(EaseConstant.EXTRA_USER_HEAD,head);
                intent.putExtra(EaseConstant.EXTRA_CHAT_NAME,nickname);
                if (conversation.getType() == EMConversation.EMConversationType.GroupChat) {
                    intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE,EaseConstant.CHATTYPE_GROUP );
                }
                startActivity(intent);
            }
        });
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
