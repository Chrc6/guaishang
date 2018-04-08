package com.houwei.guaishang.activity;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.houwei.guaishang.R;
import com.houwei.guaishang.bean.GroupDetailResponse;
import com.houwei.guaishang.bean.SearchedMemberBean;
import com.houwei.guaishang.layout.SureOrCancelDialog;
import com.houwei.guaishang.tools.HttpUtil;
import com.houwei.guaishang.tools.JsonParser;
import com.houwei.guaishang.tools.ValueUtil;
import com.houwei.guaishang.views.PagedListView;
import com.houwei.guaishang.views.PullToRefreshBase;
import com.houwei.guaishang.views.PullToRefreshPagedListView;
import com.easemob.chat.EMContactManager;
import com.easemob.exceptions.EaseMobException;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 黑名单列表页面
 * 
 */
public class BlacklistActivity extends BaseActivity {
	private PullToRefreshPagedListView pullToRefreshView;
	private PagedListView listView;

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
			final BlacklistActivity activity = (BlacklistActivity) reference
					.get();
			if (activity == null) {
				return;
			}
			activity.pullToRefreshView.onRefreshComplete();
			activity.progress.dismiss();
			switch (msg.what) {
			case NETWORK_SUCCESS_DATA_ERROR:
				// 访问我们自己的服务器，返回用户信息
				GroupDetailResponse response = (GroupDetailResponse) msg.obj;
				if (response.isSuccess()) {
					activity.list = response.getData().getItems();
					activity.listView.onFinishLoading(false);
					activity.adapter = activity.new MyListAdapter(activity,
							activity.list);
					activity.listView.setAdapter(activity.adapter);
					activity.pullToRefreshView.setEmptyView(activity.list
							.isEmpty() ? activity.mEmptyLayout : null);
				} else {
					activity.listView.onFinishLoading(false);
					activity.showErrorToast(response.getMessage());
				}

				break;
			default:
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
		initProgressDialog();
		setTitleName("黑名单");

		pullToRefreshView = (PullToRefreshPagedListView) findViewById(R.id.listView);
		listView = pullToRefreshView.getRefreshableView();
		mEmptyLayout = LayoutInflater.from(this).inflate(
				R.layout.listview_empty, null);

	}

	private void refresh() {
		pullToRefreshView.setRefreshing();
		// 从本地获取黑名单
		List<String> blacklist = EMContactManager.getInstance()
				.getBlackListUsernames();
		new Thread(new GetMemberInfoRun(ValueUtil.ArrayListToString(blacklist)))
				.start();
	}

	private void initListener() {
		// TODO Auto-generated method stub
		BackButtonListener();
		listView.setOnLoadMoreListener(new PagedListView.OnLoadMoreListener() {

			@Override
			public void onLoadMoreItems() {
				// TODO Auto-generated method stub

			}
		});
		pullToRefreshView
				.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2() {

					@Override
					public void onPullDownToRefresh() {
						// 从本地获取黑名单
						List<String> blacklist = EMContactManager.getInstance()
								.getBlackListUsernames();
						new Thread(new GetMemberInfoRun(ValueUtil
								.ArrayListToString(blacklist))).start();
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
				SearchedMemberBean bean = list.get(arg2
						- listView.getHeaderViewsCount());
				jumpToHisInfoActivity(bean.getMemberId(), bean.getMemberName(),
						bean.getMemberAvatar());
			}
		});

		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					final int arg2, long arg3) {
				// TODO Auto-generated method stub
				final SearchedMemberBean bean = list.get(arg2 - listView.getHeaderViewsCount());
				SureOrCancelDialog followDialog = new SureOrCancelDialog(
						BlacklistActivity.this, "移除黑名单", "好",
						new SureOrCancelDialog.SureButtonClick() {

							@Override
							public void onSureButtonClick() {
								// TODO Auto-generated method stub
								progress.show();
								new Thread(new Runnable() {
									public void run() {
										try {
											// 移出黑民单
											EMContactManager.getInstance().deleteUserFromBlackList(bean.getMemberId());
											runOnUiThread(new Runnable() {
												public void run() {
													progress.dismiss();
													list.remove(bean);
													adapter.notifyDataSetChanged();
												}
											});
										} catch (EaseMobException e) {
											runOnUiThread(new Runnable() {
												public void run() {
													progress.dismiss();
												}
											});
										}
									}
								}).start();
							}
						});
				followDialog.show();
				return true;
			}
		});

		refresh();
	}

	/**
	 * 访问我们自己的服务器获取群组里所有用户的信息
	 */
	private class GetMemberInfoRun implements Runnable {
		private String memberIds;

		public GetMemberInfoRun(String memberIds) {
			this.memberIds = memberIds;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			GroupDetailResponse response = null;

			try {
				Map<String, String> data = new HashMap<String, String>();
				data.put("memberids", memberIds);
				data.put("userid", getUserID());
				response = JsonParser.getGroupDetailResponse(HttpUtil.postMsg(
						HttpUtil.getData(data), HttpUtil.IP
								+ "chat/groupdetail"));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (response == null) {
				response = new GroupDetailResponse();
				response.setMessage("网络访问失败");
			}
			handler.sendMessage(handler.obtainMessage(
					NETWORK_SUCCESS_DATA_ERROR, response));
		}
	}

	/**
	 * adapter
	 * 
	 */

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
				convertView = mInflater.inflate(R.layout.listitem_follower,
						null);

				viewHolder.item_name = (TextView) convertView
						.findViewById(R.id.item_name);

				viewHolder.item_btn = (Button) convertView
						.findViewById(R.id.item_btn);
				viewHolder.item_btn.setVisibility(View.GONE);
				viewHolder.avator = (ImageView) convertView
						.findViewById(R.id.avator);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			final SearchedMemberBean tb = list.get(position);
			imageLoader.displayImage(tb.getMemberAvatar().findSmallUrl(),
					viewHolder.avator);
			viewHolder.item_name.setText("" + tb.getMemberName());
			return convertView;
		}
	}

	private static class ViewHolder {
		private TextView item_name;
		private ImageView avator;
		private Button item_btn;
	}
}
