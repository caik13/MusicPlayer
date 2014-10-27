/**
 * 
 */
package com.caik13.musicplayer.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Binder;
import android.os.IBinder;
import android.provider.MediaStore.Audio;
import android.widget.RemoteViews;

import com.caik13.musicplayer.Config;
import com.caik13.musicplayer.R;
import com.caik13.musicplayer.entity.Song;

/**
 * @author Administrator
 * 
 */
public class PlayMusicService extends Service {

	private MediaPlayer mediaPlayer;
	private NotificationManager notificationManager;
	private Notification notification;

	public List<Song> songs;
	public int position = 0;
	private int localMusicCount = 0;

	private boolean isPause = false;
	private boolean isUpdatePlayerUI = false;

	private Timer timer;
	private MyBinder mBinder = new MyBinder();
	final Intent intent = new Intent("com.caik13.UPDATE_UI");
	
	//������Ų���������ʱ�ø�������
	private String netSongsName = "";
	private String netSingerName = "";
	private String netSongsDuration = "";

	private BroadcastReceiver playBroadcastReceiver;

	/**
	 * ��������潻�����ڲ���
	 * 
	 * @author Administrator
	 * 
	 */
	public class MyBinder extends Binder {
		/**
		 * ����service����
		 * 
		 * @return
		 */
		public PlayMusicService getService() {
			return PlayMusicService.this;
		}
	}

