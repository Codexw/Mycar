package com.ahstu.mycar.fragment;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ahstu.mycar.R;
import com.ahstu.mycar.activity.CarListActivity;
import com.ahstu.mycar.bean.Carinfomation;
import com.ahstu.mycar.me.RoundProgressBar;
import com.ahstu.mycar.sql.DatabaseHelper;
import com.ahstu.mycar.ui.PullToRefreshLayout;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * @author 吴天洛 2016/4/25
 *         功能：车辆控制界面
 */

public class CarControlFragment extends Fragment {

    LayoutInflater mInflater;
    Button addcar;
    TextView car_set_number;
    TextView car_control_enginer;
    TextView car_control_shift;
    TextView car_control_light;
    TextView start_set;
    TextView door_set;
    TextView air_set;
    TextView lock_set;
    TextView c_gas;
    TextView c_count;
    LinearLayout carcontrollinearlayout;
    PullToRefreshLayout carcontrollinearlayout2;
    RelativeLayout car_start_relativelayout;
    RelativeLayout car_door_relativelayout;
    RelativeLayout car_air_relativelayout;
    RelativeLayout car_lock_relativelayout;
    ImageView car_lock_set_true;
    ImageView car_lock_set_false;
    ImageView car_door_set_true;
    ImageView car_door_set_false;
    ImageView car_air_set_true;
    ImageView car_air_set_false;
    ImageView car_start_set_true;
    ImageView car_start_set_false;
    int car_door_state;
    int car_air_state;
    int car_lock_state;
    int car_start_state;
    int car_gaspoint;
    String objectid;
    Carinfomation car;
    //进度条
    private RoundProgressBar roundProgressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        mInflater = inflater;
        View view = inflater.inflate(R.layout.fragment_carcontrol, null);
        return view;
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            //初始化时判断本地数据库是否存在车辆信息，存在将第一个视图消失，第二个视图显示出来。
            DatabaseHelper helper = new DatabaseHelper(getActivity(), "node.db", null, 1);
            SQLiteDatabase data = helper.getReadableDatabase();
            Cursor cursor = data.query("carinfo", new String[]{"car_number"}, null, null, null, null, null);
            int count = cursor.getCount();
            cursor.close();
            data.close();
            addcar = (Button) getActivity().findViewById(R.id.carcontrol_text);
            carcontrollinearlayout = (LinearLayout) getActivity().findViewById(R.id.carcontrollinearlayout);
            carcontrollinearlayout2 = (PullToRefreshLayout) getActivity().findViewById(R.id.refresh_view);
            //判断count的值，大于0的话，数据库存在值。
            if (count > 0) {
                carcontrollinearlayout.setVisibility(View.GONE);
                carcontrollinearlayout2.setVisibility(View.VISIBLE);
            } else {
                carcontrollinearlayout.setVisibility(View.VISIBLE);
                carcontrollinearlayout2.setVisibility(View.GONE);
            }

