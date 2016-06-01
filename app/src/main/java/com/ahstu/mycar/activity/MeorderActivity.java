package com.ahstu.mycar.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.ahstu.mycar.R;
import com.ahstu.mycar.sql.DatabaseHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xuning on 2016/5/22.
 */
public class MeorderActivity extends Activity {
    ListView meorderlist;
    ImageView orderback;
    String station[];
    String time[];
    int a[];
    int size;
    List<Map<String, String>> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actvity_order);
        meorderlist = (ListView) findViewById(R.id.meorderlist);
        orderback = (ImageView) findViewById(R.id.or_back); 
        getdata();
        SimpleAdapter adapter = new SimpleAdapter(this, list, R.layout.meorder_item, new String[]{"station", "time"}, new int[]{R.id.order_item_text1, R.id.order_item_text2});
        meorderlist.setAdapter(adapter);
        meorderlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bundle bundle = new Bundle();
                bundle.putInt("id", a[i]);
                Intent intent = new Intent();
                intent.setClass(MeorderActivity.this, OrderItemActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);

            }
        });
        orderback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    void getdata() {
        DatabaseHelper helper = new DatabaseHelper(MeorderActivity.this, "node.db", null, 1);
        SQLiteDatabase data = helper.getReadableDatabase();
        Cursor cursor = data.query("gasorder", new String[]{"id", "stationname", "time"}, null, null, null, null, null);
        station = new String[cursor.getCount()];
        time = new String[cursor.getCount()];
        a = new int[cursor.getCount()];
        size = cursor.getCount();
        int j = 0;
        if (size > 0) {
            cursor.moveToLast();
            station[j] = cursor.getString(cursor.getColumnIndex("stationname"));
            time[j] = cursor.getString(cursor.getColumnIndex("time"));
            a[j] = cursor.getInt(cursor.getColumnIndex("id"));
            while (cursor.moveToPrevious()) {
                j++;
                station[j] = cursor.getString(cursor.getColumnIndex("stationname"));
                time[j] = cursor.getString(cursor.getColumnIndex("time"));
                a[j] = cursor.getInt(cursor.getColumnIndex("id"));

            }
            data.close();
        }
        list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("station", station[i]);
            map.put("time", time[i]);
            list.add(map);
        }


    }


}
