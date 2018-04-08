package com.houwei.guaishang.layout;

import java.util.ArrayList;
import java.util.List;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.houwei.guaishang.R;
import com.houwei.guaishang.activity.BaseActivity;
import com.houwei.guaishang.activity.BasePhotoGridActivity;
import com.houwei.guaishang.activity.GalleryActivity;
import com.houwei.guaishang.bean.AvatarBean;
import com.houwei.guaishang.bean.TopicBean;
import com.houwei.guaishang.easemob.EaseBlurUtils;
import com.houwei.guaishang.easemob.StackBlur;
import com.houwei.guaishang.tools.HttpUtil;
import com.houwei.guaishang.tools.ValueUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;


public class PictureGridLayout extends GridLayout {

	private int pictureSize;
	
	private RedPacketClickListener onRedPacketClickListener;
	
	public PictureGridLayout(Context context){
		super(context);
		initView(context);
	}
	
	public PictureGridLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	private void initView(Context context) {
		// TODO Auto-generated method stub
		Activity activity = (Activity) context;
		WindowManager wm = activity.getWindowManager();
		int margin = (int)context.getResources().getDimension(R.dimen.topic_gridphoto_leftmargin);
		pictureSize = (wm.getDefaultDisplay().getWidth() - 2 *margin )/3;
		setRowCount(3);
	}
	
	public void setPictures(final BaseActivity context,final List<AvatarBean> pictures,final TopicBean bean, DisplayImageOptions options){
		removeAllViews();
		if (pictures == null) {
			setVisibility(View.GONE);
			return ;
		}
		
		if(pictures.size() == 4){
			setColumnCount(2);
		}else{
			setColumnCount(3);
		}
		
		switch (pictures.size()) {
		case 0:
			//无图片
			setVisibility(View.GONE);
			break;
		case 1:
			setVisibility(View.VISIBLE);
			//一张图，按照服务器返回的宽高等比例缩放大小
			String originalUrl = pictures.get(0).findOriginalUrl();
			
			//需要我付款（说明是红包 + 我没付款过 + 我不是发布人）
			boolean needPayPhoto = bean.needPayPhoto(context.getITopicApplication().getHomeManager()) ;

			View layout =  LayoutInflater.from(context).inflate((bean.getRedpacket() == 1 || originalUrl.endsWith(".gif"))?R.layout.griditem_imageview_gif:R.layout.griditem_imageview,null);
			final ImageView imageview =  (ImageView)layout.findViewById(R.id.imageview);
			
			//无论是gif图，还是红包图，还是普通单图，都要设置好大小
			addView(layout);
			ValueUtil.resetLayoutParams(context,layout,bean);
			
			if (bean.getRedpacket() == 1) {
				TextView tag_tv =  (TextView)layout.findViewById(R.id.tag_tv);
				tag_tv.setText("红包");
			}
			
			if(needPayPhoto){
				//是红包动态
				ImageLoader.getInstance().displayImage(pictures.get(0).findSmallUrl(), imageview, options,new ImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {
					}

					@Override
					public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
					}

					@Override
					public void onLoadingCancelled(String imageUri, View view) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						// TODO Auto-generated method stub
						imageview.setImageBitmap(EaseBlurUtils.blurBitmap(loadedImage));
						
						// 是挺快，但是总是有黑边 https://github.com/robinxdroid/Blur
//						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//							imageview.setImageBitmap(StackBlur.blurRenderScript(context,loadedImage, 12, false));
//						}else{
//							
//						}
					}
				});

				imageview.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						if (onRedPacketClickListener!=null) {
							onRedPacketClickListener.onRedPacketClick(bean.getTopicId(),bean.getMemberId());
						}
					}
				});
				

			} else if(originalUrl.endsWith(".gif")){
				//从服务器下载gif

				Glide.with(context).load(originalUrl)
					.into(imageview);
				
				imageview.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						intentToGallery(context, pictures,0);
					}
				});
				
			} else {
				//进入这里，也可能是红包照片，但是我无需付款
				ImageLoader.getInstance().displayImage(originalUrl,
						imageview, options);
				
				imageview.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						intentToGallery(context, pictures,0);
					}
				});
			}

			

			break;
		default:
			//大于1张图，不支持红包
			setVisibility(View.VISIBLE);
			for (int i = 0; i < pictures.size(); i++) {
				AvatarBean avatarBean = pictures.get(i);
				View layout2 =  LayoutInflater.from(context).inflate(avatarBean.getOriginal().endsWith(".gif")?R.layout.griditem_imageview_gif:R.layout.griditem_imageview,null);
				ImageView imageviews =  (ImageView)layout2.findViewById(R.id.imageview);
				addView(layout2);

				layout2.getLayoutParams().width = pictureSize;
				layout2.getLayoutParams().height = pictureSize;
				
				ImageLoader.getInstance().displayImage(avatarBean.findSmallUrl(),
						imageviews, options);
				
				final int index = i;
				imageviews.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						intentToGallery(context, pictures,index);
					}
				});
			}
			break;
		}
	}
	
	private void intentToGallery(final Context context,final List<AvatarBean> pictures,int index) {
		Intent intent = new Intent(context, GalleryActivity.class);
		ArrayList<String> urls = new ArrayList<String>();
		for (AvatarBean bean : pictures) {
			urls .add(bean.findOriginalUrl());
		}
		intent.putExtra(GalleryActivity.EXTRA_IMAGE_URLS, urls);
		intent.putExtra(GalleryActivity.EXTRA_IMAGE_INDEX, index);
		context.startActivity(intent);
	}
	
	public void setOnRedPacketClickListener(RedPacketClickListener onRedPacketClickListener) {
		this.onRedPacketClickListener = onRedPacketClickListener;
	}

	public interface RedPacketClickListener{
		public void onRedPacketClick(String topicId,String to_memberid);
	}
}
