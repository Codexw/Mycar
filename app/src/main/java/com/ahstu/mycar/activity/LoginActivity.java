package com.ahstu.mycar.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ahstu.mycar.R;
import com.ahstu.mycar.bean.User;

import cn.bmob.v3.listener.SaveListener;

/**
 * @author redowu 2016/5/2
 *         功能：登录界面，用于登录
 */
public class LoginActivity extends Activity implements View.OnClickListener {
    private long exitTime = 0;
    private Context context;
    private EditText et_username;
    private EditText et_password;
    private Button btnLogin;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.context = this;
        initView();
        initClick();
        // 动画效果
        init();
    }

    /**
     * 监听点击事件
     */
    private void initClick() {
        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
    }

    /**
     * 初始化控件
     */
    private void initView() {
        et_username = (EditText) findViewById(R.id.et_username);
        et_password = (EditText) findViewById(R.id.et_password);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnRegister = (Button) findViewById(R.id.btn_register);
    }

    /**
     * 处理点击事件
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                User user = new User();
                user.setUsername(et_username.getText().toString());
                user.setPassword(et_password.getText().toString());
                user.login(context, new SaveListener() {
                    @Override
                    public void onSuccess() {

                        //登录成功就将用户的信息保存到本地数据库中
                        SharedPreferences sp = getSharedPreferences("User", MODE_PRIVATE);
                        //存入数据
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("name", et_username.getText().toString());
                        editor.putString("password", et_password.getText().toString());
                        editor.commit();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        Toast.makeText(LoginActivity.this, "登录失败" + s + i, Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case R.id.btn_register:
                startActivity(new Intent(LoginActivity.this, RegisterPhoneActivity.class));
                break;
        }
    }

    // 动画效果
    private void init() {
        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.login_anim);
        anim.setFillAfter(true);
        findViewById(R.id.user).startAnimation(anim);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - exitTime > 2000) {
                Toast.makeText(LoginActivity.this, "再按一次退出登录", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, SplashActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
