package com.caik13.musicplayer;

import java.util.HashMap;

import com.caik13.musicplayer.entity.Song;
import com.caik13.musicplayer.service.PlayMusicService;
import com.caik13.musicplayer.service.PlayMusicService.MyBinder;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.SeekBar;

public class Activity_Play extends Activity {

	float touchX = 0;
	
	public PlayMusicService playMusicService;
	private ServiceConnection mServiceConnection;
	
	private SeekBar playProgress;
	private ImageButton playLoop;
	private ImageButton playPrev;
	private ImageButton playPlay;
	private ImageButton playNext;
	private ImageButton playMenu;
	
	private boolean isPause = false;
	private int progressTemp = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//����û�б���
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_play);
		
		playProgress = (SeekBar) findViewById(R.id.play_progress);
		playLoop = (ImageButton) findViewById(R.id.play_down_loop);
		playPrev = (ImageButton) findViewById(R.id.play_down_prev_music);
		playPlay = (ImageButton) findViewById(R.id.play_down_play_music);
		playNext = (ImageButton) findViewById(R.id.play_down_next_music);
		playMenu = (ImageButton) findViewById(R.id.play_down_menu);
		

		//��̬ע��㲥������
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("com.caik13.UPDATE_PLAYER_UI");
		registerReceiver(new updatePlayerUIReceiver(), intentFilter);
		
//		Intent intent = new Intent("com.caik13.PLAY_MANAGER");
//		intent.putExtra("type", "isupdateplayerui");
//		sendBroadcast(intent);
		init();
	}
	
	private void init() {

		// ���ڰ�service
		mServiceConnection = new ServiceConnection() {
			// ����󶨺����
			@Override
			public void onServiceDisconnected(ComponentName name) {
			}

			// �󶨳ɹ������
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				playMusicService = ((MyBinder) service).getService();
			}
		};
		//��service
		bindService(new Intent("com.caik13.PLAY"), mServiceConnection, Service.BIND_AUTO_CREATE);
	}
	
	/**
	 * ���½���Ĺ㲥������
	 * @author Administrator
	 *
	 */
	class updatePlayerUIReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			System.out.println("2");
			//���²��Ž���
			int progress = arg1.getIntExtra("progress", 0);
			if(progress != 0){
				progressTemp = progress;
			}
			playProgress.setProgress(progress);
			
			//���¸�������
			if(arg1.getStringExtra("type") != null && arg1.getStringExtra("type").equals("updateSongName")){
				if(arg1.getStringExtra("playerpause") != null && arg1.getStringExtra("playerpause").equals("pause")){
					updateSongName("pause");
				}else{
					updateSongName("");
				}
			}
		}
	}
	
	/**
	 * ���½�����ʾ�������ֺ͸�������Ϊ��ǰ����
	 */
	public void updateSongName(String playOrPause){
//		int position = playMusicService.position;
//		musicName.setText(songNames.get(position).get("songname"));
//		artist.setText(songNames.get(position).get("artist"));
//		musicProgress.setMax(Integer.parseInt(songNames.get(position).get("duration")));
		
		playProgress.setMax(Integer.parseInt(playMusicService.songs.get(playMusicService.position).getDuration()));
		
		if(playOrPause.equals("pause")){
			playPlay.setImageResource(R.drawable.ic_player_play_default);
			playProgress.setProgress(progressTemp);
			isPause = false;
		}else{
			playPlay.setImageResource(R.drawable.ic_player_play_default);
			isPause = true;
		}
	}
	

	/**
	 * activity�л�����
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			touchX = event.getX();
		}
		
		Intent intent = new Intent();
		intent.setClass(Activity_Play.this, Activity_Main.class);
		
		if(event.getAction() == MotionEvent.ACTION_UP && event.getX() - touchX < 0){
			startActivity(intent);
			overridePendingTransition(R.anim.no_rotate, R.anim.right_to_left_rotate_out);
		}
		
		if(event.getAction() == MotionEvent.ACTION_UP && event.getX() - touchX > 0){
			startActivity(intent);
			overridePendingTransition(R.anim.no_rotate, R.anim.left_to_right_rotate_out);
		}
		return super.onTouchEvent(event);
	}
}
