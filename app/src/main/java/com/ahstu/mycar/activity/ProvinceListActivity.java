package com.ahstu.mycar.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.ahstu.mycar.R;
import com.ahstu.mycar.me.ListAdapter;
import com.ahstu.mycar.me.ListModel;
import com.cheshouye.api.client.WeizhangClient;
import com.cheshouye.api.client.json.ProvinceInfoJson;

import java.util.ArrayList;
import java.util.List;

public class ProvinceListActivity extends Activity {
    private ListView lv_list;
    private ListAdapter mAdapter;
    private SwipeRefreshLayout refreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.csy_activity_citys);
        //getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.csy_titlebar);

        //标题
        TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtTitle.setText("选择查询地-省份");

        //返回按钮
        Button btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        lv_list = (ListView) findViewById(R.id.lv_1ist);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
//        refreshLayout.setColorSchemeColors(R.color.);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter = new ListAdapter(ProvinceListActivity.this, getData2());
                        lv_list.setAdapter(mAdapter);
                        refreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });
        mAdapter = new ListAdapter(this, getData2());
        lv_list.setAdapter(mAdapter);
        //给Listview添加监听器
        lv_list.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                TextView txt_name = (TextView) view.findViewById(R.id.txt_name);

                Intent intent = new Intent();
                intent.putExtra("province_name", txt_name.getText());
                intent.putExtra("province_id", txt_name.getTag().toString());

                intent.setClass(ProvinceListActivity.this, CityListActivity.class);
                startActivityForResult(intent, 20);
            }
        });
        lv_list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0) {
                    refreshLayout.setEnabled(true);
                } else {
                    refreshLayout.setEnabled(false);
                }
            }
        });

    }

    /**
     * title:获取省份信息
     *
     * @return
     */

    //得到省份，并保存在List中
    private List<ListModel> getData2() {

        List<ListModel> list = new ArrayList<ListModel>();
        List<ProvinceInfoJson> provinceList = WeizhangClient.getAllProvince();

        //开通数量提示
        TextView txtListTip = (TextView) findViewById(R.id.list_tip);
        if (provinceList != null) {
            txtListTip.setText("全国已开通" + provinceList.size() + "个省份, 其它省将陆续开放");
            for (ProvinceInfoJson provinceInfoJson : provinceList) {
                String provinceName = provinceInfoJson.getProvinceName();
                int provinceId = provinceInfoJson.getProvinceId();
                ListModel model = new ListModel();
                model.setTextName(provinceName);
                model.setNameId(provinceId);
                list.add(model);
            }
        } else {

            txtListTip.setText("服务器异常，请下拉刷新重新加载");

        }

        return list;
    }

    //获取CityActivity传递过来的数据
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null)
            return;
        Bundle bundle = data.getExtras();
        // 获取城市name和id
        String cityName = bundle.getString("city_name");
        String cityId = bundle.getString("city_id");
        Intent intent = new Intent();
        intent.putExtra("city_name", cityName);
        intent.putExtra("city_id", cityId);
        setResult(1, intent);
        finish();
    }
}

