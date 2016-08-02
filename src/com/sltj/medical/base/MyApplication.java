package com.sltj.medical.base;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

import org.apache.http.util.EncodingUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.sltj.medical.R;
import com.sltj.medical.bean.NetParam;
import com.sltj.medical.config.Define;
import com.sltj.medical.dao.DbCore;
import com.sltj.medical.service.NetWorkStateService;
import com.sltj.medical.service.NetWorkStateService.GetConnState;
import com.sltj.medical.socketutil.AuthSocketConn;
import com.sltj.medical.socketutil.DNSParsing;
import com.sltj.medical.socketutil.HouseSocketConn;
import com.sltj.medical.socketutil.PushData;
import com.sltj.medical.util.CrashHandler;
import com.sltj.medical.util.LogUtils;
import com.sltj.medical.util.MTools;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;

public class MyApplication extends Application {
	public static Context context;
	public static int userId = 0;
	public String accoutId; // 帐号ID

	public static AuthSocketConn authSocketConn;
	public static HouseSocketConn houseSocketConn;

	public static boolean exitState = false; // 帐号退出状态
	public static boolean connState = true; // 记录当前连接状态，因为广播会接收所有的网络状态改变wifi/3g等等，所以需要一个标志记录当前状态
	public NetWorkStateService netWorkState; // 监测网络状态服务

	public static String stcliyLocation; // 具体城市
	public static int iCityLocationId = 610100;// 城市id
	public static boolean cityLocationFlag = false;// 用于标识是否是用户选定的地理位置

	public static int badgeViewCount = 0;// 用于订单按钮右上角红点的数量

	// 登陆状态
	public static boolean loginState = false;
	public static boolean isNetConnOpen = false;
	// webView刷新
	public static boolean isFresh = true;

	public static int vertifacationCode;// 短信验证码
	public static Map<String, Long> map;// 用于存放倒计时时间

	public static int SequenceNo = 0;// 用于全局发送数据的sequenceNo，每发一条消息会自加
	public static int seqLoginConnHouse = -1;// 用于在登录界面，登录成功后发送的连接家政服务器的sequence
	public static int seqServiceConnAuth = -1;// 用于在服务中，连接登录认证 服务器的sequence
	public static int seqServiceConnHouse = -1;// 用于在服务中，连接家政服务器的sequence

	public static NetParam netParam = new NetParam();

	@Override
	public void onCreate() {
		context = this.getApplicationContext();

		CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(this);
		DbCore.init(getApplicationContext());
		initImageLoader();
		getLinkConfig();// 解析网络连接配置文件

		// 判断网络连接情况
		if (isNetworkConnected() != false) {
			isNetConnOpen = true;
			connState = true;
			 new Thread(runnable).start();
		} else {
			connState = false;
//			Toast.makeText(context, "网络连接错误，请检查网络连接", 0).show();
		}

		bind();// 绑定服务
		// setShareConfig();
	}

	/**
	 * 第三方分享的配置
	 */
	// private void setShareConfig() {
	// // 微信分享
	// PlatformConfig.setWeixin(Constants.WX_APP_ID, Constants.WX_APP_SECRET);
	// // 新浪微博
	// PlatformConfig.setSinaWeibo(Constants.SINA_KEY,
	// Constants.SINA_SECRET);
	// // QQ空间
	// PlatformConfig.setQQZone("100424468",
	// "c7394704798a158208a74ab60104f0ba");
	// }

	/*
	 * ImageLoaderConfiguration的配置主要是全局性的配置，主要有线程类、缓存大小、磁盘大小、图片下载与解析、日志方面的配置。
	 */
	public static String IMAGE_CACHE_PATH = "imageloader/Cache"; // 图片缓存路

