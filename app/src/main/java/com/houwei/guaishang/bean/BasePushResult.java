package com.houwei.guaishang.bean;

import java.io.Serializable;

/**
 * 推送的内容
 */
@SuppressWarnings("serial")
public class BasePushResult implements Serializable {

	public final static int PushStateHadRead = 0;
	public final static int PushStateUnRead = 1; 
	
	
	public static final int FANS_PUSH = 1;
	public static final int TOPIC_COMMENT_PUSH = 2;

	private int pushType;

	//client
	private boolean isUnReaded;
	
	public boolean isUnReaded() {
		return isUnReaded;
	}

	public void setUnReaded(boolean isUnReaded) {
		this.isUnReaded = isUnReaded;
	}
	
	public int getPushType() {
		return pushType;
	}

	public void setPushType(int pushType) {
		this.pushType = pushType;
	}
}
