package com.example.funactivity.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

//获取位置信息
public class LocationUtil {
    private Context context;
    private String province;//省
    private String locality="石家庄";//市
    private String subLocality = "裕华区";//区

    public LocationUtil(Context context) {
        this.context = context;
    }

    public String getLocality() {
        //获取位置服务
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();//可根据设备情况自动选择provider
        //ACCURACY_HIGH/ACCURACY_LOW精度选择
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        //方位信息
        criteria.setBearingRequired(true);
        //高度
        criteria.setAltitudeRequired(false);
        //是否允许付费
        criteria.setCostAllowed(true);
        //对电量的要求
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        //速度
        criteria.setSpeedRequired(false);
        //获取最佳服务
        String provider = locationManager.getBestProvider(criteria, true);
        //权限检查
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        //获取到了位置
        assert provider != null;
        Location location = locationManager.getLastKnownLocation(provider);
        //开启地理位置监听定位类型、毫秒、米、监听时间
        locationManager.requestLocationUpdates(provider, 3000, 1, new listener());
        //将经纬度转为地址
        assert location != null;
        Log.e("经纬度", location.getLatitude() + "  " + location.getLongitude());
        getAddress(location.getLatitude(), location.getLongitude());
        return locality + " " + subLocality;
    }

    public class listener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            //位置变化，获取最新的位置
            getAddress(location.getLatitude(), location.getLongitude());
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    }

    //通过经纬度获取具体位置信息
    private void getAddress(double latitude, double longitude) {
        //Geocoder通过经纬度获取具体信息
        Geocoder gc = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> locationList = gc.getFromLocation(latitude, longitude, 1);
            if (locationList != null) {
                Address address = locationList.get(0);
                province = address.getAdminArea();
                Log.e("province",province);
                locality = address.getLocality();//市
                subLocality = address.getSubLocality();//区
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
