package com.sltj.medical.dataUtil;

import com.sltj.medical.dataUtil.protobuf.AuthMsgPro;
import com.sltj.medical.dataUtil.protobuf.AuthMsgPro.AUTH_GetCodeReq_PRO;
import com.sltj.medical.dataUtil.protobuf.AuthMsgPro.AUTH_GetServerInfoReq_PRO;
import com.sltj.medical.dataUtil.protobuf.AuthMsgPro.AUTH_LoginReq_PRO;
import com.sltj.medical.dataUtil.protobuf.CmdMsgPro;
import com.sltj.medical.dataUtil.protobuf.CmdMsgPro.CMD_AddMoodReq_PRO;
import com.sltj.medical.dataUtil.protobuf.CmdMsgPro.CMD_ChatReq_PRO;
import com.sltj.medical.dataUtil.protobuf.CmdMsgPro.CMD_CollectionNewsReq_PRO;
import com.sltj.medical.dataUtil.protobuf.CmdMsgPro.CMD_GetDoctorInfoReq_PRO;
import com.sltj.medical.dataUtil.protobuf.CmdMsgPro.CMD_GetHomeNewsReq_PRO;
import com.sltj.medical.dataUtil.protobuf.CmdMsgPro.CMD_GetMyDoctorListReq_PRO;
import com.sltj.medical.dataUtil.protobuf.CmdMsgPro.CMD_GetNewsCommentListReq_PRO;
import com.sltj.medical.dataUtil.protobuf.CmdMsgPro.CMD_GetNewsReq_PRO;
import com.sltj.medical.dataUtil.protobuf.CmdMsgPro.CMD_GetRecoderReq_PRO;
import com.sltj.medical.dataUtil.protobuf.CmdMsgPro.CMD_Get_Mood_RecordReq_PRO;
import com.sltj.medical.dataUtil.protobuf.CmdMsgPro.CMD_Get_Tijian_InfoReq_PRO;
import com.sltj.medical.dataUtil.protobuf.CmdMsgPro.CMD_Get_Tijian_RecordReq_PRO;
import com.sltj.medical.dataUtil.protobuf.CmdMsgPro.CMD_Get_Yongyao_InfoReq_PRO;
import com.sltj.medical.dataUtil.protobuf.CmdMsgPro.CMD_Get_Yongyao_RecordReq_PRO;
import com.sltj.medical.dataUtil.protobuf.CmdMsgPro.CMD_Get_Zhiliao_InfoReq_PRO;
import com.sltj.medical.dataUtil.protobuf.CmdMsgPro.CMD_ReadNewsReq_PRO;
import com.sltj.medical.dataUtil.protobuf.CmdMsgPro.CMD_SetNewsCommentReq_PRO;
import com.sltj.medical.dataUtil.protobuf.CmdMsgPro.CMD_SetNewsCommentSupportReq_PRO;
import com.sltj.medical.dataUtil.protobuf.EnumPro;
import com.sltj.medical.dataUtil.protobuf.EnumPro.eLOGIN_TYPE_PRO;
import com.sltj.medical.dataUtil.protobuf.EnumPro.eNEWS_PRO;
import com.sltj.medical.dataUtil.protobuf.EnumPro.ePAGE_TYPE_PRO;
import com.sltj.medical.dataUtil.protobuf.PublicmsgPro;
import com.sltj.medical.dataUtil.protobuf.PublicmsgPro.NetConnectReq_PRO_Msg;
import com.sltj.medical.publicMsg.MsgInncDef;
import com.sltj.medical.publicMsg.NetHouseMsgType;
import com.sltj.medical.util.MTools;

/**
 * 数据处理类 将需要发送的数据转为protobuf
 */
public class HandleNetSendMsg {
	/**
	 * 建立连接请求 --
	 */
	public static byte[] HandleConnectToPro(MsgInncDef.NetConnectReq netConn, int sequence) {
		// 组织序列化消息
		PublicmsgPro.NetConnectReq_PRO_Msg.Builder builder = PublicmsgPro.NetConnectReq_PRO_Msg.newBuilder();
		builder.setSzAuthInfo(netConn.SzAuthInfo);// 认证信息
		builder.setSzCrcInfo(netConn.szCrcInfo); // 加密循环校验
		// 将消息内容建立
		NetConnectReq_PRO_Msg connectReq_PRO_Msg = builder.build();
		// 将消息序列化
		byte[] msgProBody = connectReq_PRO_Msg.toByteArray();
		// 消息的长度
		int msgLength = connectReq_PRO_Msg.getSerializedSize();
		// 构建消息头
		HandleNetHeadMsg headMsg = new HandleNetHeadMsg();
		byte[] msgByteHead = headMsg.buildHeadMsg(NetHouseMsgType.NETCMD_CONNECT_REQ, msgLength, msgLength, sequence,
				0);
		// 拼接消息头和消息体
		final byte[] msgByteArray = MTools.copyByteArray(msgByteHead, msgProBody);
		// 发送消息
		return msgByteArray;

	}

