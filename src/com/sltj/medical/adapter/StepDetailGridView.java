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

/**
 * Author linan E-mail: fengyunzhinan@163.com Date：2016年8月12日 下午3:38:55
 */
public class StepDetailGridView extends MyBaseAdapter<Map<String, String>> {

	public StepDetailGridView(Context context, List<Map<String, String>> lst) {
		super(context, lst);
	}

	@Override
	public int[] getFindViewByIDs() {
		return new int[] { R.id.tv_gv_what, R.id.tv_gv_date };
	}

	@Override
	public View getLayout() {
		return LayoutInflater.from(mContext).inflate(R.layout.adapter_stepdetail_gridview, null);
	}

	@Override
	public void renderData(int position, ViewHolder vh) {
		Map<String, String> map = this.getItemT(position);
		TextView title = vh.getView(TextView.class, R.id.tv_gv_what);
		TextView date = vh.getView(TextView.class, R.id.tv_gv_date);
		title.setText(map.get("title"));
		title.setText(map.get("value"));
	}

}
