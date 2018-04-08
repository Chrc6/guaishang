package com.houwei.guaishang.bean;

import java.io.Serializable;
import java.util.List;

public class CustomerListResponse extends BaseResponse implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6373433945675310461L;
	private CustomerListData data;

	public CustomerListData getData() {
		return data;
	}

	public void setData(CustomerListData data) {
		this.data = data;
	}

	public class CustomerListData extends PagerData implements Serializable{

		/**
		 * 
		 */
		private static final long serialVersionUID = 2992714119670787789L;
		private List<CustomerBean> items;

		public List<CustomerBean> getItems() {
			return items;
		}

		public void setItems(List<CustomerBean> items) {
			this.items = items;
		}

	}
}
