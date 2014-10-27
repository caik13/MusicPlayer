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
	
	//��ǰ���ؽ��ȣ����ڽ�����ʾ
	private int downloadProgress = 0;

	/**
	 * ��������潻�����ڲ���
	 * 
	 * @author Administrator
	 * 
	 */
	public class DownloadBinder extends Binder {
		/**
		 * ����service����
		 * 
		 * @return
		 */
		public MyDownloadService getService() {

			return MyDownloadService.this;
		}
	}
	
	/**
	 * ��������
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
					//��������
					startDownload(url, fileName);
				}
			}
			//��ͣ
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
	 * ��ȡ���ؽ���
	 */
	public int getDownloadProgress(){
		int downloadProgress = 0;
		for (MyDownloadThread myThread : myThreads) {
			downloadProgress += myThread.getThreadDownloadProgress();
		}
		return downloadProgress;
	}
	
	/**
	 * �ж���û��SD��������ø�Ŀ¼
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
	 * ��ȡ�ϵ�������Ϣ(���ݿ�)
	 */
	private List<Map<String, String>> getResumeBrokenDownload(String fileName) {
		return db.getResumeBrokenDownload(fileName);
	}

	/**
	 * ��ʼ����
	 * 
	 * @param url
	 *            ���ص�ַ
	 * @param fileName
	 *            ���ش���ļ�����
	 */
	public void startDownload(final String url, final String fileName) {
		myThreads = new MyDownloadThread[4];

		httpClient = new DefaultHttpClient();

		Thread a = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					// ִ������
					HttpHead httpHead = new HttpHead(url);
					HttpResponse httpResponse = httpClient.execute(httpHead);

					int fileSize = Integer.parseInt(httpResponse
							.getHeaders("Content-Length")[0].getValue());

					// ��ȡ���ݿ��жϵ�������Ϣ
					List<Map<String, String>> resumeBrokenDownloads = getResumeBrokenDownload(fileName);

					File fil = new File(getSDCardPath(), fileName + ".mp3");
					
					// �Ƿ�Ϊ�ϵ�����
					boolean isResume = false;
					int downloadedSize = 0;
					
					//���浽�����б�
					saveDownloadList(fileName, url, fil.getPath(), fileSize + "", "false", "0");
					//���͹㲥�������ؽ���
					sendBroadcastUpdateUI(fileName, fileSize + "", downloadedSize);

					if (resumeBrokenDownloads == null
							|| resumeBrokenDownloads.size() <= 0) {
						// ִ����������
						downloadedSize = 0;
					} else {
						// ִ�жϵ���������,����ļ��Ѿ������ڣ�����������
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
					// ���û��������ʣ�µ�һ���ֵĴ�С
					int lastNeedDownloadLen = fileSize - threadCount
							* needDownloadLen;

					for (int i = 0; i < threadCount; i++) {

						int startPosition = i * needDownloadLen;
						int endPosition = (i + 1) * needDownloadLen - 1;

						if (!isResume) {
							// �������ؽ��ȵ����ݿ⣬Ϊ�ϵ�������
							db.saveResumeBrokenDownload("thread" + i, fileName,
									url, needDownloadLen + "", fil.getPath(),fileSize+"",
									startPosition + "");
						} else {
							for (Map<String, String> map : resumeBrokenDownloads) {
								if (map.containsKey("�߳�" + fileName + i)) {
									downloadedSize = Integer.parseInt(map
											.get("downloadsize"));
								}
							}
						}

						MyDownloadThread mydownloadThread = new MyDownloadThread(
								"�߳�" + fileName + i, fil, url, startPosition,
								endPosition, downloadedSize, needDownloadLen, db);
//						Thread thread = new Thread(mydownloadThread);
//						thread.setName("thread" + i);
//						thread.start();

						mydownloadThread.setName("�߳�" + fileName + i);
						mydownloadThread.start();
						
						myThreads[i] = mydownloadThread;
					}

					// �����ļ���С���߳���û���������ʣ������һ�����ļ����ٿ�һ���߳�ȥ����ʣ�µĲ��֡�
					if (lastNeedDownloadLen > 0) {
						int startPosition = threadCount * needDownloadLen;
						int endPosition = startPosition + lastNeedDownloadLen;

						if (!isResume) {
							// �������ؽ��ȵ����ݿ⣬Ϊ�ϵ�������
							db.saveResumeBrokenDownload("thread" + threadCount,
									fileName, url, needDownloadLen + "",
									fil.getPath(),fileSize+"", startPosition + "");
						} else {
							for (Map<String, String> map : resumeBrokenDownloads) {
								if (map.containsKey("�߳�" + fileName
										+ threadCount)) {
									downloadedSize = Integer.parseInt(map
											.get("downloadsize"));
								}
							}
						}

						MyDownloadThread mydownloadThread = new MyDownloadThread(
								"�߳�" + threadCount, fil, url, startPosition,
								endPosition, downloadedSize, endPosition
										- startPosition, db);
//						Thread thread = new Thread(mydownloadThread);
//						thread.setName("thread" + fileName + threadCount);
//						thread.start();

						mydownloadThread.setName("�߳�" + fileName + threadCount);
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
	 * ���͹㲥����UI
	 * @param fileName �ļ�����
	 * @param fileSize �ļ���С
	 * @param downloadProgress ��ǰ���ؽ���(��ǰ�������ֽ�)
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
	 * �ѵ�ǰ���ر��浽�����б�
	 * @param fileName �ļ�����
	 * @param url ���ص�ַ
	 * @param filePath ����·��
	 * @param fileSize �ļ���С
	 * @param complete �Ƿ��������
	 * @param downloaded �Ѿ����ص��ֽ���
	 */
	private void saveDownloadList(String fileName, String url, String filePath, String fileSize, String complete, String downloaded){
		db.saveDownloadList(fileName, url, filePath, fileSize, complete, downloaded);
	}
	
	/**
	 * ��ͣ����
	 */
	public void pauseDownload() {
		for (MyDownloadThread thread : myThreads) {
			thread.setIsStop(true);
		}
	}
}
