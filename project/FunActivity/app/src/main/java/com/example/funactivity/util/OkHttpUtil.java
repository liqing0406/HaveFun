package com.example.funactivity.util;

import android.util.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttpUtil {
    private OkHttpClient client;
    private String jsonStr = new String();

    public OkHttpUtil(OkHttpClient client) {
        this.client = client;
    }

    public String getDate(FormBody body, String otherUrl){
        Request request = new Request.Builder()
                .post(body)
                .url(Constant.BASE_URL + otherUrl)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //获取活动列表数据
                jsonStr = response.body().string();
                Log.e("传递前",jsonStr);
            }
        });
        return jsonStr;
    }
}
