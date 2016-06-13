package com.ahstu.mycar.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ahstu.mycar.R;
import com.ahstu.mycar.bean.Carinfomation;
import com.ahstu.mycar.bean.User;
import com.ahstu.mycar.sql.DatabaseHelper;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
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
    TextView car_frame;
    TextView car_box;
    Button car_save;
    ImageView cancel;
    int countp;
    String number;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.carinformation);
        //初始化汽车信息里面的组件
        initview();
        //给组件赋值
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alert = new AlertDialog.Builder(CarinformationActivity.this).create();
                alert.setTitle("提示");
                alert.setMessage("没保存信息，你确定要退出吗");
                alert.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();

                    }
                });
                alert.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                alert.show();
                
            }
        });
        Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();
        final String s = bundle.getString("car_sign").toString();
        new imageTask().execute(s);
        number = bundle.getString("car_number");
        car_number.setText(bundle.getString("car_number"));
        car_brand.setText(bundle.getString("car_brand"));
        car_model.setText(bundle.getString("car_model"));
        car_enginerno.setText(bundle.getString("car_enginerno"));
        car_frame.setText(bundle.getString("car_frame"));
        car_level.setText(bundle.getString("car_level"));
        car_gas.setText(String.valueOf(bundle.getInt("car_gas")) + "%");
        car_mile.setText(String.valueOf(bundle.getInt("car_mile")));
        car_box.setText(String.valueOf(bundle.getInt("car_box")));
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
        //判断车辆信息表，查找这辆车是否已经存在，存在则返回1.
        BmobQuery<Carinfomation> query = new BmobQuery<Carinfomation>();
        query.addWhereEqualTo("car_number", number);
        query.findObjects(CarinformationActivity.this, new FindListener<Carinfomation>() {
            @Override
            public void onSuccess(List<Carinfomation> list) {
                countp = list.size();
            }

            @Override
            public void onError(int i, String s) {

            }
        });
        //保存按钮添加监听事件
        car_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (countp > 0) {
                    Toast.makeText(CarinformationActivity.this, "车辆信息已存在", Toast.LENGTH_SHORT).show();
                } else if (countp == 0) {
                    //将信息保存到服务器中，和本地数据库中
                    User user = BmobUser.getCurrentUser(getApplicationContext(), User.class);
                    Carinfomation carinfomation = new Carinfomation();
                    carinfomation.setCar_brand(bundle.getString("car_brand"));
                    carinfomation.setCar_number(bundle.getString("car_number"));
                    carinfomation.setCar_sign(bundle.getString("car_sign"));
                    carinfomation.setCar_enginerno(bundle.getString("car_enginerno"));
                    carinfomation.setCar_frame(bundle.getString("car_frame"));
                    carinfomation.setCar_model(bundle.getString("car_model"));
                    carinfomation.setCar_mile(bundle.getInt("car_mile"));
                    carinfomation.setCar_gas(bundle.getInt("car_gas"));
                    carinfomation.setCar_box(bundle.getInt("car_box"));
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
                            Toast.makeText(CarinformationActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            Toast.makeText(CarinformationActivity.this, "添加失败", Toast.LENGTH_SHORT).show();
                        }
                    });

                    //将汽车信息保存在本地数据库中
//                    DatabaseHelper helper = new DatabaseHelper(CarinformationActivity.this, "node.db", null, 1);
//                    SQLiteDatabase db = helper.getWritableDatabase();
//                    ContentValues value = new ContentValues();
//                    value.put("car_number", bundle.getString("car_number"));
//                    value.put("car_sign", bundle.getString("car_sign"));
//                    value.put("car_brand", bundle.getString("car_brand"));
//                    value.put("car_model", bundle.getString("car_model"));
//                    value.put("car_enginerno", bundle.getString("car_enginerno"));
//                    value.put("car_frame", bundle.getString("car_frame"));
//                    value.put("car_level", bundle.getString("car_level"));
//                    value.put("car_mile", bundle.getInt("car_mile"));
//                    value.put("car_gas", bundle.getInt("car_gas"));
//                    value.put("car_box", bundle.getInt("car_box"));
//                    value.put("car_enginerstate", bundle.getString("car_enginerstate"));
//                    value.put("car_shiftstate", bundle.getString("car_shiftstate"));
//                    value.put("car_light", bundle.getString("car_light"));
//                    value.put("car_start", bundle.getBoolean("car_start"));
//                    value.put("car_door", bundle.getBoolean("car_door"));
//                    value.put("car_lock", bundle.getBoolean("car_lock"));
//                    value.put("car_air", bundle.getBoolean("car_air"));
//                    long a = db.insert("carinfo", null, value);
//                    Toast.makeText(CarinformationActivity.this, a + "", Toast.LENGTH_SHORT).show();
                    //使用异步把数据保存在本地
                    new Asytask().execute(bundle);

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
        car_frame = (TextView) findViewById(R.id.car_frame);
        car_box = (TextView) findViewById(R.id.car_box);
        cancel = (ImageView) findViewById(R.id.carlist_back);
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

    //使用异步加载把数据保存在本地数据库中
    class Asytask extends AsyncTask<Bundle, Void, ContentValues> {

        @Override
        protected ContentValues doInBackground(Bundle... params) {
            Bundle bundle = params[0];
            ContentValues value = new ContentValues();
            value.put("car_number", bundle.getString("car_number"));
            value.put("car_sign", bundle.getString("car_sign"));
            value.put("car_brand", bundle.getString("car_brand"));
            value.put("car_model", bundle.getString("car_model"));
            value.put("car_enginerno", bundle.getString("car_enginerno"));
            value.put("car_frame", bundle.getString("car_frame"));
            value.put("car_level", bundle.getString("car_level"));
            value.put("car_mile", bundle.getInt("car_mile"));
            value.put("car_gas", bundle.getInt("car_gas"));
            value.put("car_box", bundle.getInt("car_box"));
            value.put("car_enginerstate", bundle.getString("car_enginerstate"));
            value.put("car_shiftstate", bundle.getString("car_shiftstate"));
            value.put("car_light", bundle.getString("car_light"));
            value.put("car_start", bundle.getBoolean("car_start"));
            value.put("car_door", bundle.getBoolean("car_door"));
            value.put("car_lock", bundle.getBoolean("car_lock"));
            value.put("car_air", bundle.getBoolean("car_air"));
            Bitmap bitmap = getPicture(bundle.getString("car_sign").toString());
            final ByteArrayOutputStream os = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
            value.put("car_sign", os.toByteArray());
            return value;
        }

        @Override
        protected void onPostExecute(ContentValues values) {
            DatabaseHelper helper = new DatabaseHelper(CarinformationActivity.this, "node.db", null, 1);
            SQLiteDatabase db = helper.getWritableDatabase();
            db.insert("carinfo", null, values);
            db.close();
            // super.onPostExecute(values);
        }
    }

    class imageTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = getPicture(params[0]);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {

            car_sign.setImageBitmap(bitmap);
            super.onPostExecute(bitmap);
        }
    }
}
