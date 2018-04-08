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

import com.houwei.guaishang.R;
import com.houwei.guaishang.bean.IntResponse;
import com.houwei.guaishang.bean.SearchedMemberBean;
import com.houwei.guaishang.bean.SearchedMemberListResponse;
import com.houwei.guaishang.bean.UserBean;
import com.houwei.guaishang.tools.HttpUtil;
import com.houwei.guaishang.tools.JsonParser;
import com.houwei.guaishang.tools.ValueUtil;
import com.houwei.guaishang.views.CloudTagViewGroup;
import com.houwei.guaishang.views.PagedListView;
import com.houwei.guaishang.views.PullToRefreshBase;
import com.houwei.guaishang.views.PullToRefreshPagedListView;
import com.nostra13.universalimageloader.core.ImageLoader;

public class SearchMoreMemberActivity extends BaseActivity {

	private int pageNumber = 1;
	private PullToRefreshPagedListView pullToRefreshView;
	private PagedListView listView;
	
	private View mEmptyLayout;
	private List<SearchedMemberBean> list;
	private MemberAdapter adapter;
	

	private MyHandler handler = new MyHandler(this);
	private static class MyHandler extends Handler {
		
		private WeakReference<Context> reference;
		
	    public MyHandler(Context context) {
	    	reference = new WeakReference<Context>(context);
	    }
	    
	    @Override
		public void handleMessage(Message msg) {
	    	final SearchMoreMemberActivity activity = (SearchMoreMemberActivity) reference.get();
			if(activity == null){
				return;
			}
			activity.pullToRefreshView.onRefreshComplete();
			switch (msg.what) {
			case NETWORK_SUCCESS_DATA_RIGHT:
				SearchedMemberListResponse response = (SearchedMemberListResponse) msg.obj;
				if (response.isSuccess()) {
					activity.list = response.getData().getItems();
					activity.listView.onFinishLoading(response.getData().hasMore());
					activity.adapter = activity.new MemberAdapter(activity, activity.list);
					activity.listView.setAdapter(activity.adapter);
					activity.pageNumber = 2;
					activity.pullToRefreshView.setEmptyView(activity.list.isEmpty() ? activity.mEmptyLayout:null);
				} else {
					activity.listView.onFinishLoading(false);
					activity.showErrorToast(response.getMessage());
				}
				break;
			case NETWORK_SUCCESS_PAGER_RIGHT:
				SearchedMemberListResponse pageResponse = (SearchedMemberListResponse) msg.obj;
				if (pageResponse.isSuccess()) {
					List<SearchedMemberBean> tempList = pageResponse.getData().getItems();
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
		setTitleName("搜索结果");
		
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

		@SuppressWarnings("unchecked")
		public void run() {
			// TODO Auto-generated method stub
			SearchedMemberListResponse response = null;
			try {
				HashMap<String, String> data = new HashMap<String, String>();
				data.put("userid", getUserID());
				data.put("keyword", getIntent().getStringExtra("keyword"));
				data.put("page", "1");
				response = JsonParser.getSearchedMemberListResponse(HttpUtil.getMsg(HttpUtil.IP
						+ "search/member"+"?"+ HttpUtil.getData(data)));
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
			SearchedMemberListResponse response = null;
			try {
				HashMap<String, String> data = new HashMap<String, String>();
				data.put("userid", getUserID());
				data.put("keyword", getIntent().getStringExtra("keyword"));
				data.put("page", ""+pageNumber);
				response = JsonParser.getSearchedMemberListResponse(HttpUtil.getMsg(HttpUtil.IP
						+ "search/member"+"?"+ HttpUtil.getData(data)));
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
				SearchedMemberBean bean = list.get(arg2-listView.getHeaderViewsCount());
				jumpToHisInfoActivity(bean.getMemberId(), bean.getMemberName(), bean.getMemberAvatar());
			}
		});
	}


	private class MemberAdapter extends BaseAdapter {
		private List<SearchedMemberBean> list;
		private LayoutInflater mInflater;
		private ImageLoader imageLoader = ImageLoader.getInstance();

		public MemberAdapter(Context mContext, List<SearchedMemberBean> list) {
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
						R.layout.listitem_search_member, null);

				viewHolder.item_name = (TextView) convertView
						.findViewById(R.id.item_name);
			
				viewHolder.avator = (ImageView) convertView
						.findViewById(R.id.avator);
				
				viewHolder.tagsViewGroup = (CloudTagViewGroup)convertView.findViewById(R.id.tagsViewGroup);
				
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			final SearchedMemberBean tb = list.get(position);
			imageLoader.displayImage(tb.getMemberAvatar().findSmallUrl(), viewHolder.avator);
			viewHolder.item_name.setText(""+tb.getMemberName());
			
			viewHolder.tagsViewGroup.setTags(SearchMoreMemberActivity.this, tb.findPersonalTags(),null);
			if (tb.findPersonalTags().isEmpty()) {			
				viewHolder.tagsViewGroup.addTag(SearchMoreMemberActivity.this, "未填写");
			}
			
			
			convertView.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					jumpToHisInfoActivity(tb.getMemberId(), tb.getMemberName(), tb.getMemberAvatar());
				}
			});
			
			return convertView;
		}


	}
	
	private static class ViewHolder {
		private TextView item_name;
		private ImageView avator;
		private CloudTagViewGroup tagsViewGroup;
	}

}
