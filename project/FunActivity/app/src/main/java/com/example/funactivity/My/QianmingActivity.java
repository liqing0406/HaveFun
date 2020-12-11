package com.example.funactivity.My;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.example.funactivity.Fragment.MyFragment;
import com.example.funactivity.Main2Activity;
import com.example.funactivity.R;
import com.example.funactivity.entity.User.UserPublishActivity;
import com.example.funactivity.util.Constant;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class QianmingActivity extends AppCompatActivity {
    private ImageView back;
    private Button edit;//提交修改按钮
    private EditText personalSignature;
    private String text;
    private String id;
    private OkHttpClient client;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    EventBus.getDefault().post(personalSignature.getText().toString());
                    finish();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qianming);
        Intent intent = getIntent();
        //获取个性签名
        text = intent.getStringExtra("personalSignature");
        id = intent.getStringExtra("id");
        initView();
    }

    private void initView() {
        back = findViewById(R.id.iv_back);
        edit = findViewById(R.id.btn_edit);
        personalSignature = findViewById(R.id.et_qianming);
        personalSignature.setText(text);
        client = new OkHttpClient();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //提交修改
        edit.setOnClickListener(v -> changePersonalSignature());
    }

    private void changePersonalSignature() {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("id", id);
        builder.add("personalSignature", personalSignature.getText().toString());
        FormBody body = builder.build();
        Request request = new Request.Builder()
                .post(body)
                .url(Constant.BASE_URL + "user/modifyPersonalSignature")
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //获取修改结果
                String json = response.body().string();
                Log.e("修改结果",json);
                if (json.equals("true")) {
                    Message message = new Message();
                    message.what = 1;
                    handler.sendMessage(message);
                } else {
                    Looper.prepare();
                    Toast.makeText(QianmingActivity.this, "修改失败，请重试！", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }

            }
        });
    }
}
