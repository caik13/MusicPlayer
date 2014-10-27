package com.caik13.musicplayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.caik13.musicplayer.util.ParseXML;
import com.caik13.musicplayer.util.net.HttpMethod;
import com.caik13.musicplayer.util.net.NetConnection;
import com.caik13.musicplayer.util.net.NetConnection.CallBackExecuteMethod;

/**
 * 歌曲列表fragment
 * @author Administrator
 *
 */
public class Fragment_SingerOfSongs extends Fragment implements OnItemClickListener{

	private Activity_Main mcontext;
	private ListView songsList;
	private List<Map<String,String>> songs;
	private String singerId = "";
	
	public Fragment_SingerOfSongs(Activity_Main context) {
		this.mcontext = context;
	}
	
	public Fragment_SingerOfSongs(Activity_Main context, String singerId) {
		this.mcontext = context;
		this.singerId = singerId;
	}

	/**
	 * 为Fragment加载布局时调用
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_singer_songs, null);

		init(view);

		songs = new ArrayList<Map<String,String>>();
		
		if (songs.size() <= 0) {
			getSongs();
		}
		
		songsList.setOnItemClickListener(this);
		return view;
	}
	
	private void init(View view){
		songsList = (ListView) view.findViewById(R.id.songs_list);
	}
	
	/**
	 * 
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		String songName = songs.get(position).get("singer_songs_name");
		final String[] songAndSinger = songName.split("-");
		
		String url = Config.BAIDU_MUSIC + songAndSinger[1].trim() + "$$" + songAndSinger[0].trim() + "$$$$";

		System.out.println(url);
		
		new NetConnection(url, HttpMethod.GET, new CallBackExecuteMethod() {
			@Override
			public void onCallBackExecuteMethod(String result) {
				try {
					if(result != null && result != ""){
						
						String musicUrl = ParseXML.parse(result);
						
						Intent intent = new Intent("com.caik13.PLAY_MANAGER");
						intent.putExtra("type", "play");
						intent.putExtra("position", "-1");
						intent.putExtra("url", musicUrl);
						
						intent.putExtra("netSongsName", songAndSinger[1].trim());
						intent.putExtra("netSingerName", songAndSinger[0].trim());
//						intent.putExtra("netSongsDuration", netSongsDuration);
						
						getActivity().sendBroadcast(intent);
					}
				} catch (XmlPullParserException | IOException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onCallBackExecuteMethod(Bitmap bitmap) {
				// TODO Auto-generated method stub
			}
		});
	}

	/**
	 * 获取所选歌手的歌曲列表
	 */
	private void getSongs() {
		new NetConnection(Config.SINGER_SONGS_LIST + singerId, HttpMethod.GET,
				new CallBackExecuteMethod() {

					@Override
					public void onCallBackExecuteMethod(String result) {
						result = result.replace("<!--KG_TAG_RES_START-->", "");
						try {
							JSONObject json = new JSONObject(result);
							JSONObject json_data = new JSONObject(json.getString("data"));
							JSONArray json_info = json_data.getJSONArray("info");
							for (int i = 0; i < json_info.length(); i++) {
								HashMap<String, String> map = new HashMap<String, String>();
								map.put("singer_songs_name",((JSONObject) json_info.get(i))
												.getString("filename"));
								songs.add(map);
							}
//							SimpleAdapter adapter = new SimpleAdapter(
//									mcontext, songs,
//									R.layout.fragment_singer_songs_item,
//									new String[] { "singer_songs_name" },
//									new int[] { R.id.singer_songs_name });

							//这里改用自定义的adapter 为了实现按钮事件
							Adapter_Fragment_SingerOfSongs adapter = new Adapter_Fragment_SingerOfSongs(
									mcontext, songs,
									R.layout.fragment_singer_songs_item,
									new String[] { "singer_songs_img",
											"singer_songs_name" }, 
									new int[] {	R.id.singer_songs_add,
											R.id.singer_songs_name });
							songsList.setAdapter(adapter);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onCallBackExecuteMethod(Bitmap bitmap) {
						// TODO Auto-generated method stub
					}
				});
	}
}
