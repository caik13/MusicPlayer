package com.caik13.musicplayer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 主界面fragment
 * @author Administrator
 *
 */
public class Fragment_Main extends Fragment{

	public AutoCompleteTextView mainSearch;
	private Activity_Main mcontext;
	
	private Fragment_LocalMusicList localMusic = null;
	private Fragment_Download downloadFragment = null;
	private Fragment_MainSinger singerFragment = null;
	
	private RelativeLayout nativeUp;
	private Button nativeDownloadBtn;
	private Button singerBtn;
	public TextView nativeMusicCount;
	
	public Fragment_Main(Activity_Main context) {
		this.mcontext = context;
	}

	/**
	 * 为Fragment加载布局时调用
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_main_main, null);
		nativeUp = (RelativeLayout) view.findViewById(R.id.native_up);
		nativeDownloadBtn = (Button) view.findViewById(R.id.btn_native_download);
		singerBtn = (Button) view.findViewById(R.id.ic_navigation_singer_btn);
		nativeMusicCount = (TextView) view.findViewById(R.id.native_music_count);
		
		nativeMusicCount.setText(Config.MUSIC_COUNT + "");
		
		//歌手
		singerBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mcontext.fragmentAdapter.getCount() > 2) {
					if(!(mcontext.fragmentLists.get(2) instanceof Fragment_MainSinger)){
						mcontext.fragmentLists.remove(2);
						
						mcontext.fragmentLists.add(new Fragment_MainSinger(mcontext));
						mcontext.fragmentAdapter.notifyDataSetChanged();
						mcontext.viewPager.setCurrentItem(2);
					}else{
						mcontext.viewPager.setCurrentItem(2);
					}
				} else {
					mcontext.fragmentLists.add(new Fragment_MainSinger(mcontext));
					mcontext.fragmentAdapter.notifyDataSetChanged();
					mcontext.viewPager.setCurrentItem(2);
				}
			}
		});
		
		//下载管理
		nativeDownloadBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mcontext.fragmentAdapter.getCount() > 2) {
					if(!(mcontext.fragmentLists.get(2) instanceof Fragment_Download)){
						mcontext.fragmentLists.remove(2);
						
						mcontext.fragmentLists.add(new Fragment_Download(mcontext));
						mcontext.fragmentAdapter.notifyDataSetChanged();
						mcontext.viewPager.setCurrentItem(2);
					}else{
						mcontext.viewPager.setCurrentItem(2);
					}
				} else {
					mcontext.fragmentLists.add(new Fragment_Download(mcontext));
					mcontext.fragmentAdapter.notifyDataSetChanged();
					mcontext.viewPager.setCurrentItem(2);
				}
			}
		});
		//本地音乐
		nativeUp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mcontext.fragmentAdapter.getCount() > 2) {
					if(!(mcontext.fragmentLists.get(2) instanceof Fragment_LocalMusicList)){
						mcontext.fragmentLists.remove(2);

						mcontext.fragmentLists.add(new Fragment_LocalMusicList(mcontext));
						mcontext.fragmentAdapter.notifyDataSetChanged();
						mcontext.viewPager.setCurrentItem(2);
					}else{
						mcontext.viewPager.setCurrentItem(2);
					}
				} else {
					mcontext.fragmentLists.add(new Fragment_LocalMusicList(mcontext));
					mcontext.fragmentAdapter.notifyDataSetChanged();
					mcontext.viewPager.setCurrentItem(2);
				}
			}
		});
		
		mainSearch = (AutoCompleteTextView) view.findViewById(R.id.autoCompleteTextView1);
		
		mainSearch.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
//					mcontext.playingBar.setVisibility(View.GONE);
					System.out.println("获取焦点");
				}else{
//					mcontext.playingBar.setVisibility(View.VISIBLE);
					System.out.println("失去取焦点");
				}
			}
		});
		
		
		
		return view;
	}

}
