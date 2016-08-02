package com.sltj.medical.publicMsg;

import com.sltj.medical.base.MyApplication;
import com.sltj.medical.dataUtil.protobuf.EnumPro.ePAGE_TYPE_PRO;

public final class MsgInncDef {
	/**
	 * 上拉下拉标志
	 * 
	 * @author linan
	 *
	 */
	public enum OnrefreshFlag {

		/**
		 * 获取新的
		 */

		REFRESH_DOWN(0),
		/**
		 * 获取当前时间 之前的
		 */
		REFRESH_PullUp(1);
		int flag;
		String time;

		private OnrefreshFlag(int flag) {
			this.flag = flag;
		}

		public static OnrefreshFlag refreshFlag(int flag) {
			for (OnrefreshFlag mFlag : values()) {
				if (flag == mFlag.flag) {
					return mFlag;
				}
			}
			return null;

		}
	}

	/*
	 * 登录认证--通用请求
	 */
	public static class AuthNetCommonReq {
		public int iUserid; // userid

		public void buildMsg(int iUserid) {
			this.iUserid = iUserid;
		}
	}

	/*
	 * 连接请求
	 */
	public class NetConnectReq {
		public int iUserId = MyApplication.userId;// 随机生成用户唯一ID
		public String SzAuthInfo = "1";// 加密认证信息
		public String szCrcInfo = "2";// 加密循环校验

	}

	/*
	 * 登录请求信息
	 */
	public class LoginReq {
		public String strAccountID;// 帐号ID
		public String strPasswd;// 帐号密码
		public int type;

		public void buildMsg(String accountid, String strpwd) {
			this.strAccountID = accountid;// 帐号ID
			this.strPasswd = strpwd;// 帐号密码
		}

	}

	/**
	 * 获取验证码请求
	 */
	public class GetVerification {
		public String szAccountID;// 帐号
		public int eAccountType = 3;

		public void buildMsg(String accout) {
			this.szAccountID = accout;
		}
	}

	/**
	 * 获取首页资讯
	 */
	public static class IhomeNewsReq {
		public int iUserId;

	}

	/**
	 * 获取资讯列表
	 */
	public static class INewsReq {
		public int iUserId;
		public int eType;
		public String szTime;
		public int ePageType;

	}

	/**
	 * 获取资讯列表
	 */
	public static class IReadNewsReq {
		public int iUserId; // 用户ID
		public int inewsid; // 资讯ID

	}

	// -------------------------------------------------------------------------------------//
	/*
	 * 更新密码请求
	 */
	public class UpdatePasswordReq {
		public int iuserid; // 更新者
		public String szOldPwd; // 旧密码
		public String szNewPwd; // 新密码

		public void buildMsg(int iuserId, String szOldPwd, String szNewPwd) {
			this.iuserid = iuserId;
			this.szOldPwd = szOldPwd;
			this.szNewPwd = szNewPwd;
		}
	}

	/*
	 * 帐号激活请求
	 */

	public class AccountActiveReq {
		public int iUserID; // userid
		public String szActivKey; // 激活码

		public void buildeMsg(int iUserID, String activiKey) {
			this.iUserID = iUserID;
			this.szActivKey = activiKey;
		}
	}

	/*
	 * 获取服务器列表请求 通用请求 查询服务器信息请求
	 */
	public class GetServerInfoReq {
		public int iuserid; // userid
		public int info; // 要查找的服务器类型列表

		public void buildMsg(int iuserid, int info) {
			this.iuserid = iuserid;
			this.info = info;
		}
	}

	/*
	 * 重设密码请求信息
	 */
	public class ResetPasswordReq {
		public int iuserid = 1; // 账号ID
		public String szNewPwd; // 新密码

		public void buildMsg(int iuserid, String szNewPwd) {
			this.iuserid = iuserid;
			this.szNewPwd = szNewPwd;
		}
	}

