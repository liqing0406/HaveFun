package com.example.funactivity.news;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.funactivity.R;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.ui.EaseChatFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {

    //相机,位置,相册
    List<String> notpermissed = new ArrayList<>();
    String[] permissions = {Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initPermission(permissions);
        initData();
    }

    private void initPermission(String[] permissions) {

        notpermissed.clear();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                notpermissed.add(permission);
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
                    break;
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
        EaseChatFragment mEaseChatFragment = new EaseChatFragment();
        String mMHxid = getIntent().getStringExtra(EaseConstant.EXTRA_USER_ID);
        //获取聊天类型
        int mChatType = Objects.requireNonNull(getIntent().getExtras()).getInt(EaseConstant.EXTRA_CHAT_TYPE);
        mEaseChatFragment.setArguments(getIntent().getExtras());
        //替换fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fl_chat, mEaseChatFragment).commit();
        //注册一个发送广播的管理者
        LocalBroadcastManager mManager = LocalBroadcastManager.getInstance(this);
    }
}
