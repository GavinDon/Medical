package com.sltj.medical;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.fiter.pulltorefresh.PullToRefreshBase;
import com.fiter.pulltorefresh.PullToRefreshBase.Mode;
import com.fiter.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.fiter.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.fiter.pulltorefresh.PullToRefreshListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sltj.medical.adapter.BannerAdapter;
import com.sltj.medical.adapter.HealthHotAdapter;
import com.sltj.medical.base.MyApplication;
import com.sltj.medical.config.Config;
import com.sltj.medical.config.Define;
import com.sltj.medical.dataUtil.HandleMsgDistribute;
import com.sltj.medical.dataUtil.HandleNetSendMsg;
import com.sltj.medical.dataUtil.protobuf.EnumPro.eNEWS_PRO;
import com.sltj.medical.dataUtil.protobuf.EnumPro.ePAGE_TYPE_PRO;
import com.sltj.medical.dataUtil.protobuf.PublicmsgPro.Net_NewsInfo_PRO;
import com.sltj.medical.publicMsg.MsgInncDef;
import com.sltj.medical.publicMsg.MsgReceiveDef;
import com.sltj.medical.publicMsg.MsgInncDef.OnrefreshFlag;
import com.sltj.medical.publicMsg.MsgReceiveDef.NewsResp;
import com.sltj.medical.socketutil.HouseSocketConn;
import com.sltj.medical.util.FixedSpeedScroller;
import com.sltj.medical.util.LogUtils;
import com.sltj.medical.util.ToastUtils;
import com.sltj.medical.util.MTools;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.mtp.MtpObjectInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

/**
 * 资讯Fragment
 * 
 * @author linan
 *
 */

public class NewsFragment extends Fragment implements OnCheckedChangeListener, OnItemClickListener {
	private RadioGroup rg;
	private View view;
	private View headView;
	private Context mContext;
	private ViewPager adViewPager;
	private List<ImageView> imageViews;// 滑动的图片集合

	private List<View> dots; // 图片标题正文的那些点
	private int currentItem = 0; // 当前图片的索引号
	// 定义的指示点
	private View dot0;
	private View dot1;
	private View dot2;

	// 异步加载图片
	private ImageLoader mImageLoader;
	List<Map<String, Object>> lst = new ArrayList<Map<String, Object>>();

	private PullToRefreshListView mPullListView;

	private OnrefreshFlag freshFlag; // 是下拉默认为true;

