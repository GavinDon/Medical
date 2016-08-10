package com.sltj.medical;

import java.util.List;
import java.util.Map;

import com.sltj.medical.adapter.PrivatorDoctorAdapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 产品Fragment
 * @author linan
 *
 */
public class ProductFragment extends Fragment {
	private ImageView ivBack;
	private TextView tvTitle;
	private List<Map<String, String>> lst;
	private Context mContext;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_product, null);
		return v;
	}


}
