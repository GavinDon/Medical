package com.sltj.medical;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sltj.medical.R.integer;
import com.sltj.medical.adapter.AddMoodAdapter.MoodRecord;
import com.sltj.medical.adapter.HeathManagerAdapter;
import com.sltj.medical.base.MyApplication;
import com.sltj.medical.bean.NetParam;
import com.sltj.medical.config.Define;
import com.sltj.medical.dataUtil.HandleMsgDistribute;
import com.sltj.medical.dataUtil.HandleNetSendMsg;
import com.sltj.medical.dataUtil.protobuf.PublicmsgPro.Net_ServerInfo_PRO;
import com.sltj.medical.publicMsg.MsgInncDef;
import com.sltj.medical.publicMsg.MsgInncDef.AuthNetCommonReq;
import com.sltj.medical.publicMsg.MsgReceiveDef;
import com.sltj.medical.publicMsg.MsgReceiveDef.NetBestServerResp;
import com.sltj.medical.service.NetWorkStateService;
import com.sltj.medical.socketutil.AuthSocketConn;
import com.sltj.medical.socketutil.HouseSocketConn;
import com.sltj.medical.socketutil.PushData;
import com.sltj.medical.util.FragmentController;
import com.sltj.medical.util.LogUtils;
import com.sltj.medical.util.ScreenUtils;
import com.sltj.medical.util.ToastUtils;
import com.sltj.medical.wedgit.CircleImageView;

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
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements OnCheckedChangeListener,OnClickListener,OnItemClickListener {
	private RadioGroup mRadioGroup;
	private long exitTime = 0;

	private FragmentController controller;
	private TextView mtitle;//标题
	private CircleImageView ivUser;//用户中心头像

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initViews();
		controller = FragmentController.getInstance(this, R.id.fl_content);
		controller.showFragment(0);
		IntentFilter filter = new IntentFilter();
		filter.addAction(Define.BROAD_CAST_RECV_DATA_COMPLETE);
		this.registerReceiver(mReceiver, filter);
		bestServerReq();
	}

	/**
	 * 初始化页面控件
	 */
	private void initViews() {
		initDrawerLayout();
//		initActionBar();
		mtitle=(TextView) findViewById(R.id.tv_fragment_title);
		ivUser=(CircleImageView) findViewById(R.id.slide_user);
		ivUser.setOnClickListener(this);
		mRadioGroup = (RadioGroup) findViewById(R.id.rb_main_page);
		mRadioGroup.setOnCheckedChangeListener(this);

	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.radio_homepage:
			controller.showFragment(0);
			mtitle.setText("天娇会");
			break;
		case R.id.radio_news:
			controller.showFragment(1);
			mtitle.setText("资讯");
			break;
		case R.id.radio_persion:
			controller.showFragment(2);
			mtitle.setText("私人医生");
			break;
		case R.id.radio_product:
			controller.showFragment(3);
			mtitle.setText("产品");
			break;
		case R.id.radio_healthmanager:
			controller.showFragment(4);
			mtitle.setText("健康管理");
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 销毁Fragment
		FragmentController.onDestroy();
		unregisterReceiver(mReceiver);
		stopService(new Intent(getApplicationContext(),NetWorkStateService.class));
	}

	@Override
	public void onBackPressed() {
		if (mRadioGroup.getCheckedRadioButtonId() == R.id.radio_homepage) {
			if (System.currentTimeMillis() - exitTime > 2000) {
				Toast.makeText(this, "再按一次退出", 0).show();
				exitTime = System.currentTimeMillis();
			} else {
				moveTaskToBack(true);
				this.finish();
				System.exit(0);
			}
		} else {
			mRadioGroup.check(R.id.radio_homepage);
			controller.showFragment(0);
		}
	}
    //最优服务器seq
	private int seqBestServer;

	private void bestServerReq() {
		seqBestServer = MyApplication.SequenceNo++;
		AuthNetCommonReq req = new MsgInncDef.AuthNetCommonReq();
		req.iUserid = MyApplication.userId;
		byte[] connData = HandleNetSendMsg.HandleBestServerPro(req, seqBestServer);
		AuthSocketConn.pushtoList(connData);
	}

	/*
	 * 最优服务器响应
	 */
	private void bestServerResp(Long recvTime) {
		MsgReceiveDef.NetBestServerResp resp = (NetBestServerResp) HandleMsgDistribute.getInstance()
				.queryCompleteMsg(recvTime);

		if (resp.serverList.size() < 0) {
			return;
		} else if (resp.serverList.size() > 0) {
			List<Net_ServerInfo_PRO> list = resp.serverList;
			String ip = list.get(0).getSzIp();
			int port = list.get(0).getIPort();
			PushData.setHouseIp(ip);
			PushData.setHousePort(port);
			if(MyApplication.authSocketConn!=null&&!MyApplication.authSocketConn.isClose()){
				MyApplication.authSocketConn.closeAuthSocket();
				//连接消息处理服务器
				new HouseSocketConn(ip, port);
				LogUtils.i("处理消息服务器登录成功============");
				
			}
		}

	}

	BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (Define.BROAD_CAST_RECV_DATA_COMPLETE.equals(intent.getAction())) {
				int iSequence = intent.getIntExtra(Define.BROAD_SEQUENCE, -1);
				long recvTime = intent.getLongExtra(Define.BROAD_MSG_RECVTIME, -1);

				if (seqBestServer == iSequence) {
					bestServerResp(recvTime);
				}
			}

		}
	};
  /**
   * DrawerLayout
   */
	private DrawerLayout drawerLayout;
	private ActionBar actionBar;

	@SuppressLint("NewApi")
	private void initActionBar() {
		actionBar = getActionBar();
		LogUtils.i(actionBar+"");
		actionBar.show();
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeAsUpIndicator(R.drawable.biz_tie_user_avater_default_common);
		Drawable colorDrawable = new ColorDrawable(android.R.color.transparent);
		actionBar.setIcon(colorDrawable);

		actionBar.setDisplayShowCustomEnabled(true);
		TextView tvTitle = new TextView(this);
		tvTitle.setText("个人中心");
		tvTitle.setTextColor(Color.WHITE);
		tvTitle.setTextSize(18);
		tvTitle.setGravity(Gravity.CENTER);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		tvTitle.setLayoutParams(params);
		actionBar.setCustomView(tvTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	private void initDrawerLayout() {
		drawerLayout = (DrawerLayout) this.findViewById(R.id.drawer_main);
		initUserData();
		drawerLayout.setScrimColor(Color.TRANSPARENT);
		

	}

	private void toggleLeftSliding() {
		if (drawerLayout.isDrawerOpen(Gravity.START)) {
			drawerLayout.closeDrawer(Gravity.START);
		} else {
			drawerLayout.openDrawer(Gravity.START);
		}
	}

	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.slide_user:
			toggleLeftSliding();
			break;

		default:
			break;
		}
	}

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

	/*
	 * 个人中心
	 */
	private ListView mListView;
	private List<Map<String, Object>> lst = new ArrayList<Map<String, Object>>();
	private HeathManagerAdapter mAdapter;
	private void initUserData() {
		mListView = (ListView) findViewById(R.id.lv_usercenter);
		String title[] = new String[] { "我的收藏", "设置帮助", "意见反鐀" };
		int[] img = new int[] { R.id.useLogo, R.id.useLogo, R.id.useLogo, R.id.useLogo, R.id.useLogo };
		for (int i = 0; i < title.length; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("img", img[i]);
			map.put("title", title[i]);
			lst.add(map);
		}
		mAdapter = new HeathManagerAdapter(this, lst);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		ToastUtils.show(this, position+"", 0);
	}

}
