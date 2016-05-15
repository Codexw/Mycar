package com.ahstu.mycar.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.ahstu.mycar.R;
import com.ahstu.mycar.sql.DatabaseHelper;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by xuning on 2016/5/12.
 */
public class MeCarActivity extends Activity {
    Context context;
    TextView mecar_number;
    TextView mecar_brand;
    TextView mecar_model;
    TextView mecar_enginerno;
    TextView mecar_level;
    TextView mecar_mile;
    TextView mecar_gas;
    TextView mecar_enginerstate;
    TextView mecar_shiftstate;
    TextView mecar_light;
    ImageView mecar_sign;
    TextView mecar_start;
    TextView mecar_door;
    TextView mecar_air;
    TextView mecar_lock;

    Intent intent;
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mecaractivity);
        initview();
        intent = getIntent();
        bundle = intent.getExtras();
        set();

    }

    void initview() {
        mecar_number = (TextView) findViewById(R.id.mecar_number);
        mecar_brand = (TextView) findViewById(R.id.mecar_brand);
        mecar_model = (TextView) findViewById(R.id.mecar_model);
        mecar_enginerno = (TextView) findViewById(R.id.mecar_enginerno);
        mecar_level = (TextView) findViewById(R.id.mecar_level);
        mecar_mile = (TextView) findViewById(R.id.mecar_mile);
        mecar_gas = (TextView) findViewById(R.id.mecar_gas);
        mecar_enginerstate = (TextView) findViewById(R.id.mecar_enginerstate);
        mecar_shiftstate = (TextView) findViewById(R.id.mecar_shiftstate);
        mecar_light = (TextView) findViewById(R.id.mecar_light);
        mecar_sign = (ImageView) findViewById(R.id.mecar_sign);
        mecar_start = (TextView) findViewById(R.id.mecar_start);
        mecar_door = (TextView) findViewById(R.id.mecar_door);
        mecar_air = (TextView) findViewById(R.id.mecar_air);
        mecar_lock = (TextView) findViewById(R.id.mecar_lock);


    }

    void set() {
        String s = bundle.getString("car_number");
        Log.e("TAG", ">>>>>>>>>>>>>>>>>>111<<<<<<<<<<<<<<<" + s);
        String[] columns = {"car_number", "car_model", "car_brand", "car_enginerno", "car_level", "car_mile",
                "car_gas", "car_enginerstate", "car_shiftstate", "car_light", "car_sign", "car_start", "car_door", "car_air", "car_lock"};
        DatabaseHelper helper = new DatabaseHelper(MeCarActivity.this, "node.db", null, 1);
        SQLiteDatabase data = helper.getReadableDatabase();
        Cursor cursor = data.query("carinfo", columns, "car_number=?", new String[]{s}, null, null, null);
        while (cursor.moveToNext()) {
            mecar_number.setText(cursor.getString(cursor.getColumnIndex("car_number")));
            mecar_brand.setText(cursor.getString(cursor.getColumnIndex("car_brand")));
            mecar_model.setText(cursor.getString(cursor.getColumnIndex("car_model")));
            mecar_enginerno.setText(cursor.getString(cursor.getColumnIndex("car_enginerno")));
            mecar_level.setText(cursor.getString(cursor.getColumnIndex("car_level")));
            mecar_mile.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex("car_mile"))));
            mecar_gas.setText(String.valueOf(cursor.getString(cursor.getColumnIndex("car_gas"))) + "%");
            mecar_enginerstate.setText(cursor.getString(cursor.getColumnIndex("car_enginerstate")));
            mecar_shiftstate.setText(cursor.getString(cursor.getColumnIndex("car_shiftstate")));
            mecar_light.setText(cursor.getString(cursor.getColumnIndex("car_light")));
            final String url = cursor.getString(cursor.getColumnIndex("car_sign"));
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final Bitmap bitmap = getPicture(url);
                    mecar_sign.post(new Runnable() {
                        @Override
                        public void run() {
                            mecar_sign.setImageBitmap(bitmap);
                        }
                    });
                }
            }).start();
            if (cursor.getInt(cursor.getColumnIndex("car_start")) == 0) {
                mecar_start.setText("已关闭");
            } else {
                mecar_start.setText("已开启");
            }
            if (cursor.getInt(cursor.getColumnIndex("car_door")) == 0) {

                mecar_door.setText("已关闭");
            } else {
                mecar_door.setText("已开启");
            }
            if (cursor.getInt(cursor.getColumnIndex("car_lock")) == 0) {

                mecar_lock.setText("已关闭");
            } else {

                mecar_lock.setText("已开启");
            }
            if (cursor.getInt(cursor.getColumnIndex("car_air")) == 0) {

                mecar_air.setText("已关闭");

            } else {
                mecar_air.setText("已开启");
            }

        }
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
