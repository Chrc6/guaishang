package com.houwei.guaishang.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.houwei.guaishang.R;
import com.houwei.guaishang.bean.TopicBean;
import com.houwei.guaishang.bean.event.TopicHomeEvent;
import com.houwei.guaishang.views.PullToRefreshBase;
import com.houwei.guaishang.views.PullToRefreshPagedListView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
//首页fragment
public class TopicHomeFragment extends BaseTopicFragment{
	
	private View mEmptyLayout;
	private PullToRefreshPagedListView pullToRefreshView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		DEFAULT_REFRESH_TYPE = 0;
        return inflater.inflate(R.layout.activity_listview_divider_layout, container, false);
    }
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		EventBus.getDefault().register(this);
		initView();
		initListener();
	}

	@Override
	public void onResume() {
		super.onResume();

	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		super.initView();
		pullToRefreshView = (PullToRefreshPagedListView) getView().findViewById(R.id.listView);
		listView = pullToRefreshView.getRefreshableView();

		if (DEFAULT_REFRESH_TYPE == 1) {
			mEmptyLayout = LayoutInflater.from(getActivity()).inflate(
					R.layout.listview_orders_empty, null);
		} else {
			mEmptyLayout = LayoutInflater.from(getActivity()).inflate(
					R.layout.listview_empty, null);
		}

		pullToRefreshView.setRefreshing();
		refresh();
	}

	@Override
	protected int getJumpType() {
		return 0;
	}

	@SuppressLint("WrongViewCast")
	protected void initListener() {
		// TODO Auto-generated method stub
		super.initListener();
		pullToRefreshView
				.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2() {

					@Override
					public void onPullDownToRefresh() {
						// new Thread(listViewFirstPagerun).start();
						new Thread(run).start();refresh();
					}

					@Override
					public void onPullUpToRefresh() {

					}
				});
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

	//订阅方法，当接收到事件的时候，会调用该方法
	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onEvent(TopicHomeEvent event){
    	refresh();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}
}
