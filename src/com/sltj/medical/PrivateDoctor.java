package com.sltj.medical;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sltj.medical.adapter.PrivatorDoctorAdapter;
import com.sltj.medical.util.ToastUtils;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 私人医生Fragment
 * 
 * @author linan
 *
 */

public class PrivateDoctor extends Fragment implements OnItemClickListener{
	private ImageView ivBack;
	private TextView tvTitle;
	private List<Map<String, String>> lst;
	private ListView mListView;
	private PrivatorDoctorAdapter mAdapter;
	private Context mContext;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext=this.getActivity();
		lst = new ArrayList<Map<String, String>>();
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.activity_privatedoctor, null);
		initView(v);
		return null;
	}

	private void initView(View v) {
		ivBack = (ImageView) v.findViewById(R.id.iv_back);
		ivBack.setVisibility(View.INVISIBLE);
		tvTitle = (TextView) v.findViewById(R.id.tv_title);
		tvTitle.setText("健康管理");
		mListView=(ListView) v.findViewById(R.id.lv_privator);
		initData();
		mListView.setOnItemClickListener(this);
	}

	private void initData() {
		String[] item = new String[] { "体检记录", "治疗记录", "用药记录", "运动记录", "心情记录" };
		for (int i = 0; i < item.length; i++) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("text", item[i]);
			lst.add(map);
		}
		mAdapter=new PrivatorDoctorAdapter(mContext, lst);
		mListView.setAdapter(mAdapter);
		

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		  ToastUtils.show(mContext, position+"", 0);
	}

}
