package com.example.funactivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.example.funactivity.entity.activity.ActivityDetail;
import com.example.funactivity.news.ChatActivity;
import com.example.funactivity.util.Constant;
import com.hyphenate.easeui.EaseConstant;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DetailActivity extends AppCompatActivity {
    private ImageView back;//返回
    private ImageView img;//图片
    private TextView title;//标题
    private TextView theme;//主题
    private TextView date;//活动时间
    private TextView money;//费用
    private TextView place;//集合地点
    private ImageView map;//地图位置
    private TextView phone;//联系方式
    private TextView detail;//活动介绍
    private TextView other;//其他信息
    private ImageView chat;//私聊发布者
    private ImageView like;//收藏
    private TextView collectNum;
    private Button enroll;//报名
    private OkHttpClient client;
    private String id;//用户id
    private String activityId;//活动id
    private Integer num;//收藏人数
    private Boolean isCollect;//是否收藏过
    private Boolean collect;//收藏是否可点击
    private ActivityDetail activityDetail;
    private LinearLayout toChat;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 1:
                    setData(activityDetail);
                    break;
                case 2:
                    String result1 = (String) msg.obj;
                    Log.e("报名是否成功", "" + result1);
                    switch (result1) {
                        case "true":
                            Toast.makeText(DetailActivity.this, "报名成功", Toast.LENGTH_SHORT).show();
                            enroll.setText("已报名");
                            break;
                        case "exists":
                            Toast.makeText(DetailActivity.this, "您已经报名了", Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(DetailActivity.this, "报名失败", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    break;
                case 3:
                    Boolean result2 = (Boolean) msg.obj;
                    if (result2) { //返回修改成功结果
                        if (isCollect) {//当前为已收藏，点击更换为未收藏，收藏人数-1
                            like.setImageResource(R.drawable.like1);
                            Log.e("收藏人数", num + "");
                            collectNum.setText((--num) + "");
                            isCollect = !isCollect;
                            Toast.makeText(DetailActivity.this, "已取消收藏", Toast.LENGTH_SHORT).show();
                        } else {//当前为未收藏，点击更换为已收藏，收藏人数+1
                            like.setImageResource(R.drawable.like);
                            collectNum.setText((++num) + "");
                            isCollect = !isCollect;
                            Toast.makeText(DetailActivity.this, "收藏成功", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(DetailActivity.this, "请重试", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 4:
                    Boolean isCollect = (Boolean) msg.obj;
                    if (isCollect) {//收藏过
                        like.setImageResource(R.drawable.like);
                    } else {//未收藏
                        like.setImageResource(R.drawable.like1);
                    }
                    break;
                case 5://已经报名
                    String s = (String) msg.obj;
                    if (s.equals("true")) {
                        enroll.setText("已报名");
                    }
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        initView();
        getDate();//获取活动信息
        getCollect();//用户是否收藏该活动
    }

    private void ifHanUp() {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("activityId", activityId);
        builder.add("id", id);
        FormBody body = builder.build();
        Request request = new Request.Builder()
                .post(body)
                .url(Constant.BASE_URL + "activity/judgeEnterActivity")
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Log.e("result", result + "");
                Message message = new Message();
                message.what = 5;
                message.obj = result;
                handler.sendMessage(message);
            }
        });
    }

    private void initView() {
        back = findViewById(R.id.iv_back);
        img = findViewById(R.id.iv_img);
        title = findViewById(R.id.tv_title);
        theme = findViewById(R.id.tv_theme);
        date = findViewById(R.id.tv_date);
        money = findViewById(R.id.tv_money);
        place = findViewById(R.id.tv_place);
        map = findViewById(R.id.iv_map);
        phone = findViewById(R.id.tv_phone);
        other = findViewById(R.id.tv_other);
        chat = findViewById(R.id.iv_chat);
        like = findViewById(R.id.iv_like);
        collectNum = findViewById(R.id.tv_collectnum);
        enroll = findViewById(R.id.enroll);
        toChat = findViewById(R.id.chat);
        client = new OkHttpClient();
        final Intent intent = getIntent();
        collect = intent.getBooleanExtra("collect", true);
        id = intent.getStringExtra("id");
        activityId = intent.getStringExtra("activityId");
        ifHanUp();
    }

    @SuppressLint("SetTextI18n")
    private void setData(ActivityDetail activityDetail) {
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        title.setText(activityDetail.getActivity().getActivityTile());
        theme.setText(activityDetail.getActivity().getTypeOfKind().toString());
        date.setText(dateFormat.format(activityDetail.getActivity().getActivityTime()));
        money.setText(activityDetail.getActivity().getActivityCost());
        place.setText(activityDetail.getActivity().getActivityLocation().toString());
        phone.setText(activityDetail.getActivity().getActivityContact());
        other.setText(activityDetail.getOtherInfo());
        collectNum.setText(activityDetail.getActivity().getCollectNum() + "");
        num = activityDetail.getActivity().getCollectNum();
        Glide.with(this)
                .load(Constant.PIC_PATH + activityDetail.getActivity().getFrontPicture().getPictureName())
                .into(img);
    }

    public void buttonClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back://返回
                this.finish();
                break;
            case R.id.iv_chat://私聊
                Intent intent1 = new Intent(DetailActivity.this, ChatActivity.class);
                Log.i("用户id",activityDetail.getActivity().getUser().getId()+"");
                Log.i("用户id",EaseConstant.EXTRA_USER_ID+"");
                intent1.putExtra(EaseConstant.EXTRA_USER_ID, activityDetail.getActivity().getUser().getPhoneNum()+"");
                startActivity(intent1);
                break;
            case R.id.iv_like://收藏
                if (collect) {
                    if (isCollect) {//当前为已收藏，点击更换为未收藏
                        like.setImageResource(R.drawable.like1);
                        changeCollect(false);
                    } else {
                        like.setImageResource(R.drawable.like);
                        changeCollect(true);
                    }
                }

                break;
            case R.id.enroll://报名
                toEnroll();
                break;
            case R.id.iv_map://打开地图
                Intent intent = new Intent();
                intent.putExtra("placeStr", place.getText().toString());
                intent.setClass(this, MapActivity.class);
                startActivity(intent);
                break;
        }
    }

    public void changeCollect(Boolean collect) {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("activityId", activityId);
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
                Message message = new Message();
                message.what = 3;
                message.obj = result;
                handler.sendMessage(message);
            }
        });
    }

    public void getCollect() {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("id", id);
        builder.add("activityId", activityId);
        Log.i("phz", activityId + "," + id);
        FormBody body = builder.build();
        Request request = new Request.Builder()
                .post(body)
                .url(Constant.BASE_URL + "activity/judgeCollected")
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //返回查询结果
                isCollect = Boolean.parseBoolean(response.body().string());
                Message message = new Message();
                message.what = 4;
                message.obj = isCollect;
                handler.sendMessage(message);
            }
        });
    }

    public void toEnroll() {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("activityId", activityId);
        builder.add("id", id);
        FormBody body = builder.build();
        Request request = new Request.Builder()
                .post(body)
                .url(Constant.BASE_URL + "user/enrollActivity")
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //返回报名结果
                String result = response.body().string();
                Message message = new Message();
                message.what = 2;
                message.obj = result;
                handler.sendMessage(message);
            }
        });
    }

    public void getDate() {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("activityId", activityId);
        FormBody body = builder.build();
        Request request = new Request.Builder()
                .post(body)
                .url(Constant.BASE_URL + "activity/getActivityDetail")
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //获取活动列表数据
                String jsonStr = response.body().string();
                activityDetail = JSON.parseObject(jsonStr, ActivityDetail.class);
                Log.i("phz",jsonStr);
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }
        });
    }

}