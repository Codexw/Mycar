package com.ahstu.mycar.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ahstu.mycar.R;
import com.ahstu.mycar.bean.Carinfomation;
import com.ahstu.mycar.sql.DatabaseHelper;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;

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
    TextView mecar_start;
    TextView mecar_door;
    TextView mecar_air;
    TextView mecar_lock;
    TextView mecar_frame;
    TextView mecar_box;
    TextView delete;
    Intent intent;
    Bundle bundle;
    String objectId;
    ImageView image;
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
        mecar_start = (TextView) findViewById(R.id.mecar_start);
        mecar_door = (TextView) findViewById(R.id.mecar_door);
        mecar_air = (TextView) findViewById(R.id.mecar_air);
        mecar_lock = (TextView) findViewById(R.id.mecar_lock);
        mecar_frame = (TextView) findViewById(R.id.mecar_frame);
        mecar_box = (TextView) findViewById(R.id.mecar_box);
        delete = (TextView) findViewById(R.id.medelete);
        image = (ImageView) findViewById(R.id.mecar_back);
        
    }

    void set() {
        final String s = bundle.getString("car_number");
        Log.e("MeCarActivity", ">>>>>>>>>>>>>>>>>>111<<<<<<<<<<<<<<<" + s);
        String[] columns = {"car_number", "car_model", "car_brand", "car_enginerno", "car_frame", "car_level", "car_mile",
                "car_gas", "car_box", "car_enginerstate", "car_shiftstate", "car_light", "car_start", "car_door", "car_air", "car_lock"};
        DatabaseHelper helper = new DatabaseHelper(MeCarActivity.this, "node.db", null, 1);
        SQLiteDatabase data = helper.getReadableDatabase();
        Cursor cursor = data.query("carinfo", columns, "car_number=?", new String[]{s}, null, null, null);
        while (cursor.moveToNext()) {
            mecar_number.setText(cursor.getString(cursor.getColumnIndex("car_number")));
            mecar_brand.setText(cursor.getString(cursor.getColumnIndex("car_brand")));
            mecar_model.setText(cursor.getString(cursor.getColumnIndex("car_model")));
            mecar_enginerno.setText(cursor.getString(cursor.getColumnIndex("car_enginerno")));
            mecar_frame.setText(cursor.getString(cursor.getColumnIndex("car_frame")));
            mecar_level.setText(cursor.getString(cursor.getColumnIndex("car_level")));
            mecar_mile.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex("car_mile"))) + "km");
            mecar_gas.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex("car_gas"))) + "%");
            mecar_box.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex("car_box"))) + "L");
            mecar_enginerstate.setText(cursor.getString(cursor.getColumnIndex("car_enginerstate")));
            mecar_shiftstate.setText(cursor.getString(cursor.getColumnIndex("car_shiftstate")));
            mecar_light.setText(cursor.getString(cursor.getColumnIndex("car_light")));
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

        BmobQuery<Carinfomation> query = new BmobQuery<Carinfomation>();
        query.addWhereEqualTo("car_number", s);
        query.findObjects(this, new FindListener<Carinfomation>() {
            @Override
            public void onSuccess(List<Carinfomation> list) {
                if (list.size() > 0) {
                    objectId = list.get(0).getObjectId();
                    //Toast.makeText(MeCarActivity.this, "查询成功" + objectId, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alert = new AlertDialog.Builder(MeCarActivity.this).create();
                alert.setTitle("删除");
                alert.setMessage("你确定要删除吗");
                alert.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //删除本地数据库
                        DatabaseHelper helper = new DatabaseHelper(MeCarActivity.this, "node.db", null, 1);
                        SQLiteDatabase db = helper.getWritableDatabase();
                        db.delete("carinfo", "car_number=?", new String[]{s});
                        db.close();
                        //删除服务器数据
                        Carinfomation carinfomation = new Carinfomation();
                        carinfomation.setObjectId(objectId);
                        carinfomation.delete(MeCarActivity.this, objectId, new DeleteListener() {
                            @Override
                            public void onSuccess() {
                                finish();
                            }
                            @Override
                            public void onFailure(int i, String s) {

                            }
                        });


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

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        
        
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
