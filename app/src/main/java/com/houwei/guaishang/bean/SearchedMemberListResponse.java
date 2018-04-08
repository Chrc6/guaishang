package com.houwei.guaishang.bean;

import java.io.Serializable;
import java.util.List;

public class SearchedMemberListResponse extends BaseResponse implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6373433945675310461L;
	private SearchedMemberListData data;

	public SearchedMemberListData getData() {
		return data;
	}

	public void setData(SearchedMemberListData data) {
		this.data = data;
	}

	public class SearchedMemberListData extends PagerData implements Serializable{

		/**
		 * 
		 */
		private static final long serialVersionUID = 2992714119670787789L;
		private List<SearchedMemberBean> items;

		public List<SearchedMemberBean> getItems() {
			return items;
		}

		public void setItems(List<SearchedMemberBean> items) {
			this.items = items;
		}

	}
}
