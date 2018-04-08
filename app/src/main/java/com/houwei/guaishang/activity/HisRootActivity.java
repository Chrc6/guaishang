package com.houwei.guaishang.activity;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.houwei.guaishang.R;
import com.houwei.guaishang.bean.AvatarBean;
import com.houwei.guaishang.bean.HisInfoResponse;
import com.houwei.guaishang.bean.IntResponse;
import com.houwei.guaishang.bean.StringResponse;
import com.houwei.guaishang.bean.TopicBean;
import com.houwei.guaishang.bean.UserBean;
import com.houwei.guaishang.easemob.EaseConstant;
import com.houwei.guaishang.layout.MenuDialog;
import com.houwei.guaishang.layout.MenuTwoButtonDialog;
import com.houwei.guaishang.layout.SureOrCancelInterfaceDialog;
import com.houwei.guaishang.layout.TopicAdapter;
import com.houwei.guaishang.manager.FollowManager.FollowListener;
import com.houwei.guaishang.manager.MyUserBeanManager.EditInfoListener;
import com.houwei.guaishang.manager.MyUserBeanManager.UserStateChangeListener;
import com.houwei.guaishang.tools.HttpUtil;
import com.houwei.guaishang.tools.JsonParser;
import com.houwei.guaishang.tools.ValueUtil;
import com.houwei.guaishang.view.cropimage.CropImageActivity;
import com.houwei.guaishang.views.CircleBitmapDisplayer;
import com.houwei.guaishang.views.ParallaxListView;
import com.easemob.chat.EMContactManager;
import com.easemob.exceptions.EaseMobException;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.text.ClipboardManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//查看他人主页activity
public class HisRootActivity extends BasePhotoActivity{

	public static final String SENDER_ID_KEY = "senderUserId";
	public static final String SENDER_NAME_KEY = "senderName";
	public static final String SENDER_AVATAR_KEY = "senderAvatar";

	public static final String RECEIVER_ID_KEY = "receiverUserId";
	public static final String RECEIVER_NAME_KEY = "receiverName";
	public static final String RECEIVER_AVATAR_KEY = "receiverAvatar";

	public static final String HIS_ID_KEY = "hisUserId";
	public static final String HIS_NAME_KEY = "hisName";
	public static final String HIS_AVATAR_KEY = "hisAvatar";
	
	private HisRootFragment hisRootFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_framelayout);
		initView();
		initListener();
	}

	protected void initListener() {
		// TODO Auto-generated method stub
		
	}

	protected void initView() {
		// TODO Auto-generated method stub
		initProgressDialog();
		setNeedCropImage(true);
		
		hisRootFragment = new HisRootFragment();
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.add(R.id.container,hisRootFragment);    
		transaction.commit();
	}
	
	
	/**
	 * 选取图片成功
	 * 
	 * @param imageUrl
	 * @param currentImageView
	 */
	public void onPhotoSelectSuccess(String picturePath,
			ImageView currentImageView) {
	}

	/**
	 * 上传成功
	 * 
	 * @param imageUrl
	 * @param currentImageView
	 */
	public void onPhotoUploadSuccess(String imageUrl, String picturePath,
			ImageView currentImageView) {
		hisRootFragment.progress.show();
		getITopicApplication().getMyUserBeanManager().
			startEditInfoRun("background", imageUrl, hisRootFragment);
	}

	/**
	 * 上传失败
	 * 
	 * @param
	 * @param currentImageView
	 */
	public void onPhotoUploadFail(ImageView currentImageView) {
	}
}
