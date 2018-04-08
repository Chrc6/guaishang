package com.houwei.guaishang.bean;

import java.io.Serializable;
import java.util.List;

import com.houwei.guaishang.R;
import com.houwei.guaishang.tools.ValueUtil;

public class TopicListResponse extends BaseResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5412718391658834399L;
	private TopicListData data;

	public TopicListData getData() {
		return data;
	}

	public void setData(TopicListData data) {
		this.data = data;
	}

	public class TopicListData extends PagerData implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = -7745768155652864477L;
		private List<TopicBean> items;

		public List<TopicBean> getItems() {
			return items;
		}

		public void setItems(List<TopicBean> items) {
			this.items = items;
		}

		@Override
		public String toString() {
			return "TopicListData{" +
					"items=" + items +
					'}';
		}
	}

	@Override
	public String toString() {
		return "TopicListResponse{" +
				"data=" + data +
				'}';
	}
}
