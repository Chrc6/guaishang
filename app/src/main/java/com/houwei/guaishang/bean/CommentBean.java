package com.houwei.guaishang.bean;

import java.io.Serializable;

import com.houwei.guaishang.tools.ValueUtil;

public class CommentBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2408390151461552572L;
	private String id; //评论的id
	private String memberId;
	private String memberName;
	private AvatarBean memberAvatar;
	private String content;
	private String createdAt;
	
	private String toMemberId;
	private String toMemberName;
	private AvatarBean toMemberAvatar;
	
	//client
	private String timeString;

	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMemberId() {
		return memberId;
	}
	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}
	public String getMemberName() {
		return memberName;
	}
	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
		try {
			timeString = ValueUtil.getTimeStringFromNow(ValueUtil.getTimeLong(createdAt));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			timeString = "";
			e.printStackTrace();
		}
	}
	
	public String getTimeString(){
		return timeString;
	}
	public AvatarBean getMemberAvatar() {
		return memberAvatar;
	}
	public void setMemberAvatar(AvatarBean memberAvatar) {
		this.memberAvatar = memberAvatar;
	}
	public String getToMemberName() {
		return toMemberName;
	}
	public void setToMemberName(String toMemberName) {
		this.toMemberName = toMemberName;
	}
	public String getToMemberId() {
		return toMemberId;
	}
	public void setToMemberId(String toMemberId) {
		this.toMemberId = toMemberId;
	}
	public AvatarBean getToMemberAvatar() {
		return toMemberAvatar;
	}
	public void setToMemberAvatar(AvatarBean toMemberAvatar) {
		this.toMemberAvatar = toMemberAvatar;
	}

	
	

}

