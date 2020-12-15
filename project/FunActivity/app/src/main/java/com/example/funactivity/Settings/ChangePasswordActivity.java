package com.example.funactivity.Settings;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.funactivity.R;
import com.example.funactivity.entity.User.User;
import com.example.funactivity.util.Constant;

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

public class ChangePasswordActivity extends AppCompatActivity {
   private EditText oldPassword;
   private EditText newPassword;
   private String oldtext;
   private String newtext;
   private Button btnSave;
    private OkHttpClient client;
    private User user=new User();
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    user.setPassword(newtext);
                    EventBus.getDefault().post(user);
                    finish();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        EventBus.getDefault().register(this);
        oldPassword=findViewById(R.id.et_old_password);
        newPassword=findViewById(R.id.et_new_password);
        btnSave=findViewById(R.id.btn_save);
        client=new OkHttpClient();
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("旧密码",oldtext);
                if (oldPassword.getText().toString().equals("")!=true&&newPassword.getText().toString().equals("")!=true){
                newtext=newPassword.getText().toString();
                if(oldPassword.getText().toString().equals(oldtext)!=true&&newPassword.getText().toString().equals(oldtext)!=true){
                    Toast.makeText(ChangePasswordActivity.this, "旧密码输入错误！", Toast.LENGTH_SHORT).show();
                }
                else if (newPassword.getText().toString().equals(oldtext)&&oldPassword.getText().toString().equals(oldtext)){
                    Toast.makeText(ChangePasswordActivity.this, "旧密码和新密码不得相同！", Toast.LENGTH_SHORT).show();
                }
                else if (oldPassword.getText().toString().equals(oldtext)!=true&&newPassword.getText().toString().equals(oldtext)){
                    Toast.makeText(ChangePasswordActivity.this, "旧密码和新密码错误！", Toast.LENGTH_SHORT).show();
                }else if(oldPassword.getText().toString().equals(oldtext)&&newPassword.getText().toString().equals(oldtext)!=true){
                    changepassword();
                }
                }else {
                    Toast.makeText(ChangePasswordActivity.this, "旧密码,新密码不能为空！", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
    @Subscribe(threadMode = ThreadMode.MAIN ,sticky = true)
    public void updata(User u){
       oldtext=u.getPassword();
       user=u;

    }
    public void back(View view){
        finish();
    }

    public void changepassword(){
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("phoneNum", user.getPhoneNum()+"");
        builder.add("password",newtext);
        FormBody body = builder.build();
        Request request = new Request.Builder()
                .post(body)
                .url(Constant.BASE_URL + "user/modifyPassword")
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //获取修改结果
                String json = response.body().string();
                Log.e("修改结果",json);
                if (json.equals("true")) {
                    Message message = new Message();
                    message.what = 1;
                    handler.sendMessage(message);
                } else {
                    Looper.prepare();
                    Toast.makeText(ChangePasswordActivity.this, "修改失败，请重试！", Toast.LENGTH_SHORT).show();
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
