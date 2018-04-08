package com.houwei.guaishang.activity;

import java.util.ArrayList;
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
import android.widget.TextView.BufferType;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.houwei.guaishang.R;
import com.houwei.guaishang.bean.BasePushResult;
import com.houwei.guaishang.bean.CommentPushBean;
import com.houwei.guaishang.data.DBReq;
import com.houwei.guaishang.manager.FaceManager;
import com.houwei.guaishang.views.PagedListView;
import com.houwei.guaishang.views.PullToRefreshPagedListView;

//未读的评论列表
//数据是从本地数据库里取出来的
public class CommentUnReadActivity extends BaseActivity {
	private PagedListView listView;
	private ArrayList<CommentPushBean> list;
	private FaceManager faceManager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_listview_title_layout);
		initView();
		initListener();
	}

	private void initView() {
		// TODO Auto-generated method stub
		setTitleName("新评论");
		this.faceManager = getITopicApplication().getFaceManager();
		list = DBReq.getInstence(getITopicApplication()).getCommentPushBean();
		PullToRefreshPagedListView pullToRefreshView = (PullToRefreshPagedListView) findViewById(R.id.listView);
		listView = pullToRefreshView.getRefreshableView();
		listView.onFinishLoading(false);
		pullToRefreshView.setPullToRefreshEnabled(false);
		listView.setAdapter(new MyListAdapter(this, list));
	}

	private void initListener() {
		// TODO Auto-generated method stub
		BackButtonListener();
		getITopicApplication().getChatManager().readCommentAction();
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				final CommentPushBean tb = list.get(arg2- listView.getHeaderViewsCount());
				Intent i = new Intent();
				switch (tb.getPushType()) {
				case BasePushResult.TOPIC_COMMENT_PUSH:
					i.setClass(CommentUnReadActivity.this, TopicDetailActivity.class);
					i.putExtra("TopicBean", tb.getTopicBean());
					startActivity(i);
					break;
				
				default:
					break;
				}
			}
		});
	}

	private class MyListAdapter extends BaseAdapter {

		private Context mContext;
		private ImageLoader imageLoader = ImageLoader.getInstance();
		private LayoutInflater mInflater;
		private List<CommentPushBean> list;

		public MyListAdapter(Context mContext, List<CommentPushBean> list) {
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
			ViewHolder viewHolder = null;
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = mInflater.inflate(
						R.layout.listitem_unread_comment, null);
				viewHolder.header_name = (TextView) convertView
						.findViewById(R.id.header_name);
				viewHolder.header_memo = (TextView) convertView
						.findViewById(R.id.header_memo);
				viewHolder.header_time = (TextView) convertView
						.findViewById(R.id.header_time);
				viewHolder.body_name = (TextView) convertView
						.findViewById(R.id.body_name);
				viewHolder.content_ll = (View) convertView
						.findViewById(R.id.content_ll);
				viewHolder.body_avator = (ImageView) convertView
						.findViewById(R.id.body_avator);
				viewHolder.avator = (ImageView) convertView
						.findViewById(R.id.avator);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			final CommentPushBean tb = list.get(position);
			
			imageLoader.displayImage(tb.getCommentMemberAvatar().findSmallUrl(), viewHolder.avator);
			viewHolder.header_name.setText("" + tb.getCommentMemberName());
			

			viewHolder.header_memo.setText(faceManager.
					convertNormalStringToSpannableString(mContext,tb.getCommentContent()),
					BufferType.SPANNABLE);
			
			
			
			viewHolder.header_time.setText(tb.getTimeString());

			switch (tb.getPushType()) {
			case BasePushResult.TOPIC_COMMENT_PUSH:
				imageLoader.displayImage(tb.getTopicBean().getMemberAvatar().findSmallUrl(), viewHolder.body_avator);
				
				viewHolder.body_name.setText(faceManager.
						convertNormalStringToSpannableString(mContext,tb.getTopicBean().getContent()),
						BufferType.SPANNABLE);
				
				
				break;
		
			default:
				break;
			}
			return convertView;
		}
	}
	
	private static class ViewHolder {
		private TextView header_name, header_memo, header_time, body_name;
		private ImageView avator, body_avator;
		private View content_ll;
	}
}
