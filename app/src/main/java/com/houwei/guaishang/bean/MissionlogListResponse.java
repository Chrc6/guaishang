package com.houwei.guaishang.bean;

import java.io.Serializable;
import java.util.List;

public class MissionlogListResponse extends BaseResponse implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6373433945675310461L;
	private MissionlogListData data;

	public MissionlogListData getData() {
		return data;
	}

	public void setData(MissionlogListData data) {
		this.data = data;
	}

	public class MissionlogListData extends PagerData implements Serializable{

		/**
		 * 
		 */
		private static final long serialVersionUID = 2992714119670787789L;
		private List<MissionlogBean> items;

		public List<MissionlogBean> getItems() {
			return items;
		}

		public void setItems(List<MissionlogBean> items) {
			this.items = items;
		}

	}
}
