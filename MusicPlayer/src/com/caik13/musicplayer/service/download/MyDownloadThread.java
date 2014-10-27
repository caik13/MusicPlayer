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
 * �������߳�(����ִ�����ز�����)
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
	// �Ѿ����صĳ���
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
	 *            ���ص�ַ
	 * @param startPosition
	 *            ��ʼλ��
	 * @param endPosition
	 *            ����λ��
	 * @param randomAccessFile
	 * @param downloadLen
	 *            �Ѿ����صĳ��ȣ��ϵ�����ʱ��Ϊ0��
	 * @param needDownloadLen
	 *            ÿ���߳���Ҫ���صĳ���
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
	 * �����Ƿ�����߳�
	 * @param isStop
	 */
	public void setIsStop(boolean isStop) {
		this.isStop = isStop;
	}

	/**
	 * ��ǰ�߳����ؽ���
	 */
	public int getThreadDownloadProgress(){
		return downloadLen;
	}
	
	@Override
	public void run() {
		HttpGet httpGet = new HttpGet(url);
		// �������ط�Χ
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

				// �������س���
				db.updateResumeBrokenDownload(downloadLen + "", name);

				// �����߳�
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
