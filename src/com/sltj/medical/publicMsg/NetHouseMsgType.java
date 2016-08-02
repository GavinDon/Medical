package com.sltj.medical.publicMsg;

/**
 * 功能：与服务端协议的消息类型
 * 
 * @author linan
 *
 */
public class NetHouseMsgType {

	// 登录类消息
	public final static int NETCMD_CONNECT_REQ = 1;// 连接请求
	public final static int NETCMD_CONNECT_RESP = 2; // 响应

	public final static int NETAUTH_USERLOGIN_REQ = 5; // 登录请求消息
	public final static int NETAUTH_USERLOGIN_RESP = 6; // 响应

	public final static int NETAUTH_GETBESTSRV_REQ = 7; // 提取最优服务器请求
	public final static int NETAUTH_GETBESTSRV_RESP = 8; // 响应

	public final static int NETAUTH_VERTIFY_REQ = 9; // 获取验证码请求
	public final static int NETAUTH_VERTIFY_RESP = 10; // 响应

	public final static int CMD_HOMENEWS_REQ = 10005; // 首页资讯列表请求
	public final static int CMD_HOMENEWS_RESP = 10006; // 响应

	public final static int CMD_NEWSLIST_REQ = 10003; // 资讯列表请求
	public final static int CMD_NEWSLIST_RESP = 10004; // 响应

	public final static int CMD_PUSH_COMMENT_REQ = 10007; // 发表评论请求
	public final static int CMD_PUSH_COMMENT_RESP = 10008; // 响应

	public final static int CMD_GET_COMMENT_REQ = 10009; // 获取评论请求
	public final static int CMD_GET_COMMENT_RESP = 10010; // 响应

	public final static int CMD_READ_NEWS_REQ = 10019; // 读取资讯请求
	public final static int CMD_READ_NEWS_RESP = 10020; // 响应

	public final static int CMD_GET_SPORT_REQ = 10013; // 获取运动记录请求消息
	public final static int CMD_GET_SPORT_RESP = 10014; // 获取运动记录响应消息

	public final static int CMD_COLLECT_NEWS_REQ = 10017; // 收藏资讯请求消息
	public final static int CMD_COLLECT_NEWS_RESP = 10018; // 获收藏资讯响应消息

	public final static int CMD_GET_MYDOCTOR_REQ = 10021; // 获取我的医生列表请求消息
	public final static int CMD_GET_MYDOCTOR_RESP = 10022; // 获取我的医生列表响应消息

	public final static int CMD_GET_DOCTOR_REQ = 10023; // 获取医生列表请求消息
	public final static int CMD_GET_DOCTOR_RESP = 10024; // 获取医生列表请求消息

	public final static int CMD_DOCTORDETAIL_REQ = 10025; // 获取医生详细信息请求消息
	public final static int CMD_DOCTORDETAIL_RESP = 10026; // 获取医生详细信息响应消息

	public final static int CMD_GET_PHYSICAL_REQ = 10033; // 获取体检记录列表请求消息
	public final static int CMD_GET_PHYSICAL_RESP = 10034; // 获取体检记录列表响应消息

	public final static int CMD_GET_TREAT_REQ = 10035; // 获取治疗记录列表请求消息
	public final static int CMD_GET_TREAT_RESP = 10036; // 获取治疗记录列表响应消息

	public final static int CMD_GET_MEDICATIONS__REQ = 10037; // 获取用药记录列表请求消息
	public final static int CMD_GET_MEDICATIONS__RESP = 10038; // 获取用药记录列表响应消息

	public final static int CMD_ADD_MOOD_REQ = 10039; // 添加心情请求消息
	public final static int CMD_ADD_MOOD_RESP = 10040; // 添加心情响应消息

	public final static int CMD_GET_MOOD_REQ = 10041; // 获取心情列表请求消息
	public final static int CMD_GET_MOOD_RESQ = 10042; // 获取心情列表响应消息

	public final static int CMD_GET_PHYSICALDETAIL_REQ = 10043; // 获取体检详细信息请求消息
	public final static int CMD_GET_PHYSICALDETAIL_RESP = 10044; // 获取体检详细信息请求消息

	public final static int CMD_GET_TREATDETAIL_REQ = 10045; // 获取治疗详细信息请求消息
	public final static int CMD_GET_TREATDETAIL_RESP = 10046; // 获取治疗详细信息请求消息

	public final static int CMD_GET_MEDICATIONSDETAIL_REQ = 10047; // 获获取用药详细信息请求消息
	public final static int CMD_GET_MEDICATIONSDETAIL_RESP = 10048; // 获取用药详细信息响应消息

}