	private HealthHotAdapter mAdapter;
	// 返回参数排序
	Comparator<Long> reverseOrder = Collections.<Long> reverseOrder();
	List<Long> timeList = new ArrayList<Long>();// 资讯创建时间列表
	// 轮播banner的数据
	private String AdList[];
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case Config.DELAY_AD_PAGER:
				adViewPager.setCurrentItem(msg.arg1);
				break;
			case Config.LOAD_DATA_SUCCESS:
				mAdapter.notifyDataSetChanged();
				mPullListView.onRefreshComplete();
				break;
			case Config.LOAD_DATA_OVERTIME:
				// mAdapter.notifyDataSetChanged();
				mPullListView.onRefreshComplete();
				// ToastUtils.show(mContext, "请求失败",0);

			}
		};
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Define.BROAD_CAST_RECV_DATA_COMPLETE);
		this.getActivity().registerReceiver(mReceiver, filter);
		mImageLoader = ImageLoader.getInstance();
		AdList = new String[] { Config.URL_BANNER_IMAG1, Config.URL_BANNER_IMAG2, Config.URL_BANNER_IMAG3 };
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.news_listview, null);
		headView = inflater.inflate(R.layout.fragment_news, null);
		initView();
		return view;
	}

	private ListView mListview;

	private void initView() {
		rg = (RadioGroup) headView.findViewById(R.id.rg_news_bar);
		rg.setOnCheckedChangeListener(this);
		// 默认选中精准医疗页面
		rg.check(R.id.rb_news_medica);
		// setHotData();
		initPullFresh();
		mListview.addHeaderView(headView, null, false);
		initAdData();
	}

	@SuppressWarnings("unchecked")
	private void initPullFresh() {
		mPullListView = (PullToRefreshListView) view.findViewById(R.id.lv_news);
		// 解决当没有数据的时候不显示头部;
		mAdapter = new HealthHotAdapter(mContext, lst);
		mPullListView.setAdapter(mAdapter);
		//
		mPullListView.setOnItemClickListener(this);
		mPullListView.setMode(Mode.BOTH);
		mListview = mPullListView.getRefreshableView();
		mPullListView.setOnRefreshListener(new OnRefreshListener2() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				freshFlag = OnrefreshFlag.REFRESH_DOWN;
				String label = DateUtils.formatDateTime(getActivity().getApplicationContext(),
						System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel("上次更新时间：" + label);
				switch (checkdTag) {
				case R.id.rb_news_medica:
					newsProbReq(eNEWS_PRO.EN_NEWS_ACCURATE_PRO_VALUE);
					break;
				case R.id.rb_news_technology:
					newsProbReq(eNEWS_PRO.EN_NEWS_BIOTECH_PRO_VALUE);
					break;
				case R.id.rb_news_health:
					newsProbReq(eNEWS_PRO.EN_NEWS_HEALTH_PRO_VALUE);
					break;
				case R.id.rb_news_control:
					newsProbReq(eNEWS_PRO.EN_NEWS_NCD_CONTRAL_PRO_VALUE);
					break;

				}
				freshFlag = null;

			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {
				freshFlag = OnrefreshFlag.REFRESH_PullUp;
				String label = DateUtils.formatDateTime(getActivity().getApplicationContext(),
						System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel("上次更新时间：" + label);
				switch (checkdTag) {
				case R.id.rb_news_medica:
					newsProbReq(eNEWS_PRO.EN_NEWS_ACCURATE_PRO_VALUE);
					break;
				case R.id.rb_news_technology:
					newsProbReq(eNEWS_PRO.EN_NEWS_BIOTECH_PRO_VALUE);
					break;
				case R.id.rb_news_health:
					newsProbReq(eNEWS_PRO.EN_NEWS_HEALTH_PRO_VALUE);
					break;
				case R.id.rb_news_control:
					newsProbReq(eNEWS_PRO.EN_NEWS_NCD_CONTRAL_PRO_VALUE);
					break;

				}
				freshFlag = null;

			}

		});

	}

	// 上拉加载时需要判断是哪个资讯类型
	private int checkdTag;

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		freshFlag = OnrefreshFlag.REFRESH_PullUp;
		switch (checkedId) {
		case R.id.rb_news_medica:
			lst.clear();
			timeList.clear();
			newsProbReq(eNEWS_PRO.EN_NEWS_ACCURATE_PRO_VALUE);
			checkdTag = checkedId;
			break;
		case R.id.rb_news_technology:
			lst.clear();
			timeList.clear();
			newsProbReq(eNEWS_PRO.EN_NEWS_BIOTECH_PRO_VALUE);
			checkdTag = checkedId;
			break;
		case R.id.rb_news_health:
			lst.clear();
			timeList.clear();
			newsProbReq(eNEWS_PRO.EN_NEWS_HEALTH_PRO_VALUE);
			checkdTag = checkedId;
			break;
		case R.id.rb_news_control:
			lst.clear();
			timeList.clear();
			newsProbReq(eNEWS_PRO.EN_NEWS_NCD_CONTRAL_PRO_VALUE);
			checkdTag = checkedId;
			break;
		}
		freshFlag = null;

	}

	private void initAdData() {
		imageViews = new ArrayList<ImageView>();

		// 点
		dots = new ArrayList<View>();
		dot0 = headView.findViewById(R.id.v_dot0);
		dot1 = headView.findViewById(R.id.v_dot1);
		dot2 = headView.findViewById(R.id.v_dot2);
		dots.add(dot0);
		dots.add(dot1);
		dots.add(dot2);
		adViewPager = (ViewPager) headView.findViewById(R.id.vp);
		addDynamicView();// 动态添加图片资源
		adViewPager.setAdapter(new BannerAdapter(mContext, imageViews));// 设置填充ViewPager页面的适配器
		// 设置一个监听器，当ViewPager中的页面改变时调用
		startLoop();
		adViewPager.setOnPageChangeListener(new MyPageChangeListener());
		adViewPager.setOffscreenPageLimit(1);
		adViewPager.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_UP:
					startLoop();
					break;
				case MotionEvent.ACTION_MOVE:
					stopLoop();
					break;
				case MotionEvent.ACTION_DOWN:
					stopLoop();
					break;

				}

				return false;
			}
		});
		try {
			// 利用Relect修改mScroller private Scroller mScroller = new
			// Scroller(context);
			Field field = ViewPager.class.getDeclaredField("mScroller");
			field.setAccessible(true);
			FixedSpeedScroller scroller = new FixedSpeedScroller(adViewPager.getContext(),
					new AccelerateInterpolator());
			field.set(adViewPager, scroller);
			scroller.setmDuration(300);// 设置图片平滑滚动持续的时间
		} catch (Exception e) {

		}

	}

	private void addDynamicView() {
		// 动态添加图片和下面指示的圆点
		// 初始化图片资源
		for (int i = 0; i < AdList.length; i++) {
			ImageView imageView = new ImageView(mContext);
			// 异步加载图片
			mImageLoader.displayImage(AdList[i], imageView, MyApplication.getDisplayOptions());
			imageView.setScaleType(ScaleType.CENTER_CROP);
			imageViews.add(imageView);
			dots.get(i).setVisibility(View.VISIBLE);
		}
	}

	private Timer timer;

	private void startLoop() {
		if (timer == null)// 当用户快速滑动的时候，touchup有几率触发两次，导致开启两个定时器，加个判断防止
		{
			timer = new Timer();
			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					Message message = handler.obtainMessage();
					message.what = Config.DELAY_AD_PAGER;
					message.arg1 = currentItem;
					handler.sendMessage(message);
					currentItem++;
				}
			}, 10, 2000);
		}
	}

	private void stopLoop() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

	/**
	 * viewpager切换时的监听器
	 * 
	 * @author linan
	 *
	 */
	private class MyPageChangeListener implements OnPageChangeListener {
		// 显示当前位置的前一个位置
		private int oldPosition = 0;

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageSelected(int position) {
			// 无限轮播。获取当前的位置。并把上一个位置圆点置暗
			currentItem = position;
			dots.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);
			dots.get(position % imageViews.size()).setBackgroundResource(R.drawable.dot_focused);
			oldPosition = position % imageViews.size();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Map<String, Object> map = (Map<String, Object>) parent.getAdapter().getItem(position);
		Intent mIntent = new Intent(this.getActivity(), NewsDetailActivity.class);
		mIntent.putExtra("newsId", String.valueOf(map.get("newsId")));
		startActivity(mIntent);
	}

	// --------------------------------数据请求分割线---------------------------------------//
	private int seqAccurate;// 精准医疗
	private int seqBiotech;// 生物科技
	private int seqHealth;// 健康美学
	private int seqContral;// 慢病控制

	/*
	 * 资讯列表请求
	 */
	private void newsProbReq(int type) {
		byte[] btData;
		handler.sendEmptyMessageDelayed(Config.LOAD_DATA_OVERTIME, 4000);
		MsgInncDef.INewsReq newsReq = new MsgInncDef.INewsReq();
		newsReq.iUserId = MyApplication.userId;
		newsReq.eType = type;
		/*
		 * 判断 上拉下拉需要上传的时间点
		 */
		if (freshFlag == OnrefreshFlag.REFRESH_DOWN) {
			if (!timeList.isEmpty()) {
				newsReq.szTime = MTools.getTimeYMDHMS(timeList.get(0));
			} else {
				newsReq.szTime = MTools.getCurrentDate("yyyy-MM-dd HH:mm:ss");
			}
			newsReq.ePageType = ePAGE_TYPE_PRO.EN_PAGE_NEW_PRO_VALUE;
		} else if (freshFlag == OnrefreshFlag.REFRESH_PullUp) {
			if (timeList.isEmpty()) {
				newsReq.szTime = MTools.getCurrentDate("yyyy-MM-dd HH:mm:ss");
			} else {
				newsReq.szTime = MTools.getTimeYMDHMS(timeList.get(timeList.size() - 1));
			}
			newsReq.ePageType = ePAGE_TYPE_PRO.EN_PAGE_OLD_PRO_VALUE;
		}

		switch (type) {
		case eNEWS_PRO.EN_NEWS_ACCURATE_PRO_VALUE:// 精准医疗
			seqAccurate = MyApplication.SequenceNo++;
			btData = HandleNetSendMsg.HandleNewsPro(newsReq, seqAccurate);
			HouseSocketConn.pushtoList(btData);
			break;
		case eNEWS_PRO.EN_NEWS_BIOTECH_PRO_VALUE: // 生物科技
			seqBiotech = MyApplication.SequenceNo++;
			btData = HandleNetSendMsg.HandleNewsPro(newsReq, seqBiotech);
			HouseSocketConn.pushtoList(btData);
			break;
		case eNEWS_PRO.EN_NEWS_HEALTH_PRO_VALUE:// 健康美学
			seqHealth = MyApplication.SequenceNo++;
			btData = HandleNetSendMsg.HandleNewsPro(newsReq, seqHealth);
			HouseSocketConn.pushtoList(btData);
			break;
		case eNEWS_PRO.EN_NEWS_NCD_CONTRAL_PRO_VALUE:// 慢病控制
			seqContral = MyApplication.SequenceNo++;
			btData = HandleNetSendMsg.HandleNewsPro(newsReq, seqContral);
			HouseSocketConn.pushtoList(btData);
			break;
		}
	}

	/*
	 * 资讯列表结果
	 */

	private void newsProResp(Long recvTime) {

		NewsResp resp = (NewsResp) HandleMsgDistribute.getInstance().queryCompleteMsg(recvTime);
		List<Net_NewsInfo_PRO> info = resp.info;

		if (!info.isEmpty()) {
			for (int i = 0; i < info.size(); i++) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("read", info.get(i).getIHaveReadNum());
				map.put("collect", info.get(i).getICollectionNum());
				map.put("title", info.get(i).getSzTitle());
				map.put("newsId", info.get(i).getId());
				map.put("imgurl", info.get(i).getSzImage());
				lst.add(map);
				timeList.add(MTools.datetimeToTimeMillis(info.get(i).getSzCreateTime()));
			}
			Collections.sort(timeList, reverseOrder);
			handler.sendEmptyMessage(Config.LOAD_DATA_SUCCESS);
		}

	}

	/*
	 * 广播接收从socket中得到的值
	 */
	BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (Define.BROAD_CAST_RECV_DATA_COMPLETE.equals(intent.getAction())) {
				int iSequence = intent.getIntExtra(Define.BROAD_SEQUENCE, -1);
				long recvTime = intent.getLongExtra(Define.BROAD_MSG_RECVTIME, -1);
				if (iSequence == seqAccurate || iSequence == seqBiotech || iSequence == seqContral
						|| iSequence == seqHealth) {
					// 资讯处理
					newsProResp(recvTime);
				}

			}

		}
	};

}
