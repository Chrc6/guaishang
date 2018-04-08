package com.houwei.guaishang.bean;

import java.io.Serializable;
import java.util.List;

public class AlbumListResponse extends BaseResponse implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3100299924567238696L;
	/**
	 * 
	 */
	private AlbumListData data;

	public AlbumListData getData() {
		return data;
	}

	public void setData(AlbumListData data) {
		this.data = data;
	}

	public class AlbumListData extends PagerData implements Serializable{

		/**
		 * 
		 */
		private static final long serialVersionUID = 2992714119670787789L;
		private List<AlbumBean> items;

		public List<AlbumBean> getItems() {
			return items;
		}

		public void setItems(List<AlbumBean> items) {
			this.items = items;
		}

	}
}
