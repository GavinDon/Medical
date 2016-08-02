package com.sltj.medical;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.fiter.pulltorefresh.PullToRefreshBase;
import com.fiter.pulltorefresh.PullToRefreshBase.Mode;
import com.fiter.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.fiter.pulltorefresh.PullToRefreshListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sltj.medical.adapter.BannerAdapter;
import com.sltj.medical.adapter.HealthHotAdapter;
import com.sltj.medical.base.MyApplication;
import com.sltj.medical.config.Config;
import com.sltj.medical.config.Define;
import com.sltj.medical.dataUtil.HandleMsgDistribute;
import com.sltj.medical.dataUtil.HandleNetSendMsg;
import com.sltj.medical.dataUtil.protobuf.EnumPro.eOPERRESULT_PRO;
import com.sltj.medical.dataUtil.protobuf.PublicmsgPro.Net_NewsInfo_PRO;
import com.sltj.medical.publicMsg.MsgInncDef;
import com.sltj.medical.publicMsg.MsgReceiveDef.HomeNewsResp;
import com.sltj.medical.publicMsg.MsgReceiveDef.ReadNewsResp;
import com.sltj.medical.service.StepService;
import com.sltj.medical.socketutil.HouseSocketConn;
import com.sltj.medical.util.FixedSpeedScroller;
import com.sltj.medical.util.LogUtils;
import com.sltj.medical.util.ToastUtils;
import com.sltj.medical.wedgit.RoundProgressBar;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HomepageFragment extends Fragment implements OnClickListener, OnItemClickListener {

	private ViewPager adViewPager;
	private List<ImageView> imageViews;// 滑动的图片集合

	private List<View> dots; // 图片标题正文的那些点
	private int currentItem = 0; // 当前图片的索引号
	// 定义的指示点
	private View dot0;
	private View dot1;
	private View dot2;
	private final static int DELAY_AD_PAGER = 1;

	private Messenger messenger;
	private MyHandler mHandler;
	private Messenger getReplyMessager;
	private long TIME_INTERVAL = 500;
	private TextView text_step;
	private TextView text_cal;
	private int step;//今日走路步数;

	// 异步加载图片
	private ImageLoader mImageLoader;
	private DisplayImageOptions options;
	List<Map<String, Object>> lst = new ArrayList<Map<String, Object>>(); // 资讯列表List
	private HealthHotAdapter mAdapter;

	// 轮播banner的数据
	private String AdList[];
	private View view;
	private Context mContext;
	private RoundProgressBar stepProgress;
	private LinearLayout ll_Step;

	private PullToRefreshListView mPullListview;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case DELAY_AD_PAGER:
				adViewPager.setCurrentItem(msg.arg1);
				break;
			}
		};
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this.getActivity();
		// 初始化handler并
		mHandler = new MyHandler((Activity) mContext);
		getReplyMessager = new Messenger(mHandler);
		IntentFilter filter = new IntentFilter();
		filter.addAction(Define.BROAD_CAST_RECV_DATA_COMPLETE);
		this.getActivity().registerReceiver(mReceiver, filter);
		// 获取图片加载实例
		mImageLoader = ImageLoader.getInstance();
		// 广播图片的URL地址;
		AdList = new String[] { Config.URL_BANNER_IMAG1, Config.URL_BANNER_IMAG2, Config.URL_BANNER_IMAG3 };
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.homepage_fragment, null);
		initAdData();
		newsProbReq();
		setStep();
		setupService();
		return view;
	}

	/**
	 * 计算走了多少步
	 */
	private void setStep() {
		stepProgress = (RoundProgressBar) view.findViewById(R.id.pb_count);
		stepProgress.setTextSize(18);
		stepProgress.setMax(10000);

		text_step = (TextView) view.findViewById(R.id.tv_step);
		text_cal = (TextView) view.findViewById(R.id.tv_cal);
		ll_Step = (LinearLayout) view.findViewById(R.id.ll_step);
		ll_Step.setOnClickListener(this);
		initListView();// 上拉加载下拉刷新

	}

	private void initAdData() {
		imageViews = new ArrayList<ImageView>();

		// 点
		dots = new ArrayList<View>();
		dot0 = view.findViewById(R.id.v_dot0);
		dot1 = view.findViewById(R.id.v_dot1);
		dot2 = view.findViewById(R.id.v_dot2);
		dots.add(dot0);
		dots.add(dot1);
		dots.add(dot2);
		adViewPager = (ViewPager) view.findViewById(R.id.vp);
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
			mImageLoader.displayImage(AdList[i], imageView, options);
			imageView.setScaleType(ScaleType.CENTER_CROP);
			imageViews.add(imageView);
			dots.get(i).setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mContext.getApplicationContext().unbindService(conn);
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
					message.what = DELAY_AD_PAGER;
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

	/*
	 * 热门资讯列表
	 */
	@SuppressWarnings("unchecked")
	private void initListView() {
		mPullListview = (PullToRefreshListView) view.findViewById(R.id.lv_hots);
		mAdapter=new  HealthHotAdapter(mContext, lst);
		mPullListview.setAdapter(mAdapter);
		mPullListview.setOnItemClickListener(this);

		// 设置模式为上拉加载
		mPullListview.setMode(Mode.DISABLED);
		mPullListview.setOnRefreshListener(new OnRefreshListener2() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				String label = DateUtils.formatDateTime(getActivity().getApplicationContext(),
						System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel("上次更新时间：" + label);
				newsProbReq();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {
				String label = DateUtils.formatDateTime(getActivity().getApplicationContext(),
						System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel("上次更新时间：" + label);
				newsProbReq();
			}
		});

	}

	// 启动服务
	private void setupService() {
		Intent intent = new Intent(mContext.getApplicationContext(), StepService.class);
		mContext.getApplicationContext().bindService(intent, conn, Context.BIND_AUTO_CREATE);
		mContext.getApplicationContext().startService(intent);
	}

	ServiceConnection conn = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {

			try {
				messenger = new Messenger(service);
				Message msg = Message.obtain(null, Config.MSG_FROM_CLIENT);
				msg.replyTo = getReplyMessager;
				messenger.send(msg);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			System.out.println(name);
		}
	};

	class MyHandler extends Handler {
		WeakReference<Activity> mActivityReference;
		DecimalFormat format = new DecimalFormat("####.##");

		MyHandler(Activity activity) {
			mActivityReference = new WeakReference<Activity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			final Activity activity = mActivityReference.get();
			if (activity != null) {
				switch (msg.what) {
				case Config.MSG_FROM_SERVER:
					 step = msg.getData().getInt("step");
					text_step.setText(step + "");
					String cal = format.format(msg.getData().getDouble("cal"));
					text_cal.setText(cal);
					stepProgress.setProgress(step);// 设置一天中的运动百分比
					mHandler.sendEmptyMessageDelayed(Config.REQUEST_SERVER, TIME_INTERVAL);
					break;
				case Config.REQUEST_SERVER:
					try {
						Message msg1 = Message.obtain(null, Config.MSG_FROM_CLIENT);
						msg1.replyTo = getReplyMessager;
						messenger.send(msg1);
					} catch (RemoteException e) {
						e.printStackTrace();
					}

					break;
				case Config.DELAY_AD_PAGER:
					adViewPager.setCurrentItem(msg.arg1);
					break;
				case Config.LOAD_DATA_SUCCESS:
					mAdapter.notifyDataSetChanged();
					mPullListview.onRefreshComplete();
					break;
				case Config.LOAD_DATA_OVERTIME:
					mAdapter.notifyDataSetChanged();
					mPullListview.onRefreshComplete();
					break;
				}
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Map<String, Object> map = (Map<String, Object>) parent.getAdapter().getItem(position);
		Net_NewsInfo_PRO info = (Net_NewsInfo_PRO) map.get("news");
		ToastUtils.show(this.getActivity(), info.getId() + "", 0);
		readNewsProbReq(info.getId());
	}
	// ----------------以下为消息请求与解析处理----分割线-----------//

	private int seqHomeNews = -1; // 首页资讯请求seq;
	private int seqReadNews = -1; // 首页资讯请求seq;

	/*
	 * 首页资讯请求
	 */
	private void newsProbReq() {
		handler.sendEmptyMessageDelayed(Config.LOAD_DATA_OVERTIME, 4000);
//		MyApplication.authSocketConn.closeAuthSocket();
//		HouseSocketConn mSocket = new HouseSocketConn("218.30.21.157", 10011);
		seqHomeNews = MyApplication.SequenceNo++;
		MsgInncDef.IhomeNewsReq homeNews = new MsgInncDef.IhomeNewsReq();
		homeNews.iUserId = MyApplication.userId;
		byte[] btData = HandleNetSendMsg.HandleHomeNewsPro(homeNews, seqHomeNews);
		HouseSocketConn.pushtoList(btData);
	}

	/*
	 * 资讯响应
	 */

	private void newsProbResp(long recvTime) {
		HomeNewsResp resp = (HomeNewsResp) HandleMsgDistribute.getInstance().queryCompleteMsg(recvTime);
		List<Net_NewsInfo_PRO> info = resp.info;
		if (info.size() > 0) {
			for (int i = 0; i < info.size(); i++) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("read", info.get(i).getIHaveReadNum());
				map.put("collect", info.get(i).getICollectionNum());
				map.put("title", info.get(i).getSzTitle());
				map.put("news", info.get(i));
				String ss=info.get(i).getSzImage();
				mAdapter.append(map);
			}
			mHandler.sendEmptyMessage(Config.LOAD_DATA_SUCCESS);

		}
	}

	/*
	 * 阅读资讯请求
	 */
	private void readNewsProbReq(int newsId) {
		seqReadNews = MyApplication.SequenceNo++;
		MsgInncDef.IReadNewsReq readNews = new MsgInncDef.IReadNewsReq();
		readNews.iUserId = MyApplication.userId;
		readNews.inewsid = newsId;
		byte[] btData = HandleNetSendMsg.HandleReadNewsPro(readNews, seqReadNews);
		HouseSocketConn.pushtoList(btData);
	}

	private void readNewsProbResp(long recvTime) {
		ReadNewsResp resp = (ReadNewsResp) HandleMsgDistribute.getInstance().queryCompleteMsg(recvTime);
		eOPERRESULT_PRO s = resp.eResult;
		LogUtils.i(resp.eResult + "");

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
				if (iSequence == seqHomeNews) {
					// 资讯处理
					newsProbResp(recvTime);
				}
				if (iSequence == seqReadNews) {
					// 资讯处理
					readNewsProbResp(recvTime);
				}

			}

		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_step:
			Intent mIntent = new Intent(this.getActivity(), StepDetailActivity.class);
			mIntent.putExtra("todayStep", step);
			startActivity(mIntent);
			break;

		default:
			break;
		}
	}

}
