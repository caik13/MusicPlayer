package com.caik13.musicplayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.caik13.musicplayer.util.net.HttpMethod;
import com.caik13.musicplayer.util.net.NetConnection;
import com.caik13.musicplayer.util.net.NetConnection.CallBackExecuteMethod;

public class Adapter_Fragment_MainSinger extends BaseAdapter{

	private ViewHolder holder;

	private List<Map<String, String>> data;
	private LayoutInflater inflater;
	private Context context;
	private int resource;
	private String[] from;
	private int[] to;
	
	/**
	 * 
	 * @param context
	 * @param data 
	 * @param resource listviewitem 的布局文件
	 * @param from map中数据的key，与"@param to" 中对应
	 * @param to listviewitem中的控件的ID
	 */
	public Adapter_Fragment_MainSinger(Context context,
			List<Map<String, String>> data, int resource, String[] from,
			int[] to) {
		this.data = data;
		this.context = context;
		inflater = (LayoutInflater) this.context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.resource = resource;
		this.from = from;
		this.to = to;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		HashMap<String, String> hashMap = (HashMap<String, String>) data
				.get(position);
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(resource, null);
			
			if (to.length > 0) {
				for (int i = 0; i < to.length; i++) {
					View view = convertView.findViewById(to[i]);
					if (view instanceof ImageView) {
						holder.imageView = (ImageView) view;
						setViewImage(holder.imageView, hashMap.get(from[i])
								.toString());
					} else if (view instanceof TextView) {
						holder.singerName = (TextView) view;
						setViewText(holder.singerName, hashMap.get(from[i])
								.toString());
					}
				}
			}
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		return convertView;
	}

	private void setViewImage(final ImageView v, String imgUrl) {
		new NetConnection(imgUrl.replace("{size}/", ""), HttpMethod.GET_IMAGE,
				new CallBackExecuteMethod() {

					@Override
					public void onCallBackExecuteMethod(Bitmap bitmap) {
						v.setImageBitmap(bitmap);
					}

					@Override
					public void onCallBackExecuteMethod(String result) {
						// TODO Auto-generated method stub
					}
				});
	}

	private void setViewText(TextView v, String text) {
		v.setText(text);
	}

	private static class ViewHolder {
		ImageView imageView;
		TextView singerName;
		TextView singerInfo;
	}

}
