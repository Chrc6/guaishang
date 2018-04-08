package com.houwei.guaishang.activity;

import java.lang.ref.WeakReference;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.houwei.guaishang.R;
import com.houwei.guaishang.bean.FriendShipListResponse;
import com.houwei.guaishang.bean.IntResponse;
import com.houwei.guaishang.bean.UserBean;
import com.houwei.guaishang.bean.FriendShipListResponse.FriendShipListData.FriendShipBean;
import com.houwei.guaishang.manager.FollowManager.FollowListener;
import com.houwei.guaishang.tools.HttpUtil;
import com.houwei.guaishang.tools.JsonParser;
import com.houwei.guaishang.tools.ValueUtil;
import com.houwei.guaishang.views.PagedListView;
import com.houwei.guaishang.views.PullToRefreshBase;
import com.houwei.guaishang.views.PullToRefreshPagedListView;

//关注 粉丝 列表
public class FriendShipActivity extends BaseActivity implements FollowListener {

	private int pageNumber = 1;
	private PullToRefreshPagedListView pullToRefreshView;
	private PagedListView listView;

	private String memberid;//要查看TA的粉丝 关注列表
	
	private boolean isFansList = true; //为false表示查看关注列表，true表示粉丝列表
	private View mEmptyLayout;
	private List<FriendShipBean> list;
	private MyListAdapter adapter;
	
	private MyHandler handler = new MyHandler(this);
	private static class MyHandler extends Handler {
		
		private WeakReference<Context> reference;
		
	    public MyHandler(Context context) {
	    	reference = new WeakReference<Context>(context);
	    }
	    
