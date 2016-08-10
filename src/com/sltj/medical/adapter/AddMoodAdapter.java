package com.sltj.medical.adapter;

import java.util.List;
import java.util.Map;

import com.sltj.medical.R;
import com.sltj.medical.base.MyBaseAdapter;
import com.sltj.medical.base.ViewHolder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

/**
 * Author linan Date：2016年8月5日 上午9:26:50
 */
public class AddMoodAdapter extends MyBaseAdapter<Map<String, Object>> {
	private Context mContext;

	public AddMoodAdapter(Context context, List<Map<String, Object>> lst) {
		super(context, lst);
		this.mContext = context;
	}

	@Override
	public int[] getFindViewByIDs() {
		return new int[] { R.id.iv_mood_image, R.id.cb_cheked, R.id.tv_add_mood_ };
	}

	@Override
	public View getLayout() {
		return LayoutInflater.from(mContext).inflate(R.layout.adapter_add_mood, null);
	}

	@Override
	public void renderData(int position, ViewHolder vh) {
		Map<String, Object> map = this.getItemT(position);
		TextView tv = vh.getView(TextView.class, R.id.tv_add_mood_);
		ImageView iv = vh.getView(ImageView.class, R.id.iv_mood_image);
		final RadioButton cb = vh.getView(RadioButton.class, R.id.cb_cheked);
		iv.getDrawable().setLevel((Integer) map.get("imgLevel"));
		final int mood = (Integer) map.get("imgLevel");
		tv.setText(MoodRecord.getMood(mood).detail);
		
//		iv.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				if(!cb.isChecked()){
//					cb.setChecked(true);
//				}else{
//					cb.setChecked(false);
//				}
//			}
//		});

	}


	/*
	 * 心情配置
	 */
	public enum MoodRecord {
		MOOD_100(100, "极度高兴"), MOOD_90(90, "非常高兴"), MOOD_80(80, "开怀大笑"), MOOD_70(70, "高兴"), MOOD_60(60,
				"心如止水"), MOOD_50(50, "有点不高兴"), MOOD_40(40,
						"宝宝不开心"), MOOD_30(30, "忧伤"), MOOD_20(20, "痛苦"), MOOD_10(10, "非常痛苦"), MOOD_0(0, "没有感觉了");
		int moodscore; // 心情分数
		public String detail; // 心情的描述(高兴，不高兴)

		MoodRecord(int id, String detail) {
			this.moodscore = id;
			this.detail = detail;
		}

		public static MoodRecord getMood(int id) {
			for (MoodRecord mood : values()) {
				if (id == mood.moodscore) {
					return mood;
				}

			}
			return MOOD_0;
		}

	}

}
