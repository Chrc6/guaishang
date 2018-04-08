package com.houwei.guaishang.bean;

import java.io.Serializable;
import java.util.List;

public class FriendShipListResponse extends BaseResponse implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6373433945675310461L;
	private FriendShipListData data;

	public FriendShipListData getData() {
		return data;
	}

	public void setData(FriendShipListData data) {
		this.data = data;
	}

	public class FriendShipListData extends PagerData implements Serializable{

		/**
		 * 
		 */
		private static final long serialVersionUID = 2992714119670787789L;
		private List<FriendShipBean> items;

		public List<FriendShipBean> getItems() {
			return items;
		}

		public void setItems(List<FriendShipBean> items) {
			this.items = items;
		}

		public class FriendShipBean implements Serializable {

			/**
			 * 
			 */
			private static final long serialVersionUID = 6447032868098874099L;

			private String memberId;
			private String memberName;
			private AvatarBean memberAvatar;
			private int friendship;
			
			public String getMemberId() {
				return memberId;
			}
			public void setMemberId(String memberId) {
				this.memberId = memberId;
			}
			
			public AvatarBean getMemberAvatar() {
				return memberAvatar;
			}
			public void setMemberAvatar(AvatarBean memberAvatar) {
				this.memberAvatar = memberAvatar;
			}
			public int getFriendship() {
				return friendship;
			}
			public void setFriendship(int friendship) {
				this.friendship = friendship;
			}
			public String getMemberName() {
				return memberName;
			}
			public void setMemberName(String memberName) {
				this.memberName = memberName;
			}
			
	
			

		}

	}
}
