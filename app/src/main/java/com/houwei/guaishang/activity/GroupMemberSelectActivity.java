package com.houwei.guaishang.activity;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
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
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.houwei.guaishang.R;
import com.houwei.guaishang.bean.IntResponse;
import com.houwei.guaishang.bean.SearchedMemberBean;
import com.houwei.guaishang.bean.SearchedMemberListResponse;
import com.houwei.guaishang.bean.UserBean;
import com.houwei.guaishang.manager.FollowManager.FollowListener;
import com.houwei.guaishang.tools.HttpUtil;
import com.houwei.guaishang.tools.JsonParser;
import com.houwei.guaishang.tools.ValueUtil;
import com.houwei.guaishang.views.PagedListView;
import com.houwei.guaishang.views.PullToRefreshBase;
import com.houwei.guaishang.views.PullToRefreshPagedListView;

public class GroupMemberSelectActivity extends BaseActivity{

	private final int PAGE_SIZE = 20;
	private int pageNumber = 1;
	private PagedListView listView;
	
	private List<String> disableIdlist;
	private List<SearchedMemberBean> selectlist ;
	
	private String memberid;//要查看TA的粉丝 关注列表
	
	private View mEmptyLayout;
	private List<SearchedMemberBean> list;
	private MyListAdapter adapter;
	
	private MyHandler handler = new MyHandler(this);
	private static class MyHandler extends Handler {
		
		private WeakReference<Context> reference;
		
	    public MyHandler(Context context) {
	    	reference = new WeakReference<Context>(context);
	    }
	    
	    @Override
		public void handleMessage(Message msg) {
	    	final GroupMemberSelectActivity activity = (GroupMemberSelectActivity) reference.get();
			if(activity == null){
				return;
			}
			activity.findViewById(R.id.loadingBar).setVisibility(View.GONE);
			switch (msg.what) {
			case NETWORK_SUCCESS_DATA_RIGHT:
				SearchedMemberListResponse response = (SearchedMemberListResponse) msg.obj;
				if (response.isSuccess()) {
					activity.list = response.getData().getItems();
					activity.listView.onFinishLoading(response.getData().hasMore());
					activity.adapter = activity.new MyListAdapter(activity, activity.list);
					activity.listView.setAdapter(activity.adapter);
					activity.pageNumber = 2;
				} else {
					activity.listView.onFinishLoading(false);
					activity.showErrorToast(response.getMessage());
				}
				break;
			case NETWORK_SUCCESS_PAGER_RIGHT:
				SearchedMemberListResponse pageResponse = (SearchedMemberListResponse) msg.obj;
				if (pageResponse.isSuccess()) {
					List<SearchedMemberBean> tempList = pageResponse.getData()
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
		setContentView(R.layout.activity_listview_norefresh_title);
		initView();
		initListener();
	}

	private void initView() {
		// TODO Auto-generated method stub
		initProgressDialog();
		setTitleName("选择成员");
		selectlist = new ArrayList<SearchedMemberBean>();
		disableIdlist = ValueUtil.StringToArrayList(getIntent().getStringExtra("disableIdlist"));
		
		//如果前一个界面没传memberid近来，表示查询userid的关注粉丝列表
		memberid = getIntent().getStringExtra("memberid");
		memberid = memberid==null?getUserID():memberid;
		
		listView = (PagedListView) findViewById(R.id.pagedListView);
		mEmptyLayout =  LayoutInflater.from(this).inflate(R.layout.listview_empty, null);
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
				data.put("memberid", memberid);
				data.put("page", "1");
				data.put("isfan", "1");
				response = JsonParser.getSearchedMemberListResponse(HttpUtil.getMsg(HttpUtil.IP
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
			SearchedMemberListResponse response = null;
			try {
				HashMap<String, String> data = new HashMap<String, String>();
				data.put("userid", getUserID());
				data.put("memberid", memberid);
				data.put("page", ""+pageNumber);
				data.put("isfan", "1");
				response = JsonParser.getSearchedMemberListResponse(HttpUtil.getMsg(HttpUtil.IP
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

	
	
	@SuppressLint("WrongViewCast")
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
		
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				SearchedMemberBean tb = list.get(arg2 - listView.getHeaderViewsCount());
				if (disableIdlist.contains(tb.getMemberId())) {
					return ;
				}else if(selectlist.contains(tb)){
					selectlist.remove(tb);
				}else{
					selectlist.add(tb);
				}
				adapter.notifyDataSetChanged();
				
			}
		});
		TextView title_right  = (TextView)findViewById(R.id.title_right);
		title_right.setVisibility(View.VISIBLE);
		title_right.setText("确定");
		findViewById(R.id.title_right).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent();
				i.putExtra("selectedList", (Serializable) selectlist);
				setResult(RESULT_OK, i);
				finish();
			}
		});
	}

	private class MyListAdapter extends BaseAdapter {
		private List<SearchedMemberBean> list;
		private LayoutInflater mInflater;
		private Context mContext;
		private ImageLoader imageLoader = ImageLoader.getInstance();

		public MyListAdapter(Context mContext, List<SearchedMemberBean> list) {

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
						R.layout.listitem_member_select, null);

				viewHolder.item_name = (TextView) convertView
						.findViewById(R.id.item_name);


				viewHolder.public_check_box = (CheckBox) convertView
						.findViewById(R.id.public_check_box);
				
				viewHolder.avator = (ImageView) convertView
						.findViewById(R.id.avator);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			final SearchedMemberBean tb = list.get(position);
			
			if (disableIdlist.contains(tb.getMemberId())) {
				viewHolder.public_check_box .setVisibility(View.GONE);
			}else if(selectlist.contains(tb)){
				viewHolder.public_check_box .setVisibility(View.VISIBLE);
				viewHolder.public_check_box.setChecked(true);
			}else{
				viewHolder.public_check_box .setVisibility(View.VISIBLE);
				viewHolder.public_check_box.setChecked(false);
			}
			
			imageLoader.displayImage(tb.getMemberAvatar().findSmallUrl(), viewHolder.avator);
			viewHolder.item_name.setText(""+tb.getMemberName());
		
			return convertView;
		}


	}

	private static class ViewHolder {
		private TextView item_name;
		private ImageView avator;
		private CheckBox public_check_box;
	}
	
}
