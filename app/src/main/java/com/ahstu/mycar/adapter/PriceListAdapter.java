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

/**
 * Created by redowu on 2016/5/12.
 */
public class PriceListAdapter extends BaseAdapter {

    private List<Price> list;
    private LayoutInflater inflater;

    public PriceListAdapter(Context context, List<Price> list) {
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = null;
        if (convertView == null) {
            rowView = inflater.inflate(R.layout.item_info_list, null);
        } else {
            rowView = convertView;
        }
        TextView tv_name = (TextView) rowView.findViewById(R.id.tv_name);
        TextView tv_price = (TextView) rowView.findViewById(R.id.tv_price);
        Price p = (Price) getItem(position);
        tv_name.setText(p.getType());
        tv_price.setText(p.getPrice());
        return rowView;
    }
}
