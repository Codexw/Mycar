package com.ahstu.mycar.bean;

import java.util.ArrayList;

/**
 * Created by redowu on 2016/5/8.
 */
public class Station {
    private String name;
    private String addr;
    private String area;
    private String brand;
    private double lat;
    private double lon;
    private int distance;
    private ArrayList<Price> gastPriceList;
    private ArrayList<Price> priceList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public ArrayList<Price> getGastPriceList() {
        return gastPriceList;
    }

    public void setGastPriceList(ArrayList<Price> gastPriceList) {
        this.gastPriceList = gastPriceList;
    }
}
