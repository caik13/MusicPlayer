package com.caik13.musicplayer.service.download;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.RandomAccessFile;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.caik13.musicplayer.db.DBHelper;

/**
 * 下载子线程(真正执行下载操作的)
 * 
 * @author Administrator
 * 
 */
public class MyDownloadThread extends Thread {

	private HttpClient httpClient;
	private String url;
	private int startPosition;
	private int endPosition;
	private HttpResponse httpResponse;
	private RandomAccessFile randomAccessFile;
	private File file;
	// 已经下载的长度
	private int downloadLen;
	private int needDownloadLen;
	private DBHelper db;

	private String name;

	public boolean isStop = false;

	private BufferedInputStream bis = null;

	/**
	 * 
	 * @param httpClient
	 * @param url
	 *            下载地址
	 * @param startPosition
	 *            开始位置
	 * @param endPosition
	 *            结束位置
	 * @param randomAccessFile
	 * @param downloadLen
	 *            已经下载的长度（断点续传时不为0）
	 * @param needDownloadLen
	 *            每个线程需要下载的长度
	 */
	MyDownloadThread(String name, File file, String url, int startPosition,
			int endPosition, int downloadLen, int needDownloadLen, DBHelper db) {
		this.httpClient = new DefaultHttpClient();
		this.url = url;
		this.startPosition = startPosition;
		this.endPosition = endPosition;
		this.downloadLen = downloadLen;
		this.needDownloadLen = needDownloadLen;
		this.name = name;
		this.file = file;
		this.db = db;
	}

	/**
	 * 设置是否结束线程
	 * @param isStop
	 */
	public void setIsStop(boolean isStop) {
		this.isStop = isStop;
	}

	/**
	 * 当前线程下载进度
	 */
	public int getThreadDownloadProgress(){
		return downloadLen;
	}
	
	@Override
	public void run() {
		HttpGet httpGet = new HttpGet(url);
		// 设置下载范围
		httpGet.addHeader("Range", "bytes=" + startPosition + "-" + endPosition);
		try {
			httpResponse = httpClient.execute(httpGet);
			HttpEntity entity = httpResponse.getEntity();
			bis = new BufferedInputStream(entity.getContent());

			randomAccessFile = new RandomAccessFile(file, "rw");
			randomAccessFile.seek(startPosition);
			byte[] b = new byte[1024];
			int len = 0;
			int redundance = 0;
			while ((len = bis.read(b)) != -1 && downloadLen < needDownloadLen) {
				downloadLen += len;
				if (downloadLen > needDownloadLen) {
					redundance = downloadLen - needDownloadLen;
					downloadLen = needDownloadLen;
				}
				randomAccessFile.write(b, 0, len - redundance);

				// 更新下载长度
				db.updateResumeBrokenDownload(downloadLen + "", name);

				// 结束线程
				if (isStop) {
					break;
				}
			}
			randomAccessFile.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
