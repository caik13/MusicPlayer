package com.caik13.musicplayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.caik13.musicplayer.Activity_Play.updatePlayerUIReceiver;
import com.caik13.musicplayer.db.DBHelper;
import com.caik13.musicplayer.entity.Song;
import com.caik13.musicplayer.service.PlayMusicService;
import com.caik13.musicplayer.service.PlayMusicService.MyBinder;
import com.caik13.musicplayer.service.download.MyDownloadService;
import com.caik13.musicplayer.service.download.MyDownloadService.DownloadBinder;

public class Activity_Main extends FragmentActivity {

	public List<Fragment> fragmentLists;
	public ViewPager viewPager;
	
	public RelativeLayout playingBar;	
	private Button playMusic;
	private Button nextMusic;
	public TextView musicName;
	public TextView artist;
	public SeekBar musicProgress;
	
	private ServiceConnection mServiceConnection;
	private PlayMusicService.MyBinder myBinder;
	public PlayMusicService playMusicService;

	private MyDownloadService.DownloadBinder downloadBinder;
	public MyDownloadService dowmloadService;
	
	
	private List<Song> songList;
	public List<Map<String,String>> songNames;
	private DBHelper db;
	private boolean isPause = false;
	//歌曲当前播放进度
	private int progressTemp = 0;
	//歌曲总时间长度
	private int progressMax = 0;
	
	public Adapter_MyFragmentPager fragmentAdapter;
	private Song lastMusic;

	//用来存放播网络音乐时得歌曲数据
	private String netSongsName = "";
	private String netSingerName = "";
	private String netSongsDuration = "";
	
	//
	private Intent intent = new Intent("com.caik13.PLAY_MANAGER");
	
	//广播接收器-内部类
	//负责更新界面
	class updateUIReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			//更新播放进度
			int progress = arg1.getIntExtra("progress", 0);
			if(progress != 0){
				progressTemp = progress;
			}
			musicProgress.setProgress(progress);
			
			//更新歌曲名字
			if(arg1.getStringExtra("type") != null && arg1.getStringExtra("type").equals("updateSongName")){
				if(arg1.getStringExtra("position") != null && arg1.getStringExtra("position").equals("-1")){
					netSongsName = arg1.getStringExtra("netSongsName"); 
					netSingerName = arg1.getStringExtra("netSingerName"); 
					netSongsDuration = arg1.getStringExtra("netSongsDuration"); 
				}
				
				if(arg1.getStringExtra("playorpause") != null && arg1.getStringExtra("playorpause").equals("pause")){
					updateSongName("pause");
				}else{
					updateSongName("");
				}
			}
			
//			//更新歌曲数量
//			if(arg1.getStringExtra("musiccount") != null){
//				updateLocalMusicCount(arg1.getStringExtra("musiccount"));
//			}
		}
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//设置没有标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		init();
		initViewPager();
		
		//动态注册广播接收器
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("com.caik13.UPDATE_UI");
		registerReceiver(new updateUIReceiver(), intentFilter);
		
	}
	
	private void init(){
		playMusic = (Button) findViewById(R.id.play_music);
		nextMusic = (Button) findViewById(R.id.next_music);
		musicName = (TextView) findViewById(R.id.play_music_name);
		artist = (TextView) findViewById(R.id.play_music_singer);
		musicProgress = (SeekBar) findViewById(R.id.music_progress);
		playingBar = (RelativeLayout) findViewById(R.id.playing_bar);
		
		
		db = DBHelper.newInstance(Activity_Main.this);
		
		songNames = new ArrayList<Map<String,String>>();
		//获取最后一次播放的音乐
//		lastMusic = getLastMusic();
		if(lastMusic != null){
			musicName.setText(lastMusic.getMusicName());
			artist.setText(lastMusic.getArtist());
		}
		
		//打开播放activity
		playingBar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent playIntent = new Intent();
				playIntent.setClass(Activity_Main.this, Activity_Play.class);
				startActivity(playIntent);
				overridePendingTransition(R.anim.right_to_left_rotate_in, R.anim.no_rotate);
			}
		});
		
		//播放音乐
		playMusic.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(lastMusic != null){
					intent.putExtra("type", "play");
					sendBroadcast(intent);
				}
				
				if(isPause){
					intent.putExtra("type", "play");
					sendBroadcast(intent);
				} else {
					// 没有暂停,执行暂停
					intent.putExtra("type", "play");
					sendBroadcast(intent);
				}
			}
		});

		
		//下一曲
		nextMusic.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				intent.putExtra("type", "next");
				sendBroadcast(intent);
			}
		});
		
		//用于绑定service
		mServiceConnection = new ServiceConnection() {
			//解除绑定后调用
			@Override
			public void onServiceDisconnected(ComponentName name) {
			}
			
			//绑定成功后调用
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				myBinder = (MyBinder) service;
				playMusicService = myBinder.getService();
				songList = playMusicService.songs;
				for (Song song : songList) {
					HashMap<String,String> map = new HashMap<String,String>();
					map.put("songname", song.getMusicName());
					map.put("artist", song.getArtist());
					map.put("duration", song.getDuration());
					songNames.add(map);
				}
			}
		};
		
		ServiceConnection mServiceConnection2 = new ServiceConnection() {
			//解除绑定后调用
			@Override
			public void onServiceDisconnected(ComponentName name) {
			}
			
			//绑定成功后调用
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				downloadBinder = (DownloadBinder) service;
				dowmloadService = downloadBinder.getService();
//				dowmloadService.startDownload("http://115.28.88.132:8080/aa/Butterfly.mp3", "aa.mp3");
			}
		};
		//绑定service
		bindService(new Intent("com.caik13.PLAY"), mServiceConnection, Service.BIND_AUTO_CREATE);
		//后台下载服务
		bindService(new Intent("com.caik13.musicplayer.DOWNLOAD"), mServiceConnection2, Service.BIND_AUTO_CREATE);
	}
	
	
