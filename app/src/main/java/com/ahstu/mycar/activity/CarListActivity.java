package com.ahstu.mycar.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.ahstu.mycar.R;
import com.ahstu.mycar.adapter.RadioAdapter;
import com.ahstu.mycar.sql.DatabaseHelper;
import com.xys.libzxing.zxing.activity.CaptureActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuning on 2016/5/9.
 */
public class CarListActivity extends Activity {
    Button caradd;
    ListView carlist;
    List list;

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

    }

    //初始化控件
    void initview() {
        caradd = (Button) findViewById(R.id.car_add);
        carlist = (ListView) findViewById(R.id.carList);


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


    }

    @Override
    protected void onResume() {
        super.onResume();
        carlist.setAdapter(new RadioAdapter(CarListActivity.this, getdata()));
        list = getdata();
        carlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(CarListActivity.this, MeCarActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("car_number", list.get(i).toString());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

    }
}

