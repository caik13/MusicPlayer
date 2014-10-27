package com.caik13.musicplayer.service.download;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;

import com.caik13.musicplayer.db.DBHelper;

public class MyDownloadService extends Service {

	private DownloadBinder downloadBinder = new DownloadBinder();

	private HttpClient httpClient;
	private RandomAccessFile randomAccessFile;

	private DBHelper db;

	private MyDownloadThread[] myThreads;
	private int threadCount = 3;
	
	//当前下载进度，用于界面显示
	private int downloadProgress = 0;

	/**
	 * 用于与界面交互的内部类
	 * 
	 * @author Administrator
	 * 
	 */
	public class DownloadBinder extends Binder {
		/**
		 * 返回service对象
		 * 
		 * @return
		 */
		public MyDownloadService getService() {

			return MyDownloadService.this;
		}
	}
	
	/**
	 * 控制下载
	 * @author Administrator
	 *
	 */
	class DownloadManage extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			if(null != intent.getStringExtra("startDown") && "true".equals(intent.getStringExtra("startDown"))){
				if(null != intent.getStringExtra("url") && null != intent.getStringExtra("fileName")){
					String url = intent.getStringExtra("url");
					String fileName = intent.getStringExtra("fileName");
					//启动下载
					startDownload(url, fileName);
				}
			}
			//暂停
			if(null != intent.getStringExtra("pauseDownload") && "true".equals(intent.getStringExtra("pauseDownload"))){
				pauseDownload();
			}
		}
	}

	@Override
	public void onCreate() {
		db = DBHelper.newInstance(getApplicationContext());
		
		registerReceiver(new DownloadManage(), new IntentFilter("com.caik13.DOWNLOAD_MANAGE"));
		
		super.onCreate();
	}
	
	@Override
	public IBinder onBind(Intent arg0) {

		return downloadBinder;
	}

	/**
	 * 获取下载进度
	 */
	public int getDownloadProgress(){
		int downloadProgress = 0;
		for (MyDownloadThread myThread : myThreads) {
			downloadProgress += myThread.getThreadDownloadProgress();
		}
		return downloadProgress;
	}
	
	/**
	 * 判断有没有SD卡，并获得根目录
	 */
	private File getSDCardPath() {
		File file = null;
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			file = Environment.getExternalStorageDirectory();
		}

		return file;
	}

	/**
	 * 获取断点续传信息(数据库)
	 */
	private List<Map<String, String>> getResumeBrokenDownload(String fileName) {
		return db.getResumeBrokenDownload(fileName);
	}

	/**
	 * 开始下载
	 * 
	 * @param url
	 *            下载地址
	 * @param fileName
	 *            本地存的文件名字
	 */
	public void startDownload(final String url, final String fileName) {
		myThreads = new MyDownloadThread[4];

		httpClient = new DefaultHttpClient();

		Thread a = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					// 执行请求
					HttpHead httpHead = new HttpHead(url);
					HttpResponse httpResponse = httpClient.execute(httpHead);

					int fileSize = Integer.parseInt(httpResponse
							.getHeaders("Content-Length")[0].getValue());

					// 获取数据库中断点续传信息
					List<Map<String, String>> resumeBrokenDownloads = getResumeBrokenDownload(fileName);

					File fil = new File(getSDCardPath(), fileName + ".mp3");
					
					// 是否为断点续传
					boolean isResume = false;
					int downloadedSize = 0;
					
					//保存到下载列表
					saveDownloadList(fileName, url, fil.getPath(), fileSize + "", "false", "0");
					//发送广播更新下载界面
					sendBroadcastUpdateUI(fileName, fileSize + "", downloadedSize);

					if (resumeBrokenDownloads == null
							|| resumeBrokenDownloads.size() <= 0) {
						// 执行正常下载
						downloadedSize = 0;
					} else {
						// 执行断点续传下载,如果文件已经不存在，则重新下载
						if (fil.exists()) {
							isResume = true;
							
						} else {
							isResume = false;
							
						}
					}

					randomAccessFile = new RandomAccessFile(fil, "rwd");
					randomAccessFile.setLength(fileSize);
					randomAccessFile.close();

					int needDownloadLen = fileSize / threadCount;
					// 如果没有整除，剩下的一部分的大小
					int lastNeedDownloadLen = fileSize - threadCount
							* needDownloadLen;

					for (int i = 0; i < threadCount; i++) {

						int startPosition = i * needDownloadLen;
						int endPosition = (i + 1) * needDownloadLen - 1;

						if (!isResume) {
							// 保存下载进度到数据库，为断点续传用
							db.saveResumeBrokenDownload("thread" + i, fileName,
									url, needDownloadLen + "", fil.getPath(),fileSize+"",
									startPosition + "");
						} else {
							for (Map<String, String> map : resumeBrokenDownloads) {
								if (map.containsKey("线程" + fileName + i)) {
									downloadedSize = Integer.parseInt(map
											.get("downloadsize"));
								}
							}
						}

						MyDownloadThread mydownloadThread = new MyDownloadThread(
								"线程" + fileName + i, fil, url, startPosition,
								endPosition, downloadedSize, needDownloadLen, db);
//						Thread thread = new Thread(mydownloadThread);
//						thread.setName("thread" + i);
//						thread.start();

						mydownloadThread.setName("线程" + fileName + i);
						mydownloadThread.start();
						
						myThreads[i] = mydownloadThread;
					}

					// 处理：文件大小和线程数没有整除情况剩下来的一部分文件，再开一个线程去下载剩下的部分。
					if (lastNeedDownloadLen > 0) {
						int startPosition = threadCount * needDownloadLen;
						int endPosition = startPosition + lastNeedDownloadLen;

						if (!isResume) {
							// 保存下载进度到数据库，为断点续传用
							db.saveResumeBrokenDownload("thread" + threadCount,
									fileName, url, needDownloadLen + "",
									fil.getPath(),fileSize+"", startPosition + "");
						} else {
							for (Map<String, String> map : resumeBrokenDownloads) {
								if (map.containsKey("线程" + fileName
										+ threadCount)) {
									downloadedSize = Integer.parseInt(map
											.get("downloadsize"));
								}
							}
						}

						MyDownloadThread mydownloadThread = new MyDownloadThread(
								"线程" + threadCount, fil, url, startPosition,
								endPosition, downloadedSize, endPosition
										- startPosition, db);
//						Thread thread = new Thread(mydownloadThread);
//						thread.setName("thread" + fileName + threadCount);
//						thread.start();

						mydownloadThread.setName("线程" + fileName + threadCount);
						mydownloadThread.start();
						
						myThreads[3] = mydownloadThread;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		a.start();

	}

	/**
	 * 发送广播更新UI
	 * @param fileName 文件名字
	 * @param fileSize 文件大小
	 * @param downloadProgress 当前下载进度(当前已下载字节)
	 */
	private void sendBroadcastUpdateUI(String fileName, String fileSize, int downloadProgress){
		Intent intent = new Intent("com.caik13.UPDATE_DOWNLOAD_UI");
		intent.putExtra("update", "true");
//		intent.putExtra("fileName", fileName);
//		intent.putExtra("fileSize", fileSize);
//		intent.putExtra("downloadProgress", downloadProgress);
		sendBroadcast(intent);
	}
	
	/**
	 * 把当前下载保存到下载列表
	 * @param fileName 文件名字
	 * @param url 下载地址
	 * @param filePath 保存路径
	 * @param fileSize 文件大小
	 * @param complete 是否下载完成
	 * @param downloaded 已经下载的字节数
	 */
	private void saveDownloadList(String fileName, String url, String filePath, String fileSize, String complete, String downloaded){
		db.saveDownloadList(fileName, url, filePath, fileSize, complete, downloaded);
	}
	
	/**
	 * 暂停下载
	 */
	public void pauseDownload() {
		for (MyDownloadThread thread : myThreads) {
			thread.setIsStop(true);
		}
	}
}
