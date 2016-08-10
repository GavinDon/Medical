package com.sltj.medical.publicMsg;

import com.sltj.medical.base.MyApplication;
import com.sltj.medical.dataUtil.protobuf.EnumPro.eMSG_OPER_PRO;
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
	 * 阅读资讯请求
	 */
	public static class IReadNewsReq {
		public int iUserId; // 用户ID
		public int inewsid; // 资讯ID

	}

	/**
	 * 收藏资讯请求
	 */
	public static class ICollectNewsReq {
		public int iUserId; // 用户ID
		public int inewsid; // 资讯ID
		public eMSG_OPER_PRO collectType; // 资讯ID

	}
	/**
	 * 用户发表评论请求
	 */
	public static class IUserCommentReq {
		public int iUserId; // 用户ID
		public int inewsid; // 资讯ID
		public String szComment; //	//评论的内容
	}
	/**
	 * 评论列表请求
	 */
	public static class ICommenListReq {
		public int iUserId; // 用户ID
		public int inewsid; // 资讯ID

	}
	/**
	 * 评论点赞请求
	 */
	public static class ICommenZanReq {
		public int iUserId; // 用户ID
		public int iCommentid; // 评论ID

	}
	
	

	/**
	 * 获取体检记录列表-治疗记录-用药记录 请求参数一样（消息类型是不一样的。）
	 */
	public static class IphsicalRecordReq {
		public int iUserId; // 用户ID
		public ePAGE_TYPE_PRO ePageType; // 获取时间点前的还是后
		public String szBeforTime; // 这个时间点
		public int iRecoderNum;// 一次获取多少条消息
		public int iBeforRecoderid;// 这条记录之前的消息，每条消息发送成功会返还一个消息ID
	}

	/**
	 * 获取体检记录详情
	 *
	 */
	public static class IPhsicalRecordDetailReq {
		public int IUserId;
		public int iRecordIndex; // 记录索引号
	}

	/**
	 * 获取治疗记录详情
	 *
	 */
	public static class ITreatRecordDetailReq {
		public int IUserId;
		public int iRecordIndex; // 记录索引号
	}

	// /**
	// * 获取用药记录祥情
	// */
	// public static class IMedicationsDetailRecordReq {
	// public int IUserId;
	// public int iRecordIndex;// 记录索引号
	// }
	/**
	 * 获取心情记录列表
	 */
	public static class IMoodRecordReq {
		public int iUserId; // 用户ID
		public String szBeforTime; // 这个时间点
		public int iRecoderNum;// 一次获取多少条消息
		public int iBeforRecoderid;// 这条记录之前的消息，每条消息发送成功会返还一个消息ID
	}

	/**
	 * 添加心情请求参数
	 *
	 */
	public static class IMoodAddReq {
		public int iuserid; //
		public int iGrade; // 记录评分
		public String szContent;// 消息内容
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

}
