package com.sltj.medical;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.sltj.medical.adapter.AdapterChat;
import com.sltj.medical.base.BaseActivity;
import com.sltj.medical.base.MyApplication;
import com.sltj.medical.bean.ChatBean;
import com.sltj.medical.config.Define;
import com.sltj.medical.dataUtil.HandleMsgDistribute;
import com.sltj.medical.dataUtil.HandleNetSendMsg;
import com.sltj.medical.dataUtil.protobuf.EnumPro.eMESSAGE_TYPE_PRO;
import com.sltj.medical.dataUtil.protobuf.EnumPro.eOPERRESULT_PRO;
import com.sltj.medical.publicMsg.MsgInncDef;
import com.sltj.medical.publicMsg.MsgReceiveDef.chatInfoResp;
import com.sltj.medical.socketutil.HouseSocketConn;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class ChatActivity extends BaseActivity implements  OnClickListener{
	private int seqChat; // 聊天seq;
	private EditText mEditTextContent;
	private Button buttonSend;
	private Button btnMore;
	private int doctorId;
	private ListView  chatListView;
	private List<ChatBean>lst=new ArrayList<ChatBean>();
	private  AdapterChat mAdapter;
	private Button btnFrom;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		IntentFilter filter = new IntentFilter();
		filter.addAction(Define.BROAD_CAST_RECV_DATA_COMPLETE);
		this.registerReceiver(mReceiver, filter);
		doctorId = getIntent().getIntExtra("doctorId", -1);
		initialize();
		initBar();
	}


	@Override
	public void initView() {
		mEditTextContent = (EditText) findViewById(R.id.et_sendmessage);
		buttonSend = (Button) findViewById(R.id.btn_send);
		chatListView=(ListView) findViewById(R.id.chat_list);
		btnMore = (Button) findViewById(R.id.btn_more);
		btnFrom = (Button) findViewById(R.id.btn_set_mode_voice);
		
		// 监听文字框
		mEditTextContent.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (!TextUtils.isEmpty(s)) {
					btnMore.setVisibility(View.GONE);
					buttonSend.setVisibility(View.VISIBLE);
				} else {
					btnMore.setVisibility(View.VISIBLE);
					buttonSend.setVisibility(View.GONE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
	}

	@Override
	public void setupData() {
		mAdapter=new AdapterChat(this, lst);
		chatReq();
		buttonSend.setOnClickListener(this);
		btnFrom.setOnClickListener(this);
		
	}

	/*
	 * 聊天请求
	 */
	private void chatReq() {
		seqChat = MyApplication.SequenceNo++;
		MsgInncDef.IChatReq req = new MsgInncDef.IChatReq();
		req.idstid = doctorId; // 对方ID 和谁聊天
		req.content = mEditTextContent.getText().toString();
		req.type = eMESSAGE_TYPE_PRO.EN_TEXT_MSG_PRO;
		req.iuserid = MyApplication.userId;
		byte bData[] = HandleNetSendMsg.HandleChatInfoPro(req, seqChat);
		HouseSocketConn.pushtoList(bData);
	}

	/*
	 * 聊天响应
	 */
	private void chatResp(long recvTime) {
		chatInfoResp resp = (chatInfoResp) HandleMsgDistribute.getInstance().queryCompleteMsg(recvTime);
		if (resp.eResult == eOPERRESULT_PRO.E_OPER_SUCCESS_PRO) {
			int idstId = resp.idstid;
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
				if (iSequence == seqChat) {
					// 聊天响应
					chatResp(recvTime);
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
		iv.setText("徐峙医生");
		llback.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ChatActivity.this.finish();
			}
		});

	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_send:
			ChatBean chatBean=new ChatBean();
			chatBean.setSendDate(new Date());//发送时间
			chatBean.setChatContent(mEditTextContent.getText().toString());
			chatBean.setType(ChatBean.MESSAGE_SEND);
			chatListView.setAdapter(mAdapter);
			lst.add(chatBean);
			mAdapter.notifyDataSetChanged();
			mEditTextContent.setText("");
			chatListView.setSelection(lst.size()-1);//设置偏移到最后一条消息;
			chatReq();//聊天请求
			
			break;
			
		case R.id.btn_set_mode_voice:
			//模拟接收消息
			ChatBean chatBean2=new ChatBean();
			chatBean2.setChatContent(mEditTextContent.getText().toString());
			chatBean2.setType(ChatBean.MESSAGE_RECIVE);
			chatBean2.setSendDate(new Date());
			chatListView.setAdapter(mAdapter);
			lst.add(chatBean2);
			mAdapter.notifyDataSetChanged();
			mEditTextContent.setText("");
			chatListView.setSelection(lst.size()-1);
		}
	}

}
