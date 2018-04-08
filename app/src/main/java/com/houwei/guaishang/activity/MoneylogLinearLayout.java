package com.houwei.guaishang.activity;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.houwei.guaishang.R;
import com.houwei.guaishang.bean.MoneylogBean;
import com.houwei.guaishang.bean.MoneylogListResponse;
import com.houwei.guaishang.tools.HttpUtil;
import com.houwei.guaishang.tools.JsonParser;
import com.houwei.guaishang.views.PagedListView;
import com.houwei.guaishang.views.PullToRefreshBase;
import com.houwei.guaishang.views.PullToRefreshPagedListView;

public class MoneylogLinearLayout extends BaseLinearLayout {

	private int pageNumber = 1;
	private PullToRefreshPagedListView pullToRefreshView;
	private PagedListView listView;
	
	private View mEmptyLayout;
	private List<MoneylogBean> list;
	private MemberAdapter adapter;
	
	private String api;
	
	private MyHandler handler = new MyHandler(this);
	private static class MyHandler extends Handler {
		
		private WeakReference<MoneylogLinearLayout> reference;
		
	    public MyHandler(MoneylogLinearLayout context) {
	    	reference = new WeakReference<MoneylogLinearLayout>(context);
	    }
	    
	    @Override
		public void handleMessage(Message msg) {
	    	final MoneylogLinearLayout activity = (MoneylogLinearLayout) reference.get();
			if(activity == null){
				return;
			}
			activity.progress.dismiss();
			activity.pullToRefreshView.onRefreshComplete();
			switch (msg.what) {
			case BaseActivity.NETWORK_SUCCESS_DATA_RIGHT:
				MoneylogListResponse response = (MoneylogListResponse) msg.obj;
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
				MoneylogListResponse pageResponse = (MoneylogListResponse) msg.obj;
				if (pageResponse.isSuccess()) {
					List<MoneylogBean> tempList = pageResponse.getData().getItems();
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


	public MoneylogLinearLayout(BaseActivity homeActivity,String api) {
		super(homeActivity);
		this.api = api;
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
			MoneylogListResponse response = null;
			try {
				HashMap<String, String> data = new HashMap<String, String>();
				BaseActivity activity = (BaseActivity)mContext;
				data.put("memberid", activity.getUserID());
				data.put("page", "1");
				response = JsonParser.getMoneylogListResponse(HttpUtil.getMsg(HttpUtil.IP
						+ api+"?"+ HttpUtil.getData(data)));
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
			MoneylogListResponse response = null;
			try {
				HashMap<String, String> data = new HashMap<String, String>();
				BaseActivity activity = (BaseActivity)mContext;
				data.put("memberid", activity.getUserID());
				data.put("page", ""+pageNumber);
				response = JsonParser.getMoneylogListResponse(HttpUtil.getMsg(HttpUtil.IP
						+ api+"?"+ HttpUtil.getData(data)));
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
		private List<MoneylogBean> list;
		private LayoutInflater mInflater;

		public MemberAdapter(Context mContext, List<MoneylogBean> list) {
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
			final MoneylogBean tb = list.get(position);
			
			viewHolder.item_time.setText(""+tb.getCreatedAt());
			if (tb.getPrice()>0) {
				viewHolder.item_content.setText("付款人："+tb.getPayerName());
				viewHolder.item_price.setText("+"+tb.getPrice()+"元");
				viewHolder.item_price.setTextColor(getResources().getColor(R.color.red_color));
			}else{
				viewHolder.item_content.setText("作者："+tb.getAuthorName());
				viewHolder.item_price.setText(""+tb.getPrice()+"元");
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
