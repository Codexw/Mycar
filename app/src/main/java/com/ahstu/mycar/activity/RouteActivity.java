package com.ahstu.mycar.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.ahstu.mycar.R;
import com.ahstu.mycar.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;

public class RouteActivity extends Activity implements OnGetRoutePlanResultListener {

    private Context mContext;
    private MapView mMapView = null;
    private BaiduMap mBaiduMap = null;
    private ImageView iv_back = null;
    private RoutePlanSearch mSearch = null;
    private RouteLine<DrivingRouteLine.DrivingStep> route = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        mContext = this;
        initView();
    }

    private void initView() {
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setVisibility(View.VISIBLE);
        iv_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mMapView = (MapView) findViewById(R.id.bmapView);
        mMapView.showScaleControl(false);
        mMapView.showZoomControls(false);
        mBaiduMap = mMapView.getMap();
        // 初始化搜索模块，注册事件监听
        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(this);

        Intent intent = getIntent();
        LatLng locLatLng = new LatLng(intent.getDoubleExtra("locLat", 0), intent.getDoubleExtra("locLon", 0));
        LatLng desLatlng = new LatLng(intent.getDoubleExtra("lat", 0), intent.getDoubleExtra("lon", 0));
        PlanNode st = PlanNode.withLocation(locLatLng);
        PlanNode en = PlanNode.withLocation(desLatlng);
        mSearch.drivingSearch(new DrivingRoutePlanOption().from(st).to(en));

    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        mMapView.onDestroy();
    }

    //步行路径
    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {

    }

    //公交路径
    @Override
    public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

    }

    //开车路径
    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {
        if (drivingRouteResult == null || drivingRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(RouteActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }
        if (drivingRouteResult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {

        }
        if (drivingRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
//            route = drivingRouteResult.getRouteLines().get(0);
            DrivingRouteOverlay overlay = new DrivingRouteOverlay(mBaiduMap);
            overlay.setData(drivingRouteResult.getRouteLines().get(0));
            overlay.addToMap();
            overlay.zoomToSpan();
        }
    }

    //自行车路径
    @Override
    public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

    }
}
