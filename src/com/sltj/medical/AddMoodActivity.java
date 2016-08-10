package com.sltj.medical;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sltj.medical.adapter.AddMoodAdapter;
import com.sltj.medical.base.BaseActivity;
import com.sltj.medical.base.MyApplication;
import com.sltj.medical.base.ViewHolder;
import com.sltj.medical.config.Define;
import com.sltj.medical.dataUtil.HandleMsgDistribute;
import com.sltj.medical.dataUtil.HandleNetSendMsg;
import com.sltj.medical.dataUtil.protobuf.EnumPro.eOPERRESULT_PRO;
import com.sltj.medical.publicMsg.MsgInncDef;
import com.sltj.medical.publicMsg.MsgReceiveDef.AddMoodResp;
import com.sltj.medical.socketutil.HouseSocketConn;
import com.sltj.medical.util.ToastUtils;
import com.sltj.medical.wedgit.LoadingDialog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

public class AddMoodActivity extends BaseActivity implements OnClickListener, OnItemClickListener {
	private LoadingDialog mDialog;
	// 添加心情记录seq号
	private int seqAddMoodRecord;
	private List<Map<String, Object>> lst = new ArrayList<Map<String, Object>>();
	private AddMoodAdapter mAdapter;
	private GridView mGridView;
	private Button btnAddMood;
	private EditText etMood;
	// 心情等级
	private int moodGrade;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_and_mood);
		mDialog = new LoadingDialog(this);
		// 注册接收消息的广播
		IntentFilter filter = new IntentFilter();
		filter.addAction(Define.BROAD_CAST_RECV_DATA_COMPLETE);
		this.registerReceiver(mReceiver, filter);
		initialize();
		initBar();
	}

	@Override
	public void initView() {
		btnAddMood = (Button) findViewById(R.id.btn_push_addmood);
		mGridView = (GridView) findViewById(R.id.gv_addmood);
		etMood = (EditText) findViewById(R.id.et_record_mood);
		for (int i = 0; i <= 100; i += 10) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("imgLevel", i);
			lst.add(map);
		}
		mAdapter = new AddMoodAdapter(this, lst);
		mGridView.setAdapter(mAdapter);

	}

	@Override
	public void setupData() {
		btnAddMood.setOnClickListener(this);
		mGridView.setOnItemClickListener(this);
	}

	/*
	 * 添加 心情请求
	 */
	private void addMoodProReq() {
		mDialog.show();
		final MsgInncDef.IMoodAddReq recordReq = new MsgInncDef.IMoodAddReq();
		recordReq.iuserid = MyApplication.userId;
		recordReq.iGrade = moodGrade;
		recordReq.szContent = etMood.getText().toString();
		seqAddMoodRecord = MyApplication.SequenceNo++;

		byte[] btData = HandleNetSendMsg.HandleAddMoodPro(recordReq, seqAddMoodRecord);
		HouseSocketConn.pushtoList(btData);
	}

	private void addMoodProResp(long recvTime) {
		mDialog.dismiss();
		AddMoodResp resp = (AddMoodResp) HandleMsgDistribute.getInstance().queryCompleteMsg(recvTime);
		if (resp.eResult == eOPERRESULT_PRO.E_OPER_SUCCESS_PRO) {
			ToastUtils.show(this, "添加成功", 0);
			this.finish();
		}else{
			ToastUtils.show(this, resp.eResult+"", 0);
		}

	}

	// 广播
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (Define.BROAD_CAST_RECV_DATA_COMPLETE.equals(intent.getAction())) {
				int iSequence = intent.getIntExtra(Define.BROAD_SEQUENCE, -1);
				long recvTime = intent.getLongExtra(Define.BROAD_MSG_RECVTIME, -1);
				if (iSequence == seqAddMoodRecord) {
					addMoodProResp(recvTime);
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
				AddMoodActivity.this.finish();
			}
		});

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.unregisterReceiver(mReceiver);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_push_addmood) {
			addMoodProReq();

		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Map<String, Object> map = (Map<String, Object>) parent.getAdapter().getItem(position);
		moodGrade = (Integer) map.get("imgLevel");
		ViewHolder vh = (ViewHolder) view.getTag();
		RadioButton cb = vh.getView(RadioButton.class, R.id.cb_cheked);
		if (cb.isChecked()) {
			cb.setChecked(false);

		} else {
			cb.setChecked(true);

		}
	}
}
