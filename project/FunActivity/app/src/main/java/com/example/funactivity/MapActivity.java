package com.example.funactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapActivity extends AppCompatActivity {
    private ImageView back;
    private MapView mapView;
    private static BaiduMap baiduMap;
    private LocationClient client;//定位客户端
    private String placeStr;//活动地点
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Intent intent = getIntent();
        placeStr = intent.getStringExtra("placeStr");
        initView();

    }

    private void initView() {
        back = findViewById(R.id.iv_back);
        mapView = findViewById(R.id.mv_view);
        back.setOnClickListener(v -> finish());
        //隐藏百度的logo
        View child = mapView.getChildAt(1);
        if (child != null && (child instanceof ImageView)){
            child.setVisibility(View.GONE);
        }
        //获取百度地图控制器类对象
        baiduMap = mapView.getMap();
        //设置显示为普通地图
        baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        //设置默认比例尺为500米
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.zoomTo(14);
        baiduMap.setMapStatus(mapStatusUpdate);
        //用户定位
        client = new LocationClient(getApplicationContext());
        //动态申请权限
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},100);
        getGeoPointBystr(this,placeStr);
    }
    public static Address getGeoPointBystr(Context context, String str) {
        Address address_temp = null;
        if (str != null) {
            Geocoder gc = new Geocoder(context, Locale.CHINA);
            List<Address> addressList = null;
            try {
                addressList = gc.getFromLocationName(str, 1);
                if (!addressList.isEmpty()) {
                    address_temp = addressList.get(0);
                    double Latitude = address_temp.getLatitude();
                    double Longitude = address_temp.getLongitude();
                    Log.d("zxc003",str+" Latitude = "+Latitude+" Longitude = "+Longitude);
                    BitmapDescriptor descriptor = BitmapDescriptorFactory.fromResource(R.drawable.map1);
                    //定义坐标点
                    LatLng latLng = new LatLng(Latitude,Longitude);
                    //创建OverlayOption子类对象
                    MarkerOptions options = new MarkerOptions()
                            .position(latLng)
                            .icon(descriptor)
                            .draggable(true);
                    //将覆盖物显示到地图界面
                    baiduMap.addOverlay(options);

                }else {
                    Toast.makeText(context,"定位失败",Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return address_temp;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100&& grantResults[0] == 0){
            //创建locationClientOption对象
            LocationClientOption option = new LocationClientOption();
            //打开jps
            option.setOpenGps(true);
            //设置坐标类型
            option.setCoorType("bd0911");
            //设置定位模式
            option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);
            //将定位参数应用到客户端
            client.setLocOption(option);
            client.registerLocationListener(new BDLocationListener() {
                @Override
                public void onReceiveLocation(BDLocation bdLocation) {
                    //获取定位成功的经纬度
                    double latitude = bdLocation.getLatitude();
                    double longitude = bdLocation.getLongitude();
                    //移动地图界面显示到当前位置
                    LatLng point = new LatLng(latitude,longitude);
                    MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(point);
                    //移动地图界面
                    baiduMap.animateMapStatus(update);
                    //添加定位图层
                    MyLocationConfiguration configuration = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL,
                            true,
                            BitmapDescriptorFactory.fromResource(R.drawable.map2));
                    //在地图上显示定位图层
                    baiduMap.setMyLocationConfiguration(configuration);
                    baiduMap.setMyLocationEnabled(true);
                    //配置定位数据
                    MyLocationData data = new MyLocationData.Builder()
                            .latitude(latitude)
                            .longitude(longitude)
                            .build();
                    //将定位数据设置到地图
                    baiduMap.setMyLocationData(data);
                }
            } );
            client.start();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //开启图层定位
        baiduMap.setMyLocationEnabled(true);
        //判断定位服务被关闭，启动定位
        if (!client.isStarted()){
            client.start();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        client.stop();
        baiduMap.setMyLocationEnabled(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
}
