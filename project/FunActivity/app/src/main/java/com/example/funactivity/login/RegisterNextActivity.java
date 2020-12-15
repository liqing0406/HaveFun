package com.example.funactivity.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.funactivity.Main2Activity;
import com.example.funactivity.R;
import com.example.funactivity.entity.User.User;
import com.example.funactivity.util.Constant;
import com.example.funactivity.util.LocationUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RegisterNextActivity extends AppCompatActivity {
    private EditText password;
    private Button login;
    private String num;
    private OkHttpClient client;
    private String userJson;
    private String cityStr = "石家庄市 裕华区";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_next);
        EventBus.getDefault().register(this);
        initView();
        //获取返回的位置信息
        cityStr = new LocationUtil(RegisterNextActivity.this).getLocality();
        Intent intent = getIntent();
        num = intent.getStringExtra("num");
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }

    private void initView() {
        password = findViewById(R.id.et_password);
        login = findViewById(R.id.btn_login);
        client = new OkHttpClient();
    }

    public void register() {
        //提交键值对格式数据
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("phoneNum",num);
        builder.add("password", password.getText().toString());
        FormBody body = builder.build();
        //创建请求对象
        Request request = new Request.Builder()
                .post(body)//请求方式为post
               // .url(Constant.BASE_URL + "LoginServlet")
                .url(Constant.BASE_URL + "user/register")
                .build();
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
                if (back.equals("true")) {
                    login();
                }
            }
        });
    }
    //向服务端请求登录
    private void login() {
        //提交键值对格式数据
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("phoneNum", num);
        builder.add("password", password.getText().toString());
        FormBody body = builder.build();
        //创建请求对象
        Request request = new Request.Builder()
                .post(body)//请求方式为post
                .url(Constant.BASE_URL + "user/login")
                .build();
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
                if ("".equals(back)) {
                    Looper.prepare();
                    Toast.makeText(RegisterNextActivity.this, "账号或密码错误，请重新输入", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                } else {
                    userJson = back;
                    Log.e("获取的用户信息",userJson);
                    EventBus.getDefault().post("login");
                }
            }
        });
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void toLogin(String msg) {
        if (msg.equals("login")) {
            Intent intent = new Intent();
            intent.putExtra("user", userJson);
            intent.putExtra("code",100+"");
            intent.putExtra("cityStr",cityStr);
            intent.setClass(this, Main2Activity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
