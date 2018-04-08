package com.houwei.guaishang.manager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import com.houwei.guaishang.R;
import com.houwei.guaishang.bean.CityBean;
import com.houwei.guaishang.tools.JsonParser;
import com.houwei.guaishang.tools.JsonUtil;
import com.houwei.guaishang.tools.LogUtil;
import com.houwei.guaishang.views.CircleBitmapDisplayer;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;

public class OtherManager {

	private Context mContext;

	public OtherManager(Context mContext) {
		this.mContext = mContext;
	}

	public void initOther() {
		initImageLoader(mContext);
	}

	private void initImageLoader(Context context) {
		DisplayImageOptions rectAvatarOptions = new DisplayImageOptions.Builder()
				.cacheInMemory(true).cacheOnDisk(true)
				.showImageForEmptyUri(R.drawable.user_photo)
				.showImageOnFail(R.drawable.user_photo)
				.showImageOnLoading(R.drawable.user_photo)
				.imageScaleType(ImageScaleType.EXACTLY)// 设置图片以如何的编码方式显示，EXACTLY_STRETCHED会比较卡
				.build();

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).defaultDisplayImageOptions(rectAvatarOptions)
				.diskCacheSize(16 * 1024 * 1024)// 50M
				.diskCacheFileCount(100)// 缓存一百张图片\
				.build();
		ImageLoader.getInstance().init(config);
	}

	//圆形图片（用于圆头像）
	public DisplayImageOptions getCircleOptionsDisplayImageOptions() {
		DisplayImageOptions circleOptions = new DisplayImageOptions.Builder()
				.cacheInMemory(true).cacheOnDisk(true)
				.showImageForEmptyUri(R.drawable.user_photo)
				.showImageOnFail(R.drawable.user_photo)
				.showImageOnLoading(R.drawable.user_photo)
				.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)// 设置图片以如何的编码方式显示
				.displayer(new CircleBitmapDisplayer()).build();
		return circleOptions;
	}

	// 用于正方形直角的图片
	public DisplayImageOptions getRectDisplayImageOptions() {
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.gray_rect)
				.showImageOnFail(R.drawable.gray_rect)
				.showImageOnLoading(R.drawable.gray_rect)
				.imageScaleType(ImageScaleType.EXACTLY)// 设置图片以如何的编码方式显示，EXACTLY_STRETCHED会比较卡
				.cacheInMemory(true).cacheOnDisk(true).build();
		return defaultOptions;
	}

	// 获取当前版本号
	public String getVersionName() {
		PackageManager packageManager = mContext.getPackageManager();
		// getPackageName()是你当前类的包名，0代表是获取版本信息
		try {
			PackageInfo packInfo = packageManager.getPackageInfo(
					mContext.getPackageName(), 0);

			return packInfo.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "未知";
		}
	}

}
