package com.ahstu.mycar.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by xuning on 2016/5/21.
 * 功能：同步订单到服务器
 */
public class Order extends BmobObject {
    private User user;
    private String time;
    private String stationname;
    private String carnumber;
    private String ctype;
    private Double gascount;
    private Double gasprice;
    private String countprice;
    private Integer state;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStationname() {
        return stationname;
    }

    public void setStationname(String stationname) {
        this.stationname = stationname;
    }

    public String getCtype() {
        return ctype;
    }

    public void setCtype(String ctype) {
        this.ctype = ctype;
    }

    public Double getGascount() {
        return gascount;
    }

    public void setGascount(Double gascount) {
        this.gascount = gascount;
    }

    public Double getGasprice() {
        return gasprice;
    }

    public void setGasprice(Double gasprice) {
        this.gasprice = gasprice;
    }

    public String getCountprice() {
        return countprice;
    }

    public void setCountprice(String countprice) {
        this.countprice = countprice;
    }

    public String getCarnumber() {
        return carnumber;
    }

    public void setCarnumber(String carnumber) {
        this.carnumber = carnumber;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }
}
