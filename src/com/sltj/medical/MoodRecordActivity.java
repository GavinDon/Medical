package com.sltj.medical;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sltj.medical.adapter.MoodRecordAdapter;
import com.sltj.medical.adapter.RecordAdapter;
import com.sltj.medical.base.BaseActivity;
import com.sltj.medical.base.MyApplication;
import com.sltj.medical.config.Define;
import com.sltj.medical.dataUtil.HandleMsgDistribute;
import com.sltj.medical.dataUtil.HandleNetSendMsg;
import com.sltj.medical.publicMsg.MsgInncDef;
import com.sltj.medical.publicMsg.MsgReceiveDef.MoodRecordResp;
import com.sltj.medical.socketutil.HouseSocketConn;
import com.sltj.medical.util.LogUtils;
import com.sltj.medical.util.MTools;
import com.sltj.medical.wedgit.LoadingDialog;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 心情记录页面
 */
public class MoodRecordActivity extends BaseActivity implements OnClickListener {
	private ImageButton imgbBtn;
	private ListView mListView;
	private TextView tvEvarage;
	private List<Map<String, String>> lst = new ArrayList<Map<String, String>>();
	private MoodRecordAdapter mAdapter;
	private LoadingDialog mDialog;
	// 心情记录seq号
	private int seqMoodRecord;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mood_record);
		mDialog = new LoadingDialog(this);
		// 注册接收消息的广播
		IntentFilter filter = new IntentFilter();
		filter.addAction(Define.BROAD_CAST_RECV_DATA_COMPLETE);
		this.registerReceiver(mReceiver, filter);
		initialize();
		initBar();
//		initActionBar();
//		initDrawerLayout();
	}

	@Override
	public void initView() {
		imgbBtn = (ImageButton) findViewById(R.id.btn);
		tvEvarage = (TextView) findViewById(R.id.tv_mood_evarage);
		mListView = (ListView) findViewById(R.id.lv_mood_record);
	}

	@Override
	public void setupData() {
//		MoodProReq();
		imgbBtn.setOnClickListener(this);
	}
	@Override
	protected void onStart() {
		super.onStart();
		lst.clear();
		MoodProReq();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn:
			Intent mItent = new Intent(this, AddMoodActivity.class);
			startActivity(mItent);
			break;

		default:
			break;
		}
	}

	/*
	 * 心情记录请求
	 */
	private void MoodProReq() {
		mDialog.show();
		MsgInncDef.IMoodRecordReq recordReq = new MsgInncDef.IMoodRecordReq();
		recordReq.iUserId = MyApplication.userId;
		recordReq.iRecoderNum = 10;
		recordReq.iBeforRecoderid = 1000;
		recordReq.szBeforTime = MTools.getCurrentDate("yyyy-MM-dd HH:mm:ss");
		seqMoodRecord = MyApplication.SequenceNo++;
		byte[] btData = HandleNetSendMsg.HandleMoodRecordPro(recordReq, seqMoodRecord);
		HouseSocketConn.pushtoList(btData);
	}

	private void MoodProResp(long recvTime) {
		mDialog.dismiss();

		MoodRecordResp resp = (MoodRecordResp) HandleMsgDistribute.getInstance().queryCompleteMsg(recvTime);
		tvEvarage.setText("您今日的心情总得分:"+resp.iAverage);
		if (!resp.info.isEmpty()) {
			for (int i = 0; i < resp.info.size(); i++) {
				Map<String, String> params = new HashMap<String, String>();
				params.put("time", resp.info.get(i).getSzCreateTime());
//				int grade=resp.info.get(i).getGrade();
				params.put("grade", resp.info.get(i).getGrade()+"");
				lst.add(params);
			}
			mAdapter=new MoodRecordAdapter(this, lst);
			mListView.setAdapter(mAdapter);
			
		}

	}

	// 广播
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (Define.BROAD_CAST_RECV_DATA_COMPLETE.equals(intent.getAction())) {
				int iSequence = intent.getIntExtra(Define.BROAD_SEQUENCE, -1);
				long recvTime = intent.getLongExtra(Define.BROAD_MSG_RECVTIME, -1);
				if (iSequence == seqMoodRecord) {
					MoodProResp(recvTime);
				}

			}
		}

	};

	/*
	 * 初始化标题栏
	 */
	private void initBar() {
		LinearLayout llback = (LinearLayout) findViewById(R.id.ll_back);
		TextView iv = (TextView) findViewById(R.id.tv_title);
		iv.setText("心情记录");
		llback.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MoodRecordActivity.this.finish();
			}
		});

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.unregisterReceiver(mReceiver);
	}
	
//	private DrawerLayout drawerLayout;
//	private ActionBarDrawerToggle toggle;
//	private View right_sliding;
//	private ActionBar actionBar;
//
//	@SuppressLint("NewApi")
//	private void initActionBar() {
//		actionBar = super.getActionBar();
//		LogUtils.i(actionBar+"");
//		actionBar.show();
//		actionBar.setDisplayShowHomeEnabled(true);
//		actionBar.setDisplayShowTitleEnabled(false);
//		actionBar.setDisplayHomeAsUpEnabled(true);
//		actionBar.setHomeAsUpIndicator(R.drawable.biz_tie_user_avater_default_common);
//		Drawable colorDrawable = new ColorDrawable(android.R.color.transparent);
//		actionBar.setIcon(colorDrawable);
//
//		actionBar.setDisplayShowCustomEnabled(true);
//		TextView tvTitle = new TextView(this);
//		tvTitle.setText("个人中心");
//		tvTitle.setTextColor(Color.WHITE);
//		tvTitle.setTextSize(18);
//		tvTitle.setGravity(Gravity.CENTER);
//		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
//		tvTitle.setLayoutParams(params);
//		actionBar.setCustomView(tvTitle);
//	}
//
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		getMenuInflater().inflate(R.menu.menu, menu);
//		return true;
//	}
//
//	private void initDrawerLayout() {
//		drawerLayout = (DrawerLayout) super.findViewById(R.id.drawer);
//		drawerLayout.setScrimColor(Color.TRANSPARENT);
//		toggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.user_icon,
//				R.string.hello_world, R.string.hello_world) {
//
//			@Override
//			public void onDrawerClosed(View drawerView) {
//				super.onDrawerClosed(drawerView);
//			}
//
//			@Override
//			public void onDrawerOpened(View drawerView) {
//				super.onDrawerOpened(drawerView);
//			}
//
//		};
//		drawerLayout.setDrawerListener(toggle);
//		// drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
//		// drawerLayout.openDrawer(right_sliding);
//
//	}
//
//	private void toggleLeftSliding() {
//		if (drawerLayout.isDrawerOpen(Gravity.START)) {
//			drawerLayout.closeDrawer(Gravity.START);
//		} else {
//			drawerLayout.openDrawer(Gravity.START);
//		}
//	}
//
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		switch (item.getItemId()) {
//		case android.R.id.home:
//			toggleLeftSliding();
//			break;
//		}
//
//		return super.onOptionsItemSelected(item);
//	}
//
//	@Override
//	public boolean onMenuOpened(int featureId, Menu menu) {
//		if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
//			if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
//				try {
//					Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
//					m.setAccessible(true);
//					m.invoke(menu, true);
//				} catch (Exception e) {
//				}
//			}
//		}
//		return super.onMenuOpened(featureId, menu);
//	}
}
