package com.houwei.guaishang.activity;

import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.houwei.guaishang.R;
import com.houwei.guaishang.bean.CommentPushBean;
import com.houwei.guaishang.bean.FansPushBean;
import com.houwei.guaishang.bean.TopicBean;
import com.houwei.guaishang.data.DBReq;
import com.houwei.guaishang.manager.ChatManager.OnMyActionMessageGetListener;
import com.houwei.guaishang.manager.ChatManager.OnMyActionMessageHadReadListener;
import com.houwei.guaishang.tools.LogUtil;
import com.houwei.guaishang.views.PullToRefreshBase;
import com.houwei.guaishang.views.PullToRefreshPagedListView;

public class TopicFootprintFragment extends TopicHomeFragment{
	

	/**
	 * 表示调用的接口是getlist（默认） 子类可以修改，比如返回getpraiselist 表示查询我赞的动态
	 */
	protected String getApi() {
		return "topic/getmyfootprintlist";
	}
	

	/**
	 * 如果重写并返回id，表示查询某人的发布过的动态列表
	 */
	public String getTargetMemberId() {
		return getUserID();
	}
	/**
	 * 设置跳转类型：评论详情和我的订单详情
	 */
	protected int getJumpType() {
		return 0;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		DEFAULT_REFRESH_TYPE = 1;
		super.onActivityCreated(savedInstanceState);
	}
}
