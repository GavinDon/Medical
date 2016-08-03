package com.sltj.medical.service;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.sltj.medical.MainActivity;
import com.sltj.medical.R;
import com.sltj.medical.config.Config;
import com.sltj.medical.dao.DbCore;
import com.sltj.medical.dao.stepTable;
import com.sltj.medical.dao.stepTableDao;
import com.sltj.medical.dao.stepTableDao.Properties;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;

public class StepService extends Service implements SensorEventListener {
	// 默认为30秒进行一次存储
	private static int duration = 30000;
	private static String CURRENTDATE = "";
	private SensorManager sensorManager;
	private StepDcretor stepDetector;
	private NotificationManager nm;
	private NotificationCompat.Builder builder;
	private Messenger messenger = new Messenger(new MessenerHandler());
	private BroadcastReceiver mBatInfoReceiver;
	private WakeLock mWakeLock;
	private TimeCount time;
	private static double distance = 0.0;
	private int step_length = 70; // 步长
	private static int weight = 60; // 体重
	// 根据体重步长 换算卡路里
	private static double calories = weight * distance * 0.001;

	// 测试
	private static int i = 0;
	// 计步传感器类型 0-counter 1-detector
	private static int stepSensor = -1;

	private static class MessenerHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Config.MSG_FROM_CLIENT:
				try {
					Messenger messenger = msg.replyTo;
					Message replyMsg = Message.obtain(null, Config.MSG_FROM_SERVER);
					Bundle bundle = new Bundle();
					bundle.putInt("step", StepDcretor.CURRENT_SETP);
					bundle.putDouble("cal", calories);
					replyMsg.setData(bundle);
					messenger.send(replyMsg);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				break;
			default:
				super.handleMessage(msg);
			}
		}
	}

	/**
	 * 计算行走的距离
	 */
	private void countDistance() {
		if (StepDcretor.CURRENT_SETP % 2 == 0) {
			distance = (StepDcretor.CURRENT_SETP / 2) * 3 * step_length * 0.01;
		} else {
			distance = ((StepDcretor.CURRENT_SETP / 2) * 3 + 1) * step_length * 0.01;
		}
	}

	/**
	 * 计算并格式化doubles数值，保留两位有效数字
	 * 
	 * @param doubles
	 * @return 返回当前路程
	 */
	private String formatDouble(Double doubles) {
		DecimalFormat format = new DecimalFormat("####.##");
		String distanceStr = format.format(doubles);
		return distanceStr.equals(getString(R.string.zero)) ? getString(R.string.double_zero) : distanceStr;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		CURRENTDATE = getTodayDate();
		initBroadcastReceiver();
		new Thread(new Runnable() {
			public void run() {
				startStepDetector();
			}
		}).start();

		startTimeCount();
		initTodayData();

		 updateNotification("今日步数：" + StepDcretor.CURRENT_SETP + " 步");
	}

	private String getTodayDate() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(date);
	}

	private void initTodayData() {
		// 获取当天的数据，用于展示
		stepTableDao dao = DbCore.getDaoSession().getStepTableDao();
		// 查询当日的数据
		List<stepTable> stepList = dao.queryBuilder().where(Properties.Date.eq(CURRENTDATE)).list();
		if (stepList.size() == 0 || stepList.isEmpty()) {
			StepDcretor.CURRENT_SETP = 0;
		} else if (stepList.size() == 1) {
			StepDcretor.CURRENT_SETP = Integer.parseInt(stepList.get(0).getStep());

		}

	}

	private void initBroadcastReceiver() {
		final IntentFilter filter = new IntentFilter();
		// 屏幕灭屏广播
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		// 关机广播
		filter.addAction(Intent.ACTION_SHUTDOWN);
		// 屏幕亮屏广播
		filter.addAction(Intent.ACTION_SCREEN_ON);
		// 屏幕解锁广播
		filter.addAction(Intent.ACTION_USER_PRESENT);
		// 当长按电源键弹出“关机”对话或者锁屏时系统会发出这个广播
		// example：有时候会用到系统对话框，权限可能很高，会覆盖在锁屏界面或者“关机”对话框之上，
		// 所以监听这个广播，当收到时就隐藏自己的对话，如点击pad右下角部分弹出的对话框
		filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);

		mBatInfoReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(final Context context, final Intent intent) {
				String action = intent.getAction();

				if (Intent.ACTION_SCREEN_ON.equals(action)) {
				} else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
					// 改为60秒一存储
					duration = 60000;
				} else if (Intent.ACTION_USER_PRESENT.equals(action)) {
					save();
					// 改为30秒一存储
					duration = 30000;
				} else if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(intent.getAction())) {
					// 保存一次
					save();
				} else if (Intent.ACTION_SHUTDOWN.equals(intent.getAction())) {
					save();
				}
			}
		};
		registerReceiver(mBatInfoReceiver, filter);
	}

	private void startTimeCount() {
		time = new TimeCount(duration, 1000);
		time.start();
	}

	/**
	 * 更新通知
	 */
	private void updateNotification(String content) {
		builder = new NotificationCompat.Builder(this);
		builder.setPriority(Notification.PRIORITY_MIN);

		// Notification.Builder builder = new Notification.Builder(this);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
		builder.setContentIntent(contentIntent);
		builder.setSmallIcon(R.drawable.app_icon);
		builder.setTicker("施郎天娇");
		builder.setContentTitle("施郎天娇");
		// 设置不可清除
		builder.setOngoing(true);
		builder.setContentText(content);
		Notification notification = builder.build();

		startForeground(0, notification);

		nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//		nm.notify(R.string.app_name, notification);
	}

	@Override
	public IBinder onBind(Intent intent) {

		return messenger.getBinder();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}

	private void startStepDetector() {
		if (sensorManager != null && stepDetector != null) {
			sensorManager.unregisterListener(stepDetector);
			sensorManager = null;
			stepDetector = null;
		}
		getLock(this);
		// 获取传感器管理器的实例
		sensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
		// android4.4以后可以使用计步传感器
		int VERSION_CODES = android.os.Build.VERSION.SDK_INT;
		if (VERSION_CODES >= 19) {
			addCountStepListener();
		} else {
			addBasePedoListener();
		}
	}

	private void addCountStepListener() {
		Sensor detectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
		Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
		if (countSensor != null) {
			stepSensor = 0;
			sensorManager.registerListener(StepService.this, countSensor, SensorManager.SENSOR_DELAY_UI);
		} else if (detectorSensor != null) {
			stepSensor = 1;
			sensorManager.registerListener(StepService.this, detectorSensor, SensorManager.SENSOR_DELAY_UI);
		} else {
			addBasePedoListener();
		}
	}

	private void addBasePedoListener() {
		stepDetector = new StepDcretor(this);
		// 获得传感器的类型，这里获得的类型是加速度传感器
		// 此方法用来注册，只有注册过才会生效，参数：SensorEventListener的实例，Sensor的实例，更新速率
		Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		// sensorManager.unregisterListener(stepDetector);
		sensorManager.registerListener(stepDetector, sensor, SensorManager.SENSOR_DELAY_UI);
		stepDetector.setOnSensorChangeListener(new StepDcretor.OnSensorChangeListener() {

			@Override
			public void onChange() {
				 updateNotification("今日步数：" + StepDcretor.CURRENT_SETP + "步");
			}
		});
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// i++;
		if (stepSensor == 0) {
			StepDcretor.CURRENT_SETP = (int) event.values[0];
		} else if (stepSensor == 1) {
			StepDcretor.CURRENT_SETP++;
		}
		 updateNotification("今日步数：" + StepDcretor.CURRENT_SETP + " 步");
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	class TimeCount extends CountDownTimer {
		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			// 如果计时器正常结束，则开始计步
			time.cancel();
			save();
			startTimeCount();
		}

		@Override
		public void onTick(long millisUntilFinished) {

		}

	}

	private void save() {
		int tempStep = StepDcretor.CURRENT_SETP;
		stepTableDao dao = DbCore.getDaoSession().getStepTableDao();
		// 查询当日的数据
		List<stepTable> stepList = dao.queryBuilder().where(Properties.Date.eq(CURRENTDATE)).list();
		stepTable item;
		if (stepList.size() == 0 || stepList.isEmpty()) {
			item = new stepTable();
			item.setDate(CURRENTDATE);
			item.setStep(tempStep + "");
			countDistance();
			calories = weight * distance * 0.001;
			item.setKilometer(formatDouble(distance/1000));
			item.setCal(formatDouble(calories));
			dao.insert(item);
		} else if (stepList.size() == 1) {
			item = stepList.get(0);
			item.setDate(CURRENTDATE);
			item.setStep(tempStep + "");
			countDistance();
			calories = weight * distance * 0.001;
			item.setKilometer(formatDouble(distance/1000));
			item.setCal(formatDouble(calories));
			dao.insertOrReplace(item);
		}

	}

	@Override
	public void onDestroy() {
		// 取消前台进程
		stopForeground(true);
		unregisterReceiver(mBatInfoReceiver);
		Intent intent = new Intent(this, StepService.class);
		startService(intent);
		super.onDestroy();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}

	// private void unlock(){
	// setLockPatternEnabled(android.provider.Settings.Secure.LOCK_PATTERN_ENABLED,false);
	// }
	//
	// private void setLockPatternEnabled(String systemSettingKey, boolean
	// enabled) {
	// //推荐使用
	// android.provider.Settings.Secure.putInt(getContentResolver(),
	// systemSettingKey,enabled ? 1 : 0);
	// }

	synchronized private PowerManager.WakeLock getLock(Context context) {
		if (mWakeLock != null) {
			if (mWakeLock.isHeld())
				mWakeLock.release();
			mWakeLock = null;
		}

		if (mWakeLock == null) {
			PowerManager mgr = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
			mWakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, StepService.class.getName());
			mWakeLock.setReferenceCounted(true);
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(System.currentTimeMillis());
			int hour = c.get(Calendar.HOUR_OF_DAY);
			if (hour >= 23 || hour <= 6) {
				mWakeLock.acquire(5000);
			} else {
				mWakeLock.acquire(300000);
			}
		}

		return (mWakeLock);
	}
}
