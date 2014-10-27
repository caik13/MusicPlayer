package com.caik13.musicplayer;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.caik13.musicplayer.util.ParseXML;
import com.caik13.musicplayer.util.net.HttpMethod;
import com.caik13.musicplayer.util.net.NetConnection;
import com.caik13.musicplayer.util.net.NetConnection.CallBackExecuteMethod;

public class Adapter_Fragment_SingerOfSongs extends BaseAdapter implements
		OnClickListener {

	private ViewHolder holder;

	private List<Map<String, String>> data;
	private LayoutInflater inflater;
	private Context context;
	private int resource;
	private String[] from;
	private int[] to;
	
	//用来存放更多界面是否隐藏
	private boolean[] isGone;
	
	private boolean moreLayoutIsGone = true;

	/**
	 * 
	 * @param context
	 * @param data 
	 * @param resource listviewitem 的布局文件
	 * @param from map中数据的key，与"@param to" 中对应
	 * @param to listviewitem中的控件的ID
	 */
	public Adapter_Fragment_SingerOfSongs(Context context,
			List<Map<String, String>> data, int resource, String[] from,
			int[] to) {
		this.data = data;
		this.context = context;
		inflater = (LayoutInflater) this.context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.resource = resource;
		this.from = from;
		this.to = to;
		isGone = new boolean[data.size()];
		Arrays.fill(isGone, true);
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
			holder.linearLayout = (LinearLayout) convertView.findViewById(R.id.singer_songs_more_layout);
			
			holder.layout_DownloadBtn = (Button) convertView.findViewById(R.id.singer_songs_more_layout_download);
			holder.layout_AddBtn = (Button) convertView.findViewById(R.id.singer_songs_more_layout_add);
			holder.layout_ShareBtn = (Button) convertView.findViewById(R.id.singer_songs_more_layout_share);
			holder.layout_SongsInfoBtn = (Button) convertView.findViewById(R.id.singer_songs_more_layout_info);
			holder.addBtn = (ImageButton) convertView.findViewById(R.id.singer_songs_add);
			holder.moreBtn = (Button) convertView.findViewById(R.id.singer_songs_more);

			holder.moreBtn.setOnClickListener(Adapter_Fragment_SingerOfSongs.this);
			holder.layout_DownloadBtn.setOnClickListener(Adapter_Fragment_SingerOfSongs.this);
			holder.layout_AddBtn.setOnClickListener(Adapter_Fragment_SingerOfSongs.this);
			holder.layout_ShareBtn.setOnClickListener(Adapter_Fragment_SingerOfSongs.this);
			holder.layout_SongsInfoBtn.setOnClickListener(Adapter_Fragment_SingerOfSongs.this);
			holder.addBtn.setOnClickListener(Adapter_Fragment_SingerOfSongs.this);
			
			holder.moreBtn.setTag(position);
			holder.layout_DownloadBtn.setTag(position);
			holder.layout_AddBtn.setTag(position);
			holder.layout_ShareBtn.setTag(position);
			holder.layout_SongsInfoBtn.setTag(position);
			holder.addBtn.setTag(position);


			if (to.length > 0) {
				for (int i = 0; i < to.length; i++) {
					View view = convertView.findViewById(to[i]);
					if (view instanceof ImageButton) {
//						holder.imageBtn = (ImageButton) view;
//						setViewImage(holder.imageBtn, hashMap.get(from[i])
//								.toString());
					} else if (view instanceof TextView) {
						holder.textView = (TextView) view;
						setViewText(holder.textView, hashMap.get(from[i])
								.toString());
					}
				}
			}
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		if(isGone[position]){
			holder.linearLayout.setVisibility(View.GONE);
		}else{
			holder.linearLayout.setVisibility(View.VISIBLE);
		}
		
		return convertView;
	}

	private void setViewImage(final ImageButton v, String imgUrl) {
		new NetConnection(imgUrl, HttpMethod.GET_IMAGE,
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
		Button moreBtn;
		Button layout_DownloadBtn;
		Button layout_AddBtn;
		Button layout_ShareBtn;
		Button layout_SongsInfoBtn;
		ImageButton addBtn;
		ImageButton imageBtn;
		TextView textView;
		LinearLayout linearLayout;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.singer_songs_add:
			
			break;
		case R.id.singer_songs_more:
			setVisibility(Integer.parseInt(v.getTag().toString()));
			break;
		case R.id.singer_songs_more_layout_download:
			startDownload(Integer.parseInt(v.getTag().toString()));
			break;
		case R.id.singer_songs_more_layout_add:
			
			break;
		case R.id.singer_songs_more_layout_share:
			
			break;
		case R.id.singer_songs_more_layout_info:
			
			break;
//		case R.id.singer_songs_more:
//			
//			break;
		}
	}
	
	/**
	 * 设置更多菜单的隐藏于显示
	 * @param tag
	 */
	private void setVisibility(int tag){
		if(isGone[tag]){
			isGone[tag] = false;
			for (int i = 0; i < isGone.length; i++) {
				if(i != tag){
					isGone[i] = true;
				}
			}
		}else{
			isGone[tag] = true;
		}
		notifyDataSetChanged();
	}
	
	/**
	 * 下载音乐
	 */
	private void startDownload(int tag){
		
		final String songName = data.get(tag).get("singer_songs_name");
		final String[] songAndSinger = songName.split("-");
		
		String url = Config.BAIDU_MUSIC + songAndSinger[1].trim() + "$$" + songAndSinger[0].trim() + "$$$$";
		new NetConnection(url, HttpMethod.GET, new CallBackExecuteMethod() {
			
			@Override
			public void onCallBackExecuteMethod(Bitmap bitmap) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onCallBackExecuteMethod(String result) {
				try {
					String musicUrl = ParseXML.parse(result);
					
					System.out.println("下载地址:" + musicUrl);
					if("".equals(musicUrl)){
						Toast.makeText(context, "这首歌曲无法下载", Toast.LENGTH_SHORT).show();
					}else{
						Intent intent = new Intent("com.caik13.DOWNLOAD_MANAGE");
						intent.putExtra("startDown", "true");
						intent.putExtra("url", musicUrl);
						intent.putExtra("fileName", songName);
						context.sendBroadcast(intent);
					}
				} catch (XmlPullParserException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
}