	public void initImageLoader() {
		// 配置ImageLoader的属性，并没有把图片缓存起来，可以在加载图片的时候缓存起来
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).memoryCacheExtraOptions(480, 800)
				// .discCacheExtraOptions(480, 800, null)
				// Can slow ImageLoader, use it carefully (Better don't use
				// it)/设置缓存的详细信息，最好不要设置这个
				.threadPoolSize(3)
				// 线程池内加载的数量
				.threadPriority(Thread.NORM_PRIORITY - 2).denyCacheImageMultipleSizesInMemory()
				.memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024))
				// You can pass your own memory cache
				// implementation/你可以通过自己的内存缓存实现
				.memoryCacheSize(2 * 1024 * 1024)
				// .discCacheSize(50 * 1024 * 1024)
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				// 将保存的时候的URI名称用MD5 加密
				.tasksProcessingOrder(QueueProcessingType.LIFO).discCacheFileCount(100)
				// 缓存的文件数量
				.discCache(new UnlimitedDiscCache(new File(Define.PATH_BANNER_IMAGE_CACHE)))
				// 自定义缓存路径
				.defaultDisplayImageOptions(getDisplayOptions())
				.imageDownloader(new BaseImageDownloader(this, 5 * 1000, 30 * 1000)).writeDebugLogs() // Remove
																										// for
																										// release
																										// app
				.build();// 开始构建
		ImageLoader.getInstance().init(config);// 将配置信息写入imageLoader中
	}

	// image的设置
	public static DisplayImageOptions getDisplayOptions() {
		DisplayImageOptions options;
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.top_banner_android) // 设置图片在下载期间显示的图片
				.showImageForEmptyUri(R.drawable.top_banner_android)// 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.drawable.top_banner_android) // 设置图片加载/解码过程中错误时候显示的图片
				.cacheInMemory(true)// 设置下载的图片是否缓存在内存中
				.cacheOnDisc(true)// 设置下载的图片是否缓存在SD卡中
				.considerExifParams(true) // 是否考虑JPEG图像EXIF参数（旋转，翻转）
				.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)// 设置图片以如何的编码方式显示
				.bitmapConfig(Bitmap.Config.RGB_565)// 设置图片的解码类型//
				// .delayBeforeLoading(int delayInMillis)//int
				// delayInMillis为你设置的下载前的延迟时间
				// 设置图片加入缓存前，对bitmap进行设置
				// .preProcessor(BitmapProcessor preProcessor)
				.resetViewBeforeLoading(true)// 设置图片在下载前是否重置，复位
				.displayer(new RoundedBitmapDisplayer(20))// 是否设置为圆角，弧度为多少
				.displayer(new FadeInBitmapDisplayer(100))// 是否图片加载好后渐入的动画时间
				.build();// 构建完成
		return options;
	}

	/*
	 * 判断是否有网络连接，没有的话提示客户连接网络
	 */
	public boolean isNetworkConnected() {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	private void bind() {
		Intent intent = new Intent(context, NetWorkStateService.class);
		bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
	}

	// 当与sevice建立连接后调用
	public ServiceConnection serviceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			netWorkState = ((NetWorkStateService.MyBinder) service).getService();
			netWorkState.setConnState(new GetConnState() {// 添加接口实例获取连接状态

				@Override
				public void getConnState(boolean isConnected) {

					connState = isConnected;
				}

			});
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			LogUtils.i("绑定失败");
		}
	};


	/**
	 * 启动线程解析域名
	 */
	 Runnable runnable = new Runnable() {
	
	 @Override
	 public void run() {
	
	 if (MTools.isStringEmpty(netParam.getAuthDns())) {
	 PushData.setAuthIp(netParam.getAuthIp());
	 PushData.setAuthPort(netParam.getAuthPort());
	 } else {
	 String strDns = netParam.getAuthDns();
	
	 if(strDns.indexOf(":") > 0){
	 String ip = DNSParsing.getIP(strDns.substring(0, strDns.indexOf(":")));
	 int port = Integer.parseInt(strDns.substring(
	 strDns.indexOf(":") + 1, strDns.length()));
	 netParam.setAuthDnsParsIp(ip);
	 netParam.setAuthDnsParsPort(port);
	 PushData.setAuthIp(ip);
	 PushData.setAuthPort(port);
	 }else{
	 PushData.setAuthIp(netParam.getAuthIp());
	 PushData.setAuthPort(netParam.getAuthPort());
	 }
	
	 }
	 LogUtils.i("连接socket");
	// socket = new SocketConn(ip, port);
	 authSocketConn = new AuthSocketConn(PushData.getAuthIp(),
	 PushData.getAuthPort());
	 LogUtils.i("IP_________"+PushData.getAuthIp()+ PushData.getAuthPort());
	 }
	 };
	
	/*
	 * 读取assets中 网络连接的配置文件
	 */
	private void getLinkConfig() {
		String fileName = "link_config.txt";// assets下文件
		String linkInfo = "";
		try {
			InputStream is = getResources().getAssets().open(fileName);
			int len = is.available();
			byte[] buffer = new byte[len + 1];
			is.read(buffer);
			linkInfo = EncodingUtils.getString(buffer, "utf-8");
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		JSONObject object;
		try {
			object = new JSONObject(linkInfo);
			JSONObject linkObj = object.getJSONObject("link");
			netParam.setAuthIp(linkObj.getString("ip"));
//			netParam.setAuthDns(linkObj.getString("dns"));
			netParam.setAuthPort(linkObj.getInt("port"));
			// netParam.setStrIp(linkObj.getString("ip"));
			// netParam.setiPort(linkObj.getInt("port"));
			// netParam.setStrDns(linkObj.getString("dns"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
