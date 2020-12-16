package com.example.funactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.funactivity.login.LoginActivity;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private TextView text1;
    private ImageView jump;
    Timer timer = new Timer();

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
