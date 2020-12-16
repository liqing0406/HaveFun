package com.example.funactivity.view;

import android.content.Context;

import com.baidu.mapapi.SDKInitializer;
import com.example.funactivity.model.Model;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.EaseUI;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;

public class Application extends android.app.Application {
    private static Application instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        //初始化百度地图SDK,是否获取百度地图权限，包名秘钥
        SDKInitializer.initialize(this);
        //初始化easeUi
        EMOptions emOptions = new EMOptions();
        emOptions.setAutoAcceptGroupInvitation(false);
        emOptions.setAcceptInvitationAlways(false);
        EaseUI.getInstance().init(this, emOptions);
        //初始化数据模型层
        Model.getInstance().init(instance);
        //初始化相机权限
        ZXingLibrary.initDisplayOpinion(this);
    }

    public static Context getInstance() {
        return instance;
    }

}
