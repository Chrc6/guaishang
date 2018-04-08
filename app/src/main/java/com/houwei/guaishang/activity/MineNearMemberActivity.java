package com.houwei.guaishang.activity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.houwei.guaishang.R;
import com.houwei.guaishang.bean.LocationBean;
import com.houwei.guaishang.bean.NearMemberListResponse;
import com.houwei.guaishang.bean.NearMemberListResponse.NearMemberListData.NearMemberBean;
import com.houwei.guaishang.layout.SureOrCancelInterfaceDialog;
import com.houwei.guaishang.manager.MyLocationManager;
import com.houwei.guaishang.manager.MyLocationManager.LocationListener;
import com.houwei.guaishang.tools.HttpUtil;
import com.houwei.guaishang.tools.JsonParser;
import com.houwei.guaishang.views.PagedListView;
import com.houwei.guaishang.views.PullToRefreshBase;
import com.houwei.guaishang.views.PullToRefreshPagedListView;
import com.nostra13.universalimageloader.core.ImageLoader;


public class MineNearMemberActivity extends BaseActivity implements LocationListener {

	private int pageNumber = 1;
	private PullToRefreshPagedListView pullToRefreshView;
	private PagedListView listView;
	private View mEmptyLayout;
	private List<NearMemberBean> list;
	private MyListAdapter adapter;
	private LocationBean currentLocationBean;

	private MyHandler handler = new MyHandler(this);
	private static class MyHandler extends Handler {
		
		private WeakReference<Context> reference;
		
	    public MyHandler(Context context) {
	    	reference = new WeakReference<Context>(context);
	    }
	    