//	public void playOrPause(){
//		playMusic
//		.setCompoundDrawablesRelativeWithIntrinsicBounds(
//				null,
//				Activity_Main.this.getResources().getDrawable(
//								R.drawable.playing_bar_pause_music_selector),
//				null, null);
//		isPause = true;
//	}
	
	/**
	 * 初始化ViewPager
	 */
	private void initViewPager() {
		viewPager = (ViewPager) findViewById(R.id.vPager);
		
		fragmentLists = new ArrayList<Fragment>();
		// 分别加载2个fragment，并放到ArrayList中
		fragmentLists.add(new Fragment_More(Activity_Main.this));
		fragmentLists.add(new Fragment_Main(Activity_Main.this));

		FragmentManager fm = getSupportFragmentManager();
		fragmentAdapter = new Adapter_MyFragmentPager(fm, fragmentLists);
		viewPager.setAdapter(fragmentAdapter);
		viewPager.setCurrentItem(1);
	}
	
	/**
	 * 更新界面显示歌曲名字和歌手名字为当前播放
	 */
	public void updateSongName(String playOrPause){
		int position = playMusicService.position;
		if(position == -1){
			//position 等于 -1 代表播放网络歌曲，歌曲的名字要从广播中获取。
			musicName.setText(netSongsName);
			artist.setText(netSingerName);
			musicProgress.setMax(Integer.parseInt(netSongsDuration));
		}else{
			musicName.setText(songNames.get(position).get("songname"));
			artist.setText(songNames.get(position).get("artist"));
			musicProgress.setMax(Integer.parseInt(songNames.get(position).get("duration")));
		}
		
		if(playOrPause.equals("pause")){
			playMusic.setCompoundDrawablesRelativeWithIntrinsicBounds(
					null,Activity_Main.this.getResources().getDrawable(
					R.drawable.playing_bar_playmusic_selector),	null, null);

			musicProgress.setProgress(progressTemp);
			isPause = false;
		}else{
			playMusic.setCompoundDrawablesRelativeWithIntrinsicBounds(
					null,Activity_Main.this.getResources().getDrawable(
					R.drawable.playing_bar_pause_music_selector), null, null);
			isPause = true;
		}
	}
	
	/**
	 * 获取最后一次播放的音乐
	 */
	private Song getLastMusic(){
		Song song = db.getLastMusic();
		return song;
	}

	
	/**
	 * 退出时保存最后播放的歌曲
	 */
	@Override
	protected void onDestroy() {
		Song song = songList.get(playMusicService.position);
		db.saveLastMusic(song);
		super.onDestroy();
	}
	
}
