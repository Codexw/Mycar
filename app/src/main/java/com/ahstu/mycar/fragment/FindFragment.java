package com.ahstu.mycar.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ahstu.mycar.R;
import com.ahstu.mycar.activity.SearchMapActivity;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRoutePlanOption;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;

/**
 * @author 吴天洛 2016/4/25
 */
public class FindFragment extends Fragment implements OnGetRoutePlanResultListener, View.OnClickListener {
    RouteLine route = null;
    RoutePlanSearch mSearch = null;
    private EditText editSt;
    private EditText editEn;
    private Button drive;
    private Button transit;
    private Button walk;
    private Button bike;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initClick();
        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_find, null);
        return view;
    }

    private void initClick() {
        drive.setOnClickListener(this);
        transit.setOnClickListener(this);
        walk.setOnClickListener(this);
        bike.setOnClickListener(this);
    }

    private void initView() {
        editSt = (EditText) getActivity().findViewById(R.id.start);
        editEn = (EditText) getActivity().findViewById(R.id.end);
        drive = (Button) getActivity().findViewById(R.id.drive);
        transit = (Button) getActivity().findViewById(R.id.transit);
        walk = (Button) getActivity().findViewById(R.id.walk);
        bike = (Button) getActivity().findViewById(R.id.bike);
    }

    @Override
    public void onClick(View v) {
        route = null;
        // 设置起终点信息，对于tranist search 来说，城市名无意义 
        PlanNode stNode = PlanNode.withCityNameAndPlaceName("北京", editSt.getText().toString());
        PlanNode enNode = PlanNode.withCityNameAndPlaceName("北京", editEn.getText().toString());

        // 实际使用中对起点终点城市进行正确的设定
        switch (v.getId()) {
            case R.id.drive:
                mSearch.drivingSearch((new DrivingRoutePlanOption()).from(stNode).to(enNode));
                break;
            case R.id.transit:
                mSearch.transitSearch((new TransitRoutePlanOption()).from(stNode).city("北京").to(enNode));
                break;
            case R.id.walk:
                mSearch.walkingSearch((new WalkingRoutePlanOption()).from(stNode).to(enNode));
                break;
            case R.id.bike:
                mSearch.bikingSearch((new BikingRoutePlanOption()).from(stNode).to(enNode));
                break;
        }
    }

    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(getActivity(), "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }

        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            // result.getSuggestAddrInfo()
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            route = result.getRouteLines().get(0);
            Intent i = new Intent(getActivity(), SearchMapActivity.class);
            Bundle b = new Bundle();
            b.putInt("key", 1);
            b.putParcelable("route", route);
            b.putParcelable("result", result);
            i.putExtras(b);
            startActivity(i);
        }
    }

    @Override
    public void onGetTransitRouteResult(TransitRouteResult result) {

        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(getActivity(), "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            // result.getSuggestAddrInfo()
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            route = result.getRouteLines().get(0);
            Intent i = new Intent(getActivity(), SearchMapActivity.class);
            Bundle b = new Bundle();
            b.putInt("key", 2);
            b.putParcelable("route", route);
            b.putParcelable("result", result);
            i.putExtras(b);
            startActivity(i);
        }
    }

    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(getActivity(), "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            // result.getSuggestAddrInfo()
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            route = result.getRouteLines().get(0);
            Intent i = new Intent(getActivity(), SearchMapActivity.class);
            Bundle b = new Bundle();
            b.putInt("key", 3);
            b.putParcelable("route", route);
            b.putParcelable("result", result);
            i.putExtras(b);
            startActivity(i);
        }
    }

    @Override
    public void onGetBikingRouteResult(BikingRouteResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(getActivity(), "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            System.out.println(result.getSuggestAddrInfo());
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            route = result.getRouteLines().get(0);
            Intent i = new Intent(getActivity(), SearchMapActivity.class);
            Bundle b = new Bundle();
            b.putInt("key", 4);
            b.putParcelable("route", route);
            b.putParcelable("result", result);
            i.putExtras(b);
            startActivity(i);
        }
    }
}

