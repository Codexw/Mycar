package com.ahstu.mycar.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.ahstu.mycar.R;
import com.ahstu.mycar.bean.User;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * @author 吴天洛 2016/4/25
 *         功能：欢迎界面动画、以及程序后台的部分加载
 */
public class SplashActivity extends Activity implements Animation.AnimationListener {
    private ImageView imageView;
    private Animation animation;
    private LocationClient mLocationClient;
    private MyLocationListener myLocationListener;
    private double mLatitude;
    private double mLongitude;
    private SharedPreferences sp;
    private String name;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //初始化BmobSDK
        Bmob.initialize(this, "ccd46e34cec57d61dbcedaa08f722296");
        imageView = (ImageView) findViewById(R.id.splash_img);

        //欢迎界面动画效果
        animation = AnimationUtils.loadAnimation(this, R.anim.welcome);
        animation.setFillEnabled(true);
        animation.setFillAfter(true);
        imageView.setAnimation(animation);
        animation.setAnimationListener(this);

        //从SharedPreferences本地数据库中读取是否有缓存的登录用户信息
        sp = getSharedPreferences("User", MODE_PRIVATE);
        name = sp.getString("name", "");
        password = sp.getString("password", "");

        initLocation();
    }

    private void initLocation() {
        mLocationClient = new LocationClient(this);
        myLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(myLocationListener);

        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll"); // 返回百度经纬度坐标系 ：bd09ll
        option.setIsNeedAddress(true); // 设置是否需要地址信息，默认为无地址
        option.setOpenGps(true);
        option.setScanSpan(1000);// 设置扫描间隔，单位毫秒，当<1000(1s)时，定时定位无效
        mLocationClient.setLocOption(option);//将上面option中的设置加载
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // TODO Auto-generated method stub
        super.onNewIntent(intent);
        if ((Intent.FLAG_ACTIVITY_CLEAR_TOP & intent.getFlags()) != 0) {
            finish();
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (name.isEmpty() || password.isEmpty()) {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));

        } else {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false;
        }
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mLocationClient.isStarted()) {
            mLocationClient.start();
        }
    }

    //退出程序时关闭定位
    @Override
    public void onStop() {
        super.onStop();
        //停止地图定位
        mLocationClient.stop();
    }

    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null) {
                return;
            }
            mLatitude = location.getLatitude();
            mLongitude = location.getLongitude();

            if (!name.isEmpty() && !password.isEmpty()) {

                //更新当前用户的经纬度
                BmobQuery<User> query = new BmobQuery<User>();
                query.addWhereEqualTo("username", name);
                query.setLimit(1);
                query.findObjects(SplashActivity.this, new FindListener<User>() {
                    @Override
                    public void onSuccess(List<User> list) {
                        if(list.size()>0) {
                            for (User user : list) {
                                user.setLat(mLatitude);
                                user.setLon(mLongitude);
                                user.update(SplashActivity.this, user.getObjectId(), new UpdateListener() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onFailure(int i, String s) {
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onError(int i, String s) {

                    }
                });
            }
        }
    }
}