	/**
	 * 获取验证码
	 * 
	 * @param getVertification
	 * @param sequence
	 * @return
	 */
	public static byte[] HandleVerficationPro(MsgInncDef.GetVerification getVertification, int sequence) {

		// 组织序列化消息
		AuthMsgPro.AUTH_GetCodeReq_PRO.Builder builder = AuthMsgPro.AUTH_GetCodeReq_PRO.newBuilder();
		builder.setSzAccountID(getVertification.szAccountID);

		EnumPro.eLOGIN_TYPE_PRO eAccountType = EnumPro.eLOGIN_TYPE_PRO.valueOf(getVertification.eAccountType);
		builder.setEAccountType(eAccountType);
		// 将消息内容建立
		AUTH_GetCodeReq_PRO getCode = builder.build();
		// 将消息序列化
		byte[] msgProBody = getCode.toByteArray();
		// 消息的长度
		int msgLength = getCode.getSerializedSize();
		// 构建消息头
		HandleNetHeadMsg headMsg = new HandleNetHeadMsg();
		byte[] msgByteHead = headMsg.buildHeadMsg(NetHouseMsgType.NETAUTH_VERTIFY_REQ, msgLength, msgLength, sequence,
				0);
		// 拼接消息头和消息体
		byte[] msgByteArray = MTools.copyByteArray(msgByteHead, msgProBody);

		// 发送消息
		return msgByteArray;

	}

	/**
	 * 登录请求消息
	 */
	public static byte[] HandleLoginToPro(MsgInncDef.LoginReq loginMsg, int sequence) {

		// 组织序列化消息
		AuthMsgPro.AUTH_LoginReq_PRO.Builder builder = AuthMsgPro.AUTH_LoginReq_PRO.newBuilder();

		// builder.setEAccountType(EnumPro.eACCOUNTTYPE_PRO.en_ACCOUNT_PHONE_PRO);//
		// 账号类型
		builder.setSzAccountID(loginMsg.strAccountID);// 登录的账号，更新账号类型，这里的值也不一样
		builder.setSzPassword(loginMsg.strPasswd);// md5后的密码
		builder.setDeviceID("1");
		builder.setEAccountType(eLOGIN_TYPE_PRO.valueOf(loginMsg.type));

		// 建立消息内容
		AUTH_LoginReq_PRO loginReq_PRO = builder.build();
		// 消息长度
		int msgLength = loginReq_PRO.getSerializedSize();
		// 将消息序列化
		byte[] msgProBody = loginReq_PRO.toByteArray();

		// 构建消息头
		HandleNetHeadMsg headMsg = new HandleNetHeadMsg();
		byte[] msgByteHead = headMsg.buildHeadMsg(NetHouseMsgType.NETAUTH_USERLOGIN_REQ, msgLength, msgLength, sequence,
				0);

		// 拼接消息头和消息体
		final byte[] msgByteArray = MTools.copyByteArray(msgByteHead, msgProBody);

		return msgByteArray;
	}

	/**
	 * 首页资讯消息
	 */
	public static byte[] HandleHomeNewsPro(MsgInncDef.IhomeNewsReq homeNewsParam, int sequence) {

		// 组织序列化消息
		CmdMsgPro.CMD_GetHomeNewsReq_PRO.Builder builder = CmdMsgPro.CMD_GetHomeNewsReq_PRO.newBuilder();

		builder.setIuserid(homeNewsParam.iUserId);
		// 建立消息内容
		CMD_GetHomeNewsReq_PRO homeNews_PRO = builder.build();
		// 消息长度
		int msgLength = homeNews_PRO.getSerializedSize();
		// 将消息序列化
		byte[] msgProBody = homeNews_PRO.toByteArray();

		// 构建消息头
		HandleNetHeadMsg headMsg = new HandleNetHeadMsg();
		// 复制时此处不要忘记修改了=======
		byte[] msgByteHead = headMsg.buildHeadMsg(NetHouseMsgType.CMD_HOMENEWS_REQ, msgLength, msgLength, sequence, 0);

		// 拼接消息头和消息体
		final byte[] msgByteArray = MTools.copyByteArray(msgByteHead, msgProBody);

		return msgByteArray;
	}

