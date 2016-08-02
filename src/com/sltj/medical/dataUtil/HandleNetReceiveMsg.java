package com.sltj.medical.dataUtil;

import com.google.protobuf.InvalidProtocolBufferException;
import com.sltj.medical.dataUtil.protobuf.AuthMsgPro;
import com.sltj.medical.dataUtil.protobuf.AuthMsgPro.AUTH_GetCodeResp_PRO;
import com.sltj.medical.dataUtil.protobuf.AuthMsgPro.AUTH_GetServerInfoResp_PRO;
import com.sltj.medical.dataUtil.protobuf.AuthMsgPro.AUTH_LoginResp_PRO;
import com.sltj.medical.dataUtil.protobuf.CmdMsgPro;
import com.sltj.medical.dataUtil.protobuf.CmdMsgPro.CMD_GetHomeNewsResp_PRO;
import com.sltj.medical.dataUtil.protobuf.CmdMsgPro.CMD_GetNewsResp_PRO;
import com.sltj.medical.dataUtil.protobuf.CmdMsgPro.CMD_ReadNewsResp_PRO;
import com.sltj.medical.dataUtil.protobuf.PublicmsgPro;
import com.sltj.medical.dataUtil.protobuf.PublicmsgPro.NetConnectResp_PRO_MSG;
import com.sltj.medical.dataUtil.protobuf.PublicmsgPro.Net_CommonResp_PRO_MSG;
import com.sltj.medical.publicMsg.MsgReceiveDef;
import com.sltj.medical.publicMsg.MsgReceiveDef.AuthLoginResp;
import com.sltj.medical.publicMsg.MsgReceiveDef.AuthNetCommonResp;
import com.sltj.medical.publicMsg.MsgReceiveDef.AuthcodeResp;
import com.sltj.medical.publicMsg.MsgReceiveDef.HomeNewsResp;
import com.sltj.medical.publicMsg.MsgReceiveDef.NetBestServerResp;
import com.sltj.medical.publicMsg.MsgReceiveDef.NetConnectResp;
import com.sltj.medical.publicMsg.MsgReceiveDef.NewsResp;
import com.sltj.medical.publicMsg.MsgReceiveDef.ReadNewsResp;
import com.sltj.medical.publicMsg.NetHouseMsgType;

/**
 * 数据处理 将从网络获取的数据转为内部使用的数据
 */
public class HandleNetReceiveMsg {

	/**
	 * 
	 * 根据消息头来分发处理消息--登录认证服务器
	 * 
	 * @param proBufArray
	 * @return
	 */
	public static Object getParseAuthMsgType(int iMsgType, byte[] proBufBody) {
		Object obj = null;
		switch (iMsgType) {
		case NetHouseMsgType.NETCMD_CONNECT_RESP:// 连接请求的响应
			obj = HandleProToConnectResp(proBufBody);
			break;

		case NetHouseMsgType.NETAUTH_VERTIFY_RESP:// 验证码的响应
			obj = HandleProToCodeResp(proBufBody);
			break;
		case NetHouseMsgType.NETAUTH_USERLOGIN_RESP: // 登录的响应
			obj = HandleProToLoginResp(proBufBody);
		default:
			break;
		case NetHouseMsgType.NETAUTH_GETBESTSRV_RESP:// 最优服务器的响应
			obj = HandleBestServerResp(proBufBody);
			break;
		}

		return obj;

	}

	/**
	 * 
	 * 根据消息头来分发处理消息--家政服务器
	 * 
	 * @param proBufBody
	 * @return
	 */
	public static Object getParseHouseMsgType(int iMsgType, byte[] proBufBody) {
		Object obj = null;
		switch (iMsgType) {
		case NetHouseMsgType.CMD_HOMENEWS_RESP:// 首页资讯列表
			obj = HandleHomeNewsResp(proBufBody);
			break;
		case NetHouseMsgType.CMD_NEWSLIST_RESP:// 资讯列表响应
			obj = HandleNewsResp(proBufBody);
			break;
		case NetHouseMsgType.CMD_READ_NEWS_RESP:// 阅读资讯列表响应
			obj = HandleReadNewsResp(proBufBody);
			break;

		default:
			break;
		}

		return obj;

	}

	/**
	 * 连接请求响应数据
	 * 
	 * @param proBufBody
	 *            消息体
	 */
	private static NetConnectResp HandleProToConnectResp(byte[] proBufBody) {

		// 建立连接响应的数据体
		NetConnectResp connectReceive = new NetConnectResp();

		try {

			PublicmsgPro.NetConnectResp_PRO_MSG connectResp_PRO_MSG = NetConnectResp_PRO_MSG.parseFrom(proBufBody);

			connectReceive.eResult = connectResp_PRO_MSG.getEResult();// 操作结果（成功/失败）
			// connectReceive.iUserid = connectResp_PRO_MSG.getIUserid();//
			// 用户账号id
			connectReceive.iSrvTime = connectResp_PRO_MSG.getISrvTime();// 服务器的时间

		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}

		return connectReceive;
	}