	/*
	 * 查找个人信息请求
	 */
	public class QueryBaseInfoReq {
		public int iuserid;// 源userid
		public int iDstuserid;// 目的userid

		public void buildMsg(int iuserid, int iDstuserid) {
			this.iuserid = iuserid;
			this.iDstuserid = iDstuserid;
		}
	}

	/*
	 * 更新个人基本信息请求
	 */
	public class AUTHUserSelfMoveInfoModReq {
		public int iuserid; // 源userid
		public int iModType; // 修改类别：:1：昵称, 2:个性签名, 3：手机号码,4：头像,5: 性别, 6:地区）
		public String szModInfo; // 数据修改内容

		public void buildMsg(int iuserid, int iModType, String szModInfo) {
			this.iuserid = iuserid;
			this.iModType = iModType;
			this.szModInfo = szModInfo;
		}
	}

	// ============================以下为家政消息================================

	/**
	 * 家政服务-通用请求
	 */
	public class HsNetCommon_Req {
		public int iSrcID; // 为用户生成一个唯一的ID（必须）
		public int iSelectID; // 选定的乙方唯一ID（可能是订单ID，也可能是用户ID或公司ID）

		public void buildMsg(int iSrcID, int iSelectID) {
			this.iSrcID = iSrcID;
			this.iSelectID = iSelectID;
		}
	}

	/**
	 * 家政服务--通用通知消息
	 */
	public class HsCommon_Notify {
		public int uUserID; // 为用户生成一个唯一的ID;(必须)
		public int uCompanyID; // 为公司成一个唯一的ID;
		public int iOrderID;

		public void buildMsg(int uUserID, int uCompanyID, int iOrderID) {
			this.uUserID = uUserID;
			this.uCompanyID = uCompanyID;
			this.iOrderID = iOrderID;
		}
	}

	/*
	 * 首页家政结构体请求
	 */
	public class HsAppHomenInfo_Req {
		public int bGetHome; // true:提取【首页】订单类型数据， false:提取【更多】订单类型数据
								// --(使用1表示true,0表示false)
		public int iUserID; // 保存的用户ID

		public void buildMsg(int bGetHome, int iUserID) {
			this.bGetHome = bGetHome;
			this.iUserID = iUserID;
		}
	}

	/*
	 * 广播下单请求消息
	 */
	public class HsBroadCastOrderInfo_Req {
		public int uUserID; // 用户ID;（必须）
		public int uCompanyID; // 公司ID--预约下单时有效
		public int iOrderTypeID; // 下单类型
		public int iUsrSrvedIndex; // 服务地址索引
		public String szSrvBeginTime; // 服务开始时间
		public String szUnitArea; // 单位面积
		public String szHeartPrice; // 心理价位
		public String szReMark; // 备注信息;

		public void buildMsg(int uUserID, int uCompanyID, int iOrderTypeID, int iUsrSrvedIndex, String szSrvBeginTime,
				String szUnitArea, String szHeartPrice, String szReMark) {
			this.uUserID = uUserID;
			this.uCompanyID = uCompanyID;
			this.iOrderTypeID = iOrderTypeID;
			this.iUsrSrvedIndex = iUsrSrvedIndex;
			this.szSrvBeginTime = szSrvBeginTime;
			this.szUnitArea = szUnitArea;
			this.szHeartPrice = szHeartPrice;
			this.szReMark = szReMark;
		}
	}

	/*
	 * 预约下单请求消息
	 */
	public class HsSubScribeOrderInfo_Req {
		public int uUserID; // 用户ID;
		public int uCompanyID; // 公司ID--预约下单时有效
		public int iOrderTypeID; // 下单类型
		public int iUsrSrvedIndex; // 服务地址索引
		public String szSrvBeginTime; // 服务开始时间
		public String szServiceItem; // 服务清单
		public String szSrvPrice; // 服务价格
		public String szReMark; // 备注信息;
	}

