package com.ahstu.mycar.music;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ahstu.mycar.R;
import com.ahstu.mycar.bean.MusicMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/5/23.
 */
public class MusicSearchAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<MusicMessage> musicMessageList;

    private ArrayList<String> str;

    public MusicSearchAdapter(Context context, List<MusicMessage> str) {
        mInflater = LayoutInflater.from(context);
        musicMessageList = str;
    }

    @Override
    public int getCount() {
        return musicMessageList.size();
    }

    @Override
    public Object getItem(int position) {
        return musicMessageList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        convertView = mInflater.inflate(R.layout.music_down_item, null);
        holder.musicDownName = (TextView) convertView.findViewById(R.id.music_name_item);
        holder.musicDownName.setText(musicMessageList.get(position).getSong_name());
        return convertView;
    }

    private class ViewHolder {
        TextView musicDownName;
    }

}
