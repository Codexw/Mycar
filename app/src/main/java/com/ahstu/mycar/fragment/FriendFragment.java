package com.ahstu.mycar.fragment;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ahstu.mycar.R;
import com.ahstu.mycar.activity.CarListActivity;
import com.ahstu.mycar.bean.Carinfomation;
import com.ahstu.mycar.me.RoundProgressBar;
import com.ahstu.mycar.sql.DatabaseHelper;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * @author 吴天洛 2016/4/25
 */

public class FriendFragment extends Fragment {

    Button addcar;
    TextView car_set_number;
    TextView car_control_enginer;
    TextView car_control_shift;
    TextView car_control_light;
    TextView start_set;
    TextView door_set;
    TextView air_set;
    TextView lock_set;
    LinearLayout carcontrollinearlayout;
    RelativeLayout carcontrollinearlayout2;
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
    View view;
    int car_door_state;
    int car_air_state;
    int car_lock_state;
    int car_start_state;
    String objectid;
    Carinfomation car;
    //进度条
    private RoundProgressBar progressBar;
    private int progress = 9;
    private int all = 100;
    private int youliang = 25;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        //初始化时判断本地数据库是否存在车辆信息，存在将第一个视图消失，第二个视图显示出来。
        DatabaseHelper helper = new DatabaseHelper(getActivity(), "node.db", null, 1);
        SQLiteDatabase data = helper.getReadableDatabase();
        Cursor cursor = data.query("carinfo", new String[]{"car_number"}, null, null, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        data.close();
        //将布局layout动态的加入到里面
        view = inflater.inflate(R.layout.fragment_friend, null);
        addcar = (Button) view.findViewById(R.id.carcontrol_button);
        carcontrollinearlayout = (LinearLayout) view.findViewById(R.id.carcontrollinearlayout);
        carcontrollinearlayout2 = (RelativeLayout) view.findViewById(R.id.carcontrol);
        //判断count的值，大于0的话，数据库存在值。
        if (count > 0) {

            carcontrollinearlayout.setVisibility(View.GONE);
            carcontrollinearlayout2.setVisibility(View.VISIBLE);
        }


        addcar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), CarListActivity.class));
            }
        });


        initview();

        //进度条
        progressBar = (RoundProgressBar) view.findViewById(R.id.progressBar);
        progressBar.setMax(100);
        progressBar.setProgress(progress);
        if (all / youliang >= 2) {
            youliang = progress + 40 / 2;
        } else {

        }

        new Thread(new Runnable() {

            public void run() {
                while (progress <= youliang) {
                    progress++;
                    progressBar.setProgress(progress);
                    try {
                        Thread.sleep(30);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();
        
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //从Activity结束返回到fragment时，判断数据库是否存在值。
        DatabaseHelper helper = new DatabaseHelper(getActivity(), "node.db", null, 1);
        SQLiteDatabase data = helper.getReadableDatabase();
        Cursor cursor = data.query("carinfo", new String[]{"car_number"}, null, null, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        data.close();
        if (count > 0) {

            carcontrollinearlayout.setVisibility(View.GONE);
            carcontrollinearlayout2.setVisibility(View.VISIBLE);
        }
        initview();

    }

    //初始化组件，并且加监听器
    void initview() {
        
        SharedPreferences share = getActivity().getSharedPreferences("text", getActivity().MODE_PRIVATE);
        final String s = share.getString("number", "");
        final Carinfomation carinfomation = new Carinfomation();
        //car=new Carinfomation();
        final BmobQuery<Carinfomation> query = new BmobQuery<Carinfomation>();
        query.addWhereEqualTo("car_number", s);
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
                    

                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });


        //Log.e("TAG", "《《《《《《《《《《《《《《《《《《《" + s);
        DatabaseHelper helper = new DatabaseHelper(getActivity(), "node.db", null, 1);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("carinfo", new String[]{"car_start", "car_air", "car_door", "car_lock", "car_mile", "car_gas"}, "car_number=?", new String[]{s}, null, null, null);
        //初始化组件
        car_set_number = (TextView) view.findViewById(R.id.car_set_number);
        car_air_relativelayout = (RelativeLayout) view.findViewById(R.id.car_air_relativelayout);
        car_door_relativelayout = (RelativeLayout) view.findViewById(R.id.car_door_relativelayout);
        car_start_relativelayout = (RelativeLayout) view.findViewById(R.id.car_start_relativelayout);
        car_lock_relativelayout = (RelativeLayout) view.findViewById(R.id.car_lock_relativelayout);
        car_door_set_false = (ImageView) view.findViewById(R.id.car_door_set_false);
        car_door_set_true = (ImageView) view.findViewById(R.id.car_door_set_ture);
        car_air_set_false = (ImageView) view.findViewById(R.id.car_air_set_false);
        car_air_set_true = (ImageView) view.findViewById(R.id.car_air_set_ture);
        car_start_set_true = (ImageView) view.findViewById(R.id.car_start_set_ture);
        car_start_set_false = (ImageView) view.findViewById(R.id.car_start_set_false);
        car_lock_set_false = (ImageView) view.findViewById(R.id.car_lock_set_false);
        car_lock_set_true = (ImageView) view.findViewById(R.id.car_lock_set_ture);
        car_control_enginer = (TextView) view.findViewById(R.id.car_control_enginer);
        car_control_shift = (TextView) view.findViewById(R.id.car_control_shift);
        car_control_light = (TextView) view.findViewById(R.id.car_control_light);
        start_set = (TextView) view.findViewById(R.id.start_set);
        door_set = (TextView) view.findViewById(R.id.door_set);
        air_set = (TextView) view.findViewById(R.id.air_set);
        lock_set = (TextView) view.findViewById(R.id.lock_set);
        //根据本地数据库的值设置
        while (cursor.moveToNext()) {
            if (cursor.getInt(cursor.getColumnIndex("car_start")) == 0) {
                car_start_state = 0;
                car_start_set_false.setVisibility(View.VISIBLE);
                start_set.setText("关闭");
                
            } else {
                car_start_state = 1;
                car_start_set_true.setVisibility(View.VISIBLE);
                start_set.setText("开启");

            }
            if (cursor.getInt(cursor.getColumnIndex("car_air")) == 0) {
                car_air_state = 0;
                car_air_set_false.setVisibility(View.VISIBLE);
                air_set.setText("关闭");
            } else {
                car_air_state = 1;
                car_air_set_true.setVisibility(View.VISIBLE);
                air_set.setText("开启");
            }

            if (cursor.getInt(cursor.getColumnIndex("car_door")) == 0) {
                car_door_state = 0;
                car_door_set_false.setVisibility(View.VISIBLE);
                door_set.setText("关闭");

            } else {
                car_door_state = 1;
                car_door_set_true.setVisibility(View.VISIBLE);
                door_set.setText("开启");
            }
            if (cursor.getInt(cursor.getColumnIndex("car_lock")) == 0) {

                car_lock_state = 0;
                car_lock_set_false.setVisibility(View.VISIBLE);
                lock_set.setText("关闭");

            } else {
                car_lock_state = 1;
                car_lock_set_true.setVisibility(View.VISIBLE);
                lock_set.setText("开启");

            }
        }
        cursor.close();
        db.close();
        //设置车牌号
        car_set_number.setText(s);
        //空调控制监听器
        car_air_relativelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (car_air_state == 0) {
                    car_air_set_true.setVisibility(View.VISIBLE);
                    car_air_set_false.setVisibility(View.INVISIBLE);
                    car_air_state = 1;
                    carinfomation.setValue("car_air", true);
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
                    DatabaseHelper helper = new DatabaseHelper(getActivity(), "node.db", null, 1);
                    SQLiteDatabase db = helper.getWritableDatabase();
                    ContentValues value = new ContentValues();
                    value.put("car_air", 1);
                    db.update("carinfo", value, "car_number=?", new String[]{s});
                    db.close();
                    air_set.setText("开启");
                } else {
                    car_air_set_true.setVisibility(View.INVISIBLE);
                    car_air_set_false.setVisibility(View.VISIBLE);
                    car_air_state = 0;

                    carinfomation.setValue("car_air", false);
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
                    DatabaseHelper helper = new DatabaseHelper(getActivity(), "node.db", null, 1);
                    SQLiteDatabase db = helper.getWritableDatabase();
                    ContentValues value = new ContentValues();
                    value.put("car_air", 0);
                    db.update("carinfo", value, "car_number=?", new String[]{s});
                    db.close();
                    air_set.setText("关闭");
                }


            }


        });
        //车门控制监听器
        car_door_relativelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (car_door_state == 0) {
                    car_door_set_true.setVisibility(View.VISIBLE);
                    car_door_set_false.setVisibility(View.INVISIBLE);
                    car_door_state = 1;
                    carinfomation.setValue("car_door", true);
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
                    DatabaseHelper helper = new DatabaseHelper(getActivity(), "node.db", null, 1);
                    SQLiteDatabase db = helper.getWritableDatabase();
                    ContentValues value = new ContentValues();
                    value.put("car_door", 1);
                    db.update("carinfo", value, "car_number=?", new String[]{s});
                    db.close();

                    door_set.setText("开启");
                    
                    
                } else {
                    car_door_set_true.setVisibility(View.INVISIBLE);
                    car_door_set_false.setVisibility(View.VISIBLE);
                    car_door_state = 0;
                    carinfomation.setValue("car_door", false);
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

                    DatabaseHelper helper = new DatabaseHelper(getActivity(), "node.db", null, 1);
                    SQLiteDatabase db = helper.getWritableDatabase();
                    ContentValues value = new ContentValues();
                    value.put("car_door", 0);
                    db.update("carinfo", value, "car_number=?", new String[]{s});
                    db.close();

                    door_set.setText("关闭");
                }
            }
        });
        //汽车开启监听器
        car_start_relativelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (car_start_state == 0) {
                    car_start_set_true.setVisibility(View.VISIBLE);
                    car_start_set_false.setVisibility(View.INVISIBLE);
                    car_start_state = 1;

                    carinfomation.setValue("car_start", true);
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
                    DatabaseHelper helper = new DatabaseHelper(getActivity(), "node.db", null, 1);
                    SQLiteDatabase db = helper.getWritableDatabase();
                    ContentValues value = new ContentValues();
                    value.put("car_start", 1);
                    db.update("carinfo", value, "car_number=?", new String[]{s});
                    db.close();
                    start_set.setText("开启");
                    
                } else {
                    car_start_set_true.setVisibility(View.INVISIBLE);
                    car_start_set_false.setVisibility(View.VISIBLE);
                    car_start_state = 0;
                    carinfomation.setValue("car_start", false);
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
                    DatabaseHelper helper = new DatabaseHelper(getActivity(), "node.db", null, 1);
                    SQLiteDatabase db = helper.getWritableDatabase();
                    ContentValues value = new ContentValues();
                    value.put("car_start", 0);
                    db.update("carinfo", value, "car_number=?", new String[]{s});
                    db.close();
                    start_set.setText("关闭");
                }
            }
        });
        //汽车锁定监听器
        car_lock_relativelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (car_lock_state == 0) {
                    car_lock_set_true.setVisibility(View.VISIBLE);
                    car_lock_set_false.setVisibility(View.INVISIBLE);
                    car_lock_state = 1;
                    carinfomation.setValue("car_lock", true);
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
                    DatabaseHelper helper = new DatabaseHelper(getActivity(), "node.db", null, 1);
                    SQLiteDatabase db = helper.getWritableDatabase();
                    ContentValues value = new ContentValues();
                    value.put("car_lock", 1);
                    db.update("carinfo", value, "car_number=?", new String[]{s});
                    db.close();
                    lock_set.setText("开启");
                } else {
                    car_lock_set_true.setVisibility(View.INVISIBLE);
                    car_lock_set_false.setVisibility(View.VISIBLE);
                    car_lock_state = 0;
                    carinfomation.setValue("car_lock", false);
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
                    DatabaseHelper helper = new DatabaseHelper(getActivity(), "node.db", null, 1);
                    SQLiteDatabase db = helper.getWritableDatabase();
                    ContentValues value = new ContentValues();
                    value.put("car_lock", 0);
                    db.update("carinfo", value, "car_number=?", new String[]{s});
                    db.close();

                    lock_set.setText("关闭");
                }
            }
        });
        
        
    }

}
