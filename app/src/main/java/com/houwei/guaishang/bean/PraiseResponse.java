package com.houwei.guaishang.bean;


public class PraiseResponse extends BaseResponse {

	
	
	//client 全是手机端自定义参数
	private int praiseCnt; 
	private boolean stillPraise;
	private String topicid;

	public String getTopicid() {
		return topicid;
	}

	public void setTopicid(String topicid) {
		this.topicid = topicid;
	}

	public boolean isStillPraise() {
		return stillPraise;
	}

	public void setStillPraise(boolean stillPraise) {
		this.stillPraise = stillPraise;
	}

	public int getPraiseCnt() {
		return praiseCnt;
	}

	public void setPraiseCnt(int praiseCnt) {
		this.praiseCnt = praiseCnt;
	}

}
