package com.houwei.guaishang.tools;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.houwei.guaishang.bean.AlbumListResponse;
import com.houwei.guaishang.bean.AvatarBean;
import com.houwei.guaishang.bean.BasePushResult;
import com.houwei.guaishang.bean.BaseResponse;
import com.houwei.guaishang.bean.CityBean;
import com.houwei.guaishang.bean.CommentListResponse;
import com.houwei.guaishang.bean.CommentPushBean;
import com.houwei.guaishang.bean.CustomerListResponse;
import com.houwei.guaishang.bean.FansPushBean;
import com.houwei.guaishang.bean.FloatResponse;
import com.houwei.guaishang.bean.FriendShipListResponse;
import com.houwei.guaishang.bean.GroupDetailResponse;
import com.houwei.guaishang.bean.HisInfoResponse;
import com.houwei.guaishang.bean.HomeInfoResponse;
import com.houwei.guaishang.bean.IntResponse;
import com.houwei.guaishang.bean.MissionProgressResponse;
import com.houwei.guaishang.bean.MissionlogListResponse;
import com.houwei.guaishang.bean.ModifyResponse;
import com.houwei.guaishang.bean.MoneylogListResponse;
import com.houwei.guaishang.bean.NameIDBean;
import com.houwei.guaishang.bean.NearMemberListResponse;
import com.houwei.guaishang.bean.PraiseResponse;
import com.houwei.guaishang.bean.SearchResponse;
import com.houwei.guaishang.bean.SearchedMemberListResponse;
import com.houwei.guaishang.bean.StringResponse;
import com.houwei.guaishang.bean.TopicListResponse;
import com.houwei.guaishang.bean.UserResponse;
import com.houwei.guaishang.bean.ValidateCodeResponse;
import com.houwei.guaishang.bean.VersionResponse;
import com.houwei.guaishang.bean.VideoBean;
import com.houwei.guaishang.bean.VideoListResponse;
import com.houwei.guaishang.huanxin.order.OrderInfoResponse;


public class JsonParser {

	public VersionResponse getVersionResponse(String json) {
		VersionResponse rr = JsonUtil.getObject(json, VersionResponse.class);
		return rr;
	}

	public static MoneylogListResponse getMoneylogListResponse(String json) {
		MoneylogListResponse rr = JsonUtil.getObject(json, MoneylogListResponse.class);
		return rr;
	}
	
	public static CustomerListResponse getCustomerListResponse(String json) {
		CustomerListResponse rr = JsonUtil.getObject(json, CustomerListResponse.class);
		return rr;
	}
	
	public static MissionlogListResponse getMissionlogListResponse(String json) {
		MissionlogListResponse rr = JsonUtil.getObject(json, MissionlogListResponse.class);
		return rr;
	}
	
	public static FloatResponse getFloatResponse(String json) {
		FloatResponse rr = JsonUtil.getObject(json, FloatResponse.class);
		return rr;
	}
	
	
	public static SearchResponse getSearchResponse(String json) {
		SearchResponse rr = JsonUtil.getObject(json, SearchResponse.class);
		return rr;
	}
	
	public static AlbumListResponse getAlbumListResponse(String json) {
		AlbumListResponse rr = JsonUtil.getObject(json, AlbumListResponse.class);
		return rr;
	}
	
	public static SearchedMemberListResponse getSearchedMemberListResponse(String json) {
		SearchedMemberListResponse rr = JsonUtil.getObject(json, SearchedMemberListResponse.class);
		return rr;
	}
	
	public static GroupDetailResponse getGroupDetailResponse(String json) {
		GroupDetailResponse rr = JsonUtil.getObject(json, GroupDetailResponse.class);
		return rr;
	}
	
	
	public static FriendShipListResponse getFriendShipListResponse(String json) {
		FriendShipListResponse rr = JsonUtil.getObject(json, FriendShipListResponse.class);
		return rr;
	}

	public static BasePushResult getBasePushResult(String json) {
		BasePushResult rr = JsonUtil.getObject(json, BasePushResult.class);
		return rr;
	}
	public static CommentPushBean getCommentPushBean(String json) {
		CommentPushBean rr = JsonUtil.getObject(json, CommentPushBean.class);
		return rr;
	}
	
