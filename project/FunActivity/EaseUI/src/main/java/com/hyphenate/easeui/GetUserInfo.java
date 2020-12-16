package com.hyphenate.easeui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.hyphenate.easeui.User.User;
import com.hyphenate.easeui.utils.Constant;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetUserInfo extends AppCompatActivity {

    User user = null;
    OkHttpClient client = new OkHttpClient();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_user_info);
    }

    public User getUser(){
        return user;
    }

    public void getUser(String phoneNum){
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("phoneNum", phoneNum);
        Log.e("dada",""+phoneNum);
        FormBody body = builder.build();
        final Request request = new Request.Builder()
                .post(body)
                .url(Constant.BASE_URL + "user/getUserInfo")
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("shibai","~!!!!");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                user = JSON.parseObject(result, User.class);
            }
        });
    }
}