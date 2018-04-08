package com.houwei.guaishang.bean;

import com.houwei.guaishang.tools.JsonUtil;
import com.houwei.guaishang.tools.ValueUtil;

public class CommentPushBean  extends BasePushResult {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3647038575171873252L;

	
	private String commentMemberId;
	private String commentContent;
	private String commentMemberName;
	private AvatarBean commentMemberAvatar;
	private String content;
	private String createdAt;
	
	//client
	private int _id;
	private String timeString;
	private boolean isUnReaded;
	private TopicBean topicBean;
	
	public void initContentBean() {
		topicBean = JsonUtil.getObject(content, TopicBean.class);
	}
	
	
	public String getCommentMemberId() {
		return commentMemberId;
	}
	public void setCommentMemberId(String commentMemberId) {
		this.commentMemberId = commentMemberId;
	}
	public String getCommentContent() {
		return commentContent;
	}
	public void setCommentContent(String commentContent) {
		this.commentContent = commentContent;
	}
	public String getCommentMemberName() {
		return commentMemberName;
	}
	public void setCommentMemberName(String commentMemberName) {
		this.commentMemberName = commentMemberName;
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
	public int get_id() {
		return _id;
	}
	public void set_id(int _id) {
		this._id = _id;
	}
	public String getTimeString() {
		return timeString;
	}
	public void setTimeString(String timeString) {
		this.timeString = timeString;
	}
	public boolean isUnReaded() {
		return isUnReaded;
	}
	public void setUnReaded(boolean isUnReaded) {
		this.isUnReaded = isUnReaded;
	}
	public TopicBean getTopicBean() {
		return topicBean;
	}
	public void setTopicBean(TopicBean topicBean) {
		this.topicBean = topicBean;
	}


	public AvatarBean getCommentMemberAvatar() {
		return commentMemberAvatar;
	}


	public void setCommentMemberAvatar(AvatarBean commentMemberAvatar) {
		this.commentMemberAvatar = commentMemberAvatar;
	}
	
	
}
