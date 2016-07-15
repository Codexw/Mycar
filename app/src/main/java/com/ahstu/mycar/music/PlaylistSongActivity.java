package com.ahstu.mycar.music;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ahstu.mycar.R;
import com.ahstu.mycar.activity.MyApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author 徐伟
 *         功能：音乐播放
 */
@SuppressLint("NewApi")
public class PlaylistSongActivity extends Activity {

    private final int SETADAPTER = 111;
    public Button btn_back;
    private TextView tv_edit, tv_clear, tv_delete, tv_add, tv_back, title_name;
    private ImageView iv_back;
    private LinearLayout ll_normal, ll_edit;
    private ListView listView;
    private boolean idEdit = false;//判断是不是编辑模式，是的话显示删除图标
    private long playlistId;//当前播放列表id
    private ArrayList<Mp3> songs;//储存当前播放列表所有歌曲
    private ListViewAdapter listViewAdapter;//适配器
    private List<Map<String, Object>> listItems;//存入适配器的数据
    private ArrayList<String> pl_songIds;// 列表歌曲的id集合
    private Timer timer;//定时器
    private TimerTask myTimerTask;//定时器任务
    private MusicPlayService mService;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlist_songs);
        MyApplication application = (MyApplication) getApplication();
        mService = application.getmService();
        initView();
        iv_back.setVisibility(View.VISIBLE);
        initListener();

        Intent intent = getIntent();
        String songlist_name = intent.getStringExtra("listname");
        title_name.setText(songlist_name);
        if (intent != null) {
            //判断是不是从添加列表界面跳过来的，是的话就点击一下添加歌曲按钮，跳到添加歌曲界面
            boolean addSong = intent.getBooleanExtra("autoAddSong", false);
            if (addSong) {
                String playlistName = intent.getStringExtra("playListName");
                title_name.setText(playlistName);
                long listId = MusicUtils.getPlayListId(PlaylistSongActivity.this, playlistName);
                playlistId = listId;
                tv_add.performClick();//这里点击了添加歌曲按钮
            } else {
                ArrayList<String> al_playlist = MusicUtils.getAl_playlist();//得到全部播放列表
                if (al_playlist == null || al_playlist.isEmpty()) {
                    return;
                }
                String targetStr = al_playlist.get(intent.getIntExtra("position", -1));
                playlistId = Integer.parseInt(targetStr);
            }
        }
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

    public void initView() {
        iv_back = (ImageView) findViewById(R.id.iv_back);
        title_name = (TextView) findViewById(R.id.title_name);
        listView = (ListView) findViewById(R.id.listView);
        tv_delete = (TextView) findViewById(R.id.tv_delete);
        tv_clear = (TextView) findViewById(R.id.tv_clear);
        tv_add = (TextView) findViewById(R.id.tv_add);
        tv_edit = (TextView) findViewById(R.id.tv_edit);
        tv_back = (TextView) findViewById(R.id.tv_back);
        ll_normal = (LinearLayout) findViewById(R.id.ll_normal);
        ll_edit = (LinearLayout) findViewById(R.id.ll_edit);
//        btn_back = (Button) findViewById(R.id.back_btn);
    }

    private void initListener() {
        iv_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //编辑
        tv_edit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                ll_normal.setVisibility(View.GONE);
                ll_edit.setVisibility(View.VISIBLE);

                idEdit = true;

                setAdapter();
            }
        });
        //清空列表里的歌曲
        tv_clear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                new AlertDialog.Builder(PlaylistSongActivity.this).setTitle("清空列表").setMessage("确定删除列表内所有歌曲？").setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MusicUtils.clearPlaylist(getApplicationContext(), playlistId);
                        setAdapter();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();

            }
        });
        //删除本列表
        tv_delete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                new AlertDialog.Builder(PlaylistSongActivity.this).setTitle("删除列表").setMessage("确定彻底移除此列表？").setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, playlistId);
                        getContentResolver().delete(uri, null, null);
                        finish();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();

            }
        });
        //添加歌曲到本列表
        tv_add.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(PlaylistSongActivity.this, AddSongtoPlaylistActivity.class);
                intent.putExtra("playlistId", playlistId);
                startActivity(intent);
            }
        });
        //返回上一界面
        tv_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                ll_normal.setVisibility(View.VISIBLE);
                ll_edit.setVisibility(View.GONE);
                idEdit = false;
                setAdapter();
            }
        });

    }

    public void setAdapter() {
        listItems = getListItems();//得到适配器数据
        listViewAdapter = new ListViewAdapter(this, listItems, R.layout.item_playlist_song_activity); // 创建适配�?
        listViewAdapter.setPl_songIds(pl_songIds);//传入列表歌曲id
        listView.setAdapter(listViewAdapter);
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
                final int positionInt = position;
                if (idEdit) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(PlaylistSongActivity.this);
                    dialog.setTitle("删除歌曲").setMessage("确定将本歌曲移除？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId);
                            getContentResolver().delete(ContentUris.withAppendedId(uri, songs.get(positionInt).getSqlId()), null, null);
                            listItems = getListItems();
                            listViewAdapter.setListItems(listItems);
                            listViewAdapter.notifyDataSetChanged();
                        }

                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();// 取消弹出
                        }

                    }).create().show();
                } else {
                    mService.setCurrentListItme(position);
                    mService.setSongs(songs);
                    mService.playMusic(songs.get(position).getUrl());
                }
            }
        });
    }

    /**
     * 得到歌曲信息
     */
    private List<Map<String, Object>> getListItems() {
        List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
        pl_songIds = new ArrayList<String>();//存储列表歌曲id
        songs = MusicUtils.getSongListForPlaylist(PlaylistSongActivity.this, playlistId);//存储列表歌曲

        for (int i = 0; i < songs.size(); i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            if (idEdit) {
                map.put("deleteIcon", R.drawable.delete_01);// 删除图标
            } else {
                map.put("deleteIcon", -1);
            }
            map.put("songName", songs.get(i).getName()); // 歌曲
            if (songs.get(i).getSingerName().equals("<unknown>")) {
                map.put("singerName", "----");
            } else {
                map.put("singerName", songs.get(i).getSingerName()); // 歌手
            }
            pl_songIds.add(songs.get(i).getAllSongIndex() + "");//存储列表歌曲id
            listItems.add(map);
        }
        return listItems;
    }
}
