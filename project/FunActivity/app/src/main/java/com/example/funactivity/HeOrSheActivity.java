package com.example.funactivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.funactivity.entity.User.User;
import com.example.funactivity.news.ChatActivity;
import com.example.funactivity.userFragment.CollectFragment;
import com.example.funactivity.userFragment.FaBuFragment;
import com.example.funactivity.userFragment.HandUpFragment;
import com.example.funactivity.util.Constant;
import com.google.gson.Gson;
import com.hyphenate.easeui.EaseConstant;

import java.io.IOException;

import github.chenupt.multiplemodel.viewpager.ModelPagerAdapter;
import github.chenupt.multiplemodel.viewpager.PagerManager;
import github.chenupt.springindicator.SpringIndicator;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HeOrSheActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private SpringIndicator springIndicator;
    private Gson gson;
    private OkHttpClient client;
    private String id;//她的id
    private TextView tvId,tvQianMing;
    private ImageView ivSex,touxiang;
    private User user;
    private PagerManager manager = new PagerManager();
    private ModelPagerAdapter adapter;
    private ImageView ivBack;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 1://获取User
                    tvId.setText(user.getUserId()+"");
                    tvQianMing.setText(user.getUserDetail().getPersonalSignature());
                    if(user.getUserDetail().getSex().equals("1")){
                        ivSex.setImageResource(R.drawable.man);
                    }else{
                        ivSex.setImageResource(R.drawable.woman);
                    }
                    RequestOptions requestOptions=new RequestOptions()
                            .circleCrop();
                    Glide.with(getApplicationContext())
                            .load(Constant.PIC_PATH+user.getHeadPortrait())
                            .apply(requestOptions)
                            .into(touxiang);
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_he_or_she);

        findViews();

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HeOrSheActivity.this, ChatActivity.class);
                intent.putExtra(EaseConstant.EXTRA_USER_ID, user.getPhoneNum()+"");
                intent.putExtra(EaseConstant.EXTRA_CHAT_NAME,user.getUserName());
                intent.putExtra(EaseConstant.EXTRA_USER_HEAD,user.getHeadPortrait());
                startActivity(intent);
            }
        });

        //获取进入页面的用户的id
        id = getIntent().getStringExtra("id");

        getUserInfo();
        setIndicator();
        //getGuanZhuAndFenSi();


    }

    /**
     * 获取粉丝和专注数量，并显示
     */
    private void getGuanZhuAndFenSi() {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("id",id+"");
        FormBody body = builder.build();
        //获取粉丝数量
        Request request1 = new Request.Builder()
                .url(Constant.BASE_URL+"user/getFollowedCount")
                .post(body)
                .build();
        Call call1 = client.newCall(request1);
        call1.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String result = response.body().string();
                Message m = new Message();
                m.obj = result;
                m.what = 2;
                handler.sendMessage(m);
            }
        });
        //获取关注数量
        Request request2 = new Request.Builder()
                .url(Constant.BASE_URL+"user/getFollowCount")
                .post(body)
                .build();
        Call call2 = client.newCall(request2);
        call2.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String result = response.body().string();
                Message message = new Message();
                message.what = 3;
                message.obj = result;
                handler.sendMessage(message);
            }
        });


    }

    private void setIndicator() {
        manager.setTitles(getTitles());
        manager.addFragment(new FaBuFragment());
        manager.addFragment(new HandUpFragment());
        manager.addFragment(new CollectFragment());
        adapter = new ModelPagerAdapter(getSupportFragmentManager(), manager);
        viewPager.setAdapter(adapter);
        springIndicator.setViewPager(viewPager);
    }

    private String[] getTitles() {
        return new String[]{"发布","参加","收藏"};
    }

    /**
     * 获得用户的信息，并显示
     */
    private void getUserInfo() {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("id",id+"");
        FormBody body = builder.build();
        Request request1 = new Request.Builder()
                .url(Constant.BASE_URL+"user/getUser")
                .post(body)
                .build();
        Call call = client.newCall(request1);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String result = response.body().string();
                Log.e("微信第三代",result+"微信第三代");
                user = gson.fromJson(result, User.class);
                Message m = new Message();
                m.what = 1;
                handler.sendMessage(m);
            }
        });
    }

    private void findViews() {
        touxiang = findViewById(R.id.touxaing);
        client = new OkHttpClient();
        gson = new Gson();
        tvId = findViewById(R.id.tv_id);
        tvQianMing = findViewById(R.id.tv_qianming);
        ivSex = findViewById(R.id.iv_sex);
        viewPager = findViewById(R.id.view_pager);
        springIndicator = findViewById(R.id.indicator);
        ivBack = findViewById(R.id.iv_back);
    }

    public String getId(){
        return id;
    }
}
