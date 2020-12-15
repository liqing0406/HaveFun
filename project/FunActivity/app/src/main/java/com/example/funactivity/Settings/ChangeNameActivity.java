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

public class ChangeNameActivity extends AppCompatActivity {
    private Button save;
    private EditText name;
    private String oldname;
    private String newname;
    private OkHttpClient client;
    private User user=new User();
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    user.setUserName(newname);
                    EventBus.getDefault().post(user);
                    finish();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_name);

        save=findViewById(R.id.btn_save);
        name=findViewById(R.id.et_name);
        client=new OkHttpClient();
        EventBus.getDefault().register(this);
        name.setText(oldname);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newname=name.getText().toString();
                Log.e("new",name.getText().toString());//得到修改后的名字
                changename();
            }
        });
    }
    @Subscribe(threadMode = ThreadMode.MAIN ,sticky = true)
    public void updata(User u){
        oldname=u.getUserName();
        Log.e("获取的用户名",oldname);
        if (u.getUserName() != null){
            user=u;
        }
    }
    public void back(View view){
        finish();
    }
    public void changename(){
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("id", user.getId()+"");
        builder.add("userName", name.getText().toString());
        FormBody body = builder.build();
        Request request = new Request.Builder()
                .post(body)
                .url(Constant.BASE_URL + "user/modifyUserName")
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
                    Toast.makeText(ChangeNameActivity.this, "修改失败，请重试！", Toast.LENGTH_SHORT).show();
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
