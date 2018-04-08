package com.houwei.guaishang.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.houwei.guaishang.tools.HttpUtil;
import com.houwei.guaishang.tools.ValueUtil;

public class VideoBean implements Serializable{

	private static final long serialVersionUID = 5989764060956802030L;
	private String mp4_url;
	private String cover;
	private String title;
	private String vid;
	
	public String getVid() {
		return vid;
	}
	public void setVid(String vid) {
		this.vid = vid;
	}
	public String getTitle() {
		return (title==null||title.equals(""))?"无题":title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getCover() {
		return cover.startsWith("http")?cover:HttpUtil.IP_NOAPI+cover;
	}
	public void setCover(String cover) {
		this.cover = cover;
	}
	public String getMp4_url() {
		return mp4_url.startsWith("http")?mp4_url:HttpUtil.IP_NOAPI+mp4_url;
	}
	public void setMp4_url(String mp4_url) {
		this.mp4_url = mp4_url;
	}
	
}