	/**
	 * 资讯列表消息
	 */
	public static byte[] HandleNewsPro(MsgInncDef.INewsReq newsParam, int sequence) {

		// 组织序列化消息
		CmdMsgPro.CMD_GetNewsReq_PRO.Builder builder = CmdMsgPro.CMD_GetNewsReq_PRO.newBuilder();

		builder.setIuserid(newsParam.iUserId);
		builder.setEType(eNEWS_PRO.valueOf(newsParam.eType));
		builder.setSzTime(newsParam.szTime);
		builder.setEPageType(ePAGE_TYPE_PRO.valueOf(newsParam.ePageType));
		// 建立消息内容
		CMD_GetNewsReq_PRO news_PRO = builder.build();
		// 消息长度
		int msgLength = news_PRO.getSerializedSize();
		// 将消息序列化
		byte[] msgProBody = news_PRO.toByteArray();

		// 构建消息头
		HandleNetHeadMsg headMsg = new HandleNetHeadMsg();
		// 复制时此处不要忘记修改了=======
		byte[] msgByteHead = headMsg.buildHeadMsg(NetHouseMsgType.CMD_NEWSLIST_REQ, msgLength, msgLength, sequence, 0);

		// 拼接消息头和消息体
		final byte[] msgByteArray = MTools.copyByteArray(msgByteHead, msgProBody);

		return msgByteArray;
	}

	/**
	 * 读取某条资讯消息
	 */
	public static byte[] HandleReadNewsPro(MsgInncDef.IReadNewsReq Param, int sequence) {

		// 组织序列化消息
		CmdMsgPro.CMD_ReadNewsReq_PRO.Builder builder = CmdMsgPro.CMD_ReadNewsReq_PRO.newBuilder();

		builder.setIuserid(Param.iUserId);
		builder.setInewsid(Param.inewsid);// 资讯ID
		// 建立消息内容
		CMD_ReadNewsReq_PRO news_PRO = builder.build();
		// 消息长度
		int msgLength = news_PRO.getSerializedSize();
		// 将消息序列化
		byte[] msgProBody = news_PRO.toByteArray();

		// 构建消息头
		HandleNetHeadMsg headMsg = new HandleNetHeadMsg();
		// 复制时此处不要忘记修改了=======
		byte[] msgByteHead = headMsg.buildHeadMsg(NetHouseMsgType.CMD_READ_NEWS_REQ, msgLength, msgLength, sequence, 0);

		// 拼接消息头和消息体
		final byte[] msgByteArray = MTools.copyByteArray(msgByteHead, msgProBody);

		return msgByteArray;
	}

	/**
	 * 用户评论请求
	 */
	public static byte[] HandleUserCommentPro(MsgInncDef.IUserCommentReq Param, int sequence) {

		// 组织序列化消息
		CmdMsgPro.CMD_SetNewsCommentReq_PRO.Builder builder = CmdMsgPro.CMD_SetNewsCommentReq_PRO.newBuilder();

		builder.setIuserid(Param.iUserId);
		builder.setInewsid(Param.inewsid);// 资讯ID
		builder.setSzcomment(Param.szComment);
		// 建立消息内容
		CMD_SetNewsCommentReq_PRO news_PRO = builder.build();
		// 消息长度
		int msgLength = news_PRO.getSerializedSize();
		// 将消息序列化
		byte[] msgProBody = news_PRO.toByteArray();

		// 构建消息头
		HandleNetHeadMsg headMsg = new HandleNetHeadMsg();
		// 复制时此处不要忘记修改了=======
		byte[] msgByteHead = headMsg.buildHeadMsg(NetHouseMsgType.CMD_PUSH_COMMENT_REQ, msgLength, msgLength, sequence,
				0);
		// 拼接消息头和消息体
		final byte[] msgByteArray = MTools.copyByteArray(msgByteHead, msgProBody);

		return msgByteArray;
	}

