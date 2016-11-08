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
 * Author linan E-mail: fengyunzhinan@163.com Date：2016年8月11日 上午10:41:02
 */
public class DoctorListAdapter extends MyBaseAdapter<Map<String, Object>> {
	private Context mContext;
	private List<Map<String, Object>> lst;

	public DoctorListAdapter(Context context, List<Map<String, Object>> lst) {
		super(context, lst);
		this.mContext = context;
	}

	public void setDatas(List<Map<String, Object>> list) {
		this.lst.clear();
		this.lst.addAll(list);
		notifyDataSetChanged();
	}

	@Override
	public int[] getFindViewByIDs() {
		return new int[] { R.id.ada_tv_chatinfo, R.id.ada_tv_doctrename, R.id.ada_tv_doctrjob, R.id.ada_tv_workyears,
				R.id.ada_iv_doctoricon };
	}

	@Override
	public View getLayout() {
		return LayoutInflater.from(mContext).inflate(R.layout.adapter_doctorlist, null);
	}

	@Override
	public void renderData(int position, ViewHolder vh) {
		Map<String, Object> map = this.getItemT(position);
		TextView tvChatInfo = vh.getView(TextView.class, R.id.ada_tv_chatinfo);// 聊天信息
		TextView tvDoctName = vh.getView(TextView.class, R.id.ada_tv_doctrename);// 医生姓名
		TextView tvDoctJob = vh.getView(TextView.class, R.id.ada_tv_doctrjob); // 医生职位
		TextView tvWorkYears = vh.getView(TextView.class, R.id.ada_tv_workyears);// 工龄
		ImageView ivIcon = vh.getView(ImageView.class, R.id.ada_iv_doctoricon);// 医生头像
		tvChatInfo.setText("");
		tvDoctName.setText(map.get("name").toString());
		tvDoctJob.setText(map.get("workYears").toString());

	}

}
