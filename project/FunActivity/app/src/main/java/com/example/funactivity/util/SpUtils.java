package com.example.funactivity.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.funactivity.view.Application;


public class SpUtils {
    public static final String IS_NEW_INVITE = "is_new_invite";
    private static SpUtils sSpUtils = new SpUtils();
    private static SharedPreferences sMSp;

    private SpUtils() {
    }

    public static SpUtils getInstance() {
        if (sMSp == null) {
            sMSp = Application.getInstance().getSharedPreferences("im", Context.MODE_PRIVATE);
        }
        return sSpUtils;
    }

    public void save(String key, Object value) {
        if (value instanceof String) {
            sMSp.edit().putString(key, String.valueOf((value))).apply();
        } else if (value instanceof Boolean) {
            sMSp.edit().putBoolean(key, (Boolean) value).apply();
        } else if (value instanceof Integer) {
            sMSp.edit().putInt(key, (Integer) value).apply();
        }
    }

    //获取数据的方法
    public String getString(String key, String defValue) {
        return sMSp.getString(key, defValue);
    }

    //获取boolean类型数据
    public boolean getBoolean(String key, boolean defValue) {
        return sMSp.getBoolean(key, defValue);
    }


    //获取int类型数据
    public int getInt(String key, int defValue) {
        return sMSp.getInt(key, defValue);
    }


}
