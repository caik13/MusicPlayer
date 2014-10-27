package com.caik13.musicplayer;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.ViewGroup;

/**
 * ViewPager的Adapter
 * 
 * @author Administrator
 * 
 */
public class Adapter_MyFragmentPager extends FragmentStatePagerAdapter {
	public List<Fragment> fragmentLists;

	public Adapter_MyFragmentPager(FragmentManager fm, List<Fragment> fragmentLists2) {
		super(fm);
		this.fragmentLists = fragmentLists2;
	}

	/**
	 * 获取其中一个fragment
	 */
	@Override
	public Fragment getItem(int position) {
		return fragmentLists.get(position);
	}

	@Override
	public int getItemPosition(Object object) {
		// TODO Auto-generated method stub
		return PagerAdapter.POSITION_NONE;
	}

	/**
	 * 获取总共页卡的数量
	 */
	@Override
	public int getCount() {
		return fragmentLists.size();
	}

}
