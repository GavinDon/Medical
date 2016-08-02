package com.sltj.medical;

import java.util.List;

import com.sltj.medical.base.MyApplication;
import com.sltj.medical.config.Define;
import com.sltj.medical.dataUtil.HandleMsgDistribute;
import com.sltj.medical.dataUtil.HandleNetSendMsg;
import com.sltj.medical.dataUtil.protobuf.PublicmsgPro.Net_ServerInfo_PRO;
import com.sltj.medical.publicMsg.MsgInncDef;
import com.sltj.medical.publicMsg.MsgInncDef.AuthNetCommonReq;
import com.sltj.medical.publicMsg.MsgReceiveDef;
import com.sltj.medical.publicMsg.MsgReceiveDef.NetBestServerResp;
import com.sltj.medical.socketutil.AuthSocketConn;
import com.sltj.medical.socketutil.HouseSocketConn;
import com.sltj.medical.util.FragmentController;
import com.sltj.medical.util.LogUtils;
import com.sltj.medical.util.ToastUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements OnCheckedChangeListener {
	private RadioGroup mRadioGroup;
	private long exitTime = 0;

	private FragmentController controller;

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
		mRadioGroup = (RadioGroup) findViewById(R.id.rb_main_page);
		mRadioGroup.setOnCheckedChangeListener(this);

	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.radio_homepage:
			controller.showFragment(0);
			break;
		case R.id.radio_news:
			controller.showFragment(1);
			break;
		case R.id.radio_persion:
			controller.showFragment(2);
			break;
		case R.id.radio_product:
			controller.showFragment(3);
			break;
		case R.id.radio_mine:
			controller.showFragment(4);
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 销毁Fragment
		FragmentController.onDestroy();
		unregisterReceiver(mReceiver);
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

}
