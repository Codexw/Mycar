package com.ahstu.mycar.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ahstu.mycar.R;
import com.ahstu.mycar.bean.Price;

import java.util.List;

public class ListGridViewAdapter extends BaseAdapter {

    private List<Price> mList;
    private LayoutInflater mInflater;

    public ListGridViewAdapter(Context context, List<Price> list) {
        this.mList = list;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mList.size();
    }

    @Override
    public Price getItem(int position) {
        // TODO Auto-generated method stub
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        // TODO Auto-generated method stub
        View mView = null;
        if (convertView == null) {
            mView = mInflater.inflate(R.layout.item_price_gridview, null);
        } else {
            mView = convertView;
        }
        TextView tv = (TextView) mView.findViewById(R.id.tv);
        Price p = getItem(position);
        tv.setText(p.getType() + " " + p.getPrice());
        return mView;
    }

}