	/*
	 * 订单评价信息（评价）
	 */
	public class HsUserOrderValuate_Req {
		public int uCompanyID; // 公司ID

		public int uUserID; // 用户ID
		public int iOrderID; // 订单ID
		public int iGiveStar; // 给定星级
		public String szRemark; // 评价内容
		public String szPicUrl; // 评价内容

		public void buildMsg(int uCompanyID, int uUserID, int iOrderID, int iGiveStar, String szRemark,
				String szPicUrl) {
			this.uCompanyID = uCompanyID;
			this.uUserID = uUserID;
			this.iOrderID = iOrderID;
			this.iGiveStar = iGiveStar;
			this.szRemark = szRemark;
			this.szPicUrl = szPicUrl;
		}
	}

	/*
	 * 用户提请申诉请求(投诉)
	 */
	public class HsUserAskAppeal_Req {
		public int uUserID;// 用户id(必须)
		public int uCompanyID;// 公司id(必须)
		public int iOrderID;// 订单id(必须)
		public String szApperlClass; // 投诉类别
		public String szReason;// 投诉原因

		public void buildMsg(int uUserID, int uCompanyID, int iOrderID, String szApperlClass, String szReason) {
			this.uUserID = uUserID;
			this.uCompanyID = uCompanyID;
			this.iOrderID = iOrderID;
			this.szApperlClass = szApperlClass;
			this.szReason = szReason;
		}
	}

	/*
	 * 用户加入指定公司请求(加入明星公司)
	 */
	public class HsUserAddCompany_Req {
		public int uUserID; // 用户ID(必须)
		public int uCompanyID; // 家政公司ID(必须)
		public int iSrvType; // 服务类型；
		public String szName; // 用户名(必须)
		public String szLinkTel; // 联系电话(必须)
		public String szCurAddr; // 现居住址(必须)
		public String szRemark; // 个人介绍;

		public void buildMsg(int uUserID, int uCompanyID, int iSrvType, String szName, String szLinkTel,
				String szCurAddr, String szRemark) {
			this.uUserID = uUserID;
			this.uCompanyID = uCompanyID;
			this.iSrvType = iSrvType;
			this.szName = szName;
			this.szLinkTel = szLinkTel;
			this.szCurAddr = szCurAddr;
			this.szRemark = szRemark;
		}
	}

	/*
	 * 用户退出指定公司请求(退出明星公司)
	 */
	public class HsUserQuitComany_Req {
		public int uUserID; // 用户ID
		public int uCompanyID; // 公司ID
		public String szRemark; // 退出原因

		public void buildMsg(int uUserID, int uCompanyID, String szRemark) {
			this.uUserID = uUserID;
			this.uCompanyID = uCompanyID;
			this.szRemark = szRemark;
		}
	}

	/*
	 * 用户确认付款请求
	 */
	public class HsUserSurePay_Req {
		public int uUserID; // 用户ID(必须)
		public int iOrderID; // 订单ID(必须)
		public String szPrice; // 付款单价

		public void buildMsg(int uUserID, int iOrderID, String szPrice) {
			this.uUserID = uUserID;
			this.iOrderID = iOrderID;
			this.szPrice = szPrice;
		}
	}

	/*
	 * 用户添加服务联系地址 请求
	 */
	public class HsUserServiceAddrInfo_Req {
		public int uUserID; // 用户ID（必须）

		public int uAreaID; // 地址区域ID
		public int uAddrIndex; // 地址序号（必须）
		public String szUserName; // 联系人名
		public String szUserTel; // 联系人电话
		public String szUserAddr; // 联系人地址

		public void buildMsg(int uUserID, int uAreaID, int uAddrIndex, String szUserName, String szUserTel,
				String szUserAddr) {
			this.uUserID = uUserID;
			this.uAreaID = uAreaID;
			this.uAddrIndex = uAddrIndex;
			this.szUserName = szUserName;
			this.szUserTel = szUserTel;
			this.szUserAddr = szUserAddr;
		}

	}

}
