
package com.ahstu.mycar.me;

import android.widget.BaseAdapter;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import com.ahstu.mycar.R;


import java.util.List;

public class ListAdapter extends BaseAdapter {
    private List<ListModel> mData;
    private Context mContext;

    public ListAdapter(Context mContext, List mData) {
        this.mData = mData;
        this.mContext = mContext;


    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View view = View.inflate(mContext, R.layout.csy_listitem_citys, null);

        //初始化
        ListModel model = mData.get(position);
        TextView txt_name = (TextView) view.findViewById(R.id.txt_name);
        //ImageView image=(ImageView)view.findViewById(R.id.iv_1);


        //绑定数据
        txt_name.setText(model.getTextName());
        txt_name.setTag(model.getNameId());
        //返回
        return view;
    }

}
