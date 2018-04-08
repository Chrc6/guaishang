package com.houwei.guaishang.activity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.houwei.guaishang.R;
import com.houwei.guaishang.bean.VideoBean;
import com.houwei.guaishang.bean.VideoListResponse;
import com.houwei.guaishang.layout.VideoAdapter;
import com.houwei.guaishang.tools.HttpUtil;
import com.houwei.guaishang.tools.JsonParser;
import com.houwei.guaishang.views.PagedListView;
import com.houwei.guaishang.views.PullToRefreshBase;
import com.houwei.guaishang.views.PullToRefreshPagedListView;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;

public class VideoLinearLayout extends BaseLinearLayout {

	private final static int PAGE_SIZE = 20; 
	
	private boolean hadGetFirstData;
	
	private int pageNumber = 0;
	private PullToRefreshPagedListView pullToRefreshView;
	private PagedListView listView;
	private View mEmptyLayout;
	private List<VideoBean> list;
	private VideoAdapter adapter;

	private String videoTypeId;
	
	private MyHandler handler = new MyHandler(this);

	private static class MyHandler extends Handler {

		private WeakReference<VideoLinearLayout> reference;

		public MyHandler(VideoLinearLayout context) {
			reference = new WeakReference<VideoLinearLayout>(context);
		}

		@Override
		public void handleMessage(Message msg) {
			final VideoLinearLayout activity = reference.get();
			if (activity == null) {
				return;
			}
			activity.pullToRefreshView.onRefreshComplete();
			switch (msg.what) {
			case BaseActivity.NETWORK_SUCCESS_DATA_RIGHT:
				List<VideoBean> list = (List<VideoBean>) msg.obj;
//				activity.list = list;
//				activity.listView.onFinishLoading(list.size() >= PAGE_SIZE);
//				activity.adapter = new VideoAdapter(activity.mContext,
//						activity.list);
//				activity.listView.setAdapter(activity.adapter);
				
				activity.hadGetFirstData = true; 
				
				activity.list.clear();
				activity.list.addAll(list);
				activity.adapter.notifyDataSetChanged();
				activity.listView.onFinishLoading(list.size() >= PAGE_SIZE);
				
				activity.pageNumber = 1;
				activity.pullToRefreshView.setEmptyView(activity.list
						.isEmpty() ? activity.mEmptyLayout : null);
				break;
			case BaseActivity.NETWORK_SUCCESS_PAGER_RIGHT:
				List<VideoBean> tempList = (List<VideoBean>) msg.obj;
				activity.list.addAll(tempList);
				activity.adapter.notifyDataSetChanged();
				activity.listView.onFinishLoading(tempList.size() >= PAGE_SIZE);
				activity.pageNumber++;
				break;
			default:
				activity.listView.onFinishLoading(false);
				break;
			}
		}
	};

	public VideoLinearLayout(BaseActivity homeActivity,String videoTypeId) {
		super(homeActivity);
		this.videoTypeId = videoTypeId;
		initView();
		initListener();
	}

	protected void initView() {
		// TODO Auto-generated method stub
		initProgressDialog();

		LayoutInflater.from(mContext).inflate(
				R.layout.activity_listview_divider_layout, this, true);

		pullToRefreshView = (PullToRefreshPagedListView) findViewById(R.id.listView);
		listView = pullToRefreshView.getRefreshableView();

		mEmptyLayout = LayoutInflater.from(mContext).inflate(
				R.layout.listview_empty, null);

		list = new ArrayList<VideoBean>();
		listView.onFinishLoading(false);
		adapter = new VideoAdapter(mContext,list);
		listView.setAdapter(adapter);
		
	}
	

	private Runnable run = new Runnable() {

		@SuppressWarnings("unchecked")
		public void run() {
			// TODO Auto-generated method stub
			List<VideoBean> list = null;
			try {
				
				String url = "http://c.m.163.com/nc/video/list/"+getVideoTypeId()+"/y/1-20.html";
				
				list = JsonParser.getVideoList(HttpUtil.getMsg(url), getVideoTypeId());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (list != null) {
				handler.sendMessage(handler.obtainMessage(
						BaseActivity.NETWORK_SUCCESS_DATA_RIGHT, list));
			} else {
				handler.sendEmptyMessage(BaseActivity.NETWORK_FAIL);
			}
		}
	};

	private Runnable pageRun = new Runnable() {

		@SuppressWarnings("unchecked")
		public void run() {
			// TODO Auto-generated method stub
			List<VideoBean> list = null;
			try {
				int cursor = ((pageNumber * PAGE_SIZE) + 1);
				int cursor_end = ((pageNumber + 1) * PAGE_SIZE);
				String pageString = ""+cursor + "-" + cursor_end;
				
				String url = "http://c.m.163.com/nc/video/list/"+getVideoTypeId()+"/y/"+pageString+".html";
				
				list = JsonParser.getVideoList(HttpUtil.getMsg(url), getVideoTypeId());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (list != null) {
				handler.sendMessage(handler.obtainMessage(
						BaseActivity.NETWORK_SUCCESS_PAGER_RIGHT, list));
			} else {
				handler.sendEmptyMessage(BaseActivity.NETWORK_FAIL);
			}
		}
	};

	@Override
	public void refresh() {
		if (!hadGetFirstData) {
			pullToRefreshView.setRefreshing();
			new Thread(run).start();
		}
	}

	protected void initListener() {
		// TODO Auto-generated method stub
		listView.setOnLoadMoreListener(new PagedListView.OnLoadMoreListener() {

			@Override
			public void onLoadMoreItems() {
				// TODO Auto-generated method stub
				new Thread(pageRun).start();

			}
		});
		pullToRefreshView
				.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2() {

					@Override
					public void onPullDownToRefresh() {
						new Thread(run).start();
					}

					@Override
					public void onPullUpToRefresh() {

					}
				});
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Intent i = new Intent(mContext,VideoCommentsActivity.class);
				i.putExtra("VideoBean",list.get(arg2 - listView.getHeaderViewsCount()));
				mContext.startActivity(i);
			}
		});
	}

	@Override
	public void onActivityDestory() {
		// TODO Auto-generated method stub
	}

	public String getVideoTypeId() {
		return videoTypeId;
	}

	public void setVideoTypeId(String videoTypeId) {
		this.videoTypeId = videoTypeId;
	}

}