            addcar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getActivity(), CarListActivity.class));
                }
            });

            //进度条
            roundProgressBar = (RoundProgressBar) getActivity().findViewById(R.id.progressBar);
            initview();
            roundProgressBar.setBox_point(0);
            roundProgressBar.update();
        }
        super.onHiddenChanged(hidden);
    }

    //初始化组件，并且加监听器
    void initview() {
        SharedPreferences share = getActivity().getSharedPreferences("text", getActivity().MODE_PRIVATE);
        final String s = share.getString("number", "");
        final Carinfomation carinfomation = new Carinfomation();
        final BmobQuery<Carinfomation> query = new BmobQuery<Carinfomation>();
        query.addWhereEqualTo("car_number", s);
        try {
            query.findObjects(getActivity(), new FindListener<Carinfomation>() {
                @Override
                public void onSuccess(List<Carinfomation> list) {
                    if (list.size() != 0) {
                        car = new Carinfomation();
                        objectid = list.get(0).getObjectId();
                        car = list.get(0);
                        //Toast.makeText(getActivity(),"查找成功",Toast.LENGTH_SHORT).show();
                        car_control_enginer.setText(car.getCar_enginerstate().toString());
                        car_control_shift.setText(car.getCar_shiftstate().toString());
                        car_control_light.setText(car.getCar_light().toString());
                        c_count.setText(String.valueOf(car.getCar_mile()));
                        c_gas.setText(String.valueOf(car.getCar_box() * car.getCar_gas() / 100));
                        car_gaspoint = car.getCar_gas();
                        roundProgressBar.setBox_point(car.getCar_gas());
                        roundProgressBar.update();
                        DatabaseHelper data = new DatabaseHelper(getActivity(), "node.db", null, 1);
                        SQLiteDatabase db = data.getWritableDatabase();
                        ContentValues value = new ContentValues();
                        if (car.getCar_start() == false) {
                            value.put("car_start", 0);
                            car_start_state = 0;
                            car_start_set_false.setBackgroundDrawable(getResources().getDrawable(R.drawable.check_box_false));
                            start_set.setText("已关闭");
                        } else {
                            value.put("car_start", 1);
                            car_start_state = 1;
                            car_start_set_false.setBackgroundDrawable(getResources().getDrawable(R.drawable.check_box_true));
                            start_set.setText("已开启");
                        }
                        if (car.getCar_door() == false) {
                            value.put("car_door", 0);
                            car_door_state = 0;
                            car_door_set_false.setBackgroundDrawable(getResources().getDrawable(R.drawable.check_box_false));
                            door_set.setText("已关闭");

                        } else {
                            value.put("car_door", 1);
                            car_door_state = 1;
                            car_door_set_false.setBackgroundDrawable(getResources().getDrawable(R.drawable.check_box_true));
                            door_set.setText("已开启");

                        }
                        if (car.getCar_lock() == false) {

                            value.put("car_lock", 0);
                            car_lock_state = 0;
                            car_lock_set_false.setBackgroundDrawable(getResources().getDrawable(R.drawable.check_box_false));
                            lock_set.setText("已关闭");
                        } else {
                            value.put("car_lock", 1);
                            car_lock_state = 1;
                            car_lock_set_false.setBackgroundDrawable(getResources().getDrawable(R.drawable.check_box_true));
                            lock_set.setText("已开启");
                        }

                        if (car.getCar_air() == false) {

                            value.put("car_air", 0);
                            car_air_state = 0;
                            car_air_set_false.setBackgroundDrawable(getResources().getDrawable(R.drawable.check_box_false));
                            air_set.setText("已关闭");
                        } else {
                            value.put("car_air", 1);
                            car_air_state = 1;
                            car_air_set_false.setBackgroundDrawable(getResources().getDrawable(R.drawable.check_box_true));
                            air_set.setText("已开启");
                        }
                        db.update("carinfo", value, "car_number=?", new String[]{s});
                        db.close();
                    }
                }

                @Override
                public void onError(int i, String s) {

                }
            });
        } catch (Exception e) {
            Toast.makeText(getActivity(), "网速慢请重试", Toast.LENGTH_SHORT).show();

        }


//        //初始化组件
        car_set_number = (TextView) getActivity().findViewById(R.id.car_set_number);
        car_air_relativelayout = (RelativeLayout) getActivity().findViewById(R.id.car_air_relativelayout);
        car_door_relativelayout = (RelativeLayout) getActivity().findViewById(R.id.car_door_relativelayout);
        car_start_relativelayout = (RelativeLayout) getActivity().findViewById(R.id.car_start_relativelayout);
        car_lock_relativelayout = (RelativeLayout) getActivity().findViewById(R.id.car_lock_relativelayout);
        car_door_set_false = (ImageView) getActivity().findViewById(R.id.car_door_set_false);
        car_door_set_true = (ImageView) getActivity().findViewById(R.id.car_door_set_ture);
        car_air_set_false = (ImageView) getActivity().findViewById(R.id.car_air_set_false);
        car_air_set_true = (ImageView) getActivity().findViewById(R.id.car_air_set_ture);
        car_start_set_true = (ImageView) getActivity().findViewById(R.id.car_start_set_ture);
        car_start_set_false = (ImageView) getActivity().findViewById(R.id.car_start_set_false);
        car_lock_set_false = (ImageView) getActivity().findViewById(R.id.car_lock_set_false);
        car_lock_set_true = (ImageView) getActivity().findViewById(R.id.car_lock_set_ture);
        car_control_enginer = (TextView) getActivity().findViewById(R.id.car_control_enginer);
        car_control_shift = (TextView) getActivity().findViewById(R.id.car_control_shift);
        car_control_light = (TextView) getActivity().findViewById(R.id.car_control_light);
        start_set = (TextView) getActivity().findViewById(R.id.start_set);
        door_set = (TextView) getActivity().findViewById(R.id.door_set);
        air_set = (TextView) getActivity().findViewById(R.id.air_set);
        lock_set = (TextView) getActivity().findViewById(R.id.lock_set);
        c_count = (TextView) getActivity().findViewById(R.id.c_count);
        c_gas = (TextView) getActivity().findViewById(R.id.c_gas);
        ((PullToRefreshLayout) getActivity().findViewById(R.id.refresh_view))
                .setOnRefreshListener(new MyListener());

        //设置车牌号
        car_set_number.setText(s);
        //空调控制监听器
        car_air_relativelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (car_air_state == 0) {
//                    car_air_set_true.setVisibility(View.VISIBLE);
//                    car_air_set_false.setVisibility(View.INVISIBLE);
                    car_air_set_false.setBackgroundDrawable(getResources().getDrawable(R.drawable.check_box_true));

                    car_air_state = 1;
                    carinfomation.setValue("car_air", true);
                    try {
                        carinfomation.update(getActivity(), objectid, new UpdateListener() {
                            @Override
                            public void onSuccess() {
                                // Toast.makeText(getActivity(), "保存成功", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(int i, String s) {
                                //  Toast.makeText(getActivity(), "保存失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), "网速较慢", Toast.LENGTH_SHORT).show();
                    }
                    DatabaseHelper helper = new DatabaseHelper(getActivity(), "node.db", null, 1);
                    SQLiteDatabase db = helper.getWritableDatabase();
                    ContentValues value = new ContentValues();
                    value.put("car_air", 1);
                    db.update("carinfo", value, "car_number=?", new String[]{s});
                    db.close();
                    air_set.setText("已开启");
                } else {
//                    car_air_set_true.setVisibility(View.INVISIBLE);
//                    car_air_set_false.setVisibility(View.VISIBLE);
                    car_air_set_false.setBackgroundDrawable(getResources().getDrawable(R.drawable.check_box_false));

                    car_air_state = 0;

                    carinfomation.setValue("car_air", false);
                    try {
                        carinfomation.update(getActivity(), objectid, new UpdateListener() {
                            @Override
                            public void onSuccess() {
                                //  Toast.makeText(getActivity(), "保存成功", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(int i, String s) {
                                //   Toast.makeText(getActivity(), "保存失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), "网速较慢", Toast.LENGTH_SHORT).show();
                    }
                    DatabaseHelper helper = new DatabaseHelper(getActivity(), "node.db", null, 1);
                    SQLiteDatabase db = helper.getWritableDatabase();
                    ContentValues value = new ContentValues();
                    value.put("car_air", 0);
                    db.update("carinfo", value, "car_number=?", new String[]{s});
                    db.close();
                    air_set.setText("已关闭");
                }


            }


        });
        //车门控制监听器
        car_door_relativelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (car_door_state == 0) {
//                    car_door_set_true.setVisibility(View.VISIBLE);
//                    car_door_set_false.setVisibility(View.INVISIBLE);
                    car_door_set_false.setBackgroundDrawable(getResources().getDrawable(R.drawable.check_box_true));

                    car_door_state = 1;
                    carinfomation.setValue("car_door", true);
                    try {
                        carinfomation.update(getActivity(), objectid, new UpdateListener() {
                            @Override
                            public void onSuccess() {
                                // Toast.makeText(getActivity(), "保存成功", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(int i, String s) {
                                // Toast.makeText(getActivity(), "保存失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), "网速较慢", Toast.LENGTH_SHORT).show();
                    }
                    DatabaseHelper helper = new DatabaseHelper(getActivity(), "node.db", null, 1);
                    SQLiteDatabase db = helper.getWritableDatabase();
                    ContentValues value = new ContentValues();
                    value.put("car_door", 1);
                    db.update("carinfo", value, "car_number=?", new String[]{s});
                    db.close();

                    door_set.setText("已开启");


                } else {
//                    car_door_set_true.setVisibility(View.INVISIBLE);
//                    car_door_set_false.setVisibility(View.VISIBLE);
                    car_door_set_false.setBackgroundDrawable(getResources().getDrawable(R.drawable.check_box_false));

                    car_door_state = 0;
                    carinfomation.setValue("car_door", false);
                    try {
                        carinfomation.update(getActivity(), objectid, new UpdateListener() {
                            @Override
                            public void onSuccess() {
                                //   Toast.makeText(getActivity(), "保存成功", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(int i, String s) {
                                // Toast.makeText(getActivity(), "保存失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), "网速较慢", Toast.LENGTH_SHORT).show();
                    }

                    DatabaseHelper helper = new DatabaseHelper(getActivity(), "node.db", null, 1);
                    SQLiteDatabase db = helper.getWritableDatabase();
                    ContentValues value = new ContentValues();
                    value.put("car_door", 0);
                    db.update("carinfo", value, "car_number=?", new String[]{s});
                    db.close();

                    door_set.setText("已关闭");
                }
            }
        });
        //汽车开启监听器
        car_start_relativelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (car_start_state == 0) {
//                    car_start_set_true.setVisibility(View.VISIBLE);
//                    car_start_set_false.setVisibility(View.INVISIBLE);
                    car_start_set_false.setBackgroundDrawable(getResources().getDrawable(R.drawable.check_box_true));
                    car_start_state = 1;

                    carinfomation.setValue("car_start", true);
                    try {
                        carinfomation.update(getActivity(), objectid, new UpdateListener() {
                            @Override
                            public void onSuccess() {
                                // Toast.makeText(getActivity(), "保存成功", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(int i, String s) {
                                //  Toast.makeText(getActivity(), "保存失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), "网速较慢", Toast.LENGTH_SHORT).show();
                    }
                    DatabaseHelper helper = new DatabaseHelper(getActivity(), "node.db", null, 1);
                    SQLiteDatabase db = helper.getWritableDatabase();
                    ContentValues value = new ContentValues();
                    value.put("car_start", 1);
                    db.update("carinfo", value, "car_number=?", new String[]{s});
                    db.close();
                    start_set.setText("已开启");

                } else {
//                    car_start_set_true.setVisibility(View.INVISIBLE);
//                    car_start_set_false.setVisibility(View.VISIBLE);
                    car_start_set_false.setBackgroundDrawable(getResources().getDrawable(R.drawable.check_box_false));

                    car_start_state = 0;
                    carinfomation.setValue("car_start", false);
                    try {
                        carinfomation.update(getActivity(), objectid, new UpdateListener() {
                            @Override
                            public void onSuccess() {
                                // Toast.makeText(getActivity(), "保存成功", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(int i, String s) {
                                // Toast.makeText(getActivity(), "保存失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), "网速较慢", Toast.LENGTH_SHORT).show();
                    }
                    DatabaseHelper helper = new DatabaseHelper(getActivity(), "node.db", null, 1);
                    SQLiteDatabase db = helper.getWritableDatabase();
                    ContentValues value = new ContentValues();
                    value.put("car_start", 0);
                    db.update("carinfo", value, "car_number=?", new String[]{s});
                    db.close();
                    start_set.setText("已关闭");
                }
            }
        });
        //汽车锁定监听器
        car_lock_relativelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (car_lock_state == 0) {
//                    car_lock_set_true.setVisibility(View.VISIBLE);
//                    car_lock_set_false.setVisibility(View.INVISIBLE);
                    car_lock_set_false.setBackgroundDrawable(getResources().getDrawable(R.drawable.check_box_true));

                    car_lock_state = 1;
                    carinfomation.setValue("car_lock", true);
                    try {
                        carinfomation.update(getActivity(), objectid, new UpdateListener() {
                            @Override
                            public void onSuccess() {
                                //  Toast.makeText(getActivity(), "保存成功", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(int i, String s) {
                                //  Toast.makeText(getActivity(), "保存失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), "网速较慢", Toast.LENGTH_SHORT).show();
                    }
                    DatabaseHelper helper = new DatabaseHelper(getActivity(), "node.db", null, 1);
                    SQLiteDatabase db = helper.getWritableDatabase();
                    ContentValues value = new ContentValues();
                    value.put("car_lock", 1);
                    db.update("carinfo", value, "car_number=?", new String[]{s});
                    db.close();
                    lock_set.setText("已开启");
                } else {
//                    car_lock_set_true.setVisibility(View.INVISIBLE);
//                    car_lock_set_false.setVisibility(View.VISIBLE);
                    car_lock_set_false.setBackgroundDrawable(getResources().getDrawable(R.drawable.check_box_false));

                    car_lock_state = 0;
                    carinfomation.setValue("car_lock", false);
                    try {
                        carinfomation.update(getActivity(), objectid, new UpdateListener() {
                            @Override
                            public void onSuccess() {
                                //Toast.makeText(getActivity(), "保存成功", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(int i, String s) {
                                //Toast.makeText(getActivity(), "保存失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), "网速较慢", Toast.LENGTH_SHORT).show();
                    }
                    DatabaseHelper helper = new DatabaseHelper(getActivity(), "node.db", null, 1);
                    SQLiteDatabase db = helper.getWritableDatabase();
                    ContentValues value = new ContentValues();
                    value.put("car_lock", 0);
                    db.update("carinfo", value, "car_number=?", new String[]{s});
                    db.close();
                    lock_set.setText("已关闭");
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Toast.makeText(getActivity(),"onresume",Toast.LENGTH_SHORT).show();
        onHiddenChanged(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Toast.makeText(getActivity(),"onpause",Toast.LENGTH_SHORT).show();
    }

    class MyListener implements PullToRefreshLayout.OnRefreshListener {
        @Override
        public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
            new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    // 控件刷新完毕了哦！
                    pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                    onHiddenChanged(false);

                }
            }.sendEmptyMessageDelayed(0, 2500);
        }

        @Override
        public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {

        }
    }
}
