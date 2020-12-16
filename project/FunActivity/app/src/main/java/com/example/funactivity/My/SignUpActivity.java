package com.example.funactivity.My;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.funactivity.DetailActivity;
import com.example.funactivity.R;
import com.example.funactivity.adapter.SignUpAdapter;
import com.example.funactivity.entity.User.UserEnterActivity;
import com.example.funactivity.util.Constant;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SignUpActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private List<UserEnterActivity> list;
    private SignUpAdapter signUpAdapter;
    private ImageView back;
    private TextView setting;
    private OkHttpClient client;
    private String id;
    private SmartRefreshLayout srl;
    private int pageNum = 1;//当前页码
    private int pageSize = 20;//每页数据条数
    private int cancelPosition;//点击取消报名的活动处于list的位置
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 1:
                    //在list列表移除
                    list.remove(cancelPosition);
                    //在界面移除
                    signUpAdapter.notifyDataSetChanged();
                    Toast toast = Toast.makeText(SignUpActivity.this,
                            "取消成功", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        EventBus.getDefault().register(this);
        id = getIntent().getStringExtra("id");
        initView();
        //改变设置字体
        TextView setting = findViewById(R.id.tv_text);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "FZGongYHJW.TTF");
        setting.setTypeface(typeface);
        //初始化
        list = new ArrayList<>();
        recyclerView = findViewById(R.id.recycle_view);
        signUpAdapter = new SignUpAdapter(this, R.layout.signup_or_collect_layout, list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        //点击事件
        signUpAdapter.setOnItemClickListener(onMyItemClickListener);
        recyclerView.setAdapter(signUpAdapter);
        signUpAdapter.setOn(view -> {
            int position = recyclerView.getChildAdapterPosition(view);
            Intent intent = new Intent();
            intent.putExtra("id", id + "");//用户id
            intent.putExtra("activityId", list.get(position).getActivity().getActivityId() + "");//活动id
            Log.e("id", list.get(position).getId() + "");
            intent.setClass(view.getContext(), DetailActivity.class);
            startActivity(intent);
        });
    }

    private void initView() {
        back = findViewById(R.id.iv_back);
        back.setOnClickListener(v -> finish());
        //改变收藏的字体
        setting = findViewById(R.id.tv_text);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "FZGongYHJW.TTF");
        setting.setTypeface(typeface);
        srl = findViewById(R.id.srl);
        client = new OkHttpClient();
        srl.setEnableRefresh(false);
        requestData(1, pageSize);
        srl.setOnLoadMoreListener(refreshLayout -> requestData(pageNum, pageSize));
        recyclerView = findViewById(R.id.recycle_view);
        signUpAdapter = new SignUpAdapter(this, R.layout.signup_or_collect_layout, list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        //点击事件
        signUpAdapter.setOnItemClickListener(onMyItemClickListener);
        recyclerView.setAdapter(signUpAdapter);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updataUI(String msg) {
        if (msg.equals("update")) {
            //刷新adapter
            signUpAdapter.notifyDataSetChanged();
            //结束上拉动画
            srl.finishLoadMore();
        }
    }

    //点击取消报名
    private SignUpAdapter.OnMyItemClickListener onMyItemClickListener = new SignUpAdapter.OnMyItemClickListener() {
        @Override
        public void onMyItemClick(View view, int position) {
            switch (view.getId()) {
                case R.id.btn_cancel:
                    cancelPosition = position;
                    cancelEnrollActivity();

                    break;
            }
        }
    };

    //取消报名
    private void cancelEnrollActivity() {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("activityId", list.get(cancelPosition).getActivity().getActivityId() + "");
        builder.add("id", id);
        FormBody body = builder.build();
        final Request request = new Request.Builder()
                .post(body)
                .url(Constant.BASE_URL + "user/cancelEnrollActivity")
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //返回取消修改报名的结果
                assert response.body() != null;
                boolean result = Boolean.parseBoolean(response.body().string());
                if (result) {
                    Message message = new Message();
                    message.what = 1;
                    handler.sendMessage(message);
                } else {
                    Looper.prepare();
                    Toast toast = Toast.makeText(SignUpActivity.this,
                            "取消失败", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    Looper.loop();
                }
            }
        });
    }

    //从服务端获取数据
    private void requestData(int pageNum, int pageSize) {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("id", id + "");
        builder.add("pageNum", pageNum + "");
        builder.add("pageSize", pageSize + "");
        FormBody body = builder.build();
        Request request = new Request.Builder()
                .post(body)
                .url(Constant.BASE_URL + "activity/getEnterActivities")
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String actjson = response.body().string();
                if ("empty".equals(actjson)) {
                    Looper.prepare();
                    Toast.makeText(SignUpActivity.this, "没有更多内容了！", Toast.LENGTH_SHORT).show();
                    srl.finishLoadMoreWithNoMoreData();
                    Looper.loop();
                } else {
                    List<UserEnterActivity> activities = JSON.parseArray(actjson, UserEnterActivity.class);
                    list.addAll(activities);
                    SignUpActivity.this.pageNum++;
                    EventBus.getDefault().post("update");
                }
            }
        });
    }


}
