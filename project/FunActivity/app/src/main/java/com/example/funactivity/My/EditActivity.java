package com.example.funactivity.My;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.example.funactivity.Main2Activity;
import com.example.funactivity.R;
import com.example.funactivity.entity.activity.Activity;
import com.example.funactivity.entity.activity.ActivityDetail;
import com.example.funactivity.util.Constant;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import github.chenupt.multiplemodel.IViewHolder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class EditActivity extends AppCompatActivity {
    private ImageView img;
    private TextView title;//标题
    private EditText theme;//活动主题
    private EditText date;//活动时间
    private EditText money;//活动费用
    private EditText province;//省份
    private EditText city;//城市
    private EditText county;//县（区）
    private EditText detail;//详细地址
    private EditText phone;//电话号码
    private EditText other;//其他
    private Button submit;//提交
    private String userStr;//用户str
    private String activityId;//活动id
    private OkHttpClient client;
    private ActivityDetail activityDetail;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    ActivityDetail activityDetail = (ActivityDetail) msg.obj;
                    setData(activityDetail);
                    break;

            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        initView();
        getData();
    }

    private void initView() {
        client = new OkHttpClient();
        img=findViewById(R.id.iv_img);
        title=findViewById(R.id.tv_title);
        theme=findViewById(R.id.et_theme);
        date=findViewById(R.id.et_date);
        money=findViewById(R.id.et_money);
        province=findViewById(R.id.et_province);
        city=findViewById(R.id.et_city);
        county=findViewById(R.id.et_county);
        detail=findViewById(R.id.et_detail);
        phone=findViewById(R.id.et_phone);
        other=findViewById(R.id.et_other);
        submit=findViewById(R.id.btn_submit);

        Intent intent=getIntent();
        userStr = intent.getStringExtra("user");
        activityId = intent.getStringExtra("activityId");
    }

    //提交按钮
    public void buttonClicked(View view) {
        switch (view.getId()){
            case R.id.btn_submit:
                //提交修改
                dataChanged();
                break;
//            case R.id.iv_delete:
//                //删除
//                showAlertDialog();
//                break;
        }
    }

    //显示对话框
    private void showAlertDialog() {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(this)
                .setTitle("删除确认")
                .setMessage("确认删除这项活动吗?")
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteActivity();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    //删除活动
    private void deleteActivity() {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("activityId",activityId);
        FormBody body = builder.build();
        Request request = new Request.Builder()
                .post(body)
                .url(Constant.BASE_URL + "activity/deleteActivity")
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json=response.body().string();
                if (json.equals("true")){
//                    Intent i=new Intent(EditActivity.this, Main2Activity.class);
//                    i.putExtra("user",userStr);
//                    i.putExtra("code",300+"");
//                    startActivity(i);

                    finish();
//                    Toast.makeText(EditActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(EditActivity.this,"删除失败",Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    //提交修改
    private void dataChanged() {
        FormBody.Builder builder = new FormBody.Builder();
        ActivityDetail ad=setDetail();
        builder.add("activityDetailJson",JSON.toJSONString(ad));
        FormBody body = builder.build();
        Request request = new Request.Builder()
                .post(body)
                .url(Constant.BASE_URL + "activity/modifyActivity")
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
//                    Looper.prepare();
//                    Toast.makeText(EditActivity.this,"修改成功",Toast.LENGTH_SHORT).show();
//                    Looper.loop();
//                    Intent i=new Intent(EditActivity.this, Main2Activity.class);
//                    i.putExtra("user",userStr);
//                    i.putExtra("code",200+"");
//                    startActivity(i);
                    finish();
                }else {
                    Toast.makeText(EditActivity.this,"修改失败",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private ActivityDetail setDetail(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//        Activity activity=activityDetail.getActivity();
//        activityDetail.getActivity().setActivityTile(title.toString());
        activityDetail.getActivity().setActivityCost(money.getText().toString());
        try {
            activityDetail.getActivity().setActivityTime(dateFormat.parse(date.getText().toString()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        activityDetail.getActivity().setActivityContact(phone.getText().toString());
        activityDetail.getActivity().getActivityLocation().setProvince(province.getText().toString());
        activityDetail.getActivity().getActivityLocation().setCity(city.getText().toString());
        activityDetail.getActivity().getActivityLocation().setCounty(county.getText().toString());
        activityDetail.getActivity().getActivityLocation().setDetailedAddress(detail.getText().toString());
        activityDetail.setOtherInfo(other.getText().toString());
        return activityDetail;
    }




    //获取活动列表
    public void getData() {
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
                Log.e("json",jsonStr);
                activityDetail = JSON.parseObject(jsonStr, ActivityDetail.class);
                Message message = new Message();
                message.what = 1;
                message.obj = activityDetail;
                handler.sendMessage(message);
            }
        });
    }

    private void setData(ActivityDetail activityDetail) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        //显示修改前的活动信息
        title.setText(activityDetail.getActivity().getActivityTile());
        theme.setText(activityDetail.getActivity().getTypeOfKind().toString());
        date.setText(dateFormat.format(activityDetail.getActivity().getActivityTime()));
        money.setText(activityDetail.getActivity().getActivityCost());
        province.setText(activityDetail.getActivity().getActivityLocation().getProvince());
        city.setText(activityDetail.getActivity().getActivityLocation().getCity());
        county.setText(activityDetail.getActivity().getActivityLocation().getCounty());
        detail.setText(activityDetail.getActivity().getActivityLocation().getDetailedAddress());
        phone.setText(activityDetail.getActivity().getActivityContact());
        other.setText(activityDetail.getOtherInfo());
        Log.e("省份",activityDetail.getActivity().getActivityLocation().getProvince()+activityDetail.getActivity().getActivityLocation().getCity());
        Glide.with(this)
                .load(Constant.PIC_PATH + activityDetail.getActivity().getFrontPicture().getPictureName())
                .into(img);
    }


}
