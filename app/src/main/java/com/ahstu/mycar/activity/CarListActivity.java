package com.ahstu.mycar.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.ahstu.mycar.R;
import com.ahstu.mycar.adapter.RadioAdapter;
import com.ahstu.mycar.bean.Carinfomation;
import com.ahstu.mycar.bean.User;
import com.ahstu.mycar.sql.DatabaseHelper;
import com.xys.libzxing.zxing.activity.CaptureActivity;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by xuning on 2016/5/9.
 */
public class CarListActivity extends Activity {
    Button caradd;
    ListView carlist;
    ImageView image;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.car_activity_list);
        initview();

        //添加按钮添加监听事件
        caradd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CarListActivity.this, CaptureActivity.class);
                startActivityForResult(intent, 0);
            }
        });
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    //初始化控件
    void initview() {
        caradd = (Button) findViewById(R.id.car_add);
        carlist = (ListView) findViewById(R.id.carList);
        image = (ImageView) findViewById(R.id.carlist_back);

    }

    //返回的数据调用其方法
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {


                    final Bundle bundle = data.getExtras();
                    Intent intent = new Intent();
                    intent.putExtras(bundle);
                    intent.setClass(CarListActivity.this, CarinformationActivity.class);
                    startActivity(intent);

                }
                break;

        }


    }

    //从数据库获取数据，添加到集合中
    public ArrayList getdata() {
        ArrayList<String> arraylist = new ArrayList<String>();

        DatabaseHelper helper = new DatabaseHelper(CarListActivity.this, "node.db", null, 1);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cs = db.query("carinfo", new String[]{"car_number"}, null, null, null, null, null);
        while (cs.moveToNext()) {
            String s = cs.getString(cs.getColumnIndex("car_number"));
            arraylist.add(s);

        }
        cs.close();
        db.close();
        return arraylist;
    }

    @Override
    protected void onPause() {
        super.onPause();
        carlist.setAdapter(new RadioAdapter(CarListActivity.this, getdata()));
        //list = getdata();

    }

    @Override
    protected void onResume() {
        super.onResume();
        User user = BmobUser.getCurrentUser(getApplicationContext(), User.class);
        BmobQuery<Carinfomation> query = new BmobQuery<Carinfomation>();
        query.addWhereEqualTo("user", user);
        query.order("-updatedAt");
        query.findObjects(CarListActivity.this, new FindListener<Carinfomation>() {
            @Override
            public void onSuccess(List<Carinfomation> list) {
                DatabaseHelper helper = new DatabaseHelper(CarListActivity.this, "node.db", null, 1);
                SQLiteDatabase db = helper.getWritableDatabase();
                for (int i = 0; i < list.size(); i++) {
                    Carinfomation carinfomation = list.get(i);
                    ContentValues value = new ContentValues();
                    // value.put("car_number", carinfomation.getCar_number());
                    value.put("car_brand", carinfomation.getCar_brand());
                    value.put("car_model", carinfomation.getCar_model());
                    value.put("car_sign", carinfomation.getCar_sign());
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
                    db.update("carinfo", value, "car_number=?", new String[]{carinfomation.getCar_number()});


                }
                db.close();
            }

            @Override
            public void onError(int i, String s) {

            }
        });
        carlist.setAdapter(new RadioAdapter(CarListActivity.this, getdata()));


    }


}

