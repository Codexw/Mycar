package com.ahstu.mycar.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ahstu.mycar.R;
import com.ahstu.mycar.bean.Price;
import com.ahstu.mycar.bean.Station;
import com.ahstu.mycar.bean.User;
import com.ahstu.mycar.bean.Order;
import com.ahstu.mycar.sql.DatabaseHelper;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.SaveListener;

/**
 * @author xuning on 2016/5/20.
 *         功能：预约加油
 */
public class GasorderActivity extends Activity {
    TextView station_name;
    TextView count;
    TextView gas_price;
    Spinner spinner;
    EditText editText;
    Button submit;
    ImageView order_back;
    Station s;
    Price price;
    ArrayList<Price> Pricelist;
    String[] ctype;
    String[] gasprice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gas_orderactivity);
        initview();
        set();
    }

    void initview() {
        station_name = (TextView) findViewById(R.id.gasstation_name);
        count = (TextView) findViewById(R.id.count_price);
        gas_price = (TextView) findViewById(R.id.gas_price);
        spinner = (Spinner) findViewById(R.id.spinner_type);
        editText = (EditText) findViewById(R.id.gas_count);
        submit = (Button) findViewById(R.id.btn_order);
        order_back = (ImageView) findViewById(R.id.order_back);


    }

    void set() {
        order_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        s = getIntent().getParcelableExtra("station");
        Pricelist = s.getGastPriceList();
        ctype = new String[Pricelist.size()];
        gasprice = new String[Pricelist.size()];
        for (int i = 0; i < Pricelist.size(); i++) {
            price = new Price();
            price = Pricelist.get(i);
            ctype[i] = price.getType();
            gasprice[i] = price.getPrice();


        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ctype);
        adapter.setDropDownViewResource(R.layout.spinner);
        spinner.setAdapter(adapter);
        station_name.setText(s.getName());
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                gas_price.setText(gasprice[i]);
                editText.setText("");

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (editText.getText().toString().equals("")) {
                    count.setText("0");
                } else if (editText.getText().toString().startsWith(".")) {
                    Toast.makeText(GasorderActivity.this, "输入格式不正确", Toast.LENGTH_SHORT).show();

                } else {
                    String ss = gas_price.getText().toString().trim();
                    double sum = Double.parseDouble(ss.substring(0, ss.length() - 3));
                    double cot = Double.parseDouble(editText.getText().toString().trim());
                    NumberFormat format = NumberFormat.getCurrencyInstance();
                    format.setMaximumFractionDigits(2);
                    format.setMinimumFractionDigits(1);
                    count.setText(format.format(sum * cot));

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                //获取用户名
                SharedPreferences sp = getSharedPreferences("User", MODE_PRIVATE);
                String name = sp.getString("name", "");
                //打开数据库
                DatabaseHelper helper = new DatabaseHelper(GasorderActivity.this, "node.db", null, 1);
                SQLiteDatabase data = helper.getWritableDatabase();
                //获取正在控制的车辆车牌
                SharedPreferences share = getSharedPreferences("text", MODE_PRIVATE);
                String carnumber = share.getString("number", "");
                ContentValues values = new ContentValues();
                Date currentTime = new Date();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String time = formatter.format(currentTime);
                String ctype = spinner.getSelectedItem().toString();

                if (editText.getText().toString().equals("")) {
                    Toast.makeText(GasorderActivity.this, "请填写预约加油量", Toast.LENGTH_SHORT).show();


                } else {
                    //订单信息保存在本地
                    values.put("time", time);
                    values.put("stationname", station_name.getText().toString());
                    values.put("username", name);
                    values.put("ctype", ctype);
                    String a = gas_price.getText().toString().trim();
                    values.put("gasprice", Double.valueOf(a.substring(0, a.length() - 3)));
                    String b = editText.getText().toString();
                    values.put("gascount", Double.valueOf(b));
                    values.put("carnumber", carnumber);
                    values.put("countprice", count.getText().toString());
                    data.insert("gasorder", null, values);
                    data.close();
                    //订单信息保存在服务器中
                    User user = BmobUser.getCurrentUser(getApplicationContext(), User.class);
                    Order order = new Order();
                    order.setUser(user);
                    order.setStationname(station_name.getText().toString());
                    order.setCtype(ctype);
                    order.setGasprice(Double.valueOf(a.substring(0, a.length() - 3)));
                    order.setCarnumber(carnumber);
                    order.setGascount(Double.valueOf(b));
                    order.setCountprice(count.getText().toString());
                    order.setTime(time);
                    order.save(GasorderActivity.this, new SaveListener() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(GasorderActivity.this, "订单已完成", Toast.LENGTH_SHORT).show();
                            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputMethodManager.hideSoftInputFromInputMethod(view.getWindowToken(), 0);
                            finish();
                        }

                        @Override
                        public void onFailure(int i, String s) {
                        }
                    });
                }
            }
        });

    }


}
