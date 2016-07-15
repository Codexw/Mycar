package com.ahstu.mycar.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.ahstu.mycar.R;
import com.ahstu.mycar.adapter.PriceListAdapter;
import com.ahstu.mycar.bean.Station;
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
 * @author 吴天洛
 *         功能：加油站详情、导航到加油站
 */
public class StationInfoActivity extends Activity {

    public static final String ROUTE_PLAN_NODE = "routePlanNode";
    private static final String APP_FOLDER_NAME = "MyCar";
    public static List<Activity> activityList = new LinkedList<Activity>();
    String authinfo = null;
    private Context mContext;
    private TextView tv_name, tv_distance, tv_area, tv_addr;
    private ImageView iv_back;
    private Station s;
    private ScrollView sv;
    private ListView lv_gast_price, lv_price;
    private Button addgas;
    private TextView tv_bd09ll;
    private TextView tvName;
    private String mSDCardPath = null;
    private double stLat;
    private double stLon;
    private double enLat;
    private double enLon;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        activityList.add(this);
        setContentView(R.layout.activity_info);
        mContext = this;
        initView();
        setText();
        BNOuterLogUtil.setLogSwitcher(true);

        initListener();
        if (initDirs()) {
            initNavi();
        }
    }

    private void initView() {
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_distance = (TextView) findViewById(R.id.tv_distance);
        tv_area = (TextView) findViewById(R.id.tv_area);
        tv_addr = (TextView) findViewById(R.id.tv_addr);

        iv_back = (ImageView) findViewById(R.id.iv_back);
        tv_bd09ll = (TextView) findViewById(R.id.tv_bd09ll);
        tvName = (TextView) findViewById(R.id.title_name);

        tvName.setText("加油站详情");
        tv_bd09ll.setText("导航 >");
        tv_bd09ll.setVisibility(View.VISIBLE);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setVisibility(View.VISIBLE);
        iv_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        lv_gast_price = (ListView) findViewById(R.id.lv_gast_price);
        lv_price = (ListView) findViewById(R.id.lv_price);
        sv = (ScrollView) findViewById(R.id.sv);
        addgas = (Button) findViewById(R.id.btn_addgas);
        addgas.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(StationInfoActivity.this, GasorderActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("station", s);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private void setText() {
        s = getIntent().getParcelableExtra("s");
        tv_name.setText(s.getName() + " - " + s.getBrand());
        tv_addr.setText(s.getAddr());
        tv_distance.setText(s.getDistance() + "m");
        tv_area.setText(s.getArea());
        PriceListAdapter gastPriceAdapter = new PriceListAdapter(mContext, s.getGastPriceList());
        lv_gast_price.setAdapter(gastPriceAdapter);
        PriceListAdapter priceAdapter = new PriceListAdapter(mContext, s.getPriceList());
        lv_price.setAdapter(priceAdapter);
        sv.smoothScrollTo(0, 0);
    }

    private void initListener() {
        if (tv_bd09ll != null) {
            tv_bd09ll.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    if (BaiduNaviManager.isNaviInited()) {
                        routeplanToNavi();
                    }
                }
            });
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

    private void initNavi() {
        BaiduNaviManager.getInstance().init(this, mSDCardPath, APP_FOLDER_NAME, new BaiduNaviManager.NaviInitListener() {
            @Override
            public void onAuthResult(int status, String msg) {
                if (0 == status) {
//                    authinfo = "key验证成功!";
                } else {
//                    authinfo = "key验证失败" + msg;
                }
            }

            public void initSuccess() {
//                Toast.makeText(StationInfoActivity.this, "百度导航引擎初始化成功", Toast.LENGTH_SHORT).show();
            }

            public void initStart() {
//                Toast.makeText(StationInfoActivity.this, "百度导航引擎初始化开始", Toast.LENGTH_SHORT).show();
            }

            public void initFailed() {
//                Toast.makeText(StationInfoActivity.this, "百度导航引擎初始化失败", Toast.LENGTH_SHORT).show();
            }
        }, null, null, null);

    }

    private String getSdcardDir() {
        if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().toString();
        }
        return null;
    }

    private void routeplanToNavi() {
        stLat = getIntent().getDoubleExtra("locLat", 0);
        stLon = getIntent().getDoubleExtra("locLon", 0);
        enLat = s.getLat();
        enLon = s.getLon();

        BNRoutePlanNode sNode = null;
        BNRoutePlanNode eNode = null;

        sNode = new BNRoutePlanNode(stLon, stLat, null, null);
        eNode = new BNRoutePlanNode(enLon, enLat, null, null);

        if (sNode != null && eNode != null) {
            List<BNRoutePlanNode> list = new ArrayList<BNRoutePlanNode>();
            list.add(sNode);
            list.add(eNode);
            BaiduNaviManager.getInstance().launchNavigator(this, list, 1, true, new DemoRoutePlanListener(sNode));
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
                if (ac.getClass().getName().endsWith("BDStationGuideActivity")) {
                    return;
                }
            }
            Intent intent = new Intent(StationInfoActivity.this, BDStationGuideActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(ROUTE_PLAN_NODE, (BNRoutePlanNode) mBNRoutePlanNode);
            intent.putExtras(bundle);
            startActivity(intent);
        }

        @Override
        public void onRoutePlanFailed() {
            // TODO Auto-generated method stub
            Toast.makeText(StationInfoActivity.this, "算路失败", Toast.LENGTH_SHORT).show();
        }
    }

}
