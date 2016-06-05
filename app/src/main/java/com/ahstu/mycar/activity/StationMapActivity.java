package com.ahstu.mycar.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ahstu.mycar.R;
import com.ahstu.mycar.bean.Price;
import com.ahstu.mycar.bean.Station;
import com.ahstu.mycar.ui.MyOrientationListener;
import com.ahstu.mycar.util.StationData;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 吴天洛 2016/4/25
 *         功能:加油站
 */

public class StationMapActivity extends Activity implements OnClickListener {
    private MapView mMapView;
    private BaiduMap mBaiduMap;

    //定位相关变量
    private LocationClient mLocationClient;
    private MyLocationListener myLocationListener;
    private boolean isFirstIn = true;
    private double mLatitude;
    private double mLongitude;
    private LocationMode mLocationMode;

    //自定义定位图标
    private BitmapDescriptor mbitmapDescriptor;
    private MyOrientationListener mMyOrientationListener;
    private float mCurrentX;

    //加油站相关变量
    private Button btn_map_search_station;
    private ImageView stop;
    private ImageView iv_list;
    private Toast mToast;
    private TextView tv_name, tv_distance, tv_price_a, tv_price_b;
    private LinearLayout ll_summary;
    private Dialog loadingDialog;
    private Marker mLastMaker;
    private ArrayList<Station> mList;
    private Station mStation = null;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case 0x01:
                    mList = (ArrayList<Station>) msg.obj;
                    setMarker(mList);
                    showLayoutInfo("1", mList.get(0));
                    loadingDialog.dismiss();
                    break;
                case 0x02:
                    loadingDialog.dismiss();
                    showToast(String.valueOf(msg.obj));
                    break;
                default:
                    break;
            }
        }
    };
    private StationData mStationData;
    private int mDistance = 80000;
    private BDLocation loc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_map);
        mStationData = new StationData(mHandler);
        ininView();
        initClick();
        initLocation();
    }

    private void ininView() {
        ll_summary = (LinearLayout) findViewById(R.id.ll_summary);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_distance = (TextView) findViewById(R.id.tv_distance);
        tv_price_a = (TextView) findViewById(R.id.tv_price_a);
        tv_price_b = (TextView) findViewById(R.id.tv_price_b);

        //地图
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);  //地图比例初始化为500M
        mBaiduMap.setMapStatus(msu);


        //加油站布局
        iv_list = (ImageView) findViewById(R.id.iv_list);
        btn_map_search_station = (Button) findViewById(R.id.btn_map_search_station);
        stop = (ImageView) findViewById(R.id.stop);

    }

    private void initClick() {
        iv_list.setOnClickListener(this);
        ll_summary.setOnClickListener(this);
        btn_map_search_station.setOnClickListener(this);
        stop.setOnClickListener(this);
    }

    //定位初始化
    private void initLocation() {
        mLocationClient = new LocationClient(this);
        myLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(myLocationListener);

        LocationClientOption option = new LocationClientOption();

        // 返回国测局经纬度坐标系：gcj02 返回百度墨卡托坐标系 ：bd09
        // 返回百度经纬度坐标系 ：bd09ll
        option.setCoorType("bd09ll");
        option.setIsNeedAddress(true); // 设置是否需要地址信息，默认为无地址
        option.setOpenGps(true);// 设置扫描间隔，单位毫秒，当<1000(1s)时，定时定位无效
        option.setScanSpan(1000);
        mLocationClient.setLocOption(option);//将上面option中的设置加载

        //初始化方向指示图标
//        mbitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.map_my_location_icon);  //自定义方向图标
        mMyOrientationListener = new MyOrientationListener(this);
        mMyOrientationListener.setmOnOrientationListener(new MyOrientationListener.OnOrientationListener() {
            @Override
            public void onOrientationChanged(float x) {
                mCurrentX = x;
            }
        });

        //默认地图模式
        mLocationMode = LocationMode.NORMAL;
    }

    //设置地图中的加油站位置
    public void setMarker(ArrayList<Station> list) {
        View view = LayoutInflater.from(this).inflate(R.layout.marker, null);
        final TextView tv = (TextView) view.findViewById(R.id.tv_marker);

        for (int i = 0; i < list.size(); i++) {
            Station s = list.get(i);
            tv.setText((i + 1) + "");
            if (i == 0) {
                tv.setBackgroundResource(R.drawable.icon_focus_mark);
            } else {
                tv.setBackgroundResource(R.drawable.icon_mark);
            }

            BitmapDescriptor mBitmap = BitmapDescriptorFactory.fromView(tv);
            LatLng mLatLng = new LatLng(s.getLat(), s.getLon());
            Bundle b = new Bundle();
            b.putParcelable("s", list.get(i));
            MarkerOptions mMarkerOptions = new MarkerOptions().position(mLatLng).icon(mBitmap).title((i + 1) + "").extraInfo(b);
            //掉下动画
            mMarkerOptions.animateType(MarkerOptions.MarkerAnimateType.drop);
            if (i == 0) {
                mLastMaker = (Marker) mBaiduMap.addOverlay(mMarkerOptions);
                mStation = s;
                showLayoutInfo((i + 1) + "", mStation);
            } else {
                mBaiduMap.addOverlay(mMarkerOptions);
            }
        }

        //点击地图中的加油站图标时，做出相应的响应
        mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                //没被选中的maker
                if (mLastMaker != null) {
                    tv.setText(mLastMaker.getTitle());
                    tv.setBackgroundResource(R.drawable.icon_mark);
                    BitmapDescriptor bitmap = BitmapDescriptorFactory.fromView(tv);
                    mLastMaker.setIcon(bitmap);
                }

                //点击后的maker
                mLastMaker = marker;
                String position = marker.getTitle();
                tv.setText(position);
                tv.setBackgroundResource(R.drawable.icon_focus_mark);
                BitmapDescriptor bitmap = BitmapDescriptorFactory.fromView(tv);
                marker.setIcon(bitmap);
                mStation = marker.getExtraInfo().getParcelable("s");
                showLayoutInfo(position, mStation);
                return false;
            }
        });
    }

    /**
     * 显示加油站加载的信息
     *
     * @param position
     * @param s
     */
    public void showLayoutInfo(String position, Station s) {
        tv_name.setText(position + "." + s.getName());
        tv_distance.setText(s.getDistance() + "");
        List<Price> list = s.getGastPriceList();

        if (list != null && list.size() > 0) {
            tv_price_a.setText(list.get(0).getType() + " " + list.get(0).getPrice());
            if (list.size() > 1) {
                tv_price_b.setText(list.get(1).getType() + " " + list.get(1).getPrice());
            }
        }
        ll_summary.setVisibility(View.VISIBLE);
    }

    public void searchStation(double lat, double lon, int distance) {
        showLoadingDialog();
        mBaiduMap.clear();
        ll_summary.setVisibility(View.GONE);
        mStationData.getStationData(lat, lon, distance);
    }


    //软件开启时判断地图是否打开，没有打开，则打开
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
    public void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
        mHandler = null;
    }

    //按钮动画点击监听事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_map_search_station:
                searchStation(loc.getLatitude(), loc.getLongitude(), mDistance);
                break;

            case R.id.stop:
                mBaiduMap.clear();
                mLastMaker = null;
                ll_summary.setVisibility(View.GONE);
                finish();
                break;

            case R.id.ll_summary:
                Intent intent = new Intent(this, StationInfoActivity.class);
                intent.putExtra("s", mStation);
                intent.putExtra("locLat", loc.getLatitude());
                intent.putExtra("locLon", loc.getLongitude());
                startActivity(intent);
                break;

            case R.id.iv_list:
                Intent listIntent = new Intent(this, StationListActivity.class);
                listIntent.putParcelableArrayListExtra("list", mList);
                listIntent.putExtra("locLat", loc.getLatitude());
                listIntent.putExtra("locLon", loc.getLongitude());
                startActivity(listIntent);
                break;
        }

    }

    //加油站正在加载。。。
    @SuppressLint("InflateParams")
    private void showLoadingDialog() {
        if (loadingDialog != null) {
            loadingDialog.show();
            return;
        }
        loadingDialog = new Dialog(this, R.style.dialog_loading);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_loading, null);
        loadingDialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
        loadingDialog.setCancelable(false);
        loadingDialog.show();
    }

    /**
     * 显示通知
     *
     * @param msg
     */
    private void showToast(String msg) {
        if (mToast == null) {
            mToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        }
        mToast.setText(msg);
        mToast.show();
    }

    //地图定位加载是耗时的，因此采用异步加载
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null) {
                return;
            }
            loc = location;
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

            System.out.println(loc.getLatitude() + "aaa" + loc.getLongitude() + "bbbbbbbbbbb" + mLatitude + "ccccccccccccc" + mLongitude);
            if (isFirstIn) {
                LatLng latLng = new LatLng(mLatitude, mLongitude);
                MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
                mBaiduMap.animateMapStatus(msu);
                isFirstIn = false;
            }
        }
    }

}
