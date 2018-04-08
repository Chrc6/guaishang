package com.houwei.guaishang.activity;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.houwei.guaishang.R;
import com.houwei.guaishang.bean.BaseResponse;
import com.houwei.guaishang.bean.IntResponse;
import com.houwei.guaishang.bean.MissionlogBean;
import com.houwei.guaishang.bean.MissionlogListResponse;
import com.houwei.guaishang.bean.UserBean;
import com.houwei.guaishang.layout.ApplySuccessDialog;
import com.houwei.guaishang.layout.SureOrCancelDialog;
import com.houwei.guaishang.tools.HttpUtil;
import com.houwei.guaishang.tools.JsonParser;
import com.houwei.guaishang.tools.ShareUtil;
import com.houwei.guaishang.tools.ValueUtil;
import com.houwei.guaishang.views.CloudTagViewGroup;
import com.houwei.guaishang.views.PagedListView;
import com.houwei.guaishang.views.PullToRefreshBase;
import com.houwei.guaishang.views.PullToRefreshPagedListView;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MissionlogLinearLayout extends BaseLinearLayout {

	private int pageNumber = 1;
	private PullToRefreshPagedListView pullToRefreshView;
	private PagedListView listView;
	
	private View mEmptyLayout;
	private List<MissionlogBean> list;
	private MemberAdapter adapter;
	
	private MyHandler handler = new MyHandler(this);
	private static class MyHandler extends Handler {
		
		private WeakReference<MissionlogLinearLayout> reference;
		
	    public MyHandler(MissionlogLinearLayout context) {
	    	reference = new WeakReference<MissionlogLinearLayout>(context);
	    }
	    
	    @Override
		public void handleMessage(Message msg) {
	    	final MissionlogLinearLayout activity = (MissionlogLinearLayout) reference.get();
			if(activity == null){
				return;
			}
			activity.progress.dismiss();
			activity.pullToRefreshView.onRefreshComplete();
			switch (msg.what) {
			case BaseActivity.NETWORK_SUCCESS_DATA_RIGHT:
				MissionlogListResponse response = (MissionlogListResponse) msg.obj;
				if (response.isSuccess()) {
					activity.list = response.getData().getItems();
					activity.listView.onFinishLoading(response.getData().hasMore());
					activity.adapter = activity.new MemberAdapter(activity.mContext, activity.list);
					activity.listView.setAdapter(activity.adapter);
					activity.pageNumber = 2;
					activity.pullToRefreshView.setEmptyView(activity.list.isEmpty() ? activity.mEmptyLayout:null);
				} else {
					activity.listView.onFinishLoading(false);
					activity.showErrorToast(response.getMessage());
				}
				break;
			case BaseActivity.NETWORK_SUCCESS_PAGER_RIGHT:
				MissionlogListResponse pageResponse = (MissionlogListResponse) msg.obj;
				if (pageResponse.isSuccess()) {
					List<MissionlogBean> tempList = pageResponse.getData().getItems();
					activity.list.addAll(tempList);
					activity.adapter.notifyDataSetChanged();
					activity.listView.onFinishLoading(pageResponse.getData().hasMore());
					activity.pageNumber++;
				} else {
					activity.listView.onFinishLoading(false);
					activity.showErrorToast(pageResponse.getMessage());
				}
				break;
			default:
//				activity.showErrorToast();
				activity.listView.onFinishLoading(false);
				break;
			}
		}
	};


	public MissionlogLinearLayout(BaseActivity homeActivity,String api) {
		super(homeActivity);
		initView();
		initListener();
	}

	private void initView() {
		// TODO Auto-generated method stub

		LayoutInflater.from(mContext).inflate(
				R.layout.activity_listview_layout, this, true);
		
		initProgressDialog();
		
		pullToRefreshView = (PullToRefreshPagedListView) findViewById(R.id.listView);
		listView = pullToRefreshView.getRefreshableView();
		mEmptyLayout =  LayoutInflater.from(mContext).inflate(R.layout.listview_empty, null);
		
		refresh();
	}

	@Override
	public void refresh() {
		pullToRefreshView.setRefreshing();
		new Thread(run).start();
	}

	
	private Runnable run = new Runnable() {

		@SuppressWarnings("unchecked")
		public void run() {
			// TODO Auto-generated method stub
			MissionlogListResponse response = null;
			try {
				HashMap<String, String> data = new HashMap<String, String>();
				BaseActivity activity = (BaseActivity)mContext;
				data.put("memberid", activity.getUserID());
				data.put("page", "1");
				response = JsonParser.getMissionlogListResponse(HttpUtil.getMsg(HttpUtil.IP
						+"mission/missionlog?"+ HttpUtil.getData(data)));
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
			MissionlogListResponse response = null;
			try {
				HashMap<String, String> data = new HashMap<String, String>();
				BaseActivity activity = (BaseActivity)mContext;
				data.put("memberid", activity.getUserID());
				data.put("page", ""+pageNumber);
				response = JsonParser.getMissionlogListResponse(HttpUtil.getMsg(HttpUtil.IP
						+"missionlog?"+ HttpUtil.getData(data)));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (response != null) {
				handler.sendMessage(handler.obtainMessage(BaseActivity.NETWORK_SUCCESS_PAGER_RIGHT, response));
			} else {
				handler.sendEmptyMessage(BaseActivity.NETWORK_FAIL);
			}
		}
	};

	
	
	private void initListener() {
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
						// new Thread(listViewFirstPagerun).start();
						new Thread(run).start();
					}

					@Override
					public void onPullUpToRefresh() {

					}
				});
	}



	
	private class MemberAdapter extends BaseAdapter {
		private List<MissionlogBean> list;
		private LayoutInflater mInflater;

		public MemberAdapter(Context mContext, List<MissionlogBean> list) {
			this.list = list;
			mInflater = LayoutInflater.from(mContext);
		}

		public int getCount() {
			return list.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = mInflater.inflate(
						R.layout.listitem_moneylog, null);

				viewHolder.item_content = (TextView) convertView
						.findViewById(R.id.item_content);
				
				viewHolder.item_time = (TextView) convertView
						.findViewById(R.id.item_time);
				
				viewHolder.item_price = (TextView) convertView
						.findViewById(R.id.item_price);
				
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			final MissionlogBean tb = list.get(position);
			
			viewHolder.item_time.setText(""+tb.getCreatedAt());
			if (tb.getPoint()>0) {
				viewHolder.item_content.setText(tb.getName());
				viewHolder.item_price.setText("+"+tb.getPoint()+"金币");
				viewHolder.item_price.setTextColor(getResources().getColor(R.color.red_color));
			}else{
				viewHolder.item_content.setText(tb.getName());
				viewHolder.item_price.setText(""+tb.getPoint()+"金币");
				viewHolder.item_price.setTextColor(getResources().getColor(R.color.green_dark_color));
			}
			
			return convertView;
		}
	}
	
	private static class ViewHolder {
		private TextView item_content,item_time,item_price;
	}


	@Override
	public void onActivityDestory() {
		// TODO Auto-generated method stub
		
	}


}
