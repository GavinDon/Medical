package com.sltj.medical;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sltj.medical.adapter.MineZoneAdapter;
import com.sltj.medical.wedgit.WrapHeightListView;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MineFragment extends Fragment implements OnItemClickListener {
	private WrapHeightListView mListView;
	private List<Map<String, Object>> lst = new ArrayList<Map<String, Object>>();
	private MineZoneAdapter mAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_mine, null);
		// 初始化控件
		initBar(view);
		initView(view);
		return view;
	}

	private void initView(View view) {
		mListView = (WrapHeightListView) view.findViewById(R.id.lv_mine_);
		initData();
		mAdapter = new MineZoneAdapter(this.getActivity(), lst);
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

	private void initBar(View v) {
		LinearLayout llback = (LinearLayout) v.findViewById(R.id.ll_back);
		TextView iv = (TextView) v.findViewById(R.id.tv_title);
		iv.setText("个人中心");
		llback.setVisibility(View.INVISIBLE);

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		switch (position) {
		case 0:
			Intent miIntent=new Intent(MineFragment.this.getActivity(),HealthManagerActivity.class);
			this.getActivity().startActivity(miIntent);
			break;
		case 1:

			break;
		case 2:

			break;
		case 3:

			break;
		case 4:

			break;

		default:
			break;
		}

	}

}
