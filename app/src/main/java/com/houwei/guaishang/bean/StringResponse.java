package com.houwei.guaishang.bean;

import java.io.Serializable;
import java.util.List;

public class StringResponse implements Serializable{
	private int code;
	private String message;
	private String data;
	private String tag;
	private List<PictureBean> pictures;

	public List<PictureBean> getPictures() {
		return pictures;
	}

	public void setPictures(List<PictureBean> pictures) {
		this.pictures = pictures;
	}

	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public boolean isSuccess(){
		return code == 1;
	}

	public class PictureBean {

		/**
		 * original : /media/topic/photo/2017-10-20/0aeabae3857810de.600_900.png
		 * small : /media/topic/photo/2017-10-20/0aeabae3857810de.small.png
		 */

		private String original;
		private String small;

		public String getOriginal() {
			return original;
		}

		public void setOriginal(String original) {
			this.original = original;
		}

		public String getSmall() {
			return small;
		}

		public void setSmall(String small) {
			this.small = small;
		}
	}
}
