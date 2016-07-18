package com.ahstu.mycar.music;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.ahstu.mycar.R;
import com.ahstu.mycar.activity.MyApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 徐伟
 *         功能：音乐主界面
 */
public class MusicMainActivity extends Activity {

    public static final int PLAYLIST = 1;//适配器加载的数据是歌曲列表
    public static final int SONGS_LIST = 2;
    boolean isReturePlaylist;
    private ListView listView;
    private RadioButton btn_playlist, btn_allSongs;
    private ImageView tv_newPlaylist;
    private SimpleAdapter adapter;
    private int type = -1;
    private List<Mp3> songs;// 歌曲集合
    private List<String> al_playlist;// 播放列表集合
    private MusicPlayService mService;
    private MyApplication application;
    private ImageView iv_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_main_activity);
        application = (MyApplication) getApplication();

        initView();
//        title_name.setText("我的音乐");
//        iv_back.setVisibility(View.VISIBLE);
//        tv_newPlaylist.setVisibility(View.VISIBLE);
//        tv_newPlaylist.setText("新建列表");

        //默认添加我的收藏列表
        ContentResolver resolver = getContentResolver();
        int id = idForplaylist("我的收藏");
        Uri uri;
        if (id >= 0) {
            uri = ContentUris.withAppendedId(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, id);
        } else {
            ContentValues values = new ContentValues(1);
            values.put(MediaStore.Audio.Playlists.NAME, "我的收藏");
            uri = resolver.insert(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, values);
        }
        setResult(RESULT_OK, (new Intent()).setData(uri));

        initListener();
        playListOnclick();
    }

    public void initView() {
//        title_name = (TextView) findViewById(R.id.title_name);
        iv_back = (ImageView) findViewById(R.id.order_pay_back);
        listView = (ListView) this.findViewById(R.id.listview);
        btn_playlist = (RadioButton) this.findViewById(R.id.btn_playlist);
        btn_allSongs = (RadioButton) this.findViewById(R.id.btn_allSongs);
        tv_newPlaylist = (ImageView) this.findViewById(R.id.create_song_list);
    }

    public void playListOnclick() {
        al_playlist = MusicUtils.PlaylistList(MusicMainActivity.this);
        List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();

        for (int i = 0; i < al_playlist.size(); i++) {
            Map<String, Object> map = new HashMap<String, Object>();
//					map.put("id", "");
            map.put("songName", al_playlist.get(i));
//            map.put("singerName", "");
            listItems.add(map);
        }
        adapter = new SimpleAdapter(MusicMainActivity.this, listItems, R.layout.item_list_music_main_activity, new String[]{"songName"}, new int[]{
                R.id.tv_songlist_Name});
        type = PLAYLIST;
        listView.setAdapter(adapter);
    }

    public void initListener() {
        iv_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //新增列表
        tv_newPlaylist.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                addplaylist();
            }
        });
        //列出所有列表
        btn_playlist.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                playListOnclick();
            }
        });
        //列出所有歌曲
        btn_allSongs.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                songs = MusicUtils.getAllSongs(MusicMainActivity.this);
                List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
                for (int i = 0; i < songs.size(); i++) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("songName", songs.get(i).getName());
                    if (songs.get(i).getSingerName().equals("<unknown>")) {
                        map.put("singerName", "未知歌手");
                    } else {
                        map.put("singerName", songs.get(i).getSingerName());
                    }
                    listItems.add(map);
                }
                adapter = new SimpleAdapter(MusicMainActivity.this, listItems, R.layout.item_all_music_main_activity, new String[]{"songName", "singerName"}, new int[]{
                        R.id.tv_songName, R.id.tv_singerName});
                type = SONGS_LIST;
                listView.setAdapter(adapter);
            }
        });

        listView.setOnItemClickListener(new OnItemClickListener() {
            Intent it = new Intent();

            //            private List<Map<String, Object>> listItems;
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                switch (type) {
                    case PLAYLIST:
                        isReturePlaylist = true;
                        it.putExtra("position", position);
                        it.putExtra("listname", al_playlist.get(position));
                        it.setClass(MusicMainActivity.this, PlaylistSongActivity.class);
                        startActivity(it);
                        break;
                    case SONGS_LIST:
                        if (null == mService) {
                            mService = application.getmService();
                        }
                        mService.setCurrentListItme(position);
                        mService.setSongs(songs);
                        mService.playMusic(songs.get(position).getUrl());
                        break;
                }
            }
        });
    }

    /**
     * 新建列表
     */
    public void addplaylist() {
        final EditText inputServer = new EditText(MusicMainActivity.this);
        AlertDialog.Builder builder = new AlertDialog.Builder(MusicMainActivity.this);

        builder.setTitle("请输入新建歌单名称").setView(inputServer).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setPositiveButton("保存", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String name = inputServer.getText().toString().trim();
                if (!TextUtils.isEmpty(name) && !name.equals("我的收藏")) {
                    ContentResolver resolver = getContentResolver();
                    int id = idForplaylist(name);
                    Uri uri;
                    if (id >= 0) {
                        Toast.makeText(getApplicationContext(), "列表名已存在！", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        ContentValues values = new ContentValues(1);
                        values.put(MediaStore.Audio.Playlists.NAME, name);
                        uri = resolver.insert(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, values);
                    }
                    setResult(RESULT_OK, (new Intent()).setData(uri));

                    isReturePlaylist = true;
                    Intent it = new Intent();
                    it.putExtra("listname", name);
                    it.putExtra("autoAddSong", true);
                    it.setClass(MusicMainActivity.this, PlaylistSongActivity.class);
                    startActivity(it);
                } else {
                    if (name.equals("我的收藏"))
                        Toast.makeText(getApplicationContext(), "列表名已存在！", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(getApplicationContext(), "列表名不能为空！", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
        builder.create().show();
    }

    /**
     * 通过列表名得到列表id
     */
    private int idForplaylist(String name) {
        Cursor c = MusicUtils.query(this, MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Audio.Playlists._ID}, MediaStore.Audio.Playlists.NAME + "=?",
                new String[]{name}, MediaStore.Audio.Playlists.NAME);
        int id = -1;
        if (c != null) {
            c.moveToFirst();
            if (!c.isAfterLast()) {
                id = c.getInt(0);
            }
            c.close();
        }
        return id;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //这里要重新刷新列表，因为跳到列表歌曲界面时可能把这个列表删了，
        //跳回来刷新，新建列表回来刷新
        if (isReturePlaylist) {
            playListOnclick();
            isReturePlaylist = false;
        }
    }
}
