package com.ahstu.mycar.music;

public class Mp3 {
	public int id;
	public int playlistId;//列表id
	public String singerName;//歌手名字
	public String url;//sd卡路径
	public String name;//歌曲名字
	public int Duration;
	private long allSongIndex ;
	

	
	public int getSqlId() {
		return id;
	}

	public void setSqlId(int sqlId) {
		this.id = sqlId;
	}

	public int getPlaylistId() {
		return playlistId;
	}

	public void setPlaylistId(int playlistId) {
		this.playlistId = playlistId;
	}

	public int getDuration() {
		return Duration;
	}

	public void setDuration(int duration) {
		Duration = duration;
	}
	
	public String getSingerName() {
		return singerName;
	}

	public void setSingerName(String singerName) {
		this.singerName = singerName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getAllSongIndex() {
		return allSongIndex;
	}

	public void setAllSongIndex(long allSongIndex) {
		this.allSongIndex = allSongIndex;
	}

	@Override
	public String toString() {
		return "Mp3 [id=" + id + ", playlistId=" + playlistId +  ", singerName=" + singerName + ", url=" + url
				+ ", name=" + name + ", Duration="
				+ Duration + ", allSongIndex=" + allSongIndex + "]";
	}

	
	
}
