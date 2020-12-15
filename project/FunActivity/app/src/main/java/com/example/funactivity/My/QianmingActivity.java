package com.example.funactivity.My;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.funactivity.R;
import com.example.funactivity.entity.User.User;
import com.example.funactivity.entity.User.UserDetail;
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

public class QianmingActivity extends AppCompatActivity {
    private ImageView back;
    private Button edit;//提交修改按钮
    private EditText personalSignature;
    private String text;
    private String id;
    private String name;
    private String sex;
    private OkHttpClient client;
    private User user=new User();
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                   UserDetail userDetail=new UserDetail();
                   userDetail=user.getUserDetail();
                   userDetail.setPersonalSignature(personalSignature.getText().toString());
                   user.setUserDetail(userDetail);
                    EventBus.getDefault().post(user);
                    finish();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qianming);
        initView();
    }

    private void initView() {
        back = findViewById(R.id.iv_back);
        edit = findViewById(R.id.btn_edit);
        personalSignature = findViewById(R.id.et_qianming);
        EventBus.getDefault().register(this);
        personalSignature.setText(text);
        client = new OkHttpClient();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //提交修改
        edit.setOnClickListener(v -> changePersonalSignature());
    }
    @Subscribe(threadMode = ThreadMode.MAIN ,sticky = true)
    public void updata(User u){
        text=u.getUserDetail().getPersonalSignature();
        user=u;
    }
    private void changePersonalSignature() {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("id", user.getId()+"");
        builder.add("personalSignature", personalSignature.getText().toString());
        FormBody body = builder.build();
        Request request = new Request.Builder()
                .post(body)
                .url(Constant.BASE_URL + "user/modifyPersonalSignature")
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
                    Toast.makeText(QianmingActivity.this, "修改失败，请重试！", Toast.LENGTH_SHORT).show();
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
