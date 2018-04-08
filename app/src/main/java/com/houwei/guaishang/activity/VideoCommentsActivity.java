package com.houwei.guaishang.activity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import com.houwei.guaishang.R;
import com.houwei.guaishang.bean.CommentBean;
import com.houwei.guaishang.bean.CommentListResponse;
import com.houwei.guaishang.bean.PraiseResponse;
import com.houwei.guaishang.bean.StringResponse;
import com.houwei.guaishang.bean.VideoBean;
import com.houwei.guaishang.layout.InputLinearLayout;
import com.houwei.guaishang.layout.MenuDialog;
import com.houwei.guaishang.layout.InputLinearLayout.SendClickListener;
import com.houwei.guaishang.manager.FaceManager;
import com.houwei.guaishang.manager.HomeManager.TopicPraiseCountChangeListener;
import com.houwei.guaishang.tools.HttpUtil;
import com.houwei.guaishang.tools.JsonParser;
import com.houwei.guaishang.tools.ShareUtil;
import com.houwei.guaishang.tools.ValueUtil;
import com.houwei.guaishang.video.JCVideoPlayer;
import com.houwei.guaishang.video.JCVideoPlayerStandard;
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
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;



/**
 * 视频评论界面
 */

public class VideoCommentsActivity extends BaseActivity {

	private final static int REVIEW_SUCCESS = 0x36;
	private final static int REVIEW_FAIL = 0x35;

	public final static int COMMENT_COUNT_CHANGE = 0x40;
	
	private MyListAdapter adapter;
	private VideoBean videoBean;
	private List<CommentBean> list;

	private int pageNumber = 1;
	private PullToRefreshPagedListView pullToRefreshView;
	private PagedListView listView;
	private String toMemberId; //提交评论时候 @的某人
	
	private InputLinearLayout inputLinearLayout;
	
	private MyHandler handler = new MyHandler(this);
	private static class MyHandler extends Handler {
		
		private WeakReference<Context> reference;
		
	    public MyHandler(Context context) {
	    	reference = new WeakReference<Context>(context);
	    }
	    
	    @Override
		public void handleMessage(Message msg) {
	    	final VideoCommentsActivity activity = (VideoCommentsActivity) reference.get();
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
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_topic_detail);
		initView();
		initListener();
	}

	private void initListener() {
		// TODO Auto-generated method stub

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
	}

	private void initView() {
		// TODO Auto-generated method stub
		initProgressDialog();
		videoBean = (VideoBean) getIntent().getSerializableExtra("VideoBean");

		pullToRefreshView = (PullToRefreshPagedListView) findViewById(R.id.listView);
		listView = pullToRefreshView.getRefreshableView();
		inputLinearLayout = (InputLinearLayout) findViewById(R.id.review_ll);
		
		LinearLayout header =  (LinearLayout)LayoutInflater.from(this).inflate(R.layout.listitem_video, null);
		JCVideoPlayerStandard jcVideoPlayer = (JCVideoPlayerStandard) header.findViewById(R.id.videoplayer);
		TextView title_tv = (TextView) header.findViewById(R.id.title_tv);
		View comment_ll = header.findViewById(R.id.comment_ll);
		View share_ll = header.findViewById(R.id.share_ll);
		jcVideoPlayer.setUp(videoBean.getMp4_url(),"");
		title_tv.setText(videoBean.getTitle());
		comment_ll.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				showKeyboard(inputLinearLayout.getEditText());
			}
		});
		
		share_ll.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				ShareUtil shareUtil = new ShareUtil(VideoCommentsActivity.this);
				shareUtil.setContent(videoBean.getTitle());
				shareUtil.setUrl(videoBean.getMp4_url());
				shareUtil.setIsVideoShare(true);
				shareUtil.showBottomPopupWin();
			}
		});
		
		ImageLoader.getInstance().displayImage(videoBean.getCover(),jcVideoPlayer.thumbImageView,getITopicApplication().getOtherManage().getRectDisplayImageOptions());

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

		list = new ArrayList<CommentBean>();
		adapter = new MyListAdapter(this, list);
		listView.setAdapter(adapter);
		
		//这样可以立刻自动播放，但是发现如果视频正在初始化中，点返回键，会导致卡屏
//		jcVideoPlayer.prepareVideo();
	}
	
	private Runnable commentReleaseRun = new Runnable() {

		@SuppressWarnings("unchecked")
		public void run() {
			// TODO Auto-generated method stub
			StringResponse response = null;
			try {
				HashMap<String, String> data = new HashMap<String, String>();
				data.put("userid", getUserID());
				data.put("to_memberid", toMemberId);
				data.put("vid", ""+videoBean.getVid());
				data.put("content", inputLinearLayout.getInputText());
				response = JsonParser.getStringResponse(HttpUtil.postMsg(
						HttpUtil.getData(data), HttpUtil.IP + "video/comment"));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (response != null) {
				response.setTag(""+videoBean.getVid());
				handler.sendMessage(handler.obtainMessage(REVIEW_SUCCESS,response));
			} else {
				handler.sendEmptyMessage(REVIEW_FAIL);
			}
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
				data.put("vid", ""+videoBean.getVid());
				response = JsonParser.getCommentListResponse(HttpUtil
						.getMsg(HttpUtil.IP + "video/commentlist?"
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
				data.put("vid", ""+videoBean.getVid());
				response = JsonParser.getCommentListResponse(HttpUtil
						.getMsg(HttpUtil.IP + "video/commentlist?"
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

	@Override
	public void onPause() {
		super.onPause();
		JCVideoPlayer.releaseAllVideos();
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
					MenuDialog followDialog = new MenuDialog(VideoCommentsActivity.this,
							new MenuDialog.ButtonClick() {

								@Override
								public void onSureButtonClick() {
									// TODO Auto-generated method stub
									ClipboardManager clipboard = (ClipboardManager) VideoCommentsActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
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
}
