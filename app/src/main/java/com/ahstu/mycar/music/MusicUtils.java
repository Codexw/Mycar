package com.ahstu.mycar.music;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MusicUtils {

	private static List<String> mMusicList = new ArrayList<String>();
	private static List<Album> albums = new ArrayList<Album>();
	public static ArrayList<String> al_playlist = new ArrayList<String>();
	private static List<Mp3> playList = new ArrayList<Mp3>();
	private static String[] mCols = new String[] { MediaStore.Audio.Playlists._ID, MediaStore.Audio.Playlists.NAME };
	/**
	 * 清空歌曲列表中的全部歌曲，plid为列表id
	 */
	public static void clearPlaylist(Context context, long plid) {
		Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", plid);
		context.getContentResolver().delete(uri, null, null);
		return;
	}

	public static Cursor query(Context context, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		return query(context, uri, projection, selection, selectionArgs, sortOrder, 0);
	}

	public static Cursor query(Context context, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder, int limit) {
		try {
			ContentResolver resolver = context.getContentResolver();
			if (resolver == null) {
				return null;
			}
			if (limit > 0) {
				uri = uri.buildUpon().appendQueryParameter("limit", "" + limit).build();
			}
			return resolver.query(uri, projection, selection, selectionArgs, sortOrder);
		} catch (UnsupportedOperationException ex) {
			return null;
		}
	}
	/**
	 * 得到歌曲列表中的全部歌曲，plid为列表id
	 */
	public static ArrayList<Mp3> getSongListForPlaylist(Context context, long plid) {
		final String[] ccols = new String[] { MediaStore.Audio.Playlists.Members._ID, MediaStore.Audio.Playlists.Members.TITLE, MediaStore.Audio.Playlists.Members.ARTIST,
				MediaStore.Audio.Playlists.Members.AUDIO_ID,MediaColumns.DATA };

		Cursor cursor = query(context, MediaStore.Audio.Playlists.Members.getContentUri("external", plid), ccols, null, null, MediaStore.Audio.Playlists.Members.DEFAULT_SORT_ORDER);

		if (cursor != null) {
			ArrayList<Mp3> list = getSongListForCursor(context, cursor);
			cursor.close();
			return list;
		}
		return null;
	}

	public static ArrayList<Mp3> getSongListForCursor(Context context, Cursor cursor) {
		if (cursor == null) {
			return null;
		}
		int len = cursor.getCount();
		long[] list = new long[len];
		cursor.moveToFirst();
		int id = -1, title = -1, artist = -1;
		int allSongIndex = -1;
		int url = -1;
		try {
			allSongIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.AUDIO_ID);
			id = cursor.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members._ID);
			title = cursor.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.TITLE);
			artist = cursor.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.ARTIST);
			url = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
		} catch (IllegalArgumentException ex) {
			id = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
		}
		ArrayList<Mp3> songs = new ArrayList<Mp3>();
		songs.clear();
		for (int i = 0; i < len; i++) {
			long song_id = cursor.getLong(id);
			long allSongId = cursor.getLong(allSongIndex);
			String song_url = cursor.getString(url);
			String tilte = cursor.getString(title);
			String song_artist = cursor.getString(artist);

			Mp3 song = new Mp3();
			song.setSqlId(Integer.parseInt(song_id + ""));
			song.setName(tilte);
//			song.setSingerName(song_artist);
			song.setAllSongIndex(allSongId);
			song.setUrl(song_url);
			songs.add(song);

			cursor.moveToNext();
		}
		return songs;
	}
	/**
	 * 将某一首歌添加到某个歌曲列表中，ids是歌曲id，playlistid是列表id
	 */
	public static void addToPlaylist(Context context, long[] ids, long playlistid) {
		if (ids == null) {
			Log.e("MusicBase", "ListSelection null");
		} else {
			int size = ids.length;
			ContentResolver resolver = context.getContentResolver();
			String[] cols = new String[] { "count(*)" };
			Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistid);
			Cursor cur = resolver.query(uri, cols, null, null, null);
			cur.moveToFirst();
			int base = cur.getInt(0);
			cur.close();
			int numinserted = 0;
			for (int i = 0; i < size; i += 1000) {
				makeInsertItems(ids, i, 1000, base);
				numinserted += resolver.bulkInsert(uri, sContentValuesCache);
			}
		}
	}

	private static ContentValues[] sContentValuesCache = null;

	private static void makeInsertItems(long[] ids, int offset, int len, int base) {
		if (offset + len > ids.length) {
			len = ids.length - offset;
		}
		if (sContentValuesCache == null || sContentValuesCache.length != len) {
			sContentValuesCache = new ContentValues[len];
		}
		for (int i = 0; i < len; i++) {
			if (sContentValuesCache[i] == null) {
				sContentValuesCache[i] = new ContentValues();
			}
			sContentValuesCache[i].put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, base + offset + i);
			sContentValuesCache[i].put(MediaStore.Audio.Playlists.Members.AUDIO_ID, ids[offset + i]);
		}
	}

	/**
	 * 得到媒体库中的全部歌曲
	 */
	public static ArrayList<Mp3> getAllSongs(Context context) {
		Cursor c = query(context, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[] { MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST,MediaColumns.DATA },
				MediaStore.Audio.Media.IS_MUSIC + "=1", null, null);
		try {
			if (c == null || c.getCount() == 0) {
				return null;
			}
			int len = c.getCount();

			ArrayList<Mp3> list = new ArrayList<Mp3>();

			int id = c.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
			int title = c.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
			int name = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
			int url = c.getColumnIndex(MediaColumns.DATA);
			for (int i = 0; i < len; i++) {
				Mp3 mp3 = new Mp3();
				c.moveToNext();
				mp3.setSqlId(Integer.parseInt(c.getLong(id) + ""));
				mp3.setName(c.getString(title));
//				mp3.setSingerName(c.getString(name));
				mp3.setUrl(c.getString(url));
				list.add(mp3);
			}

			return list;
		} finally {
			if (c != null) {
				c.close();
			}
		}
	}

	/**
	 * 通过歌曲列表名找到列表id
	 */
	public static long getPlayListId(Context context, String listName) {
		long listId = -1;
		ContentResolver cr = context.getContentResolver();
		Cursor cursor = cr.query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, mCols, null, null, MediaStore.Audio.Playlists._ID + " desc");
		al_playlist.clear();
		int len = cursor.getCount();
		int id = cursor.getColumnIndexOrThrow(MediaStore.Audio.Playlists._ID);
		int name = cursor.getColumnIndexOrThrow(MediaStore.Audio.Playlists.NAME);

		for (int i = 0; i < len; i++) {
			cursor.moveToNext();
			long id_temp = cursor.getLong(id);
			String temp = cursor.getString(name);
			if (listName.equals(temp)) {
				listId = id_temp;
			}
		}
		return listId;
	}

	public static List<Mp3> getPlayList() {
		return playList;
	}

	public static void setPlayList(List<Mp3> playList) {
		MusicUtils.playList = playList;
	}

	public static ArrayList<String> getAl_playlist() {
		return al_playlist;
	}

	public static void setAl_playlist(ArrayList<String> al_playlist) {
		MusicUtils.al_playlist = al_playlist;
	}

}
