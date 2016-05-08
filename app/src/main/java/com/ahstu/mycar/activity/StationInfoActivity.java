package com.ahstu.mycar.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ahstu.mycar.R;


public class StationInfoActivity extends Activity implements OnClickListener {

    private Context mContext;
    private TextView tv_title_right, tv_name, tv_distance, tv_area, tv_addr;
    private ImageView iv_back;

    private ScrollView sv;
    private ListView lv_gast_price, lv_price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        mContext = this;
        initView();
    }

    private void initView() {
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_distance = (TextView) findViewById(R.id.tv_distance);
        tv_area = (TextView) findViewById(R.id.tv_area);
        tv_addr = (TextView) findViewById(R.id.tv_addr);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
        tv_title_right = (TextView) findViewById(R.id.tv_title_button);
        tv_title_right.setText("导航 >");
        tv_title_right.setOnClickListener(this);
        tv_title_right.setVisibility(View.VISIBLE);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setVisibility(View.VISIBLE);
        iv_back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
            }
        });

        lv_gast_price = (ListView) findViewById(R.id.lv_gast_price);
        lv_price = (ListView) findViewById(R.id.lv_price);
        sv = (ScrollView) findViewById(R.id.sv);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_title_button:
                break;
            default:
                break;
        }
    }

}