	public static VideoListResponse getVideoListResponse(String json) {
		VideoListResponse rr = JsonUtil.getObject(json, VideoListResponse.class);
		return rr;
	}
	
	
	public static FansPushBean getFansPushBean(String json) {
		FansPushBean rr = JsonUtil.getObject(json, FansPushBean.class);
		return rr;
	}

	
	public ValidateCodeResponse getValidateCodeResponse(String json) {
		ValidateCodeResponse rr = JsonUtil.getObject(json, ValidateCodeResponse.class);
		return rr;
	}
	
	public static ModifyResponse getModifyResponse(String json) {
		ModifyResponse rr = JsonUtil.getObject(json, ModifyResponse.class);
		return rr;
	}
	
	
	public static StringResponse getStringResponse(String json) {
		StringResponse response = JsonUtil.getObject(json, StringResponse.class);
		return response;
	}	
	
	
	public static UserResponse  getUserResponse(String json) {
		UserResponse response = JsonUtil.getObject(json, UserResponse.class);
		return response;
	}
	public static IntResponse getIntResponse(String json) {
		IntResponse response = JsonUtil.getObject(json, IntResponse.class);
		return response;
	}
	public static OrderInfoResponse getOrderInfoResponse(String json) {
		OrderInfoResponse response = JsonUtil.getObject(json, OrderInfoResponse.class);
		return response;
	}
	
	public static TopicListResponse
	getTopicListResponse(String json) {
		TopicListResponse response = JsonUtil.getObject(json, TopicListResponse.class);
		return response;
	}
	public static CommentListResponse getCommentListResponse(String json) {
		CommentListResponse response = JsonUtil.getObject(json, CommentListResponse.class);
		return response;
	}
	public PraiseResponse getPraiseResponse(String json) {
		PraiseResponse response = JsonUtil.getObject(json, PraiseResponse.class);
		return response;
	}
	public static HisInfoResponse getHisInfoResponse(String json) {
		HisInfoResponse response = JsonUtil.getObject(json, HisInfoResponse.class);
		return response;
	}
	public static HomeInfoResponse getHomeInfoResponse(String json) {
		HomeInfoResponse response = JsonUtil.getObject(json, HomeInfoResponse.class);
		return response;
	}
	public static NearMemberListResponse getNearMemberListResponse(String json) {
		NearMemberListResponse response = JsonUtil.getObject(json, NearMemberListResponse.class);
		return response;
	}
	
	
	
	public static StringResponse getStringResponse2(String json) {
		StringResponse response = JsonUtil.getObject(json, StringResponse.class);
		return response;
	}	
	
	public static List<CityBean> getCityBean(String json) {
		List<CityBean> list = JsonUtil.getArray(json, CityBean.class);
		return list;
	}
	public static AvatarBean getAvatarBean(String json) {
		AvatarBean response = JsonUtil.getObject(json, AvatarBean.class);
		return response;
	}
	public static List<NameIDBean> getNameIDBean(String json) {
		List<NameIDBean> list = JsonUtil.getArray(json, NameIDBean.class);
		return list;
	}
	public static List<String> getStringList(String json) {
		List<String> list = JsonUtil.getArray(json, String.class);
		return list;
	}
	
	public static BaseResponse getBaseResponse(String json) {
		BaseResponse rr = JsonUtil.getObject(json, BaseResponse.class);
		return rr;
	}
	
	public static MissionProgressResponse getMissionProgressResponse(String json) {
		MissionProgressResponse rr = JsonUtil.getObject(json, MissionProgressResponse.class);
		return rr;
	}
	
	
	public static List<VideoBean> getVideoList(String json,String videoTypeId) throws JSONException {
		JSONObject obj = new JSONObject(json);
		JSONArray jarray = obj.getJSONArray(videoTypeId);
		List<VideoBean> list = new ArrayList<VideoBean>();
		for (int i = 0; i < jarray.length(); i++) {
			JSONObject videoObj = jarray.getJSONObject(i);
			VideoBean bean = new VideoBean();
			bean.setMp4_url(videoObj.getString("mp4_url"));
			bean.setCover(videoObj.getString("cover"));
			bean.setVid(videoObj.getString("vid"));
			bean.setTitle(videoObj.getString("title"));
			list.add(bean);
		}
		return list;
	}

}
