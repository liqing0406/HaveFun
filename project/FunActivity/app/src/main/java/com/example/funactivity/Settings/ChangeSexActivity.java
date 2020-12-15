package com.example.funactivity.Settings;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

public class ChangeSexActivity extends AppCompatActivity {
    private RadioGroup radioGroup;
    private RadioButton man;
    private RadioButton woman;
    private Button save;
    private String oldsex;
    private String newsex;
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
                    userDetail.setSex(Integer.parseInt(newsex));
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
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_change_sex);
        radioGroup=findViewById(R.id.rg_manorwoman);
        man=findViewById(R.id.rb_man);
        woman=findViewById(R.id.rb_woman);
        save=findViewById(R.id.btn_save);
        client=new OkHttpClient();
        if(oldsex.equals("0")){
            woman.setTextColor(getColor(R.color.pink));
            man.setTextColor(getColor(R.color.black));
            woman.setTextSize(30);
            man.setTextSize(20);
        }
        else if(oldsex.equals("1")){
            man.setTextColor(getColor(R.color.pink));
            woman.setTextColor(getColor(R.color.black));
            man.setTextSize(30);
            woman.setTextSize(20);
        }
        radioGroup.setOnCheckedChangeListener(new changListener());
        int checkid=radioGroup.getCheckedRadioButtonId();
        RadioButton checkbtn = findViewById(checkid);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSex();
            }
        });


    }

    public void back(View view){
        finish();
    }
    @Subscribe(threadMode = ThreadMode.MAIN ,sticky = true)
    public void updata(User u){
       Log.e("获取的性别",u.getUserDetail().getSex().toString());
       user=u;
       oldsex=user.getUserDetail().getSex()+"";

    }
    public class changListener implements RadioGroup.OnCheckedChangeListener{

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId){
                case R.id.rb_man:
                    man.setTextColor(getResources().getColor(R.color.pink));
                    woman.setTextColor(getResources().getColor(R.color.black));
                    man.setTextSize(30);
                    woman.setTextSize(20);
                    newsex="1";
                    break;
                case R.id.rb_woman:
                    woman.setTextColor(getResources().getColor(R.color.pink));
                    man.setTextColor(getResources().getColor(R.color.black));
                    woman.setTextSize(30);
                    man.setTextSize(20);
                    newsex="0";
                    break;
            }
        }
    }
    public void changeSex(){
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("id", user.getId()+"");
        builder.add("sex",newsex);
        FormBody body = builder.build();
        Request request = new Request.Builder()
                .post(body)
                .url(Constant.BASE_URL + "user/modifyUserSex")
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
                    Toast.makeText(ChangeSexActivity.this, "修改失败，请重试！", Toast.LENGTH_SHORT).show();
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