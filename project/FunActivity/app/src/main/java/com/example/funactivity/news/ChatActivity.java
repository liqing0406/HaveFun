package com.example.funactivity.news;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.funactivity.HeOrSheActivity;
import com.example.funactivity.entity.User.User;
import com.hyphenate.easeui.ui.EaseChatFragment.EaseChatFragmentHelper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.funactivity.R;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.ui.EaseChatFragment;
import com.hyphenate.easeui.utils.Constant;
import com.hyphenate.easeui.widget.chatrow.EaseCustomChatRowProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class ChatActivity extends AppCompatActivity {

    private OkHttpClient client = new OkHttpClient();
    private String mPhone;
    private User user;
    private int mChatType;
    private String nickname;
    private String head;
    private EaseChatFragment mEaseChatFragment;
    private EaseChatFragmentHelper easeChatFragmentHelper = new EaseChatFragmentHelper() {
        @Override
        public void onSetMessageAttributes(EMMessage message) {

        }

        @Override
        public void onEnterToChatDetails() {

        }

        @Override
        public void onAvatarClick(String username) {
            Intent intent = new Intent();
            intent.setClass(ChatActivity.this, HeOrSheActivity.class);
            intent.putExtra("id",user.getId()+"");
            Log.e("ChatActivity",user.getId()+"");
            startActivity(intent);
        }

        @Override
        public void onAvatarLongClick(String username) {

        }

        @Override
        public boolean onMessageBubbleClick(EMMessage message) {
            return false;
        }

        @Override
        public void onMessageBubbleLongClick(EMMessage message) {

        }

        @Override
        public boolean onExtendMenuItemClick(int itemId, View view) {
            return false;
        }

        @Override
        public EaseCustomChatRowProvider onSetCustomChatRowProvider() {
            return null;
        }

    };

    //相机,位置,相册
    List<String> notpermissed = new ArrayList<>();
    String[] permissions = {Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    private LocalBroadcastManager mManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initPermission(permissions);
        initData();

    }

    private void initPermission(String[] permissions) {

        notpermissed.clear();
        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                notpermissed.add(permissions[i]);
            }
        }

        if (notpermissed.size() > 0) {
            ActivityCompat.requestPermissions(this, permissions, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean permissed = false;
        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == -1) {
                    permissed = true;
                }
            }

            if (permissed) {
                Toast.makeText(this, "权限未通过", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "权限通过", Toast.LENGTH_SHORT).show();
            }

        }
    }


    private void initData() {
        //ease会话的fragment
        mEaseChatFragment = new EaseChatFragment();
        mPhone = getIntent().getStringExtra(EaseConstant.EXTRA_USER_ID);
        getUser();
        nickname=getIntent().getStringExtra(EaseConstant.EXTRA_CHAT_NAME);
        Log.e("nicknam3131e",nickname);
        head=getIntent().getStringExtra(EaseConstant.EXTRA_USER_HEAD);
        //获取聊天类型
        mChatType = getIntent().getExtras().getInt(EaseConstant.EXTRA_CHAT_TYPE);

        mEaseChatFragment.setArguments(getIntent().getExtras());
        mEaseChatFragment.setChatFragmentHelper(easeChatFragmentHelper);

        //替换fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fl_chat, mEaseChatFragment).commit();
        //注册一个发送广播的管理者
        mManager = LocalBroadcastManager.getInstance(this);
    }

    private void getUser() {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("phoneNum", mPhone);
        FormBody body = builder.build();
        final Request request = new Request.Builder()
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
                String result = response.body().string();
                user = JSON.parseObject(result, User.class);
            }
        });
    }


}
