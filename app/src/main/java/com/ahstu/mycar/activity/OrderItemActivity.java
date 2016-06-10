package com.ahstu.mycar.activity;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ahstu.mycar.R;
import com.ahstu.mycar.sql.DatabaseHelper;
import com.xys.libzxing.zxing.encoding.EncodingUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by xuning on 2016/5/23.
 */
public class OrderItemActivity extends Activity {
    TextView info_station;
    TextView info_username;
    TextView info_carnumber;
    TextView info_ctype;
    TextView info_gasprice;
    TextView info_gascount;
    TextView info_countprice;
    TextView info_time;
    ImageView erweima;
    ImageView meorder_back;
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myorder_item);
        inview();
        set();

    }

    void inview() {
        info_station = (TextView) findViewById(R.id.info_station);
        info_carnumber = (TextView) findViewById(R.id.info_carnumber);
        info_username = (TextView) findViewById(R.id.info_username);
        info_ctype = (TextView) findViewById(R.id.info_ctype);
        info_gasprice = (TextView) findViewById(R.id.info_gasprice);
        info_gascount = (TextView) findViewById(R.id.info_gascount);
        info_countprice = (TextView) findViewById(R.id.info_countprice);
        info_time = (TextView) findViewById(R.id.info_time);
        erweima = (ImageView) findViewById(R.id.info_erweima);
        meorder_back = (ImageView) findViewById(R.id.meorderback);
    }

    void set() {
        DatabaseHelper helper = new DatabaseHelper(OrderItemActivity.this, "node.db", null, 1);
        SQLiteDatabase data = helper.getReadableDatabase();
        bundle = getIntent().getExtras();
        int id = bundle.getInt("id");
        Log.e("Tag", "aaaaaaaaaaaaaaaaaaaa" + id);
        Cursor cursor = data.query("gasorder", new String[]{"username", "carnumber", "stationname", "ctype", "gascount", "gasprice", "countprice", "time"}, "id=?", new String[]{id + ""}, null, null, null);
        while (cursor.moveToNext()) {
            String username = cursor.getString(cursor.getColumnIndex("username"));

            String carnumber = cursor.getString(cursor.getColumnIndex("carnumber")).toString();
            String staion = cursor.getString(cursor.getColumnIndex("stationname")).toString();
            String ctype = cursor.getString(cursor.getColumnIndex("ctype")).toString();
            Double gascount = cursor.getDouble(cursor.getColumnIndex("gascount"));
            Double gasprice = cursor.getDouble(cursor.getColumnIndex("gasprice"));
            // Double countprice = cursor.getDouble(cursor.getColumnIndex("countprice"));
            String countprice = cursor.getString(cursor.getColumnIndex("countprice"));
            String time = cursor.getString(cursor.getColumnIndex("time")).toString();

            info_station.setText(staion);
            info_carnumber.setText(carnumber);
            info_username.setText(username);
            info_ctype.setText(ctype);
            info_gascount.setText(String.valueOf(gascount) + "升");
            info_gasprice.setText(String.valueOf(gasprice) + "元/升");
            info_countprice.setText(countprice);
            info_time.setText(time);
            JSONObject json = new JSONObject();
            try {
                json.put("加油站", staion);
                json.put("用户姓名", username);
                json.put("车牌号码", carnumber);
                json.put("加油类型", ctype);
                json.put("加油单价", gasprice);
                json.put("加油数量", gascount);
                json.put("加油总价", countprice);
                json.put("加油时间", time);


            } catch (JSONException e) {
                e.printStackTrace();
            }

            String conent = json.toString();
            Bitmap bitmap = EncodingUtils.createQRCode(conent, 400, 400, null);
            erweima.setImageBitmap(bitmap);
        }
        meorder_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


}
