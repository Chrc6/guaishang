package com.houwei.guaishang.activity;

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

import com.nostra13.universalimageloader.core.ImageLoader;
import com.houwei.guaishang.R;
import com.houwei.guaishang.bean.AvatarBean;
import com.houwei.guaishang.bean.SearchedMemberBean;
import com.houwei.guaishang.easemob.EaseConstant;
import com.houwei.guaishang.tools.HttpUtil;
import com.houwei.guaishang.views.PagedListView;
import com.houwei.guaishang.views.PullToRefreshBase;
import com.houwei.guaishang.views.PullToRefreshPagedListView;
import com.easemob.EMCallBack;
import com.easemob.EMValueCallBack;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.exceptions.EaseMobException;

public class GroupMineActivity extends BaseActivity {

	private final static int GROUP_CREATE_REQUITE = 0x86;
	private PullToRefreshPagedListView pullToRefreshView;
	private PagedListView listView;
	
	private List<EMGroup> grouplist;

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
		setTitleName("我的群组");
		
		pullToRefreshView = (PullToRefreshPagedListView) findViewById(R.id.listView);
		listView = pullToRefreshView.getRefreshableView();
		listView.onFinishLoading(false);
		
		View headerView = LayoutInflater.from(this).inflate(R.layout.layout_group_header, null);
		headerView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				//防止点到header
			}
		});
		headerView.findViewById(R.id.group_search_ll).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent(GroupMineActivity.this, GroupPublicActivity.class);
				startActivity(i);
			}
		});
		headerView.findViewById(R.id.group_new_ll).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent(GroupMineActivity.this, GroupCreateActivity.class);
				startActivityForResult(i, GROUP_CREATE_REQUITE);
			}
		});
		
		listView.addHeaderView(headerView);
		
		grouplist = EMGroupManager.getInstance().getAllGroups();
		adapter = new MyListAdapter(this,grouplist);
		listView.setAdapter(adapter);
		
		if (grouplist.isEmpty()) {
			listView.showEmptyFooter(GroupMineActivity.this, "尚没加入任何群");
		}else{
			listView.removeEmptyFooter();
		}
		
		
//		if (!HXSDKHelper.getInstance().isGroupsSyncedWithServer()) {
		pullToRefreshView.setRefreshing();
		refresh();
	}

	private void refresh() {
	
		EMGroupManager.getInstance().asyncGetGroupsFromServer(new EMValueCallBack<List<EMGroup>>() {
            
            @Override
            public void onSuccess(List<EMGroup> value) {
            	
            	runOnUiThread(new Runnable() {
            		
            		@Override
        			public void run() {
        				pullToRefreshView.onRefreshComplete();
        				grouplist = EMGroupManager.getInstance().getAllGroups();
    					adapter = new MyListAdapter(GroupMineActivity.this,grouplist);
    					listView.setAdapter(adapter);
    					
    					if (grouplist.isEmpty()) {
    						listView.showEmptyFooter(GroupMineActivity.this, "尚没加入任何群");
    					}else{
    						listView.removeEmptyFooter();
    					}
        			}
        		});
            }
            
            @Override
            public void onError(int error, final String errorMsg) {
            	
            	runOnUiThread(new Runnable() {
            		
            		@Override
        			public void run() {
        				pullToRefreshView.onRefreshComplete();
        				showErrorToast(errorMsg);
        			}
        		});
            }
        });
	}

	
	
	private void initListener() {
		// TODO Auto-generated method stub
		BackButtonListener();
		pullToRefreshView
				.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2() {

					@Override
					public void onPullDownToRefresh() {
						refresh();
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
				EMGroup bean = grouplist.get(arg2-listView.getHeaderViewsCount());
				jumpToChatActivity(bean.getGroupId(), bean.getGroupName(), null, EaseConstant.CHATTYPE_GROUP,"",false);
			}
		});
	}

	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode ==  GROUP_CREATE_REQUITE && resultCode == RESULT_OK) {
			//刚刚新建完群组
			pullToRefreshView.setRefreshing();
			refresh();
		}
	}
	
	
	private class MyListAdapter extends BaseAdapter {
		private List<EMGroup> list;
		private LayoutInflater mInflater;
		private Context mContext;
		private ImageLoader imageLoader = ImageLoader.getInstance();

		public MyListAdapter(Context mContext, List<EMGroup> list) {
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
			final EMGroup tb = list.get(position);
			imageLoader.displayImage(HttpUtil.IP_NOAPI+tb.getDescription(), viewHolder.avatar);
			viewHolder.item_name.setText(""+tb.getGroupName());
			viewHolder.member_count_tv.setText(tb.getMembers().size()+"人");
			viewHolder.member_count_tv.setVisibility(tb.getMembers().size() == 0?View.GONE:View.VISIBLE);
			return convertView;
		}
	}
	
	private static class ViewHolder {
		private TextView item_name;
		private ImageView avatar;
		private TextView member_count_tv;
	}
	
	
}
