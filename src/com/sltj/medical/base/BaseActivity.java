package com.sltj.medical.base;

import java.util.ArrayList;
import java.util.List;

import com.sltj.medical.R;
import com.sltj.medical.util.LogUtils;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public abstract class BaseActivity extends Activity {
	public static final String TAG = BaseActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ActivityStackControlUtil.add(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);// 隐藏软键盘

	}

	public void initialize() {
		initView();
		setupData();
		overridePendingTransition();
	}

	@Override
	protected void onStart() {
		LogUtils.d(this.getClass().getSimpleName() + " onStart() invoked!!");
		super.onStart();
	}

	@Override
	protected void onRestart() {
		LogUtils.d(this.getClass().getSimpleName() + " onRestart() invoked!!");
		super.onRestart();
	}

	@Override
	protected void onResume() {
		LogUtils.d(this.getClass().getSimpleName() + " onResume() invoked!!");
		super.onResume();
	}

	@Override
	protected void onPause() {
		LogUtils.d(this.getClass().getSimpleName() + " onPause() invoked!!");
		super.onPause();
		try {
			// MobclickAgent.onPause(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onStop() {
		LogUtils.d(this.getClass().getSimpleName() + " onStop() invoked!!");
		System.gc();
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		LogUtils.d(this.getClass().getSimpleName() + " onDestroy() invoked!!");
		super.onDestroy();
		System.gc();
	}

	public  void overridePendingTransition() {
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

	}

	public abstract void initView();

	public abstract void setupData();

	public static class ActivityStackControlUtil {
		private static List<Activity> activityList = new ArrayList<Activity>();

		public static int getCounter() {
			return activityList.size();
		}

		public static void remove(Activity activity) {
			activityList.remove(activity);
		}

		public static void add(Activity activity) {
			activityList.add(activity);
		}

		public static void finishProgram() {
			for (Activity activity : activityList) {
				if (activity != null) {
					activity.finish();
				}
			}
			System.gc();
			// android.os.Process.killProcess(android.os.Process.myPid());
		}

		public static void finishAllActivityWithout(Activity withoutActivity) {

			for (int index = activityList.size() - 1; index >= 0; index--) {
				if (withoutActivity != activityList.get(index)) {
					activityList.get(index).finish();
					activityList.remove(index);
				}
			}
		}
	}

}
