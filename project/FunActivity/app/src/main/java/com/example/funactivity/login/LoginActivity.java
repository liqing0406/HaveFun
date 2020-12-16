package com.example.funactivity.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.funactivity.Main2Activity;
import com.example.funactivity.MainActivity;
import com.example.funactivity.R;
import com.example.funactivity.entity.User.User;
import com.example.funactivity.model.Model;
import com.example.funactivity.model.bean.UserInfo;
import com.example.funactivity.util.Constant;
import com.example.funactivity.util.LocationUtil;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.EaseConstant;

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

public class LoginActivity extends AppCompatActivity {
    private EditText num;
    private EditText password;
    private ImageView visible;
    private CheckBox remember;
    private CheckBox autologin;
    private Button find;
    private Button login;
    private Button register;
    private OkHttpClient client;
    private boolean flag = false;
    private String cityStr = "石家庄市 裕华区";//市+区
    private String userJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //注册事件订阅者
        EventBus.getDefault().register(this);
        initView();
        //动态获取位置的相关权限
        requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,},
                100);
        //设置密码是否可见
        visible.setOnClickListener(v -> {
            if (flag) {
                password.setTransformationMethod(PasswordTransformationMethod.getInstance());//不可见
                visible.setImageResource(R.drawable.invisible);
            } else {
                password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());//可见
                visible.setImageResource(R.drawable.visible);
            }
            flag = !flag;
            password.setSelection(password.getText().toString().length());
        });
        //找回密码点击事件
        find.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), GetBackPasswordActivity.class);
            startActivity(intent);
        });
        //登录按钮点击事件
        login.setOnClickListener(v -> {
            if (num.getText().toString().length() == 0) {//账号为空
                Toast.makeText(v.getContext(), "请输入账号", Toast.LENGTH_SHORT).show();
            } else if (password.getText().toString().length() == 0) {//密码为空
                Toast.makeText(v.getContext(), "请输入密码", Toast.LENGTH_SHORT).show();
            } else if (num.getText().toString().length() != 11) {//手机号位数错误
                Toast.makeText(v.getContext(), "您输入的手机号有误", Toast.LENGTH_SHORT).show();
            } else {
                login();
            }
        });
        //注册按钮点击事件
        register.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), RegisterActivity.class);
            startActivity(intent);
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            //获取返回的位置信息
//            cityStr = new LocationUtil(LoginActivity.this).getLocality();
       }
    }

    public String getCityStr() {
        return cityStr;
    }

    public void setCityStr(String cityStr) {
        this.cityStr = cityStr;
    }

    private void initView() {
        num = findViewById(R.id.et_num);
        password = findViewById(R.id.et_password);
        find = findViewById(R.id.btn_find);
        remember = findViewById(R.id.cb_remember);
        autologin = findViewById(R.id.cb_auto_login);
        visible = findViewById(R.id.iv_visible);
        login = findViewById(R.id.btn_login);
        register = findViewById(R.id.btn_register);
        client = new OkHttpClient();
    }

    //向服务端请求登录
    private void login() {
        //提交键值对格式数据
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("phoneNum", num.getText().toString());
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
                    Toast.makeText(LoginActivity.this, "账号或密码错误，请重新输入", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                } else {
                    userJson = back;
                    Log.e("获取的用户信息", userJson);
                    Model.getInstance().getGlobalThreadPool().execute(() -> EMClient.getInstance().login(num.getText().toString(), password.getText().toString(), new EMCallBack() {
                        @Override
                        public void onSuccess() {
                            //对模型层数据的处理
                            Model.getInstance().loginSuccess(new UserInfo(num.getText().toString()));

                            //保存到本地
                            Model.getInstance().getUserAccountDao().addAccount(new UserInfo(num.getText().toString()));
                            //提示登录成功
                            Toast("欢迎登录 " + num.getText().toString());

                            finish();
                        }

                        //登录失败的处理
                        @Override
                        public void onError(int i, String s) {
                            Toast("登录失败 " + s);
                        }

                        @Override
                        public void onProgress(int i, String s) {

                        }
                    }));
                    EventBus.getDefault().post("login");
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void toLogin(String msg) {
        if (msg.equals("login")) {
            Log.i("phz", "正在登录，跳转中");
            Intent intent = new Intent();
            intent.putExtra("user", userJson);
            intent.putExtra("code", 100 + "");
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

    private void Toast(String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LoginActivity.this, str, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