	/**
	 * ����serviceʱ���ã�ֻ����һ��
	 */
	@Override
	public void onCreate() {
		mediaPlayer = new MediaPlayer();
		// ��ȡ��������
		getLocalMusicList();

		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				next(position + 1, "");
			}
		});

		mediaPlayer.setOnErrorListener(new OnErrorListener() {
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				// ����������������ִ���ʱĬ�Ϸ���false�����������OnCompletionListener���
				return true;
			}
		});
		setNotification();
		registBroastRec();
		super.onCreate();
	}

	/**
	 * ע��㲥������ ���𲥷�����
	 */
	private void registBroastRec() {
		BroadcastReceiver playBroadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getStringExtra("type") != null) {
					switch (intent.getStringExtra("type")) {
					case "close":
						// �ر�
						onDestroy();
						stopService(new Intent("com.caik13.PLAY"));
						System.exit(0);
						break;
					case "loop":
						// ����ѭ��ģʽ

						break;
					case "prev":
						// ��һ��
						prev(position - 1, "");
						updateNotification(songs.get(position).getMusicName(), "1");
						break;
					case "play":
						// ����/��ͣ
						String url = intent.getStringExtra("url");
						if (intent.getStringExtra("position") != null) {
							int position = Integer.parseInt(intent.getStringExtra("position"));
							if(position == -1 && url != null){
								netSongsName = intent.getStringExtra("netSongsName");
								netSingerName = intent.getStringExtra("netSingerName");
								
								play(position, url);
							}else{
								play(position, "");
								updateNotification(songs.get(position).getMusicName(), "1");
							}
						} else if (!mediaPlayer.isPlaying() && !isPause) {
							// ����
							play(position, "");
							updateNotification(songs.get(position).getMusicName(), "1");
						} else if (mediaPlayer.isPlaying()) {
							// ִ�� ��ͣ
							pause();
							if(position == -1){
								
							}else{
								updateNotification(songs.get(position).getMusicName(), "");
							}
						} else if (!mediaPlayer.isPlaying() && isPause) {
							// ִ�� ����
							pause();
							if(position == -1){
								
							}else{
								updateNotification(songs.get(position).getMusicName(), "1");
							}
						}
						break;
					case "next":
						// ��һ��
						next(position + 1, "");
						updateNotification(songs.get(position).getMusicName(), "1");
						break;
					case "isupdateplayerui":
						isUpdatePlayerUI = true;
						break;
					case "noupdateplayerui":
						isUpdatePlayerUI = false;
						break;
					}
				}
			}
		};
		registerReceiver(playBroadcastReceiver, new IntentFilter(
				"com.caik13.PLAY_MANAGER"));
	}

	/**
	 * ����֪ͨ��
	 */
	private void setNotification() {
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notification = new Notification.Builder(PlayMusicService.this)
				.setSmallIcon(R.drawable.alam_playback_icon)
				.setTicker("�߷¿ṷ����").build();

		updateNotification("����������", "");
	}

	/**
	 * ����notification
	 * @param songName	����������
	 * @param playerpause	�Ƿ����Ϊ��ͣ��ť
	 */
	private void updateNotification(String songName, String playerpause) {
		RemoteViews remoteView = new RemoteViews(getApplication()
				.getPackageName(), R.layout.notification);

		Intent playManagerIntent = new Intent("com.caik13.PLAY_MANAGER");
		playManagerIntent.putExtra("type", "close");
		remoteView.setOnClickPendingIntent(R.id.notification_close,
				PendingIntent.getBroadcast(PlayMusicService.this, 0,
						playManagerIntent, 0));

		playManagerIntent.putExtra("type", "loop");
		remoteView.setOnClickPendingIntent(R.id.notification_loop,
				PendingIntent.getBroadcast(PlayMusicService.this, 2,
						playManagerIntent, 0));

		playManagerIntent.putExtra("type", "prev");
		remoteView.setOnClickPendingIntent(R.id.notification_prev,
				PendingIntent.getBroadcast(PlayMusicService.this, 3,
						playManagerIntent, 0));

		playManagerIntent.putExtra("type", "play");
		remoteView.setOnClickPendingIntent(R.id.notification_play,
				PendingIntent.getBroadcast(PlayMusicService.this, 4,
						playManagerIntent, 0));

		playManagerIntent.putExtra("type", "next");
		remoteView.setOnClickPendingIntent(R.id.notification_next,
				PendingIntent.getBroadcast(PlayMusicService.this, 5,
						playManagerIntent, 0));

		remoteView.setTextViewText(R.id.notification_songname, songName);
		if(!playerpause.equals("")){
			remoteView.setImageViewResource(R.id.notification_play, R.drawable.alarm_pause_btn);
		}else{
			remoteView.setImageViewResource(R.id.notification_play, R.drawable.alarm_play_btn);
		}
			
		notification.bigContentView = remoteView;
		notification.flags = notification.FLAG_ONGOING_EVENT;
		notification.contentView = remoteView;

		notificationManager.notify(0, notification);
	}

	/**
	 * ��ȡ���б�������
	 */
	private void getLocalMusicList() {
		songs = new ArrayList<Song>();
		Cursor cursor = getContentResolver().query(
				Audio.Media.EXTERNAL_CONTENT_URI, null, "duration>60000", null,
				null);
		Config.MUSIC_COUNT = cursor.getCount();
		while (cursor.moveToNext()) {
			String musicName = cursor.getString(cursor
					.getColumnIndex(Audio.Media.TITLE));
			String path = cursor.getString(cursor
					.getColumnIndex(Audio.Media.DATA));
			String artist = cursor.getString(cursor
					.getColumnIndex(Audio.Media.ARTIST));
			String duration = cursor.getString(cursor
					.getColumnIndex(Audio.Media.DURATION));
			Song song = new Song(musicName, path, artist, duration, "");
			songs.add(song);
		}

//		// ���͹㲥���½����������
//		localMusicCount = cursor.getCount();
//		Intent countIntent = new Intent("com.caik13.UPDATE_UI");
//		countIntent.putExtra("musiccount", localMusicCount + "");
//		sendBroadcast(countIntent);
	}

	/**
	 * ����
	 */
	public void play(int position, String url) {
		if (!mediaPlayer.isPlaying()) {
			initPlay(position, url);
		} else {
			next(position, url);
		}
	}

	/**
	 * �������Ų���
	 * 
	 * @param position
	 */
	private void initPlay(int position, String url) {
		this.position = position;

		if(isPause){
			mediaPlayer.reset();
		}
		
		try {
			if(position == -1 && url != null && !url.equals("")){
				mediaPlayer.setDataSource(url);
			}else{
				String path = songs.get(position).getPath();
				mediaPlayer.setDataSource(path);
			}
			
			mediaPlayer.prepare();
			mediaPlayer.start();
			updateProgress(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ��ͣ
	 */
	public void pause() {
		if (isPause) {
			mediaPlayer.start();
			isPause = false;
			updateProgress(true);
		} else {
			mediaPlayer.pause();
			isPause = true;
			updateProgress(false);
		}
	}

	/**
	 * ��һ��
	 */
	public void next(int position, String url) {
		mediaPlayer.reset();
		initPlay(position, url);
	}

	/**
	 * ��һ��
	 */
	public void prev(int position, String url) {
		mediaPlayer.reset();
		initPlay(position, url);
	}

	/**
	 * ��ʱ����ÿ�����ڻ�ȡ���Ž���
	 */
	private void updateProgress(boolean isUpdate) {
		Intent newIntent = new Intent("com.caik13.UPDATE_UI");
		final Intent playerIntent = new Intent("com.caik13.UPDATE_PLAYER_UI");
		
		if (timer == null) {
			timer = new Timer();
		}
		if (isUpdate) {
			newIntent.putExtra("type", "updateSongName");
			if(position == -1){
				//position ���� -1 �������������������Ҫ�Ѹ������֡��������֡�����ʱ��ͨ���㲥����ȥ��
				newIntent.putExtra("position", "-1");
				newIntent.putExtra("netSongsName", netSongsName);
				newIntent.putExtra("netSingerName", netSingerName);
				newIntent.putExtra("netSongsDuration", mediaPlayer.getDuration() + "");
			}
			sendBroadcast(newIntent);
			
//			if(isUpdatePlayerUI){
				sendPlayerUpdate(playerIntent, "1");
//			}
			
			timer.scheduleAtFixedRate(new TimerTask() {

				@Override
				public void run() {
					intent.putExtra("progress",	mediaPlayer.getCurrentPosition());
					sendBroadcast(intent);

//					if(isUpdatePlayerUI){
						sendPlayerUpdate(playerIntent, "2");
//					}
				}
			}, 0, 1000);
		} else {
			timer.cancel();
			timer = null;
			newIntent.putExtra("type", "updateSongName");
			newIntent.putExtra("playorpause", "pause");
			sendBroadcast(newIntent);

//			if(isUpdatePlayerUI){
				sendPlayerUpdate(playerIntent, "3");
//			}
		}
	}

	/**
	 * ���͹㲥
	 * @param playerIntent
	 * @param type	1:���½��档2:���²��Ž�������3:���²���/��ͣ��ť
	 */
	private void sendPlayerUpdate(Intent playerIntent, String type){
		switch(type){
		case "1":
			playerIntent.putExtra("type", "updateSongName");
			sendBroadcast(playerIntent);
			break;
		case "2":
			playerIntent.putExtra("progress",
					mediaPlayer.getCurrentPosition());
			sendBroadcast(playerIntent);
			break;
		case "3":
			playerIntent.putExtra("type", "updateSongName");
			playerIntent.putExtra("playorpause", "pause");
			sendBroadcast(playerIntent);
			break;
		}
	}
	
	/**
	 * ����startServiceʱ�����
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}

	/**
	 * ��service��ʱ����
	 */
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return mBinder;
	}

	/**
	 * ��service�����ʱ����
	 */
	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		return super.onUnbind(intent);
	}

	@Override
	public void onDestroy() {
		System.out.println("ȡ��֪ͨ");
		notificationManager.cancel(0);
		if (mediaPlayer.isPlaying()) {
			mediaPlayer.stop();
		}
		super.onDestroy();
	}
}
