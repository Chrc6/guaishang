package com.houwei.guaishang.bean;

import com.houwei.guaishang.tools.HttpUtil;
import com.houwei.guaishang.tools.ValueUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class HisUserBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9223008374056229177L;
	private String userid;
	private String mobile;
	private String name;
	private AvatarBean avatar;
	
	private String intro;//个人介绍
	private String sex;
	private String age;
	
	private String background;//背景图
	
	private String personalTags;
	
	private int followsCount;
	private int fansCount;
	private int topicCount;
	private String event;
	private String value;
	//查看他人接口才有
	private int friendship;

	//营业执照
	private String license;
	private List<AvatarBean> picture;

	private String bank;
	private String bankNum;
	private String address;
	private String gudingPhone;


	public AvatarBean getAvatar() {
		return avatar;
	}


	public void setAvatar(AvatarBean avatar) {
		this.avatar = avatar;
	}

	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}

	public String getBank() {
		return bank;
	}

	public void setBank(String bank) {
		this.bank = bank;
	}

	public String getBankNum() {
		return bankNum;
	}

	public void setBankNum(String bankNum) {
		this.bankNum = bankNum;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getGudingPhone() {
		return gudingPhone;
	}

	public void setGudingPhone(String gudingPhone) {
		this.gudingPhone = gudingPhone;
	}

	public String getUserid() {
		return userid;
	}


	public void setUserid(String userid) {
		this.userid = userid;
	}


	public String getMobile() {
		return mobile;
	}


	public void setMobile(String mobile) {
		this.mobile = mobile;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}




	public String getIntro() {
		return intro == null ? "":intro;
	}


	public void setIntro(String intro) {
		this.intro = intro;
	}


	public String getAge() {
		return age;
	}


	public void setAge(String age) {
		this.age = age;
	}



	public int getFollowsCount() {
		return followsCount;
	}


	public void setFollowsCount(int followsCount) {
		this.followsCount = followsCount < 0 ? 0:followsCount;
	}


	public int getFansCount() {
		return fansCount;
	}


	public void setFansCount(int fansCount) {
		this.fansCount = fansCount< 0 ? 0:fansCount;
	}


	public int getTopicCount() {
		return topicCount;
	}


	public void setTopicCount(int topicCount) {
		this.topicCount = topicCount< 0 ? 0:topicCount;
	}


	public int getFriendship() {
		return friendship;
	}


	public void setFriendship(int friendship) {
		this.friendship = friendship;
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

	public String getSex() {
		return sex;
	}


	public void setSex(String sex) {
		this.sex = sex;
	}


	public String getBackground() {
		return background.startsWith("http")?background:HttpUtil.IP_NOAPI+background;
	}

	public void setBackground(String background) {
		this.background = background == null?"":background;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public List<AvatarBean> getPicture() {
		return picture;
	}

	public void setPicture(List<AvatarBean> picture) {
		this.picture = picture;
	}
}
