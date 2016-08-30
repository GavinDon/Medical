package com.sltj.medical.dataUtil;

import com.google.protobuf.InvalidProtocolBufferException;
import com.sltj.medical.dataUtil.protobuf.AuthMsgPro;
import com.sltj.medical.dataUtil.protobuf.AuthMsgPro.AUTH_GetCodeResp_PRO;
import com.sltj.medical.dataUtil.protobuf.AuthMsgPro.AUTH_GetServerInfoResp_PRO;
import com.sltj.medical.dataUtil.protobuf.AuthMsgPro.AUTH_LoginResp_PRO;
import com.sltj.medical.dataUtil.protobuf.CmdMsgPro;
import com.sltj.medical.dataUtil.protobuf.CmdMsgPro.CMD_AddMoodResp_PRO;
import com.sltj.medical.dataUtil.protobuf.CmdMsgPro.CMD_ChatResp_PRO;
import com.sltj.medical.dataUtil.protobuf.CmdMsgPro.CMD_CollectionNewsResp_PRO;
import com.sltj.medical.dataUtil.protobuf.CmdMsgPro.CMD_GetDoctorInfoResp_PRO;
import com.sltj.medical.dataUtil.protobuf.CmdMsgPro.CMD_GetHomeNewsResp_PRO;
import com.sltj.medical.dataUtil.protobuf.CmdMsgPro.CMD_GetMyDoctorListResp_PRO;
import com.sltj.medical.dataUtil.protobuf.CmdMsgPro.CMD_GetNewsCommentListResp_PRO;
import com.sltj.medical.dataUtil.protobuf.CmdMsgPro.CMD_GetNewsResp_PRO;
import com.sltj.medical.dataUtil.protobuf.CmdMsgPro.CMD_GetOffLineMessageResp_PRO;
import com.sltj.medical.dataUtil.protobuf.CmdMsgPro.CMD_GetRecoderResp_PRO;
import com.sltj.medical.dataUtil.protobuf.CmdMsgPro.CMD_Get_Mood_RecordResp_PRO;
import com.sltj.medical.dataUtil.protobuf.CmdMsgPro.CMD_Get_Tijian_InfoResp_PRO;
import com.sltj.medical.dataUtil.protobuf.CmdMsgPro.CMD_Get_Tijian_RecordResp_PRO;
import com.sltj.medical.dataUtil.protobuf.CmdMsgPro.CMD_Get_Yongyao_InfoResp_PRO;
import com.sltj.medical.dataUtil.protobuf.CmdMsgPro.CMD_Get_Yongyao_RecordResp_PRO;
import com.sltj.medical.dataUtil.protobuf.CmdMsgPro.CMD_Get_Zhiliao_InfoResp_PRO;
import com.sltj.medical.dataUtil.protobuf.CmdMsgPro.CMD_Get_Zhiliao_RecordResp_PRO;
import com.sltj.medical.dataUtil.protobuf.CmdMsgPro.CMD_ReadNewsResp_PRO;
import com.sltj.medical.dataUtil.protobuf.CmdMsgPro.CMD_SetNewsCommentResp_PRO;
import com.sltj.medical.dataUtil.protobuf.CmdMsgPro.CMD_SetNewsCommentSupportReq_PRO;
import com.sltj.medical.dataUtil.protobuf.CmdMsgPro.CMD_SetNewsCommentSupportResp_PRO;
import com.sltj.medical.dataUtil.protobuf.EnumPro.ePAGE_TYPE_PRO;
import com.sltj.medical.dataUtil.protobuf.PublicmsgPro;
import com.sltj.medical.dataUtil.protobuf.PublicmsgPro.NetConnectResp_PRO_MSG;
import com.sltj.medical.dataUtil.protobuf.PublicmsgPro.Net_CommonResp_PRO_MSG;
import com.sltj.medical.publicMsg.MsgReceiveDef;
import com.sltj.medical.publicMsg.MsgReceiveDef.AddMoodResp;
import com.sltj.medical.publicMsg.MsgReceiveDef.AuthLoginResp;
import com.sltj.medical.publicMsg.MsgReceiveDef.AuthNetCommonResp;
import com.sltj.medical.publicMsg.MsgReceiveDef.AuthcodeResp;
import com.sltj.medical.publicMsg.MsgReceiveDef.CollectNewsResp;
import com.sltj.medical.publicMsg.MsgReceiveDef.CommentListResp;
import com.sltj.medical.publicMsg.MsgReceiveDef.CommentZanResp;
import com.sltj.medical.publicMsg.MsgReceiveDef.DoctorInfoResp;
import com.sltj.medical.publicMsg.MsgReceiveDef.DoctorListResp;
import com.sltj.medical.publicMsg.MsgReceiveDef.HomeNewsResp;
import com.sltj.medical.publicMsg.MsgReceiveDef.MedicaltionsDetailResp;
import com.sltj.medical.publicMsg.MsgReceiveDef.MedicationsResp;
import com.sltj.medical.publicMsg.MsgReceiveDef.MoodRecordResp;
import com.sltj.medical.publicMsg.MsgReceiveDef.NetBestServerResp;
import com.sltj.medical.publicMsg.MsgReceiveDef.NetConnectResp;
import com.sltj.medical.publicMsg.MsgReceiveDef.NewsResp;
import com.sltj.medical.publicMsg.MsgReceiveDef.OffLineMessageResp;
import com.sltj.medical.publicMsg.MsgReceiveDef.PhysicalDetailResp;
import com.sltj.medical.publicMsg.MsgReceiveDef.PhysicalResp;
import com.sltj.medical.publicMsg.MsgReceiveDef.ReadNewsResp;
import com.sltj.medical.publicMsg.MsgReceiveDef.TreatDetailResp;
import com.sltj.medical.publicMsg.MsgReceiveDef.TreatResp;
import com.sltj.medical.publicMsg.MsgReceiveDef.UserCommentResp;
import com.sltj.medical.publicMsg.MsgReceiveDef.chatInfoResp;
import com.sltj.medical.publicMsg.MsgReceiveDef.historyMessageResp;
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
		case NetHouseMsgType.CMD_GET_PHYSICAL_RESP:// 体检记录响应
			obj = HandlePhysicalResp(proBufBody);
			break;
		case NetHouseMsgType.CMD_GET_PHYSICALDETAIL_RESP:// 体检记录祥情响应
			obj = HandlePhysicalDetailResp(proBufBody);
			break;
		case NetHouseMsgType.CMD_GET_TREAT_RESP:// 治疗记录响应
			obj = HandleTreatResp(proBufBody);
			break;
		case NetHouseMsgType.CMD_GET_TREATDETAIL_RESP:// 治疗记录祥情
			obj = HandleTreatDetailResp(proBufBody);
			break;
		case NetHouseMsgType.CMD_GET_MEDICATIONS__RESP:// 用药记录列表
			obj = HandleMedicationsResp(proBufBody);
			break;
		case NetHouseMsgType.CMD_GET_MEDICATIONSDETAIL_RESP:// 用药记录祥情
			obj = HandleMedicationsDetailResp(proBufBody);
			break;
		case NetHouseMsgType.CMD_GET_MOOD_RESP:// 心情记录列表
			obj = HandleMoodRecordResp(proBufBody);
			break;
		case NetHouseMsgType.CMD_ADD_MOOD_RESP:// 添加心情响应
			obj = HandleAddMoodResp(proBufBody);
			break;

		case NetHouseMsgType.CMD_GET_COMMENT_RESP:// 评论列表 响应
			obj = HandleCommentListResp(proBufBody);
			break;
		case NetHouseMsgType.CMD_PUSH_COMMENT_RESP:// 评论 响应
			obj = HandleCommentResp(proBufBody);
			break;
		case NetHouseMsgType.CMD_GET_COMMENTZAN_RESP:// 赞响应
			obj = HandleCommentZanResp(proBufBody);
			break;
		case NetHouseMsgType.CMD_COLLECT_NEWS_RESP:// 收藏响应
			obj = HandleCollectResp(proBufBody);
			break;
		case NetHouseMsgType.CMD_GET_DOCTOR_RESP:// 医生列表
			obj = HandleDoctorListResp(proBufBody);
			break;
		case NetHouseMsgType.CMD_DOCTORDETAIL_RESP:// 医生祥情
			obj = HandleDoctorDetailResp(proBufBody);
			break;
		case NetHouseMsgType.CMD_GET_CHAT_RESP:// 聊天响应
			obj = HandleChatResp(proBufBody);
			break;
		case NetHouseMsgType.CMD_GET_HISTORY_RESP:// 历史记录
			obj = HandleChatHistoryResp(proBufBody);
			break;
		case NetHouseMsgType.CMD_GET_OFF_LINE_RESP:// 离线信息
			obj = HandleOffLineResp(proBufBody);
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
			resp.szTime = newsResp_PRO.getSzTime();
			resp.ePageType = newsResp_PRO.getEPageType();

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

	/*
	 * 获取体检记录 响应
	 */
	private static PhysicalResp HandlePhysicalResp(byte[] proBufBody) {

		MsgReceiveDef.PhysicalResp resp = new MsgReceiveDef.PhysicalResp();
		try {
			CmdMsgPro.CMD_Get_Tijian_RecordResp_PRO newsResp_PRO = CMD_Get_Tijian_RecordResp_PRO.parseFrom(proBufBody);
			resp.info = newsResp_PRO.getInfoList();
			resp.ePageType = newsResp_PRO.getEPageType();
			resp.szTime = newsResp_PRO.getSzTime();
			resp.eResult = newsResp_PRO.getEResult();

		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
		return resp;
	}

	/*
	 * 获取治疗记录 响应
	 */
	private static TreatResp HandleTreatResp(byte[] proBufBody) {

		TreatResp Localresp = new MsgReceiveDef.TreatResp();
		try {
			CmdMsgPro.CMD_Get_Zhiliao_RecordResp_PRO servResp = CMD_Get_Zhiliao_RecordResp_PRO.parseFrom(proBufBody);
			Localresp.info = servResp.getInfoList();
			Localresp.ePageType = servResp.getEPageType();
			Localresp.szTime = servResp.getSzTime();
			Localresp.eResult = servResp.getEResult();

		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
		return Localresp;
	}

	/*
	 * 获取治疗记录 响应
	 */
	private static MedicationsResp HandleMedicationsResp(byte[] proBufBody) {

		MedicationsResp Localresp = new MsgReceiveDef.MedicationsResp();
		try {
			CmdMsgPro.CMD_Get_Yongyao_RecordResp_PRO servResp = CMD_Get_Yongyao_RecordResp_PRO.parseFrom(proBufBody);
			Localresp.info = servResp.getInfoList();
			Localresp.ePageType = servResp.getEPageType();
			Localresp.szTime = servResp.getSzTime();
			Localresp.eResult = servResp.getEResult();

		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
		return Localresp;
	}

	/*
	 * 获取体检记录祥情 响应
	 */
	private static PhysicalDetailResp HandlePhysicalDetailResp(byte[] proBufBody) {

		MsgReceiveDef.PhysicalDetailResp Localresp = new MsgReceiveDef.PhysicalDetailResp();
		try {
			CmdMsgPro.CMD_Get_Tijian_InfoResp_PRO servResp = CMD_Get_Tijian_InfoResp_PRO.parseFrom(proBufBody);
			Localresp.info = servResp.getInfo();
			Localresp.eResult = servResp.getEResult();
			Localresp.Szurl = servResp.getSzURL();

		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
		return Localresp;
	}

	/*
	 * 获取治疗记录祥情 响应
	 */
	private static TreatDetailResp HandleTreatDetailResp(byte[] proBufBody) {

		MsgReceiveDef.TreatDetailResp Localresp = new MsgReceiveDef.TreatDetailResp();
		try {
			CmdMsgPro.CMD_Get_Zhiliao_InfoResp_PRO servResp = CMD_Get_Zhiliao_InfoResp_PRO.parseFrom(proBufBody);
			Localresp.info = servResp.getInfo();
			Localresp.eResult = servResp.getEResult();
			Localresp.szContent = servResp.getSzContent();

		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
		return Localresp;
	}

	/*
	 * 获取用药记录祥情 响应
	 */
	private static MedicaltionsDetailResp HandleMedicationsDetailResp(byte[] proBufBody) {

		MsgReceiveDef.MedicaltionsDetailResp Localresp = new MsgReceiveDef.MedicaltionsDetailResp();
		try {
			CmdMsgPro.CMD_Get_Yongyao_InfoResp_PRO servResp = CMD_Get_Yongyao_InfoResp_PRO.parseFrom(proBufBody);
			Localresp.info = servResp.getInfo();
			Localresp.eResult = servResp.getEResult();
			Localresp.szContent = servResp.getSzContent();

		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
		return Localresp;
	}

	/*
	 * 获取心情记录 响应
	 */
	private static MoodRecordResp HandleMoodRecordResp(byte[] proBufBody) {

		MsgReceiveDef.MoodRecordResp Localresp = new MsgReceiveDef.MoodRecordResp();
		try {
			CmdMsgPro.CMD_Get_Mood_RecordResp_PRO servResp = CMD_Get_Mood_RecordResp_PRO.parseFrom(proBufBody);
			Localresp.info = servResp.getInfoList();
			Localresp.eResult = servResp.getEResult();
			Localresp.iAverage = servResp.getIaverage();
			Localresp.szTime = servResp.getSzTime();

		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
		return Localresp;
	}

	/*
	 * 获取添加心情结果
	 */
	private static AddMoodResp HandleAddMoodResp(byte[] proBufBody) {

		MsgReceiveDef.AddMoodResp Localresp = new MsgReceiveDef.AddMoodResp();
		try {
			CmdMsgPro.CMD_AddMoodResp_PRO servResp = CMD_AddMoodResp_PRO.parseFrom(proBufBody);
			Localresp.iRecordIndex = servResp.getIRecordIndex();
			Localresp.eResult = servResp.getEResult();

		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
		return Localresp;
	}

	/*
	 * 阅读结果
	 */
	private static AddMoodResp HandleNewsDetailResp(byte[] proBufBody) {

		MsgReceiveDef.AddMoodResp Localresp = new MsgReceiveDef.AddMoodResp();
		try {
			CmdMsgPro.CMD_AddMoodResp_PRO servResp = CMD_AddMoodResp_PRO.parseFrom(proBufBody);
			Localresp.iRecordIndex = servResp.getIRecordIndex();
			Localresp.eResult = servResp.getEResult();

		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
		return Localresp;
	}

	/*
	 * 评论结果
	 */
	private static UserCommentResp HandleCommentResp(byte[] proBufBody) {

		MsgReceiveDef.UserCommentResp Localresp = new MsgReceiveDef.UserCommentResp();
		try {
			CmdMsgPro.CMD_SetNewsCommentResp_PRO servResp = CMD_SetNewsCommentResp_PRO.parseFrom(proBufBody);
			Localresp.eResult = servResp.getEResult();
			Localresp.iCommentNum = servResp.getICommentNum();

		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
		return Localresp;
	}

	/*
	 * 评论列表结果
	 */
	private static CommentListResp HandleCommentListResp(byte[] proBufBody) {

		MsgReceiveDef.CommentListResp Localresp = new MsgReceiveDef.CommentListResp();
		try {
			CmdMsgPro.CMD_GetNewsCommentListResp_PRO servResp = CMD_GetNewsCommentListResp_PRO.parseFrom(proBufBody);
			Localresp.eResult = servResp.getEResult();
			Localresp.iNewsId = servResp.getInewsid();
			Localresp.info = servResp.getInfoList();

		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
		return Localresp;
	}

	/*
	 * 评论点赞结果
	 */
	private static CommentZanResp HandleCommentZanResp(byte[] proBufBody) {

		MsgReceiveDef.CommentZanResp Localresp = new MsgReceiveDef.CommentZanResp();
		try {
			CmdMsgPro.CMD_SetNewsCommentSupportResp_PRO servResp = CMD_SetNewsCommentSupportResp_PRO
					.parseFrom(proBufBody);

			Localresp.eResult = servResp.getEResult();
			Localresp.iCommentId = servResp.getIcommentid();
			Localresp.iSupportNum = servResp.getISupportNum();

		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
		return Localresp;
	}

	/*
	 * 资讯收藏结果
	 */
	private static CollectNewsResp HandleCollectResp(byte[] proBufBody) {

		MsgReceiveDef.CollectNewsResp Localresp = new MsgReceiveDef.CollectNewsResp();
		try {
			CmdMsgPro.CMD_CollectionNewsResp_PRO servResp = CMD_CollectionNewsResp_PRO.parseFrom(proBufBody);

			Localresp.eResult = servResp.getEResult();
			Localresp.iNewsId = servResp.getInewsid();
			Localresp.collectType = servResp.getType();

		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
		return Localresp;
	}

	/*
	 * 医生列表响应
	 */
	private static DoctorListResp HandleDoctorListResp(byte[] proBufBody) {

		MsgReceiveDef.DoctorListResp Localresp = new MsgReceiveDef.DoctorListResp();
		try {
			CmdMsgPro.CMD_GetMyDoctorListResp_PRO servResp = CMD_GetMyDoctorListResp_PRO.parseFrom(proBufBody);

			Localresp.eResult = servResp.getEResult();
			Localresp.info = servResp.getInfoList();
			Localresp.iPage = servResp.getIPage();

		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
		return Localresp;
	}

	/*
	 * 医生祥情响应
	 */
	private static DoctorInfoResp HandleDoctorDetailResp(byte[] proBufBody) {

		MsgReceiveDef.DoctorInfoResp Localresp = new MsgReceiveDef.DoctorInfoResp();
		try {
			CmdMsgPro.CMD_GetDoctorInfoResp_PRO servResp = CMD_GetDoctorInfoResp_PRO.parseFrom(proBufBody);

			Localresp.eResult = servResp.getEResult();
			Localresp.doctorinfo = servResp.getDoctorinfo();

		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
		return Localresp;
	}
	/*
	 * 聊天响应
	 */
	private static chatInfoResp HandleChatResp(byte[] proBufBody) {
		MsgReceiveDef.chatInfoResp Localresp=new MsgReceiveDef.chatInfoResp();
		try {
			CmdMsgPro.CMD_ChatResp_PRO servResp = CMD_ChatResp_PRO.parseFrom(proBufBody);

			Localresp.eResult = servResp.getEResult();
			Localresp.idstid=servResp.getIdstid();
			Localresp.iRecoderid=servResp.getIRecoderid();	//返回此消息的id

		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
		return Localresp;
	}
	/*
	 * 历史记录响应
	 */
	private static historyMessageResp HandleChatHistoryResp(byte[] proBufBody) {
		MsgReceiveDef.historyMessageResp Localresp=new MsgReceiveDef.historyMessageResp();
		try {
			CmdMsgPro.CMD_GetRecoderResp_PRO servResp = CMD_GetRecoderResp_PRO.parseFrom(proBufBody);
			
			Localresp.eResult = servResp.getEResult();
			Localresp.idstid=servResp.getIdstid();
			Localresp.info=servResp.getInfoList();
			Localresp.szBeforTime=servResp.getSzBeforTime();
			
			
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
		return Localresp;
	}
	/*
	 * 获取离线响应消息
	 */
	private static OffLineMessageResp HandleOffLineResp(byte[] proBufBody) {
		MsgReceiveDef.OffLineMessageResp Localresp=new MsgReceiveDef.OffLineMessageResp();
		try {
			CmdMsgPro.CMD_GetOffLineMessageResp_PRO servResp = CMD_GetOffLineMessageResp_PRO.parseFrom(proBufBody);
			
			Localresp.eResult = servResp.getEResult();
			Localresp.info=servResp.getInfoList();
			
			
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
		return Localresp;
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