	/**
	 * 获取评论列表请求
	 */
	public static byte[] HandleUserCommentListPro(MsgInncDef.ICommenListReq Param, int sequence) {

		// 组织序列化消息
		CmdMsgPro.CMD_GetNewsCommentListReq_PRO.Builder builder = CmdMsgPro.CMD_GetNewsCommentListReq_PRO.newBuilder();

		builder.setIuserid(Param.iUserId);
		builder.setInewsid(Param.inewsid);// 资讯ID
		// 建立消息内容
		CMD_GetNewsCommentListReq_PRO news_PRO = builder.build();
		// 消息长度
		int msgLength = news_PRO.getSerializedSize();
		// 将消息序列化
		byte[] msgProBody = news_PRO.toByteArray();

		// 构建消息头
		HandleNetHeadMsg headMsg = new HandleNetHeadMsg();
		// 复制时此处不要忘记修改了=======
		byte[] msgByteHead = headMsg.buildHeadMsg(NetHouseMsgType.CMD_GET_COMMENT_REQ, msgLength, msgLength, sequence,
				0);
		// 拼接消息头和消息体
		final byte[] msgByteArray = MTools.copyByteArray(msgByteHead, msgProBody);

		return msgByteArray;
	}

	/**
	 *评论点赞请求
	 */
	public static byte[] HandleCommentZanPro(MsgInncDef.ICommenZanReq Param, int sequence) {

		// 组织序列化消息
		CmdMsgPro.CMD_SetNewsCommentSupportReq_PRO.Builder builder = CmdMsgPro.CMD_SetNewsCommentSupportReq_PRO
				.newBuilder();

		builder.setIuserid(Param.iUserId);
		builder.setIcommentid(Param.iCommentid);// 	//评论ID
		// 建立消息内容
		CMD_SetNewsCommentSupportReq_PRO news_PRO = builder.build();
		// 消息长度
		int msgLength = news_PRO.getSerializedSize();
		// 将消息序列化
		byte[] msgProBody = news_PRO.toByteArray();

		// 构建消息头
		HandleNetHeadMsg headMsg = new HandleNetHeadMsg();
		// 复制时此处不要忘记修改了=======
		byte[] msgByteHead = headMsg.buildHeadMsg(NetHouseMsgType.CMD_GET_COMMENTZAN_REQ, msgLength, msgLength, sequence,
				0);
		// 拼接消息头和消息体
		final byte[] msgByteArray = MTools.copyByteArray(msgByteHead, msgProBody);

		return msgByteArray;
	}
	/**
	 *收藏资讯请求
	 */
	public static byte[] HandleCollectNewsPro(MsgInncDef.ICollectNewsReq Param, int sequence) {

		// 组织序列化消息
		CmdMsgPro.CMD_CollectionNewsReq_PRO.Builder builder = CmdMsgPro.CMD_CollectionNewsReq_PRO
				.newBuilder();

		builder.setIuserid(Param.iUserId);
		builder.setInewsid(Param.inewsid);
		builder.setType(Param.collectType);
		// 建立消息内容
		CMD_CollectionNewsReq_PRO news_PRO = builder.build();
		// 消息长度
		int msgLength = news_PRO.getSerializedSize();
		// 将消息序列化
		byte[] msgProBody = news_PRO.toByteArray();

		// 构建消息头
		HandleNetHeadMsg headMsg = new HandleNetHeadMsg();
		// 复制时此处不要忘记修改了=======
		byte[] msgByteHead = headMsg.buildHeadMsg(NetHouseMsgType.CMD_COLLECT_NEWS_REQ, msgLength, msgLength, sequence,
				0);
		// 拼接消息头和消息体
		final byte[] msgByteArray = MTools.copyByteArray(msgByteHead, msgProBody);

		return msgByteArray;
	}

	/**
	 * 获取最优服务器请求
	 */
	public static byte[] HandleBestServerPro(MsgInncDef.AuthNetCommonReq Param, int sequence) {

		// 组织序列化消息
		AuthMsgPro.AUTH_GetServerInfoReq_PRO.Builder builder = AuthMsgPro.AUTH_GetServerInfoReq_PRO.newBuilder();

		builder.setIuserid(Param.iUserid);
		// 建立消息内容
		AUTH_GetServerInfoReq_PRO pro = builder.build();
		// 消息长度
		int msgLength = pro.getSerializedSize();
		// 将消息序列化
		byte[] msgProBody = pro.toByteArray();

		// 构建消息头
		HandleNetHeadMsg headMsg = new HandleNetHeadMsg();
		// 复制时此处不要忘记修改了=======
		byte[] msgByteHead = headMsg.buildHeadMsg(NetHouseMsgType.NETAUTH_GETBESTSRV_REQ, msgLength, msgLength,
				sequence, 0);

		// 拼接消息头和消息体
		final byte[] msgByteArray = MTools.copyByteArray(msgByteHead, msgProBody);

		return msgByteArray;
	}

