package com.houwei.guaishang.bean;

import java.io.Serializable;

import com.houwei.guaishang.tools.HttpUtil;

public class AvatarBean implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6693823233242834286L;
	private String original;
	private String small;
	public String getOriginal() {
		return original;
	}
	//client,如果用get开头，会导致fastjson把这个方法也存进去
	public String findOriginalUrl() {
		if (original==null || original.startsWith("http") || original.equals("")) {
			//为null 或者 “” 或者全路径。直接返回原始图片
			return original;
		} else {
			return HttpUtil.IP_NOAPI+original;
		}
	}
	public void setOriginal(String original) {
		this.original = original;
	}
	public String getSmall() {
		return small;
	}
	//client ,如果用get开头，会导致fastjson把这个方法也存进去
	public String findSmallUrl() {
		if (small==null || small.startsWith("http") || small.equals("")) {
			//为null 或者 “” 或者全路径。直接返回原始图片
			return small;
		} else {
			return HttpUtil.IP_NOAPI+small;
		}
	}
	public void setSmall(String small) {
		this.small = small;
	}
	public boolean isEmpty(){
	    return original==null || original.equals("");
	}
}
