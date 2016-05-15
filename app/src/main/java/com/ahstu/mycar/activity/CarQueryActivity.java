package com.ahstu.mycar.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ahstu.mycar.R;
import com.cheshouye.api.client.WeizhangClient;
import com.cheshouye.api.client.WeizhangIntentService;
import com.cheshouye.api.client.json.CarInfo;
import com.cheshouye.api.client.json.InputConfigJson;

/**
 * Created by xuning on 2016/5/3.
 */
public class CarQueryActivity extends Activity {
    TextView chaxundi;
    String chepainumber = "浙C6RH69";
    String engine_number = "8300861-3F1";
    String chejia_number = "LKCBACAB88H011922";
    String s = "";
    Button btn_query;
    TextView chepai;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.che_query);
        //初始化违章查询的服务
        Intent weizhangIntent = new Intent(this, WeizhangIntentService.class);
        weizhangIntent.putExtra("appId", 1702);// 您的appId
        weizhangIntent.putExtra("appKey", "62fdfbce0fd98c355994140f850d2353");// 您的appKey
        startService(weizhangIntent);
        chaxundi = (TextView) findViewById(R.id.chaxundi);
        chepai = (TextView) findViewById(R.id.chepai);
        chepai.setText(chepainumber);
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
                    //int registno = inputConfig.getRegistno();
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


                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("carInfo", car);
                    intent.putExtras(bundle);
                    intent.setClass(CarQueryActivity.this, WeizhangResultActivity.class);
                    startActivity(intent);
                    finish();


                }
            }
        });

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
}
