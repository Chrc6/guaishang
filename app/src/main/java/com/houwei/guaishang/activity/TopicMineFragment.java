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
import com.houwei.guaishang.views.PullToRefreshBase;
import com.houwei.guaishang.views.PullToRefreshPagedListView;

public class TopicMineFragment extends BaseTopicFragment{
	
	private View mEmptyLayout;
	private PullToRefreshPagedListView pullToRefreshView;
	private String hisUserID;
	
	

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_listview_divider_title_layout, container, false);
    }
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
		initListener();
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		super.initView();
		hisUserID = getActivity().getIntent().getStringExtra(HisRootActivity.HIS_ID_KEY);
		initProgressDialog();
		setTitleName(getActivity().getIntent().getStringExtra("title"));

		pullToRefreshView = (PullToRefreshPagedListView) getView().findViewById(R.id.listView);
		listView = pullToRefreshView.getRefreshableView();
		mEmptyLayout = LayoutInflater.from(getActivity()).inflate(
				R.layout.listview_empty, null);
		
		pullToRefreshView.setRefreshing();
		refresh();
	}
	

	@SuppressLint("WrongViewCast")
	protected void initListener() {
		// TODO Auto-generated method stub
		super.initListener();
		BackButtonListener();
		TextView rightTV =  (TextView) getView().findViewById(R.id.title_right);
		rightTV.setText("写一条");
		pullToRefreshView
				.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2() {

					@Override
					public void onPullDownToRefresh() {
						// new Thread(listViewFirstPagerun).start();
						new Thread(run).start();
					}

					@Override
					public void onPullUpToRefresh() {

					}
				});

		getView().findViewById(R.id.title_right).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(getActivity(),
								TopicReleaseActivity.class);
						startActivityForResult(intent, 1);
					}
				});
	}

	/**
	 * 表示调用的接口是getlist（默认）
	 * 子类可以修改，比如返回getpraiselist 表示查询我赞的动态
	 */
	@Override
	protected String getApi(){
		return getActivity().getIntent().getStringExtra("api");
	}

	@Override
	protected int getJumpType() {
		return 1;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case TopicReleaseActivity.RELEASE_SUCCESS:
			refresh();
			break;
		default:
			break;
		}
	}


	@Override
	public void onNetWorkFinish(Message msg) {
		// TODO Auto-generated method stub
		pullToRefreshView.onRefreshComplete();

	}

	@Override
	public void onRefreshNetWorkSuccess(List<TopicBean> list) {
		// TODO Auto-generated method stub
		pullToRefreshView.setEmptyView(list.isEmpty() ? mEmptyLayout : null);
	}

	
	/**
	 * 如果重写并返回id，表示查询某人的发布过的动态列表
	 */
	@Override
	public String getTargetMemberId(){
		return hisUserID;
	}
	
}
