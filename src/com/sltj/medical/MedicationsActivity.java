package com.sltj.medical;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fiter.pulltorefresh.PullToRefreshBase;
import com.fiter.pulltorefresh.PullToRefreshBase.Mode;
import com.fiter.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.fiter.pulltorefresh.PullToRefreshListView;
import com.sltj.medical.adapter.RecordAdapter;
import com.sltj.medical.base.BaseActivity;
import com.sltj.medical.base.MyApplication;
import com.sltj.medical.config.Config;
import com.sltj.medical.config.Define;
import com.sltj.medical.dataUtil.HandleMsgDistribute;
import com.sltj.medical.dataUtil.HandleNetSendMsg;
import com.sltj.medical.dataUtil.protobuf.EnumPro.ePAGE_TYPE_PRO;
import com.sltj.medical.dataUtil.protobuf.PublicmsgPro.Net_Yongyao_RecoderInfo_PRO;
import com.sltj.medical.publicMsg.MsgInncDef;
import com.sltj.medical.publicMsg.MsgInncDef.OnrefreshFlag;
import com.sltj.medical.publicMsg.MsgReceiveDef;
import com.sltj.medical.publicMsg.MsgReceiveDef.MedicationsResp;
import com.sltj.medical.socketutil.HouseSocketConn;
import com.sltj.medical.util.MTools;
import com.sltj.medical.util.ToastUtils;
import com.sltj.medical.wedgit.LoadingDialog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
/**
 * 用药记录页面
 * 
 * @author linan
 */
public class MedicationsActivity extends BaseActivity implements OnItemClickListener{
	private int seqMedicatonsRecord; // 用药记录seq;
	private PullToRefreshListView mPullListView;
	// 页面数据List
	private List<Map<String, String>> lst = new ArrayList<Map<String, String>>();
	private RecordAdapter mAdapter;
	private LoadingDialog mDialog;
	Comparator<Long> reverseOrder = Collections.<Long> reverseOrder();
	List<Long> timeList = new ArrayList<Long>();// 资讯创建时间列表
	private OnrefreshFlag freshFlag = OnrefreshFlag.REFRESH_PullUp; // 默认为上拉;

	/*
	 * handler更新加载数据结果
	 */
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case Config.LOAD_DATA_SUCCESS:
				mAdapter.notifyDataSetChanged();
				mPullListView.onRefreshComplete();
				break;
			case Config.LOAD_DATA_OVERTIME:
				mPullListView.onRefreshComplete();

			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_medications);
		// 注册接收消息的广播
		IntentFilter filter = new IntentFilter();
		filter.addAction(Define.BROAD_CAST_RECV_DATA_COMPLETE);
		this.registerReceiver(mReciver, filter);
		mDialog = new LoadingDialog(this);
		initialize();
		initBar();
	}

	@Override
	public void initView() {
		initPullView();
	}

	@Override
	public void setupData() {
		medicationsRecordReq();
		mPullListView.setOnItemClickListener(this);
	}
	

	/*
	 * ListView上拉下拉
	 *
	 */
	@SuppressWarnings("unchecked")
	public void initPullView() {
		mPullListView = (PullToRefreshListView) findViewById(R.id.lv_medications_record);
		mPullListView.setMode(Mode.PULL_FROM_END);
		mPullListView.setOnRefreshListener(new OnRefreshListener2() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				freshFlag = OnrefreshFlag.REFRESH_DOWN;
				String label = DateUtils.formatDateTime(MedicationsActivity.this.getApplicationContext(),
						System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel("上次更新时间：" + label);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {
				freshFlag = OnrefreshFlag.REFRESH_PullUp;
				String label = DateUtils.formatDateTime(MedicationsActivity.this.getApplicationContext(),
						System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel("上次更新时间：" + label);
				medicationsRecordReq();

			}
		});
	}

	/*
	 * 用药记录请求
	 */
	private void medicationsRecordReq() {
		mDialog.show();
		handler.sendEmptyMessageDelayed(Config.LOAD_DATA_OVERTIME, 4000);	
		MsgInncDef.IphsicalRecordReq req = new MsgInncDef.IphsicalRecordReq();
		req.iUserId = MyApplication.userId;
		req.iRecoderNum = 10;
		req.iBeforRecoderid = 10000;
		req.ePageType = ePAGE_TYPE_PRO.EN_PAGE_OLD_PRO;//支持上拉获取更早之前的记录
		// 判断是什么操作(上拉下拉)
		if (freshFlag == OnrefreshFlag.REFRESH_PullUp) {
			if (freshFlag == OnrefreshFlag.REFRESH_PullUp) {
				// 根据状态选则timelist中的时间去请求服务器
				if (!timeList.isEmpty()) {
					req.szBeforTime = MTools.getTimeYMDHMS(timeList.get(timeList.size() - 1));
				} else {
					req.szBeforTime = MTools.getCurrentDate("yyyy-MM-dd HH:mm:ss");
				}
			}
		}
		freshFlag = null;
		seqMedicatonsRecord = MyApplication.SequenceNo++;
		byte[] btData = HandleNetSendMsg.HandleMedicationsRecordPro(req, seqMedicatonsRecord);
		HouseSocketConn.pushtoList(btData);

	}

	/*
	 * 用药记录响应
	 */
	private void medicationsRecordResp(Long recvTime) {
		mDialog.dismiss();
		MsgReceiveDef.MedicationsResp resp = (MedicationsResp) HandleMsgDistribute.getInstance().queryCompleteMsg(recvTime);
		List<Net_Yongyao_RecoderInfo_PRO> list = resp.info;
		if (!list.isEmpty()) {
			for (int i = 0; i < list.size(); i++) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("date", list.get(i).getSzCreateTime());
				map.put("title", list.get(i).getSzContent());
				map.put("treatDoctor", "-1");// 显示眼睛图标
				map.put("index", String.valueOf(list.get(i).getIRecordIndex()));
				lst.add(map);
				timeList.add(MTools.datetimeToTimeMillis(list.get(i).getSzCreateTime()));
			}
			Collections.sort(timeList, reverseOrder);
			mAdapter = new RecordAdapter(this, lst);
			mPullListView.setAdapter(mAdapter);
			handler.sendEmptyMessage(Config.LOAD_DATA_SUCCESS);
		} else {
			ToastUtils.show(this, "没有更多啦", 0);
		}
	}

	/*
	 * 广播接收数据
	 */
	private BroadcastReceiver mReciver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (Define.BROAD_CAST_RECV_DATA_COMPLETE.equals(intent.getAction())) {
				int iSequence = intent.getIntExtra(Define.BROAD_SEQUENCE, -1);
				long recvTime = intent.getLongExtra(Define.BROAD_MSG_RECVTIME, -1);
				if(seqMedicatonsRecord==iSequence){
					medicationsRecordResp(recvTime);
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
		iv.setText("用药记录");
		llback.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MedicationsActivity.this.finish();
			}
		});

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Map<String, String> map = (Map<String, String>) parent.getAdapter().getItem(position);
		Intent mIntent = new Intent(this, TreatDetailActivity.class);
		mIntent.putExtra("treatDetail", (Serializable) map);
		mIntent.putExtra("flag", 1);
		startActivity(mIntent);
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.unregisterReceiver(mReciver);
	}

}
