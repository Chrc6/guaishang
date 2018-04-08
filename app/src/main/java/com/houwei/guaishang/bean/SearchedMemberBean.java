package com.houwei.guaishang.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.houwei.guaishang.tools.ValueUtil;

public class SearchedMemberBean implements Serializable{


	/**
	 * 
	 */
	private static final long serialVersionUID = 5989764060956802030L;
	private String memberId;
	private String memberName;
	private AvatarBean memberAvatar;
	private String personalTags;
	
	public String getMemberId() {
		return memberId;
	}
	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}
	
	public AvatarBean getMemberAvatar() {
		return memberAvatar;
	}
	public void setMemberAvatar(AvatarBean memberAvatar) {
		this.memberAvatar = memberAvatar;
	}
	public List<String> findPersonalTags() {
		return personalTags == null ? (new ArrayList<String>()):ValueUtil.StringToArrayList(personalTags);
	}
	public void setPersonalTags(String personalTags) {
		this.personalTags = personalTags;
	}
	public String getPersonalTags() {
		return personalTags;
	}

	public String getMemberName() {
		return memberName;
	}
	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}
}
