package com.ahstu.mycar.util;

import android.os.Handler;
import android.os.Message;

import com.ahstu.mycar.bean.Price;
import com.ahstu.mycar.bean.Station;
import com.thinkland.sdk.android.DataCallBack;
import com.thinkland.sdk.android.JuheData;
import com.thinkland.sdk.android.Parameters;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by 吴天洛 on 2016/5/8.
 */
public class StationData {
    private Handler handler;

    public StationData(Handler handler) {
        this.handler = handler;
    }

    public void getStationData(Double lat, Double lon, int distance) {
        Parameters mParameters = new Parameters();
        mParameters.add("lat", lat);
        mParameters.add("lon", lon);
        mParameters.add("r", distance);
        JuheData.executeWithAPI(7, "http://apis.juhe.cn/oil/local", JuheData.GET, mParameters, new DataCallBack() {

            @Override
            public void resultLoaded(int i, String s, String s1) {
                //0表示成功
                if (i == 0) {
                    ArrayList<Station> list = parser(s1);
                    if (list != null && list.size() > 0) {
                        Message msg = Message.obtain(handler, 0x01, list);
                        msg.sendToTarget();
                    }
                } else {
                    Message msg = Message.obtain(handler, 0x02, i);
                    msg.sendToTarget();
                }
            }
        });
    }

    //解析JSON数据
    private ArrayList<Station> parser(String str) {//
        ArrayList<Station> list = null;
        try {
            JSONObject json = new JSONObject(str);
            int code = json.getInt("error_code");
            if (code == 0) {
                list = new ArrayList<Station>();
                JSONArray arr = json.getJSONObject("result").getJSONArray("data");
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject dataJSON = arr.getJSONObject(i);
                    Station s = new Station();
                    s.setName(dataJSON.getString("name"));
                    s.setAddr(dataJSON.getString("address"));
                    s.setArea(dataJSON.getString("areaname"));
                    s.setBrand(dataJSON.getString("brandname"));
                    s.setLat(dataJSON.getDouble("lat"));
                    s.setLon(dataJSON.getDouble("lon"));
                    s.setDistance(dataJSON.getInt("distance"));

                    JSONObject priceJson = dataJSON.getJSONObject("price");
                    ArrayList<Price> priceList = new ArrayList<Price>();//
                    Iterator<String> priceI = priceJson.keys();
                    while (priceI.hasNext()) {
                        Price p = new Price();
                        String key = priceI.next();
                        String value = priceJson.getString(key);
                        p.setType(key.replace("E", "") + "#");
                        p.setPrice(value + "元/升");
                        priceList.add(p);
                    }
                    s.setPriceList(priceList);

                    JSONObject gastPriceJson = dataJSON.getJSONObject("gastprice");
                    ArrayList<Price> gastPriceList = new ArrayList<Price>();
                    Iterator<String> gastPriceI = gastPriceJson.keys();
                    while (gastPriceI.hasNext()) {
                        Price p = new Price();
                        String key = gastPriceI.next();
                        String value = gastPriceJson.getString(key);
                        p.setType(key);
                        p.setPrice(value + "元/升");
                        gastPriceList.add(p);
                    }
                    s.setGastPriceList(gastPriceList);
                    list.add(s);
                }

            } else {
                Message msg = Message.obtain(handler, 0x02, code);
                msg.sendToTarget();
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return list;
    }
}