	/**
	 * 体检记录请求
	 */
	public static byte[] HandlePhysicalRecordPro(MsgInncDef.IphsicalRecordReq Param, int sequence) {

		// 组织序列化消息
		CmdMsgPro.CMD_Get_Tijian_RecordReq_PRO.Builder builder = CmdMsgPro.CMD_Get_Tijian_RecordReq_PRO.newBuilder();

		builder.setIuserid(Param.iUserId);
		builder.setEPageType(Param.ePageType);
		builder.setIRecoderNum(10);
		builder.setIBeforRecoderid(Param.iBeforRecoderid);
		builder.setSzBeforTime(Param.szBeforTime);
		// 建立消息内容
		CMD_Get_Tijian_RecordReq_PRO pro = builder.build();
		// 消息长度
		int msgLength = pro.getSerializedSize();
		// 将消息序列化
		byte[] msgProBody = pro.toByteArray();

		// 构建消息头
		HandleNetHeadMsg headMsg = new HandleNetHeadMsg();
		// 复制时此处不要忘记修改了=======
		byte[] msgByteHead = headMsg.buildHeadMsg(NetHouseMsgType.CMD_GET_PHYSICAL_REQ, msgLength, msgLength, sequence,
				0);

		// 拼接消息头和消息体
		final byte[] msgByteArray = MTools.copyByteArray(msgByteHead, msgProBody);

		return msgByteArray;
	}

	/**
	 * 治疗记录请求
	 */
	public static byte[] HandleTreatRecordPro(MsgInncDef.IphsicalRecordReq Param, int sequence) {

		// 组织序列化消息
		CmdMsgPro.CMD_Get_Tijian_RecordReq_PRO.Builder builder = CmdMsgPro.CMD_Get_Tijian_RecordReq_PRO.newBuilder();

		builder.setIuserid(Param.iUserId);
		builder.setEPageType(Param.ePageType);
		builder.setIRecoderNum(10);
		builder.setIBeforRecoderid(Param.iBeforRecoderid);
		builder.setSzBeforTime(Param.szBeforTime);
		// 建立消息内容
		CMD_Get_Tijian_RecordReq_PRO pro = builder.build();
		// 消息长度
		int msgLength = pro.getSerializedSize();
		// 将消息序列化
		byte[] msgProBody = pro.toByteArray();

		// 构建消息头
		HandleNetHeadMsg headMsg = new HandleNetHeadMsg();
		// 复制时此处不要忘记修改了=======
		byte[] msgByteHead = headMsg.buildHeadMsg(NetHouseMsgType.CMD_GET_TREAT_REQ, msgLength, msgLength, sequence, 0);

		// 拼接消息头和消息体
		final byte[] msgByteArray = MTools.copyByteArray(msgByteHead, msgProBody);

		return msgByteArray;
	}

	/*
	 * 用药记录列表 请求
	 */
	public static byte[] HandleMedicationsRecordPro(MsgInncDef.IphsicalRecordReq Param, int sequence) {

		// 组织序列化消息
		CmdMsgPro.CMD_Get_Yongyao_RecordReq_PRO.Builder builder = CmdMsgPro.CMD_Get_Yongyao_RecordReq_PRO.newBuilder();

		builder.setIuserid(Param.iUserId);
		builder.setEPageType(Param.ePageType);
		builder.setIBeforRecoderid(Param.iBeforRecoderid);
		builder.setIRecoderNum(Param.iRecoderNum);
		builder.setSzBeforTime(Param.szBeforTime);

		// 建立消息内容
		CMD_Get_Yongyao_RecordReq_PRO pro = builder.build();
		// 消息长度
		int msgLength = pro.getSerializedSize();
		// 将消息序列化
		byte[] msgProBody = pro.toByteArray();

		// 构建消息头
		HandleNetHeadMsg headMsg = new HandleNetHeadMsg();
		// 复制时此处不要忘记修改了=======
		byte[] msgByteHead = headMsg.buildHeadMsg(NetHouseMsgType.CMD_GET_MEDICATIONS__REQ, msgLength, msgLength,
				sequence, 0);

		// 拼接消息头和消息体
		final byte[] msgByteArray = MTools.copyByteArray(msgByteHead, msgProBody);

		return msgByteArray;
	}

