package com.caik13.musicplayer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * 更多界面fragment
 * @author Administrator
 *
 */
public class Fragment_More extends Fragment {

	private Activity_Main mcontext;

	public Fragment_More(Activity_Main context) {
		this.mcontext = context;
	}

	/**
	 * 为Fragment加载布局时调用
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_main_more, null);

		Button backMain = (Button) view.findViewById(R.id.back_main);
		Button logout = (Button) view.findViewById(R.id.more_exit);
		//返回主页面
		backMain.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mcontext.viewPager.setCurrentItem(1);
			}
		});
		//退出
		logout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				mcontext.finish();
			}
		});
		
		return view;
	}
}
