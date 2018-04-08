package com.houwei.guaishang.activity;

import java.lang.ref.WeakReference;
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

public class OriginalVideoLinearLayout extends BaseLinearLayout {

	private boolean hadGetFirstData;
	
	private int pageNumber = 1;
	private PullToRefreshPagedListView pullToRefreshView;
	private PagedListView listView;
	private View mEmptyLayout;
	private List<VideoBean> list;
	private VideoAdapter adapter;

	private MyHandler handler = new MyHandler(this);

	private static class MyHandler extends Handler {

		private WeakReference<OriginalVideoLinearLayout> reference;

		public MyHandler(OriginalVideoLinearLayout context) {
			reference = new WeakReference<OriginalVideoLinearLayout>(context);
		}

		@Override
		public void handleMessage(Message msg) {
			final OriginalVideoLinearLayout activity = reference.get();
			if (activity == null) {
				return;
			}
			activity.pullToRefreshView.onRefreshComplete();
			switch (msg.what) {
			case BaseActivity.NETWORK_SUCCESS_DATA_RIGHT:
				VideoListResponse otherresponse = (VideoListResponse) msg.obj;
				if (otherresponse.isSuccess()) {
					
					activity.hadGetFirstData = true; 
					
					activity.list = otherresponse.getData().getItems();
					activity.listView.onFinishLoading(otherresponse.getData()
							.hasMore());
					activity.adapter = new VideoAdapter(activity.mContext,
							activity.list);
					activity.listView.setAdapter(activity.adapter);
					activity.pageNumber = 2;
					activity.pullToRefreshView.setEmptyView(activity.list
							.isEmpty() ? activity.mEmptyLayout : null);
				} else {
					activity.listView.onFinishLoading(false);
					activity.showErrorToast(otherresponse.getMessage());
				}
				break;
			case BaseActivity.NETWORK_SUCCESS_PAGER_RIGHT:
				VideoListResponse pageResponse = (VideoListResponse) msg.obj;
				if (pageResponse.isSuccess()) {
					List<VideoBean> tempList = pageResponse.getData()
							.getItems();
					activity.list.addAll(tempList);
					activity.adapter.notifyDataSetChanged();
					activity.listView.onFinishLoading(pageResponse.getData()
							.hasMore());
					activity.pageNumber++;
				} else {
					activity.listView.onFinishLoading(false);
					activity.showErrorToast(pageResponse.getMessage());
				}
				break;
			default:
				activity.listView.onFinishLoading(false);
				break;
			}
		}
	};

	public OriginalVideoLinearLayout(BaseActivity homeActivity) {
		super(homeActivity);
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

	}

	@Override
	public void refresh() {
		if (!hadGetFirstData) {
			pullToRefreshView.setRefreshing();
			new Thread(run).start();
		}
	}

	public void refreshEnforced() {
		pullToRefreshView.setRefreshing();
		new Thread(run).start();
	}
	
	private Runnable run = new Runnable() {

		@SuppressWarnings("unchecked")
		public void run() {
			// TODO Auto-generated method stub
			VideoListResponse response = null;
			try {
				Map<String, String> data = new HashMap<String, String>();
				data.put("page", "1");
				response = JsonParser.getVideoListResponse(HttpUtil.postMsg(
						HttpUtil.getData(data), HttpUtil.IP
								+ "video/getlist"));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (response != null) {
				handler.sendMessage(handler.obtainMessage(
						BaseActivity.NETWORK_SUCCESS_DATA_RIGHT, response));
			} else {
				handler.sendEmptyMessage(BaseActivity.NETWORK_FAIL);
			}
		}
	};

	private Runnable pageRun = new Runnable() {

		@SuppressWarnings("unchecked")
		public void run() {
			// TODO Auto-generated method stub
			VideoListResponse response = null;
			try {
				HashMap<String, String> data = new HashMap<String, String>();
				data.put("page", "" + pageNumber);
				response = JsonParser.getVideoListResponse(HttpUtil.postMsg(
						HttpUtil.getData(data), HttpUtil.IP
								+ "video/getlist"));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (response != null) {
				handler.sendMessage(handler.obtainMessage(
						BaseActivity.NETWORK_SUCCESS_PAGER_RIGHT, response));
			} else {
				handler.sendEmptyMessage(BaseActivity.NETWORK_FAIL);
			}
		}
	};


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
				Intent i = new Intent(mContext,
						GroupPublicDetailsActivity.class);
				i.putExtra("VideoBean",
						list.get(arg2 - listView.getHeaderViewsCount()));
				mContext.startActivity(i);
			}
		});
	}

	@Override
	public void onActivityDestory() {
		// TODO Auto-generated method stub
	}

}
