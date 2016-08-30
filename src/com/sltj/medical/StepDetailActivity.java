package com.sltj.medical;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.db.chart.Tools;
import com.db.chart.listener.OnEntryClickListener;
import com.db.chart.model.LineSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.LineChartView;
import com.db.chart.view.animation.Animation;
import com.sltj.medical.adapter.StepDetailGridView;
import com.sltj.medical.base.BaseActivity;
import com.sltj.medical.dao.DbCore;
import com.sltj.medical.dao.stepTable;
import com.sltj.medical.dao.stepTableDao;
import com.sltj.medical.dao.stepTableDao.Properties;
import com.sltj.medical.util.MTools;
import com.sltj.medical.util.ToastUtils;
import com.sltj.medical.wedgit.CircleBar;
import com.sltj.medical.wedgit.LineGridView;

import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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
		int todayStep = getIntent().getIntExtra("todayStep", 0);
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
			stepProgress.update(Integer.parseInt(list.get(0).getStep()), 500);
			tvDictance.setText(list.get(0).getKilometer() + "公里");
			tvCal.setText(list.get(0).getCal() + "卡");
		}
		mFramLayout.addView(vStep);
	}

	/**
	 * 查看近一周步行拆线图
	 */
	private final static String[] mLabels = { "周一", "周二", "周三", "周四", "周五", "周六", "周日" };
	private final float[] mValues = { 0f, 0f, 0f, 0f, 0f, 0f, 0f };
	private LineSet dataset; // 折线图圆点设置类
	private String[] titleValue = { "总步数", "总距离", "活动时间", "活动消耗" };// 显示某一天的数据
	private String[] Value = {}; // 对应着每一个item的数据数组。如：步数：cal,kilometer
	private LineGridView gv; // 展示数据的GridView;
	private TextView tvCurrentDate;
	private List<Map<String, String>> lst = new ArrayList<Map<String, String>>();

	/**
	 * 展示运动折线图
	 */
	private void initWillianmChart() {
		lst.clear();
		vSleep = mInflayter.inflate(R.layout.stepandsleep, null);
		LineChartView willLineChartView = (LineChartView) vSleep.findViewById(R.id.will_linechart);
		gv = (LineGridView) vSleep.findViewById(R.id.stepdetail_gv);
		tvCurrentDate = (TextView) vSleep.findViewById(R.id.stepdetail_tv_date);
		stepTableDao dao = DbCore.getDaoSession().getStepTableDao();
		List<stepTable> talbe = dao.loadAll();
		dataset = new LineSet(mLabels, mValues);
		// 添加折线图的每一个点
		for (int i = 0; i < talbe.size(); i++) {
			float step = Float.parseFloat(talbe.get(i).getStep());
			String week = talbe.get(i).getWeek();
			for (int j = 0; j < mLabels.length; j++) {
				if (mLabels[j].equals(week)) {
					mValues[j] = step;
					dataset.updateValues(mValues);
				}
			}
		}
		// 设置折线图的样式
		dataset.setColor(Color.parseColor("#ff0000")).setDotsRadius(Tools.fromDpToPx(6))
				.setDotsColor(Color.parseColor("#FFE4E1")).setSmooth(false).setDotsStrokeColor(Color.WHITE)
				.setDotsStrokeThickness(2F);
		willLineChartView.addData(dataset);
		willLineChartView.setTopSpacing(10).setStep(10000).setAxisLabelsSpacing(20)
				// x,y轴上文字显示的位置,默认是显示为outside;
				.setXLabels(AxisController.LabelPosition.OUTSIDE).setYLabels(AxisController.LabelPosition.OUTSIDE)
				// 是否显示x,y轴
				.setXAxis(false).setYAxis(false).setBorderSpacing(Tools.fromDpToPx(5));
		// Tooltip t = new Tooltip(this);
		Animation anim = new Animation();
		anim.setAlpha(1000);

		// 拆线图上小圆点的点击事件
		willLineChartView.setOnEntryClickListener(this);
		willLineChartView.show(anim);
		// 默认展示今日祥细数据
		setTodayData();
		//
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
		lst.clear();
		String strDay = "";
		stepTableDao dao = DbCore.getDaoSession().getStepTableDao();
		switch (entryIndex) {
		case 0:
			strDay = "周一";
			break;
		case 1:
			strDay = "周二";
			break;
		case 2:
			strDay = "周三";
			break;
		case 3:
			strDay = "周四";
			break;
		case 4:
			strDay = ("周五");
			break;
		case 5:
			strDay = "周六";
			break;
		case 6:
			strDay = "周日";
			break;
		}
		// 找到数据库中点击的数据
		List<stepTable> tableLst = dao.queryBuilder().where(Properties.Week.eq(strDay)).list();
		if (!tableLst.isEmpty()) {
			String step = tableLst.get(0).getStep();
			String cal = tableLst.get(0).getCal();
			String kilometer = tableLst.get(0).getKilometer();
			Value = new String[] { step + " 步", cal + " 卡路里", "23" + " 分钟", kilometer + " 大卡" };

			for (int i = 0; i < titleValue.length; i++) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("title", titleValue[i]);
				map.put("value", Value[i]);
				lst.add(map);
			}
			StepDetailGridView mAdapter = new StepDetailGridView(StepDetailActivity.this, lst);
			gv.setAdapter(mAdapter);
			mAdapter.notifyDataSetChanged();

		}

	}

	/**
	 * 展示当日数据
	 */
	private void setTodayData() {
		stepTableDao dao = DbCore.getDaoSession().getStepTableDao();
		// 获取当日日期从数据库获取数据
		String currentDate = MTools.getCurrentDate("yyyy-MM-dd");
		tvCurrentDate.setText(currentDate);
		List<stepTable> tableLst = dao.queryBuilder().where(Properties.Date.eq(currentDate)).list();
		// 若有数据则显示出来
		if (!tableLst.isEmpty()) {
			String step = tableLst.get(0).getStep();
			String cal = tableLst.get(0).getCal();
			String kilometer = tableLst.get(0).getKilometer();
			Value = new String[] { step + " 步", cal + " 卡路里", "63" + " 分钟", kilometer + " 大卡" };

			for (int i = 0; i < titleValue.length; i++) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("title", titleValue[i]);
				map.put("value", Value[i]);
				lst.add(map);
			}
			StepDetailGridView mAdapter = new StepDetailGridView(StepDetailActivity.this, lst);
			gv.setAdapter(mAdapter);
		}

	}
}
