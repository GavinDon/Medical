package com.sltj.medical.adapter;

import java.util.List;
import java.util.Map;

import com.sltj.medical.R;
import com.sltj.medical.base.MyBaseAdapter;
import com.sltj.medical.base.ViewHolder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Author linan 
 * Date：2016年8月3日 上午9:50:00
 */
public class RecordAdapter extends MyBaseAdapter<Map<String, String>> {
	private Context mContext;

	public RecordAdapter(Context context, List<Map<String, String>> lst) {
		super(context, lst);
		this.mContext = context;
	}

	@Override
	public int[] getFindViewByIDs() {
		return new int[]{R.id.tv_record_date,R.id.xuejian,R.id.tv_record_doctor,R.id.iv_look};
	}

	@Override
	public View getLayout() {
		return LayoutInflater.from(mContext).inflate(R.layout.adapter_record, null);
	}

	@Override
	public void renderData(int position, ViewHolder vh) {
		Map<String, String>map=this.getItemT(position);
		TextView tvTitle=vh.getView(TextView.class, R.id.xuejian);
		TextView tvDate=vh.getView(TextView.class, R.id.tv_record_date);
		TextView tvDoctor=vh.getView(TextView.class, R.id.tv_record_doctor);
		ImageView ivLook=vh.getView(ImageView.class, R.id.iv_look);
		tvTitle.setText(map.get("title"));
		tvDate.setText(map.get("date"));
		if(!map.get("treatDoctor").equals("-1")){
			tvDoctor.setVisibility(View.VISIBLE);
			ivLook.setVisibility(View.INVISIBLE);
			tvDoctor.setText(map.get("treatDoctor"));
		}else{
			ivLook.setVisibility(View.VISIBLE);
			tvDoctor.setVisibility(View.INVISIBLE);
		}
	}

}
