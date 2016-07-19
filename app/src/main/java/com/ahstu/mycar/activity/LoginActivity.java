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
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ahstu.mycar.R;
import com.ahstu.mycar.adapter.Util;
import com.ahstu.mycar.bean.Carinfomation;
import com.ahstu.mycar.bean.Order;
import com.ahstu.mycar.bean.User;
import com.ahstu.mycar.sql.DatabaseHelper;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
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
 * @author 吴天洛 2016/4/25
 *         功能：登录界面，用于登录
 */
public class LoginActivity extends Activity implements View.OnClickListener {

    public static Tencent mTencent;
    //    private static boolean isServerSideLogin = false;
    private static String openId;
    ProgressDialog progress;
    IUiListener loginListener = new BaseUiListener() {
        @Override
        protected void doComplete(JSONObject values) {
//        	Log.d("SDKQQAgentPref", "AuthorSwitch_SDK:" + SystemClock.elapsedRealtime());
            initOpenidAndToken(values);
//            updateUserInfo();
//            updateLoginButton();
        }
    };
    private long exitTime = 0;
    private Context context;
    private EditText et_username;
    private EditText et_password;
    private Button btnLogin;
    private Button btnRegister;
    private Button btn_third_load;
    private UserInfo mInfo;
    private double mLatitude;
    private double mLongitude;
    private LocationClient mLocationClient;
    private MyLocationListener myLocationListener;
    private User qqusery;

