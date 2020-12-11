package com.example.funactivity.news;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.funactivity.R;
import com.example.funactivity.util.Constant;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.ui.EaseChatFragment;
import com.hyphenate.easeui.widget.chatrow.EaseCustomChatRowProvider;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private String mMHxid;
    private int mChatType;
    private EaseChatFragment mEaseChatFragment;
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
        mMHxid = getIntent().getStringExtra(EaseConstant.EXTRA_USER_ID);
        //获取聊天类型
        mChatType = getIntent().getExtras().getInt(EaseConstant.EXTRA_CHAT_TYPE);
        mEaseChatFragment.setArguments(getIntent().getExtras());
        //替换fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fl_chat, mEaseChatFragment).commit();
        //注册一个发送广播的管理者
        mManager = LocalBroadcastManager.getInstance(this);
    }
}
