package com.ahstu.mycar.music;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.ahstu.mycar.R;
import com.ahstu.mycar.bean.MusicMessage;

import java.util.List;

interface AdpterOnItemClick {
    void onAdpterClick(int postion);
}

public class MusicSearchAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<MusicMessage> musicMessageList;
    private AdpterOnItemClick myAdpterOnclick;
    
    public MusicSearchAdapter(Context context, List<MusicMessage> str) {
        mInflater = LayoutInflater.from(context);
        musicMessageList = str;
    }

    public void onListener(AdpterOnItemClick listener) {

        this.myAdpterOnclick = listener;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        convertView = mInflater.inflate(R.layout.music_down_item, null);
        holder.musicDownName = (TextView) convertView.findViewById(R.id.music_name_item);

        holder.bt_music_down = (Button) convertView.findViewById(R.id.bt_music_download);
        
        holder.musicDownName.setText(musicMessageList.get(position).getSong_name());
        holder.bt_music_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myAdpterOnclick != null) {
                    myAdpterOnclick.onAdpterClick(position);
                }
            }
        });
        return convertView;
    }

    private class ViewHolder {
        TextView musicDownName;
        Button bt_music_down;
    }
}