    public static void initOpenidAndToken(JSONObject jsonObject) {
        try {
            String token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
            String expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
            openId = jsonObject.getString(Constants.PARAM_OPEN_ID);
            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires)
                    && !TextUtils.isEmpty(openId)) {
                mTencent.setAccessToken(token, expires);
                mTencent.setOpenId(openId);
            }
        } catch (Exception e) {
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.context = this;
        mTencent = Tencent.createInstance("222222", this);
        initView();
        initLocation();
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
        btn_third_load.setOnClickListener(this);
    }

    /**
     * 初始化控件
     */
    private void initView() {
        et_username = (EditText) findViewById(R.id.et_username);
        et_password = (EditText) findViewById(R.id.et_password);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnRegister = (Button) findViewById(R.id.btn_register);
        btn_third_load = (Button) findViewById(R.id.btn_third_load);

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
                    try {
                        progress = new ProgressDialog(LoginActivity.this);
                        progress.setMessage("正在登陆...");
                        progress.setCanceledOnTouchOutside(false);
                        progress.show();//偶尔会打印出异常
                    } catch (Exception e) {
                        Log.e("LoginActivityProgress", e.toString());
                    }

                    final User user = new User();
                    user.setUsername(et_username.getText().toString());
                    user.setPassword(et_password.getText().toString());
                    user.login(context, new SaveListener() {
                        @Override
                        public void onSuccess() {

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
                                }

                                @Override
                                public void onError(int i, String s) {
                                }

                            });

                            //从服务器获取订单信息
                            BmobQuery<Order> orderquery = new BmobQuery<Order>();
                            orderquery.addWhereEqualTo("user", user);
                            orderquery.order("-updatedAt");
                            orderquery.findObjects(LoginActivity.this, new FindListener<Order>() {
                                @Override
                                public void onSuccess(List<Order> list) {
                                    DatabaseHelper helper = new DatabaseHelper(LoginActivity.this, "node.db", null, 1);
                                    SQLiteDatabase db = helper.getWritableDatabase();
                                    for (int i = list.size() - 1; i >= 0; i--) {
                                        Order order = new Order();
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
                                        values.put("ordernumber", order.getObjectId());
                                        values.put("state", order.getState());
                                        db.insert("gasorder", null, values);

                                    }
//                                    try {
//                                        progress.dismiss();//java.lang.IllegalArgumentException: View not attached to window manager 会崩
//                                    }
//                                    catch(Exception e)
//                                    { 
//                                        
//                                    }
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
                            progress.dismiss();
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
            case R.id.btn_third_load:
//                if (!mTencent.isSessionValid()) {
                mTencent.login(LoginActivity.this, "all", loginListener);
//                    isServerSideLogin = false;
//                } else {
//                    mTencent.logout(LoginActivity.this);//退出登录
//                    updateUserInfo();
//                }
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

    //保存头像图片到本地
    public void savePicture(Bitmap bitmap) {
        String pictureName = "/sdcard/mycarmusic/qq" + openId.substring(0, 4) + ".jpg";
        File file = new File(pictureName);
        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    Handler mHandler = new Handler() {
//
//        @Override
//        public void handleMessage(Message msg) {
//            if (msg.what == 0) {
//                JSONObject response = (JSONObject) msg.obj;
//                if (response.has("nickname")) {
////                    try {
////                        mUserInfo.setText(response.getString("nickname"));
////                    } catch (JSONException e) {
////                        e.printStackTrace();
////                    }
//                }
//            }else if(msg.what == 1){
//                Bitmap bitmap = (Bitmap)msg.obj;
////                Bundle bundle=new Bundle();
//                Log.i("photo",bitmap+"<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
////                mUserLogo.setImageBitmap(bitmap);
//            }
//        }
//
//    };

    //更新用户信息
    private void updateUserInfo() {
        if (mTencent != null && mTencent.isSessionValid()) {
            IUiListener listener = new IUiListener() {

                @Override
                public void onError(UiError e) {

                }

                @Override
                public void onComplete(final Object response) {
//                    Message msg = new Message();
//                    msg.obj = response;
//                    msg.what = 0;
//                    mHandler.sendMessage(msg);
                    new Thread() {

                        @Override
                        public void run() {
                            JSONObject json = (JSONObject) response;
                            if (json.has("figureurl")) {

                                Bitmap bitmap = null;
                                try {
//                                    bitmap = Util.getbitmap(json.getString("figureurl_qq_2"));
//                                    savePicture(bitmap);
                                    new picture().execute(json.getString("figureurl_qq_2"));
                                } catch (JSONException e) {

                                }
//                                Message msg = new Message();
//                                msg.obj = bitmap;
//                                msg.what = 1;
//                                mHandler.sendMessage(msg);
                            }
                        }

                    }.start();
                }

                @Override
                public void onCancel() {

                }
            };
            mInfo = new UserInfo(this, mTencent.getQQToken());
            mInfo.getUserInfo(listener);

        } else {
//            mUserInfo.setText("");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_LOGIN ||
                requestCode == Constants.REQUEST_APPBAR) {
            Tencent.onActivityResultData(requestCode, resultCode, data, loginListener);
        }

        super.onActivityResult(requestCode, resultCode, data);
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

    private class BaseUiListener implements IUiListener {

        @Override
        public void onComplete(Object response) {
            JSONObject jsonResponse = (JSONObject) response;
            if ((null != jsonResponse && jsonResponse.length() == 0) || null == response) {
                Util.showResultDialog(LoginActivity.this, "返回为空", "登录失败");
                return;
            }


//			Util.showResultDialog(MainActivity.this, response.toString(), "登录成功");
            Toast.makeText(LoginActivity.this, "账号登录成功", Toast.LENGTH_SHORT).show();
            doComplete((JSONObject) response);

            updateUserInfo();

            final User qquser = new User();
            final String username = "qq" + openId.substring(0, 4);

            qquser.setUsername(username);
            qquser.setPassword(openId);
            qquser.setOpenId(openId);
            qquser.setMyInstallation(BmobInstallation.getInstallationId(LoginActivity.this));
            qquser.setLat(mLatitude);
            qquser.setLon(mLongitude);
            qquser.signUp(context, new SaveListener() {
                @Override
                public void onSuccess() {
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
                }

                @Override
                public void onFailure(int i, String s) {
                    if (i == 202) {

                        BmobQuery<User> qquser_query = new BmobQuery<User>();
                        qquser_query.addWhereEqualTo("username", username);
                        qquser_query.findObjects(LoginActivity.this, new FindListener<User>() {
                            @Override
                            public void onSuccess(List<User> list) {

                                qqusery = list.get(0);
                                BmobQuery<Carinfomation> query = new BmobQuery<Carinfomation>();
                                query.addWhereEqualTo("user", qqusery);
                                query.findObjects(LoginActivity.this, new FindListener<Carinfomation>() {

                                    @Override
                                    public void onSuccess(List<Carinfomation> list) {
                                        for (int i = 0; i < list.size(); i++) {
                                            Carinfomation carinfomation = list.get(i);

                                            new Asyntask().execute(carinfomation);
                                        }
                                    }

                                    @Override
                                    public void onError(int i, String s) {
                                    }

                                });


                                //从服务器获取订单信息
                                BmobQuery<Order> orderquery = new BmobQuery<Order>();
                                orderquery.addWhereEqualTo("user", qqusery);
                                orderquery.order("-updatedAt");
                                orderquery.findObjects(LoginActivity.this, new FindListener<Order>() {
                                    @Override
                                    public void onSuccess(List<Order> list) {
                                        DatabaseHelper helper = new DatabaseHelper(LoginActivity.this, "node.db", null, 1);
                                        SQLiteDatabase db = helper.getWritableDatabase();
                                        for (int i = list.size() - 1; i >= 0; i--) {
                                            Order order = new Order();
                                            order = list.get(i);
                                            ContentValues values = new ContentValues();
                                            values.put("stationname", order.getStationname());
                                            values.put("carnumber", order.getCarnumber());
                                            values.put("username", qqusery.getUsername());
                                            values.put("ctype", order.getCtype());
                                            values.put("gascount", order.getGascount());
                                            values.put("countprice", order.getCountprice());
                                            values.put("gasprice", order.getGasprice());
                                            values.put("time", order.getTime());
                                            values.put("ordernumber", order.getObjectId());
                                            values.put("state", order.getState());
                                            db.insert("gasorder", null, values);

                                        }
//                                    try {
//                                        progress.dismiss();//java.lang.IllegalArgumentException: View not attached to window manager 会崩
//                                    }
//                                    catch(Exception e)
//                                    { 
//                                        
//                                    }
                                        db.close();
                                    }

                                    @Override
                                    public void onError(int i, String s) {

                                    }
                                });


                            }

                            @Override
                            public void onError(int i, String s) {
                            }

                        });

                    } else
                        Toast.makeText(context, "注册失败" + s + i, Toast.LENGTH_SHORT).show();  //调试
                }
            });


            //qq验证成功就将用户的信息保存到本地数据库中
            SharedPreferences sp = getSharedPreferences("User", MODE_PRIVATE);
            //存入数据
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("name", username);
            editor.putString("password", openId);

//            Toast.makeText(LoginActivity.this,username,Toast.LENGTH_SHORT).show();
            editor.commit();

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }

        protected void doComplete(JSONObject values) {

        }

        @Override
        public void onError(UiError e) {
//			Util.toastMessage(MainActivity.this, "onError: " + e.errorDetail);
            Util.toastMessage(LoginActivity.this, "登录异常");
            Util.dismissDialog();
        }

        @Override
        public void onCancel() {
            Util.toastMessage(LoginActivity.this, "取消登录");
            Util.dismissDialog();
//            if (isServerSideLogin) {
//                isServerSideLogin = false;
//            }
        }
    }

    class picture extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {
            String path = strings[0];
            Bitmap bitmap = Util.getbitmap(path);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            savePicture(bitmap);
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
            DatabaseHelper helper = new DatabaseHelper(LoginActivity.this, "node.db", null, 1);
            SQLiteDatabase db = helper.getWritableDatabase();
            db.insert("carinfo", null, values);
            db.close();
        }
    }


}
