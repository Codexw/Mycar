package com.ahstu.mycar.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ahstu.mycar.R;
import com.ahstu.mycar.ui.MyOrientationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

/**
 * @author 吴天洛 2016/4/25
 */

public class MapFragment extends Fragment implements View.OnClickListener {
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private Button btn_map_normal;
    private Button btn_map_site;
    private Button btn_map_traffic;
    private Button btn_map_mylocation;

    //定位相关变量
    private LocationClient mLocationClient;
    private MyLocationListener myLocationListener;
    private boolean isFirstIn = true;
    private double mLatitude;
    private double mLongitude;

    //自定义定位图标
    private BitmapDescriptor mbitmapDescriptor;
    private MyOrientationListener mMyOrientationListener;
    private float mCurrentX;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        SDKInitializer.initialize(getActivity().getApplicationContext());
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        ininView(view);
        initClick(view);
        initLocation();
        return view;
    }

    //定位初始化
    private void initLocation() {
        mLocationClient = new LocationClient(getActivity());
        myLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(myLocationListener);

        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");
        option.setIsNeedAddress(true);
        option.setOpenGps(true);
        option.setScanSpan(1000);
        mLocationClient.setLocOption(option);//将上面option中的设置加载
        //初始化图标
        mbitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.map_my_location_icon);
        mMyOrientationListener = new MyOrientationListener(getActivity());
        mMyOrientationListener.setmOnOrientationListener(new MyOrientationListener.OnOrientationListener() {
            @Override
            public void onOrientationChanged(float x) {
                mCurrentX = x;
            }
        });
    }

    private void initClick(View view) {
        btn_map_normal.setOnClickListener(this);
        btn_map_site.setOnClickListener(this);
        btn_map_traffic.setOnClickListener(this);
        btn_map_mylocation.setOnClickListener(this);
    }

    private void ininView(View view) {
        mMapView = (MapView) view.findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(17.0f);  //地图比例初始化为100M
        mBaiduMap.setMapStatus(msu);

        btn_map_normal = (Button) view.findViewById(R.id.btn_map_normal);
        btn_map_site = (Button) view.findViewById(R.id.btn_map_site);
        btn_map_traffic = (Button) view.findViewById(R.id.btn_map_traffic);
        btn_map_mylocation = (Button) view.findViewById(R.id.btn_map_mylocation);
    }

    //软件开启时判断滴入是否打开，没有打开，则打开
    @Override
    public void onStart() {
        super.onStart();
        //开启定位
        mBaiduMap.setMyLocationEnabled(true);
        if (!mLocationClient.isStarted()) {
            mLocationClient.start();
        }
        //开启方向传感器
        mMyOrientationListener.start();
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

    //退出程序时关闭地图
    @Override
    public void onStop() {
        super.onStop();
        //停止地图定位
        mBaiduMap.setMyLocationEnabled(false);
        mLocationClient.stop();

        //停止方向传感器
        mMyOrientationListener.stop();
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
            case R.id.btn_map_mylocation:
                LatLng latLng = new LatLng(mLatitude, mLongitude);
                MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
                mBaiduMap.animateMapStatus(msu);
                break;
        }
    }

    //地图加载是耗时的，因此采用异步加载
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            MyLocationData data = new MyLocationData.Builder()//
                    .direction(mCurrentX)//
                    .accuracy(location.getRadius())//
                    .latitude(location.getLatitude())//
                    .longitude(location.getLongitude())//
                    .build();

            mBaiduMap.setMyLocationData(data);
            MyLocationConfiguration config = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, mbitmapDescriptor);
            mBaiduMap.setMyLocationConfigeration(config);
            mLatitude = location.getLatitude();
            mLongitude = location.getLongitude();

            if (isFirstIn) {
                LatLng latLng = new LatLng(mLatitude, mLongitude);
                MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
                mBaiduMap.animateMapStatus(msu);
                isFirstIn = false;
            }

        }
    }

}