	private static NetBestServerResp HandleBestServerResp(byte[] proBufBody) {

		// 建立连接响应的数据体
		NetBestServerResp resp = new NetBestServerResp();

		try {

			AuthMsgPro.AUTH_GetServerInfoResp_PRO parse = AUTH_GetServerInfoResp_PRO.parseFrom(proBufBody);

			resp.eResult = parse.getEResult();
			resp.serverList = parse.getServerInfoList();

		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}

		return resp;
	}

	/*
	 * 获取验证码响应
	 */
	private static AuthcodeResp HandleProToCodeResp(byte[] proBufBody) {

		AuthcodeResp resp = new AuthcodeResp();
		try {
			AuthMsgPro.AUTH_GetCodeResp_PRO getCode = AUTH_GetCodeResp_PRO.parseFrom(proBufBody);
			resp.szCode = getCode.getSzCode();
			resp.szAccountID = getCode.getSzAccountID();
			resp.eResult = getCode.getEResult();
		} catch (InvalidProtocolBufferException e1) {
			e1.printStackTrace();
		}
		return resp;
	}

	/**
	 * 登录请求的响应
	 * 
	 * @param proBufBody
	 * @return
	 */
	private static AuthLoginResp HandleProToLoginResp(byte[] proBufBody) {

		AuthLoginResp loginResp = new AuthLoginResp();

		try {
			AuthMsgPro.AUTH_LoginResp_PRO loginResp_PRO = AUTH_LoginResp_PRO.parseFrom(proBufBody);
			loginResp.eResult = loginResp_PRO.getEResult();
			loginResp.iuserid = loginResp_PRO.getIuserid();// userid
			// loginResp.szEmail = loginResp_PRO.getSzEmail();//邮箱
			// loginResp.szPhoneNum = loginResp_PRO.getSzPhoneNum();//手机
			loginResp.szUserNick = loginResp_PRO.getSzUserNick();// 昵称
			loginResp.szSignaTure = loginResp_PRO.getSzSignaTure();// 个性签名
			loginResp.szUserID = loginResp_PRO.getSzUserID();// 用户账号id
			loginResp.iHeadPic = loginResp_PRO.getIHeadPic();// 头像id
			loginResp.szHeadPic = loginResp_PRO.getSzHeadPic();// 头像信息
			loginResp.iVipLevel = loginResp_PRO.getIVipLevel();// VIP等级
			loginResp_PRO.getESex();// 性别
			loginResp.iAreaID = loginResp_PRO.getIAreaID();// 区域id

		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}

		return loginResp;
	}

	/*
	 * 获取首页资讯响应
	 */
	private static HomeNewsResp HandleHomeNewsResp(byte[] proBufBody) {

		MsgReceiveDef.HomeNewsResp resp = new MsgReceiveDef.HomeNewsResp();
		try {
			CmdMsgPro.CMD_GetHomeNewsResp_PRO homeNewsResp_PRO = CMD_GetHomeNewsResp_PRO.parseFrom(proBufBody);
			resp.info = homeNewsResp_PRO.getInfoList();

		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
		return resp;
	}

	/*
	 * 获取资讯列表 响应
	 */
	private static NewsResp HandleNewsResp(byte[] proBufBody) {

		MsgReceiveDef.NewsResp resp = new MsgReceiveDef.NewsResp();
		try {
			CmdMsgPro.CMD_GetNewsResp_PRO newsResp_PRO = CMD_GetNewsResp_PRO.parseFrom(proBufBody);
			resp.info = newsResp_PRO.getInfoList();
			resp.eResult = newsResp_PRO.getEResult();
			resp.szTime=newsResp_PRO.getSzTime();
			resp.ePageType=newsResp_PRO.getEPageType();

		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
		return resp;
	}

	/*
	 * 获取阅读资讯 响应
	 */
	private static ReadNewsResp HandleReadNewsResp(byte[] proBufBody) {

		MsgReceiveDef.ReadNewsResp resp = new MsgReceiveDef.ReadNewsResp();
		try {
			CmdMsgPro.CMD_ReadNewsResp_PRO newsResp_PRO = CMD_ReadNewsResp_PRO.parseFrom(proBufBody);
			resp.newsId = newsResp_PRO.getInewsid();

		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
		return resp;
	}

	// ----------------------------以下为通用响应------------------------------------------//
	/**
	 * 登录认证--通用响应--只需要获得操作结果的，一律使用该消息
	 */
	private static AuthNetCommonResp HandleProToAuthCommonResp(byte[] proBufBody) {

		AuthNetCommonResp commonResp = new AuthNetCommonResp();

		try {
			PublicmsgPro.Net_CommonResp_PRO_MSG commonResp_PRO_MSG = Net_CommonResp_PRO_MSG.parseFrom(proBufBody);
			commonResp.eResult = commonResp_PRO_MSG.getEResult();
			commonResp.iUserid = commonResp_PRO_MSG.getIUserid();// 用户id

		} catch (InvalidProtocolBufferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return commonResp;
	}

}
