package com.example.funactivity.My;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.example.funactivity.Fragment.MyFragment;
import com.example.funactivity.R;
import com.example.funactivity.adapter.CollectAdapter;
import com.example.funactivity.entity.User.UserCollectActivity;
import com.example.funactivity.entity.User.UserPublishActivity;
import com.example.funactivity.entity.activity.Activity;
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
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CollectActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private List<UserCollectActivity> activities= new ArrayList<>();
    private CollectAdapter collectAdapter;
    private ImageView back;
    private OkHttpClient client;
    private TextView setting;
    private SmartRefreshLayout srl;
    private int pageNum = 1;//当前页码
    private int pageSize = 20;//每页数据条数
    private String id;
    private int calcelPosition;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 1:
                    //在list列表移除
                    activities.remove(calcelPosition);
                    //在界面移除
                    collectAdapter.notifyDataSetChanged();
                    Toast toast = Toast.makeText(CollectActivity.this,
                            "取消成功",Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect);
        EventBus.getDefault().register(this);
        id = getIntent().getStringExtra("id");
        initView();

        recyclerView = findViewById(R.id.recycle_view);
        collectAdapter = new CollectAdapter(this, R.layout.signup_or_collect_layout, activities,id);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        //点击事件
        collectAdapter.setOnItemClickListener(onItemClickListener);
        recyclerView.setAdapter(collectAdapter);
        collectAdapter.setOn(new CollectAdapter.onItemClickListener() {
            @Override
            public void onItemClick(View view) {
                int position=recyclerView.getChildAdapterPosition(view);
                Intent intent = new Intent();
                intent.putExtra("collect",false);//是否收藏
                intent.putExtra("id", id + "");//用户id
                intent.putExtra("activityId", activities.get(position).getActivity().getActivityId() + "");//活动id
                Log.e("id",activities.get(position).getId() + "");
                intent.setClass(view.getContext(), DetailActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initView() {
        back =  findViewById(R.id.iv_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //改变收藏的字体
        setting = findViewById(R.id.tv_text);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "FZGongYHJW.TTF");
        setting.setTypeface(typeface);
        //初始化
        srl = findViewById(R.id.srl);
        client = new OkHttpClient();
        srl.setEnableRefresh(false);
        requestData(1,pageSize);
        srl.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                requestData(pageNum, pageSize);
            }
        });
        recyclerView = findViewById(R.id.recycle_view);
        collectAdapter = new CollectAdapter(this, R.layout.signup_or_collect_layout, activities,id);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        //点击事件
        collectAdapter.setOnItemClickListener(onItemClickListener);
        recyclerView.setAdapter(collectAdapter);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updataUI(String msg) {
        if (msg.equals("update")) {
            //刷新adapter
            collectAdapter.notifyDataSetChanged();
            //结束上拉动画
            srl.finishLoadMore();
        }
    }
    //从服务端获取数据
    private void requestData(int pageNum, int pageSize) {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("id",id+"");
        builder.add("pageNum", pageNum + "");
        builder.add("pageSize", pageSize + "");
        FormBody body = builder.build();
        Request request = new Request.Builder()
                .post(body)
                .url(Constant.BASE_URL + "activity/getCollectedActivities")
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
                    Toast.makeText(CollectActivity.this, "没有更多内容了！", Toast.LENGTH_SHORT).show();
                    srl.finishLoadMoreWithNoMoreData();
                    Looper.loop();
                } else {
                    List<UserCollectActivity> activitie1 = JSON.parseArray(actjson, UserCollectActivity.class);
                    activities.addAll(activitie1);
                    CollectActivity.this.pageNum++;
                    EventBus.getDefault().post("update");
                }
            }
        });
    }
    private CollectAdapter.OnMyItemClickListener onItemClickListener = new CollectAdapter.OnMyItemClickListener() {
        @Override
        public void onMyItemClick(View view, int position) {
            switch (view.getId()) {
                case R.id.btn_cancel:
                    calcelPosition = position;
                    //从数据库移除
                    changeCollect(false,position);
                    break;
            }
        }
    };

    public void changeCollect(Boolean collect,int position) {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("activityId", activities.get(position).getActivity().getActivityId()+"");
        builder.add("id", id);
        builder.add("collect", collect + "");
        FormBody body = builder.build();
        final Request request = new Request.Builder()
                .post(body)
                .url(Constant.BASE_URL + "user/changeCollectActivity")
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //返回修改收藏的结果（修改成功或修改失败）
                boolean result = Boolean.parseBoolean(response.body().string());
                if(result){
                    Message message = new Message();
                    message.what = 1;
                    handler.sendMessage(message);
                }else {
                    Looper.prepare();
                    Toast toast = Toast.makeText(CollectActivity.this,
                            "取消失败",Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                    Looper.loop();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
