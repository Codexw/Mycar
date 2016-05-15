package com.ahstu.mycar.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by redowu on 2016/5/8.
 */
public class Station implements Parcelable {
    private String name;
    private String addr;
    private String area;
    private String brand;
    private double lat;
    private double lon;
    private int distance;
    private ArrayList<Price> gastPriceList;  //本地油价
    private ArrayList<Price> priceList; //省控油价

/*    protected Station(Parcel in) {
        name = in.readString();
        addr = in.readString();
        area = in.readString();
        brand = in.readString();
        lat = in.readDouble();
        lon = in.readDouble();
        distance = in.readInt();
    }*/

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

    public ArrayList<Price> getPriceList() {
        return priceList;
    }

    public void setPriceList(ArrayList<Price> priceList) {
        this.priceList = priceList;
    }


    @Override
    public int describeContents() {

        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(addr);
        dest.writeString(area);
        dest.writeString(brand);
        dest.writeDouble(lat);
        dest.writeDouble(lon);
        dest.writeInt(distance);
        dest.writeList(gastPriceList);
        dest.writeList(priceList);
    }

    public static final Creator<Station> CREATOR = new Creator<Station>() {
        @Override
        public Station createFromParcel(Parcel in) {
            Station s = new Station();
            s.name = in.readString();
            s.addr = in.readString();
            s.area = in.readString();
            s.brand = in.readString();
            s.lat = in.readDouble();
            s.lon = in.readDouble();
            s.distance = in.readInt();
            s.gastPriceList = in.readArrayList(Price.class.getClassLoader());
            s.priceList = in.readArrayList(Price.class.getClassLoader());
            return s;
        }

        @Override
        public Station[] newArray(int size) {
            return new Station[size];
        }
    };
}
