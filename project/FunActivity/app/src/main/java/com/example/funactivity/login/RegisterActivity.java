package com.example.funactivity.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.funactivity.R;
import com.example.funactivity.util.Constant;

import java.io.IOException;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {
    private ImageView ivback;
    private EditText num;
    private EditText code;
    private Button getnum;
    private Button submit;
    private OkHttpClient client;
    private String back;//服务器返回注册信息
    private CountDownTimer timer;
    private int TIME = 60;//倒计时
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    int i = msg.arg1;
                    int i1 = msg.arg2;
                    if (i1==SMSSDK.RESULT_COMPLETE) {
                        //回调完成
                        if (i == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                            //校验验证码，返回校验的手机号和国家代码，提交验证码成功
                            Log.e("接口回调", "校验成功");
                            //页面跳转
                            // 如果验证码正确，跳转下一页面
                            Intent intent = new Intent();
                            intent.putExtra("num",num.getText().toString());
                            intent.setClass(RegisterActivity.this,RegisterNextActivity.class);
                            startActivity(intent);
                        }
                    }
                    break;
                case 2:
                    alertWarning();
                    code.requestFocus();//指定验证码输入框获取焦点
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
        ivback.setOnClickListener(v -> {
            finish();
        });
        //注册账号密码回调接口
        final EventHandler ethandler = new EventHandler(){
            @Override
            public void afterEvent(int i, int i1, Object o) {
                super.afterEvent(i, i1, o);
                if (i1==SMSSDK.RESULT_COMPLETE){
                    Message msg = new Message();
                    msg.arg1 = i;
                    msg.arg2 = i1;
                    msg.obj = o;
                    msg.what = 1;
                    handler.sendMessage(msg);
                }
            }
        };
        //注册
        SMSSDK.registerEventHandler(ethandler);
        //获取验证码按钮监听器
        getnum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //向手机号发送验证码
                sendMessage();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitCode();

            }
        });
    }
    //发送弹窗确认
    private void alertWarning(){
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @SuppressLint("ShowToast")
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case Dialog.BUTTON_POSITIVE:
                        dialog.dismiss();
                        SMSSDK.getVerificationCode("86",num.getText().toString());//给手机发送验证码
                        code.requestFocus();//指定验证码输入框获取焦点
                        getnum.setClickable(false);//设置获取验证码按钮不能点击

                        timer = new CountDownTimer((TIME+1)*1000,1000) {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onTick(long millisUntilFinished) {
                                getnum.setText(TIME--+"秒后重新获取");//设置获取验证码按钮显示内容为倒计时数字
                            }

                            @Override
                            public void onFinish() {
                                getnum.setClickable(true);//设置获取验证码按钮可点击
                                getnum.setText("重新发送");
                            }
                        };
                        timer.start();
                        break;
                    case Dialog.BUTTON_NEGATIVE:
                        dialog.dismiss();
                        Toast.makeText(RegisterActivity.this,"已取消",Toast.LENGTH_SHORT);
                        break;
                }
            }
        };
        //dialog参数设置,方法链调用
        new AlertDialog.Builder(this)
                .setTitle("发送验证码")
                .setMessage("我们将把验证码发送到以下号码\n"+"86-"+num.getText().toString())
                .setPositiveButton("确认",listener)
                .setNegativeButton("取消",listener)
                .create()
                .show();
    }
    //判断验证码
    private void submitCode(){
        if (!code.getText().toString().isEmpty()){//验证码不为空
            if (code.length()==6){
                SMSSDK.submitVerificationCode("86",num.getText().toString(),code.getText().toString());

            }else{
                Toast.makeText(this,"请输入完整验证码",Toast.LENGTH_SHORT).show();
                code.requestFocus();
            }
        }else{
            Toast.makeText(this,"请输入验证码",Toast.LENGTH_SHORT).show();
            code.requestFocus();
        }
    }
    //发送验证码
    private void sendMessage() {
        if (!num.getText().toString().isEmpty()){//手机号不为空
            if (num.getText().toString().length()==11){
                isRegister();
            }else{
                Toast.makeText(this,"请输入完整的电话号码",Toast.LENGTH_SHORT).show();
                num.requestFocus();//指定账号输入框获取焦点
            }
        }else{
            Toast.makeText(this,"请输入电话号码",Toast.LENGTH_SHORT).show();
            num.requestFocus();//指定账号输入框获取焦点
        }
    }
    //判断手机号是否注册过
    public void isRegister(){
        //提交键值对格式数据
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("phoneNum",num.getText().toString());
//        builder.add("num",num.getText().toString());
        FormBody body = builder.build();
        //创建请求对象
        Request request = new Request.Builder()
                .post(body)//请求方式为post
//                .url(Constant.BASE_URL+"/LoginServlet")
                .url(Constant.BASE_URL+"/user/judgeRegistered")
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
                back = response.body().string();
                if (back.equals("true")) {//没有被注册过
                    Message message = new Message();
                    message.what = 2;
                    handler.sendMessage(message);
                } else if ("false".equals(back)) {//手机号已注册
                    Looper.prepare();
                    Toast.makeText(RegisterActivity.this, "此手机号码已经注册，请直接登录", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        });
    }
    private void initView(){
        ivback = findViewById(R.id.iv_back);
        num = findViewById(R.id.et_num);
        code = findViewById(R.id.et_code);
        getnum = findViewById(R.id.btn_getnum);
        submit = findViewById(R.id.btn_submit);
        client = new OkHttpClient();
        back = "";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterAllEventHandler();//注销回调接口
    }
}
