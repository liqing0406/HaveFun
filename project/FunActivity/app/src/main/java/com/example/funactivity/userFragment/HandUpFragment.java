package com.example.funactivity.userFragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSON;
import com.example.funactivity.entity.activity.Activity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.example.funactivity.HeOrSheActivity;
import com.example.funactivity.R;
import com.example.funactivity.adapter.UpAdapter;
import com.example.funactivity.entity.User.UserPublishActivity;
import com.example.funactivity.util.Constant;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HandUpFragment extends Fragment {
    private String id;
    private List<UserPublishActivity> myHanUpActivity;
    private UpAdapter upAdapter;
    private OkHttpClient client;
    private GridView gridView;
    private View view;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 1:
                    myHanUpActivity = (List<UserPublishActivity>) msg.obj;
                    upAdapter = new UpAdapter(getContext(), myHanUpActivity, R.layout.up_list_item,id);
                    gridView.setAdapter(upAdapter);
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.view_pager_fragment, container, false);

        setAttribute();
        getMyHanUpActivity();

        return view;
    }

    /**
     * 获取我收藏的活动信息
     */
    private void getMyHanUpActivity() {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("id", id + "");
        builder.add("pageNum", 1 + "");
        builder.add("pageSize", 10 + "");
        FormBody body = builder.build();
        final Request request = new Request.Builder()
                .post(body)
                .url(Constant.BASE_URL + "activity/getEnterActivities")
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String result = response.body().string();
                if (!result.equals("empty")) {
                    List<UserPublishActivity> hanUpActivity = JSON.parseArray(result,UserPublishActivity.class);
                    Message message = new Message();
                    message.what = 1;
                    message.obj = hanUpActivity;
                    handler.sendMessage(message);
                }
            }
        });
    }

    private void setAttribute() {
        HeOrSheActivity heOrSheActivity = (HeOrSheActivity) getActivity();
        id = heOrSheActivity.getId();
        gridView = view.findViewById(R.id.grid_view);
        client = new OkHttpClient();

    }
}

