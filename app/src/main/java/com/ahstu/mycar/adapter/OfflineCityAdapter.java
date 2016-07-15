package com.ahstu.mycar.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ahstu.mycar.R;
import com.baidu.mapapi.map.offline.MKOLSearchRecord;
import com.baidu.mapapi.map.offline.MKOfflineMap;

import java.util.ArrayList;

/**
 * Created by redowu on 2016/7/15.
 * 支持离线地图的城市列表适配器
 */
public class OfflineCityAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<MKOLSearchRecord> list;
    private LayoutInflater mInflater;
    private MKOfflineMap mOffline = null;


    public OfflineCityAdapter(Context context, ArrayList<MKOLSearchRecord> list, MKOfflineMap mOffline) {
        this.list = list;
        this.mContext = context;
        mOffline = this.mOffline;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int index) {
        return list.get(index);
    }

    @Override
    public long getItemId(int index) {
        return index;
    }

    @Override
    public View getView(int index, View view, ViewGroup arg2) {
        MKOLSearchRecord e = (MKOLSearchRecord) getItem(index);
        view = View.inflate(mContext, R.layout.offline_cities_list, null);
        initViewItem(view, e);
        return view;
    }

    void initViewItem(View view, final MKOLSearchRecord e) {

        final Button display = (Button) view.findViewById(R.id.display2);
        final Button remove = (Button) view.findViewById(R.id.remove2);
        TextView cityName = (TextView) view.findViewById(R.id.cityName);
//        TextView update = (TextView) view.findViewById(R.id.update);
        TextView ratio = (TextView) view.findViewById(R.id.size);
//        ratio.setText(e.ratio + "%");
        cityName.setText(e.cityName + formatDataSize(e.size));
        /*if (e.update) {
            update.setText("可更新");
        } else {
            update.setText("最新");
        }*/
//        if (e.ratio != 100) {
//            display.setEnabled(false);
//        } else {
//            display.setEnabled(true);
//        }
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mOffline.pause(e.cityID);
//                int cityid = Integer.parseInt(cidView.getText().toString());
//                mOffline.pause(cityid);
                Toast.makeText(mContext, "暂停下载离线地图. cityid: ", Toast.LENGTH_SHORT)
                        .show();
                display.setVisibility(View.VISIBLE);
                remove.setVisibility(View.GONE);
                
            }
        });
        display.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                int cityid = Integer.parseInt(cidView.getText().toString());
//                mOffline.start(e.cityID);
//                clickLocalMapListButton(null);
                Toast.makeText(mContext, "开始下载离线地图. cityid: " + e.cityID, Toast.LENGTH_SHORT)
                        .show();
                remove.setVisibility(View.VISIBLE);
                display.setVisibility(View.GONE);
            }
        });
    }

    //地图尺寸格式
    public String formatDataSize(int size) {
        String ret = "";
        if (size < (1024 * 1024)) {
            ret = String.format("%dK", size / 1024);
        } else {
            ret = String.format("%.1fM", size / (1024 * 1024.0));
        }
        return ret;
    }

}
