package com.caik13.musicplayer.util.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

/**
 * 负责连接HTTP服务噄1�7,发�1�7�POST/GET请求
 * 
 * @author Administrator
 * 
 */
public class NetConnection {

	/**
	 * 异步访问HTTP服务器
	 * 
	 * @param url
	 *            要访问的地址
	 * @param httpMethod
	 *            请求类型
	 * @param execetuMethod
	 *            请求结束后的回调方法
	 * @param kvs
	 *            请求参数
	 */
	public NetConnection(final String url, final HttpMethod httpMethod,
			final CallBackExecuteMethod callBackExecuteMethod,final String... kvs) {
		new AsyncTask<String, Integer, String>() {
			Bitmap bitmap;
			
			HttpClient httpClient = new DefaultHttpClient();

			// 第一个执行的方法
			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
			}

			// 第二个执行的方法
			@Override
			protected String doInBackground(String... params) {
				StringBuffer result = new StringBuffer();
				switch (httpMethod) {
				case GET:
					// 执行GET请求,get请求将参数写在URL里面
					HttpGet httpGet = new HttpGet(url);
					try {
						//执行请求
						HttpResponse httpResponse = httpClient.execute(httpGet);
						HttpEntity httpEntity = httpResponse.getEntity();

						//把结果处理成String
						if (httpEntity != null) {
							BufferedReader br = new BufferedReader(new InputStreamReader(httpEntity.getContent()));
							String line = null;
							while ((line = br.readLine()) != null) {
								result.append(line);
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
					break;
				case GET_IMAGE:
					//获取图片
					// 执行GET请求,get请求将参数写在URL里面
					HttpGet httpGetImage = new HttpGet(url);
					try {
						//执行请求
						HttpResponse httpResponse = httpClient.execute(httpGetImage);
						HttpEntity httpEntity = httpResponse.getEntity();

						//把结果处理成String
						if (httpEntity != null) {
							bitmap = BitmapFactory.decodeStream(httpEntity.getContent());
							
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
//					result.delete(0, result.length());
					result.append("image");
					break;
				case POST:
					// 执行POST请求
					try {
						HttpPost httpPost = new HttpPost(url);
						List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
						//将参数封装成NameValuePair对象，post请求霄1�7�1�7�将参数封装成NameValuePair
						for (int i = 0; i < kvs.length; i += 2) {
							nameValuePairs.add(new BasicNameValuePair(kvs[i], kvs[i + 1]));
						}
						//将List<NameValuePair>对象转换成entity，并设置编码格式
						httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf-8"));
						//执行请求，并返回结果
						HttpResponse httpResponse = httpClient.execute(httpPost);
						HttpEntity httpEntity = httpResponse.getEntity();
						//把结果处理成String
						if (httpEntity != null) {
							BufferedReader br = new BufferedReader(new InputStreamReader(httpEntity.getContent()));
							String line = null;
							while ((line = br.readLine()) != null) {
								result.append(line);
							}
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				}
				return result.toString();
			}

			//
			@Override
			protected void onProgressUpdate(Integer... values) {
				// TODO Auto-generated method stub
				super.onProgressUpdate(values);
			}

			// doInBackground返回时触发，doInBackground执行完后触发
			@Override
			protected void onPostExecute(String result) {
				if("image".equals(result)){
					callBackExecuteMethod.onCallBackExecuteMethod(bitmap);
				}else{
					//回调
					callBackExecuteMethod.onCallBackExecuteMethod(result);
				}
				super.onPostExecute(result);
			}
		}.execute();
	}

	//
	public static interface CallBackExecuteMethod {
		void onCallBackExecuteMethod(String result);
		void onCallBackExecuteMethod(Bitmap bitmap);
	}
}
