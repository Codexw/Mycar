package com.ahstu.mycar.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ahstu.mycar.R;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;

/**
 * @author 吴天洛 2016,4,25
 */

public class MapFragment extends Fragment implements View.OnClickListener {
    private MapView mMapView = null;
    private BaiduMap mBaiduMap;
    private Button btn_map_normal;
    private Button btn_map_site;
    private Button btn_map_traffic;
    private LocationClient mlocationClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        SDKInitializer.initialize(getActivity().getApplicationContext());
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        ininView(view);
        initClick(view);
        return view;
    }

    private void initClick(View view) {
        btn_map_normal.setOnClickListener(this);
        btn_map_site.setOnClickListener(this);
        btn_map_traffic.setOnClickListener(this);
    }

    private void ininView(View view) {
        mMapView = (MapView) view.findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(17.0f);  //地图比例初始化为100M
        mBaiduMap.setMapStatus(msu);

        btn_map_normal = (Button) view.findViewById(R.id.btn_map_normal);
        btn_map_site = (Button) view.findViewById(R.id.btn_map_site);
        btn_map_traffic = (Button) view.findViewById(R.id.btn_map_traffic);
    }

    @Override
    public void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

//        在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_map_normal:
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                break;
            case R.id.btn_map_site:
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.btn_map_traffic:
                if (mBaiduMap.isTrafficEnabled()) {
                    mBaiduMap.setTrafficEnabled(false);
                    btn_map_traffic.setText("实时交通(off)");
                } else {
                    mBaiduMap.setTrafficEnabled(true);
                    btn_map_traffic.setText("实时交通(on)");
                }
                break;
        }
    }


}