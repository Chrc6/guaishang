package com.houwei.guaishang.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.TextUtils;
import android.util.Log;

import com.houwei.guaishang.R;
import com.houwei.guaishang.manager.HomeManager;
import com.houwei.guaishang.tools.HttpUtil;
import com.houwei.guaishang.tools.ValueUtil;

public class TopicBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2408390151461552572L;
	private String topicId;
	private String memberId;
	private String memberName;//发布人姓名
	private AvatarBean memberAvatar;//发布人头像model
	private String content;//内容
	private List<AvatarBean> picture;//9张图
	private String createdAt;//发布时间
	private String address;//地址
	private String mobile;//地址

	private String isOffer;//单的状态
	private String brand;//行业名称
	private String jifen;
	private int shareNum;
	private boolean praised;//我是否点过赞
	private int praiseCount;
	private int commentCount;
//	private List<CommentBean> praiseMembers;//点赞的人，可能为nil
	private List<CommentBean> comments;//最多3条评论，可能为nil
	private Payment payment;//订单信息
	//无用
	private int redpacket;//0普通动态，1红包照片
	
	private float price;
	
	private int status;
	
	private String videourl; 
	private String cover;
	private String bigcover;
	private int friendship;

	
	// client 自己定义的
	private String timeString;
	private int photoOriginalWidth;
	private int photoOriginalHeight;

	//距离，之所以不用float去接受，是因为只有near接口才返回distance，别的接口distance是null
	//用 ==null来判断是不是near接口，以区分距离正好==0的情况
	private String distance;
	
	//只有“我购买的” 才有这个参数
	private String paidCreatedAt;
	private String orderid;
	private String nowRob;
	private String setRob;
	private String sumPrice;
	private String dealNum;
	private List<OffersBean.OfferBean> offerPrice;


	public List<OffersBean.OfferBean> getOfferPrice() {
		return offerPrice;
	}

	public void setOfferPrice(List<OffersBean.OfferBean> offerPrice) {
		this.offerPrice = offerPrice;
	}

	public String getDealNum() {
		return dealNum;
	}

	public void setDealNum(String dealNum) {
		this.dealNum = dealNum;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getJifen() {
		return jifen;
	}

	public void setJifen(String jifen) {
		this.jifen = jifen;
	}

	public String getSumPrice() {
		return sumPrice;
	}

	public void setSumPrice(String sumPrice) {
		this.sumPrice = sumPrice;
	}

	public String getNowRob() {
		return nowRob;
	}

	public void setNowRob(String nowRob) {
		this.nowRob = nowRob;
	}

	public String getSetRob() {
		return setRob;
	}

	public void setSetRob(String setRob) {
		this.setRob = setRob;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}


	public String getIsOffer() {
		return isOffer;
	}

	public void setIsOffer(String isOffer) {
		this.isOffer = isOffer;
	}

	public AvatarBean getMemberAvatar() {
		return memberAvatar;
	}

	public void setMemberAvatar(AvatarBean memberAvatar) {
		this.memberAvatar = memberAvatar;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
		try {
			timeString = ValueUtil.getTimeStringFromNow(ValueUtil
					.getTimeLong(createdAt));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			timeString = "";
			e.printStackTrace();
		}
	}

	public int getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}

	public String getTopicId() {
		return topicId;
	}

	public void setTopicId(String topicId) {
		this.topicId = topicId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTimeString() {
		return timeString;
	}

	public int getShareNum() {
		return shareNum;
	}

	public void setShareNum(int shareNum) {
		this.shareNum = shareNum;
	}

	public boolean isPraised() {
		return praised;
	}

	public void setPraised(boolean praised) {
		this.praised = praised;
	}

	public Payment getPayment() {
		return payment;
	}

	public void setPayment(Payment payment) {
		this.payment = payment;
	}

	public int getZanDrawable() {
		return praised ? R.drawable.listitem_praise
				: R.drawable.listitem_unpraise;
	}

	public int getZanColor() {
		return praised ? 0xffF46533 : 0xff999999;
	}

	public int getPraiseCount() {
		return praiseCount;
	}

	public void setPraiseCount(int praiseCount) {
		this.praiseCount = praiseCount;
	}

	public int getPhotoOriginalHeight() {
		return photoOriginalHeight;
	}

	public void setPhotoOriginalHeight(int photoOriginalHeight) {
		this.photoOriginalHeight = photoOriginalHeight;
	}

	public int getPhotoOriginalWidth() {
		return photoOriginalWidth;
	}

	public void setPhotoOriginalWidth(int photoOriginalWidth) {
		this.photoOriginalWidth = photoOriginalWidth;
	}

	public List<AvatarBean> getPicture() {
		return picture;
	}

	public void setPicture(List<AvatarBean> picture) {
		this.picture = picture;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}
	
	//转为可以直接显示距离
	public String getDistanceString() {
		try {
			float distanceFloat = Float.parseFloat(distance);
			if (distanceFloat > 1000) {
				float km = (float) (Math.round((distanceFloat / 1000.0) * 10)) / 10;
				return km + "公里";
			} else if (distanceFloat < 50) {
				return "50米以内";
			} else {
				return distanceFloat + "米";
			}
		} catch (Exception e) {
			// TODO: handle exception
			// 如果不是topic/near接口。服务器不会返回distance
		}
		return address;
	}

	public List<CommentBean> getComments() {
		return comments;
	}

	public void setComments(List<CommentBean> comments) {
		this.comments = comments;
	}

	
	public boolean needPayPhoto(HomeManager homeManager) {
		return redpacket == 1 && homeManager.checkNeedPay(topicId, memberId);
	}

	public int getRedpacket() {
		return redpacket;
	}

	public void setRedpacket(int redpacket) {
		this.redpacket = redpacket;
	}

	public String getVideourl() {
		return videourl;
	}

	public void setVideourl(String videourl) {
		this.videourl = videourl;
	}
	
	public String getCover() {
		if(TextUtils.isEmpty(cover)){
			return null;
		}
		return cover.startsWith("http")?cover:HttpUtil.IP_NOAPI+cover;
	}
	
	public void setCover(String cover) {
		this.cover = cover;
		
		if (cover != null && !cover.equals("")) {
			try {
				String urlWithOutPng = cover
						.substring(0,cover.lastIndexOf("."));
				String str = urlWithOutPng.substring(
						urlWithOutPng.lastIndexOf(".") + 1,
						urlWithOutPng.length());
				String[] zu = str.split("\\_");
				photoOriginalWidth = Integer.parseInt(zu[0]);
				photoOriginalHeight = Integer.parseInt(zu[1]);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public String getBigcover() {
		if(TextUtils.isEmpty(cover)){
			return null;
		}
		return cover.startsWith("http")?cover:HttpUtil.IP_NOAPI+cover;
	}

	public void setBigcover(String cover) {
		this.cover = cover;

		if (cover != null && !cover.equals("")) {
			try {
				String urlWithOutPng = cover
						.substring(0,cover.lastIndexOf("."));
				String str = urlWithOutPng.substring(
						urlWithOutPng.lastIndexOf(".") + 1,
						urlWithOutPng.length());
				String[] zu = str.split("\\_");
				photoOriginalWidth = Integer.parseInt(zu[0]);
				photoOriginalHeight = Integer.parseInt(zu[1]);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getPaidCreatedAt() {
		return paidCreatedAt;
	}

	public void setPaidCreatedAt(String paidCreatedAt) {
		this.paidCreatedAt = paidCreatedAt;
	}

	public String getOrderid() {
		return orderid;
	}

	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}

	public int getFriendship() {
		return friendship;
	}

	public void setFriendship(int friendship) {
		this.friendship = friendship;
	}

	@Override
	public String toString() {
		return "TopicBean{" +
				"topicId='" + topicId + '\'' +
				", memberId='" + memberId + '\'' +
				", memberName='" + memberName + '\'' +
				", memberAvatar=" + memberAvatar +
				", content='" + content + '\'' +
				", picture=" + picture +
				", createdAt='" + createdAt + '\'' +
				", address='" + address + '\'' +
				", mobile='" + mobile + '\'' +
				", shareNum=" + shareNum +
				", praised=" + praised +
				", praiseCount=" + praiseCount +
				", commentCount=" + commentCount +
				", comments=" + comments +
				", redpacket=" + redpacket +
				", price=" + price +
				", status=" + status +
				", videourl='" + videourl + '\'' +
				", cover='" + cover + '\'' +
				", friendship=" + friendship +
				", timeString='" + timeString + '\'' +
				", photoOriginalWidth=" + photoOriginalWidth +
				", photoOriginalHeight=" + photoOriginalHeight +
				", distance='" + distance + '\'' +
				", paidCreatedAt='" + paidCreatedAt + '\'' +
				", orderid='" + orderid + '\'' +
				", nowRob='" + nowRob + '\'' +
				", setRob='" + setRob + '\'' +
				", sumPrice='" + sumPrice + '\'' +
				", dealNum='" + dealNum + '\'' +
				", offerPrice=" + offerPrice +
				'}';
	}
}
