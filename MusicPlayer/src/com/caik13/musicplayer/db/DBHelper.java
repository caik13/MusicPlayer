package com.caik13.musicplayer.db;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.caik13.musicplayer.entity.Song;

public class DBHelper extends SQLiteOpenHelper {

	private static DBHelper instance;
	private SQLiteDatabase db;

	// ���췽������� private ˽�еģ�Ϊ�˵���ģʽ
	private DBHelper(Context context) {
		super(context, "music", null, 1);
		getConnection();
	}

	/**
	 * ����DBHelperʵ��
	 * 
	 * @param context
	 * @return
	 */
	public static DBHelper newInstance(Context context) {
		if (null == instance) {
			instance = new DBHelper(context);
		}
		return instance;
	}

	/**
	 * ��ȡ���ݿ�����
	 */
	public SQLiteDatabase getConnection() {
		if (null == db) {
			db = this.getWritableDatabase();
		}
		return db;
	}

	// ������ݿⲻ���ڣ����Զ������������������
	@Override
	public void onCreate(SQLiteDatabase db) {
		createLastMusicTable(db);
		createMusicListTable(db);
		createResumeBrokenDownloadTable(db);
		createDownloadList(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// �������ݿ⣺�Ƚ�ԭ�еı�ɾ����Ȼ�����oncreate�������´���
		dropTable(db);
		onCreate(db);
	}

	private void dropTable(SQLiteDatabase db) {
		String sql = "drop table if exists last_music";
		String sql2 = "drop table if exists music_list";
		String sql3 = "drop table if exists resume_broken_download";
		String sql4 = "drop table if exists download_list";
		db.execSQL(sql);
		db.execSQL(sql2);
		db.execSQL(sql3);
		db.execSQL(sql4);
	}

	/**
	 * �����ϴβ��ŵ����ֱ�
	 * 
	 * @param db
	 */
	private void createLastMusicTable(SQLiteDatabase db) {
		StringBuffer tableSQL = new StringBuffer();
		tableSQL.append(" create table last_music( ")
				.append(" _id integer primary key autoincrement, ")
				.append(" name text, ").append(" artist text, ")
				.append(" path text, ").append(" duration text, ")
				.append(" mime_type text) ");
		db.execSQL(tableSQL.toString());
	}

	/**
	 * ���������б���Ϣ��
	 * 
	 * @param db
	 */
	private void createMusicListTable(SQLiteDatabase db) {
		StringBuffer tableSQL = new StringBuffer();
		tableSQL.append(" create table music_list( ")
				.append(" _id integer primary key autoincrement, ")
				.append(" name text, ").append(" artist text, ")
				.append(" path text, ").append(" duration text, ")
				.append(" mime_type text) ");
		db.execSQL(tableSQL.toString());
	}

	/**
	 * �����ϵ�������Ϣ��
	 * 
	 * @param db
	 */
	private void createResumeBrokenDownloadTable(SQLiteDatabase db) {
		StringBuffer tableSQL = new StringBuffer();
		tableSQL.append(" create table resume_broken_download( ")
				.append(" _id integer primary key autoincrement, ")
				.append(" threadname text, ").append(" filename text, ")
				.append(" url text, ").append(" range text, ")
				.append(" filepath text, ").append(" filesize text, ")
				.append(" downloadsize text) ");
		db.execSQL(tableSQL.toString());
	}

	/**
	 * ���������б�
	 * 
	 * @param db
	 */
	private void createDownloadList(SQLiteDatabase db) {
		StringBuffer tableSQL = new StringBuffer();
		tableSQL.append(" create table download_list( ")
				.append(" _id integer primary key autoincrement, ")
				.append(" filename text, ")
				.append(" url text, ")
				.append(" filepath text, ")
				.append(" filesize text, ")
				.append(" complete text, ")
				.append(" downloadsize text) ");
		db.execSQL(tableSQL.toString());
	}
	
	/**
	 * �����ϴβ�������
	 * 
	 * @param userInfo
	 */
	public long saveLastMusic(Song song) {
		ContentValues values = new ContentValues();
		values.put("name", song.getMusicName());
		values.put("artist", song.getArtist());
		values.put("path", song.getPath());
		values.put("duration", song.getDuration());
		values.put("mime_type", song.getMimeType());
		return db.insert("download_list", null, values);
	}

	/**
	 * ���������б�
	 * 
	 * @param userInfo
	 */
	public long saveDownloadList(String fileName, String url, String filePath, String fileSize, String complete, String downloaded) {
		ContentValues values = new ContentValues();
		values.put("filename", fileName);
		values.put("url", url);
		values.put("filepath", filePath);
		values.put("filesize", downloaded);
		values.put("complete", downloaded);
		values.put("downloadsize", downloaded);
		return db.insert("download_list", null, values);
	}

	/**
	 * ����ϵ�������Ϣ
	 * 
	 * @param userInfo
	 */
	public long saveResumeBrokenDownload(String threadName, String fileName,
			String url, String range, String filePath, String fileSize, String downloaded) {
		ContentValues values = new ContentValues();
		values.put("threadname", threadName);
		values.put("filename", fileName);
		values.put("url", url);
		values.put("range", range);
		values.put("filepath", filePath);
		values.put("filesize", fileSize);
		values.put("downloadsize", downloaded);
		return db.insert("resume_broken_download", null, values);
	}
	
	/**
	 * ��ȡ�洢�����ݿ��е�����
	 * 
	 * @param table
	 *            ����
	 * @param columns
	 *            Ҫ��ѯ������(select�����)
	 * @param selection
	 *            ����(where�����)
	 * @param selectionArgs
	 *            ��������ռλ����?����ֵ
	 * @param groupBy
	 * @param having
	 * @param orderBy
	 */
	// public List<MyAlarmClock> getAlarmClockDatas(){
	// // Cursor cursor = db.query(table, columns, selection, selectionArgs,
	// groupBy, having, orderBy);
	//
	// List<MyAlarmClock> alarms = new ArrayList<MyAlarmClock>();
	// Cursor cursor = db.query("alarms", null, null, null, null, null, null);
	// while (cursor.moveToNext()) {
	// MyAlarmClock myAlarmClock = new MyAlarmClock(cursor);
	// alarms.add(myAlarmClock);
	// }
	// cursor.close();
	//
	// return alarms;
	// }

	/**
	 * ��ȡ���һ�β�������
	 * 
	 * @return
	 */
	public Song getLastMusic() {
		Song song = null;
		Cursor cursor = db.query("last_music", null, null, null, null, null,
				null);
		while (cursor.moveToNext()) {
			song = new Song(cursor);
		}
		cursor.close();
		return song;
	}

	/**
	 * ��ȡ�ϵ�������Ϣ
	 * 
	 * @return
	 */
	public List<Map<String,String>> getResumeBrokenDownload(String fileName) {
		System.out.println(fileName);
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		Cursor cursor = db.query("resume_broken_download", null, "filename=?",
				new String[] { fileName }, null, null, null);
		if(cursor.getCount()>0){
			while (cursor.moveToNext()) {
				Map<String,String> map = new HashMap<String,String>();
				map.put("threadname", cursor.getString(cursor.getColumnIndex("threadname")));
				map.put("filename", cursor.getString(cursor.getColumnIndex("filename")));
				map.put("url", cursor.getString(cursor.getColumnIndex("url")));
				map.put("range", cursor.getString(cursor.getColumnIndex("range")));
				map.put("filepath", cursor.getString(cursor.getColumnIndex("filepath")));
				map.put("filesize", cursor.getString(cursor.getColumnIndex("filesize")));
				map.put("downloadsize", cursor.getString(cursor.getColumnIndex("downloadsize")));
				list.add(map);
			}
		}else{
			return null;
		}
		cursor.close();
		return list;
	}

	/**
	 * ���¶ϵ�������Ϣ
	 */
	public synchronized void updateResumeBrokenDownload(String downloadSize,String threadName){
		
		ContentValues values = new ContentValues();
		values.put("downloadsize", downloadSize);
		
		int row = db.update("resume_broken_download", values, "threadname=?", new String[]{threadName});
	}

	/**
	 * ��ȡ�����б���Ϣ
	 * 
	 * @return
	 */
	public List<Map<String,String>> getDownloadList() {
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		Cursor cursor = db.query("download_list", null, null, null, null, null, null);
		if(cursor.getCount()>0){
			while (cursor.moveToNext()) {
				Map<String,String> map = new HashMap<String,String>();
				map.put("filename", cursor.getString(cursor.getColumnIndex("filename")));
//				map.put("url", cursor.getString(cursor.getColumnIndex("url")));
//				map.put("filepath", cursor.getString(cursor.getColumnIndex("filepath")));
				map.put("filesize", cursor.getString(cursor.getColumnIndex("filesize")));
//				map.put("complete", cursor.getString(cursor.getColumnIndex("complete")));
//				map.put("downloadsize", cursor.getString(cursor.getColumnIndex("downloadsize")));
				list.add(map);
			}
		}
		cursor.close();
		return list;
	}
}
