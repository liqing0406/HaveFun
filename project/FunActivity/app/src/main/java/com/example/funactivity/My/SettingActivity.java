package com.example.funactivity.My;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.funactivity.MainActivity;
import com.example.funactivity.R;
import com.example.funactivity.Settings.AboutUsActivity;
import com.example.funactivity.Settings.AccountManagerActivity;
import com.example.funactivity.Settings.IdAndSercurityActivity;
import com.example.funactivity.Settings.NotificationSetActivity;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        //改变设置字体
        TextView setting = findViewById(R.id.tv_setting);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "FZGongYHJW.TTF");
        setting.setTypeface(typeface);

        //头像圆形处理
        ImageView touxiang = findViewById(R.id.touxaing);
        RequestOptions requestOptions = new RequestOptions()
                .circleCrop();
        Glide.with(this).load(R.drawable.laozhou).apply(requestOptions).into(touxiang);

        //点击按钮退出登录
        Button out = findViewById(R.id.btn_out);
        out.setOnClickListener(v -> out());


    }

    //左上角箭头返回
    public void back(View view) {
        finish();
    }

    //退出登录
    private void out() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
        builder.setMessage("确定要退出吗？");
        //确定返回登录页面
        builder.setPositiveButton("确定", (dialog, which) -> {
            dialog.dismiss();
            Intent i = new Intent(getBaseContext(), MainActivity.class);
            startActivity(i);
        });
        //取消对话框消失
        builder.setNegativeButton("取消", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    //跳转账户管理
    public void onClick(View view) {
        Intent i = new Intent(this, AccountManagerActivity.class);
        startActivity(i);
    }

    //跳转账户与安全
    public void onClickTwo(View view) {
        Intent i = new Intent(this, IdAndSercurityActivity.class);
        startActivity(i);
    }

    //跳转通知设置
    public void onClickThree(View view) {
        Intent i = new Intent(this, NotificationSetActivity.class);
        startActivity(i);
    }

    //跳转关于我们
    public void onClickSix(View view) {
        Intent i = new Intent(this, AboutUsActivity.class);
        startActivity(i);
    }
}