	    @Override
		public void handleMessage(Message msg) {
	    	final MineNearMemberActivity activity = (MineNearMemberActivity) reference.get();
			if(activity == null){
				return;
			}
			activity.pullToRefreshView.onRefreshComplete();
			switch (msg.what) {
			case NETWORK_SUCCESS_DATA_RIGHT:
				NearMemberListResponse response = (NearMemberListResponse) msg.obj;
				if (response.isSuccess()) {
					activity.list = response.getData().getItems();
					activity.listView.onFinishLoading(response.getData().hasMore());
					activity.adapter = activity.new MyListAdapter(activity, activity.list);
					activity.listView.setAdapter(activity.adapter);
					activity.pageNumber = 2;
					activity.pullToRefreshView.setEmptyView(activity.list.isEmpty() ? activity.mEmptyLayout:null);
				} else {
					activity.listView.onFinishLoading(false);
					activity.showErrorToast(response.getMessage());
				}
				break;
			case NETWORK_SUCCESS_PAGER_RIGHT:
				NearMemberListResponse pageResponse = (NearMemberListResponse) msg.obj;
				if (pageResponse.isSuccess()) {
					List<NearMemberBean> tempList = pageResponse.getData()
							.getItems();
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
				activity.listView.onFinishLoading(false);
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_listview_title_layout);
		initView();
		initListener();
	}

	protected void initView() {
		// TODO Auto-generated method stub
		initProgressDialog();
		setTitleName("附近的人");
		
		pullToRefreshView = (PullToRefreshPagedListView) findViewById(R.id.listView);
		listView = pullToRefreshView.getRefreshableView();
		
		mEmptyLayout =  LayoutInflater.from(this).inflate(R.layout.listview_empty, null);
		TextView iv = (TextView)mEmptyLayout.findViewById(R.id.textViewMessage);
		iv.setText("定位失败！\n请检查是否开启定位权限");

		getITopicApplication().getLocationManager().addLocationListener(MineNearMemberActivity.this);
		getITopicApplication().getLocationManager().startLoction(false);
		pullToRefreshView.setRefreshing();
	}

	protected void refresh() {
		pullToRefreshView.setRefreshing();
		new Thread(run).start();
	}
	
	private Runnable run = new Runnable() {

		@SuppressWarnings("unchecked")
		public void run() {
			// TODO Auto-generated method stub
			NearMemberListResponse response = null;
			try {
				HashMap<String, String> data = new HashMap<String, String>();
				data.put("userid", getUserID());
				data.put("page", "1");
				data.put("latitude", ""+currentLocationBean.getLatitude());
				data.put("longitude", ""+currentLocationBean.getLongitude());
				
				response = JsonParser.getNearMemberListResponse(HttpUtil.getMsg(HttpUtil.IP
						+ "user/near?"+HttpUtil.getData(data)));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (response != null) {
				handler.sendMessage(handler.obtainMessage(
						NETWORK_SUCCESS_DATA_RIGHT, response));
			} else {
				handler.sendEmptyMessage(NETWORK_FAIL);
			}
		}
	};

	private Runnable pageRun = new Runnable() {

		@SuppressWarnings("unchecked")
		public void run() {
			// TODO Auto-generated method stub
			NearMemberListResponse response = null;
			try {
				HashMap<String, String> data = new HashMap<String, String>();
				data.put("userid", getUserID());
				data.put("page", ""+pageNumber);
				data.put("latitude", ""+currentLocationBean.getLatitude());
				data.put("longitude", ""+currentLocationBean.getLongitude());
				
				response = JsonParser.getNearMemberListResponse(HttpUtil.getMsg(HttpUtil.IP
						+ "user/near?"+HttpUtil.getData(data)));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (response != null) {
				handler.sendMessage(handler.obtainMessage(
						NETWORK_SUCCESS_PAGER_RIGHT, response));
			} else {
				handler.sendEmptyMessage(NETWORK_FAIL);
			}
		}
	};

	@Override
	public void onLocationFail() {
		// TODO Auto-generated method stub
		//定位失败，请检查是否开启定位权限
		pullToRefreshView.onRefreshComplete();
		getITopicApplication().getLocationManager().removeLocationListener(this);
		
		adapter = new MyListAdapter(MineNearMemberActivity.this, new ArrayList<NearMemberBean>());
		listView.setAdapter(adapter);
		pullToRefreshView.setEmptyView(mEmptyLayout);

	    if (!MyLocationManager.isOpenService(this)) {
	    	//检查系统全局 定位服务 大权限
	    	SureOrCancelInterfaceDialog followDialog = new SureOrCancelInterfaceDialog(
					MineNearMemberActivity.this, "请开启定位服务", "去开启","取消",
					new SureOrCancelInterfaceDialog.ButtonClick() {

						@Override
						public void onSureButtonClick() {
							// TODO Auto-generated method stub
							MyLocationManager.openGPS(MineNearMemberActivity.this);
						}

						@Override
						public void onCancelButtonClick() {
							// TODO Auto-generated method stub

						}
					});
			followDialog.show();
	    }else{
	    	//小权限关闭
		}
	}

	@Override
	public void onLocationSuccess(LocationBean currentLocationBean) {
		// TODO Auto-generated method stub
		getITopicApplication().getLocationManager().removeLocationListener(this);
		this.currentLocationBean = currentLocationBean;
		refresh();
	}
	
	
	protected void initListener() {
		// TODO Auto-generated method stub
		BackButtonListener();
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
						
						if (currentLocationBean!=null) {
							new Thread(run).start();
						}else{
							getITopicApplication().getLocationManager().addLocationListener(MineNearMemberActivity.this);
							getITopicApplication().getLocationManager().startLoction(false);
						}
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
				NearMemberBean bean = list.get(arg2-listView.getHeaderViewsCount());
				jumpToHisInfoActivity(bean.getMemberId(), bean.getMemberName(), bean.getMemberAvatar());
			}
		});
	}

	private class MyListAdapter extends BaseAdapter {
		private List<NearMemberBean> list;
		private LayoutInflater mInflater;
		private Context mContext;
		private ImageLoader imageLoader = ImageLoader.getInstance();

		public MyListAdapter(Context mContext, List<NearMemberBean> list) {

			this.mContext = mContext;
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
						R.layout.listitem_member_nearly, null);

				viewHolder.item_name = (TextView) convertView
						.findViewById(R.id.item_name);

		
				viewHolder.item_memo2 = (TextView) convertView
						.findViewById(R.id.item_memo2);
		
				viewHolder.avator = (ImageView) convertView
						.findViewById(R.id.avator);
				
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			final NearMemberBean tb = list.get(position);
			imageLoader.displayImage(tb.getMemberAvatar().findSmallUrl(), viewHolder.avator);
			viewHolder.item_name.setText("" + tb.getMemberName());

			viewHolder.item_memo2.setText(""+tb.getDistanceString());
			
			return convertView;
		}

	}
	
	static class ViewHolder {
		private TextView item_name, item_memo2;
		private ImageView avator;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		getITopicApplication().getLocationManager().removeLocationListener(this);
		super.onDestroy();
	}

	

	
}
