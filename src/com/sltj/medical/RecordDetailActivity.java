package com.sltj.medical;

import java.util.Map;

import com.sltj.medical.base.BaseActivity;
import com.sltj.medical.base.MyApplication;
import com.sltj.medical.config.Define;
import com.sltj.medical.dataUtil.HandleMsgDistribute;
import com.sltj.medical.dataUtil.HandleNetSendMsg;
import com.sltj.medical.dataUtil.protobuf.PublicmsgPro.Net_Tijian_RecoderInfo_PRO;
import com.sltj.medical.publicMsg.MsgInncDef;
import com.sltj.medical.publicMsg.MsgReceiveDef;
import com.sltj.medical.publicMsg.MsgReceiveDef.PhysicalDetailResp;
import com.sltj.medical.socketutil.HouseSocketConn;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RecordDetailActivity extends BaseActivity {
	private Map<String, String> map;
	private TextView tvdetail;
	private TextView tvTime;
	private ImageView ivRecord;
	private Button BtnSave;
	private String recordIndex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record_detail);
		map = (Map<String, String>) getIntent().getSerializableExtra("recordDetail");
		recordIndex = map.get("index");

		IntentFilter filter = new IntentFilter();
		filter.addAction(Define.BROAD_CAST_RECV_DATA_COMPLETE);
		this.registerReceiver(mReciver, filter);

		initialize();
		initBar();
	}

	@Override
	public void initView() {
		tvdetail = (TextView) findViewById(R.id.tv_detail);
		tvTime = (TextView) findViewById(R.id.tv_detail_time);
		ivRecord = (ImageView) findViewById(R.id.iv_physical_record);
		BtnSave = (Button) findViewById(R.id.btn_savetophone);
		tvdetail.setText(map.get("title"));
		tvTime.setText(map.get("date"));
		physicalDetailReq();
	}

	@Override
	public void setupData() {

	}

	private int seqPhysicalDetail;

	private void physicalDetailReq() {
		MsgInncDef.IPhsicalRecordDetailReq req = new MsgInncDef.IPhsicalRecordDetailReq();
		req.IUserId = MyApplication.userId;
		req.iRecordIndex = Integer.parseInt(recordIndex);
		seqPhysicalDetail = MyApplication.SequenceNo++;
		byte[] bData = HandleNetSendMsg.HandlePhysicalRecordDetailPro(req, seqPhysicalDetail);
		HouseSocketConn.pushtoList(bData);

	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReciver);
	}

	private void physicalDetailResp(Long recvTime) {
		MsgReceiveDef.PhysicalDetailResp resp = (PhysicalDetailResp) HandleMsgDistribute.getInstance()
				.queryCompleteMsg(recvTime);
		Net_Tijian_RecoderInfo_PRO info = resp.info;
		String url = resp.Szurl;
	}

	BroadcastReceiver mReciver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (Define.BROAD_CAST_RECV_DATA_COMPLETE.equals(intent.getAction())) {
				int iSequence = intent.getIntExtra(Define.BROAD_SEQUENCE, -1);
				long recvTime = intent.getLongExtra(Define.BROAD_MSG_RECVTIME, -1);
				if (iSequence == seqPhysicalDetail) {
					physicalDetailResp(recvTime);
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
		iv.setText(map.get("title"));
		llback.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				RecordDetailActivity.this.finish();
			}
		});

	}
}
