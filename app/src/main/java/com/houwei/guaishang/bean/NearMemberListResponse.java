package com.houwei.guaishang.bean;

import java.io.Serializable;
import java.util.List;

public class NearMemberListResponse extends BaseResponse implements
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6373433945675310461L;
	private NearMemberListData data;

	public NearMemberListData getData() {
		return data;
	}

	public void setData(NearMemberListData data) {
		this.data = data;
	}

	public class NearMemberListData extends PagerData implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 2992714119670787789L;
		private List<NearMemberBean> items;

		public List<NearMemberBean> getItems() {
			return items;
		}

		public void setItems(List<NearMemberBean> items) {
			this.items = items;
		}

		public class NearMemberBean implements Serializable {

			/**
			 * 
			 */
			private static final long serialVersionUID = 6447032868098874099L;

			private String memberId;
			private String memberName;
			private AvatarBean memberAvatar;
			private float distance;

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

			public String getMemberName() {
				return memberName;
			}

			public void setMemberName(String memberName) {
				this.memberName = memberName;
			}

			public float getDistance() {
				return distance;
			}

			public void setDistance(float distance) {
				this.distance = distance;
			}

			public String getDistanceString() {
				if (distance > 1000) {
					float km = (float) (Math.round((distance / 1000.0) * 10)) / 10;
					return km + "公里";
				} else if (distance < 50) {
					return "50米以内";
				} else {
					return distance + "米";
				}
			}

		}

	}
}
