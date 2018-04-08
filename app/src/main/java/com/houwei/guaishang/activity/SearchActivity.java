package com.houwei.guaishang.activity;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;

import com.houwei.guaishang.R;
import com.houwei.guaishang.bean.CommentBean;
import com.houwei.guaishang.bean.SearchResponse;
import com.houwei.guaishang.bean.SearchedMemberBean;
import com.houwei.guaishang.bean.TopicBean;
import com.houwei.guaishang.layout.LinearLayoutForListView;
import com.houwei.guaishang.manager.FaceManager;
import com.houwei.guaishang.tools.HttpUtil;
import com.houwei.guaishang.tools.JsonParser;
import com.houwei.guaishang.tools.ValueUtil;
import com.houwei.guaishang.views.CloudTagViewGroup;
import com.houwei.guaishang.views.SpannableTextView;
import com.houwei.guaishang.views.SpannableTextView.MemberClickListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

public class SearchActivity extends BaseActivity {
	
	private ScrollView scrollview;
	private ProgressBar progressBar;
	private EditText searchbar_et;
	private LinearLayoutForListView users_linearLayout,topic_linearLayout;
	private List<TopicBean> topicItems;
	private List<SearchedMemberBean> userItems; 
	
	//当点击查看更多用户时候，记录之前搜索的关键字。而不是即时去获取edittext内容
	private String currentWord = "";
	
	private MyHandler handler = new MyHandler(this);
	private static class MyHandler extends Handler {
		
		private WeakReference<Context> reference;
		
	    public MyHandler(Context context) {
	    	reference = new WeakReference<Context>(context);
	    }
	    
	    @Override
		public void handleMessage(Message msg) {
	    	final SearchActivity activity = (SearchActivity) reference.get();
			if(activity == null){
				return;
			}
			switch (msg.what) {
			case NETWORK_SUCCESS_DATA_RIGHT:
				SearchResponse response = (SearchResponse) msg.obj;
				activity.scrollview.setVisibility(View.VISIBLE);
				activity.progressBar.setVisibility(View.GONE);
				if (response.isSuccess()) {

					activity.topicItems = response.getData().getTopicItems();
					activity.userItems = response.getData().getUserItems();
					
					activity.users_linearLayout.setAdapter(activity.new MemberAdapter(activity, activity.userItems));
					activity.topic_linearLayout.setAdapter(activity.new TopicSearchAdapter(activity, activity.topicItems));
					
				} else {
					activity.showErrorToast(response.getMessage());
				}
				break;
			
			default:
				activity.showErrorToast();
				break;
			}
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		initView();
		initListener();
	}

	private void initListener() {
		// TODO Auto-generated method stub
		findViewById(R.id.send_btn).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				progressBar.setVisibility(View.VISIBLE);
				hideKeyboard(searchbar_et);
				currentWord = searchbar_et.getText().toString().trim();
				new Thread(run).start();
			}
		});
		
		findViewById(R.id.users_more_tv).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent(SearchActivity.this, SearchMoreMemberActivity.class);
				i.putExtra("keyword", currentWord);
				startActivity(i);
			}
		});
		
		findViewById(R.id.topic_more_tv).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent(SearchActivity.this, SearchMoreTopicActivity.class);
				i.putExtra("keyword", currentWord);
				startActivity(i);
			}
		});
	}

	private void initView() {
		// TODO Auto-generated method stub
		topic_linearLayout = (LinearLayoutForListView)findViewById(R.id.topic_linearLayout); 
		users_linearLayout = (LinearLayoutForListView)findViewById(R.id.users_linearLayout); 
		
		searchbar_et = (EditText)findViewById(R.id.searchbar_et); 
		scrollview = (ScrollView)findViewById(R.id.scrollview); 
		progressBar = (ProgressBar)findViewById(R.id.progressBar); 
		progressBar.setVisibility(View.GONE);
		scrollview.setVisibility(View.GONE);
		
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				showKeyboard(searchbar_et);
			}
		}, 150);
	}

	
	private Runnable run = new Runnable() {

		@SuppressWarnings("unchecked")
		public void run() {
			// TODO Auto-generated method stub
			SearchResponse response = null;
			try {
				HashMap<String, String> data = new HashMap<String, String>();
				data.put("userid", getUserID());
				data.put("keyword", searchbar_et.getText().toString().trim());
			
				response = JsonParser.getSearchResponse(HttpUtil.getMsg(HttpUtil.IP
						+ "search/all"+"?"+ HttpUtil.getData(data)));
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
			
			viewHolder.tagsViewGroup.setTags(SearchActivity.this, tb.findPersonalTags(),null);
			if (tb.findPersonalTags().isEmpty()) {			
				viewHolder.tagsViewGroup.addTag(SearchActivity.this, "未填写");
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

		class ViewHolder {
			private TextView item_name;
			private ImageView avator;
			private CloudTagViewGroup tagsViewGroup;
		}
	}
	
	

	private class TopicSearchAdapter extends BaseAdapter {

		private Context mContext;
		private ImageLoader imageLoader = ImageLoader.getInstance();
		private LayoutInflater mInflater;
		private List<TopicBean> list;
		private FaceManager faceManager;
		public TopicSearchAdapter(Context mContext, List<TopicBean> list) {
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
				convertView = mInflater.inflate(R.layout.listitem_search_topic, null);

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
			final TopicBean tb = list.get(position);
			imageLoader.displayImage(tb.getMemberAvatar().findSmallUrl(), viewHolder.avator);
			viewHolder.item_name.setText("" + tb.getMemberName());
			viewHolder.item_memo.setText(faceManager.
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
					Intent i = new Intent();
					i.setClass(mContext, TopicDetailActivity.class);
					i.putExtra("TopicBean", tb);
					startActivity(i);
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
