package com.houwei.guaishang.views;

import com.houwei.guaishang.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;



public class PullToRefreshLoadingLayout extends FrameLayout {

	static final int DEFAULT_ROTATION_ANIMATION_DURATION = 150;

	private final ImageView mHeaderImage;
	private final ProgressBar mHeaderProgress;
	private final TextView mHeaderText;
	
	private String mPullLabel;
	private String mRefreshingLabel;
	private String mReleaseLabel;

	private final Animation mRotateAnimation, mResetRotateAnimation;
	
	

	public PullToRefreshLoadingLayout(Context context, final int mode, String releaseLabel, String pullLabel,
			String refreshingLabel, TypedArray attrs,int type) {
		super(context);
		ViewGroup header = null;
		switch(type){
		case 0:
			header = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.pull_to_refresh_header, this);
			break;
		case 1:
			header = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.pull_to_load_footer, this);
			break;
		}
		mHeaderText = (TextView) header.findViewById(R.id.pull_to_refresh_text);
		mHeaderImage = (ImageView) header.findViewById(R.id.pull_to_refresh_image);
		mHeaderProgress = (ProgressBar) header.findViewById(R.id.pull_to_refresh_progress);

		final Interpolator interpolator = new LinearInterpolator();
		mRotateAnimation = new RotateAnimation(0, -180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		mRotateAnimation.setInterpolator(interpolator);
		mRotateAnimation.setDuration(DEFAULT_ROTATION_ANIMATION_DURATION);
		mRotateAnimation.setFillAfter(true);

		mResetRotateAnimation = new RotateAnimation(-180, 0, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		mResetRotateAnimation.setInterpolator(interpolator);
		mResetRotateAnimation.setDuration(DEFAULT_ROTATION_ANIMATION_DURATION);
		mResetRotateAnimation.setFillAfter(true);

		mReleaseLabel = releaseLabel;
		mPullLabel = pullLabel;
		mRefreshingLabel = refreshingLabel;

		switch (mode) {
			case PullToRefreshBase.MODE_PULL_UP_TO_REFRESH:
				mHeaderImage.setImageResource(R.drawable.pulltorefresh_up_arrow);
				break;
			case PullToRefreshBase.MODE_PULL_DOWN_TO_REFRESH:
			default:
				mHeaderImage.setImageResource(R.drawable.reflash_head_arrow_down);
				break;
		}

		if (attrs.hasValue(R.styleable.AppStorePullToRefresh_AppStoreptrHeaderTextColor)) {
			final int color = attrs.getColor(R.styleable.AppStorePullToRefresh_AppStoreptrHeaderTextColor, Color.BLACK);
			setTextColor(color);
		}
	}

	public void reset() {
		mHeaderText.setText(mPullLabel);
		mHeaderImage.setVisibility(View.VISIBLE);
		mHeaderProgress.setVisibility(View.GONE);
	}

	public void releaseToRefresh() {
		mHeaderText.setText(mReleaseLabel);
		mHeaderImage.clearAnimation();
		mHeaderImage.startAnimation(mRotateAnimation);
	}

	public void setPullLabel(String pullLabel) {
		mPullLabel = pullLabel;
	}

	public void refreshing() {
		mHeaderText.setText(mRefreshingLabel);
		mHeaderImage.clearAnimation();
		mHeaderImage.setVisibility(View.INVISIBLE);
		mHeaderProgress.setVisibility(View.VISIBLE);
	}

	public void setRefreshingLabel(String refreshingLabel) {
		mRefreshingLabel = refreshingLabel;
	}

	public void setReleaseLabel(String releaseLabel) {
		mReleaseLabel = releaseLabel;
	}

	public void pullToRefresh() {
		mHeaderImage.clearAnimation();
		mHeaderImage.startAnimation(mResetRotateAnimation);
	}

	public void setTextColor(int color) {
	}

}
