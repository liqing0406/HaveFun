package com.example.funactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.uuzuche.lib_zxing.activity.CodeUtils;

public class CodeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code);
        ImageView back = findViewById(R.id.iv_back);
        back.setOnClickListener(v->finish());
        ImageView code = findViewById(R.id.iv_img);
        Intent intent = getIntent();
        String activityId = intent.getStringExtra("activityId");
        Log.e("活动id",activityId);
        //生成活动二维码
        Bitmap bitmap = CodeUtils.createImage(activityId+"",500,500, BitmapFactory.decodeResource(this.getResources(),R.drawable.have_fun_icon));
        code.setImageBitmap(bitmap);
    }
}
