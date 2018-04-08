package com.houwei.guaishang.activity;

import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.houwei.guaishang.R;
import com.houwei.guaishang.bean.CommentPushBean;
import com.houwei.guaishang.bean.FansPushBean;
import com.houwei.guaishang.bean.TopicBean;
import com.houwei.guaishang.data.DBReq;
import com.houwei.guaishang.manager.ChatManager.OnMyActionMessageGetListener;
import com.houwei.guaishang.manager.ChatManager.OnMyActionMessageHadReadListener;
import com.houwei.guaishang.views.PullToRefreshBase;
import com.houwei.guaishang.views.PullToRefreshPagedListView;

public class TopicMineActivity extends BaseActivity{
	
	
	private TopicMineFragment topicMineFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_framelayout);
		initView();
		initListener();
	}

	protected void initListener() {
		// TODO Auto-generated method stub

	}

	protected void initView() {
		// TODO Auto-generated method stub
		topicMineFragment = new TopicMineFragment();
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.add(R.id.container,topicMineFragment);    
		transaction.commit();
	}

	
}
