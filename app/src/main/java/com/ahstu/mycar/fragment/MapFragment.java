package com.ahstu.mycar.fragment;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.ahstu.mycar.ui.MyOrientationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import static com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;

/**
 * @author 吴天洛 2016/4/25
 */

public class MapFragment extends Fragment implements OnClickListener {
    private Context mContext;
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private Button btn_map_normal;
    private Button btn_map_site;
    private Button btn_map_traffic;
    private Button btn_map_mylocation;
    private Button btn_map_mode_normal;
    private Button btn_map_mode_following;
    private Button btn_map_mode_compass;

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
    private ImageView iv_list, iv_loc;
    private Toast mToast;
    private TextView tv_title_right, tv_name, tv_distance, tv_price_a, tv_price_b;
    private LinearLayout ll_summary;
    private Dialog selectDialog, loadingDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, null);
        this.mContext = getActivity();
        ininView(view);
        initClick();
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

        //地图模式
        mLocationMode = LocationMode.NORMAL;
    }

    private void initClick() {
        btn_map_normal.setOnClickListener(this);
        btn_map_site.setOnClickListener(this);
        btn_map_traffic.setOnClickListener(this);
        btn_map_mylocation.setOnClickListener(this);
        btn_map_mode_normal.setOnClickListener(this);
        btn_map_mode_following.setOnClickListener(this);
        btn_map_mode_compass.setOnClickListener(this);

        iv_list.setOnClickListener(this);
        iv_loc.setOnClickListener(this);
        tv_title_right.setOnClickListener(this);
        ll_summary.setOnClickListener(this);

    }

    private void ininView(View view) {

        ll_summary = (LinearLayout) view.findViewById(R.id.ll_summary);
        tv_name = (TextView) view.findViewById(R.id.tv_name);
        tv_distance = (TextView) view.findViewById(R.id.tv_distance);
        tv_price_a = (TextView) view.findViewById(R.id.tv_price_a);
        tv_price_b = (TextView) view.findViewById(R.id.tv_price_b);

        //地图
        mMapView = (MapView) view.findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(17.0f);  //地图比例初始化为100M
        mBaiduMap.setMapStatus(msu);

        btn_map_normal = (Button) view.findViewById(R.id.btn_map_normal);
        btn_map_site = (Button) view.findViewById(R.id.btn_map_site);
        btn_map_traffic = (Button) view.findViewById(R.id.btn_map_traffic);
        btn_map_mylocation = (Button) view.findViewById(R.id.btn_map_mylocation);
        btn_map_mode_normal = (Button) view.findViewById(R.id.btn_map_mode_normal);
        btn_map_mode_following = (Button) view.findViewById(R.id.btn_map_mode_following);
        btn_map_mode_compass = (Button) view.findViewById(R.id.btn_map_mode_compass);

        //布局
        iv_list = (ImageView) view.findViewById(R.id.iv_list);
        iv_loc = (ImageView) view.findViewById(R.id.iv_loc);
        tv_title_right = (TextView) view.findViewById(R.id.tv_title_button);
        tv_title_right.setText("3km" + " >");
        tv_title_right.setVisibility(View.VISIBLE);
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


    /**
     * dialog点击事件
     * 选择附近加油站的距离，点击的view
     */
    public void onDialogClick(View v) {
        switch (v.getId()) {
            case R.id.bt_3km:
                System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
                break;
            case R.id.bt_5km:
                break;
            case R.id.bt_8km:
                break;
            case R.id.bt_10km:
                break;
            default:
                break;
        }
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
            case R.id.btn_map_mode_normal:
                mLocationMode = LocationMode.NORMAL;
                break;
            case R.id.btn_map_mode_following:
                mLocationMode = LocationMode.FOLLOWING;
                break;
            case R.id.btn_map_mode_compass:
                mLocationMode = LocationMode.COMPASS;
                break;

            case R.id.iv_list:
                break;
            case R.id.iv_loc:
                break;
            case R.id.tv_title_button:
                showSelectDialog();
                break;
            case R.id.ll_summary:
                break;
            default:
                break;
        }
    }

    /**
     * 显示范围选择dialog
     */
    private void showSelectDialog() {
        if (selectDialog != null) {
            selectDialog.show();

            return;
        }
        selectDialog = new Dialog(mContext, R.style.dialog);
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_distance, null);
        System.out.println("222222222222222222222222" + view);

        selectDialog.setContentView(view, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT));
        selectDialog.setCanceledOnTouchOutside(true);
        selectDialog.show();
        onDialogClick(this.getView());
    }

    private void showLoadingDialog() {
        if (loadingDialog != null) {
            loadingDialog.show();
            return;
        }
        loadingDialog = new Dialog(mContext, R.style.dialog_loading);
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_loading, null);
        loadingDialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.FILL_PARENT));
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
            mToast = Toast.makeText(mContext, msg, Toast.LENGTH_SHORT);
        }
        mToast.setText(msg);
        mToast.show();
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