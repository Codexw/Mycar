package com.ahstu.mycar.bean;

import cn.bmob.v3.BmobUser;

/**
 * @author 吴天洛 on 2016/4/25
 *         功能：用于Bmob注册、获取信息。
 */
public class User extends BmobUser {
    private String myInstallation;
    private double mLat;
    private double mLon;

    public String getMyInstallation() {
        return myInstallation;
    }

    public void setMyInstallation(String myInstallation) {
        this.myInstallation = myInstallation;
    }

    public double getLat() {
        return mLat;
    }

    public void setLat(double lat) {
        mLat = lat;
    }

    public double getLon() {
        return mLon;
    }

    public void setLon(double lon) {
        mLon = lon;
    }
}
