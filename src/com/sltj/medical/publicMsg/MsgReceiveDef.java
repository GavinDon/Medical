package com.sltj.medical.publicMsg;

import java.util.List;

import com.sltj.medical.dataUtil.protobuf.EnumPro.eOPERRESULT_PRO;
import com.sltj.medical.dataUtil.protobuf.EnumPro.ePAGE_TYPE_PRO;
import com.sltj.medical.dataUtil.protobuf.PublicmsgPro.Net_NewsInfo_PRO;
import com.sltj.medical.dataUtil.protobuf.PublicmsgPro.Net_ServerInfo_PRO;

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
	 * 阅读资讯
	 */
	public static class ReadNewsResp {
		public eOPERRESULT_PRO eResult;// 操作结果
		public int newsId;// 资讯信息
	}

	// ============================通知类消息================================

	/**
	 * 家政服务--通用通知消息
	 */
	public static class HsCommon_Notify {
		public int uUserID; // 为用户生成一个唯一的ID;
		public int uCompanyID; // 为公司成一个唯一的ID;
		public int iOrderID;
	}

	/**
	 * 家政公司抢单成功的通知
	 *
	 */
	public static class HsCmpRaceOrder_Notify {
		public int uCompanyID; // 公司ID；
		public int uOrderID; // 订单ID;
		public String szSrvPrice; // 服务价格
		public String szRemark; // 抢单说明信息;
	}

	/**
	 * 家政公司提请申诉通知
	 *
	 */
	public static class HsCompanyComplain_Notify {
		public int uOrderID; // 订单ID;
		public int uUserID; // 提交申诉的公司ID
		public String szContent; // 申诉内容
	}

	/**
	 * 家政公司审批结果通知
	 *
	 */
	public static class HsCmpRefuseAtticheResult_Notify {
		public int iCompanyID; // 公司唯一标示;
		public int uUserID; // 用户唯一标示
		public int uResult; // 审批结果， 0为同意，1为拒绝
		public String szRejReason; // 拒绝原因(拒绝时有效)
	}

}
