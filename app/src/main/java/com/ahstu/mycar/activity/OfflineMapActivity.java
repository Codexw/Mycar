package com.ahstu.mycar.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ahstu.mycar.R;
import com.ahstu.mycar.bean.OfflineMapCityBean;
import com.baidu.mapapi.map.offline.MKOLSearchRecord;
import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.baidu.mapapi.map.offline.MKOfflineMapListener;

import java.util.ArrayList;

public class OfflineMapActivity extends Activity implements MKOfflineMapListener {

    private MKOfflineMap mOffline = null;
    private Context mContext;
    private LayoutInflater mInflater;
    private int cityDownId;
    /**
     * 已下载的离线地图信息列表
     */
    private ArrayList<MKOLUpdateElement> localMapList = null;
    private ArrayList<OfflineMapCityBean> allCitiesBean = null;
    private ArrayList<OfflineMapCityBean> hotCitiesBean = null;
    private LocalMapAdapter lAdapter = null;
    private OfflineCityAdapter hAdapter = null;
    private OfflineCityAdapter aAdapter = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offlinemap);
        mContext = this;
        mOffline = new MKOfflineMap();
        mOffline.init(this);
        initView();
        mInflater = LayoutInflater.from(this);
        findViewById(R.id.offlineback).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
        // 获取已下载的离线地图信息
        localMapList = mOffline.getAllUpdateInfo();
        if (localMapList == null) {
            localMapList = new ArrayList<MKOLUpdateElement>();
        }
        ListView localMapListView = (ListView) findViewById(R.id.localmaplist);
        lAdapter = new LocalMapAdapter();
        localMapListView.setAdapter(lAdapter);


        // 获取热闹城市列表
//        hotCitiesBean = new ArrayList<OfflineMapCityBean>();
//        ArrayList<MKOLSearchRecord> records1 = mOffline.getHotCityList();
//        for (MKOLSearchRecord record : records1) {
//            OfflineMapCityBean cityBean = new OfflineMapCityBean();
//            cityBean.setCityName(record.cityName);
//            cityBean.setCityCode(record.cityID);
//            cityBean.setSize(record.size);
//            if (localMapList != null)//有下载记录则查找进度
//            {
//                for (MKOLUpdateElement ele : localMapList) {
//                    if (ele.cityID == record.cityID) {
//                        cityBean.setProgress(ele.ratio);
//                    }
//                }
//            }
//            hotCitiesBean.add(cityBean);
//        }
//        ListView hotCityList = (ListView) findViewById(R.id.hotcitylist);
//        hAdapter = new OfflineCityAdapter(hotCitiesBean);
//        hotCityList.setAdapter(hAdapter);


        // 获取所有支持离线地图的城市
        allCitiesBean = new ArrayList<OfflineMapCityBean>();
        ArrayList<MKOLSearchRecord> records2 = mOffline.getOfflineCityList();

//        conn:
        for (MKOLSearchRecord record : records2) {
//            for (OfflineMapCityBean bean : hotCitiesBean) {
//                if (bean.getCityCode() == record.cityID)
//                    continue conn;
//            }
            OfflineMapCityBean cityBean = new OfflineMapCityBean();
            cityBean.setCityName(record.cityName);
            cityBean.setCityCode(record.cityID);
            cityBean.setSize(record.size);
            if (localMapList != null)//有下载记录则查找进度
            {
                for (MKOLUpdateElement ele : localMapList) {
                    if (ele.cityID == record.cityID) {
                        cityBean.setProgress(ele.ratio);
                    }
                }
            }
            allCitiesBean.add(cityBean);
        }
        ListView allCityList = (ListView) findViewById(R.id.allcitylist);
        aAdapter = new OfflineCityAdapter();
        allCityList.setAdapter(aAdapter);

        LinearLayout cl = (LinearLayout) findViewById(R.id.citylist_layout);
        LinearLayout lm = (LinearLayout) findViewById(R.id.localmap_layout);
        lm.setVisibility(View.GONE);
        cl.setVisibility(View.VISIBLE);
    }

    /**
     * 切换至城市列表
     */
    public void clickCityListButton(View view) {
        dataChange();
        updateView();
        LinearLayout cl = (LinearLayout) findViewById(R.id.citylist_layout);
        LinearLayout lm = (LinearLayout) findViewById(R.id.localmap_layout);
        lm.setVisibility(View.GONE);
        cl.setVisibility(View.VISIBLE);

    }

    /**
     * 切换至下载管理列表
     */
    public void clickLocalMapListButton(View view) {
        dataChange();
        updateView();
        LinearLayout cl = (LinearLayout) findViewById(R.id.citylist_layout);
        LinearLayout lm = (LinearLayout) findViewById(R.id.localmap_layout);
        lm.setVisibility(View.VISIBLE);
        cl.setVisibility(View.GONE);
    }

    /**
     * 搜索离线城市
     *
     * @param view
     */
