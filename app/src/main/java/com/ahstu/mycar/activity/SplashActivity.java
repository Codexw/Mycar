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

import cn.bmob.v3.Bmob;

/**
 * @author redowu 2016/4/25
 *         功能：欢迎界面动画、以及程序后台的部分加载
 */
public class SplashActivity extends Activity implements Animation.AnimationListener {
    private ImageView imageView;
    private Animation animation;

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

        //从SharedPreferences本地数据库中读取是否有缓存的登录用户信息
        SharedPreferences sp = getSharedPreferences("User", MODE_PRIVATE);
        String name = sp.getString("name", "");
        String password = sp.getString("password", "");

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
}
