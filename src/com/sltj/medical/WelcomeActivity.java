package com.sltj.medical;

import java.util.ArrayList;
import java.util.List;

import com.sltj.medical.base.BaseActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class WelcomeActivity extends BaseActivity {

	
	private List<View> views;
	private View iv1, iv2, iv3;
	private View[] imageView;
	private ViewPager viewPager;
	private boolean isEntry = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		initialize();
		SharedPreferences spp = getSharedPreferences("config", MODE_PRIVATE);
		isEntry = spp.getBoolean("isEntry", false);
		Log.i("isEntry", String.valueOf(isEntry));
		if (isEntry) {
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
			this.finish();

		} else {
			viewPager.setAdapter(new Myadapter());// 给viewPager设置适配器
			viewPager.setOnPageChangeListener(new myOnPageChangeListene());

		}
	}
	
	/**
	 * 使用PagerAdapter必须重写以下4个方法
	 */
	class Myadapter extends PagerAdapter {

		@Override
		public int getCount() {

			return views.size();
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(views.get(position));
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			((ViewPager) container).addView(views.get(position));

			return views.get(position);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {

			return arg0 == arg1;
		}

	}

	class myOnPageChangeListene implements OnPageChangeListener {
		private int oldPosition=0;

		@Override
		public void onPageSelected(int position) {
		dots.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);
		dots.get(position).setBackgroundResource(R.drawable.dot_focused);
		oldPosition=position;
		

		}

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}
	}

	/**
	 *  点击体验时进入首页；
	 * @param v
	 */
	public void startExperience(View v) {
		SharedPreferences sp = getSharedPreferences("config",
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putBoolean("isEntry", true);
		editor.commit();
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
		this.finish();
	}
	List<View>dots;
	@Override
	public void initView() {
		iv1 = (View) findViewById(R.id.iv1);
		iv2 = (View) findViewById(R.id.iv2);
		iv3 = (View) findViewById(R.id.iv3);
		dots=new ArrayList<View>();
		dots.add(iv1);
		dots.add(iv2);
		dots.add(iv3);
		viewPager = (ViewPager) findViewById(R.id.vp_welcome);
		imageView = new View[] { iv1, iv2, iv3 };
		views = new ArrayList<View>();
		LayoutInflater flater = LayoutInflater.from(this);
		for (int i = 1; i <= imageView.length; i++) {
			String name = "welcome" + i;
			View view = flater.inflate(
					getResources().getIdentifier(name, "layout",
							getPackageName()), null);

			views.add(view);

		}

	}



	@Override
	public void setupData() {
		
	}
}
