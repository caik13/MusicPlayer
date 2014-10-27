package com.caik13.musicplayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.caik13.musicplayer.entity.Singer;
import com.caik13.musicplayer.util.net.HttpMethod;
import com.caik13.musicplayer.util.net.NetConnection;
import com.caik13.musicplayer.util.net.NetConnection.CallBackExecuteMethod;

/**
 * 主页面“歌手”fragment
 * 
 * @author Administrator
 * 
 */
public class Fragment_MainSinger extends Fragment implements OnItemClickListener{

	private Activity_Main mcontext;

	private TextView huayu;
	private TextView oumei;
	private TextView rihan;
	
	private boolean huayuClick = false;
	private boolean oumeiClick = false;
	private boolean rihanClick = false;
	
	private ListView singerListView;
	private Adapter_Fragment_MainSinger adapter;

	// 华语
	private List<Singer> singersHuayu;
	private List<Map<String, String>> singerListDataHuayu;

	// 欧美
	private List<Singer> singersOumei;
	private List<Map<String, String>> singerListDataOumei;

	// 日韩
	private List<Singer> singersRihan;
	private List<Map<String, String>> singerListDataRihan;

	public Fragment_MainSinger(Activity_Main context) {
		this.mcontext = context;
	}

	/**
	 * 为Fragment加载布局时调用
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_main_singer,
				null);
		init(view);

		return view;
	}

	private void init(View view) {
		huayu = (TextView) view.findViewById(R.id.singer_tab_navigation_huayu);
		oumei = (TextView) view.findViewById(R.id.singer_tab_navigation_oumei);
		rihan = (TextView) view.findViewById(R.id.singer_tab_navigation_rihan);
		singerListView = (ListView) view.findViewById(R.id.singer_context_list);
		
		singerListDataHuayu = new ArrayList<Map<String, String>>();
		singersHuayu = new ArrayList<Singer>();

		singerListDataOumei = new ArrayList<Map<String, String>>();
		singersOumei = new ArrayList<Singer>();

		singerListDataRihan = new ArrayList<Map<String, String>>();
		singersRihan = new ArrayList<Singer>();

		// 列表滑动事件，不进行任何处理，以达到不能滑动的效果
		singerListView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return false;
			}
		});
		
		singerListView.setOnItemClickListener(this);

		huayu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(huayuClick){
					huayu.setTextColor(Color.WHITE);
					huayuClick = false;
				}else{
					huayu.setTextColor(Color.parseColor("#229ef4"));
					huayuClick = true;
				}
				
				if (singerListDataOumei.size() > 0) {
					initListAdapter(singerListDataHuayu);
				} else {
					getSingerData(Config.SINGER_LIST_HUAYU);
				}
			}
		});
		oumei.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(oumeiClick){
					oumei.setTextColor(Color.WHITE);
					oumeiClick = false;
				}else{
					oumei.setTextColor(Color.parseColor("#229ef4"));
					oumeiClick = true;
				}
				
				if (singerListDataOumei.size() > 0) {
					initListAdapter(singerListDataOumei);
				} else {
					getSingerData(Config.SINGER_LIST_OUMEI);
				}
			}
		});
		rihan.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(rihanClick){
					rihan.setTextColor(Color.WHITE);
					rihanClick = false;
				}else{
					rihan.setTextColor(Color.parseColor("#229ef4"));
					rihanClick = true;
				}
				
				if (singerListDataRihan.size() > 0) {
					initListAdapter(singerListDataRihan);
				} else {
					getSingerData(Config.SINGER_LIST_RIHAN);
				}
			}
		});

		if (singerListDataHuayu.size() > 0) {
			initListAdapter(singerListDataHuayu);
		} else {
			getSingerData(Config.SINGER_LIST_HUAYU);
			huayu.setTextColor(Color.parseColor("#229ef4"));
			huayuClick = true;
		}
	}

	private void initListAdapter(List<Map<String, String>> _singerListData) {
		if (_singerListData.size() > 0) {
//			simpleAdapter = new SimpleAdapter(mcontext, _singerListData,
//					R.layout.fragment_main_singer_list_item, new String[] {
//							"singerimg", "singerid", "singername", "singerintro" },
//					new int[] { R.id.singer_img, R.id.singer_id,R.id.singer_name,
//							R.id.singer_intro });
//			simpleAdapter = new SimpleAdapter(mcontext, _singerListData,
//					R.layout.fragment_main_singer_list_item, new String[] {
//							 "singerid", "singername", "singerintro" },
//					new int[] {  R.id.singer_id,R.id.singer_name,
//							R.id.singer_intro });
			
			adapter = new Adapter_Fragment_MainSinger(mcontext, _singerListData,
					R.layout.fragment_main_singer_list_item, new String[] {
							"singerimg", "singerid", "singername", "singerintro" },
					new int[] { R.id.singer_img, R.id.singer_id,R.id.singer_name,
							R.id.singer_intro });
			singerListView.setAdapter(adapter);
		} else {
			adapter.notifyDataSetChanged();
		}
	}

	/**
	 * 从酷狗服务器获取歌手数据
	 * 
	 * @param url
	 */
	private void getSingerData(final String url) {
		new NetConnection(url, HttpMethod.GET, new CallBackExecuteMethod() {
			@Override
			public void onCallBackExecuteMethod(String result) {
				result = result.replace("<!--KG_TAG_RES_START-->", "");
				try {
					JSONObject json = new JSONObject(result);
					JSONObject dataJson = new JSONObject(json.getString("data"));
					JSONArray singerArr = new JSONArray(dataJson
							.getString("info"));

					switch (url) {
					case Config.SINGER_LIST_HUAYU:
						fillData(singerArr, singersHuayu, singerListDataHuayu);
						initListAdapter(singerListDataHuayu);
						break;
					case Config.SINGER_LIST_OUMEI:
						fillData(singerArr, singersOumei, singerListDataOumei);
						initListAdapter(singerListDataOumei);
						break;
					case Config.SINGER_LIST_RIHAN:
						fillData(singerArr, singersRihan, singerListDataRihan);
						initListAdapter(singerListDataRihan);
						break;
					}
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

	/**
	 * 填充歌手数据
	 * 
	 * @param singerArr
	 * @param _singers
	 * @param _singerListData
	 */
	private void fillData(JSONArray singerArr, List<Singer> _singers,
			List<Map<String, String>> _singerListData) {
		try {
			for (int i = 0; i < singerArr.length(); i++) {
				Singer singer = new Singer(singerArr.getJSONObject(i));
				_singers.add(singer);
			}

			
			for (Singer singer : _singers) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("singerimg", singer.getImgUrl());
				map.put("singerid", singer.getSingerId());
				map.put("singername", singer.getSingerName());
				map.put("singerintro", singer.getIntro());
				_singerListData.add(map);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		View item = singerListView.getChildAt(position);
		TextView singerId = (TextView) item.findViewById(R.id.singer_id);
		String singerIdTex = singerId.getText().toString();
		
		mcontext.fragmentLists.add(new Fragment_SingerOfSongs(mcontext, singerIdTex));
		mcontext.fragmentAdapter.notifyDataSetChanged();
		
		mcontext.viewPager.setCurrentItem(mcontext.fragmentAdapter.getCount() - 1);
	}
}
