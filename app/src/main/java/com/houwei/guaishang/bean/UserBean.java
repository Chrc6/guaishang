package com.houwei.guaishang.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.houwei.guaishang.manager.OtherManager;
import com.houwei.guaishang.tools.HttpUtil;
import com.houwei.guaishang.tools.ValueUtil;


public class UserBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9223008374062009177L;
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
	private String picture;
	
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

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}
}
