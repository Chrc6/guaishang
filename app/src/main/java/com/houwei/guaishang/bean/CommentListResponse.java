package com.houwei.guaishang.bean;

import java.io.Serializable;
import java.util.List;

import com.houwei.guaishang.tools.ValueUtil;

public class CommentListResponse extends BaseResponse implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7513824294585762607L;
	private CommentListData data;

	public CommentListData getData() {
		return data;
	}

	public void setData(CommentListData data) {
		this.data = data;
	}

	public class CommentListData extends PagerData implements Serializable{

		/**
		 * 
		 */
		private static final long serialVersionUID = -2460583263164431996L;
		private List<CommentBean> items;

		public List<CommentBean> getItems() {
			return items;
		}

		public void setItems(List<CommentBean> items) {
			this.items = items;
		}

		
	}
}
