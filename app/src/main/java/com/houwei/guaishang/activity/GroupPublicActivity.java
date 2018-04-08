package com.houwei.guaishang.activity;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.houwei.guaishang.R;
import com.houwei.guaishang.bean.SearchedMemberListResponse;
import com.houwei.guaishang.views.PagedListView;
import com.houwei.guaishang.views.PullToRefreshBase;
import com.houwei.guaishang.views.PullToRefreshPagedListView;
import com.easemob.chat.EMCursorResult;
import com.easemob.chat.EMGroupInfo;
import com.easemob.chat.EMGroupManager;
import com.easemob.exceptions.EaseMobException;
import com.nostra13.universalimageloader.core.ImageLoader;

public class GroupPublicActivity extends BaseActivity {
	private String cursor; 
	private final static int PAGESIZE = 25;
	private PullToRefreshPagedListView pullToRefreshView;
	private PagedListView listView;
	
	private List<EMGroupInfo> list;
	
	private MyListAdapter adapter;
	
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
		setTitleName("公共群");
		
		pullToRefreshView = (PullToRefreshPagedListView) findViewById(R.id.listView);
		listView = pullToRefreshView.getRefreshableView();
		
		pullToRefreshView.setRefreshing();
		new Thread(run).start();
	}

	private Runnable run = new Runnable() {

		@SuppressWarnings("unchecked")
		public void run() {
			// TODO Auto-generated method stub
			try {
				 final EMCursorResult<EMGroupInfo> result = EMGroupManager.getInstance().getPublicGroupsFromServer(PAGESIZE, null);
				  //获取group list
				
                 final List<EMGroupInfo> returnGroups = result.getData();
                 
                 runOnUiThread(new Runnable() {

                     public void run() {
                    	 pullToRefreshView.onRefreshComplete();
                    	 list = returnGroups; 
                    	 if(returnGroups.size() != 0){
                             //获取cursor
                             cursor = result.getCursor();
                    	 }
                    	 listView.onFinishLoading(returnGroups.size() >= PAGESIZE);
                    	 adapter = new MyListAdapter(GroupPublicActivity.this,list);
                         listView.setAdapter(adapter);
                     }
                 });
                 
			} catch (EaseMobException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	private Runnable pageRun = new Runnable() {

		@SuppressWarnings("unchecked")
		public void run() {
			// TODO Auto-generated method stub
			try {
				 final EMCursorResult<EMGroupInfo> result = EMGroupManager.getInstance().getPublicGroupsFromServer(PAGESIZE, cursor);
				  //获取group list
				
                 final List<EMGroupInfo> returnGroups = result.getData();
                 
                 runOnUiThread(new Runnable() {

                     public void run() {
                    	 list .addAll(returnGroups); 
                    	 if(returnGroups.size() != 0){
                             //获取cursor
                             cursor = result.getCursor();
                    	 }
                    	 listView.onFinishLoading(returnGroups.size() >= PAGESIZE);
                    	 adapter.notifyDataSetChanged();
                     }
                 });
			} catch (EaseMobException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	
	private void initListener() {
		// TODO Auto-generated method stub
		BackButtonListener();
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
		listView.setOnLoadMoreListener(new PagedListView.OnLoadMoreListener() {

			@Override
			public void onLoadMoreItems() {
				// TODO Auto-generated method stub
				new Thread(pageRun).start();
			}
		});
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Intent i = new Intent(GroupPublicActivity.this, GroupPublicDetailsActivity.class);
				i.putExtra("groupinfo", list.get(arg2 - listView.getHeaderViewsCount()));
				startActivity(i);
			}
		});
	}


	
	
	private class MyListAdapter extends BaseAdapter {
		private List<EMGroupInfo> list;
		private LayoutInflater mInflater;
		private Context mContext;
		private ImageLoader imageLoader = ImageLoader.getInstance();

		public MyListAdapter(Context mContext, List<EMGroupInfo> list) {
			this.mContext = mContext;
			this.list = list;
			mInflater = LayoutInflater.from(mContext);
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.listitem_group, null);

				viewHolder.item_name = (TextView) convertView
						.findViewById(R.id.item_name);
	
				viewHolder.member_count_tv = (TextView) convertView
						.findViewById(R.id.member_count_tv);
			
				viewHolder.avatar = (ImageView) convertView
						.findViewById(R.id.avatar);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			final EMGroupInfo tb = list.get(position);
			viewHolder.item_name.setText(""+tb.getGroupName());
			return convertView;
		}
	}
	
	private static class ViewHolder {
		private TextView item_name;
		private ImageView avatar;
		private TextView member_count_tv;
	}
	
}
