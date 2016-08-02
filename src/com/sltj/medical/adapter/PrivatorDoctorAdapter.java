package com.sltj.medical.adapter;

import java.util.List;
import java.util.Map;

import com.sltj.medical.R;
import com.sltj.medical.base.MyBaseAdapter;
import com.sltj.medical.base.ViewHolder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class PrivatorDoctorAdapter extends MyBaseAdapter<Map<String, String>> {

private Context mContext;

	public PrivatorDoctorAdapter(Context context, List<Map<String, String>> lst) {
		super(context, lst);
		this.mContext=context;
	}

	@Override
	public int[] getFindViewByIDs() {
		// TODO Auto-generated method stub
		return new int[]{R.id.iv_ada_privated,R.id.tv_ada_privated};
	}

	@Override
	public View getLayout() {
		// TODO Auto-generated method stub
		return LayoutInflater.from(mContext).inflate(R.layout.adapter_privatedoctor, null);
	}

	@Override
	public void renderData(int position, ViewHolder vh) {
		Map<String, String> map=getItemT(position);
		TextView tv=vh.getView(TextView.class, R.id.tv_ada_privated);
		tv.setText(map.get("text"));
		
	}

}
