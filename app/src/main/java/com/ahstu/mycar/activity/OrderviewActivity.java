package com.ahstu.mycar.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ahstu.mycar.R;
import com.ahstu.mycar.bean.Order;
import com.ahstu.mycar.sql.DatabaseHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import c.b.BP;
import c.b.PListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Administrator on 2016/7/14.
 */
public class OrderviewActivity extends Activity {
    private TextView station_name, gas_type, gas_number, gas_price, order_count, username, car_number, order_time;
    private ImageView order_pay_back;
    private Button order_pay_bt;
    private String h_number, objectid;
    private String count_price;
    private String station, type;
    private int PLUGINVERSION = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_order);
        BP.init(this, "ccd46e34cec57d61dbcedaa08f722296");
        h_number = getIntent().getStringExtra("h_number");
        objectid = getIntent().getStringExtra("objectid");
        init();
        operate();

    }

    public void operate() {
        order_pay_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        order_pay_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                int pluginVersion = BP.getPluginVersion();
                if (pluginVersion < PLUGINVERSION) {// 为0说明未安装支付插件, 否则就是支付插件的版本低于官方最新版
                    Toast.makeText(
                            OrderviewActivity.this,
                            pluginVersion == 0 ? "监测到本机尚未安装支付插件,无法进行支付,请先安装插件(无流量消耗)"
                                    : "监测到本机的支付插件不是最新版,最好进行更新,请先更新插件(无流量消耗)", 0).show();
                    installBmobPayPlugin("bp.db");
                }

                //订单支付
                BP.pay("加油站订单支付", station + "  " + type, Double.parseDouble(count_price), true
                        , new PListener() {

                            @Override
                            public void unknow() {
                                // 因为网络等问题,不能确认是否支付成功,请稍后手动查询(小概率事件)
                                Toast.makeText(OrderviewActivity.this, "因为网络等问题,不能确认是否支付成功,请稍后手动查询", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void succeed() {
                                // TODO Auto-generated method stub
                                Toast.makeText(OrderviewActivity.this, "订单支付成功", Toast.LENGTH_SHORT).show();
                                DatabaseHelper helper = new DatabaseHelper(OrderviewActivity.this, "node.db", null, 1);
                                SQLiteDatabase data = helper.getWritableDatabase();
                                ContentValues value = new ContentValues();
                                value.put("state", 1);
                                data.update("gasorder", value, "id=?", new String[]{h_number});
                                data.close();
                                Order order = new Order();
                                order.setValue("state", 1);
                                order.update(OrderviewActivity.this, objectid, new UpdateListener() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onFailure(int i, String s) {

                                    }
                                });
                                finish();
                            }

                            @Override
                            public void orderId(String orderid) {
                                // TODO Auto-generated method stub
//                                Toast.makeText(OrderviewActivity.this, "订单号:" + orderid, Toast.LENGTH_SHORT).show();

                            }

                            @Override
                            public void fail(int arg0, String reason) {
                                // TODO Auto-generated method stub
                                if (reason.equals("bmob plugin isn't installed"))
                                    Toast.makeText(OrderviewActivity.this, "安装支付安全插件", Toast.LENGTH_SHORT).show();
                                else if (reason.equals("user cancle the operation"))
                                    Toast.makeText(OrderviewActivity.this, "支付操作已取消", Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(OrderviewActivity.this, "交易失败:" + reason, Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                );
            }
        });
    }


    public void init() {
        order_pay_back = (ImageView) findViewById(R.id.order_pay_back);
        station_name = (TextView) findViewById(R.id.order_station_name);
        gas_type = (TextView) findViewById(R.id.order_pay_type);
        gas_price = (TextView) findViewById(R.id.order_pay_price);
        gas_number = (TextView) findViewById(R.id.order_pay_number);
        order_count = (TextView) findViewById(R.id.order_pay_count);
        username = (TextView) findViewById(R.id.order_pay_username);
        car_number = (TextView) findViewById(R.id.order_pay_car_number);
        order_time = (TextView) findViewById(R.id.order_pay_time);
        order_pay_bt = (Button) findViewById(R.id.order_paid_btn);


        DatabaseHelper helper = new DatabaseHelper(OrderviewActivity.this, "node.db", null, 1);
        SQLiteDatabase data = helper.getReadableDatabase();
        String columns[] = new String[]{"username", "carnumber", "stationname", "ctype", "gascount", "gasprice", "countprice", "time"};
        Cursor cursor = data.query("gasorder", columns, "id=?", new String[]{h_number}, null, null, null);

        while (cursor.moveToNext()) {
            station_name.setText(cursor.getString(cursor.getColumnIndex("stationname")).toString());
            gas_type.setText(cursor.getString(cursor.getColumnIndex("ctype")).toString());
            gas_price.setText(cursor.getString(cursor.getColumnIndex("gasprice")).toString() + " 元/升");
            gas_number.setText(cursor.getString(cursor.getColumnIndex("gascount")).toString() + "升");
            order_count.setText(cursor.getString(cursor.getColumnIndex("countprice")).toString());
            username.setText(cursor.getString(cursor.getColumnIndex("username")).toString());
            car_number.setText(cursor.getString(cursor.getColumnIndex("carnumber")).toString());
            order_time.setText(cursor.getString(cursor.getColumnIndex("time")).toString());

            String str = cursor.getString(cursor.getColumnIndex("countprice")).replace(",", "");
            count_price = str.substring(1, str.length());

            station = cursor.getString(cursor.getColumnIndex("stationname")).toString();
            type = cursor.getString(cursor.getColumnIndex("ctype")).toString();
        }
    }


    //安装支付安全插件
    void installBmobPayPlugin(String fileName) {
        try {
            InputStream is = getAssets().open(fileName);
            File file = new File(Environment.getExternalStorageDirectory()
                    + File.separator + fileName + ".apk");
            if (file.exists())
                file.delete();
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            byte[] temp = new byte[1024];
            int i = 0;
            while ((i = is.read(temp)) > 0) {
                fos.write(temp, 0, i);
            }
            fos.close();
            is.close();

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.parse("file://" + file),
                    "application/vnd.android.package-archive");
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
