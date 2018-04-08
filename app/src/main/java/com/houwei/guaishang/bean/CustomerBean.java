package com.houwei.guaishang.bean;
public class CustomerBean{
	
	private String createdAt;
	private String topicid; 
	
	private float price;
	
	private int status;
	private String content;
	private String payerId;
	private String payerName;
	private AvatarBean payerAvatar;
	
	public String getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}
	public AvatarBean getPayerAvatar() {
		return payerAvatar;
	}
	public void setPayerAvatar(AvatarBean payerAvatar) {
		this.payerAvatar = payerAvatar;
	}
	public String getPayerName() {
		return payerName;
	}
	public void setPayerName(String payerName) {
		this.payerName = payerName;
	}
	public String getPayerId() {
		return payerId;
	}
	public void setPayerId(String payerId) {
		this.payerId = payerId;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}
	public String getTopicid() {
		return topicid;
	}
	public void setTopicid(String topicid) {
		this.topicid = topicid;
	}

}
