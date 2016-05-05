package com.ahstu.mycar.music;

public class Mp3 {
	public int id;
//	public String singerName;//歌手名字
	public String url;//sd卡路径
	public String name;//歌曲名字
//	public int pictureID;
	public int Duration;
	private long allSongIndex ;
//	public String picUrl;//图片路径

	
	public int getSqlId() {
		return id;
	}

	public void setSqlId(int sqlId) {
		this.id = sqlId;
	}



	public int getDuration() {
		return Duration;
	}

	public void setDuration(int duration) {
		Duration = duration;
	}


//
//	public String getSingerName() {
//		return singerName;
//	}



//	public void setSingerName(String singerName) {
//		this.singerName = singerName;
//	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getName() {
		return name;
	}

//	public int getPictureID() {
//		return pictureID;
//	}
//
//	public void setPictureID(int pictureID) {
//		this.pictureID = pictureID;
//	}

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
		return "Mp3 [id=" + id +  ", albumName="
				+ ", url=" + url
				+ ", name=" + name  + ", Duration="
				+ Duration + ", allSongIndex=" + allSongIndex + "]";
	}

	
	
}
