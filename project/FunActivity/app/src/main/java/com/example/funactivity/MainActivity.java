package com.example.funactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.funactivity.login.LoginActivity;
import com.example.funactivity.view.Application;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private TextView text1;
    private ImageView jump;
    Timer timer = new Timer();
    public static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text1 = findViewById(R.id.text1);
        jump = findViewById(R.id.iv_jump);
        startAnim();//APP名称的跳转效果
        timeJump();//定时跳转的效果
    }

    private void timeJump() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                jump.callOnClick();
            }
        };
        timer.schedule(task, 5000);
    }

    public void startAnim() {
        RotateAnimation animation = new RotateAnimation(-90.0f, 0.0f);
        animation.setDuration(3000);
        text1.startAnimation(animation);
    }

    //跳转箭头的点击事件
    public void clickjump(View view) {
        if (view.getId() == R.id.iv_jump) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            timer.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }
}
