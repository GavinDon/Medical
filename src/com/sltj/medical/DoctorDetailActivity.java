package com.sltj.medical;

import com.sltj.medical.base.BaseActivity;
import com.sltj.medical.base.MyApplication;
import com.sltj.medical.config.Define;
import com.sltj.medical.dataUtil.HandleMsgDistribute;
import com.sltj.medical.dataUtil.HandleNetSendMsg;
import com.sltj.medical.dataUtil.protobuf.EnumPro.eOPERRESULT_PRO;
import com.sltj.medical.publicMsg.MsgInncDef;
import com.sltj.medical.publicMsg.MsgReceiveDef.DoctorInfoResp;
import com.sltj.medical.socketutil.HouseSocketConn;

import android.R.mipmap;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DoctorDetailActivity extends BaseActivity implements OnClickListener, OnFocusChangeListener {
	private TextView tvDoctorName;// 医生姓名
	private TextView tvDoctorDetail;// 医生信息描述
	private Button btnChat; // 咨询医生
	private Button btnAttention;// 关注人数
	private int doctorId;
	private int seqDoctInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_doctor_detail);
		doctorId = getIntent().getIntExtra("doctorId", -1);
		IntentFilter filter = new IntentFilter();
		filter.addAction(Define.BROAD_CAST_RECV_DATA_COMPLETE);
		this.registerReceiver(mReceiver, filter);
		initialize();
	}

	@Override
	public void initView() {
		initBar();
		tvDoctorName = (TextView) findViewById(R.id.tv_doctorname_doctordetail);
		tvDoctorDetail = (TextView) findViewById(R.id.tv_doctordetail);
		btnChat = (Button) findViewById(R.id.btn_chat_doctordetail);
		btnAttention = (Button) findViewById(R.id.btn_attention_doctordetail);
	}

	@Override
	public void setupData() {
		doctorDetailReq();
		btnChat.setOnClickListener(this);
	}

	private int seqDoctInfoList;

	private void doctorDetailReq() {
		seqDoctInfoList = MyApplication.SequenceNo++;
		MsgInncDef.IDoctorInfoReq req = new MsgInncDef.IDoctorInfoReq();
		req.iDoctorId = doctorId;
		req.iuserid = MyApplication.userId;
		byte bData[] = HandleNetSendMsg.HandleDoctorInfoPro(req, seqDoctInfoList);
		HouseSocketConn.pushtoList(bData);

	}

	private void doctroDetailResp(Long recvTime) {
		DoctorInfoResp resp = (DoctorInfoResp) HandleMsgDistribute.getInstance().queryCompleteMsg(recvTime);
		if (resp.eResult.equals(eOPERRESULT_PRO.E_OPER_SUCCESS_PRO)) {
			tvDoctorDetail.setText(resp.doctorinfo.toString());
		}
	}

	/*
	 * 广播接收从socket中得到的值
	 */
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (Define.BROAD_CAST_RECV_DATA_COMPLETE.equals(intent.getAction())) {
				int iSequence = intent.getIntExtra(Define.BROAD_SEQUENCE, -1);
				long recvTime = intent.getLongExtra(Define.BROAD_MSG_RECVTIME, -1);
				if (iSequence == seqDoctInfo) {
					// 医生列表
					doctroDetailResp(recvTime);
				}

			}

		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_chat_doctordetail:
			Intent mIntent = new Intent(this, ChatActivity.class);
			mIntent.putExtra("doctorId", doctorId);
			startActivity(mIntent);
			break;

		default:
			break;
		}
	}

	/*
	 * 初始化标题栏
	 */
	private void initBar() {
		LinearLayout llback = (LinearLayout) findViewById(R.id.ll_back);
		TextView iv = (TextView) findViewById(R.id.tv_title);
		iv.setText("医师祥情");
		llback.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DoctorDetailActivity.this.finish();
			}
		});

	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {

	}
}
