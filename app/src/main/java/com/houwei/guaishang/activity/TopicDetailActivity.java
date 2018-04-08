package com.houwei.guaishang.activity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.houwei.guaishang.R;
import com.houwei.guaishang.bean.CommentBean;
import com.houwei.guaishang.bean.CommentListResponse;
import com.houwei.guaishang.bean.IntResponse;
import com.houwei.guaishang.bean.LocationBean;
import com.houwei.guaishang.bean.PraiseResponse;
import com.houwei.guaishang.bean.StringResponse;
import com.houwei.guaishang.bean.TopicBean;
import com.houwei.guaishang.layout.InputLinearLayout;
import com.houwei.guaishang.layout.MenuDialog;
import com.houwei.guaishang.layout.PictureGridLayout;
import com.houwei.guaishang.layout.RedPacketDialog;
import com.houwei.guaishang.layout.InputLinearLayout.SendClickListener;
import com.houwei.guaishang.layout.SharePopupWindow;
import com.houwei.guaishang.layout.TopicAdapter;
import com.houwei.guaishang.manager.FaceManager;
import com.houwei.guaishang.manager.FollowManager.FollowListener;
import com.houwei.guaishang.manager.HomeManager.TopicPayRequireListener;
import com.houwei.guaishang.manager.HomeManager.TopicPayedListener;
import com.houwei.guaishang.manager.HomeManager.TopicPraiseCountChangeListener;
import com.houwei.guaishang.manager.MyLocationManager;
import com.houwei.guaishang.tools.HttpUtil;
import com.houwei.guaishang.tools.JsonParser;
import com.houwei.guaishang.tools.ShareUtil;
import com.houwei.guaishang.tools.ShareUtil2;
import com.houwei.guaishang.tools.ValueUtil;
import com.houwei.guaishang.views.PagedListView;
import com.houwei.guaishang.views.PullToRefreshBase;
import com.houwei.guaishang.views.PullToRefreshPagedListView;
import com.houwei.guaishang.views.SpannableTextView;
import com.houwei.guaishang.views.SpannableTextView.MemberClickListener;
import com.easemob.chat.TextMessageBody;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;


/**
 * 动态评论界面
 */

public class TopicDetailActivity extends BaseActivity implements TopicPraiseCountChangeListener, FollowListener,MyLocationManager.LocationListener  {

	private final static int REVIEW_SUCCESS = 0x36;
	private final static int REVIEW_FAIL = 0x35;
	
	private final static int NETWORK_PRAISES = 0x37;
	
	
	public final static int COMMENT_COUNT_CHANGE = 0x40;

	private final static int SHARE_SUCCESS = 0x41;
	
	private MyListAdapter adapter;
	private TopicBean topicBean;
	private List<CommentBean> list;
	private List<CommentBean> praiselist;
	

	private int pageNumber = 1;
	private PullToRefreshPagedListView pullToRefreshView;
	private PagedListView listView;
	private String toMemberId; //提交评论时候 @的某人
	
	
	private InputLinearLayout inputLinearLayout;
	private TopicDetailHeaderLayout header;
	private LocationBean currentLocationBean;
	
	private MyHandler handler = new MyHandler(this);

	@Override
	public void onLocationFail() {

	}

	@Override
	public void onLocationSuccess(LocationBean currentLocationBean) {
		this.currentLocationBean = currentLocationBean;

	}

	private static class MyHandler extends Handler {
		
		private WeakReference<Context> reference;
		
	    public MyHandler(Context context) {
	    	reference = new WeakReference<Context>(context);
	    }
	    
