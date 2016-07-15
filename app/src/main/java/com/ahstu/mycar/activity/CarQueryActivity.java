package com.ahstu.mycar.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ahstu.mycar.R;
import com.ahstu.mycar.me.WeizhangResponseAdapter;
import com.ahstu.mycar.sql.DatabaseHelper;
import com.cheshouye.api.client.WeizhangClient;
import com.cheshouye.api.client.WeizhangIntentService;
import com.cheshouye.api.client.json.CarInfo;
import com.cheshouye.api.client.json.InputConfigJson;
import com.cheshouye.api.client.json.WeizhangResponseHistoryJson;
import com.cheshouye.api.client.json.WeizhangResponseJson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuning on 2016/5/3.
 * 功能：违章查询
 */
public class CarQueryActivity extends Activity {
    final Handler cwjHandler = new Handler();//用来更新ui
    TextView chaxundi;
    String chepainumber = "浙C6RH69";
    String engine_number = "8300861-3F1";
    String chejia_number = "LKCBACAB88H011922";
    String s = "";
    Button btn_query;
    TextView chepai;
    ImageView query_back;
    WeizhangResponseJson info = null;//违章信息存放类
    FrameLayout frame;
    TextView result_null;
    TextView result_title;
    ListView result_list;
    final Runnable mUpdateResults = new Runnable() {
        public void run() {
            updateUI();
        }
    };
    private ProgressBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.che_query);
        //初始化违章查询的服务
        Intent weizhangIntent = new Intent(this, WeizhangIntentService.class);
        weizhangIntent.putExtra("appId", 1702);
        weizhangIntent.putExtra("appKey", "62fdfbce0fd98c355994140f850d2353");
        startService(weizhangIntent);
        SharedPreferences share = getSharedPreferences("text", MODE_PRIVATE);
        String number = share.getString("number", "");
        number = number.substring(0, 7);
        DatabaseHelper helper = new DatabaseHelper(CarQueryActivity.this, "node.db", null, 1);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("carinfo", new String[]{"car_number", "car_enginerno", "car_frame"}, "car_number=?", new String[]{number}, null, null, null);
        while (cursor.moveToNext()) {
            chepainumber = cursor.getString(cursor.getColumnIndex("car_number")).trim();
            engine_number = cursor.getString(cursor.getColumnIndex("car_enginerno")).trim();
            chejia_number = cursor.getString(cursor.getColumnIndex("car_frame")).trim();

        }
