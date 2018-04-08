package com.houwei.guaishang.bean;

import java.io.Serializable;
import java.util.List;

public class MoneylogListResponse extends BaseResponse implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6373433945675310461L;
	private MoneylogListData data;

	public MoneylogListData getData() {
		return data;
	}

	public void setData(MoneylogListData data) {
		this.data = data;
	}

	public class MoneylogListData extends PagerData implements Serializable{

		/**
		 * 
		 */
		private static final long serialVersionUID = 2992714119670787789L;
		private List<MoneylogBean> items;

		public List<MoneylogBean> getItems() {
			return items;
		}

		public void setItems(List<MoneylogBean> items) {
			this.items = items;
		}

	}
}
