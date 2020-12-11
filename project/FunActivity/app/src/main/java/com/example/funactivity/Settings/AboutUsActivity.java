package com.example.funactivity.Settings;

import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.funactivity.R;

public class AboutUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        TextView jia=findViewById(R.id.tv_jia);
        TextView ru=findViewById(R.id.tv_ru);
        TextView wo=findViewById(R.id.tv_wo);
        TextView men=findViewById(R.id.tv_men);
        TextView zhaopin=findViewById(R.id.zhaopin);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "FZSJ-SGLDXMHJW.TTF");
        jia.setTypeface(typeface);
        ru.setTypeface(typeface);
        wo.setTypeface(typeface);
        men.setTypeface(typeface);
        zhaopin.setTypeface(typeface);

    }
}
