package com.sltj.medical;

import java.util.Calendar;
import java.util.List;

import com.db.chart.Tools;
import com.db.chart.listener.OnEntryClickListener;
import com.db.chart.model.LineSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.LineChartView;
import com.db.chart.view.animation.Animation;
import com.sltj.medical.base.BaseActivity;
import com.sltj.medical.dao.DbCore;
import com.sltj.medical.dao.stepTable;
import com.sltj.medical.dao.stepTableDao;
import com.sltj.medical.dao.stepTableDao.Properties;
import com.sltj.medical.util.MTools;
import com.sltj.medical.util.ToastUtils;
import com.sltj.medical.wedgit.CircleBar;

import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

/**
 * 运动祥情页面
 * 
 */
public class StepDetailActivity extends BaseActivity
		implements OnCheckedChangeListener, OnClickListener, OnEntryClickListener {
	private RadioGroup mRadioGroup;
	private FrameLayout mFramLayout;
	private View vStep, vSleep, vShare; // 切换时加载的三个布局
	private LayoutInflater mInflayter;

	private LinearLayout btnback;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_step_detail);
		initialize();
	}

	@Override
	public void initView() {
		mInflayter = LayoutInflater.from(this);
		mRadioGroup = (RadioGroup) findViewById(R.id.rg_step_detail);
		mFramLayout = (FrameLayout) findViewById(R.id.fl);
		btnback = (LinearLayout) findViewById(R.id.ll_back);

	}

	@Override
	public void setupData() {
		addStepView();
		mRadioGroup.check(R.id.rb_step);
		mRadioGroup.setOnCheckedChangeListener(this);
		btnback.setOnClickListener(this);

	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.rb_step:
			mFramLayout.removeAllViews();
			addStepView();
			break;
		case R.id.rb_sleep:
			mFramLayout.removeAllViews();
			initWillianmChart();
			break;
		case R.id.rb_share:

			break;

		default:
			break;
		}

	}

	/**
	 * 展示步行数据页面
	 */
	private void addStepView() {
		vStep = mInflayter.inflate(R.layout.item_step, null);
		TextView tvDate = (TextView) vStep.findViewById(R.id.tv_date);
		TextView tvTime = (TextView) vStep.findViewById(R.id.tv_time);
		TextView tvDictance = (TextView) vStep.findViewById(R.id.tv_itemstep_distance);
		TextView tvCal = (TextView) vStep.findViewById(R.id.tv_itemstep_cal);
		TextView tvTodayStep = (TextView) vStep.findViewById(R.id.today_step);
		int todayStep = getIntent().getIntExtra("todayStep", 10);
		tvTodayStep.setText(todayStep + "");
		CircleBar stepProgress = (CircleBar) vStep.findViewById(R.id.step_circlebar);
		// 设置当前时间
		tvDate.setText(MTools.getCurrentDate("yyyy-MM-dd"));
		Calendar c = Calendar.getInstance();
		int week = c.get(Calendar.DAY_OF_WEEK) - 1;
		if (week < 0) {
			week = 0;
		}
		String[] weekOfDays = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
		tvTime.setText(weekOfDays[week]);
		stepTableDao dao = DbCore.getDaoSession().getStepTableDao();
		List<stepTable> list = dao.queryBuilder().where(Properties.Date.eq(MTools.getCurrentDate("yyyy-MM-dd"))).list();
		if (list.size() > 0) {
			// Integer.parseInt(list.get(0).getStep()
			stepProgress.update(Integer.parseInt(list.get(0).getStep()), 2000);
			// stepProgress.update(5306, 2000);
			tvDictance.setText(list.get(0).getKilometer() + "公里");
			tvCal.setText(list.get(0).getCal() + "卡");
		}
		mFramLayout.addView(vStep);
	}

	/**
	 * 查看近一周步行拆线图
	 */
	private final static String[] mLabels = { "周一", "周二", "周三", "周四", "周五", "周六", "周日" };
	private final float[][] mValues = { { 0f, 2f, 1.4f, 4.f, 3.5f, 4.3f, 2f }, { 1.5f, 2.5f, 1.5f, 5f, 4f, 5f, 4.3f } };
	private LineSet dataset;

	private void initWillianmChart() {
		vSleep = mInflayter.inflate(R.layout.stepandsleep, null);
		LineChartView willLineChartView = (LineChartView) vSleep.findViewById(R.id.will_linechart);
		dataset = new LineSet(mLabels, mValues[0]);
		// 直线颜色
		dataset.setColor(Color.parseColor("#58C674")).setDotsRadius(Tools.fromDpToPx(6))
				.setDotsColor(Color.parseColor("#FFE4E1")).setSmooth(true).setDotsStrokeColor(Color.WHITE)
				.setDotsStrokeThickness(2F);
		willLineChartView.addData(dataset);
		willLineChartView.setBorderSpacing(1)
				// x,y轴上文字显示的位置,默认是显示为outside;
				.setXLabels(AxisController.LabelPosition.OUTSIDE).setYLabels(AxisController.LabelPosition.NONE)
				// 是否显示x,y轴
				.setXAxis(false).setYAxis(false).setBorderSpacing(Tools.fromDpToPx(5));
		Animation anim = new Animation();
		anim.setAlpha(1000);

		// 拆线图上小圆点的点击事件
		willLineChartView.setOnEntryClickListener(this);
		willLineChartView.show(anim);
		mFramLayout.addView(vSleep);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_back:
			this.finish();
			break;

		default:
			break;
		}
	}

	@Override
	public void onClick(int setIndex, int entryIndex, Rect rect) {
		ToastUtils.show(this, setIndex + "--" + entryIndex + "--", 0);

	}

}
