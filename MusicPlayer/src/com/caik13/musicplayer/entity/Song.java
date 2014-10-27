package com.caik13.musicplayer.entity;

import java.io.Serializable;

import android.database.Cursor;

public class Song implements Serializable{
	private String musicName;
	private String path;
	private String artist;
	private String duration;
	private String mimeType;
	private String position;

	public Song() {

	}

	public Song(String musicName, String path, String artist, String duration, String position) {
		this.musicName = musicName;
		this.path = path;
		this.artist = artist;
		this.duration = duration;
		this.position = position;
	}

	public Song(Cursor cursor) {
		this.musicName = cursor.getString(cursor.getColumnIndex("name"));
		this.path = cursor.getString(cursor.getColumnIndex("path"));
		this.artist = cursor.getString(cursor.getColumnIndex("artist"));
		this.duration = cursor.getString(cursor.getColumnIndex("duration"));
		this.mimeType = cursor.getString(cursor.getColumnIndex("mimeType"));
		this.position = cursor.getString(cursor.getColumnIndex("position"));
	}

	public String getMusicName() {
		return musicName;
	}

	public void setMusicName(String musicName) {
		this.musicName = musicName;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

}
