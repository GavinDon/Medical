package com.sltj.medical;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sltj.medical.adapter.PrivatorDoctorAdapter;
import com.sltj.medical.util.LogUtils;
import com.sltj.medical.util.ToastUtils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Contacts.SettingsColumns;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

/**
 * 私人医生Fragment
 * 
 * @author linan
 *
 */

public class PrivateDoctorFragment extends Fragment implements OnCheckedChangeListener {
	private List<Map<String, String>> lst;
	private PrivatorDoctorAdapter mAdapter;
	private Context mContext;
	private RadioGroup mRadioGroup;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this.getActivity();
		lst = new ArrayList<Map<String, String>>();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_privatedoctor, null);
		initView(v);
		return v;
	}

	private void initView(View v) {
		mRadioGroup = (RadioGroup) v.findViewById(R.id.rg_privaterdoctor);
		mRadioGroup.setOnCheckedChangeListener(this);

	}


	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		Intent mIntent = new Intent();
		switch (checkedId) {
		
		case R.id.rb_member:
			// 会员
			mIntent.setClass(this.mContext, MemberPrivateDoctorActivity.class);
			startActivity(mIntent);
			mRadioGroup.clearCheck();
			
			break;
		case R.id.rb_doctor:
			// 医生
			mIntent.setClass(this.mContext, ChatActivity.class);
			startActivity(mIntent);
			mRadioGroup.clearCheck();
			
			break;
	
		}

	}

}
