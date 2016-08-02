package com.sltj.medical.adapter;

import java.util.List;

import com.sltj.medical.LoginActivity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class BannerAdapter extends PagerAdapter {
	private List<ImageView>imageViews;
	private Context mContext;
	public BannerAdapter(Context context,List<ImageView>imageViews) {
		this.mContext=context;
		this.imageViews=imageViews;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return Integer.MAX_VALUE;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0==arg1;
	}

	@Override
	public void destroyItem(View arg0, int arg1, Object arg2) {
//		((ViewPager) arg0).removeView((View) arg2);
	}


	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		ImageView iv = imageViews.get(position%imageViews.size());
		//当快速滑动时会出现崩溃，加以判断防止
		if(iv.getParent()!=null){
			((ViewPager)iv.getParent()).removeView(iv);
		}
		((ViewPager) container).addView(iv);
		// 在这个方法里面设置图片的点击事件
		iv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				Intent mIntent=new Intent();
//				mIntent.setClass(mContext, LoginActivity.class);
//				mContext.startActivity(mIntent);
			}
		});
		return iv;
	}
	
	

}
