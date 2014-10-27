package com.caik13.musicplayer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

public class Activity_Loading extends Activity {

	MediaPlayer mediaPlayer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_loading);
		
		mediaPlayer = MediaPlayer.create(this, R.raw.login);
		
		final SharedPreferences sharedPreferences = getSharedPreferences("setting", Context.MODE_WORLD_WRITEABLE);
		final SharedPreferences.Editor editor = sharedPreferences.edit();
		
		//启动service
		startService(new Intent("com.caik13.PLAY"));
		
		
		if(sharedPreferences.getBoolean("HELLO_KUGOU", true)){
			//播放 问候语
			mediaPlayer.start();
		}
		
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				if(sharedPreferences.getBoolean("isFirstStat", true)){
					//第一次启动
					editor.putBoolean("isFirstStat", false);
					editor.commit();
					
					Intent intent = new Intent(Activity_Loading.this, Activity_Guide.class);
					startActivity(intent);
					finish();
				}else{
					Intent intent = new Intent(Activity_Loading.this, Activity_Main.class);
					startActivity(intent);
					finish();
				}
			}
		}, 1000);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	
}
