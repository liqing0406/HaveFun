package com.example.funactivity.Settings;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.funactivity.R;
import com.example.funactivity.entity.User.User;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class IdAndSercurityActivity extends AppCompatActivity {
    private String num;
    private String id;
    private TextView phonenum;
    private TextView user_id;
    private TextView shenfenzheng;
    private String shenfenzhenghao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_id_and_sercurity);
        EventBus.getDefault().register(this);
        //改变设置字体
        TextView setting = findViewById(R.id.tv_text);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "FZGongYHJW.TTF");
        setting.setTypeface(typeface);
        phonenum = findViewById(R.id.tv_number);
        user_id = findViewById(R.id.tv_userid);
        shenfenzheng = findViewById(R.id.tv_shenfenzhenghao);
        phonenum.setText(num);
        user_id.setText(id);
        if (shenfenzhenghao.equals("未认证")) {
            shenfenzheng.setText("未认证");
        } else {
            shenfenzheng.setText("已认证");
        }


    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void updata(User u) {
        num = u.getPhoneNum();
        id = u.getUserId() + "";
        Log.e("身份证", u.getUserDetail().getResidentIdCard());
        shenfenzhenghao = u.getUserDetail().getResidentIdCard();
        Log.e("获取到的身份证号", shenfenzhenghao);

    }

    //返回
    public void back(View view) {
        finish();
    }

    //点击>修改昵称
    public void changeName(View view) {
        Intent i = new Intent(this, ChangeNameActivity.class);
        startActivity(i);
    }

    //点击修改密码
    public void changePassword(View view) {
        Intent i = new Intent(this, ChangePasswordActivity.class);
        startActivity(i);
    }

    public void changeSex(View view) {
        Intent i = new Intent(this, ChangeSexActivity.class);
        startActivity(i);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
