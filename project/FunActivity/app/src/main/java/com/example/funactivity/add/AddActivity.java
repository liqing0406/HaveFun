package com.example.funactivity.add;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.example.funactivity.R;
import com.example.funactivity.entity.User.User;
import com.example.funactivity.entity.activity.Activity;
import com.example.funactivity.entity.activity.ActivityDetail;
import com.example.funactivity.entity.activity.ActivityKind;
import com.example.funactivity.entity.activity.ActivityLocation;
import com.example.funactivity.entity.activity.TypeOfKind;
import com.example.funactivity.util.Constant;
import com.example.funactivity.util.FinalNumInter;
import com.example.funactivity.util.LocationUtil;
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;
import com.lljjcoder.Interface.OnCityItemClickListener;
import com.lljjcoder.bean.CityBean;
import com.lljjcoder.bean.DistrictBean;
import com.lljjcoder.bean.ProvinceBean;
import com.lljjcoder.citywheel.CityConfig;
import com.lljjcoder.style.citylist.Toast.ToastUtils;
import com.lljjcoder.style.citypickerview.CityPickerView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class AddActivity extends AppCompatActivity implements FinalNumInter, View.OnClickListener {
    @BindView(R.id.goBack)
    ImageView back;//返回按钮
    @BindView(R.id.tv_title)
    TextView title;
    @BindView(R.id.theme)
    EditText theme;//主题
    @BindView(R.id.sp_type)
    Spinner type;//活动类型
    @BindView(R.id.tv_time)
    TextView time;//活动时间
    @BindView(R.id.btn_gettime)
    Button getTime;//获取活动时间
    @BindView(R.id.price)
    EditText etPrice;//价格
    @BindView(R.id.et_max)//人数
            EditText etmaxNum;
    @BindView(R.id.btn_city)
    Button btn_city;//省市县
    @BindView(R.id.iscity)
    TextView iscity;//省市县获取成功显示
    @BindView(R.id.detail)
    EditText et_detailAddress;//详细地址
    @BindView(R.id.introduce)
    EditText et_introduce;//目的地介绍
    @BindView(R.id.remark)
    EditText et_remark;//备注信息
    @BindView(R.id.bt_submit)
    Button submit;//提交
    @BindView(R.id.iv_add)
    ImageView imageView;
    @BindView(R.id.contact)
    EditText et_contact;

    private String spinnerValue;
    private ActivityDetail activityDetail;
    private User user;
    private String lastType;
    private String imagePath;
    private OkHttpClient client;
    //地址
    private String provinces;//省
    private String citys;//市
    private String countys;//县/区
    //申明对象,城市选择器
    CityPickerView mPicker = new CityPickerView();
    private static final String TAG = "Sample";
    private static final String TAG_DATETIME_FRAGMENT = "TAG_DATETIME_FRAGMENT";
    private static final String STATE_TEXTVIEW = "STATE_TEXTVIEW";
    private SwitchDateTimeDialogFragment dateTimeFragment;
    private Calendar calendar;
    private Date datestr;//时间
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == 1) {
                String str = (String) msg.obj;
                if (str.equals("true")) {
                    new SweetAlertDialog(AddActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("发布成功")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    finish();
                                }
                            })
                            .show();
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        //预先加载仿iOS滚轮实现的全部数据
        mPicker.init(this);
        //黄油刀
        ButterKnife.bind(this);
        //获取上个界面传递的信息
        //获取上个界面的类型
        Intent intent = getIntent();
        lastType = intent.getStringExtra("type");
        user = (User) intent.getSerializableExtra("user");
        //设置活动类型的下拉框
        setSpinner();
        //初始化加载图片
        init();
        //时间选择器
        getTime();
        //点击事件处理
        submit.setOnClickListener(AddActivity.this);//提交
        //返回
        back.setOnClickListener(AddActivity.this);
        imageView.setOnClickListener(this);
        btn_city.setOnClickListener(this);

    }

    private void getTime() {
        dateTimeFragment = (SwitchDateTimeDialogFragment) getSupportFragmentManager().findFragmentByTag(TAG_DATETIME_FRAGMENT);
        if (dateTimeFragment == null) {
            dateTimeFragment = SwitchDateTimeDialogFragment.newInstance(
                    getString(R.string.label_datetime_dialog),
                    getString(android.R.string.ok),
                    getString(android.R.string.cancel)
            );
        }

        dateTimeFragment.setTimeZone(TimeZone.getDefault());

        final SimpleDateFormat myDateFormat = new SimpleDateFormat("yyyy年MM月dd日HH:mm", Locale.getDefault());
        dateTimeFragment.set24HoursMode(true);
        dateTimeFragment.setHighlightAMPMSelection(false);
        calendar = Calendar.getInstance();
        //设置当前时间为最小时间
        dateTimeFragment.setMinimumDateTime(new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE)).getTime());
        //设置最大时间
        dateTimeFragment.setMaximumDateTime(new GregorianCalendar(2025, Calendar.DECEMBER, 31).getTime());

        try {
            dateTimeFragment.setSimpleDateMonthAndDayFormat(new SimpleDateFormat("MMMM dd", Locale.getDefault()));
        } catch (SwitchDateTimeDialogFragment.SimpleDateMonthAndDayFormatException e) {
            Log.e(TAG, e.getMessage());
        }

        //点击确定按钮
        dateTimeFragment.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonWithNeutralClickListener() {
            @Override
            public void onPositiveButtonClick(Date date) {
                time.setText(myDateFormat.format(date));
                datestr = date;
            }

            @Override
            public void onNegativeButtonClick(Date date) {//点击取消
                // Do nothing
            }

            @Override
            public void onNeutralButtonClick(Date date) {
                time.setText("");
            }
        });

        getTime.setOnClickListener(view -> {
            dateTimeFragment.startAtCalendarView();
            dateTimeFragment.setDefaultDateTime(new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)).getTime());
            dateTimeFragment.show(getSupportFragmentManager(), TAG_DATETIME_FRAGMENT);
        });
    }


    public void init() {
        //发布活动设置字体
        Typeface typeface = Typeface.createFromAsset(this.getAssets(), "FZGongYHJW.TTF");
        title.setTypeface(typeface);

    }

    public void setSpinner() {
        List<String> types = new ArrayList<>();
        switch (lastType) {
            case "山地运动"://山地运动
                //登山、速降、攀岩、探洞
                types.add("登山");
                types.add("速降");
                types.add("攀岩");
                types.add("探洞");
                types.add("其他");
                setActivitySpinnerStyle(types);
                break;
            case "骑行运动"://骑行运动
                //单车、摩托、
                types.add("单车");
                types.add("摩托");
                types.add("其他");
                setActivitySpinnerStyle(types);
                break;
            case "跑步运动"://跑步运动
                //比赛跑、健身跑、兴趣跑、训练跑
                types.add("比赛跑");
                types.add("健身跑");
                types.add("兴趣跑");
                types.add("训练跑");
                types.add("其他");
                setActivitySpinnerStyle(types);
                break;
            case "球类运动"://球类运动
                //足球、篮球、排球、羽毛球、网球、其它
                types.add("足球");
                types.add("篮球");
                types.add("排球");
                types.add("网球");
                types.add("羽毛球");
                types.add("乒乓球");
                types.add("棒球");
                types.add("其他");
                setActivitySpinnerStyle(types);
                break;
            case "水面运动"://水面运动
                //游泳、冲浪、摩托艇、漂流、其他
                types.add("游泳");
                types.add("冲浪");
                types.add("摩托艇");
                types.add("漂流");
                types.add("其他");
                setActivitySpinnerStyle(types);
                break;
            case "其他"://其他
                types.add("其他");
                break;
        }
    }

    //活动类型的Spinner
    public void setActivitySpinnerStyle(List<String> types) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, types);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        type.setAdapter(adapter);

        type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerValue = types.get(position);
                Log.e("spinner", spinnerValue);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        //根据按钮id来区分是哪个事件
        switch (v.getId()) {
            case R.id.goBack://返回上一级
                finish();
                break;
            case R.id.bt_submit://提交
                submit();
                break;
            case R.id.iv_add:
                addPic();
                break;
            case R.id.btn_city:
                getCity();
                break;
        }
    }

    private void getCity() {
        String cityStr = new LocationUtil(AddActivity.this).getLocality();
        //添加默认的配置
        CityConfig cityConfig = new CityConfig.Builder()
                .province("河北省")
                .city("石家庄市")
                .district("裕华区")
                .build();
        mPicker.setConfig(cityConfig);


        //监听选择点击事件及返回结果
        mPicker.setOnCityItemClickListener(new OnCityItemClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSelected(ProvinceBean province, CityBean city, DistrictBean district) {
                Log.e("城市选择结果：\n", province.getName() + "(" + province.getId() + ")\n"
                        + city.getName() + "(" + city.getId() + ")\n"
                        + district.getName() + "(" + district.getId() + ")");
                //省份province
                provinces = province.getName();
                Log.e("provinces", "123" + provinces);
                //城市city
                citys = city.getName();
                //地区district
                countys = district.getName();
                btn_city.setText("点击修改");
                iscity.setText(provinces + citys + countys);
            }

            @Override
            public void onCancel() {
                ToastUtils.showLongToast(AddActivity.this, "已取消");
            }
        });

        //显示
        mPicker.showCityPicker();
    }

    public void addPic() {
        ActivityCompat.requestPermissions(AddActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                100);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            //打开图库
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 200);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //从图库界面返回以后，获取到图片的路径
        if (requestCode == 200 && resultCode == RESULT_OK) {
            ContentResolver contentResolver = getContentResolver();
            assert data != null;
            Uri uri = data.getData();
            assert uri != null;
            @SuppressLint("Recycle") Cursor cursor = contentResolver.query(uri, null,
                    null, null, null);
            assert cursor != null;
            if (cursor.moveToFirst()) {
                imagePath = cursor.getString(cursor.getColumnIndex("_data"));
                Glide.with(this)
                        .load(imagePath)
                        .into(imageView);
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void submit() {
        //将所有数据提交
        String activityTheme = theme.getText().toString();//主题
        String contact = et_contact.getText().toString();
        //价格
        String price = etPrice.getText().toString();
        //人数
        String maxNum = etmaxNum.getText().toString();

        //详细地址
        String detail = et_detailAddress.getText().toString();
        //介绍
        String introduce = et_introduce.getText().toString();
        //备注
        String remark = et_remark.getText().toString();
        if (activityTheme.isEmpty() || price.isEmpty() || maxNum.isEmpty() || provinces.isEmpty() || citys.isEmpty() || countys.isEmpty() ||
                detail.isEmpty() || introduce.isEmpty() || remark.isEmpty()) {
            Toast toast = Toast.makeText(this, "请将内容填写完整", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.getView().setBackground(getResources().getDrawable(R.drawable.btn_background));
            toast.show();
        } else {
//        将所有数据整合
            activityDetail = new ActivityDetail();
            Activity activity = new Activity();
            activity.setActivityTile(activityTheme);//类型
            activity.setActivityTime(datestr);//集合日期
            TypeOfKind typeOfKind = new TypeOfKind();
            if(spinnerValue == null){
                spinnerValue = "其他";
            }
            typeOfKind.setTypeName(spinnerValue);
            ActivityKind activityKind = new ActivityKind();
            activityKind.setKindName(lastType);
            typeOfKind.setActivityKind(activityKind);
            activity.setTypeOfKind(typeOfKind);
            activity.setActivityContact(contact);
            activity.setActivityCost(price);//花费
            activity.setPersonLimit(Integer.parseInt(maxNum));//人数
            //活动地址
            ActivityLocation location = new ActivityLocation();
            location.setProvince(provinces);
            location.setCity(citys);
            location.setCounty(countys);
            location.setDetailedAddress(detail);
            activity.setActivityLocation(location);
            activity.setUser(user);
            //目的地介绍
            activityDetail.setActivityInfo(introduce);
            //活动信息
            activityDetail.setOtherInfo(remark);
            activityDetail.setActivity(activity);
            String json = JSON.toJSONString(activityDetail);
            Log.e("json", json);
//            图片
            File file = new File(imagePath);
            MultipartBody.Builder builder = new MultipartBody.Builder();
            //3.构建MultipartBody
            builder.setType(MultipartBody.FORM).addFormDataPart("file", "image.png", RequestBody.create(MediaType.parse("image/png"), file));
            builder.addFormDataPart("activityDetailJson", json);
            MultipartBody body = builder.build();
            //4.构建请求
            final Request request = new Request.Builder()
                    .url(Constant.BASE_URL + "activity/addActivity")
                    .post(body)
                    .build();
            client = new OkHttpClient();
            //5.发送请求
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.i("phz", "phz");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    assert response.body() != null;
                    String str = response.body().string();
                    Message message = new Message();
                    message.what = 1;
                    message.obj = str;
                    handler.sendMessage(message);
                }
            });
        }
    }
}
