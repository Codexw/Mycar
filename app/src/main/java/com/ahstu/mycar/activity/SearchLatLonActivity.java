package com.ahstu.mycar.activity;


import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.ahstu.mycar.R;
import com.ahstu.mycar.ui.MyOrientationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

public class SearchLatLonActivity extends Activity implements View.OnClickListener, OnGetGeoCoderResultListener {

    private ImageView mIvBack;
    private EditText mEditText;
    private Button btnSearch;
    private MapView mMapView = null;
    private BaiduMap mBaiduMap = null;

    //定位相关变量
    private LocationClient mLocationClient;
    private MyLocationListener myLocationListener;
    private MyLocationConfiguration.LocationMode mLocationMode;
    private double mLatitude;
    private double mLongitude;
    private boolean isFirstIn = true;
    private Marker mMaker;

    //自定义定位图标
    private BitmapDescriptor mbitmapDescriptor;
    private MyOrientationListener mMyOrientationListener;
    private float mCurrentX;

    private GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用
    private InfoWindow mInfoWindow;

    private FragmentManager manager;
    private FragmentTransaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        initView();
        initClick();
        initLocation();
    }

    private void initView() {
        mIvBack = (ImageView) findViewById(R.id.back);
        mEditText = (EditText) findViewById(R.id.et);
        btnSearch = (Button) findViewById(R.id.btn_search);

        //地图
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);  //地图比例初始化为500M
        mBaiduMap.setMapStatus(msu);

        // 初始化搜索模块，注册事件监听
        mSearch = GeoCoder.newInstance();
    }

    private void initClick() {
        mIvBack.setOnClickListener(this);
        btnSearch.setOnClickListener(this);
        mSearch.setOnGetGeoCodeResultListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.btn_search:
                // Geo搜索
                mSearch.geocode(new GeoCodeOption().city("").address(mEditText.getText().toString()));
                break;
        }
    }


    //定位初始化
    private void initLocation() {
        mLocationClient = new LocationClient(this);
        myLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(myLocationListener);

        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);
        option.setCoorType("bd09ll"); // 返回百度经纬度坐标系 ：bd09ll
        option.setScanSpan(1000);
        mLocationClient.setLocOption(option);//将上面option中的设置加载
        
      /*  mMaker = BitmapDescriptorFactory.fromResource(R.drawable.map_my_location_icon);
        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL,true,mMaker));
        //默认地图模式*/

        //初始化方向指示图标
        mbitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.map_my_location_icon);
        mMyOrientationListener = new MyOrientationListener(this);
        mMyOrientationListener.setmOnOrientationListener(new MyOrientationListener.OnOrientationListener() {
            @Override
            public void onOrientationChanged(float x) {
                mCurrentX = x;
            }
        });
        mLocationMode = MyLocationConfiguration.LocationMode.NORMAL;

    }

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
        mSearch.destroy();
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

    //Geo搜索
    @Override
    public void onGetGeoCodeResult(GeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(SearchLatLonActivity.this, "抱歉，未能找到结果", Toast.LENGTH_LONG)
                    .show();
            return;
        }

        mBaiduMap.clear();
        MarkerOptions op = new MarkerOptions().position(result.getLocation())
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_mark))
                .zIndex(9).draggable(true);
        mMaker = (Marker) mBaiduMap.addOverlay(op);
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result.getLocation()));


        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                Button button = new Button(getApplicationContext());
                button.setBackgroundResource(R.drawable.popup);
                InfoWindow.OnInfoWindowClickListener listener = null;
                if (marker == mMaker) {
                    button.setText("确定");
                    listener = new InfoWindow.OnInfoWindowClickListener() {
                        public void onInfoWindowClick() {

                            Intent intent = new Intent("com.ahstu.mycar.fragment.FindFragment");
                            intent.putExtra("intent", getIntent().getStringExtra("intent"));
                            intent.putExtra("add", mEditText.getText().toString());
                            intent.putExtra("lat", marker.getPosition().latitude);
                            intent.putExtra("lon", marker.getPosition().longitude);
                            LocalBroadcastManager.getInstance(SearchLatLonActivity.this).sendBroadcast(intent);
                            finish();

                            mBaiduMap.hideInfoWindow();
                        }
                    };
                    LatLng ll = marker.getPosition();
                    mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(button), ll, -147, listener);
                    mBaiduMap.showInfoWindow(mInfoWindow);
                }
                return true;
            }
        });

    }

    //反Geo搜索
    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
    }


    //地图定位加载是耗时的，因此采用异步加载
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null) {
                return;
            }
            MyLocationData data = new MyLocationData.Builder()//
                    .direction(mCurrentX)//
                    .accuracy(location.getRadius())//
                    .latitude(location.getLatitude())//
                    .longitude(location.getLongitude())//
                    .build();

            mBaiduMap.setMyLocationData(data);
            MyLocationConfiguration config = new MyLocationConfiguration(mLocationMode, true, mbitmapDescriptor);
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