	/**
	 * 用药记录祥情请求
	 */
	public static byte[] HandleMedicationsRecordDetailPro(MsgInncDef.ITreatRecordDetailReq Param, int sequence) {

		// 组织序列化消息
		CmdMsgPro.CMD_Get_Yongyao_InfoReq_PRO.Builder builder = CmdMsgPro.CMD_Get_Yongyao_InfoReq_PRO.newBuilder();

		builder.setIuserid(Param.IUserId);
		builder.setIRecordIndex(Param.iRecordIndex);
		// 建立消息内容
		CMD_Get_Yongyao_InfoReq_PRO pro = builder.build();
		// 消息长度
		int msgLength = pro.getSerializedSize();
		// 将消息序列化
		byte[] msgProBody = pro.toByteArray();

		// 构建消息头
		HandleNetHeadMsg headMsg = new HandleNetHeadMsg();
		// 复制时此处不要忘记修改了=======
		byte[] msgByteHead = headMsg.buildHeadMsg(NetHouseMsgType.CMD_GET_MEDICATIONSDETAIL_REQ, msgLength, msgLength,
				sequence, 0);

		// 拼接消息头和消息体
		final byte[] msgByteArray = MTools.copyByteArray(msgByteHead, msgProBody);

		return msgByteArray;
	}

	/**
	 * 体检记录祥情请求
	 */
	public static byte[] HandlePhysicalRecordDetailPro(MsgInncDef.IPhsicalRecordDetailReq Param, int sequence) {

		// 组织序列化消息
		CmdMsgPro.CMD_Get_Tijian_InfoReq_PRO.Builder builder = CmdMsgPro.CMD_Get_Tijian_InfoReq_PRO.newBuilder();

		builder.setIuserid(Param.IUserId);
		builder.setIRecordIndex(Param.iRecordIndex);
		// 建立消息内容
		CMD_Get_Tijian_InfoReq_PRO pro = builder.build();
		// 消息长度
		int msgLength = pro.getSerializedSize();
		// 将消息序列化
		byte[] msgProBody = pro.toByteArray();

		// 构建消息头
		HandleNetHeadMsg headMsg = new HandleNetHeadMsg();
		// 复制时此处不要忘记修改了=======
		byte[] msgByteHead = headMsg.buildHeadMsg(NetHouseMsgType.CMD_GET_PHYSICALDETAIL_REQ, msgLength, msgLength,
				sequence, 0);

		// 拼接消息头和消息体
		final byte[] msgByteArray = MTools.copyByteArray(msgByteHead, msgProBody);

		return msgByteArray;
	}

	/**
	 * 治疗记录祥情请求
	 */
	public static byte[] HandleTreatRecordDetailPro(MsgInncDef.ITreatRecordDetailReq Param, int sequence) {

		// 组织序列化消息
		CmdMsgPro.CMD_Get_Zhiliao_InfoReq_PRO.Builder builder = CmdMsgPro.CMD_Get_Zhiliao_InfoReq_PRO.newBuilder();

		builder.setIuserid(Param.IUserId);
		builder.setIRecordIndex(Param.iRecordIndex);
		// 建立消息内容
		CMD_Get_Zhiliao_InfoReq_PRO pro = builder.build();
		// 消息长度
		int msgLength = pro.getSerializedSize();
		// 将消息序列化
		byte[] msgProBody = pro.toByteArray();

		// 构建消息头
		HandleNetHeadMsg headMsg = new HandleNetHeadMsg();
		// 复制时此处不要忘记修改了=======
		byte[] msgByteHead = headMsg.buildHeadMsg(NetHouseMsgType.CMD_GET_TREATDETAIL_REQ, msgLength, msgLength,
				sequence, 0);

		// 拼接消息头和消息体
		final byte[] msgByteArray = MTools.copyByteArray(msgByteHead, msgProBody);

		return msgByteArray;
	}

