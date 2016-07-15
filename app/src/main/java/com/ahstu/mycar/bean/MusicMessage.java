package com.ahstu.mycar.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by 徐伟 on 2016/5/23.
 * 功能：音乐信息
 */
public class MusicMessage extends BmobObject {
    private String song_name;
    private String song_url;

    public String getSong_url() {
        return song_url;
    }

    public void setSong_url(String song_url) {
        this.song_url = song_url;
    }

    public String getSong_name() {
        return song_name;
    }

    public void setSong_name(String song_name) {
        this.song_name = song_name;
    }
}
