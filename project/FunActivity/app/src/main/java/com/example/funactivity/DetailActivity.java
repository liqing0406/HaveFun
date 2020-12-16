package com.example.funactivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.example.funactivity.entity.activity.ActivityDetail;
import com.example.funactivity.news.ChatActivity;
import com.example.funactivity.util.Constant;
import com.hyphenate.easeui.EaseConstant;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
//    private TextView theme;//主题
    private TextView date;//活动时间
    private TextView money;//费用
    private TextView place;//集合地点
    private ImageView map;//地图位置
    private TextView phone;//联系方式
    private TextView detail;//活动介绍
    private TextView other;//其他信息
    private TextView theme;//活动类型
    private ImageView getMap;//定位
    private ImageView share;//分享
    private ImageView chat;//私聊发布者
    private ImageView like;//收藏
    private TextView collectNum;
    private Button enroll;//报名
    private OkHttpClient client;
    private String id;//用户id
    private String activityId;//活动id
    private Integer num;//收藏人数
    private Boolean isCollect;//是否收藏过
    private ActivityDetail activityDetail;
    private LinearLayout toChat;
    private File file;

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
                            collectNum.setText((--num) + "");
                            isCollect = !isCollect;
                            Toast.makeText(DetailActivity.this, "已取消收藏", Toast.LENGTH_SHORT).show();
                            like.setImageResource(R.drawable.like1);
                        } else {//当前为未收藏，点击更换为已收藏，收藏人数+1
                            like.setImageResource(R.drawable.like);
                            collectNum.setText((++num) + "");
                            isCollect = !isCollect;
                            Toast.makeText(DetailActivity.this, "收藏成功", Toast.LENGTH_SHORT).show();
                            like.setImageResource(R.drawable.like);
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
                    }else {
                        enroll.setText("报名");
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
        getMap = findViewById(R.id.iv_getmap);
        money = findViewById(R.id.tv_money);
        place = findViewById(R.id.tv_place);
        map = findViewById(R.id.iv_map);
        phone = findViewById(R.id.tv_phone);
        detail=findViewById(R.id.tv_detail);
        other = findViewById(R.id.tv_other);
        chat = findViewById(R.id.iv_chat);
        like = findViewById(R.id.iv_like);
        collectNum = findViewById(R.id.tv_collectnum);
        share = findViewById(R.id.iv_share);
        enroll = findViewById(R.id.enroll);
//        toChat = findViewById(R.id.chat);
        client = new OkHttpClient();
        final Intent intent = getIntent();
        id = intent.getStringExtra("id");
        activityId = intent.getStringExtra("activityId");
        ifHanUp();
    }

    @SuppressLint("SetTextI18n")
    private void setData(ActivityDetail activityDetail) {
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        title.setText(activityDetail.getActivity().getActivityTile());
        theme.setText(activityDetail.getActivity().getTypeOfKind().getTypeName());
        detail.setText(activityDetail.getActivityInfo());
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
                int faBuZheID = activityDetail.getActivity().getUser().getId();
                Log.e("fabuzhuID:"+faBuZheID,"id:"+id);
                if((id+"").equals(faBuZheID)){
                    Toast toast = Toast.makeText(DetailActivity.this,
                            "自己就是发布者",Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                }else {
                    Intent intent1 = new Intent(DetailActivity.this, ChatActivity.class);
                    intent1.putExtra(EaseConstant.EXTRA_USER_ID, activityDetail.getActivity().getUser().getPhoneNum()+"");
                    intent1.putExtra(EaseConstant.EXTRA_CHAT_NAME,activityDetail.getActivity().getUser().getUserName());
                    intent1.putExtra(EaseConstant.EXTRA_USER_HEAD,activityDetail.getActivity().getUser().getHeadPortrait());
                    startActivity(intent1);
                }
                break;
            case R.id.iv_like://收藏
                if (isCollect) {//当前为已收藏，点击更换为未收藏
                    changeCollect(false);
                } else {
                    changeCollect(true);
                }
                break;
            case R.id.enroll://报名
                toEnroll();
                break;
            case R.id.iv_getmap://打开地图
                Intent intent = new Intent();
                intent.putExtra("placeStr", place.getText().toString());
                intent.setClass(this, MapActivity.class);
                startActivity(intent);
                break;
            case R.id.iv_share://分享
                toShare();
                break;
        }
    }

    private void toShare() {
        //生成活动二维码
        Bitmap bitmap = CodeUtils.createImage(activityId+"",400,400, BitmapFactory.decodeResource(this.getResources(),R.drawable.sport));
        //获取时间戳
        long time = System.currentTimeMillis();
        String uri = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){//判断是否获取SD卡权限
            String dir = getExternalFilesDir(null).getAbsolutePath()+"/CoolImg";
            File dirFile = new File(dir);//目录转化为文件夹
            if (!dirFile.exists()){//文件夹不存在则新建
                dirFile.mkdirs();
            }
            //新建图片，以时间命名
            file = new File(dir,time+".jpg");
            //保存图片
            try {
                FileOutputStream outputStream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG,80,outputStream);
                outputStream.flush();
                outputStream.close();
                //版本大于android7.0临时访问文件
                FileProvider.getUriForFile(DetailActivity.this.getApplicationContext(),
                        "net.onest.funactivity.fileprovider",file);
                //把文件插入系统图库
                uri = MediaStore.Images.Media.insertImage(getContentResolver(),file.getAbsolutePath(),time+".jpg",null);
                //通知图库更新
                DetailActivity.this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://"+dir)));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //分享图片
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            // 可以对发起分享的 Intent 添加临时访问授权
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Intent.EXTRA_STREAM,Uri.parse(uri));
            intent.setType("image/jpeg");
            intent.putExtra(Intent.EXTRA_SUBJECT, "111");
            intent.putExtra(Intent.EXTRA_TEXT, "快来看看这个活动吧~");
            startActivity(Intent.createChooser(intent,"快来看看这个活动吧~"));
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