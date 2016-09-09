package com.sltj.medical.adapter;

import java.util.List;
import java.util.Map;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sltj.medical.R;
import com.sltj.medical.base.MyBaseAdapter;
import com.sltj.medical.base.ViewHolder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Author linan E-mail: fengyunzhinan@163.com Date：2016年6月14日 下午6:34:12
 */
public class HealthHotAdapter extends MyBaseAdapter<Map<String, Object>> {

	private List<Map<String, Object>> lst;
	private Context mContext;
	ImageLoader loader = ImageLoader.getInstance();

	public HealthHotAdapter(Context context, List<Map<String, Object>> lst) {
		super(context, lst);
		this.mContext = context;
		this.lst = lst;
	}

	@Override
	public int[] getFindViewByIDs() {
		return new int[] { R.id.ada_hot_collect, R.id.ada_hot_read, R.id.ada_hot_title, R.id.iv_image };
	}

	@Override
	public View getLayout() {
		return LayoutInflater.from(mContext).inflate(R.layout.adapter_health_hot, null);
	}

	@Override
	public void renderData(int position, ViewHolder vh) {
		TextView tvCollect = vh.getView(TextView.class, R.id.ada_hot_collect);
		TextView tvRead = vh.getView(TextView.class, R.id.ada_hot_read);
		TextView tvTitle = vh.getView(TextView.class, R.id.ada_hot_title);
		ImageView image = vh.getView(ImageView.class, R.id.iv_image);
		Map<String, Object> map = this.getItemT(position);
		tvTitle.setText(map.get("title").toString());
		tvRead.setText(map.get("read") + "阅读");
		tvCollect.setText(map.get("collect") + "收藏");
		String url = String.valueOf(map.get("imgurl"));
			//CROSSFADE 图片淡入淡出效果
		Glide.with(mContext).load(url).centerCrop().placeholder(R.drawable.cat_100).crossFade().skipMemoryCache(false)
				.diskCacheStrategy(DiskCacheStrategy.ALL).priority(Priority.HIGH).into(image);
		// loader.displayImage(url, image);
	}

	String img[] = {
			"http://a1.qpic.cn/psb?/V10wyt7k2pSXWR/VlS9HntVWscupXl5Ord871d51qFfK0iwJDMUdIg5LXw!/m/dOQAAAAAAAAAnull&bo=LAHLAAAAAAAFB8I!&rf=photolist&t=5",
			"ttp://a1.qpic.cn/psb?/V10wyt7k2pSXWR/l7*enLs5vEqUh5k.Jm07TcbOLv5Y09xVunCnh3JTPMk!/m/dAwBAAAAAAAAnull&bo=WAJYAgAAAAAFByQ!&rf=photolist&t=5",
			"http://a3.qpic.cn/psb?/V10wyt7k2pSXWR/zvFb2u*3y5GQCUF0jET2IEds8A.Ov0cFd1KmmlJIgn4!/m/dH4BAAAAAAAAnull&bo=.gB3AQAAAAAFB6g!&rf=photolist&t=5" };

}
