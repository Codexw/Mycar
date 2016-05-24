package com.ahstu.mycar.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ahstu.mycar.R;
import com.ahstu.mycar.bean.Carinfomation;
import com.ahstu.mycar.fragment.FindFragment;
import com.ahstu.mycar.fragment.FriendFragment;
import com.ahstu.mycar.fragment.MapFragment;
import com.ahstu.mycar.fragment.MeInfoFragment;
import com.ahstu.mycar.me.CarMessage;
import com.ahstu.mycar.music.ListViewAdapter;
import com.ahstu.mycar.music.Mp3;
import com.ahstu.mycar.music.MusicMainActivity;
import com.ahstu.mycar.music.MusicPlayService;
import com.ahstu.mycar.music.MusicUtils;
import com.ahstu.mycar.view.MusicMenu;
import com.ahstu.mycar.view.MusicMenu.MusicMenuListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * @author 吴天洛 2016/4/25
 */

public class MainActivity extends FragmentActivity implements OnClickListener, MusicMenuListener {
    private final int SETADAPTER = 111;
    private long exitTime;  //用于双击回退键退出软件的时间间隔处理
    private TextView txtHome, txtSearch, txtFriend, txtMe;
    private ImageView imgAdd;
    private View currentButton; //获取view，用于底部导航栏状态的切换
    private MusicMenu menuView;
    private ListView listview;
    private MusicPlayService mService;
    private MyApplication application;
    private ArrayList<Mp3> songs;//储存当前播放列表所有歌曲
    private boolean idEdit = false;//判断是不是编辑模式，是的话显示删除图标
    private long playlistId;//当前播放列表id
    private ListViewAdapter listViewAdapter;//适配器
    private List<Map<String, Object>> listItems;//存入适配器的数据
    private ArrayList<String> pl_songIds;// 列表歌曲的id集合
    Handler handler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what) {
                case SETADAPTER:
                    setAdapter();
                    break;
            }
        }
    };
    private Timer timer;//定时器
    private TimerTask myTimerTask;//定时器任务
    private messageThread messageThread = null;//消息推送
    private CarMessage carMessage = new CarMessage();

    public void setAdapter() {
        listItems = getListItems();//得到适配器数据
        listViewAdapter = new ListViewAdapter(this, listItems, R.layout.itemplaylist_song_activity); // 创建适配
        listViewAdapter.setPl_songIds(pl_songIds);//传入列表歌曲id
//		listview.setAdapter(listViewAdapter);
    }

    /**
     * 得到歌曲信息
     */
    private List<Map<String, Object>> getListItems() {
        List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
        pl_songIds = new ArrayList<String>();//存储列表�?��歌曲id
//		songs = MusicUtils.getSongListForPlaylist(MusicActivity.this, playlistId);//存储列表歌曲
        songs = MusicUtils.getAllSongs(MainActivity.this);
        for (int i = 0; i < songs.size(); i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            if (idEdit) {
                map.put("deleteIcon", R.drawable.delete_01);// 删除图标
            } else {
                map.put("deleteIcon", -1);
            }
            map.put("songName", songs.get(i).getName()); // 歌曲

            pl_songIds.add(songs.get(i).getAllSongIndex() + "");//存储列表歌曲id
            listItems.add(map);
        }

        return listItems;
    }




    @Override
    protected void onResume() {
        super.onResume();
        //以下是定时器0.1秒后再跳到handler加载适配器
        timer = new Timer();
        myTimerTask = new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = SETADAPTER;
                handler.sendMessage(message);
            }
        };
        timer.schedule(myTimerTask, 100);
    }


    //調用衛星菜單中的接口回調方法，實現衛星菜單監聽事件
    @Override

    public void dealMusicclick(View v) {
//        Toast.makeText(this, "select"+v.getTag(), Toast.LENGTH_SHORT).show();
        application = (MyApplication) getApplication();
        mService = application.getmService();
        Log.e("TAG", ">>>>>>>>>>>>>>>>>>" + v.getTag().toString());

        switch (v.getTag().toString()) {
            case "previous":
                mService.frontMusic();
                break;
            case "pause":
                mService.pausePlay();
                break;
            case "next":
                mService.nextMusic();
                break;
            case "list":
                startActivity(new Intent(MainActivity.this, MusicMainActivity.class));
                break;
        }
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);
        initView();
        initOnclick();
        startService(new Intent(MainActivity.this, MusicPlayService.class));
        setAdapter();
        menuView.setOnMusicMenuListener(this);
        listview = (ListView) findViewById(R.id.listView);
        application = (MyApplication) getApplication();
        mService = application.getmService();

        // 初始化BmobSDK
        Bmob.initialize(this, "ccd46e34cec57d61dbcedaa08f722296");
        // 使用推送服务时的初始化操作
        BmobInstallation.getCurrentInstallation(this).save();//消息推送
        // 启动推送服务
        BmobPush.startWork(this);

        //启动线程，访问服务器数据进行推送
        messageThread = new messageThread();
        messageThread.isrunning = true;
        messageThread.start();


        new Thread() {
            public void run() {
                try {
                    sleep(1000);

                } catch (InterruptedException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                if (null == mService) {
                    mService = application.getmService();

                }
                try {
                    mService.setCurrentListItme(0);
                    mService.setSongs(songs);
                    mService.playMusic(songs.get(0).getUrl());

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 初始化点击事件
     */
    private void initOnclick() {
        txtHome.setOnClickListener(this);
        txtSearch.setOnClickListener(this);
        txtFriend.setOnClickListener(this);
        txtMe.setOnClickListener(this);
        imgAdd.setOnClickListener(this);

        txtHome.performClick();//默认textHome被点击
    }

    /**
     * 初始化，获取id
     */
    private void initView() {
        txtHome = (TextView) findViewById(R.id.txtHome);
        txtSearch = (TextView) findViewById(R.id.txtSearch);
        txtFriend = (TextView) findViewById(R.id.txtFriend);
        txtMe = (TextView) findViewById(R.id.txtMe);
        imgAdd = (ImageView) findViewById(R.id.imgAdd);
        menuView = (MusicMenu) findViewById(R.id.menu);
    }

    /**
     * 处理点击事件，加载fragment
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtHome:
                mainTabUtil(new MapFragment());
                setButton(v);
                break;
            case R.id.txtSearch:
                mainTabUtil(new FindFragment());
                setButton(v);
                break;
            case R.id.txtFriend:
                mainTabUtil(new FriendFragment());
                setButton(v);
                break;
            case R.id.txtMe:
                mainTabUtil(new MeInfoFragment());
                setButton(v);
                break;
            case R.id.imgAdd:
                if (menuView.isShown())
                    menuView.in();
                else
                    menuView.out();
                break;
            default:
                break;
        }
    }

    /**
     * 切换下方导航栏的状态
     */
    private void setButton(View v) {
        if (currentButton != null && currentButton.getId() != v.getId()) {
            currentButton.setEnabled(true);
        }
        v.setEnabled(false);
        currentButton = v;
    }

    /**
     * 加载fragment
     */
    public void mainTabUtil(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_fragment, fragment).commit();
    }

    /**
     * 双击回退键，退出软件
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(MainActivity.this, "再按一次退出MyCar", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                Intent intent = new Intent();
                intent.setClass(this, SplashActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }


    public void show() {
        Toast.makeText(MainActivity.this, ">>>>>>>>>>>你好<<<<<<<<<<<<<<", Toast.LENGTH_SHORT).show();
    }


    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (mService.isPlay()) {
            mService.pausePlay();
        }


        Toast.makeText(this, "ondestory", Toast.LENGTH_SHORT).show();
    }

    //消息推送
    public class messageThread extends Thread {
        public boolean isrunning;
        //        User user = BmobUser.getCurrentUser(getApplicationContext(), User.class);
        BmobQuery<Carinfomation> carinfomationBmobQuery = new BmobQuery<Carinfomation>();
        BmobPushManager bmobPush = new BmobPushManager(MainActivity.this);
        BmobQuery<BmobInstallation> moblie_id = BmobInstallation.getQuery();//查询设备表
        private boolean ex1, ex2, ex3, ex4, ex5;

        public void run() {
            while (isrunning) {
                try {
                    sleep(3000);
                    SharedPreferences share = getSharedPreferences("text", MODE_PRIVATE);
                    String s = share.getString("number", "");
                    if (s.equals(""))
                        continue;
                    carinfomationBmobQuery.addWhereEqualTo("car_number", s);//查询默认车辆

                    moblie_id.addWhereEqualTo("installationId", BmobInstallation.getInstallationId(MainActivity.this));//匹配当前设备的id
                    bmobPush.setQuery(moblie_id);
                    carinfomationBmobQuery.findObjects(MainActivity.this, new FindListener<Carinfomation>() {
                        @Override
                        public void onSuccess(List<Carinfomation> list) {

                            for (Carinfomation car : list) {
                                if (car.getCar_mile() != 0 && (car.getCar_mile() % 15000) == 0 && (!ex1)) {
                                    ex1 = true;
                                    Toast.makeText(MainActivity.this, "success", Toast.LENGTH_SHORT).show();
                                    bmobPush.pushMessage("当前里程数:" + car.getCar_mile().toString());
                                }
                                if (car.getCar_gas() < 20 && (!ex2)) {
                                    ex2 = true;
                                    Toast.makeText(MainActivity.this, "success", Toast.LENGTH_SHORT).show();
                                    bmobPush.pushMessage("当前油量:" + car.getCar_gas().toString());
                                }
                                if (car.getCar_enginerstate().equals("异常") && (!ex3)) {
                                    ex3 = true;
                                    Toast.makeText(MainActivity.this, "success", Toast.LENGTH_SHORT).show();
                                    bmobPush.pushMessage("发动机异常");
                                }
                                if (car.getCar_shiftstate().equals("异常") && (!ex4)) {
                                    ex4 = true;
                                    Toast.makeText(MainActivity.this, "success", Toast.LENGTH_SHORT).show();
                                    bmobPush.pushMessage("变速器异常");

                                }
                                if (car.getCar_light().equals("异常") && (!ex5)) {
                                    ex5 = true;
                                    Toast.makeText(MainActivity.this, "success", Toast.LENGTH_SHORT).show();
                                    bmobPush.pushMessage("车灯异常");
                                }

                            }
                        }

                        @Override
                        public void onError(int i, String s) {

                        }
                    });

                } catch (InterruptedException e) {
                    Toast.makeText(MainActivity.this, "服务器异常", Toast.LENGTH_SHORT).show();
                }
                if (ex1 && ex2 && ex3 && ex4 && ex5)
                    isrunning = false;

            }
        }
    }

}
