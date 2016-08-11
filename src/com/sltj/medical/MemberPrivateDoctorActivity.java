package com.sltj.medical;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sltj.medical.adapter.DoctorListAdapter;
import com.sltj.medical.base.BaseActivity;
import com.sltj.medical.base.MyApplication;
import com.sltj.medical.config.Define;
import com.sltj.medical.dataUtil.HandleMsgDistribute;
import com.sltj.medical.dataUtil.HandleNetSendMsg;
import com.sltj.medical.dataUtil.protobuf.EnumPro.eOPERRESULT_PRO;
import com.sltj.medical.publicMsg.MsgInncDef;
import com.sltj.medical.publicMsg.MsgReceiveDef.DoctorListResp;
import com.sltj.medical.socketutil.HouseSocketConn;
import com.sltj.medical.util.SearchUtil;
import com.sltj.medical.util.ToastUtils;
import com.sltj.medical.wedgit.ClearEditText;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

/**
 * 私人医生 （会员版）
 * 
 * @author linan
 *
 */
public class MemberPrivateDoctorActivity extends BaseActivity implements OnItemClickListener, TextWatcher {

	private ClearEditText etSearch;

	List<Map<String, Object>> lst = new ArrayList<Map<String, Object>>();

	private SearchUtil mSearchUtil;

	private DoctorListAdapter mAdapter;

	private ListView mListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_member_private_doctor);
		IntentFilter filter = new IntentFilter();
		filter.addAction(Define.BROAD_CAST_RECV_DATA_COMPLETE);
		this.registerReceiver(mReceiver, filter);

		initialize();
	}

	@Override
	public void initView() {
		etSearch = (ClearEditText) findViewById(R.id.et_search);
		mListView = (ListView) findViewById(R.id.lv_member_doctor);
		IDoctorListReq();

	}

	@Override
	public void setupData() {
		mSearchUtil = new SearchUtil(lst);
		mListView.setOnItemClickListener(this);
		etSearch.addTextChangedListener(this);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.unregisterReceiver(mReceiver);

	}

	/*
	 * 医生列表请求
	 */
	private int seqDoctList;

	private void IDoctorListReq() {
		seqDoctList = MyApplication.SequenceNo++;
		MsgInncDef.IMyDoctorListReq req = new MsgInncDef.IMyDoctorListReq();
		req.iuserid = MyApplication.userId;
		req.iPage = 1;
		byte bData[] = HandleNetSendMsg.HandleMyDoctorListPro(req, seqDoctList);
		HouseSocketConn.pushtoList(bData);

	}

	/*
	 * 医生列表信息响应
	 */
	private void doctorListResp(Long recevTime) {
		DoctorListResp resp = (DoctorListResp) HandleMsgDistribute.getInstance().queryCompleteMsg(recevTime);

		if (resp.eResult.equals(eOPERRESULT_PRO.E_OPER_SUCCESS_PRO)) {
			if (!resp.info.isEmpty()) {
				for (int i = 0; i < resp.info.size(); i++) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("img", resp.info.get(i).getSzHeadPic());
					map.put("name", resp.info.get(i).getSzNickName());
					map.put("chatInfo", resp.info.get(i).getSzRealName());
					map.put("workYears", resp.info.get(i).getIHeadPic());
					map.put("job", resp.info.get(i).getIHeadPic());
					lst.add(map);
				}
				mAdapter = new DoctorListAdapter(this, lst);
				mListView.setAdapter(mAdapter);
			}

		} else {
			ToastUtils.show(this, resp.eResult.toString(), 0);
		}

	}

	/*
	 * 广播接收从socket中得到的值
	 */
	BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (Define.BROAD_CAST_RECV_DATA_COMPLETE.equals(intent.getAction())) {
				int iSequence = intent.getIntExtra(Define.BROAD_SEQUENCE, -1);
				long recvTime = intent.getLongExtra(Define.BROAD_MSG_RECVTIME, -1);
				if (iSequence == seqDoctList) {
					// 医生列表
					doctorListResp(recvTime);
				}

			}

		}
	};

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Map<String, Object> map = (Map<String, Object>) parent.getAdapter().getItem(position);
		Intent mIntent = new Intent(this, DoctorDetailActivity.class);
		mIntent.putExtra("doctorId", (Integer) map.get("doctorId"));
		startActivity(mIntent);
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		String content = etSearch.getText().toString();
		List<Map<String, Object>> searchLst = mSearchUtil.filterData(s.toString().toLowerCase(), "name");
		mAdapter.clear();
		mAdapter = new DoctorListAdapter(MemberPrivateDoctorActivity.this, searchLst);
		mListView.setAdapter(mAdapter);
		if (content == null || content.equals("")) {
			mAdapter.clear();
			mAdapter = new DoctorListAdapter(MemberPrivateDoctorActivity.this, lst);
			mListView.setAdapter(mAdapter);
		}
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

	}

	@Override
	public void afterTextChanged(Editable s) {

	}

}
