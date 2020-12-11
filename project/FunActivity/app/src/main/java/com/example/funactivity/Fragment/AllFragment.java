package com.example.funactivity.Fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
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
import com.example.funactivity.util.LocationUtil;
import com.example.funactivity.util.WeatherUtil;
import com.lljjcoder.Interface.OnCityItemClickListener;
import com.lljjcoder.bean.CityBean;
import com.lljjcoder.bean.DistrictBean;
import com.lljjcoder.bean.ProvinceBean;
import com.lljjcoder.style.cityjd.JDCityConfig;
import com.lljjcoder.style.cityjd.JDCityPicker;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import org.greenrobot.eventbus.EventBus;
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
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AllFragment extends Fragment {
    private View view;
    private TextView all;//所有活动标题
    private ImageView weather;//天气
    private TextView city;//定位
    private RecyclerView recyclerView;
    private RecylerAdapter adapter;
    private List<Activity> activities = new ArrayList<>();
    private SmartRefreshLayout srl;//刷新
    private int pageNum = 1;//当前页码
    private int pageSize = 2;//每页数据条数
    private Boolean status = true;
    private OkHttpClient client;
    private User user;//用户信息

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    String str = (String) msg.obj;
                    String[] all = str.split("~");
                    int wea = new WeatherUtil().getImg(all[0],all[1]);
                    weather.setImageResource(wea);
                    Log.e("天气",str);
                    break;
                case 2:
                    //刷新adapter
                    adapter.notifyDataSetChanged();
                    //结束上拉动画
                    srl.finishLoadMore();
                    break;
            }
        }
    };
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null){
            view = inflater.inflate(R.layout.fragment_all,
                    container,
                    false);
        }
        Main2Activity activity = (Main2Activity) getActivity();
        user = activity.getUser();
        initView();
        //动态获取权限
        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,},1);
        return view;
    }

    private void initView() {
        all = view.findViewById(R.id.tv_all);
        weather = view.findViewById(R.id.iv_weather);
        city = view.findViewById(R.id.tv_city);
        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "FZGongYHJW.TTF");
        all.setTypeface(typeface);
        city.setTypeface(typeface);
        client = new OkHttpClient();
        recyclerView = view.findViewById(R.id.recycle_view);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        srl = view.findViewById(R.id.srl);
        srl.setEnableRefresh(false);//禁用下拉刷新
        if (pageNum == 1) {
            requestData(pageNum, pageSize);
        }
        adapter = new RecylerAdapter(getContext(), R.layout.item_layout, activities,user.getId());
        recyclerView.setAdapter(adapter);
        adapter.setItemClickListener((view, position) -> {
            Intent intent = new Intent();
            intent.putExtra("collect",true);//收藏是否可修改
            intent.putExtra("id", user.getUserId() + "");//用户id
            intent.putExtra("activityId", activities.get(position).getActivityId() + "");//活动id
            Log.e("活动id", activities.get(position).getActivityId() + "");
            intent.setClass(view.getContext(), DetailActivity.class);
            startActivity(intent);
        });
        //处理上拉加载更多
        srl.setOnLoadMoreListener(refreshLayout -> requestData(pageNum, pageSize));
        city.setOnClickListener(v ->{
            JDCityPicker cityPicker = new JDCityPicker();
            JDCityConfig jdCityConfig = new JDCityConfig.Builder().build();

            jdCityConfig.setShowType(JDCityConfig.ShowType.PRO_CITY_DIS);
            cityPicker.init(v.getContext());
            cityPicker.setConfig(jdCityConfig);
            cityPicker.setOnCityItemClickListener(new OnCityItemClickListener() {
                @Override
                public void onSelected(ProvinceBean province, CityBean c, DistrictBean district) {
                    city.setText(c.getName()+" "+district.getName());//修改城市
                    Log.e("城市选择结果：\n", province.getName() + "(" + province.getId() + ")\n"
                            + c.getName() + "(" + city.getId() + ")\n"
                            + district.getName() + "(" + district.getId() + ")");
                    sendRequestWithHttpClient(c.getName()+" "+district.getName());

                }

                @Override
                public void onCancel() {
                }
            });
            cityPicker.showCityPicker();
        });
    }

    private void requestData(int pageNum, int pageSize) {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("activityKind", 1+"");
        builder.add("pageNum", pageNum + "");
        builder.add("pageSize", pageSize + "");
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
                    Toast.makeText(AllFragment.this.getContext(), "没有更多内容了！", Toast.LENGTH_SHORT).show();
                    srl.finishLoadMoreWithNoMoreData();
                    Looper.loop();
                } else {
                    List<Activity> activitiesList = JSON.parseArray(actjson, Activity.class);
                    activities.addAll(activitiesList);//将新获取的数据加入之前的数据集合
                    AllFragment.this.pageNum++;//设置页码＋1
                   Message message = new Message();
                   message.what = 2;
                   handler.sendMessage(message);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1){
            String citystr = new LocationUtil(getContext()).getLocality();
            city.setText(citystr);
            sendRequestWithHttpClient(citystr);
        }
    }

    private void sendRequestWithHttpClient(final String citystr) {
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        String[] str = citystr.split(" ");
                        String city = str[0];//获取城市
                        HttpURLConnection connection = null;
                        try {
                            URL url = new URL("http://gwgp-h4bqkmub7dg.n.bdcloudapi.com/day");
                            connection = (HttpURLConnection) url.openConnection();
                            connection.setRequestMethod("GET");
                            connection.setRequestProperty("city",city);
                            connection.setRequestProperty("X-Bce-Signature","AppCode/90ab1527c33b4c738449293fc5034f58");
                            connection.setConnectTimeout(5000);
                            connection.setReadTimeout(5000);
                            InputStream in = connection.getInputStream();
                            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                            StringBuilder response = new StringBuilder();
                            String line;
                            while ((line = reader.readLine())!=null){
                                response.append(line);
                            }
                            JSONObject jsonObject = new JSONObject(response.toString());
                            String updateTime = jsonObject.optString("update_time");
                            String weather = jsonObject.optString("wea");
                            Message message = new Message();
                            message.what = 1;
                            message.obj = updateTime+"~"+weather;
                            Log.e("字段",updateTime+"~"+weather);
                            handler.sendMessage(message);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
        ).start();
    }
}
