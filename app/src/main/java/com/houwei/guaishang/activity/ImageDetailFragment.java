package com.houwei.guaishang.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.houwei.guaishang.R;
import com.houwei.guaishang.view.CircleImageView;
import com.houwei.guaishang.view.gallery.PhotoViewAttacher;
import com.houwei.guaishang.view.gallery.PhotoViewAttacher.OnPhotoTapListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class ImageDetailFragment extends Fragment {
	private String mImageUrl;
	private ImageView mImageView;
	private ProgressBar progressBar;
	private PhotoViewAttacher mAttacher;
	private DisplayImageOptions options;
	public static ImageDetailFragment newInstance(String imageUrl) {
		final ImageDetailFragment f = new ImageDetailFragment();

		final Bundle args = new Bundle();
		args.putString("url", imageUrl);
		f.setArguments(args);

		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mImageUrl = getArguments() != null ? getArguments().getString("url") : null;
		options = initImageLoader();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View v = inflater.inflate(R.layout.fragment_gallery_item, container, false);
		mImageView = (ImageView) v.findViewById(R.id.image);
		mAttacher = new PhotoViewAttacher(mImageView);
		
		mAttacher.setOnPhotoTapListener(new OnPhotoTapListener() {
			
			@Override
			public void onPhotoTap(View arg0, float arg1, float arg2) {
				getActivity().finish();
			}
		});
		
		progressBar = (ProgressBar) v.findViewById(R.id.loading);
		return v;
	}
	private DisplayImageOptions initImageLoader(){

		DisplayImageOptions options;

		options = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.photo_filter_image_empty)
			.showImageForEmptyUri(R.drawable.photo_filter_image_empty)
			.showImageOnFail(R.drawable.photo_filter_image_empty)
			.cacheInMemory(true)
			.cacheOnDisk(true)
		
			.build();
		return options;

	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		if (mImageUrl!=null && mImageUrl.endsWith(".gif")) {
//
//			mImageView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
//			//从服务器下载gif
//			Glide.with(getActivity()).load(mImageUrl).fitCenter().diskCacheStrategy(DiskCacheStrategy.SOURCE)
//				.into(new GlideDrawableImageViewTarget(mImageView) {
//
//					@Override
//					public void onResourceReady(GlideDrawable arg0,
//							GlideAnimation<? super GlideDrawable> arg1) {
//						// TODO Auto-generated method stub
//						progressBar.setVisibility(View.GONE);
//						super.onResourceReady(arg0, arg1);
//					}
//
//					@Override
//					public void onStart() {
//						// TODO Auto-generated method stub
//						progressBar.setVisibility(View.VISIBLE);
//						super.onStart();
//					}
//
//					@Override
//					public void onStop() {
//						// TODO Auto-generated method stub
//						progressBar.setVisibility(View.GONE);
//						super.onStop();
//					}
//
//					@Override
//					protected void setResource(GlideDrawable resource) {
//						// TODO Auto-generated method stub
//						super.setResource(resource);
//					}
//
//			     });
//
//			return;
		}
		
		
		
		ImageLoader.getInstance().displayImage(mImageUrl, mImageView, options,new ImageLoadingListener() {
			@Override
			public void onLoadingStarted(String imageUri, View view) {
				progressBar.setVisibility(View.VISIBLE);
			}

			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				String message = "";
				switch (failReason.getType()) {
				case IO_ERROR:
					message = "下载错误";
					break;
				case DECODING_ERROR:
					message = "图片无法显示";
					break;
				case NETWORK_DENIED:
					message = "网络有问题，无法下载";
					break;
				case OUT_OF_MEMORY:
					message = "图片太大无法显示";
					break;
				case UNKNOWN:
					message = "未知的错误";
					break;
				}
//				Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
				progressBar.setVisibility(View.GONE);
			}

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				progressBar.setVisibility(View.GONE);
				mAttacher.update();
			}

			@Override
			public void onLoadingCancelled(String imageUri, View view) {
				// TODO Auto-generated method stub
				
			}
		});

		
		
		
	}

}
