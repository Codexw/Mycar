package com.ahstu.mycar.activity;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;

/**
 * @author redowu 2016/5/7
 *         功能：百度地图sdk和聚合数据sdk的初始化
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化百度地图
        SDKInitializer.initialize(getApplicationContext());
        //初始化聚合数据
        com.thinkland.sdk.android.JuheSDKInitializer.initialize(getApplicationContext());
    }

}
