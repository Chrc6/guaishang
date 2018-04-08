package com.houwei.guaishang.tools;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.houwei.guaishang.R;
import com.houwei.guaishang.activity.BaseActivity;
import com.houwei.guaishang.activity.MissionActivity;
import com.houwei.guaishang.layout.SharePopupWindow;
import com.houwei.guaishang.manager.MyUserBeanManager;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

//分享
public class ShareUtil2 {

	private BaseActivity context;
	private String memberId;
	private String imageUrl;
	private String content;
	private String url;
	private boolean isVideoShare;//分享的是视频，默认false表示分享的是网页
	private PlatformActionListener onPlatformActionListener;

	public static String ShareIMAGE;
	//sdcard中的图片名称
	private static final String FILE_NAME = "/ic_launcher.png";

	public ShareUtil2(final BaseActivity context, PlatformActionListener onPlatformActionListener){
		this.context = context;
		this.onPlatformActionListener = onPlatformActionListener;
		new Thread() {
			public void run() {
				initImagePath();
			}
		}.start();
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public void showBottomPopupWin() {

		LayoutInflater mLayoutInfalter = (LayoutInflater) context
				.getSystemService(context.LAYOUT_INFLATER_SERVICE);
		LinearLayout mPopView = (LinearLayout) mLayoutInfalter.inflate(
				R.layout.bottom_share_popupwindow, null);
		SharePopupWindow mPopupWin = new SharePopupWindow(context, mPopView);
		mPopupWin.setAnimationStyle(R.style.BottomPopupAnimation);
		mPopupWin.showAtLocation(context.getWindow().getDecorView(), Gravity.BOTTOM, 0,0);
		mPopupWin.setOnShareClickedListener(new  SharePopupWindow.ShareClickedListener() {

			@Override
			public void onShareClicked(View v) {
				// TODO Auto-generated method stub

				switch (v.getId()) {
					case R.id.share_qzone_ll:
						shareToQzone();
						break;
					case R.id.share_QQfriends_ll:
						shareToQQFriend();
						break;
					case R.id.share_weixin_moments_ll:
						shareToWXmoments();
						break;
					case R.id.share_weixin_friends_ll:
						shareToWXFriends();
						break;
					default:
						break;
				}
			}
		});

	}

	public  void shareToQzone(){
		QZone.ShareParams sp = new QZone.ShareParams();
		sp.setShareType(isVideoShare?QZone.SHARE_VIDEO:QZone.SHARE_WEBPAGE);
		sp.setTitle(context.getResources().getString(R.string.app_name));
		sp.setTitleUrl(url);
		sp.setText(content);
		if (imageUrl != null) {
			sp.setImageUrl(imageUrl);
		} else {
			Drawable picture_update_icon = context.getResources().getDrawable(R.drawable.ic_launcher);
			sp.setImageData(((BitmapDrawable) picture_update_icon).getBitmap());
		}
		sp.setSite(content);
		sp.setSiteUrl(url);
		Platform platform = ShareSDK.getPlatform(QZone.NAME);
		platform.setPlatformActionListener(onPlatformActionListener);
		platform.share(sp);
	}


	public  void shareToQQFriend(){
		Log.i("WXCH","---------------");
		OnekeyShare oks = new OnekeyShare();
		//关闭sso授权
		oks.disableSSOWhenAuthorize();
		if (imageUrl != null) {
			oks.setImageUrl(imageUrl);
		} else {
			oks.setImagePath(ShareIMAGE);
		}
		oks.setTitleUrl(url);
		oks.setText(content);
		oks.setTitle(context.getResources().getString(R.string.app_name));
		oks.setPlatform(QQ.NAME);
		oks.show(context);

        /*QQ.ShareParams sp = new QQ.ShareParams();
        sp.setShareType(isVideoShare?QQ.SHARE_VIDEO:QQ.SHARE_WEBPAGE);
        sp.setTitle(context.getResources().getString(R.string.app_name));
        sp.setTitleUrl(url);
        sp.setText(content);
        if (imageUrl != null) {
       	 	sp.setImageUrl(imageUrl);
		} else {
			Drawable picture_update_icon = context.getResources().getDrawable(R.drawable.ic_launcher);
			sp.setImageData(((BitmapDrawable) picture_update_icon).getBitmap());
		}
        sp.setSite(content);
        sp.setSiteUrl(url);

		Platform platform = PublicStaticData.myShareSDK.getPlatform (QQ.NAME);
        //Platform platform = ShareSDK.getPlatform(QQ.NAME);
        platform.setPlatformActionListener(onPlatformActionListener);// 设置分享事件回调
        platform.share(sp);// 执行图文分享*/
	}

	public  void shareToQQFriend1(){
		Log.d("CCC","shareToQQFriend");
		Platform.ShareParams sp = new Platform.ShareParams();
//		sp.setShareType(isVideoShare?QQ.SHARE_VIDEO:QQ.SHARE_WEBPAGE);
		sp.setTitle(context.getResources().getString(R.string.app_name));
		sp.setTitleUrl(url);
		sp.setText(content);
		Platform qq =ShareSDK.getPlatform (QQ.NAME);
		qq.setPlatformActionListener(new PlatformActionListener() {
			@Override
			public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
				Log.d("CCC","i:"+i);
			}

			@Override
			public void onError(Platform platform, int i, Throwable throwable) {
				Log.d("CCC","i:"+i+"e:"+throwable);
			}

			@Override
			public void onCancel(Platform platform, int i) {
				Log.d("CCC","i:"+i);
			}
		});
		qq.share(sp);
	}
	public  void shareToQQFriend2(){
		OnekeyShare oks = new OnekeyShare();
//关闭sso授权
		oks.disableSSOWhenAuthorize();
		oks.setTitle(context.getResources().getString(R.string.app_name));
//		oks.setTitleUrl(url); // 标题的超链接
//		oks.setText(content);
//
//		if (imageUrl != null) {
//			oks.setImageUrl(imageUrl);
//		} else {
//			Drawable picture_update_icon = context.getResources().getDrawable(R.drawable.ic_launcher);
//			oks.setImageData("http://f1.sharesdk.cn/imgs/2014/02/26/owWpLZo_638x960.jpg");
//		}

		oks.setImagePath("");
// 启动分享GUI
		oks.show(context);
		oks.setCallback(new PlatformActionListener() {
			@Override
			public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
				ToastUtils.toastForShort(context,"分享成功");
			}
			@Override
			public void onError(Platform platform, int i, Throwable throwable) {}
			@Override
			public void onCancel(Platform platform, int i) {}
		});
	}

	public  void shareToWXmoments(){
		WechatMoments.ShareParams sp = new WechatMoments.ShareParams();
		sp.setShareType(WechatMoments.SHARE_WEBPAGE);
		sp.setTitle(content);//朋友圈只显示这个
		sp.setText(content);//朋友圈不显示此字段
		sp.setUrl(url);
		if (imageUrl != null) {
			sp.setImageUrl(imageUrl);
		} else {
			Drawable picture_update_icon = context.getResources().getDrawable(R.drawable.ic_launcher);
			sp.setImageData(((BitmapDrawable) picture_update_icon).getBitmap());
		}
		Platform platform = ShareSDK.getPlatform(WechatMoments.NAME);
		platform.setPlatformActionListener(onPlatformActionListener);
		platform.share(sp);
	}


	public static void shareToWXmomentsForOrderBuy(Context context,String content,
												   String url, String imageUrl, PlatformActionListener onPlatformActionListener){
		WechatMoments.ShareParams sp = new WechatMoments.ShareParams();
		sp.setShareType(WechatMoments.SHARE_WEBPAGE);
		sp.setTitle(content);//朋友圈只显示这个
		sp.setText(content);//朋友圈不显示此字段
		sp.setUrl(url);
		if (imageUrl != null) {
			sp.setImageUrl(imageUrl);
		} else {
			Drawable picture_update_icon = context.getResources().getDrawable(R.drawable.ic_launcher);
			sp.setImageData(((BitmapDrawable) picture_update_icon).getBitmap());
		}
		Platform platform = ShareSDK.getPlatform(WechatMoments.NAME);
		platform.setPlatformActionListener(onPlatformActionListener);
		platform.share(sp);
	}
	public  void shareToWXmoments1(){
		WechatMoments.ShareParams sp = new WechatMoments.ShareParams();
		sp.setShareType(WechatMoments.SHARE_IMAGE);
		sp.setTitle(content);//朋友圈只显示这个
		sp.setText(content);//朋友圈不显示此字段
		sp.setImageUrl("http://f1.sharesdk.cn/imgs/2014/02/26/owWpLZo_638x960.jpg");
//        sp.setUrl(url);
//        if (imageUrl != null) {
//       	 	sp.setImageUrl(imageUrl);
//		} else {
//			Drawable picture_update_icon = context.getResources().getDrawable(R.drawable.ic_launcher);
//			sp.setImageData(((BitmapDrawable) picture_update_icon).getBitmap());
//		}
		Platform platform = ShareSDK.getPlatform(WechatMoments.NAME);
		platform.setPlatformActionListener(onPlatformActionListener);
		platform.share(sp);
	}

	public  void shareToWXFriends(){
		Wechat.ShareParams sp = new Wechat.ShareParams();
		sp.setShareType(Wechat.SHARE_WEBPAGE);
		sp.setTitle(context.getResources().getString(R.string.app_name));
		sp.setText(content);
		sp.setUrl(url);

		if (imageUrl != null) {
			sp.setImageUrl(imageUrl);
		} else {
			Drawable picture_update_icon = context.getResources().getDrawable(R.drawable.ic_launcher);
			sp.setImageData(((BitmapDrawable) picture_update_icon).getBitmap());
		}

		Platform platform = ShareSDK.getPlatform(Wechat.NAME);
		platform.setPlatformActionListener(onPlatformActionListener);
		platform.share(sp);
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setIsVideoShare(boolean isVideoShare) {
		this.isVideoShare = isVideoShare;
	}


	public PlatformActionListener getOnPlatformActionListener() {
		return onPlatformActionListener;
	}

	public void setOnPlatformActionListener(PlatformActionListener onPlatformActionListener) {
		this.onPlatformActionListener = onPlatformActionListener;
	}

	//把图片从drawable复制到sdcard中
	//copy the picture from the drawable to sdcard
	private void initImagePath() {
		try {
			if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
					&& Environment.getExternalStorageDirectory().exists()) {
				ShareIMAGE = Environment.getExternalStorageDirectory().getAbsolutePath() + FILE_NAME;
			} else {
				ShareIMAGE = context.getFilesDir().getAbsolutePath() + FILE_NAME;
			}
			File file = new File(ShareIMAGE);
			if (!file.exists()) {
				file.createNewFile();
				Bitmap pic = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
				FileOutputStream fos = new FileOutputStream(file);
				pic.compress(Bitmap.CompressFormat.JPEG, 100, fos);
				fos.flush();
				fos.close();
			}
		} catch (Throwable t) {
			t.printStackTrace();
			ShareIMAGE = null;
		}
	}



}
