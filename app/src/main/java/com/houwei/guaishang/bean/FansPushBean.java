package com.houwei.guaishang.bean;

public class FansPushBean extends BasePushResult {

	/**
	 * 
	 */
	private static final long serialVersionUID = -543569128774417627L;
	private int _id;
	private String fansId;
	private String fansName;
	private String fansAvatar;
	
	public int get_id() {
		return _id;
	}
	public void set_id(int _id) {
		this._id = _id;
	}
	public String getFansId() {
		return fansId;
	}
	public void setFansId(String fansId) {
		this.fansId = fansId;
	}
	public String getFansName() {
		return fansName;
	}
	public void setFansName(String fansName) {
		this.fansName = fansName;
	}
	public String getFansAvatar() {
		return fansAvatar;
	}
	public void setFansAvatar(String fansAvatar) {
		this.fansAvatar = fansAvatar;
	}

}
