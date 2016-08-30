package com.sltj.medical;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import com.sltj.medical.base.BaseActivity;
import com.sltj.medical.base.MyApplication;
import com.sltj.medical.config.Define;
import com.sltj.medical.dataUtil.HandleMsgDistribute;
import com.sltj.medical.dataUtil.HandleNetSendMsg;
import com.sltj.medical.dataUtil.protobuf.EnumPro.eLOGIN_TYPE_PRO;
import com.sltj.medical.dataUtil.protobuf.EnumPro.eOPERRESULT_PRO;
import com.sltj.medical.publicMsg.MsgInncDef;
import com.sltj.medical.publicMsg.MsgInncDef.GetVerification;
import com.sltj.medical.publicMsg.MsgInncDef.LoginReq;
import com.sltj.medical.publicMsg.MsgReceiveDef.AuthLoginResp;
import com.sltj.medical.publicMsg.MsgReceiveDef.AuthcodeResp;
import com.sltj.medical.publicMsg.MsgReceiveDef.NetConnectResp;
import com.sltj.medical.socketutil.AuthSocketConn;
import com.sltj.medical.socketutil.DNSParsing;
import com.sltj.medical.socketutil.HouseSocketConn;
import com.sltj.medical.socketutil.PushData;
import com.sltj.medical.util.LogUtils;
import com.sltj.medical.util.RegistAccoutUtil;
import com.sltj.medical.util.SPUtils;
import com.sltj.medical.util.ToastUtils;
import com.sltj.medical.util.MTools;
import com.sltj.medical.wedgit.ClearEditText;
import com.sltj.medical.wedgit.LoadingDialog;
import com.sltj.medical.wedgit.TimeButton;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends BaseActivity implements OnClickListener {
	// 验证码按钮
	private TimeButton timeButton;
	private Button btnLogin;
	private TextView tvPwdLgoin;
	private int seqLogin = -1;
	private int seqConnHouse = -1;
	private int seqConnAuth;
	private int seqSerList = -1;
	private int seqCode = -1;
	private ClearEditText etPhone;
	private String strPhone;
	private MyApplication myApplication;
	private String strCode;
	private ClearEditText etCode;
	private LoadingDialog mProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		IntentFilter filter = new IntentFilter();
		filter.addAction(Define.BROAD_CAST_RECV_DATA_COMPLETE);
		registerReceiver(mReceiver, filter);
		myApplication = (MyApplication) getApplication();
		mProgressDialog = new LoadingDialog(this);

		initialize();
	}

	@Override
	public void initView() {
		btnLogin = (Button) findViewById(R.id.btn_login);
		tvPwdLgoin = (TextView) findViewById(R.id.tv_login_pwd);
		etPhone = (ClearEditText) findViewById(R.id.et_login_phonenumber);
		etCode = (ClearEditText) findViewById(R.id.et_login_vertifal);
		etPhone.setText("18602928514");
		loadConnectAuthDataBroad();
		initVertify();
	}

	@Override
	public void setupData() {
		tvPwdLgoin.setOnClickListener(this);
		btnLogin.setOnClickListener(this);

	}

	/**
	 * 验证码
	 */
	private void initVertify() {
		timeButton = (TimeButton) findViewById(R.id.send_code);
		timeButton.setOnClickListener(this);
	}

	/**
	 * 加载 连接请求 -- 登录认证服务器
	 */
	public void loadConnectAuthDataBroad() {
		seqConnAuth = MyApplication.SequenceNo++;

		byte[] connData = HandleNetSendMsg.HandleConnectToPro(new MsgInncDef().new NetConnectReq(), seqConnAuth);
		// 连接登录服务器
		AuthSocketConn.pushtoList(connData);
		LogUtils.i("连接登录服务器请求数据--sequence=" + seqConnAuth + "/" + Arrays.toString(connData) + "----------");
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		// 从后台转入前台时再次请求连接服务
		if (MyApplication.authSocketConn.isClose()) {
			MyApplication.authSocketConn = new AuthSocketConn(PushData.getAuthIp(), PushData.getAuthPort());
			loadConnectAuthDataBroad();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
	}

	/**
	 * 处理连接服务器数据
	 * 
	 * @param recvTime
	 */
	private void processConnAuthData(long recvTime) {
		NetConnectResp connectResp = (NetConnectResp) HandleMsgDistribute.getInstance().queryCompleteMsg(recvTime);
		if (connectResp == null) {
			return;
		}
		if (connectResp.eResult == eOPERRESULT_PRO.E_OPER_SUCCESS_PRO) {
			int time = connectResp.iSrvTime;
			int userId = connectResp.iUserid;
			eOPERRESULT_PRO result = connectResp.eResult;

			LogUtils.i("连接登录认证服务器--" + "time = " + time + "/ userId = " + userId);

			if (MTools.isStringEmpty(PushData.getHouseIp())) {
				// loadDataServerListBroad();
			}
			// loadLoginData();// 当连接上登录认证服务器后需要发送登录请求

		}
	}

	/*
	 * 请求验证码
	 */
	public void loadVerifaction() {
		seqCode = MyApplication.SequenceNo++;
		GetVerification getVertivication = new MsgInncDef().new GetVerification();
		getVertivication.szAccountID = etPhone.getText().toString();
		byte[] connData = HandleNetSendMsg.HandleVerficationPro(getVertivication, seqCode);
		// 连接登录服务器
		AuthSocketConn.pushtoList(connData);
		LogUtils.i("请求验证码请求数据--sequence=" + seqConnAuth + "/" + Arrays.toString(connData) + "----------");
	}

	/*
	 * 请求验证码结果
	 */
	private void getReturnCode(long recvTime) {
		AuthcodeResp resp = (AuthcodeResp) HandleMsgDistribute.getInstance().queryCompleteMsg(recvTime);
		if (resp == null) {
			return;
		}
		if (resp.eResult == eOPERRESULT_PRO.E_OPER_SUCCESS_PRO) {
			strCode = resp.szCode;
			LogUtils.i("strCode=" + strCode);
			sendPlatCode(strCode);
			etCode.setText(strCode);
		}

	}

	/**
	 * 登录请求
	 */
	private void loginProBuffer(String vertify) {
		seqLogin = MyApplication.SequenceNo++;
		mProgressDialog.show();
		LoginReq loginReq = new MsgInncDef().new LoginReq();
		loginReq.strAccountID = etPhone.getText().toString().trim();
		loginReq.strPasswd = vertify;
		loginReq.type = eLOGIN_TYPE_PRO.EN_LOGIN_PHONE_CODE_PRO_VALUE;
		if (!TextUtils.isEmpty(etPhone.getText().toString()) && !TextUtils.isEmpty(vertify)) {
			byte[] connData = HandleNetSendMsg.HandleLoginToPro(loginReq, seqLogin);
			AuthSocketConn.pushtoList(connData);
			LogUtils.i("登录请求--sequence=" + seqLogin + "/" + Arrays.toString(connData));
		} else {
			ToastUtils.show(this, "帐号或者验证码不能为空", 0);
		}

	}

	/**
	 * 处理登录响应的数据
	 * 
	 * @param recvTime
	 */
	private void processLoginData(long recvTime) {
		AuthLoginResp authLoginResp = (AuthLoginResp) HandleMsgDistribute.getInstance().queryCompleteMsg(recvTime);

		if (authLoginResp == null) {
			return;
		}
		if (authLoginResp.eResult == eOPERRESULT_PRO.E_OPER_SUCCESS_PRO) {
			mProgressDialog.dismiss();
			int userId = authLoginResp.iuserid;
			MyApplication.userId = userId;
			String phone = authLoginResp.szPhoneNum;
			String szUserID = authLoginResp.szUserID;
			String szUserNick = authLoginResp.szUserNick;
			LogUtils.i(authLoginResp.eResult + "");
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
			this.finish();

		} else {
			String result = MTools.judgeNetResult_Auth(authLoginResp.eResult);
			ToastUtils.show(this, String.valueOf(eOPERRESULT_PRO.E_OPER_AUTHFAILER_PRO), 0);
			LogUtils.i("登录失败" + result);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_login:
			if (etCode.getText().toString().trim().equals(strCode)) {
				loginProBuffer(etCode.getText().toString().trim());
			} else {
				ToastUtils.show(this, "验证码错误", 0);
			}

			break;
		case R.id.tv_login_pwd:
			Intent intent = new Intent(this, PwdLoginActivity.class);
			startActivity(intent);
			break;
		case R.id.send_code:
			strPhone = etPhone.getText().toString().trim();
			if (TextUtils.isEmpty(strPhone)) {
				ToastUtils.show(this, "请输入正确的手机号", 0);
				return;
			}
			if (strPhone.length() < 11) {
				ToastUtils.show(this, "请输入正确的手机号", 0);
			} else {
				timeButton.setMobile(strPhone);
				// 获取验证码
				timeButton.settextAfter("秒后重新获取").setTextBefore("获取验证码").setLenght(60 * 1000);
				loadVerifaction();
			}

			break;

		}

	}

	/**
	 * 获取到服务器返回的验证码然后通过平台给手机发送验证码
	 */
	private void sendPlatCode(final String code) {

		new Thread(new Runnable() {
			public void run() {
				// 网络操作开启线程
				RegistAccoutUtil.sendSMS(strPhone, code);
			}
		}).start();

	}

	/**
	 * 广播接收数据
	 */
	BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (Define.BROAD_CAST_RECV_DATA_COMPLETE.equals(intent.getAction())) {
				int iSequence = intent.getIntExtra(Define.BROAD_SEQUENCE, -1);
				long recvTime = intent.getLongExtra(Define.BROAD_MSG_RECVTIME, -1);

				if (seqCode == iSequence) {
					// 处理完成的验证码
					getReturnCode(recvTime);
				} else if (seqConnAuth == iSequence) {
					// 处理完成的连接请求
					processConnAuthData(recvTime);
				} else if (seqLogin == iSequence) {
					// 处理完成的登录数据
					processLoginData(recvTime);
				}
			}
		}
	};
	/**
	 * 启动线程解析域名 用于解析登录认证服务器的域名
	 */
	Runnable anthRunnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub

			if (MTools.isStringEmpty(MyApplication.netParam.getAuthDns())) {
				PushData.setAuthIp(MyApplication.netParam.getAuthIp());
				PushData.setAuthPort(MyApplication.netParam.getAuthPort());
			} else {
				String strDns = MyApplication.netParam.getAuthDns();

				if (strDns.indexOf(":") > 0) {
					String ip = DNSParsing.getIP(strDns.substring(0, strDns.indexOf(":")));
					int port = Integer.parseInt(strDns.substring(strDns.indexOf(":") + 1, strDns.length()));
					MyApplication.netParam.setAuthDnsParsIp(ip);
					MyApplication.netParam.setAuthDnsParsPort(port);
					PushData.setAuthIp(ip);
					PushData.setAuthPort(port);
				} else {
					PushData.setAuthIp(MyApplication.netParam.getAuthIp());
					PushData.setAuthPort(MyApplication.netParam.getAuthPort());
				}

			}

			if (MyApplication.authSocketConn != null) {
				myApplication.authSocketConn.closeAuthSocket();
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			LogUtils.i("创建 Auth socket");
			myApplication.authSocketConn = new AuthSocketConn(PushData.getAuthIp(), PushData.getAuthPort());
			loadConnectAuthDataBroad();
		}
	};

	/**
	 * 启动线程解析域名 用于解析家政服务器的域名
	 */
	Runnable hsRunnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub

			if (MTools.isStringEmpty(MyApplication.netParam.getHsDns())) {
				PushData.setHouseIp(MyApplication.netParam.getHsIp());// 把得到的最优服务器ip和port放入结构体中
				PushData.setHousePort(MyApplication.netParam.getHsPort());
			} else {
				String strDns = MyApplication.netParam.getHsDns();

				if (strDns.indexOf(":") > 0) {
					String strIp = DNSParsing.getIP(strDns.substring(0, strDns.indexOf(":")));
					int iPort = Integer.parseInt(strDns.substring(strDns.indexOf(":") + 1, strDns.length()));
					MyApplication.netParam.setHsDnsParsIp(strIp);
					MyApplication.netParam.setHsDnsParsPort(iPort);
					// 把得到的最优服务器ip和port放入结构体中
					PushData.setHouseIp(strIp);
					PushData.setHousePort(iPort);
				} else {
					PushData.setHouseIp(MyApplication.netParam.getHsIp());// 把得到的最优服务器ip和port放入结构体中
					PushData.setHousePort(MyApplication.netParam.getHsPort());
				}

			}

			if (MyApplication.houseSocketConn != null) {
				MyApplication.houseSocketConn.closeHouseSocket();
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			myApplication.houseSocketConn = new HouseSocketConn(PushData.getHouseIp(), PushData.getHousePort());
		}
	};

	
	@Override
	public void onBackPressed() {
		this.finish();
	}

}
