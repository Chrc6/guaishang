package com.houwei.guaishang.layout;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.houwei.guaishang.R;
import com.houwei.guaishang.activity.BaseActivity;
import com.houwei.guaishang.activity.VideoCommentsActivity;
import com.houwei.guaishang.bean.VideoBean;
import com.houwei.guaishang.tools.HttpUtil;
import com.houwei.guaishang.tools.ShareUtil;
import com.houwei.guaishang.tools.ValueUtil;
import com.houwei.guaishang.video.JCVideoPlayerStandard;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by Nathen On 2016/02/07 01:20
 */
public class VideoAdapter extends BaseAdapter {

	private Context context;
	private List<VideoBean> list;
	private LayoutInflater mInflater;
	private DisplayImageOptions options;
	
	public VideoAdapter(Context context, List<VideoBean> list) {
		this.context = context;
		this.list = list;
		BaseActivity activity = (BaseActivity)context;
		this.options = activity.getITopicApplication().getOtherManage().getRectDisplayImageOptions();
		this.mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (null == convertView) {
			viewHolder = new ViewHolder();
			
			convertView = mInflater.inflate(R.layout.listitem_video, null);
			
			viewHolder.jcVideoPlayer = (JCVideoPlayerStandard) convertView.findViewById(R.id.videoplayer);
			
			viewHolder.title_tv = (TextView) convertView.findViewById(R.id.title_tv);
			
			viewHolder.comment_ll =convertView.findViewById(R.id.comment_ll);
			viewHolder.share_ll =convertView.findViewById(R.id.share_ll);
			

			
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		final VideoBean bean = list.get(position);
		
		viewHolder.jcVideoPlayer.setUp(bean.getMp4_url(),"");
		
		viewHolder.jcVideoPlayer.hideTimeTextView();
		
		viewHolder.title_tv.setText(bean.getTitle());
		
		ImageLoader.getInstance().displayImage(bean.getCover(),viewHolder.jcVideoPlayer.thumbImageView,options);
		
		viewHolder.comment_ll.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent(context,VideoCommentsActivity.class);
				i.putExtra("VideoBean",bean);
				context.startActivity(i);
			}
		});
		
		viewHolder.share_ll.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				ShareUtil shareUtil = new ShareUtil((BaseActivity)context);
				shareUtil.setContent(bean.getTitle());
				shareUtil.setUrl(bean.getMp4_url());
				shareUtil.setIsVideoShare(true);
				shareUtil.showBottomPopupWin();
			}
		});
		
		return convertView;
	}

	private static class ViewHolder {
		
		private JCVideoPlayerStandard jcVideoPlayer;
		
		private TextView title_tv;
		
		private View comment_ll,share_ll;
	}
}
