package com.sltj.medical.adapter;

import java.util.List;
import java.util.Map;

import com.sltj.medical.R;
import com.sltj.medical.adapter.AddMoodAdapter.MoodRecord;
import com.sltj.medical.base.MyBaseAdapter;
import com.sltj.medical.base.ViewHolder;
import com.sltj.medical.util.MTools;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MoodRecordAdapter extends MyBaseAdapter<Map<String, String>> {
	private Context mContext;

	public MoodRecordAdapter(Context context, List<Map<String, String>> lst) {
		super(context, lst);
		this.mContext = context;
	}

	@Override
	public int[] getFindViewByIDs() {

		return new int[] { R.id.tv_ada_mood_time, R.id.tv_ada_mood_title, R.id.iv_ada_mood_q };
	}
 
	@Override
	public View getLayout() {
		return LayoutInflater.from(mContext).inflate(R.layout.adapter_mood_record, null);
	}

	@Override
	public void renderData(int position, ViewHolder vh) {
		Map<String, String>map=this.getItemT(position);
		TextView tvTime = vh.getView(TextView.class, R.id.tv_ada_mood_time);
		TextView tvTitle = vh.getView(TextView.class, R.id.tv_ada_mood_title);
		ImageView iv = vh.getView(ImageView.class, R.id.iv_ada_mood_q);
		tvTime.setText(MTools.subTimeToMinute(map.get("time")));
		int grade=Integer.parseInt(map.get("grade"));
		tvTitle.setText("心情得分："+grade+"("+MoodRecord.getMood(grade).detail +")");
		iv.getDrawable().setLevel(grade);
//		iv.setImageLevel(50);
 
	}

}
