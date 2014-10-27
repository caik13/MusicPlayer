package com.caik13.musicplayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

/**
 * 本地音乐列表fragment
 * 
 * @author Administrator
 * 
 */
public class Fragment_LocalMusicList extends Fragment {

	private ListView songList;
	private List<Map<String, String>> data;
	private Activity_Main mcontext;

	public Fragment_LocalMusicList(Activity_Main context){
		data = new ArrayList<Map<String,String>>();
		this.data = context.songNames;
		this.mcontext = context;
	}
	
	/**
	 * 为Fragment加载布局时调用
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_local_music_list, null);
		songList = (ListView) view.findViewById(R.id.local_music_list);
		SimpleAdapter adapter = new SimpleAdapter(getActivity(),
				data, R.layout.fragment_local_music_list_item, new String[] {"songname"},
				new int[] {R.id.local_music_list_name});
		
		songList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				Intent intent = new Intent("com.caik13.PLAY_MANAGER");
				intent.putExtra("type", "play");
				intent.putExtra("position", position + "");
				getActivity().sendBroadcast(intent);
			}
		});
		
		Animation animation = new AlphaAnimation(0, 1);
		songList.setAnimation(animation);
		songList.setAdapter(adapter);

		return view;
	}
}
