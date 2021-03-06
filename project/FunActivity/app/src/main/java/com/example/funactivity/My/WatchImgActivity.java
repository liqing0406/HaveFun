package com.example.funactivity.My;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.funactivity.R;
import com.example.funactivity.util.Constant;

public class WatchImgActivity extends AppCompatActivity {
    private ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_img);
        ImageView back = findViewById(R.id.iv_back);
        img = findViewById(R.id.iv_img);
        back.setOnClickListener(v->finish());
        //展示大图
        showImg();
    }

    public void showImg() {
        Intent i = getIntent();
        String path = i.getStringExtra("path");
        //加载图片
        Glide.with(this)
                .load(Constant.PIC_PATH + path)
                .into(img);
    }
}

