package com.ahstu.mycar.activity;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.ahstu.mycar.music.MusicPlayService;
import com.baidu.mapapi.SDKInitializer;

/**
 * @author 吴天洛 2016/5/7
 *         功能：百度地图sdk和聚合数据sdk的初始化以及音乐的服务
 */
public class MyApplication extends Application {

    MusicPlayService mService;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = ((MusicPlayService.LocalBinder) service).getService();//用绑定方法启动service，就是从这里绑定并得到service，然后就可以操作service了
            mService.setContext(getApplicationContext());
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化百度地图
        SDKInitializer.initialize(getApplicationContext());
        //初始化聚合数据
        com.thinkland.sdk.android.SDKInitializer.initialize(getApplicationContext());
        Intent intent = new Intent(this, MusicPlayService.class);
        startService(intent);
        System.out.println("intent?" + (null == intent));
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    public MusicPlayService getmService() {
        return mService;
    }

    public void setmService(MusicPlayService mService) {
        this.mService = mService;
    }

}
