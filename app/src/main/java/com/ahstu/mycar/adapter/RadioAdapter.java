package com.ahstu.mycar.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.ahstu.mycar.R;
import com.ahstu.mycar.activity.MeCarActivity;

import java.util.ArrayList;

public class RadioAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private ArrayList list;
    private viewHolder holder;
    private int index, temp;
    private Context content;
    public RadioAdapter(Context content, ArrayList list) {
        super();
        this.content = content;
        this.list = list;
        inflater = LayoutInflater.from(content);

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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final SharedPreferences share = content.getSharedPreferences("text", content.MODE_PRIVATE);
        index = share.getInt("position", 0);
        temp = position;
        holder = new viewHolder();
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.car_list_item, null);
            holder.nameTxt = (TextView) convertView.findViewById(R.id.car_list_number);
            holder.selectBtn = (RadioButton) convertView
                    .findViewById(R.id.car_list_radiobutton);
            holder.selectstate = (TextView) convertView.findViewById(R.id.selectstate);
            holder.car_list_linearlayout = (LinearLayout) convertView.findViewById(R.id.car_list_linearlyaout);
            convertView.setTag(holder);
        } else {
            holder = (viewHolder) convertView.getTag();
        }

        holder.nameTxt.setText(list.get(position).toString());
        holder.car_list_linearlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setClass(content, MeCarActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("car_number", list.get(temp).toString());
                intent.putExtras(bundle);
                content.startActivity(intent);
                
            }
        });

        holder.selectBtn
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        if (isChecked) {
                            index = position;
                            SharedPreferences.Editor editer = share.edit();
                            editer.putInt("position", index);
                            editer.putString("number", list.get(position).toString());
                            editer.commit();
                            notifyDataSetChanged();
                        }
                    }
                });

        if (index == position) {// 选中的条目和当前的条目是否相等
            holder.selectBtn.setChecked(true);
            holder.selectstate.setVisibility(View.VISIBLE);

        } else {
            holder.selectBtn.setChecked(false);
            holder.selectstate.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }

    public class viewHolder {
        public TextView nameTxt;
        public RadioButton selectBtn;
        public TextView selectstate;
        public LinearLayout car_list_linearlayout;
    }

}
