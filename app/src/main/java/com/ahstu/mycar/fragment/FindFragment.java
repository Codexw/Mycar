package com.ahstu.mycar.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ahstu.mycar.R;
import com.ahstu.mycar.activity.BDSearchGuideActivity;
import com.ahstu.mycar.activity.SearchLatLonActivity;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.navisdk.adapter.BNOuterLogUtil;
import com.baidu.navisdk.adapter.BNOuterTTSPlayerCallback;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BNaviSettingManager;
import com.baidu.navisdk.adapter.BaiduNaviManager;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author 吴天洛 2016/4/25
 */
public class FindFragment extends Fragment implements View.OnClickListener {
    private static final String ACTION = "aaa";
    private TextView mTvSt;
    private TextView mTvEn;
    private ImageView mIvChangeStEn;
    private double stLat = 0.0;
    private double stLon = 0.0;
    private double enLat = 0.0;
    private double enLon = 0.0;

    //定位相关变量
    private LocationClient mLocationClient = null;
    private MyLocationListener myLocationListener;
    private double mLatitude;
    private double mLongitude;

    public static List<Activity> activityList = new LinkedList<Activity>();
    public static final String ROUTE_PLAN_NODE = "routePlanNode";
    private static final String APP_FOLDER_NAME = "MyCar";
    private Button mBtnSearch = null;
    private String mSDCardPath = null;

    //广播变量
    private LocalBroadcastManager broadcastManager1;
    private LocalBroadcastManager broadcastManager2;
    private IntentFilter intentFilter1;
    private IntentFilter intentFilter2;
    private BroadcastReceiver mItemViewListClickReceiver1;
    private BroadcastReceiver mItemViewListClickReceiver2;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initLocation();
        initClick();

        //获取到经纬度后传入路线规划，开始导航
        activityList.add(getActivity());
        BNOuterLogUtil.setLogSwitcher(true);

