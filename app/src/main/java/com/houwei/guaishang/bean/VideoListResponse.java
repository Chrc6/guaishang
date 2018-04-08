package com.houwei.guaishang.bean;

import java.io.Serializable;
import java.util.List;

public class VideoListResponse extends BaseResponse implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3100299924567238696L;
	/**
	 * 
	 */
	private VideoListData data;

	public VideoListData getData() {
		return data;
	}

	public void setData(VideoListData data) {
		this.data = data;
	}

	public class VideoListData extends PagerData implements Serializable{

		/**
		 * 
		 */
		private static final long serialVersionUID = 2992714119670787789L;
		private List<VideoBean> items;

		public List<VideoBean> getItems() {
			return items;
		}

		public void setItems(List<VideoBean> items) {
			this.items = items;
		}

	}
}