	/**
	 * 心情列表请求
	 */
	public static byte[] HandleMoodRecordPro(MsgInncDef.IMoodRecordReq Param, int sequence) {

		// 组织序列化消息
		CmdMsgPro.CMD_Get_Mood_RecordReq_PRO.Builder builder = CmdMsgPro.CMD_Get_Mood_RecordReq_PRO.newBuilder();

		builder.setIuserid(Param.iUserId);
		builder.setSzBeforTime(Param.szBeforTime);
		builder.setIBeforRecoderid(Param.iBeforRecoderid);
		builder.setIRecoderNum(Param.iRecoderNum);
		// 建立消息内容
		CMD_Get_Mood_RecordReq_PRO pro = builder.build();
		// 消息长度
		int msgLength = pro.getSerializedSize();
		// 将消息序列化
		byte[] msgProBody = pro.toByteArray();

		// 构建消息头
		HandleNetHeadMsg headMsg = new HandleNetHeadMsg();
		// 复制时此处不要忘记修改了=======
		byte[] msgByteHead = headMsg.buildHeadMsg(NetHouseMsgType.CMD_GET_MOOD_REQ, msgLength, msgLength, sequence, 0);

		// 拼接消息头和消息体
		final byte[] msgByteArray = MTools.copyByteArray(msgByteHead, msgProBody);

		return msgByteArray;
	}

