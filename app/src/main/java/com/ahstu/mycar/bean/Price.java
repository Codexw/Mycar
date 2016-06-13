package com.ahstu.mycar.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by 吴天洛 on 2016/5/8.
 */
public class Price implements Parcelable {
    public static final Creator<Price> CREATOR = new Creator<Price>() {
        @Override
        public Price createFromParcel(Parcel in) {
            Price p = new Price();
            p.type = in.readString();
            p.price = in.readString();
            return p;
        }

        @Override
        public Price[] newArray(int size) {
            return new Price[size];
        }
    };
    private String type;

/*    protected Price(Parcel in) {
        type = in.readString();
        price = in.readString();
    }*/
private String price;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
        dest.writeString(price);
    }
}