//        Log.i("shuchu", "sssssssssss" + chepainumber);
//        Log.i("shuchu", "ssssssssss" + engine_number);
//        Log.i("shuchu", "sssssssssss" + chejia_number);

        //初始化组件
        chaxundi = (TextView) findViewById(R.id.chaxundi);
        chepai = (TextView) findViewById(R.id.chepai);
        result_null = (TextView) findViewById(R.id.result_null);
        result_title = (TextView) findViewById(R.id.result_title);
        result_list = (ListView) findViewById(R.id.listresult);
        bar = (ProgressBar) findViewById(R.id.progress);
        frame = (FrameLayout) findViewById(R.id.frame);
        chepai.setText(number);
        query_back = (ImageView) findViewById(R.id.carquery_exit);
        query_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //查询地添加监听器进行跳转 
        chaxundi.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(CarQueryActivity.this, ProvinceListActivity.class);
                startActivityForResult(intent, 1);
            }
        });
        //查询按钮点击事件
        btn_query = (Button) findViewById(R.id.btn_query);
        btn_query.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //汽车的一些信息，用CarInfo类封装再将这个类传递到WeizhangResultActivity中去。
                CarInfo car = new CarInfo();
                String quertCityStr = null;
                String quertCityIdStr = null;
                if (chaxundi.getText() != null
                        && !chaxundi.getText().equals("")) {
                    quertCityStr = chaxundi.getText().toString().trim();

                }

                if (chaxundi.getTag() != null
                        && !chaxundi.getTag().equals("")) {
                    quertCityIdStr = chaxundi.getTag().toString().trim();
                    car.setCity_id(Integer.parseInt(quertCityIdStr));
                }
                if (car.getCity_id() == 0) {
                    Toast.makeText(CarQueryActivity.this, "请选择查询地", Toast.LENGTH_SHORT).show();

                }

                if (car.getCity_id() > 0) {
                    //使用InputConfigJson来判断进行查询时，要输入哪些内容，例如发动机号，车架号。
                    InputConfigJson inputConfig = WeizhangClient.getInputConfig(car
                            .getCity_id());
                    int engineno = inputConfig.getEngineno();
                    int classno = inputConfig.getClassno();
                    if (engineno == 0) {
                        car.setEngine_no(s);
                    } else if (engineno < 0) {
                        car.setEngine_no(engine_number);

                    } else {

                        car.setEngine_no(engine_number.substring(engine_number.length() - engineno));
                    }

                    if (classno == 0) {
                        car.setChejia_no(s);

                    } else if (classno < 0) {

                        car.setChejia_no(chejia_number);

                    } else {
                        car.setChejia_no(chejia_number.substring(chejia_number.length() - classno));
                    }

                    car.setChepai_no(chepainumber);

                    frame.setVisibility(View.VISIBLE);
                    result_null.setVisibility(View.GONE);
                    result_list.setVisibility(View.GONE);
                    result_title.setVisibility(View.GONE);
                    step4(car);
                }
            }
        });

    }

    public void step4(final CarInfo car) {
        // 声明一个子线程
        new Thread() {
            @Override
            public void run() {
                try {
                    // 这里写入子线程需要做的工作
                    info = WeizhangClient.getWeizhang(car);
                    cwjHandler.post(mUpdateResults); // 高速UI线程可以更新结果了
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    //将选择的城市名字和id传递回来
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (data == null)
            return;
        Bundle bundle1 = data.getExtras();
        String cityName = bundle1.getString("city_name");
        System.out.println(cityName);
        String cityId = bundle1.getString("city_id");

        chaxundi.setText(cityName);
        chaxundi.setTag(cityId);
    }


    private List getData() {
        List<WeizhangResponseHistoryJson> list = new ArrayList();

        for (WeizhangResponseHistoryJson weizhangResponseHistoryJson : info
                .getHistorys()) {
            WeizhangResponseHistoryJson json = new WeizhangResponseHistoryJson();
            json.setFen(weizhangResponseHistoryJson.getFen());
            json.setMoney(weizhangResponseHistoryJson.getMoney());
            json.setOccur_date(weizhangResponseHistoryJson.getOccur_date());
            json.setOccur_area(weizhangResponseHistoryJson.getOccur_area());
            json.setInfo(weizhangResponseHistoryJson.getInfo());
            list.add(json);
        }

        return list;
    }

    private void updateUI() {

        frame.setVisibility(View.GONE);
        Log.d("返回数据", info.toJson());

        // 直接将信息限制在 Activity中
        if (info.getStatus() == 2001) {
            result_null.setVisibility(View.GONE);
            result_title.setVisibility(View.VISIBLE);
            result_list.setVisibility(View.VISIBLE);

            result_title.setText("共违章" + info.getCount() + "次, 计"
                    + info.getTotal_score() + "分, 罚款 " + info.getTotal_money()
                    + "元");

            WeizhangResponseAdapter mAdapter = new WeizhangResponseAdapter(
                    this, getData());
            result_list.setAdapter(mAdapter);

        } else {
            // 没有查到为章记录

            if (info.getStatus() == 5000) {
                result_null.setText("请求超时，请稍后重试");
            } else if (info.getStatus() == 5001) {
                result_null.setText("交管局系统连线忙碌中，请稍后再试");
            } else if (info.getStatus() == 5002) {
                result_null.setText("恭喜，当前城市交管局暂无您的违章记录");
            } else if (info.getStatus() == 5003) {
                result_null.setText("数据异常，请重新查询");
            } else if (info.getStatus() == 5004) {
                result_null.setText("系统错误，请稍后重试");
            } else if (info.getStatus() == 5005) {
                result_null.setText("车辆查询数量超过限制");
            } else if (info.getStatus() == 5006) {
                result_null.setText("你访问的速度过快, 请后再试");
            } else if (info.getStatus() == 5008) {
                result_null.setText("输入的车辆信息有误，请查证后重新输入");
            } else {
                result_null.setText("恭喜, 没有查到违章记录！");
            }

            result_title.setVisibility(View.GONE);
            result_list.setVisibility(View.GONE);
            result_null.setVisibility(View.VISIBLE);
        }
    }
}
