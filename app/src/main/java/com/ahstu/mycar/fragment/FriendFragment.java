package com.ahstu.mycar.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ahstu.mycar.R;

/**
 * @author 吴天洛 2016/4/25
 */

public class FriendFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View view = inflater.inflate(R.layout.fragment_friend, null);
        return view;
    }
}