        if (initDirs()) {
            initNavi();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_find, null);
        return view;
    }

    private void initView() {
        mTvSt = (TextView) getActivity().findViewById(R.id.start);
        mTvEn = (TextView) getActivity().findViewById(R.id.end);
        mIvChangeStEn = (ImageView) getActivity().findViewById(R.id.changeStartEnd);
        mBtnSearch = (Button) getActivity().findViewById(R.id.btn_search);

        //广播
        broadcastManager1 = LocalBroadcastManager.getInstance(getActivity());
        broadcastManager2 = LocalBroadcastManager.getInstance(getActivity());
        intentFilter1 = new IntentFilter();
        intentFilter2 = new IntentFilter();
        intentFilter1.addAction("com.ahstu.mycar.fragment.FindFragment");
        intentFilter2.addAction("com.ahstu.mycar.fragment.FindFragment");
    }

    private void initClick() {
        mTvSt.setOnClickListener(this);
        mTvEn.setOnClickListener(this);
        mIvChangeStEn.setOnClickListener(this);
        mBtnSearch.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start:
                Intent intent1 = new Intent(getActivity(), SearchLatLonActivity.class);
                intent1.putExtra("intent", "start");
                startActivity(intent1);

                //Geo查询结果广播回来
                mItemViewListClickReceiver1 = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        if (intent.getStringExtra("intent").equals("start")) {
                            stLat = intent.getDoubleExtra("lat", stLat);
                            stLon = intent.getDoubleExtra("lon", stLon);
                            if (!intent.getStringExtra("add").isEmpty()) {
                                mTvSt.setText(intent.getStringExtra("add"));
                            }
                        }
                    }
                };
                broadcastManager1.registerReceiver(mItemViewListClickReceiver1, intentFilter1);
                break;
            case R.id.end:
                Intent intent2 = new Intent(getActivity(), SearchLatLonActivity.class);
                intent2.putExtra("intent", "end");
                startActivity(intent2);

                //Geo查询结果广播回来
                mItemViewListClickReceiver2 = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        if (intent.getStringExtra("intent").equals("end")) {
                            enLat = intent.getDoubleExtra("lat", enLat);
                            enLon = intent.getDoubleExtra("lon", enLon);
                            if (!intent.getStringExtra("add").isEmpty()) {
                                mTvEn.setText(intent.getStringExtra("add"));
                            }
                        }
                    }
                };
                broadcastManager2.registerReceiver(mItemViewListClickReceiver2, intentFilter2);
                break;

            case R.id.changeStartEnd:
                double changeLat;
                double changeLon;
                String str1;
                String str2;

                changeLat = stLat;
                changeLon = stLon;
                str1 = mTvSt.getText().toString();
                str2 = mTvEn.getText().toString();

                stLat = enLat;
                stLon = enLon;
                enLat = changeLat;
                enLon = changeLon;

                if (str2.isEmpty()) {
                    mTvSt.setText("");
                    mTvSt.setHint("输入起点");
                } else {
                    mTvSt.setText(str2);
                }

                if (str1.isEmpty()) {
                    mTvEn.setText("");
                    mTvEn.setHint("输入终点");
                } else {
                    mTvEn.setText(str1);
                }

                break;

            case R.id.btn_search:
                broadcastManager1.unregisterReceiver(mItemViewListClickReceiver1);
                broadcastManager2.unregisterReceiver(mItemViewListClickReceiver2);
                String str11 = mTvSt.getText().toString();
                String str22 = mTvEn.getText().toString();

                if (str11.isEmpty()) {
                    Toast.makeText(getActivity(), "请输入起点", Toast.LENGTH_SHORT).show();
                }
                if (str11.equals("当前位置")) {
                    stLat = mLatitude;
                    stLon = mLongitude;
                }
                if (str22.isEmpty()) {
                    Toast.makeText(getActivity(), "请输入终点", Toast.LENGTH_SHORT).show();
                }
                if (str22.equals("当前位置")) {
                    enLat = mLatitude;
                    enLon = mLongitude;
                }
                if (!str11.isEmpty() && !str22.isEmpty()) {
                    if (BaiduNaviManager.isNaviInited()) {
                        System.out.println(stLat + "aaa" + stLon + "bbb" + enLat + "ccc" + enLon);
                        routeplanToNavi();
                    }
                }
                break;
        }

    }

    //定位初始化
    private void initLocation() {
        mLocationClient = new LocationClient(getActivity());
        myLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(myLocationListener);

        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll"); // 返回百度经纬度坐标系 ：bd09ll
        option.setIsNeedAddress(true); // 设置是否需要地址信息，默认为无地址
        option.setOpenGps(true);
        option.setScanSpan(1000);// 设置扫描间隔，单位毫秒，当<1000(1s)时，定时定位无效
        mLocationClient.setLocOption(option);//将上面option中的设置加载

    }

    @Override
    public void onStart() {
        super.onStart();
        //开启定位
        if (!mLocationClient.isStarted()) {
            mLocationClient.start();
        }
    }

    //退出程序时关闭定位
    @Override
    public void onStop() {
        super.onStop();
        //停止地图定位
        mLocationClient.stop();
    }


    //地图定位加载是耗时的，因此采用异步加载
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null) {
                return;
            }
            mLatitude = location.getLatitude();
            mLongitude = location.getLongitude();
        }
    }

    private boolean initDirs() {
        mSDCardPath = getSdcardDir();
        if (mSDCardPath == null) {
            return false;
        }
        File f = new File(mSDCardPath, APP_FOLDER_NAME);
        if (!f.exists()) {
            try {
                f.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    String authinfo = null;

    /**
     * 内部TTS播报状态回传handler
     */
    private Handler ttsHandler = new Handler() {
        public void handleMessage(Message msg) {
            int type = msg.what;
            switch (type) {
                case BaiduNaviManager.TTSPlayMsgType.PLAY_START_MSG: {
                    showToastMsg("Handler : TTS play start");
                    break;
                }
                case BaiduNaviManager.TTSPlayMsgType.PLAY_END_MSG: {
                    showToastMsg("Handler : TTS play end");
                    break;
                }
                default:
                    break;
            }
        }
    };

    /**
     * 内部TTS播报状态回调接口
     */
    private BaiduNaviManager.TTSPlayStateListener ttsPlayStateListener = new BaiduNaviManager.TTSPlayStateListener() {

        @Override
        public void playEnd() {
            // showToastMsg("TTSPlayStateListener : TTS play end");
        }

        @Override
        public void playStart() {
            // showToastMsg("TTSPlayStateListener : TTS play start");
        }
    };

    public void showToastMsg(final String msg) {
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initNavi() {

        BNOuterTTSPlayerCallback ttsCallback = null;

        BaiduNaviManager.getInstance().init(getActivity(), mSDCardPath, APP_FOLDER_NAME, new BaiduNaviManager.NaviInitListener() {
            @Override
            public void onAuthResult(int status, String msg) {
                if (0 == status) {
                    authinfo = "key验证成功!";
                } else {
                    authinfo = "key验证失败" + msg;
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), authinfo, Toast.LENGTH_LONG).show();
                    }
                });
            }

            public void initSuccess() {
                Toast.makeText(getActivity(), "百度导航引擎初始化成功", Toast.LENGTH_SHORT).show();
            }

            public void initStart() {
                Toast.makeText(getActivity(), "百度导航引擎初始化开始", Toast.LENGTH_SHORT).show();
            }

            public void initFailed() {
                Toast.makeText(getActivity(), "百度导航引擎初始化失败", Toast.LENGTH_SHORT).show();
            }
        }, null, ttsHandler, null);

    }

    private String getSdcardDir() {
        if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().toString();
        }
        return null;
    }

    private void routeplanToNavi() {
        BNRoutePlanNode sNode = null;
        BNRoutePlanNode eNode = null;

        sNode = new BNRoutePlanNode(stLon, stLat, null, null);
        eNode = new BNRoutePlanNode(enLon, enLat, null, null);


        if (sNode != null && eNode != null) {
            List<BNRoutePlanNode> list = new ArrayList<BNRoutePlanNode>();
            list.add(sNode);
            list.add(eNode);
            BaiduNaviManager.getInstance().launchNavigator(getActivity(), list, 1, true, new DemoRoutePlanListener(sNode));
        }
    }

    public class DemoRoutePlanListener implements BaiduNaviManager.RoutePlanListener {

        private BNRoutePlanNode mBNRoutePlanNode = null;

        public DemoRoutePlanListener(BNRoutePlanNode node) {
            mBNRoutePlanNode = node;
        }

        @Override
        public void onJumpToNavigator() {
            /*
             * 设置途径点以及resetEndNode会回调该接口
             */

            for (Activity ac : activityList) {

                if (ac.getClass().getName().endsWith("BDSearchGuideActivity")) {

                    return;
                }
            }
            Intent intent = new Intent(getActivity(), BDSearchGuideActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(ROUTE_PLAN_NODE, mBNRoutePlanNode);
            intent.putExtras(bundle);
            startActivity(intent);
        }

        @Override
        public void onRoutePlanFailed() {
            // TODO Auto-generated method stub
            Toast.makeText(getActivity(), "算路失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void initSetting() {
        BNaviSettingManager.setDayNightMode(BNaviSettingManager.DayNightMode.DAY_NIGHT_MODE_DAY);
        BNaviSettingManager
                .setShowTotalRoadConditionBar(BNaviSettingManager.PreViewRoadCondition.ROAD_CONDITION_BAR_SHOW_ON);
        BNaviSettingManager.setVoiceMode(BNaviSettingManager.VoiceMode.Veteran);
        BNaviSettingManager.setPowerSaveMode(BNaviSettingManager.PowerSaveMode.DISABLE_MODE);
        BNaviSettingManager.setRealRoadCondition(BNaviSettingManager.RealRoadCondition.NAVI_ITS_ON);
    }

    private BNOuterTTSPlayerCallback mTTSCallback = new BNOuterTTSPlayerCallback() {

        @Override
        public void stopTTS() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "stopTTS");
        }

        @Override
        public void resumeTTS() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "resumeTTS");
        }

        @Override
        public void releaseTTSPlayer() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "releaseTTSPlayer");
        }

        @Override
        public int playTTSText(String speech, int bPreempt) {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "playTTSText" + "_" + speech + "_" + bPreempt);

            return 1;
        }

        @Override
        public void phoneHangUp() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "phoneHangUp");
        }

        @Override
        public void phoneCalling() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "phoneCalling");
        }

        @Override
        public void pauseTTS() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "pauseTTS");
        }

        @Override
        public void initTTSPlayer() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "initTTSPlayer");
        }

        @Override
        public int getTTSState() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "getTTSState");
            return 1;
        }
    };
}

