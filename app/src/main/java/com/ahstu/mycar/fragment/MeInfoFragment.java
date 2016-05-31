package com.ahstu.mycar.fragment;

import android.content.Context;
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
import com.ahstu.mycar.activity.MyApplication;
import com.ahstu.mycar.bean.User;
import com.ahstu.mycar.music.MusicDownload;
import com.ahstu.mycar.music.MusicPlayService;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * @author redowu 2016/4/25
 */
public class MeInfoFragment extends Fragment {
    View view;
    Context mContext;
    //LinearLayout weizhang;
    LinearLayout me_mycar;
    LinearLayout me_myform;
    LinearLayout exit;
    LinearLayout me_music;
    TextView username;
    String name;
    private Button btn_exit;

    private MyApplication application;
    private MusicPlayService mService;

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        application = (MyApplication) getActivity().getApplication();
        mService = application.getmService();
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
        mContext = view.getContext();
        initview();
        click();
        return view;
    }

    void initview() {
        SharedPreferences sp = getActivity().getSharedPreferences("User", getActivity().MODE_PRIVATE);
        name = sp.getString("name", "");
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

                //退出前清除当前登录用户的设备号
                BmobQuery<User> queryInstallation = new BmobQuery<User>();
                queryInstallation.addWhereEqualTo("username", name);
                queryInstallation.setLimit(1);
                queryInstallation.findObjects(mContext, new FindListener<User>() {
                    @Override
                    public void onSuccess(List<User> list) {
                        for (User userIns : list) {
                            userIns.setMyInstallation("");
                            userIns.update(mContext, userIns.getObjectId(), new UpdateListener() {
                                @Override
                                public void onSuccess() {
                                }

                                @Override
                                public void onFailure(int i, String s) {
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(int i, String s) {

                    }
                });


                SharedPreferences sp = getActivity().getSharedPreferences("User", getActivity().MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.clear();
                editor.commit();
                getActivity().deleteDatabase("node.db");
                if (mService.isPlay()) {
                    mService.pausePlay();
                }
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