	    @Override
		public void handleMessage(Message msg) {
	    	final FriendShipActivity activity = (FriendShipActivity) reference.get();
			if(activity == null){
				return;
			}
			activity.pullToRefreshView.onRefreshComplete();
			switch (msg.what) {
			case NETWORK_SUCCESS_DATA_RIGHT:
				FriendShipListResponse response = (FriendShipListResponse) msg.obj;
				if (response.isSuccess()) {
					activity.list = response.getData().getItems();
					activity.listView.onFinishLoading(response.getData().hasMore());
					activity.adapter = activity.new MyListAdapter(activity, activity.list);
					activity.listView.setAdapter(activity.adapter);
					activity.pageNumber = 2;
					activity.pullToRefreshView.setEmptyView(activity.list.isEmpty() ? activity.mEmptyLayout:null);
					activity.resetFriendCount(response.getData().getTotal());
				} else {
					activity.listView.onFinishLoading(false);
					activity.showErrorToast(response.getMessage());
				}
				break;
			case NETWORK_SUCCESS_PAGER_RIGHT:
				FriendShipListResponse pageResponse = (FriendShipListResponse) msg.obj;
				if (pageResponse.isSuccess()) {
					List<FriendShipBean> tempList = pageResponse.getData()
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
				activity.showErrorToast();
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

	private void initView() {
		// TODO Auto-generated method stub
		initProgressDialog();
		getITopicApplication().getFollowManager().addFollowListener(this);
		
		//如果前一个界面没传memberid近来，表示查询userid的关注粉丝列表
		memberid = getIntent().getStringExtra("memberid");
		memberid = memberid==null?getUserID():memberid;
		
		isFansList = getIntent().getBooleanExtra("isFansList", false);
		
		if(memberid.equals(getUserID())){
			setTitleName(isFansList ? "我的粉丝":"我的关注");
			if (isFansList) {
				//只要进入粉丝界面，就把数据库里存的未读粉丝 读完
				getITopicApplication().getChatManager().readFansAction();
			}
		}else{
			setTitleName(isFansList ? "TA的粉丝":"TA的关注");
		}
		
		
		pullToRefreshView = (PullToRefreshPagedListView) findViewById(R.id.listView);
		listView = pullToRefreshView.getRefreshableView();
		mEmptyLayout =  LayoutInflater.from(this).inflate(R.layout.listview_empty, null);
		refresh();
	}

	private void refresh() {
		pullToRefreshView.setRefreshing();
		new Thread(run).start();
	}

	
	private Runnable run = new Runnable() {

		public void run() {
			// TODO Auto-generated method stub
			FriendShipListResponse response = null;
			try {
				HashMap<String, String> data = new HashMap<String, String>();
				data.put("userid", getUserID());
				data.put("memberid", memberid);
				data.put("isfan", isFansList?"1":"0");
				data.put("page", "1");
				response = JsonParser.getFriendShipListResponse(HttpUtil.getMsg(HttpUtil.IP
						+ "friendship/getlist"+"?"+ HttpUtil.getData(data)));
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
			FriendShipListResponse response = null;
			try {
				HashMap<String, String> data = new HashMap<String, String>();
				data.put("userid", getUserID());
				data.put("memberid", memberid);
				data.put("isfan", isFansList?"1":"0");
				data.put("page", ""+pageNumber);
				response = JsonParser.getFriendShipListResponse(HttpUtil.getMsg(HttpUtil.IP
						+ "friendship/getlist"+"?"+ HttpUtil.getData(data)));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (response != null) {
				handler.sendMessage(handler.obtainMessage(NETWORK_SUCCESS_PAGER_RIGHT, response));
			} else {
				handler.sendEmptyMessage(NETWORK_FAIL);
			}
		}
	};

	/**
	 * 为了弥补可能粉丝关注数与本地存的不一致
	 * 进来时候看看如果不一致，刷新本地的数据库
	 * @param totalCount
	 */
	private void resetFriendCount(int totalCount) {
		if ("".equals(getUserID()) || getUserID() == null){
			return ;
		}
		if (!getUserID().equals(memberid)) {
			return ;
		}
		UserBean ub = getITopicApplication().getMyUserBeanManager().getInstance();
		if (!memberid.equals("") && !memberid.equals(ub.getUserid())) {
			return;
		}
	
		try {
			if (isFansList) {			
				if (totalCount==ub.getFansCount()) {
					return ;
				}
				ub.setFansCount(totalCount);
			}else{
				if (totalCount==ub.getFollowsCount()) {
					return ;
				}
				ub.setFollowsCount(totalCount);
			}
			getITopicApplication().getMyUserBeanManager().storeUserInfo(ub);
			getITopicApplication().getMyUserBeanManager().notityUserInfoChanged(ub);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private void initListener() {
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
						// new Thread(listViewFirstPagerun).start();
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
				FriendShipBean bean = list.get(arg2-listView.getHeaderViewsCount());
				jumpToHisInfoActivity(bean.getMemberId(), bean.getMemberName(), bean.getMemberAvatar());
			}
		});
	}

	private class MyListAdapter extends BaseAdapter {
		private List<FriendShipBean> list;
		private LayoutInflater mInflater;
		private Context mContext;
		private ImageLoader imageLoader = ImageLoader.getInstance();

		public MyListAdapter(Context mContext, List<FriendShipBean> list) {

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
						R.layout.listitem_follower, null);

				viewHolder.item_name = (TextView) convertView
						.findViewById(R.id.item_name);

	
				viewHolder.item_btn = (Button) convertView
						.findViewById(R.id.item_btn);
			
				viewHolder.avator = (ImageView) convertView
						.findViewById(R.id.avator);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			final FriendShipBean tb = list.get(position);
			imageLoader.displayImage(tb.getMemberAvatar().findSmallUrl(), viewHolder.avator);
			viewHolder.item_name.setText(""+tb.getMemberName());
		
			viewHolder.item_btn.setText(ValueUtil.getRelationTypeString(tb.getFriendship()));
			viewHolder.item_btn.setBackgroundResource(ValueUtil.getRelationTypeDrawable(tb.getFriendship()));
//			viewHolder.item_btn.setTextColor(ValueUtil.getRelationTypeColor(FriendShipActivity.this, tb.getFriendship()));
			
			viewHolder.item_btn.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					progress.show();
					getITopicApplication().getFollowManager().followOnThread(getUserID(),
							tb.getMemberId());
				}
			});
			return convertView;
		}

		class ViewHolder {
			private TextView item_name;
			private ImageView avator;
			private Button item_btn;
		}
	}
	
	@Override
	public void FollowChanged(IntResponse followResponse) {
		// TODO Auto-generated method stub
		progress.dismiss();
		if (followResponse.isSuccess()) {			
			if (list != null) {
				for (FriendShipBean bean : list) {
					if (bean.getMemberId().equals(
							followResponse.getTag())) {
						bean.setFriendship(followResponse.getData());
						break;
					}
				}
				adapter.notifyDataSetChanged();
			}
		}else {
			showErrorToast(followResponse.getMessage());
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		getITopicApplication().getFollowManager().removeFollowListener(this);
		super.onDestroy();
	}
	
}
