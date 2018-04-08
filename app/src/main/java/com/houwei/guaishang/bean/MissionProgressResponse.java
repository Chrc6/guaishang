package com.houwei.guaishang.bean;
import java.util.Map;


public class MissionProgressResponse extends BaseResponse {

	/**
	 * 
	 */
	private Map<String, Integer> data;

	public Map<String, Integer> getData() {
		return data;
	}

	public void setData(Map<String, Integer> data) {
		this.data = data;
	}

}
