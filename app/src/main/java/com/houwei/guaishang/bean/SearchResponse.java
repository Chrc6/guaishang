package com.houwei.guaishang.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.houwei.guaishang.tools.ValueUtil;

public class SearchResponse extends BaseResponse implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7059373693994121107L;
	/**
	 * 
	 */
	private SearchData data;

	public SearchData getData() {
		return data;
	}

	public void setData(SearchData data) {
		this.data = data;
	}

	public class SearchData extends PagerData implements Serializable{

		/**
		 * 
		 */
		private static final long serialVersionUID = -4537554243986444289L;
		/**
		 * 
		 */
		private List<TopicBean> topicItems;
		private List<SearchedMemberBean> userItems;
		

		public List<SearchedMemberBean> getUserItems() {
			return userItems;
		}


		public void setUserItems(List<SearchedMemberBean> userItems) {
			this.userItems = userItems;
		}


		public List<TopicBean> getTopicItems() {
			return topicItems;
		}


		public void setTopicItems(List<TopicBean> topicItems) {
			this.topicItems = topicItems;
		}

	}
}
