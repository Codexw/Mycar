package com.ahstu.mycar.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.ahstu.mycar.R;
import com.ahstu.mycar.bean.Carinfomation;
import com.ahstu.mycar.bean.User;
import com.ahstu.mycar.sql.DatabaseHelper;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.SaveListener;

/**
 * @author 吴天洛 2016/4/25
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
    private LocationClient mLocationClient;
    private MyLocationListener myLocationListener;
    private double mLatitude;
    private double mLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user_msg);
        initView();
        initClick();
        initLocation();
        this.context = this;
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
                    user.setLat(mLatitude);
                    user.setLon(mLongitude);
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
                            SharedPreferences share = getSharedPreferences("text", MODE_PRIVATE);
                            SharedPreferences.Editor edit = share.edit();
                            edit.putString("number", "浙CN20F6(测试车辆)");
                            edit.commit();
                            new savedata().execute();
                            User user = BmobUser.getCurrentUser(getApplicationContext(), User.class);
                            Carinfomation carinfomation = new Carinfomation();
                            carinfomation.setCar_number("浙CN20F6(测试车辆)");
                            carinfomation.setCar_sign("http://bmob-cdn-1238.b0.upaiyun.com/2016/06/13/6fb613eb40a17473809eff6895b5f4a1.png");
                            carinfomation.setCar_enginerno("F1196354");
                            carinfomation.setCar_frame("LJDMAA222F0228852");
                            carinfomation.setCar_model("k4");
                            carinfomation.setCar_brand("起亚");
                            carinfomation.setCar_mile(5000);
                            carinfomation.setCar_gas(60);
                            carinfomation.setCar_box(62);
                            carinfomation.setCar_enginerstate("正常");
                            carinfomation.setCar_level("4门5座");
                            carinfomation.setCar_shiftstate("正常");
                            carinfomation.setCar_light("正常");
                            carinfomation.setCar_door(false);
                            carinfomation.setCar_lock(false);
                            carinfomation.setCar_air(false);
                            carinfomation.setCar_start(false);
                            carinfomation.setUser(user);
                            carinfomation.save(context, new SaveListener() {
                                @Override
                                public void onSuccess() {
                                    //Toast.makeText(CarinformationActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(int i, String s) {
                                    //Toast.makeText(CarinformationActivity.this, "添加失败", Toast.LENGTH_SHORT).show();
                                }
                            });
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

    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null) {
                return;
            }
            mLatitude = location.getLatitude();
            mLongitude = location.getLongitude();
        }
    }

    class savedata extends AsyncTask<Void, Void, ContentValues> {


        @Override
        protected ContentValues doInBackground(Void... params) {
            ContentValues value = new ContentValues();

            value.put("car_number", "浙CN20F6(测试车辆)");
            value.put("car_sign", "http://bmob-cdn-1238.b0.upaiyun.com/2016/06/13/6fb613eb40a17473809eff6895b5f4a1.png");
            value.put("car_brand", "起亚");
            value.put("car_model", "k4");
            value.put("car_enginerno", "F1196354");
            value.put("car_frame", "LJDMAA222F0228852");
            value.put("car_level", "4门5座");
            value.put("car_mile", 5000);
            value.put("car_gas", 60);
            value.put("car_box", 62);
            value.put("car_enginerstate", "正常");
            value.put("car_shiftstate", "正常");
            value.put("car_light", "正常");
            value.put("car_start", false);
            value.put("car_door", false);
            value.put("car_lock", false);
            value.put("car_air", false);
            Bitmap bitmap = getPicture("http://bmob-cdn-1238.b0.upaiyun.com/2016/06/13/6fb613eb40a17473809eff6895b5f4a1.png");
            final ByteArrayOutputStream os = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
            value.put("car_sign", os.toByteArray());
            return value;
        }

        @Override
        protected void onPostExecute(ContentValues values) {
            DatabaseHelper helper = new DatabaseHelper(RegisterUserMsgActivity.this, "node.db", null, 1);
            SQLiteDatabase db = helper.getWritableDatabase();
            db.insert("carinfo", null, values);
            db.close();
        }
    }


}
