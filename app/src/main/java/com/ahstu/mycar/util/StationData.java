package com.ahstu.mycar.util;

import com.thinkland.sdk.android.DataCallBack;
import com.thinkland.sdk.android.JuheData;
import com.thinkland.sdk.android.Parameters;

/**
 * Created by redowu on 2016/5/8.
 */
public class StationData {
    public void getStationData(Double lat, Double lon, int distance) {
        Parameters mParameters = new Parameters();
        mParameters.add("lat", lat);
        mParameters.add("lon", lon);
        mParameters.add("r", distance);
        JuheData.executeWithAPI(7, "http://apis.juhe.cn/oil/local", JuheData.GET, mParameters, new DataCallBack() {


            @Override
            public void resultLoaded(int i, String s, String s1) {

            }
        });
    }
}
