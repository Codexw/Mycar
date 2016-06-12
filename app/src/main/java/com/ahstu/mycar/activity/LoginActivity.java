package com.ahstu.mycar.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ahstu.mycar.R;
import com.ahstu.mycar.bean.Carinfomation;
import com.ahstu.mycar.bean.User;
import com.ahstu.mycar.bean.order;
import com.ahstu.mycar.sql.DatabaseHelper;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * @author redowu 2016/4/25
 *         功能：登录界面，用于登录
 */
public class LoginActivity extends Activity implements View.OnClickListener {
    ProgressDialog progress;
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


                if (et_username.getText().toString().isEmpty()) {
                    Toast.makeText(LoginActivity.this, "请输入用户名", Toast.LENGTH_SHORT).show();
                } else if (et_password.getText().toString().isEmpty()) {
                    Toast.makeText(LoginActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                } else {
                    final User user = new User();
                    user.setUsername(et_username.getText().toString());
                    user.setPassword(et_password.getText().toString());
                    user.login(context, new SaveListener() {
                        @Override
                        public void onSuccess() {
                            progress = new ProgressDialog(LoginActivity.this);
                            progress.setMessage("正在登陆...");
                            progress.setCanceledOnTouchOutside(false);
                            progress.show();
                            //更新当前登录用户的设备号
                            BmobQuery<User> queryInstallation = new BmobQuery<User>();
                            queryInstallation.addWhereEqualTo("username", et_username.getText().toString());
                            queryInstallation.setLimit(1);
                            queryInstallation.findObjects(LoginActivity.this, new FindListener<User>() {
                                @Override
                                public void onSuccess(List<User> list) {
                                    for (User userIns : list) {
                                        userIns.setMyInstallation(BmobInstallation.getInstallationId(LoginActivity.this));
                                        userIns.update(LoginActivity.this, userIns.getObjectId(), new UpdateListener() {
                                            @Override
                                            public void onSuccess() {
                                            }

                                            @Override
                                            public void onFailure(int i, String s) {
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onError(int i, String s) {

                                }
                            });


                            //用户登录的时候，查询数据库，把车辆数据存放在本地数据库中。
                            final User user = BmobUser.getCurrentUser(getApplicationContext(), User.class);
                            BmobQuery<Carinfomation> query = new BmobQuery<Carinfomation>();
                            query.addWhereEqualTo("user", user);
                            query.order("-updatedAt");
                            query.findObjects(LoginActivity.this, new FindListener<Carinfomation>() {
                                @Override
                                public void onSuccess(List<Carinfomation> list) {

                                    for (int i = 0; i < list.size(); i++) {
                                        Carinfomation carinfomation = list.get(i);
                                        new Asyntask().execute(carinfomation);


                                    }


//                                    SharedPreferences share = getSharedPreferences("text", MODE_PRIVATE);
//                                    DatabaseHelper data = new DatabaseHelper(LoginActivity.this, "node.db", null, 1);
//                                    SQLiteDatabase sql = data.getReadableDatabase();
//                                    Cursor cursor = sql.query("carinfo", new String[]{"car_number"}, null, null, null, null, null);
//                                    if (cursor != null) {
//                                        if (cursor.moveToFirst()) {
//                                            SharedPreferences.Editor editer = share.edit();
//                                            editer.putInt("position", 0);
//                                            editer.putString("number", cursor.getString(cursor.getColumnIndex("car_number")).toString());
//                                            editer.commit();
//                                            // Log.e("TAG", "SSSSSSSSSSSSSSSSSSSS" + cursor.getString(cursor.getColumnIndex("car_number")).toString());
//                                        }
//
//                                    } else {
//
//                                        SharedPreferences.Editor editer = share.edit();
//                                        editer.putInt("position", 0);
//                                        editer.putString("number", "");
//                                        editer.commit();
//                                    }


                                }

                                @Override
                                public void onError(int i, String s) {

                                }

                            });

                            //从服务器获取订单信息
                            BmobQuery<order> orderquery = new BmobQuery<order>();
                            orderquery.addWhereEqualTo("user", user);
                            orderquery.order("-updatedAt");
                            orderquery.findObjects(LoginActivity.this, new FindListener<order>() {
                                @Override
                                public void onSuccess(List<order> list) {
                                    DatabaseHelper helper = new DatabaseHelper(LoginActivity.this, "node.db", null, 1);
                                    SQLiteDatabase db = helper.getWritableDatabase();
                                    for (int i = list.size() - 1; i >= 0; i--) {
                                        order order = new order();
                                        order = list.get(i);
                                        ContentValues values = new ContentValues();
                                        values.put("stationname", order.getStationname());
                                        values.put("carnumber", order.getCarnumber());
                                        values.put("username", user.getUsername().toString());
                                        values.put("ctype", order.getCtype());
                                        values.put("gascount", order.getGascount());
                                        values.put("countprice", order.getCountprice());
                                        values.put("gasprice", order.getGasprice());
                                        values.put("time", order.getTime());
                                        db.insert("gasorder", null, values);

                                    }
                                    progress.dismiss();
                                    db.close();

                                }

                                @Override
                                public void onError(int i, String s) {

                                }
                            });


                            //登录成功就将用户的信息保存到本地sharepreference数据库中
                            SharedPreferences sp = getSharedPreferences("User", MODE_PRIVATE);
                            //存入数据
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("name", et_username.getText().toString());
                            editor.putString("password", et_password.getText().toString());
                            editor.commit();

                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
                            finish();

                        }


                        @Override
                        public void onFailure(int i, String s) {

                            if (i == 9016) {
                                Toast.makeText(LoginActivity.this, "请检查您的网络连接", Toast.LENGTH_SHORT).show();
                            } else if (i == 101) {
                                Toast.makeText(LoginActivity.this, "账号或密码错误", Toast.LENGTH_SHORT).show();
                            } else
                                Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
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

    public Bitmap getPicture(String path) {
        Bitmap bm = null;
        try {
            URL url = new URL(path);
            URLConnection conn = url.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            bm = BitmapFactory.decodeStream(is);
        } catch (Exception e) {

            e.printStackTrace();
        }
        return bm;
    }

    void initview() {
        SharedPreferences share = getSharedPreferences("text", MODE_PRIVATE);
        DatabaseHelper data = new DatabaseHelper(LoginActivity.this, "node.db", null, 1);
        SQLiteDatabase sql = data.getReadableDatabase();
        Cursor cursor = sql.query("carinfo", new String[]{"car_number"}, null, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                SharedPreferences.Editor editer = share.edit();
                editer.putInt("position", 0);
                editer.putString("number", cursor.getString(cursor.getColumnIndex("car_number")).toString());
                editer.commit();
                // Log.e("TAG", "SSSSSSSSSSSSSSSSSSSS" + cursor.getString(cursor.getColumnIndex("car_number")).toString());
            }

        } else {

            SharedPreferences.Editor editer = share.edit();
            editer.putInt("position", 0);
            editer.putString("number", "");
            editer.commit();
        }

        //data.close();
        sql.close();
        cursor.close();

    }

    class Asyntask extends AsyncTask<Carinfomation, Void, ContentValues> {

        @Override
        protected ContentValues doInBackground(Carinfomation... params) {
            Carinfomation carinfomation = params[0];
            ContentValues value = new ContentValues();
            value.put("car_number", carinfomation.getCar_number());
            value.put("car_brand", carinfomation.getCar_brand());
            value.put("car_model", carinfomation.getCar_model());
            // value.put("car_sign", carinfomation.getCar_sign()); //圖片加載出現bug，由於網絡問題
            value.put("car_enginerno", carinfomation.getCar_enginerno());
            value.put("car_level", carinfomation.getCar_level());
            value.put("car_mile", carinfomation.getCar_mile());
            value.put("car_gas", carinfomation.getCar_gas());
            value.put("car_enginerstate", carinfomation.getCar_enginerstate());
            value.put("car_shiftstate", carinfomation.getCar_shiftstate());
            value.put("car_light", carinfomation.getCar_light());
            value.put("car_frame", carinfomation.getCar_frame());
            value.put("car_box", carinfomation.getCar_box());
            if (carinfomation.getCar_start() == false) {
                value.put("car_start", 0);
            } else {
                value.put("car_start", 1);
            }
            if (carinfomation.getCar_door() == false) {
                value.put("car_door", 0);

            } else {
                value.put("car_door", 1);

            }
            if (carinfomation.getCar_lock() == false) {

                value.put("car_lock", 0);

            } else {
                value.put("car_lock", 1);

            }

            if (carinfomation.getCar_air() == false) {

                value.put("car_air", 0);
            } else {
                value.put("car_air", 1);

            }
            Bitmap bitmap = getPicture(carinfomation.getCar_sign());
            final ByteArrayOutputStream os = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
            value.put("car_sign", os.toByteArray());


            return value;
        }

        @Override
        protected void onPostExecute(ContentValues values) {
            DatabaseHelper helper = new DatabaseHelper(LoginActivity.this, "node.db", null, 1);
            SQLiteDatabase db = helper.getWritableDatabase();
            db.insert("carinfo", null, values);
            db.close();
            Log.i("LoginActivity386", "sdsdsdddddddddddddddddd");
            initview();
        }
    }
}
