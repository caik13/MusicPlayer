package com.caik13.musicplayer.entity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * ∏Ë ÷
 * @author Administrator
 *
 */
public class Singer {
	private String singerId;
	private String singerName;
	private String intro;
	private String songCount;
	private String albumCount;
	private String mvCount;
	private String imgUrl;

	
	public Singer(){
		
	}
	
	/**
	 * 
	 * @param json
	 */
	public Singer(JSONObject json){
		try {
			this.singerId = json.getString("singerid");
			this.singerName = json.getString("singername");
			this.intro = json.getString("intro");
			this.songCount = json.getString("songcount");
			this.albumCount = json.getString("albumcount");
			this.mvCount = json.getString("mvcount");
			this.imgUrl = json.getString("imgurl");
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public String getSingerId() {
		return singerId;
	}

	public void setSingerId(String singerId) {
		this.singerId = singerId;
	}

	public String getSingerName() {
		return singerName;
	}

	public void setSingerName(String singerName) {
		this.singerName = singerName;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public String getSongCount() {
		return songCount;
	}

	public void setSongCount(String songCount) {
		this.songCount = songCount;
	}

	public String getAlbumCount() {
		return albumCount;
	}

	public void setAlbumCount(String albumCount) {
		this.albumCount = albumCount;
	}

	public String getMvCount() {
		return mvCount;
	}

	public void setMvCount(String mvCount) {
		this.mvCount = mvCount;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

}
