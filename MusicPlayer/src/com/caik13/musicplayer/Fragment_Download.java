package com.caik13.musicplayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.caik13.musicplayer.db.DBHelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.RelativeLayout;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

/**
 * 下载管理界面fragment
 * 
 * @author Administrator
 * 
 */
public class Fragment_Download extends Fragment {

	private Activity_Main mcontext;
	private DBHelper db;

	private ExpandableListView downloadList;
	
	private SimpleExpandableListAdapter exAdapter = null;

	private List<Map<String, String>> downloadParentList;
	private List<List<Map<String, String>>> downloadChildListData;

	public Fragment_Download(Activity_Main context) {
		this.mcontext = context;
	}

	//更新下载界面
	class UpdateUI extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			if(null != intent.getStringExtra("update") && "true".equals(intent.getStringExtra("update"))){
				//从数据库中获取下载列表
				downloadChildListData = getDownloadList();
				exAdapter.notifyDataSetChanged();
			}
		}
	}
	
	/**
	 * 为Fragment加载布局时调用
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_download, null);
		downloadList = (ExpandableListView) view.findViewById(R.id.download_list);
		
		init();
		
		return view;
	}
	
	private void init(){
		db = DBHelper.newInstance(mcontext);
	
		downloadParentList = new ArrayList<Map<String, String>>();
		downloadChildListData = new ArrayList<List<Map<String, String>>>();
		
		//从数据库中获取下载列表
		downloadChildListData = getDownloadList();
		
		Map<String,String> map = new HashMap<String,String>();
		map.put("downloadGroupName", "正在下载");
		downloadParentList.add(map);
		Map<String,String> map2 = new HashMap<String,String>();
		map2.put("downloadGroupName", "下载历史");
		downloadParentList.add(map2);
		
		if (downloadChildListData.size() > 0) {
			//TODO 下载列表
			
			exAdapter = new SimpleExpandableListAdapter(
					mcontext, downloadParentList,
					R.layout.fragment_download_parent_list_item,
					new String[] { "downloadGroupName" },
					new int[] { R.id.download_group_name },
					downloadChildListData,
					R.layout.fragment_download_child_list_item, new String[] {
							"filename", "filesize" },
					new int[] { R.id.download_song_name,
							R.id.download_progress_text});
		} else {
			List<Map<String, String>> child = new ArrayList<Map<String,String>>();
			Map<String,String> childMap = new HashMap<String,String>();
			childMap.put("defaulttext", "暂无歌曲正在下载");
			child.add(childMap);
			downloadChildListData.add(child);
			
			exAdapter = new SimpleExpandableListAdapter(
					mcontext, downloadParentList,
					R.layout.fragment_download_parent_list_item,
					new String[] { "downloadGroupName" },
					new int[] { R.id.download_group_name },
					downloadChildListData,
					R.layout.fragment_download_child_list_item_default, new String[] {
							"defaulttext" },
					new int[] { R.id.default_text});
		}
		
		downloadList.setAdapter(exAdapter);
		downloadList.expandGroup(0);
		
		downloadList.setOnChildClickListener(new OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				
				Intent intent = new Intent("com.caik13.DOWNLOAD_MANAGE");
				intent.putExtra("pauseDownload", "true");
				mcontext.sendBroadcast(intent);
				
				return false;
			}
		});
		
		mcontext.registerReceiver(new UpdateUI(), new IntentFilter("com.caik13.UPDATE_DOWNLOAD_UI"));
	}
	
	/**
	 * 从数据库中获取下载列表
	 */
	private List<List<Map<String,String>>> getDownloadList(){
		List<List<Map<String,String>>> data = new ArrayList<List<Map<String, String>>>();
		data.add(db.getDownloadList());
		return data;
	}
}
