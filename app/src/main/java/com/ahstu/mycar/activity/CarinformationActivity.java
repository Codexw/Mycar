package com.ahstu.mycar.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ahstu.mycar.R;
import com.ahstu.mycar.bean.Carinfomation;
import com.ahstu.mycar.bean.User;
import com.ahstu.mycar.sql.DatabaseHelper;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by xuning on 2016/5/4.
 */
public class CarinformationActivity extends Activity {
    Context context;
    TextView car_number;
    TextView car_brand;
    TextView car_model;
    TextView car_enginerno;
    TextView car_level;
    TextView car_mile;
    TextView car_gas;
    TextView car_enginerstate;
    TextView car_shiftstate;
    TextView car_light;
    ImageView car_sign;
    TextView car_start;
    TextView car_door;
    TextView car_air;
    TextView car_lock;
    Button car_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.carinformation);
        //初始化个人信息里面的组件
        initview();
        //给个人信息赋值
        Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();
        final String s = bundle.getString("car_sign").toString();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Bitmap bitmap = getPicture(s);
                car_sign.post(new Runnable() {
                    @Override
                    public void run() {
                        car_sign.setImageBitmap(bitmap);
                    }
                });
            }
        }).start();
        car_number.setText(bundle.getString("car_number"));
        car_brand.setText(bundle.getString("car_brand"));
        car_model.setText(bundle.getString("car_model"));
        car_enginerno.setText(bundle.getString("car_enginerno"));
        car_level.setText(bundle.getString("car_level"));
        //car_mile.setText(bundle.getString("car_mile"));
        //car_gas.setText(bundle.getString("car_gas"));
        car_gas.setText(String.valueOf(bundle.getInt("car_gas")) + "%");
        car_mile.setText(String.valueOf(bundle.getInt("car_mile")));
        car_enginerstate.setText(bundle.getString("car_enginerstate"));
        car_shiftstate.setText(bundle.getString("car_shiftstate"));
        car_light.setText(bundle.getString("car_light"));
        if (bundle.getBoolean("car_start") == false) {
            car_start.setText("已关闭");
        } else {
            car_start.setText("已开启");
        }
        if (bundle.getBoolean("car_door") == false) {

            car_door.setText("已关闭");
        } else {
            car_door.setText("已开启");
        }
        if (bundle.getBoolean("car_lock") == false) {

            car_lock.setText("已关闭");
        } else {

            car_lock.setText("已开启");
        }
        if (bundle.getBoolean("car_air") == false) {

            car_air.setText("已关闭");

        } else {
            car_air.setText("已开启");
        }


        car_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //将信息保存到服务器中，和本地数据库中
                User user = BmobUser.getCurrentUser(getApplicationContext(), User.class);
                Carinfomation carinfomation = new Carinfomation();
                carinfomation.setCar_brand(bundle.getString("car_brand"));
                carinfomation.setCar_number(bundle.getString("car_number"));
                carinfomation.setCar_sign(bundle.getString("car_sign"));
                carinfomation.setCar_enginerno(bundle.getString("car_enginerno"));
                carinfomation.setCar_model(bundle.getString("car_model"));
                carinfomation.setCar_mile(bundle.getInt("car_mile"));
                carinfomation.setCar_gas(bundle.getInt("car_gas"));
                carinfomation.setCar_enginerstate(bundle.getString("car_enginerstate"));
                carinfomation.setCar_level(bundle.getString("car_level"));
                carinfomation.setCar_shiftstate(bundle.getString("car_shiftstate"));
                carinfomation.setCar_light(bundle.getString("car_light"));
                carinfomation.setCar_door(bundle.getBoolean("car_door"));
                carinfomation.setCar_lock(bundle.getBoolean("car_lock"));
                carinfomation.setCar_air(bundle.getBoolean("car_air"));
                carinfomation.setCar_start(bundle.getBoolean("car_start"));
                carinfomation.setUser(user);
                carinfomation.save(context, new SaveListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(CarinformationActivity.this, "你保存成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        Toast.makeText(CarinformationActivity.this, "添加失败", Toast.LENGTH_SHORT).show();
                    }
                });

//                //将汽车信息保存在本地数据库中
                DatabaseHelper helper = new DatabaseHelper(CarinformationActivity.this, "node.db", null, 1);
                SQLiteDatabase db = helper.getWritableDatabase();
                ContentValues value = new ContentValues();
                value.put("car_number", bundle.getString("car_number"));
                value.put("car_sign", bundle.getString("car_sign"));
                value.put("car_brand", bundle.getString("car_brand"));
                value.put("car_model", bundle.getString("car_model"));
                value.put("car_enginerno", bundle.getString("car_enginerno"));
                value.put("car_level", bundle.getString("car_level"));
                value.put("car_mile", bundle.getInt("car_mile"));
                value.put("car_gas", bundle.getInt("car_gas"));
                value.put("car_enginerstate", bundle.getString("car_enginerstate"));
                value.put("car_shiftstate", bundle.getString("car_shiftstate"));
                value.put("car_light", bundle.getString("car_light"));

                value.put("car_start", bundle.getBoolean("car_start"));
                value.put("car_door", bundle.getBoolean("car_door"));
                value.put("car_lock", bundle.getBoolean("car_lock"));
                value.put("car_air", bundle.getBoolean("car_air"));
                long a = db.insert("carinfo", null, value);
                Toast.makeText(CarinformationActivity.this, a + "", Toast.LENGTH_SHORT).show();


                new Thread() {
                    @Override
                    public void run() {
                        try {
                            sleep(500);
                            finish();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();


            }


        });
    }

    //初始化控件
    void initview() {
        car_number = (TextView) findViewById(R.id.car_number);
        car_brand = (TextView) findViewById(R.id.car_brand);
        car_model = (TextView) findViewById(R.id.car_model);
        car_enginerno = (TextView) findViewById(R.id.car_enginerno);
        car_level = (TextView) findViewById(R.id.car_level);
        car_mile = (TextView) findViewById(R.id.car_mile);
        car_gas = (TextView) findViewById(R.id.car_gas);
        car_enginerstate = (TextView) findViewById(R.id.car_enginerstate);
        car_shiftstate = (TextView) findViewById(R.id.car_shiftstate);
        car_light = (TextView) findViewById(R.id.car_light);
        car_sign = (ImageView) findViewById(R.id.car_sign);
        car_save = (Button) findViewById(R.id.car_save);
        car_start = (TextView) findViewById(R.id.car_start);
        car_door = (TextView) findViewById(R.id.car_door);
        car_air = (TextView) findViewById(R.id.car_air);
        car_lock = (TextView) findViewById(R.id.car_lock);
        context = this;
    }


    //根据url获取网络图片资源。
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


}
