package com.ahstu.mycar.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.ahstu.mycar.R;
import com.ahstu.mycar.bean.User;

import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.listener.SaveListener;

/**
 * @author redowu 2016/4/25
 *         功能:用户名和密码注册
 */
public class RegisterUserMsgActivity extends Activity implements View.OnClickListener {
    private Context context;
    private String register_phone_num;
    private ImageView register_title_back;
    private EditText register_username;
    private EditText register_password;
    private EditText register_password_again;
    private Button register_button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user_msg);
        initView();
        initClick();
        this.context = this;
    }

    private void initClick() {
        register_title_back.setOnClickListener(this);
        register_button.setOnClickListener(this);
    }

    private void initView() {
        Intent i = getIntent();
        register_phone_num = i.getStringExtra("phoneNum");//获取RegisterPhoneActivity传递过来的注册手机号

        register_title_back = (ImageView) findViewById(R.id.iv_register_title_back);
        register_username = (EditText) findViewById(R.id.et_register_username);
        register_password = (EditText) findViewById(R.id.et_register_password);
        register_password_again = (EditText) findViewById(R.id.et_register_password_again);
        register_button = (Button) findViewById(R.id.btn_register);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_register_title_back:
                finish();
                break;
            case R.id.btn_register:
                if (register_username.getText().toString().isEmpty()) {
                    Toast.makeText(context, "请输入用户名", Toast.LENGTH_SHORT).show();
                } else if (register_password.getText().toString().isEmpty()) {
                    Toast.makeText(context, "请输入密码", Toast.LENGTH_SHORT).show();
                } else if (register_password_again.getText().toString().isEmpty()) {
                    Toast.makeText(context, "请再次输入密码", Toast.LENGTH_SHORT).show();
                } else if (!(register_password.getText().toString().equals(register_password_again.getText().toString()))) {
                    Toast.makeText(context, "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
                    register_password_again.setText("");

                } else {
                    final User user = new User();
                    user.setUsername(register_username.getText().toString());
                    user.setPassword(register_password.getText().toString());
                    user.setMobilePhoneNumber(register_phone_num);
                    user.setMyInstallation(BmobInstallation.getInstallationId(RegisterUserMsgActivity.this));
                    user.signUp(context, new SaveListener() {
                        @Override
                        public void onSuccess() {

                            //注册成功就将用户的信息保存到本地数据库中
                            SharedPreferences sp = getSharedPreferences("User", MODE_PRIVATE);
                            //存入数据
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("name", register_username.getText().toString());
                            editor.putString("password", register_password.getText().toString());
                            editor.commit();
                            startActivity(new Intent(context, MainActivity.class));
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            if (i == 202)
                                Toast.makeText(context, "用户名已存在，请重新输入", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(context, "注册失败" + s + i, Toast.LENGTH_SHORT).show();  //调试
                        }
                    });
                }
                break;
        }
    }

}
