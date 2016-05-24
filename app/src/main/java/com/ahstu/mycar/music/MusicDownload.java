package com.ahstu.mycar.music;

import android.app.Activity;
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
public class MusicDownload extends Activity {
    
    private EditText musicname;
    private Button searchmusicbt;
    private MusicMessage musicmessage = new MusicMessage();
    private String namestring;
    private MusicSearchAdapter musicSearchAdapter;
    private ListView musicdownlist;

    private ArrayList<MusicMessage> musicMessageArrayList;

    private ArrayList<String> al = new ArrayList<String>();
    private String str[] = {"111", "2222", "3333", "43sfd", "dsadsa", "dasdas"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_download);

        musicname = (EditText) findViewById(R.id.music_search_name);
        searchmusicbt = (Button) findViewById(R.id.bt_music_search);
        musicdownlist = (ListView) findViewById(R.id.music_search_listview);

        searchmusicbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                namestring = musicname.getText().toString();
                BmobQuery<MusicMessage> musicquery = new BmobQuery<MusicMessage>();
                musicquery.addWhereContains("song_name", namestring);//对歌曲名字进行模糊查询
                musicquery.findObjects(MusicDownload.this, new FindListener<MusicMessage>() {
                    @Override
                    public void onSuccess(List<MusicMessage> list) {

                        musicSearchAdapter = new MusicSearchAdapter(MusicDownload.this, list);
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


}
