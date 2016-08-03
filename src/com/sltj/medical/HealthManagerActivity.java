package com.sltj.medical;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sltj.medical.adapter.MineZoneAdapter;
import com.sltj.medical.base.BaseActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 健康管理页面 Author linan Date：2016年8月2日 下午4:43:51
 */
public class HealthManagerActivity extends BaseActivity implements OnItemClickListener {
	private ListView mListView;
	private List<Map<String, Object>> lst = new ArrayList<Map<String, Object>>();
	private MineZoneAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.healthmanager_activity);
		initialize();
	}

	@Override
	public void initView() {
		mListView = (ListView) findViewById(R.id.lv_heath_manager);
		initBar();
	}

	@Override
	public void setupData() {
		initData();
		mAdapter = new MineZoneAdapter(this, lst);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
	}

	private void initData() {
		String title[] = new String[] { "体检记录", "治疗记录", "用药记录", "运动记录", "心情记录" };
		int[] img = new int[] { R.id.useLogo, R.id.useLogo, R.id.useLogo, R.id.useLogo, R.id.useLogo };
		for (int i = 0; i < title.length; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("img", img[i]);
			map.put("title", title[i]);
			lst.add(map);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent mIntent = new Intent();
		switch (position) {
		case 0:
			mIntent.setClass(this, RecodActivity.class);
			startActivity(mIntent);
			break;
		case 1:
			mIntent.setClass(this, TreatRecordActivity.class);
			startActivity(mIntent);
			break;
		case 2:
			break;
		case 3:
			break;
		case 4:

			break;

		}

	}
	
	

	// --
	//
	// --

	/*
	 * 初始化标题栏
	 */
	private void initBar() {
		LinearLayout llback = (LinearLayout) findViewById(R.id.ll_back);
		TextView iv = (TextView) findViewById(R.id.tv_title);
		iv.setText("健康管理");
		llback.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				HealthManagerActivity.this.finish();
			}
		});

	}

}