	    @Override
		public void handleMessage(Message msg) {
	    	final TopicDetailActivity activity = (TopicDetailActivity) reference.get();
			if(activity == null){
				return;
			}
			activity.progress.dismiss();
			activity.pullToRefreshView.onRefreshComplete();
			switch (msg.what) {
			case BaseActivity.NETWORK_SUCCESS_DATA_RIGHT:
				CommentListResponse response = (CommentListResponse) msg.obj;
				
				if (response.isSuccess()) {
					activity.list.clear();
					activity.list.addAll(response.getData().getItems());
					activity.listView.onFinishLoading(response.getData().hasMore());
					activity.pageNumber = 2;
					if (activity.list.isEmpty()) {
						activity.listView.showEmptyFooter(activity, null);
					}else{
						activity.listView.removeEmptyFooter();
					}
					activity.adapter.notifyDataSetChanged();
					activity.toMemberId = "";
					activity.inputLinearLayout.setHint("评论");
				} else {
					activity.listView.onFinishLoading(false);
					activity.showErrorToast(response.getMessage());
				}
				break;

			case NETWORK_SUCCESS_PAGER_RIGHT:
				CommentListResponse pageResponse = (CommentListResponse) msg.obj;
				if (pageResponse.isSuccess()) {
					List<CommentBean> tempList = pageResponse.getData().getItems();
					activity.list.addAll(tempList);
					activity.listView.onFinishLoading(pageResponse.getData().hasMore());
					if (activity.list.isEmpty()) {
						activity.listView.showEmptyFooter(activity, null);
					}
					activity.pageNumber++;
					activity.adapter.notifyDataSetChanged();
				} else {
					activity.listView.onFinishLoading(false);
					activity.showErrorToast(pageResponse.getMessage());
				}
				break;
			case NETWORK_PRAISES:
				CommentListResponse praiseResponse = (CommentListResponse) msg.obj;
				if (praiseResponse.isSuccess()) {
					activity.praiselist = praiseResponse.getData().getItems();
					activity.header.setPraiseList(activity.praiselist);
				}
				break;
			case NETWORK_FAIL:
				activity.showErrorToast();
			
				break;
			case REVIEW_SUCCESS:
				StringResponse reviewResponse = (StringResponse) msg.obj;
				if (reviewResponse.isSuccess()) {
					activity.inputLinearLayout.clear();
					activity.progress.show();
					new Thread(activity.Run).start();
				} else {
					activity.showErrorToast(reviewResponse.getMessage());
				}
				break;
			case REVIEW_FAIL:
				activity.showErrorToast();
				break;
			case SHARE_SUCCESS:

				break;

			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("WXCH","TopicDetailActivity");
		setContentView(R.layout.activity_topic_detail);
		initView();
		initListener();
	}

	private void initListener() {
		// TODO Auto-generated method stub
		getITopicApplication().getLocationManager().addLocationListener(this);
		getITopicApplication().getLocationManager().startLoction(false);
		getITopicApplication().getHomeManager().addOnTopicPraiseCountChangeListener(this);
		getITopicApplication().getFollowManager().addFollowListener(this);
		
		listView.setOnLoadMoreListener(new PagedListView.OnLoadMoreListener() {

			@Override
			public void onLoadMoreItems() {
				// TODO Auto-generated method stub
				new Thread(pageRun).start();
			}
		});
		
		listView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				inputLinearLayout.hideKeyboardAndEmoji();
				return false;
			}
		});
		
