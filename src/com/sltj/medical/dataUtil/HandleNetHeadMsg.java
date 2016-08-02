package com.sltj.medical.dataUtil;

import java.io.Serializable;
import java.util.Arrays;

import com.sltj.medical.util.MTools;

import android.annotation.SuppressLint;



public class HandleNetHeadMsg implements Serializable{
	
	// 企业标识
	// final static int EnterpriseId = 0;
	public char ucEnterpriseId;
	// 业务类型标识，用来指明自己是何种业务.
	// final static int BussinessID = 1;
	public char uBussinessID;
	// 加密标识 0:不加密，1：--目前所有消息默认为不加密,使用结构体，但加密标示保留
	// final static int EncryptType = 2;
	public char ucEncryptType;
	// 消息类型
	// final static int MsgType = 3;
	public int uiMsgType;
	// 客户端类型
	// final static int ClientType = 7;
	public int uiClientType;
	// 消息流水号
	// final static int SequenceNo = 11;
	public int uiSequenceNo;
	// 消息体实际长度(序列话后的)
	// final static int MsgRealLen = 15;
	public int uiMsgRealLen;
	// 加密后消息体长度
	// final static int MsgLen = 19;
	public int uiMsgLen;

	// final static int iLength = 23;
	// byte[] head;
	

	
	/**
	 * 构建消息头
	 * @param uiMsgType 消息类型
	 * @param uiMsgRealLen 消息实际长度（序列化后的）
	 * @param uiMsgLen 消息长度 （ 加密后的）
	 * @param ucEncryptType 是否加密
	 * @return
	 */
	public  byte[]  buildHeadMsg(int uiMsgType , int uiMsgRealLen , int uiMsgLen , int uiSequenceNo, int ucEncryptType){
		
		HandleNetHeadMsg headMsg = new HandleNetHeadMsg();
		
		headMsg.ucEnterpriseId = 170;
		headMsg.uBussinessID = 11;
		headMsg.ucEncryptType = (char) ucEncryptType;
		headMsg.uiMsgType = uiMsgType;
		headMsg.uiClientType = 0;
		headMsg.uiSequenceNo = uiSequenceNo;
		headMsg.uiMsgRealLen = uiMsgRealLen;
		headMsg.uiMsgLen = uiMsgLen;
		
		
		byte[] ucEnterpriseIdArray = MTools.charToByte_Head(headMsg.ucEnterpriseId);
		byte[] uBussinessIDArray = MTools.charToByte_Head(headMsg.uBussinessID);
		byte[] ucEncryptTypeArray = MTools.charToByte_Head(headMsg.ucEncryptType);
		byte[] uiMsgTypeArray = MTools.intToBytes_Head(headMsg.uiMsgType);
		byte[] uiClientTypeArray = MTools.intToBytes_Head(headMsg.uiClientType);
		byte[] uiSequenceNoArray = MTools.intToBytes_Head(headMsg.uiSequenceNo);
		byte[] uiMsgRealLenArray = MTools.intToBytes_Head(headMsg.uiMsgRealLen);
		byte[] uiMsgLenArray = MTools.intToBytes_Head(headMsg.uiMsgLen);
		
		byte[] headMsgArray1 = MTools.copyByteArray(ucEnterpriseIdArray, uBussinessIDArray);
		byte[] headMsgArray2 = MTools.copyByteArray(headMsgArray1, ucEncryptTypeArray);
		byte[] headMsgArray3 = MTools.copyByteArray(headMsgArray2, uiMsgTypeArray);
		byte[] headMsgArray4 = MTools.copyByteArray(headMsgArray3, uiClientTypeArray);
		byte[] headMsgArray5 = MTools.copyByteArray(headMsgArray4, uiSequenceNoArray);
		byte[] headMsgArray6 = MTools.copyByteArray(headMsgArray5, uiMsgRealLenArray);
		byte[] headMsgArray7 = MTools.copyByteArray(headMsgArray6, uiMsgLenArray);
		
		return headMsgArray7;
	}

	
	/**
	 * 解析消息头
	 * @param proBuf
	 * @return
	 */
	public static HandleNetHeadMsg parseHeadMag(byte[] proBufHead){
		
		HandleNetHeadMsg headMsg = new HandleNetHeadMsg();
		
		headMsg.ucEnterpriseId = (char) proBufHead[0];
		headMsg.uBussinessID = (char) proBufHead[1];
		headMsg.ucEncryptType = (char) proBufHead[2];
		headMsg.uiMsgType = MTools.byteToInt(Arrays.copyOfRange(proBufHead, 3, 7));
		headMsg.uiClientType = MTools.byteToInt(Arrays.copyOfRange(proBufHead, 7, 11));
		headMsg.uiSequenceNo = MTools.byteToInt(Arrays.copyOfRange(proBufHead, 11, 15));
		headMsg.uiMsgRealLen = MTools.byteToInt(Arrays.copyOfRange(proBufHead, 15, 19));
		headMsg.uiMsgLen = MTools.byteToInt(Arrays.copyOfRange(proBufHead, 19, 23));
		
		return headMsg;
	}
	
	
	
	
}
