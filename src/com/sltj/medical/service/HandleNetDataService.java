package com.sltj.medical.service;

import com.sltj.medical.base.MyApplication;
import com.sltj.medical.config.Define;
import com.sltj.medical.dataUtil.HandleMsgDistribute;
import com.sltj.medical.dataUtil.HandleNetHeadMsg;
import com.sltj.medical.dataUtil.HandleNetReceiveMsg;
import com.sltj.medical.util.LogUtils;
import com.sltj.medical.util.MTools;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class HandleNetDataService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		String actionService = intent.getAction();
		long recvTime = intent.getLongExtra(Define.BROAD_RECVTIME, -1);

		// 查询接收到的数据，当拿到数据之后会将该条数据从接收数据map中删除
		byte[] pendingDealData = HandleMsgDistribute.getInstance().queryRecvMsg(recvTime);
		if (pendingDealData == null || pendingDealData.length == 0) {
			return START_REDELIVER_INTENT;
		}

		byte[] proBufHead = MTools.getNetMsgHead(pendingDealData);// 消息头数据
		byte[] proBufBody = MTools.getNetMsgBody(pendingDealData);// 消息体数据
		HandleNetHeadMsg headMsg = HandleNetHeadMsg.parseHeadMag(proBufHead);// 消息头解析

		int iSequenceNo = headMsg.uiSequenceNo;
		int iMsgType = headMsg.uiMsgType;

		LogUtils.i("android服务 收到响应--sequenceNo =" + iSequenceNo + " , iMsgType = " + iMsgType);

		// 数据来源于登录认证服务器
		if (Define.BROAD_CAST_RECV_DATA_AUTH.equals(actionService)) {
			Object objData = HandleNetReceiveMsg.getParseAuthMsgType(iMsgType, proBufBody);
			HandleMsgDistribute.getInstance().insertCompleteMsg(recvTime, objData);

			sendCompleteBroad(iSequenceNo, iMsgType, Define.BROAD_CAST_RECV_DATA_AUTH, recvTime);// 发送广播给activity
		}

		// 数据来源于家政服务器
		if (Define.BROAD_CAST_RECV_DATA_HOUSE.equals(actionService)) {
			Object objData = HandleNetReceiveMsg.getParseHouseMsgType(iMsgType, proBufBody);
			HandleMsgDistribute.getInstance().insertCompleteMsg(recvTime, objData);

			sendCompleteBroad(iSequenceNo, iMsgType, Define.BROAD_CAST_RECV_DATA_HOUSE, recvTime);// 发送广播给activity
		}

		return START_REDELIVER_INTENT;
	}

	/**
	 * 发送广播，通知activity
	 */
	public void sendCompleteBroad(int iSequenceNo, int iMSgType, String dataFrom, long recvTime) {
		Intent intent = new Intent();
		intent.setAction(Define.BROAD_CAST_RECV_DATA_COMPLETE);
		intent.putExtra(Define.BROAD_SEQUENCE, iSequenceNo);
		intent.putExtra(Define.BROAD_MSG_RECVTIME, recvTime);
		intent.putExtra(Define.BROAD_MSG_TYPE, iMSgType);
		intent.putExtra(Define.BROAD_DATA_FROM, dataFrom);
		MyApplication.context.sendBroadcast(intent);
	}

	@Override
	public void onDestroy() {
		stopSelf();
		super.onDestroy();
	}

}