		pullToRefreshView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2() {

			@Override
			public void onPullDownToRefresh() {
				// new Thread(listViewFirstPagerun).start();
				new Thread(Run).start();
			}

			@Override
			public void onPullUpToRefresh() {

			}
		});
		
		findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				hideKeyboard();
				finish();
			}
		});
		pageNumber = 1;
		listView.startToGetMore();
		
		if (getUserID() != null && !getUserID().equals("")) {
			//登录了
			new Thread(footPrintRun).start();
		}
	}

	private void initView() {
		// TODO Auto-generated method stub
		initProgressDialog();
		topicBean = (TopicBean) getIntent().getSerializableExtra("TopicBean");
		boolean needPay = getIntent().getBooleanExtra("needPay",false);
		pullToRefreshView = (PullToRefreshPagedListView) findViewById(R.id.listView);
		listView = pullToRefreshView.getRefreshableView();
		inputLinearLayout = (InputLinearLayout) findViewById(R.id.review_ll);
		header = new TopicDetailHeaderLayout(this);
		header.initView(this, topicBean, currentLocationBean,needPay);
		listView.addHeaderView(header);

		inputLinearLayout.initView(this, false, new SendClickListener() {
			
			@Override
			public void onSendClick(String content) {
				// TODO Auto-generated method stub
				if (inputLinearLayout.getInputText().equals("")) {
					return;
				}
				inputLinearLayout.hideKeyboardAndEmoji();
				progress.show();
				new Thread(commentReleaseRun).start();
			}
		});
		
		/*header.share_count_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub


			}
		});*/
		
		resetFollowButton(header.follow_btn, topicBean.getFriendship());
		header.follow_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (checkLogined()) {
					progress.show();
					getITopicApplication().getFollowManager().followOnThread(getUserID(),
							topicBean.getMemberId());
				}
			}
		});
		
		list = new ArrayList<CommentBean>();
		praiselist = new ArrayList<CommentBean>();
		
		adapter = new MyListAdapter(this, list);
		listView.setAdapter(adapter);
		
		new Thread(PraisesRun).start();
	}
	
	
	private void resetFollowButton(Button followBtn,int newfriendship){
//		followBtn.setText(ValueUtil.getRelationTypeStringSimple(newfriendship));
		followBtn.setBackgroundResource(ValueUtil.getRelationTypeDrawableSimple(newfriendship));
//		followBtn.setTextColor(ValueUtil.getRelationTextColorSimple(newfriendship));
	}
	
	private Runnable footPrintRun = new Runnable() {

		@SuppressWarnings("unchecked")
		public void run() {
			// TODO Auto-generated method stub
			StringResponse response = null;
			try {
				HashMap<String, String> data = new HashMap<String, String>();
				data.put("userid", getUserID());
				data.put("topicid", ""+topicBean.getTopicId());
				response = JsonParser.getStringResponse(HttpUtil.postMsg(
						HttpUtil.getData(data), HttpUtil.IP + "topic/footprint"));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
	
	private Runnable commentReleaseRun = new Runnable() {

		@SuppressWarnings("unchecked")
		public void run() {
			// TODO Auto-generated method stub
			StringResponse response = null;
			try {
				HashMap<String, String> data = new HashMap<String, String>();
				data.put("userid", getUserID());
				data.put("to_memberid", toMemberId);
				data.put("topicid", ""+topicBean.getTopicId());
				data.put("content", inputLinearLayout.getInputText());
				response = JsonParser.getStringResponse(HttpUtil.postMsg(
						HttpUtil.getData(data), HttpUtil.IP + "topic/comment"));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (response != null) {
				response.setTag(""+topicBean.getTopicId());
				handler.sendMessage(handler.obtainMessage(REVIEW_SUCCESS,response));
			} else {
				handler.sendEmptyMessage(REVIEW_FAIL);
			}
		}
	};
	
	private Runnable PraisesRun = new Runnable() {

		@SuppressWarnings("unchecked")
		public void run() {
			// TODO Auto-generated method stub
			CommentListResponse response = null;
			try {
				HashMap<String, String> data = new HashMap<String, String>();
				data.put("userid", getUserID());
				data.put("page", "1");
				data.put("topicid", ""+topicBean.getTopicId());
				response = JsonParser.getCommentListResponse(HttpUtil
						.getMsg(HttpUtil.IP + "topic/praiselist?"
								+ HttpUtil.getData(data)));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (response == null) {
				response = new CommentListResponse();
				response.setMessage("网络访问失败");
			}
			handler.sendMessage(handler.obtainMessage(NETWORK_PRAISES, response));
		}
	};
	
	private Runnable Run = new Runnable() {

		@SuppressWarnings("unchecked")
		public void run() {
			// TODO Auto-generated method stub
			CommentListResponse response = null;
			try {
				HashMap<String, String> data = new HashMap<String, String>();
				data.put("userid", getUserID());
				data.put("page", "1");
				data.put("topicid", ""+topicBean.getTopicId());
				response = JsonParser.getCommentListResponse(HttpUtil
						.getMsg(HttpUtil.IP + "topic/commentlist?"
								+ HttpUtil.getData(data)));
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
			CommentListResponse response = null;
			try {
				HashMap<String, String> data = new HashMap<String, String>();
				data.put("userid", getUserID());
				data.put("page", ""+pageNumber);
				data.put("topicid", ""+topicBean.getTopicId());
				response = JsonParser.getCommentListResponse(HttpUtil
						.getMsg(HttpUtil.IP + "topic/commentlist?"
								+ HttpUtil.getData(data)));
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

	
	private class RequireFinishListener implements TopicPayRequireListener{
		
		//支付红包照片网络返回监听
		@Override
		public void onRequireFinish(StringResponse response){
			if (response.isSuccess()) {
				progress.dismiss();
				showSuccessTips("支付成功！");
			}else{
				progress.dismiss();
				showErrorToast(response.getMessage());
			}
		}
	}
	
	@Override
	public void onTopicPraiseCountChanged(PraiseResponse interestResponse) {
		// TODO Auto-generated method stub
		progress.dismiss();
		if (topicBean.getTopicId().equals(interestResponse.getTopicid())) {
			topicBean.setPraiseCount(interestResponse.getPraiseCnt());
			topicBean.setPraised(interestResponse.isStillPraise());
			
			boolean contains = false;
			for (CommentBean praiseMember : praiselist) {
				if (praiseMember.getMemberId().equals(getUserID())) {
					praiselist.remove(praiseMember);
					contains = true;
					break;
				}
			}
			if (!contains) {
				CommentBean newBean = new CommentBean();
				newBean.setMemberId(getUserID());
				newBean.setMemberAvatar(getITopicApplication().getMyUserBeanManager().getInstance().getAvatar());
				newBean.setMemberName(getITopicApplication().getMyUserBeanManager().getInstance().getName());
				newBean.setContent("");
				praiselist.add(newBean);
			}
			
			header.setPraiseStatus(topicBean);
			header.setPraiseList(praiselist);
		} 
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		getITopicApplication().getHomeManager().removeOnTopicPraiseCountChangeListener(this);
		getITopicApplication().getFollowManager().removeFollowListener(this);
		getITopicApplication().getLocationManager().removeLocationListener(this);
		super.onDestroy();
	}
	

	private class MyListAdapter extends BaseAdapter {

		private Context mContext;
		private ImageLoader imageLoader = ImageLoader.getInstance();
		private LayoutInflater mInflater;
		private List<CommentBean> list;
		private FaceManager faceManager;
		public MyListAdapter(Context mContext, List<CommentBean> list) {
			this.mContext = mContext;
			this.list = list;
			this.mInflater = LayoutInflater.from(mContext);
			this.faceManager = getITopicApplication().getFaceManager();
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
			ViewHolder viewHolder = null;
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.listitem_comment, null);

				viewHolder.item_name = (TextView) convertView
						.findViewById(R.id.item_name);

				viewHolder.item_memo = (SpannableTextView) convertView
						.findViewById(R.id.item_memo);
				viewHolder.item_time = (TextView) convertView
						.findViewById(R.id.item_time);
				viewHolder.avator = (ImageView) convertView
						.findViewById(R.id.avator);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			final CommentBean tb = list.get(position);
			imageLoader.displayImage(tb.getMemberAvatar().findSmallUrl(), viewHolder.avator);
			viewHolder.item_name.setText("" + tb.getMemberName());
			viewHolder.item_memo.setCommentText(tb,new MemberClickListener() {
				
				@Override
				public void onMemberClick(CommentBean commentBean) {
					// TODO Auto-generated method stub
					// 点击了回复@的人的名字
					jumpToHisInfoActivity(tb.getToMemberId(), tb.getToMemberName(), tb.getToMemberAvatar());
				}
			});
			viewHolder.item_memo.append(faceManager.
					convertNormalStringToSpannableString(mContext,tb.getContent()));
			FaceManager.extractMention2Link(viewHolder.item_memo);

			viewHolder.item_time.setText(tb.getTimeString());
		
			viewHolder.avator.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					jumpToHisInfoActivity(tb.getMemberId(), tb.getMemberName(), tb.getMemberAvatar());
				}
			});
			
			convertView.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					if(tb.getMemberId().equals(getUserID())){
						toMemberId = "";
						inputLinearLayout.setHint("评论");
					}else{
						toMemberId = tb.getMemberId();
						inputLinearLayout.setHint("回复 @"+tb.getMemberName());
					}

					inputLinearLayout.getEditText().requestFocus();
					showKeyboard(inputLinearLayout.getEditText());
				}
			});
			
			convertView.setOnLongClickListener(new View.OnLongClickListener() {
				
				@Override
				public boolean onLongClick(View arg0) {
					// TODO Auto-generated method stub
					MenuDialog followDialog = new MenuDialog(TopicDetailActivity.this,
							new MenuDialog.ButtonClick() {

								@Override
								public void onSureButtonClick() {
									// TODO Auto-generated method stub
									ClipboardManager clipboard = (ClipboardManager) TopicDetailActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
									clipboard.setText(tb.getContent());
								}
							});
					followDialog.show();
					return true;
				}
			});
			return convertView;
		}
	}
	
	private static class ViewHolder {
		private SpannableTextView item_memo;
		private TextView item_name, item_time;
		private ImageView avator;
	}

	@Override
	public void FollowChanged(IntResponse followResponse) {
		// TODO Auto-generated method stub
		progress.dismiss();
		resetFollowButton(header.follow_btn, followResponse.getData());
	}


}