//    public void search(View view) {
//        ArrayList<MKOLSearchRecord> records = mOffline.searchCity(cityNameView
//                .getText().toString());
//        if (records == null || records.size() != 1) {
//            return;
//        }
//        cidView.setText(String.valueOf(records.get(0).cityID));
//    }

    /**
     * 暂停下载
     *
     */
//    public void stop(View view) {
//        int cityid = Integer.parseInt(cidView.getText().toString());
//        mOffline.pause(cityid);
//        Toast.makeText(this, "暂停下载离线地图. cityid: " + cityid, Toast.LENGTH_SHORT)
//                .show();
//        updateView();
//    }

    /**
     * 删除离线地图
     *
     * @param view
     */
//    public void remove(View view) {
//        int cityid = Integer.parseInt(cidView.getText().toString());
//        mOffline.remove(cityid);
//        Toast.makeText(this, "删除离线地图. cityid: " + cityid, Toast.LENGTH_SHORT)
//                .show();
//        updateView();
//    }

    /**
     * 地图下载列表更新状态显示
     */
    public void updateView() {
        localMapList = mOffline.getAllUpdateInfo();
        if (localMapList == null) {
            localMapList = new ArrayList<MKOLUpdateElement>();
        }
        lAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        MKOLUpdateElement temp = mOffline.getUpdateInfo(cityDownId);
        if (temp != null && temp.status == MKOLUpdateElement.DOWNLOADING) {
            mOffline.pause(cityDownId);
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public String formatDataSize(int size) {
        String ret = "";
        if (size < (1024 * 1024)) {
            ret = String.format("%dK", size / 1024);
        } else {
            ret = String.format("%.1fM", size / (1024 * 1024.0));
        }
        return ret;
    }

    @Override
    protected void onDestroy() {
        /**
         * 退出时，销毁离线地图模块
         */
        mOffline.destroy();
        super.onDestroy();
    }

    @Override
    public void onGetOfflineMapState(int type, int state) {
        switch (type) {
            case MKOfflineMap.TYPE_DOWNLOAD_UPDATE:
                MKOLUpdateElement update = mOffline.getUpdateInfo(state);
//                 处理下载进度更新提示
                if (update != null) {
                    updateView();
                }
//                for (OfflineMapCityBean bean : hotCitiesBean) {
//                    if (bean.getCityCode() == state) {
//                        bean.setProgress(update.ratio);
//                        break;
//                    }
//                }
//                hAdapter.notifyDataSetChanged();
                localMapList = mOffline.getAllUpdateInfo();
                for (OfflineMapCityBean bean : allCitiesBean) {
                    if (bean.getCityCode() == state) {
                        bean.setProgress(update.ratio);
                        break;
                    }
                }
                aAdapter.notifyDataSetChanged();
                break;

            case MKOfflineMap.TYPE_NEW_OFFLINE:
                // 有新离线地图安装
                break;
            case MKOfflineMap.TYPE_VER_UPDATE:
                // 版本更新提示
                // MKOLUpdateElement e = mOffline.getUpdateInfo(state);

                break;
            default:
                break;
        }
    }

    //支持离线地图列表数据更新
    public void dataChange() {
        for (OfflineMapCityBean cityBean : allCitiesBean) {
            // 获取热闹城市列表
            localMapList = mOffline.getAllUpdateInfo();
            if (localMapList != null)//有下载记录则查找进度
            {
                for (MKOLUpdateElement ele : localMapList) {
                    if (ele.cityID == cityBean.getCityCode()) {
                        cityBean.setProgress(ele.ratio);
                    }
                }
            }
        }
        aAdapter.notifyDataSetChanged();
//        hAdapter.notifyDataSetChanged();
    }

    /**
     * 离线地图管理列表适配器
     */
    public class LocalMapAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return localMapList.size();
        }

        @Override
        public Object getItem(int index) {
            return localMapList.get(index);
        }

        @Override
        public long getItemId(int index) {
            return index;
        }

        @Override
        public View getView(int index, View view, ViewGroup arg2) {
            MKOLUpdateElement e = (MKOLUpdateElement) getItem(index);
            view = View.inflate(OfflineMapActivity.this,
                    R.layout.offline_localmap_list, null);
            initViewItem(index, view, e);
            return view;
        }

        void initViewItem(final int index, View view, final MKOLUpdateElement e) {
            Button display = (Button) view.findViewById(R.id.display);
            Button remove = (Button) view.findViewById(R.id.remove);
            TextView downCityName = (TextView) view.findViewById(R.id.downCityName);
            TextView update = (TextView) view.findViewById(R.id.update);
            TextView ratio = (TextView) view.findViewById(R.id.ratio);

            ratio.setText(e.ratio + "%");
            downCityName.setText(e.cityName);
            if (e.update) {
                update.setText("可更新");
            } else {
                update.setText("最新");
            }
            if (e.ratio != 100) {
                display.setEnabled(false);
            } else {
                display.setEnabled(true);
            }

            remove.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    mOffline.remove(e.cityID);
                    updateView();
//                    dataChange();
                }
            });
            display.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra("x", e.geoPt.longitude);
                    intent.putExtra("y", e.geoPt.latitude);
                    intent.setClass(OfflineMapActivity.this, BaseMapActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    /**
     * 支持离线地图的城市列表适配器
     */
    public class OfflineCityAdapter extends BaseAdapter {
//        private ArrayList<OfflineMapCityBean> list = null;
//
//        public OfflineCityAdapter(ArrayList<OfflineMapCityBean> list) {
//            this.list = list;
//        }

        @Override
        public int getCount() {
            return allCitiesBean.size();
        }

        @Override
        public Object getItem(int index) {
            return allCitiesBean.get(index);
        }

        @Override
        public long getItemId(int index) {
            return index;
        }

        @Override
        public View getView(int index, View view, ViewGroup arg2) {
            OfflineMapCityBean bean = allCitiesBean.get(index);
            view = mInflater.inflate(R.layout.offline_cities_list, null);
            initViewItem(index, view, bean);
            return view;
        }

        void initViewItem(final int index, View view, final OfflineMapCityBean bean) {
            final Button download = (Button) view.findViewById(R.id.download);
            final Button pause = (Button) view.findViewById(R.id.pause);
            final TextView cityName = (TextView) view.findViewById(R.id.cityName);
            final TextView tvProgress = (TextView) view.findViewById(R.id.tv_progress);
            TextView tvSize = (TextView) view.findViewById(R.id.size);
            final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
            if (bean.getProgress() > 0) {
                tvProgress.setText(bean.getProgress() + "%");
            }
            cityName.setText(bean.getCityName());
            tvSize.setText(formatDataSize(bean.getSize()));

            //设置按钮状态
            MKOLUpdateElement temp = mOffline.getUpdateInfo(bean.getCityCode());
            if (temp != null && temp.status == MKOLUpdateElement.DOWNLOADING) {
                pause.setVisibility(View.VISIBLE);
                download.setVisibility(View.GONE);
            } else if (temp != null && temp.status == MKOLUpdateElement.WAITING) {
                tvProgress.setText("等待下载");
                pause.setVisibility(View.VISIBLE);
                download.setVisibility(View.GONE);
            } else if (temp != null && temp.status == MKOLUpdateElement.SUSPENDED) {
                pause.setVisibility(View.GONE);
                download.setText("继续");
                download.setVisibility(View.VISIBLE);
            } else if (temp != null && temp.status == MKOLUpdateElement.FINISHED) {
                pause.setVisibility(View.GONE);
                download.setText("已下载");
                download.setVisibility(View.VISIBLE);
                download.setEnabled(false);
                download.setClickable(false);
            } else {
                tvProgress.setText("");
            }
            pause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    mOffline.pause(bean.getCityCode());
                    allCitiesBean.get(index).setFlag(OfflineMapCityBean.Flag.PAUSE);
                    Toast.makeText(mContext, "暂停下载离线地图", Toast.LENGTH_SHORT).show();
                    download.setVisibility(View.VISIBLE);
                    pause.setVisibility(View.GONE);
                    dataChange();
                    updateView();
                }
            });
            download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cityDownId = bean.getCityCode();
                    mOffline.start(bean.getCityCode());
                    Toast.makeText(mContext, "开始下载离线地图", Toast.LENGTH_SHORT).show();
                    pause.setVisibility(View.VISIBLE);
                    download.setVisibility(View.GONE);
                    dataChange();
                    updateView();
                }
            });
        }
    }
}