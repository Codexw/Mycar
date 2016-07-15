package com.ahstu.mycar.me;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.ahstu.mycar.R;
import com.ahstu.mycar.bean.User;

import java.util.List;


/**
 * Created by 徐伟 on 2016/6/5.
 * 功能：车友信息适配器
 */

interface FriendAdpterOnItemClick {
    void onAdpterClick(int postion);
}

public class CarFriendAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<User> friendNameList;
    private FriendAdpterOnItemClick friendAdpterOnclick;
//    private ArrayList<String> str;

    public CarFriendAdapter(Context context, List<User> str) {
        mInflater = LayoutInflater.from(context);
        friendNameList = str;
    }
    public void onListener(FriendAdpterOnItemClick listener) {

        this.friendAdpterOnclick = listener;
    }

    @Override
    public int getCount() {
        return friendNameList.size();
    }

    @Override
    public Object getItem(int position) {
        return friendNameList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        convertView = mInflater.inflate(R.layout.music_down_item, null);
        holder.friendName = (TextView) convertView.findViewById(R.id.music_name_item);

        holder.bt_share_location = (Button) convertView.findViewById(R.id.bt_music_download);
        holder.bt_share_location.setText("共享位置");

        holder.friendName.setText(friendNameList.get(position).getUsername());
        holder.bt_share_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (friendAdpterOnclick != null) {
                    friendAdpterOnclick.onAdpterClick(position);

                }
            }
        });

        return convertView;
    }

    private class ViewHolder {
        TextView friendName;
        Button bt_share_location;
    }
}
