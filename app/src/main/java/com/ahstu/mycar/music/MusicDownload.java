package com.ahstu.mycar.music;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.ahstu.mycar.R;
import com.ahstu.mycar.bean.MusicMessage;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Administrator on 2016/5/23.
 */
public class MusicDownload extends Activity implements AdpterOnItemClick {

    //下载服务
    DownloadManager downManager;
    private EditText musicname;
    private Button searchmusicbt;
    private MusicMessage musicmessage = new MusicMessage();
    private String namestring;
    private MusicSearchAdapter musicSearchAdapter;
    private ListView musicdownlist;
    private Button bt_music_down;
    private List<MusicMessage> musicMessageArrayList;
    private ArrayList<String> al = new ArrayList<String>();
    private DownLoadCompleteReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_download);

        musicname = (EditText) findViewById(R.id.music_search_name);
        searchmusicbt = (Button) findViewById(R.id.bt_music_search);
        musicdownlist = (ListView) findViewById(R.id.music_search_listview);
        bt_music_down = (Button) findViewById(R.id.bt_music_download);


        // 下载按钮服务（未解绑定）
        downManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);// 下载服务
        IntentFilter filter = new IntentFilter();
        filter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);// 下载完整的行动
        filter.addAction(DownloadManager.ACTION_NOTIFICATION_CLICKED);// 单击行动的通知
        receiver = new DownLoadCompleteReceiver();
        registerReceiver(receiver, filter);
        
        

        searchmusicbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                namestring = musicname.getText().toString();
                BmobQuery<MusicMessage> musicquery = new BmobQuery<MusicMessage>();
                musicquery.addWhereContains("song_name", namestring);//对歌曲名字进行模糊查询
                musicquery.findObjects(MusicDownload.this, new FindListener<MusicMessage>() {
                    @Override
                    public void onSuccess(List<MusicMessage> list) {
                        musicMessageArrayList = list;
                        musicSearchAdapter = new MusicSearchAdapter(MusicDownload.this, list);
                        musicSearchAdapter.onListener(MusicDownload.this);
                        musicdownlist.setAdapter(musicSearchAdapter);
                        Toast.makeText(MusicDownload.this, "search success", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onError(int i, String s) {

                    }
                });
            }
        });

    }

    @Override
    public void onAdpterClick(int postion) {
        int ii = postion;
        String song_url = musicMessageArrayList.get(postion).getSong_url();
        DownloadManager.Request request = new DownloadManager.Request(
                Uri.parse(song_url));
        Toast.makeText(MusicDownload.this, " 音乐正在下载。。。",
                Toast.LENGTH_SHORT).show();

        // 设置通知栏标题
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        // 控制系统通知是否由下载管理器发布，而此下载正在运行或何时完成
        request.setTitle("mycar");
        request.setDescription("正在下载...");
        request.setAllowedOverRoaming(false);// 是否同意漫游状态下 执行操作
        // 设置文件存放目录
        request.setDestinationInExternalPublicDir("mycarmusic", musicMessageArrayList.get(postion).getSong_name() + ".mp4");
        downManager.enqueue(request);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private class DownLoadCompleteReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

        }
    }
}
