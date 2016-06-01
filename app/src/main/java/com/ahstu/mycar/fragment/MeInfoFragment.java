package com.ahstu.mycar.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ahstu.mycar.R;
import com.ahstu.mycar.activity.CarListActivity;
import com.ahstu.mycar.activity.LoginActivity;
import com.ahstu.mycar.activity.MeorderActivity;
import com.ahstu.mycar.music.MusicDownload;

/**
 * @author redowu 2016/4/25
 */
public class MeInfoFragment extends Fragment {
    View view;

    LinearLayout me_mycar;
    LinearLayout me_myform;
    LinearLayout exit;
    LinearLayout me_music;
    TextView username;


    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

//    private void initClick(View view) {
//        btn_exit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //清除用户登录记录
//                SharedPreferences sp = getActivity().getSharedPreferences("User", getActivity().MODE_PRIVATE);
//                SharedPreferences.Editor editor = sp.edit();
//                editor.clear();
//                editor.commit();
//                startActivity(new Intent(getActivity(), LoginActivity.class));
//            }
//        });
//    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        view = inflater.inflate(R.layout.fragment_meinfo, null);
        initview();
        click();
        return view;
    }

    void initview() {
        SharedPreferences sp = getActivity().getSharedPreferences("User", getActivity().MODE_PRIVATE);
        String name = sp.getString("name", "");
        me_mycar = (LinearLayout) view.findViewById(R.id.me_mycar);
        me_myform = (LinearLayout) view.findViewById(R.id.me_myform);
        me_music = (LinearLayout) view.findViewById(R.id.me_music);
        exit = (LinearLayout) view.findViewById(R.id.exit);
        username = (TextView) view.findViewById(R.id.username);
        username.setText(name);
    }

    void click() {
        me_mycar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CarListActivity.class);
                startActivity(intent);
            }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sp = getActivity().getSharedPreferences("User", getActivity().MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.clear();
                editor.commit();
                getActivity().deleteDatabase("node.db");
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });
        me_music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MusicDownload.class);
                startActivity(intent);
            }
        });

        me_myform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), MeorderActivity.class);
                startActivity(intent);
            }
        });
    }


}
