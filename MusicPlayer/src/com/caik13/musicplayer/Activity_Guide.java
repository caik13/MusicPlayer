package com.caik13.musicplayer;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;

public class Activity_Guide extends Activity {

	private ViewPager guide;
	List<View> views ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_guide);

		LayoutInflater flater = LayoutInflater.from(Activity_Guide.this);
		views = new ArrayList<View>();
		guide = (ViewPager) findViewById(R.id.guide_viewPager);
		View view1 = flater.inflate(R.layout.guide_01, null);
		View view2 = flater.inflate(R.layout.guide_02, null);
		View view3 = flater.inflate(R.layout.guide_03, null);
		View view4 = flater.inflate(R.layout.guide_04, null);
		View view5 = flater.inflate(R.layout.guide_end, null);
		ImageButton startMain = (ImageButton) view4.findViewById(R.id.guide_btn_start);
		startMain.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Activity_Guide.this, Activity_Main.class);
				startActivity(intent);
				finish();
			}
		});
		
		views.add(view1);
		views.add(view2);
		views.add(view3);
		views.add(view4);
		views.add(view5);
		
		guide.setAdapter(new MyViewPagerAdapater());
		guide.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				if(arg0 == 4){
					Intent intent = new Intent(Activity_Guide.this, Activity_Main.class);
					startActivity(intent);
					finish();
				}
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	class MyViewPagerAdapater extends PagerAdapter{

		@Override
		public int getCount() {
			return views.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public Object instantiateItem(View container, int position) {
			((ViewPager)container).addView(views.get(position));
			return views.get(position);
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			((ViewPager)container).removeView(views.get(position));
		}
	}
}
