package com.example.funactivity.Settings;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.funactivity.My.SettingActivity;
import com.example.funactivity.R;

public class IdAndSercurityActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_id_and_sercurity);
        //改变设置字体
        TextView setting=findViewById(R.id.tv_text);
        Typeface typeface= Typeface.createFromAsset(getAssets(),"FZGongYHJW.TTF");
        setting.setTypeface(typeface);


    }
    //返回
    public void back(View view){
        Intent i=new Intent(this, SettingActivity.class);
        startActivity(i);
    }
    //点击>修改昵称
    public void changeName(View view){
        Intent i=new Intent(this,ChangeNameActivity.class);
        startActivity(i);
    }
    //点击修改密码
    public void changePassword(View view){
        Intent i=new Intent(this,ChangePasswordActivity.class);
        startActivity(i);
    }
}
