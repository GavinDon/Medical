package com.sltj.medical.adapter;

import java.util.List;
import java.util.Map;

import com.sltj.medical.R;
import com.sltj.medical.base.MyBaseAdapter;
import com.sltj.medical.base.ViewHolder;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Author linan
 * Date：2016年8月2日 下午3:05:49
 */
public class HeathManagerAdapter extends MyBaseAdapter<Map<String, Object>> {
	private Context mContext;

	public HeathManagerAdapter(Context context, List<Map<String, Object>> lst) {
		super(context, lst);
		this.mContext = context;
	}

	@Override
	public int[] getFindViewByIDs() {
		return new int[] { R.id.ada_tv_mine_title, R.id.ada_iv_mine_img };
	}

	@Override
	public View getLayout() {
		return LayoutInflater.from(mContext).inflate(R.layout.adapter_healthmanager_listview, null);
	}
	@Override
	public void renderData(int position, ViewHolder vh) {
		int[] img = new int[] { R.drawable.breast, R.drawable.treat, R.drawable.pharmacy, R.drawable.sports, R.drawable.test };
		Map<String, Object>map = this.getItemT(position);
		TextView tv = vh.getView(TextView.class, R.id.ada_tv_mine_title);
		ImageView iv=vh.getView(ImageView.class,R.id.ada_iv_mine_img);
		tv.setText(map.get("title").toString());
		Drawable d=mContext.getResources().getDrawable(img[position]);
		iv.setImageDrawable(d);
	}

}
