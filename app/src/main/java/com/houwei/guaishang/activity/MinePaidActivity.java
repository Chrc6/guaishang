package com.houwei.guaishang.activity;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.houwei.guaishang.bean.StringResponse;
import com.houwei.guaishang.bean.TopicBean;
import com.houwei.guaishang.bean.TopicListResponse;
import com.houwei.guaishang.bean.UserBean;
import com.houwei.guaishang.easemob.EaseConstant;
import com.houwei.guaishang.layout.ApplySuccessDialog;
import com.houwei.guaishang.layout.ApplySuccessTwoButtonDialog;
import com.houwei.guaishang.layout.SureOrCancelDialog;
import com.houwei.guaishang.manager.MyUserBeanManager.CheckPointListener;
import com.houwei.guaishang.tools.HttpUtil;
import com.houwei.guaishang.tools.JsonParser;
import com.houwei.guaishang.tools.ShareUtil;
import com.houwei.guaishang.tools.ValueUtil;
import com.houwei.guaishang.views.CloudTagViewGroup;
import com.houwei.guaishang.views.PagedListView;
import com.houwei.guaishang.views.PullToRefreshBase;
import com.houwei.guaishang.views.PullToRefreshPagedListView;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MinePaidActivity extends BaseActivity{

	private final static int CONFIRM = 0x29;
	
	private int pageNumber = 1;
	private PullToRefreshPagedListView pullToRefreshView;
	private PagedListView listView;
	
	private View mEmptyLayout;
	private List<TopicBean> list;
	private MemberAdapter adapter;
	
	private MyHandler handler = new MyHandler(this);
	private static class MyHandler extends Handler {
		
		private WeakReference<Context> reference;
		
	    public MyHandler(Context context) {
	    	reference = new WeakReference<Context>(context);
	    }
	    
	    @Override
		public void handleMessage(Message msg) {
	    	final MinePaidActivity activity = (MinePaidActivity) reference.get();
			if(activity == null){
				return;
			}
			activity.progress.dismiss();
			activity.pullToRefreshView.onRefreshComplete();
			switch (msg.what) {
			case NETWORK_SUCCESS_DATA_RIGHT:
				TopicListResponse response = (TopicListResponse) msg.obj;
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
				TopicListResponse pageResponse = (TopicListResponse) msg.obj;
				if (pageResponse.isSuccess()) {
					List<TopicBean> tempList = pageResponse.getData().getItems();
					activity.list.addAll(tempList);
					activity.adapter.notifyDataSetChanged();
					activity.listView.onFinishLoading(pageResponse.getData().hasMore());
					activity.pageNumber++;
				} else {
					activity.listView.onFinishLoading(false);
					activity.showErrorToast(pageResponse.getMessage());
				}
				break;
				
			case CONFIRM:
				final StringResponse confirmResponse = (StringResponse) msg.obj;
				if (confirmResponse.isSuccess()) {
					
					ApplySuccessTwoButtonDialog  followDialog = new ApplySuccessTwoButtonDialog(
							activity,  new ApplySuccessTwoButtonDialog.ButtonClick() {
								
								@Override
								public void onSureButtonClick(int index) {
									// TODO Auto-generated method stub
									switch (index) {
									case 0:
										//回首页
										activity.finish();
										break;
									default:
										for (TopicBean bean : activity.list) {
											if (bean.getTopicId().equals(confirmResponse.getData())) {
												bean.setStatus(10);
												activity.adapter.notifyDataSetChanged();
												break;
											}
										}
										break;
									}
								}
							});
					followDialog.setCancelable(false);
					followDialog.show();
					
					followDialog.comment_tv.setText("留在此页");
					
					Map<String, String> paramMap = new HashMap<String, String>();
					paramMap.put("topicid", confirmResponse.getData());
					paramMap.put("to_memberid", confirmResponse.getTag());
					activity.getITopicApplication().getHuanXinManager().doPushAction(5, paramMap);

				} else {
					activity.showErrorToast(confirmResponse.getMessage());
				}

				break;
			default:
//				activity.showErrorToast();
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
		setTitleName("我购买的");
		
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
			TopicListResponse response = null;
			try {
				HashMap<String, String> data = new HashMap<String, String>();
				data.put("userid", getUserID());
				data.put("page", "1");
				response = JsonParser.getTopicListResponse(HttpUtil.getMsg(HttpUtil.IP
						+ "topic/getorderlist"+"?"+ HttpUtil.getData(data)));
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
			TopicListResponse response = null;
			try {
				HashMap<String, String> data = new HashMap<String, String>();
				data.put("userid", getUserID());
				data.put("page", ""+pageNumber);
				response = JsonParser.getTopicListResponse(HttpUtil.getMsg(HttpUtil.IP
						+ "topic/getorderlist"+"?"+ HttpUtil.getData(data)));
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
				Intent i = new Intent();
				i.setClass(MinePaidActivity.this, TopicDetailActivity.class);
				i.putExtra("TopicBean", list.get(arg2-listView.getHeaderViewsCount()));
				startActivity(i);
			}
		});
	}


	private class MemberAdapter extends BaseAdapter {
		private List<TopicBean> list;
		private LayoutInflater mInflater;
		private ImageLoader imageLoader = ImageLoader.getInstance();

		public MemberAdapter(Context mContext, List<TopicBean> list) {
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
						R.layout.listitem_mypayed, null);

				viewHolder.name_tv = (TextView) convertView
						.findViewById(R.id.item_name);
				
				viewHolder.item_memo1 = (TextView) convertView
						.findViewById(R.id.item_memo1);
				
				viewHolder.item_memo2 = (TextView) convertView
						.findViewById(R.id.item_memo2);
				
				viewHolder.price_tv = (TextView) convertView
						.findViewById(R.id.price_tv);
				
				viewHolder.chat_btn = (Button) convertView
						.findViewById(R.id.chat_btn);
				
				viewHolder.order_btn = (Button) convertView
						.findViewById(R.id.order_btn);
				
				
				viewHolder.avatar = (ImageView) convertView
						.findViewById(R.id.cover);
				
				
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			final TopicBean tb = list.get(position);
			imageLoader.displayImage(tb.getCover(), viewHolder.avatar);
			viewHolder.name_tv.setText(getITopicApplication().getFaceManager().
					convertNormalStringToSpannableString(MinePaidActivity.this,tb.getContent()));
			viewHolder.item_memo1.setText("发布人："+tb.getMemberName());
			viewHolder.item_memo2.setText("购买时间："+tb.getPaidCreatedAt());
			viewHolder.price_tv.setText("已支付："+tb.getPrice()+ "元");
			
			viewHolder.chat_btn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					jumpToChatActivity(tb.getMemberId(),
							tb.getMemberName(), tb.getMemberAvatar(),EaseConstant.CHATTYPE_SINGLE);
				}
			});
			
			
			viewHolder.order_btn.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					progress.show();
					confirm(tb.getTopicId(), tb.getOrderid(), tb.getMemberId());
				}
			});
			
			//如果是 “这条记录未被抢 且 发布人不是我” process_btn是可见的，还要再根据具体情况设置颜色和文字
		    switch (tb.getStatus()) {
		        case 1:
		        	viewHolder.order_btn.setText("确认收货");
		        	viewHolder.order_btn.setEnabled(true);
		        	viewHolder.order_btn.setVisibility(View.VISIBLE);
		            break;
		        case 10:
		        	viewHolder.order_btn.setText("已结束");
		        	viewHolder.order_btn.setEnabled(false);
		        	viewHolder.order_btn.setVisibility(View.VISIBLE);
		            break;
		        default:
		        	viewHolder.order_btn.setVisibility(View.GONE);
		            break;
		    }
		    
			return convertView;
		}
	}
	
	private static class ViewHolder {
		private TextView name_tv,item_memo2,item_memo1,price_tv;
		private Button chat_btn,order_btn;
		private ImageView avatar;
	}


	// 确认收货
	private void confirm(String topicID,String orderID, String to_memberid) {
		new Thread(new ConfirmRun(topicID,orderID, to_memberid)).start();
	}

	private class ConfirmRun implements Runnable {
		private String topicID;
		private String orderID;
		private String to_memberid;

		public ConfirmRun(String topicID,String orderID, String to_memberid) {
			this.topicID = topicID;
			this.orderID = orderID;
			this.to_memberid = to_memberid;
		}

		public void run() {
			// TODO Auto-generated method stub
			StringResponse response = null;
			try {
				HashMap<String, String> data = new HashMap<String, String>();
				data.put("userid", getUserID());
				data.put("topicid", topicID);
				data.put("to_memberid", to_memberid);
				data.put("orderid", orderID);
				response = JsonParser.getStringResponse(HttpUtil.postMsg(
						HttpUtil.getData(data), HttpUtil.IP + "order/confirm"));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (response == null) {
				response = new StringResponse();
				response.setMessage("网络访问失败");
			}
			response.setData(topicID);
			response.setTag(to_memberid);
			handler.sendMessage(handler.obtainMessage(CONFIRM, response));
		}
	};

}
