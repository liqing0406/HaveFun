package com.example.funactivity.Fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.funactivity.DetailActivity;
import com.example.funactivity.Main2Activity;
import com.example.funactivity.R;
import com.example.funactivity.adapter.RecylerAdapter;
import com.example.funactivity.entity.User.User;
import com.example.funactivity.entity.activity.Activity;
import com.example.funactivity.util.Constant;
import com.example.funactivity.util.WeatherUtil;
import com.lljjcoder.Interface.OnCityItemClickListener;
import com.lljjcoder.bean.CityBean;
import com.lljjcoder.bean.DistrictBean;
import com.lljjcoder.bean.ProvinceBean;
import com.lljjcoder.style.cityjd.JDCityConfig;
import com.lljjcoder.style.cityjd.JDCityPicker;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AllFragment extends Fragment implements View.OnClickListener {
    private View view;
    private ImageView weather;//天气
    private TextView city;//定位
    private RecylerAdapter adapter;
    private List<Activity> activities = new ArrayList<>();
    ;
    private SmartRefreshLayout srl;//刷新
    private int pageNum = 1;//当前页码
    private int pageSize = 4;//每页数据条数
    private Boolean status = true;
    private OkHttpClient client;
    private User user;//用户信息
    //下拉框
    private View popView;
    private RelativeLayout rl_type;
    private RelativeLayout rl_price;
    private RelativeLayout rl_time;
    private ImageView image1;
    private ImageView image2;
    private ImageView image3;
    private PopupWindow popupWindow;
    //筛选的内容
    private String selectedType;
    private int selectedTime;
    private int selectedPrice;
    private String selectedCity;
    private String selectedCountry;
    private TextView tv_type;
    private TextView tv_time;
    private TextView tv_price;
    private Map<String, Integer> times = new HashMap<>();
    private Map<String, Integer> price = new HashMap<>();
    private String cityStr;//市+区


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    String str = (String) msg.obj;
                    String[] all = str.split("~");
                    int wea = new WeatherUtil().getImg(all[0], all[1]);
                    weather.setImageResource(wea);
                    break;
                case 2://表示搜索条件有数据
                    activities.clear();
                    List<Activity> activity = (List<Activity>) msg.obj;
                    activities.addAll(activity);
                    Log.e("wxy", "表示搜索条件有数据"+activities.size());
                    //刷新adapter
                    adapter.notifyDataSetChanged();
                    break;
                case 3://表示刷新有数据
                    List<Activity> activity1 = (List<Activity>) msg.obj;
                    activities.addAll(activity1);
                    Log.e("wxy", "表示刷新有数据"+activities.size());
                    //刷新adapter
                    adapter.notifyDataSetChanged();
                    //结束上拉动画
                    srl.finishLoadMore();
                    break;
                case 4://搜索条件没有数据
                    activities.clear();
                    Log.e("wxy", "搜索条件没有数据");
                    //刷新adapter
                    adapter.notifyDataSetChanged();
                    break;
                case 5:
                    adapter.notifyDataSetChanged();
                    srl.finishLoadMore();
                    Toast.makeText(AllFragment.this.getContext(), "没有更多内容了！", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_all,
                    container,
                    false);
        }
        Main2Activity activity = (Main2Activity) getActivity();
        assert activity != null;
        user = activity.getUser();
        cityStr = activity.getCityStr();
        initMap();
        initViews();
        //初始化下拉列表的点击事件
        initListOnClickListener();
        //获取各参数的值
        return view;
    }

    public void initListOnClickListener() {
        rl_time.setOnClickListener(this);
        rl_price.setOnClickListener(this);
        rl_type.setOnClickListener(this);
    }

    private void initMap() {
        times.put("近三天", 3);
        times.put("近一个月", 30);
        times.put("近一周", 7);
        times.put("任意", -1);
        price.put("50以内", 50);
        price.put("100以内", 100);
        price.put("免费", 0);
        price.put("任意", -1);

    }

    private void initViews() {
        //下拉框
        rl_type = view.findViewById(R.id.rl_type);
        rl_price = view.findViewById(R.id.rl_price);
        rl_time = view.findViewById(R.id.rl_time);
        image1 = view.findViewById(R.id.iv_img1);
        image2 = view.findViewById(R.id.iv_img2);
        image3 = view.findViewById(R.id.iv_img3);
        tv_price = view.findViewById(R.id.price);
        tv_time = view.findViewById(R.id.tv_time);
        tv_type = view.findViewById(R.id.type);
        //所有活动标题
        TextView all = view.findViewById(R.id.tv_all);
        weather = view.findViewById(R.id.iv_weather);
        city = view.findViewById(R.id.tv_city);
        city.setText(cityStr);
        String[] str = cityStr.split(" ");
        sendRequestWithHttpClient(cityStr);
        selectedType = "empty";
        selectedTime = -1;
        selectedPrice = -1;
        selectedCity = str[0];
        selectedCountry = str[1];

        Typeface typeface = Typeface.createFromAsset(Objects.requireNonNull(getContext()).getAssets(), "FZGongYHJW.TTF");
        all.setTypeface(typeface);
        city.setTypeface(typeface);
        client = new OkHttpClient();
        getRecyclerView();
        city.setOnClickListener(v -> {
            JDCityPicker cityPicker = new JDCityPicker();
            JDCityConfig jdCityConfig = new JDCityConfig.Builder().build();
            jdCityConfig.setShowType(JDCityConfig.ShowType.PRO_CITY_DIS);
            cityPicker.init(v.getContext());
            cityPicker.setConfig(jdCityConfig);
            cityPicker.setOnCityItemClickListener(new OnCityItemClickListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onSelected(ProvinceBean province, CityBean c, DistrictBean district) {
                    city.setText(c.getName() + " " + district.getName());//修改城市
                    sendRequestWithHttpClient(c.getName() + " " + district.getName());
                    selectedCity = c.getName();
                    selectedCountry = district.getName();
                    pageNum = 1;
                    requestData(selectedType, 0, selectedPrice, selectedTime, selectedCity, selectedCountry, pageNum, pageSize, 0);
                }

                @Override
                public void onCancel() {
                }
            });
            cityPicker.showCityPicker();
        });
    }

    public void getRecyclerView() {
        RecyclerView recyclerView = view.findViewById(R.id.recycle_view);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        srl = view.findViewById(R.id.srl);
        srl.setEnableRefresh(false);//禁用下拉刷新
        adapter = new RecylerAdapter(getContext(), R.layout.item_layout, activities, user.getId());
        recyclerView.setAdapter(adapter);
        if (pageNum == 1) {
            requestData(selectedType, 0, selectedPrice, selectedTime, selectedCity, selectedCountry, pageNum, pageSize, 0);
        }

        adapter.setItemClickListener((view, position) -> {
            Intent intent = new Intent();
            intent.putExtra("collect", true);//收藏是否可修改
            intent.putExtra("id", user.getId() + "");//用户id
            intent.putExtra("activityId", activities.get(position).getActivityId() + "");//活动id
            intent.setClass(view.getContext(), DetailActivity.class);
            startActivity(intent);
        });
        //处理下拉加载更多
        srl.setOnLoadMoreListener(refreshLayout -> requestData(selectedType, 0, selectedPrice, selectedTime, selectedCity, selectedCountry, pageNum, pageSize, 1));
    }

    //i表示是否刷新，1表示刷新
    private void requestData(String typeName, int lowCost, int highCost, int days, String city, String county, int pageNum, int pageSize, int i) {
        if (typeName.equals("任意")) {
            typeName = "empty";
        }

        if(i==0){
            pageNum=1;
            this.pageNum=1;
        }
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("typeName", typeName);
        builder.add("lowCost", lowCost + "");
        builder.add("highCost", highCost + "");
        builder.add("howManyDays", days + "");
        builder.add("city", city);
        builder.add("county", county);
        builder.add("pageNum", pageNum + "");
        builder.add("pageSize", pageSize + "");
        FormBody body = builder.build();
        Request request = new Request.Builder()
                .post(body)
                .url(Constant.BASE_URL + "activity/screenActivities")
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
                assert response.body() != null;
                String actjson = response.body().string();
                if ("empty".equals(actjson) && i == 1) {//表述刷新没有更多数据
                    Message message = new Message();
                    message.what=5;
                    handler.sendMessage(message);
                }
                if (!"empty".equals(actjson) && i == 1) {//表示刷新有数据
                    List<Activity> activitiesList = JSON.parseArray(actjson, Activity.class);
                    AllFragment.this.pageNum++;//设置页码＋1
                    Log.e("加一","dsds");
                    Message message = new Message();
                    message.what = 3;
                    message.obj = activitiesList;
                    handler.sendMessage(message);
                }
                if (!"empty".equals(actjson) && i == 0) {//表示搜索条件有数据
                    List<Activity> activitiesList = JSON.parseArray(actjson, Activity.class);
                    AllFragment.this.pageNum++;//设置页码＋1
                    Message message = new Message();
                    message.what = 2;
                    message.obj = activitiesList;
                    handler.sendMessage(message);
                }
                if ("empty".equals(actjson) && i == 0) {//表示搜索条件没有数据
                    Message message = new Message();
                    message.what = 4;
                    handler.sendMessage(message);
                    Looper.prepare();
                    Toast.makeText(AllFragment.this.getContext(), "没有活动！", Toast.LENGTH_SHORT).show();
                    Looper.loop();

                }
            }
        });
    }

    private void sendRequestWithHttpClient(final String citystr) {
        new Thread(
                () -> {
                    String[] str = citystr.split(" ");
                    String city = str[0];//获取城市
                    HttpURLConnection connection = null;
                    try {
                        URL url = new URL("http://gwgp-h4bqkmub7dg.n.bdcloudapi.com/day");
                        connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");
                        connection.setRequestProperty("city", city);
                        connection.setRequestProperty("X-Bce-Signature", "AppCode/90ab1527c33b4c738449293fc5034f58");
                        connection.setConnectTimeout(5000);
                        connection.setReadTimeout(5000);
                        InputStream in = connection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                        JSONObject jsonObject = new JSONObject(response.toString());
                        String updateTime = jsonObject.optString("update_time");
                        String weather = jsonObject.optString("wea");
                        Message message = new Message();
                        message.what = 1;
                        message.obj = updateTime + "~" + weather;
                        handler.sendMessage(message);
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }

                }
        ).start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_time:
                image3.setBackgroundResource(R.drawable.up);
                image1.setBackgroundResource(R.drawable.down);
                image2.setBackgroundResource(R.drawable.down);
                String[] left = getResources().getStringArray(R.array.time);
                View view = initView("time", left, 1);
                popupWindow(view, rl_time);
                break;
            case R.id.rl_type:
                image1.setBackgroundResource(R.drawable.up);
                image3.setBackgroundResource(R.drawable.down);
                image2.setBackgroundResource(R.drawable.down);
                String[] left1 = getResources().getStringArray(R.array.types);
                View view1 = initView("type", left1, 2);
                popupWindow(view1, rl_type);
                break;
            case R.id.rl_price:
                image2.setBackgroundResource(R.drawable.up);
                image1.setBackgroundResource(R.drawable.down);
                image3.setBackgroundResource(R.drawable.down);
                String[] left2 = getResources().getStringArray(R.array.price);
                View view2 = initView("price", left2, 3);
                popupWindow(view2, rl_price);
                break;
        }
    }

    //创建popupWindow
    public void popupWindow(View v, View parent) {
        //创建以及初始化
        popupWindow = new PopupWindow(getContext());
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setContentView(v);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setFocusable(false);
        popupWindow.setOutsideTouchable(true);//内容外的区域是否响应
        popupWindow.showAsDropDown(parent);
        popupWindow.setOnDismissListener(new PopupOnDismissListener());
    }

    static class PopupOnDismissListener implements PopupWindow.OnDismissListener {

        @Override
        public void onDismiss() {
        }
    }

    @SuppressLint("InflateParams")
    public View initView(String choice, String[] data, int child) {
        //初始化布局
        LayoutInflater layoutInflater = Objects.requireNonNull(getActivity()).getLayoutInflater();
        popView = layoutInflater.inflate(R.layout.pop_view, null);
        final ListView left = popView.findViewById(R.id.lv_left);
        //左边的分类，包括时间：近一周、近三天、近一个月
        //类型 ：大类
        //费用：免费、50以内、100以内
        ArrayAdapter<String> array = new ArrayAdapter<>(Objects.requireNonNull(getContext()),
                android.R.layout.simple_list_item_1, data);
        left.setAdapter(array);
        left.setOnItemClickListener((parent, view, position, id) -> {
            for (int i = 0; i < parent.getCount(); i++) {
                View v = parent.getChildAt(i);
                if (position == i) {
                    v.setBackgroundResource(R.color.white);
                } else {
                    v.setBackgroundResource(R.drawable.left_list_background);
                }
            }
            String parentName = (String) parent.getItemAtPosition(position);
            //如果父标签不为空 初始化子标签
            if (child == 2) {
                //设置右边的ListView
                initRightView(parentName);
            } else if (child == 1) {
                selectedTime = times.get(parentName);
                tv_time.setText(parentName);
                requestData(selectedType, 0, selectedPrice, selectedTime, selectedCity, selectedCountry, pageNum, pageSize, 0);
                popupWindow.dismiss();
                image3.setBackgroundResource(R.drawable.down);
            } else if (child == 3) {
                selectedPrice = price.get(parentName);
                tv_price.setText(parentName);
                requestData(selectedType, 0, selectedPrice, selectedTime, selectedCity, selectedCountry, pageNum, pageSize, 0);
                popupWindow.dismiss();
                image2.setBackgroundResource(R.drawable.down);
            }
        });
        return popView;
    }

    public void initRightView(String parent) {
        ListView right = popView.findViewById(R.id.lv_right);
        String[] data = null;
        switch (parent) {
            case "骑行运动":
                data = getResources().getStringArray(R.array.ride);
                break;
            case "山地运动":
                data = getResources().getStringArray(R.array.mountain);
                break;
            case "水面运动":
                data = getResources().getStringArray(R.array.swim);
                break;
            case "跑步运动":
                data = getResources().getStringArray(R.array.run);
                break;
            case "球类运动":
                data = getResources().getStringArray(R.array.ball);
                break;
            case "任意":
                data = getResources().getStringArray(R.array.other);
                break;
        }
        assert data != null;
        ArrayAdapter<String> array = new ArrayAdapter<>(Objects.requireNonNull(getContext()),
                android.R.layout.simple_list_item_1, data);
        right.setAdapter(array);
        right.setOnItemClickListener((parent1, view, position, id) -> {
            selectedType = parent1.getItemAtPosition(position).toString();
            tv_type.setText(selectedType);
            if (selectedType.equals("任意")) {
                requestData("empty", 0, selectedPrice, selectedTime, selectedCity, selectedCountry, pageNum, pageSize, 0);
            } else {
                requestData(selectedType, 0, selectedPrice, selectedTime, selectedCity, selectedCountry, pageNum, pageSize, 0);
            }
            popupWindow.dismiss();
            image1.setBackgroundResource(R.drawable.down);
        });
    }
}
