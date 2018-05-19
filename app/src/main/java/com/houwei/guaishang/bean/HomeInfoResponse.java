package com.houwei.guaishang.bean;


public class HomeInfoResponse extends BaseResponse {
	private HisUserBean data;

	public HomeInfoResponse() {
	}

	public HisUserBean getData() {
		return data;
	}

	public void setData(HisUserBean data) {
		this.data = data;
	}
}
