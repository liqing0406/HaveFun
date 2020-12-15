package com.example.funactivity.Fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.example.funactivity.DetailActivity;
import com.example.funactivity.Main2Activity;
import com.example.funactivity.R;
import com.example.funactivity.adapter.ImageAdapter;
import com.example.funactivity.adapter.RecylerAdapter;
import com.example.funactivity.entity.DataBean;
import com.example.funactivity.entity.User.User;
import com.example.funactivity.entity.User.UserDetail;
import com.example.funactivity.entity.activity.Activity;
import com.example.funactivity.util.Constant;
import com.example.funactivity.util.LocationUtil;
import com.example.funactivity.view.CaptureActivity;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.youth.banner.Banner;
import com.youth.banner.indicator.CircleIndicator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainFragment extends Fragment {
    private User user;
    private View view;
    private ImageView img;
    private TextView username;
    private TextView activitynum;
    private TextView city;
    private Banner banner;
    private RadioGroup all;
    private RadioButton hot;
    private RadioButton recent;
    private SmartRefreshLayout srl;
    private RecyclerView recyclerView;
    private RecylerAdapter adapter;
    private OkHttpClient client;
    private ImageView scan;
    private List<Activity> activities1 = new ArrayList<>();
    private List<Activity> activities2 = new ArrayList<>();
    private int pageNum1 = 1;//热门榜当前页码
    private int pageNum2 = 1;//近期榜当前页码
    private int pageSize = 4;//每页数据条数
    private Boolean statusRecent = true;
    private Boolean statusHot = true;
    final RequestOptions options = new RequestOptions()
            .circleCrop();
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 1:
                    adapter.notifyDataSetChanged();
                    srl.finishLoadMore();
                    break;
            }
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    //根据地址下载头像
                    Glide.with(view)
                            .load(Constant.PIC_PATH + msg.obj)
                            .signature(new ObjectKey(UUID.randomUUID().toString()))
                            .apply(options)
                            .into(img);
                    break;
            }
        }
    };

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //加载内容页面布局
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_main,
                    container,
                    false);
            EventBus.getDefault().register(this);
        }
        Main2Activity activity = (Main2Activity) getActivity();
        user = activity.getUser();
        //动态获取位置的相关权限
        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,},
                100);
        initView();
        activitynum.setText(user.getUserDetail().getNumOfActivityForUser() + "");
        username.setText(user.getUserName());
        Message message = new Message();
        message.what = 1;
        message.obj = user.getHeadPortrait();
        myHandler.sendMessage(message);

        banner.addBannerLifecycleObserver(this)//添加生命周期观察者
                //  .setBannerRound2(50)
                .setBannerGalleryEffect(10, 3) //添加画廊效果
                .setAdapter(new ImageAdapter(DataBean.getTestData3()))//设置adapter
                .setIndicator(new CircleIndicator(getContext()));//设置指示器

        return view;
    }

    private void initView() {
        img = view.findViewById(R.id.iv_img);
        username = view.findViewById(R.id.tv_username);
        activitynum = view.findViewById(R.id.tv_activitynum);
        city = view.findViewById(R.id.tv_city);
        banner = view.findViewById(R.id.banner);
        all = view.findViewById(R.id.rg_all);
        hot = view.findViewById(R.id.rb_hot);
        recent = view.findViewById(R.id.rb_recent);
        srl = view.findViewById(R.id.srl);
        recyclerView = view.findViewById(R.id.recycle_view);
        scan = view.findViewById(R.id.iv_scan);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);

        srl.setEnableRefresh(false);//禁用下拉刷新

        client = new OkHttpClient();
        //单选按钮设置字体
        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "FZGongYHJW.TTF");
        hot.setTypeface(typeface);
        recent.setTypeface(typeface);
        //单选按钮点击事件
        all.setOnCheckedChangeListener(new rgTypeListener());
        //获取默认单选按钮
        int checkid = all.getCheckedRadioButtonId();
        RadioButton checkbtn = (RadioButton) view.findViewById(checkid);
        //设置字体变化
        hot.setTextSize(25);
        hot.setTextColor(ContextCompat.getColor(getContext(), R.color.pink));
        recent.setTextSize(22);
        recent.setTextColor(ContextCompat.getColor(getContext(), R.color.gray));
        if (statusHot) {
            requestData(1, pageNum1, pageSize);
            adapter = new RecylerAdapter(getContext(), R.layout.item_layout, activities1,user.getId());
            recyclerView.setAdapter(adapter);
            adapter.setItemClickListener((view, position) -> {
                Intent intent = new Intent();
                intent.putExtra("collect",true);//收藏是否可修改
                intent.putExtra("id", user.getId() + "");//用户id
                intent.putExtra("activityId", activities1.get(position).getActivityId() + "");//活动id
                intent.setClass(view.getContext(), DetailActivity.class);
                startActivityForResult(intent,1000);
            });
            //处理上拉加载更多
            srl.setOnLoadMoreListener(refreshLayout -> requestData(1, pageNum1, pageSize));
        }
        scan.setOnClickListener(v ->initActivity());//扫描活动二维码
    }

    private void initActivity() {
        Intent intent = new Intent(view.getContext(), CaptureActivity.class);
        intent.putExtra("id",user.getId()+"");
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            //获取返回的位置信息
//            String citystr = new LocationUtil(getContext()).getLocality();
            city.setText("石家庄");
            //city.setText(citystr);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000){
            adapter.notifyDataSetChanged();

        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        adapter.notifyDataSetChanged();
        Glide.with(view)
                .load(Constant.PIC_PATH + user.getHeadPortrait())
                .signature(new ObjectKey(UUID.randomUUID().toString()))
                .apply(options)
                .into(img);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updataUI(User u) {
        user.setUserName(u.getUserName());
        user.setPassword(u.getPassword());
        UserDetail userDetail=new UserDetail();
        userDetail=user.getUserDetail();
        userDetail.setSex(u.getUserDetail().getSex());
        userDetail.setPersonalSignature(u.getUserDetail().getPersonalSignature());
        userDetail.setResidentIdCard(u.getUserDetail().getResidentIdCard());
        user.setUserDetail(userDetail);
        username.setText(user.getUserName());

    }

    //获取活动信息
    private void requestData(final int acttype, final int pageNum, int pageSize) {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("activityKind", acttype + "");
        builder.add("pageNum", pageNum + "");
        builder.add("pageSize", pageSize + "");
        builder.add("city",city.getText().toString());
        FormBody body = builder.build();
        Request request = new Request.Builder()
                .post(body)
                .url(Constant.BASE_URL + "activity/getActivityList")
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
                String actjson = response.body().string();
                if ("".equals(actjson)) {
                    Looper.prepare();
                    Toast.makeText(MainFragment.this.getContext(), "没有更多内容了！", Toast.LENGTH_SHORT).show();

                    if (acttype == 1) {
                        statusHot = false;
                    } else if (acttype == 2) {
                        statusRecent = false;
                    }
                    srl.finishLoadMoreWithNoMoreData();
                    Looper.loop();
                } else {
                    List<Activity> activitiesList = JSON.parseArray(actjson, Activity.class);

                    if (acttype == 1) {
                        activities1.addAll(activitiesList);//将新获取的数据加入之前的数据集合
                        MainFragment.this.pageNum1++;//设置页码＋1
                    }
                    if (acttype == 2) {
                        activities2.addAll(activitiesList);//将新获取的数据加入之前的数据集合
                        MainFragment.this.pageNum2++;//设置页码＋1
                    }
                    Message message = new Message();
                    message.what=1;
                    handler.sendMessage(message);
                }
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private class rgTypeListener implements RadioGroup.OnCheckedChangeListener {
        //处理选项切换
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.rb_hot:
                    //设置字体变化
                    hot.setTextSize(25);
                    hot.setTextColor(ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.pink));
                    recent.setTextSize(22);
                    recent.setTextColor(ContextCompat.getColor(getContext(), R.color.gray));
                    //处理上拉加载更多
                    srl.setOnLoadMoreListener(refreshLayout -> requestData(1, pageNum1, pageSize));
                    adapter = new RecylerAdapter(getContext(), R.layout.item_layout, activities1,user.getId());
                    recyclerView.setAdapter(adapter);
                    adapter.setItemClickListener((view, position) -> {
                        Intent intent = new Intent();
                        intent.putExtra("collect",true);//收藏是否可修改
                        intent.putExtra("id", user.getId() + "");
                        intent.putExtra("activityId", activities1.get(position).getActivityId()+"");//活动id
                        intent.setClass(view.getContext(), DetailActivity.class);
                        startActivity(intent);
                    });
                    break;
                case R.id.rb_recent:
                    hot.setTextSize(22);
                    hot.setTextColor(ContextCompat.getColor(getContext(), R.color.gray));
                    recent.setTextSize(25);
                    recent.setTextColor(ContextCompat.getColor(getContext(), R.color.pink));
                    if (pageNum2 == 1) {
                        requestData(2, pageNum2, pageSize);
                    }
                    //处理上拉加载更多
                    srl.setOnLoadMoreListener(refreshLayout -> requestData(2, pageNum2, pageSize));
                    adapter = new RecylerAdapter(getContext(), R.layout.item_layout, activities2,user.getId());
                    recyclerView.setAdapter(adapter);
                    adapter.setItemClickListener((view, position) -> {
                        Intent intent = new Intent();
                        intent.putExtra("collect",true);//收藏是否可修改
                        intent.putExtra("id", user.getId() + "");
                        intent.putExtra("activityId", activities2.get(position).getActivityId()+"");//活动id
                        intent.setClass(view.getContext(), DetailActivity.class);
                        startActivity(intent);
                    });
                    break;
            }
        }
    }
}