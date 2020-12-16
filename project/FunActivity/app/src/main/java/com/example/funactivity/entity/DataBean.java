package com.example.funactivity.entity;

import com.example.funactivity.util.Constant;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DataBean {
    public String imageUrl;
    public String title;

    public DataBean(String imageUrl, String title) {
        this.imageUrl = imageUrl;
        this.title = title;
    }

    public static List<DataBean> getTestData3() {
        final List<DataBean> list = new ArrayList<>();
        Request request = new Request.Builder()
                .url(Constant.BASE_URL + "/activity/getRotationChartPictures")
                .build();
        OkHttpClient client = new OkHttpClient();
        //创建call对象
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //服务端返回登录成功，发布事件，完成界面跳转
                String back = response.body().string();
                Gson gson = new Gson();
                String[] pictures = gson.fromJson(back, String[].class);
                for (String pic : pictures) {
                    list.add(new DataBean(pic, null));
                }
            }
        });
        return list;
    }
}