	/**
	 * 添加心情请求
	 */
	public static byte[] HandleAddMoodPro(MsgInncDef.IMoodAddReq Param, int sequence) {

		// 组织序列化消息
		CmdMsgPro.CMD_AddMoodReq_PRO.Builder builder = CmdMsgPro.CMD_AddMoodReq_PRO.newBuilder();

		builder.setIuserid(Param.iuserid);
		builder.setIgrade(Param.iGrade);
		builder.setSzContent(Param.szContent);
		// 建立消息内容
		CMD_AddMoodReq_PRO pro = builder.build();
		// 消息长度
		int msgLength = pro.getSerializedSize();
		// 将消息序列化
		byte[] msgProBody = pro.toByteArray();

		// 构建消息头
		HandleNetHeadMsg headMsg = new HandleNetHeadMsg();
		// 复制时此处不要忘记修改了=======
		byte[] msgByteHead = headMsg.buildHeadMsg(NetHouseMsgType.CMD_ADD_MOOD_REQ, msgLength, msgLength, sequence, 0);

		// 拼接消息头和消息体
		final byte[] msgByteArray = MTools.copyByteArray(msgByteHead, msgProBody);

		return msgByteArray;
	}
	/**
	 * 我的医生列表
	 */
	public static byte[] HandleMyDoctorListPro(MsgInncDef.IMyDoctorListReq Param, int sequence) {

		// 组织序列化消息
		CmdMsgPro.CMD_GetMyDoctorListReq_PRO.Builder builder = CmdMsgPro.CMD_GetMyDoctorListReq_PRO.newBuilder();

		builder.setIuserid(Param.iuserid);
		builder.setIPage(Param.iPage);
		// 建立消息内容
		CMD_GetMyDoctorListReq_PRO pro = builder.build();
		// 消息长度
		int msgLength = pro.getSerializedSize();
		// 将消息序列化
		byte[] msgProBody = pro.toByteArray();

		// 构建消息头
		HandleNetHeadMsg headMsg = new HandleNetHeadMsg();
		// 复制时此处不要忘记修改了=======
		byte[] msgByteHead = headMsg.buildHeadMsg(NetHouseMsgType.CMD_GET_DOCTOR_REQ, msgLength, msgLength, sequence, 0);

		// 拼接消息头和消息体
		final byte[] msgByteArray = MTools.copyByteArray(msgByteHead, msgProBody);

		return msgByteArray;
	}
	/**
	 * 我的医生祥细信息
	 */
	public static byte[] HandleDoctorInfoPro(MsgInncDef.IDoctorInfoReq Param, int sequence) {
		
		// 组织序列化消息
		CmdMsgPro.CMD_GetDoctorInfoReq_PRO.Builder builder = CmdMsgPro.CMD_GetDoctorInfoReq_PRO.newBuilder();
		
		builder.setIuserid(Param.iuserid);
		builder.setIdoctorid(Param.iDoctorId);
		// 建立消息内容
		CMD_GetDoctorInfoReq_PRO pro = builder.build();
		// 消息长度
		int msgLength = pro.getSerializedSize();
		// 将消息序列化
		byte[] msgProBody = pro.toByteArray();
		
		// 构建消息头
		HandleNetHeadMsg headMsg = new HandleNetHeadMsg();
		// 复制时此处不要忘记修改了=======
		byte[] msgByteHead = headMsg.buildHeadMsg(NetHouseMsgType.CMD_DOCTORDETAIL_REQ, msgLength, msgLength, sequence, 0);
		
		// 拼接消息头和消息体
		final byte[] msgByteArray = MTools.copyByteArray(msgByteHead, msgProBody);
		
		return msgByteArray;
	}
	/**
	 * 聊天请求
	 */
	public static byte[] HandleChatInfoPro(MsgInncDef.IChatReq Param, int sequence) {
		
		// 组织序列化消息
		CmdMsgPro.CMD_ChatReq_PRO.Builder builder = CmdMsgPro.CMD_ChatReq_PRO.newBuilder();
		
		builder.setIuserid(Param.iuserid);
		builder.setIdstid(Param.idstid);
		builder.setSzContent(Param.content);
		builder.setType(Param.type);
		// 建立消息内容
		CMD_ChatReq_PRO pro = builder.build();
		// 消息长度
		int msgLength = pro.getSerializedSize();
		// 将消息序列化
		byte[] msgProBody = pro.toByteArray();
		
		// 构建消息头
		HandleNetHeadMsg headMsg = new HandleNetHeadMsg();
		// 复制时此处不要忘记修改了=======
		byte[] msgByteHead = headMsg.buildHeadMsg(NetHouseMsgType.CMD_GET_CHAT_REQ, msgLength, msgLength, sequence, 0);
		
		// 拼接消息头和消息体
		final byte[] msgByteArray = MTools.copyByteArray(msgByteHead, msgProBody);
		
		return msgByteArray;
	}
	/**
	 * 历史记录请求
	 */
	public static byte[] HandleChatHistoryPro(MsgInncDef.IChatHistoryReq Param, int sequence) {
		
		// 组织序列化消息
		CmdMsgPro.CMD_GetRecoderReq_PRO.Builder builder = CmdMsgPro.CMD_GetRecoderReq_PRO.newBuilder();
		
		builder.setIuserid(Param.iuserid);
		builder.setIdstid(Param.idstid);
		builder.setEPageType(Param.ePageType);
		builder.setSzBeforTime(Param.szBeforTime);
		builder.setIBeforRecoderid(Param.iBeforRecoderid);
		builder.setIRecoderNum(Param.iRecoderNum);
		// 建立消息内容
		CMD_GetRecoderReq_PRO pro = builder.build();
		// 消息长度
		int msgLength = pro.getSerializedSize();
		// 将消息序列化
		byte[] msgProBody = pro.toByteArray();
		
		// 构建消息头
		HandleNetHeadMsg headMsg = new HandleNetHeadMsg();
		// 复制时此处不要忘记修改了=======
		byte[] msgByteHead = headMsg.buildHeadMsg(NetHouseMsgType.CMD_GET_HISTORY_REQ, msgLength, msgLength, sequence, 0);
		
		// 拼接消息头和消息体
		final byte[] msgByteArray = MTools.copyByteArray(msgByteHead, msgProBody);
		
		return msgByteArray;
	}
	/**
	 * 获取离线消息请求
	 */
	public static byte[] HandleOffLinePro(MsgInncDef.AuthNetCommonReq Param, int sequence) {
		
		// 组织序列化消息
		CmdMsgPro.CMD_ChatReq_PRO.Builder builder = CmdMsgPro.CMD_ChatReq_PRO.newBuilder();
		
		builder.setIuserid(Param.iUserid);
		// 建立消息内容
		CMD_ChatReq_PRO pro = builder.build();
		// 消息长度
		int msgLength = pro.getSerializedSize();
		// 将消息序列化
		byte[] msgProBody = pro.toByteArray();
		
		// 构建消息头
		HandleNetHeadMsg headMsg = new HandleNetHeadMsg();
		// 复制时此处不要忘记修改了=======
		byte[] msgByteHead = headMsg.buildHeadMsg(NetHouseMsgType.CMD_GET_OFF_LINE_REQ, msgLength, msgLength, sequence, 0);
		
		// 拼接消息头和消息体
		final byte[] msgByteArray = MTools.copyByteArray(msgByteHead, msgProBody);
		
		return msgByteArray;
	}

}
