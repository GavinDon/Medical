package com.sltj.medical.publicMsg;

import java.util.List;

import com.sltj.medical.dataUtil.protobuf.CmdMsgPro.CMD_Get_Zhiliao_RecordResp_PRO;
import com.sltj.medical.dataUtil.protobuf.EnumPro.eMSG_OPER_PRO;
import com.sltj.medical.dataUtil.protobuf.EnumPro.eOPERRESULT_PRO;
import com.sltj.medical.dataUtil.protobuf.EnumPro.ePAGE_TYPE_PRO;
import com.sltj.medical.dataUtil.protobuf.PublicmsgPro.Net_CommentInfo_PRO;
import com.sltj.medical.dataUtil.protobuf.PublicmsgPro.Net_Mood_RecoderInfo_PRO;
import com.sltj.medical.dataUtil.protobuf.PublicmsgPro.Net_NewsInfo_PRO;
import com.sltj.medical.dataUtil.protobuf.PublicmsgPro.Net_ServerInfo_PRO;
import com.sltj.medical.dataUtil.protobuf.PublicmsgPro.Net_Tijian_RecoderInfo_PRO;
import com.sltj.medical.dataUtil.protobuf.PublicmsgPro.Net_Yongyao_RecoderInfo_PRO;
import com.sltj.medical.dataUtil.protobuf.PublicmsgPro.Net_Zhiliao_RecoderInfo_PRO;

public class MsgReceiveDef {

	/**
	 * 登录认证--通用响应--只需要获得操作结果的，一律使用该消息
	 */
	public static class AuthNetCommonResp {
		public eOPERRESULT_PRO eResult;
		public int iUserid;// 用户id
	}

	/**
	 * 连接请求的服务器响应--登录认证服务器的连接响应
	 */
	public static class NetConnectResp {
		public eOPERRESULT_PRO eResult;
		public int iUserid; // 用户账号ID;
		public int iSrvTime; // 服务器的时间
	}

	/**
	 * 最优服务器响应--
	 */
	public static class NetBestServerResp {
		public eOPERRESULT_PRO eResult;
		public List<Net_ServerInfo_PRO> serverList; // 最优服务列表
	}

	/**
	 * 验证码响应
	 */
	public static class AuthcodeResp {
		public eOPERRESULT_PRO eResult;
		public String szAccountID;// 登录的帐号
		public String szCode;// 验证码
	}

	/**
	 * 登录响应
	 *
	 */
	public static class AuthLoginResp {
		public eOPERRESULT_PRO eResult;
		public int iuserid; // userid
		public String szEmail; // 邮箱
		public String szPhoneNum; // 手机
		public String szUserNick; // 昵称
		public String szSignaTure; // 个性签名
		public String szUserID; // 用户账号ID
		public int iHeadPic; // 头像ID
		public String szHeadPic; // 头像信息
		public int iGroupVer; // 分组版本
		public int iFriendVer; // 好友列表版本
		public int iVipLevel; // VIP等级
		public int iAreaID; // 地区ID
		public int eSex; // 性别
	}

	/**
	 * 首页资讯
	 */
	public static class HomeNewsResp {
		public eOPERRESULT_PRO eResult;// 操作结果
		public List<Net_NewsInfo_PRO> info;// 资讯信息
	}

	/**
	 * 资讯列表
	 */
	public static class NewsResp {
		public eOPERRESULT_PRO eResult;// 操作结果
		public List<Net_NewsInfo_PRO> info;// 资讯信息
		public String szTime;
		public String creatTime;
		public ePAGE_TYPE_PRO ePageType; // 获取的是时间点之前还是之后的列表
	}

	/**
	 * 阅读资讯响应
	 */
	public static class ReadNewsResp {
		public eOPERRESULT_PRO eResult;// 操作结果
		public int newsId;// 资讯信息
	}

	/**
	 * 收藏资讯响应
	 */
	public static class CollectNewsResp {
		public eOPERRESULT_PRO eResult;
		public int iNewsId; // 资讯ID
		public eMSG_OPER_PRO collectType; // 增加为收藏，删除为取消收藏
	}

	/**
	 * 用户评论响应
	 */
	public static class UserCommentResp {
		public eOPERRESULT_PRO eResult;
		public int iCommentNum;// 评论后的资讯评论值
	}

	/**
	 * 获取评论列表响应
	 */
	public static class CommentListResp {
		public eOPERRESULT_PRO eResult;
		public int iNewsId; // 资讯ID
		public List<Net_CommentInfo_PRO> info; // 增加为收藏，删除为取消收藏
	}

	/**
	 * 获取评论点赞响应
	 */
	public static class CommentZanResp {
		public eOPERRESULT_PRO eResult;
		public int iCommentId; // //评论ID
		public int iSupportNum; // 点赞数
	}

	/**
	 * 获取体检记录响应消息
	 */
	public static class PhysicalResp {
		public eOPERRESULT_PRO eResult;// 操作结果
		public ePAGE_TYPE_PRO ePageType;// 获取时间点前的还是后的资讯
		public String szTime;// 获取这个时间之后的资讯
		public List<Net_Tijian_RecoderInfo_PRO> info;
	}

	/**
	 * 获取治疗响应消息
	 */
	public static class TreatResp {
		public eOPERRESULT_PRO eResult;// 操作结果
		public ePAGE_TYPE_PRO ePageType;// 获取时间点前的还是后的资讯
		public String szTime;// 获取这个时间之后的资讯
		public List<Net_Zhiliao_RecoderInfo_PRO> info;
	}

	/**
	 * 获取用药记录响应消息
	 */
	public static class MedicationsResp {
		public eOPERRESULT_PRO eResult;// 操作结果
		public ePAGE_TYPE_PRO ePageType;// 获取时间点前的还是后的资讯
		public String szTime;// 获取这个时间之后的资讯
		public List<Net_Yongyao_RecoderInfo_PRO> info;// 用药记录
	}

	/**
	 * 获取体检记录详情响应消息
	 */
	public static class PhysicalDetailResp {
		public eOPERRESULT_PRO eResult;// 操作结果
		public Net_Tijian_RecoderInfo_PRO info;
		public String Szurl;
	}

	/**
	 * 获取治疗详情响应消息
	 */
	public static class TreatDetailResp {
		public eOPERRESULT_PRO eResult;// 操作结果
		public Net_Zhiliao_RecoderInfo_PRO info;
		public String szContent;
	}

	/**
	 * 用药记录祥情响应
	 */
	public static class MedicaltionsDetailResp {
		public eOPERRESULT_PRO eResult;// 操作结果
		public String szContent; // 用药详细内容，文字
		public Net_Yongyao_RecoderInfo_PRO info;
	}

	/**
	 * 心情记录响应
	 */
	public static class MoodRecordResp {
		public eOPERRESULT_PRO eResult;// 操作结果
		public int iAverage; // 今天心情评分
		public String szTime;
		public List<Net_Mood_RecoderInfo_PRO> info;// 心情记录
	}

	/**
	 * 添加心情响应
	 */
	public static class AddMoodResp {
		public eOPERRESULT_PRO eResult;// 操作结果
		public int iRecordIndex; // 添加成功返回心情ID
	}

	// ============================通知类消息================================

}
