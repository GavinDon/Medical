package com.sltj.medical.bean;

/**
 * Author linan Date：2016年6月20日 下午3:03:19
 */
public class SingleInfo {

	private SingleInfo() {

	}

	private static SingleInfo mSingleInfo;

	/**
	 * 单例类。
	 * 
	 * @return 此类对象
	 */
	public static SingleInfo getInstance() {
		if (mSingleInfo == null) {
			synchronized (SingleInfo.class) {
				mSingleInfo = new SingleInfo();
			}
		}
		return mSingleInfo;
	}
}
