package com.ahstu.mycar.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ahstu.mycar.R;
import com.ahstu.mycar.activity.CarListActivity;
import com.ahstu.mycar.activity.LoginActivity;
import com.ahstu.mycar.activity.MeorderActivity;

/**
 * @author redowu 2016/4/25
 */
public class MeInfoFragment extends Fragment {
    View view;
    //LinearLayout weizhang;
    LinearLayout me_mycar;
    LinearLayout me_myform;
    LinearLayout exit;
    TextView username;
    private Button btn_exit;

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }


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
