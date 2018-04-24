package com.houwei.guaishang.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.baidu.tts.tools.SharedPreferencesUtils;
import com.houwei.guaishang.R;
import com.houwei.guaishang.bean.CommentPushBean;
import com.houwei.guaishang.bean.FansPushBean;
import com.houwei.guaishang.bean.UserBean;
import com.houwei.guaishang.bean.VersionResponse.VersionBean;
import com.houwei.guaishang.data.DBReq;
import com.houwei.guaishang.event.TopicSelectEvent;
import com.houwei.guaishang.layout.MenuTwoButtonDialog;
import com.houwei.guaishang.manager.ChatManager.OnMyActionMessageGetListener;
import com.houwei.guaishang.manager.ChatManager.OnMyActionMessageHadReadListener;
import com.houwei.guaishang.manager.ITopicApplication;
import com.houwei.guaishang.manager.MyUserBeanManager.UserStateChangeListener;
import com.houwei.guaishang.manager.VersionManager.LastVersion;
import com.houwei.guaishang.tools.ToastUtils;
import com.houwei.guaishang.tools.Utils;
import com.houwei.guaishang.tools.VoiceUtils;
import com.houwei.guaishang.view.PublishOrderDialog;
import com.lzy.imagepicker.bean.ImageItem;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import cn.jpush.android.api.JPushInterface;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

/**
 * Root界面，包括底部3个tab
 * NewMessageGetListener，来新聊天消息监听
 * UserStateChangeListener，当前账号状态监听
 * OnMyActionMessageGetListener，来新穿透消息监听（新粉丝，新评论）
 * OnMyActionMessageHadReadListener，新穿透消息已读监听（新粉丝已被读，新评论已被读）
 * LastVersion，是否有新版本监听
 */
public class MainActivity extends MainHuanXinActivity implements
		 UserStateChangeListener,
		OnMyActionMessageGetListener, OnMyActionMessageHadReadListener, LastVersion {

	private TextView unReadActionCountTV, unReadFansCountTV;
	private ITopicApplication app;

	private int currentTabIndex;//当前选中的tab
	
	private TopicRootFragment topicFragment;
	private MineFragment mineFragment;
//	private MineFragment mineFragment;

	private String videoPath;

	private boolean showTopicFragment;

	private ArrayList<ImageItem> selImageList; //当前选择的所有图片
	private int maxImgCount = 1;
	public static final int IMAGE_ITEM_ADD = -1;
	public static final int REQUEST_CODE_SELECT = 100;
	public static final int REQUEST_CODE_PREVIEW = 101;
	ArrayList<ImageItem> images = null;
	private RxPermissions rxPermissions;
	private RadioGroup rgOpterator;
	private RadioButton topicRadioButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			videoPath = savedInstanceState.getString("videoPath");
		}
		setContentView(R.layout.activity_main);
		rxPermissions = new RxPermissions(this);
		EventBus.getDefault().register(this);
		initView();
		initListener();
		afterContentView();
		checkHuanXinIsLogined();
//		ShareSDK.initSDK(this);

	}

	
	private void initView() {
		app = (ITopicApplication) getApplication();
	
		unReadActionCountTV = (TextView) findViewById(R.id.unReadActionCountTV);
		unReadFansCountTV = (TextView) findViewById(R.id.unReadFansCountTV);
		//未读评论 赞
		checkUnReadActionCount(DBReq.getInstence(this).getTotalUnReadCommentCount());
		
		rgOpterator = (RadioGroup) findViewById(R.id.rgOperator);
		topicRadioButton = (RadioButton) findViewById(R.id.topic_radio);
		
		currentTabIndex = rgOpterator.getCheckedRadioButtonId();
				
		rgOpterator.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				
				switch (checkedId) {
				case R.id.topic_radio:{
					FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
					
					if (null == topicFragment) {
						topicFragment = new TopicRootFragment();
                    }
					hideCurrentFragment(transaction);
					
					if (!topicFragment.isAdded()){
						 transaction.add(R.id.container,topicFragment);    
					}else{
						 transaction.show(topicFragment);
					}
					
                    transaction.commit();
                 
				}
					break;
				
				case R.id.mine_radio:{
					
					FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
					
					if (null == mineFragment) {
						mineFragment = new MineFragment();
//						mineFragment = new MineFragment();
                    }
                
					hideCurrentFragment(transaction);
					
					if (!mineFragment.isAdded()){
						 transaction.add(R.id.container,mineFragment);    
					}else{
						 transaction.show(mineFragment);
					}
					
                    transaction.commit();

					break;
				}
				}
				currentTabIndex = checkedId;
			}
		});
		
		topicFragment = new TopicRootFragment();
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.add(R.id.container,topicFragment);    
		transaction.commit();
		
		findViewById(R.id.publish_btn).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						if (!checkLogined()) {
							return;
						}

						rxPermissions.request(Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE
						,Manifest.permission.ACCESS_COARSE_LOCATION,
						Manifest.permission.ACCESS_FINE_LOCATION,
						Manifest.permission.READ_EXTERNAL_STORAGE,
						Manifest.permission.WRITE_EXTERNAL_STORAGE
								)
								.subscribe(new Consumer<Boolean>() {
									@Override
									public void accept(@NonNull Boolean aBoolean) throws Exception {
											if(aBoolean){
												PublishOrderDialog.getInstance(MainActivity.this).show();
												/*MenuTwoButtonDialog dialog = new MenuTwoButtonDialog(MainActivity.this, new MenuTwoButtonDialog.ButtonClick() {

													@Override
													public void onSureButtonClick(int index) {
														// TODO Auto-generated method stub
														Intent i=new Intent(MainActivity.this,TopicReleaseActivity.class);
														switch (index) {
															case 0:
																i.putExtra("type",0);
																startActivityForResult(i,0);
//									*//**
//									 * 0.4.7 目前直接调起相机不支持裁剪，如果开启裁剪后不会返回图片，请注意，后续版本会解决
//									 *
//									 * 但是当前直接依赖的版本已经解决，考虑到版本改动很少，所以这次没有上传到远程仓库
//									 *
//									 * 如果实在有所需要，请直接下载源码引用。
//									 *//*
//									//打开选择,本次允许选择的数量
//                               		 ImagePicker.getInstance().setSelectLimit(9);
//									Intent intent = new Intent(MainActivity.this, ImageGridActivity.class);
//									intent.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS, true); // 是否是直接打开相机
//									startActivityForResult(intent, REQUEST_CODE_SELECT);
																break;

															case 1:
																i.putExtra("type",1);
																startActivityForResult(i,0);
																//打开选择,本次允许选择的数量
//                                ImagePicker.getInstance().setSelectLimit(1);
//										ImagePicker.getInstance().setCrop(true);        //允许裁剪（单选才有效）
//										ImagePicker.getInstance().setSaveRectangle(false); //是否按矩形区域保存
//										ImagePicker.getInstance().setSelectLimit(9);    //选中数量限制
//										ImagePicker.getInstance().setStyle(CropImageView.Style.CIRCLE);  //裁剪框的形状
//										ImagePicker.getInstance().setFocusWidth(600);   //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
//										ImagePicker.getInstance().setFocusHeight(600);  //裁剪框的高度。单位像素（圆形自动取宽高最小值）
//										ImagePicker.getInstance().setOutPutX(200);//保存文件的宽度。单位像素
//										ImagePicker.getInstance().setOutPutY(200);//保存文件的高度。单位像素
//										Intent intent1 = new Intent(MainActivity.this, ImageGridActivity.class);
                                *//* 如果需要进入选择的时候显示已经选中的图片，
                                 * 详情请查看ImagePickerActivity
                                 * *//*
//                                intent1.putExtra(ImageGridActivity.EXTRAS_IMAGES,images);
//										startActivityForResult(intent1, REQUEST_CODE_SELECT);
																break;
															default:
																//打开预览
//										Intent intentPreview = new Intent(MainActivity.this, ImagePreviewDelActivity.class);
//										intentPreview.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, (ArrayList<ImageItem>) adapter.getImages());
//										intentPreview.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, position);
//										intentPreview.putExtra(ImagePicker.EXTRA_FROM_ITEMS, true);
//										startActivityForResult(intentPreview, REQUEST_CODE_PREVIEW);
																break;
														}
													}
												});
												dialog.title_tv.setText("现在拍照");
												dialog.tv2.setText("从相册里选取");
												dialog.show();*/
											}
									}
								});
					}
				});
	}

	//隐藏当前的Fragment，切换tab时候使用
	private void hideCurrentFragment(FragmentTransaction transaction) {
		switch (currentTabIndex) {
		case R.id.topic_radio:
			transaction.hide(topicFragment);
			break;
		case R.id.mine_radio:
			transaction.hide(mineFragment);
			break;
		default:
			break;
		}
	}

	private void initListener() {
		// TODO Auto-generated method stub
		app.getChatManager().addOnMyActionMessageGetListener(this);
		app.getChatManager().addOnMyActionMessageHadReadListener(this);
		app.getMyUserBeanManager().addOnUserStateChangeListener(this);
		app.getVersionManager().setOnLastVersion(this);
		app.getVersionManager().checkNewVersion();
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void topicFragmentSelect(TopicSelectEvent event) {
		showTopicFragment = true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (showTopicFragment) {
			if (topicRadioButton != null) {
				topicRadioButton.setChecked(true);
			}
			showTopicFragment = false;
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		app.getChatManager().removeOnMyActionMessageGetListener(this);
		app.getChatManager().removeOnMyActionMessageHadReadListener(this);
		app.getMyUserBeanManager().removeUserStateChangeListener(this);
		app.getVersionManager().removeListener(this);
//		ShareSDK.stopSDK(this);
		EventBus.getDefault().unregister(this);
		super.onDestroy();

	}

	private void checkHuanXinIsLogined() {
		UserBean bean = getITopicApplication().getMyUserBeanManager().getInstance();
		if (bean!=null && !demoEaseHelper.isLoggedIn())
		{
			//已经是登陆状态，但是环信的长连接断了，需要登录环信长连接
			getITopicApplication().getHuanXinManager().loginHuanXinService(this,bean.getUserid(),bean.getName(),null);
		}
	}

	/**
	 * 计算出 “我的”tab上红字 应该是 未读预订+未读付款+未读粉丝+未读聊天
	 */
	private int getUnreadMineCount() {
		return getUnreadMsgCountTotal() + DBReq.getInstence(this).getTotalUnReadFansCount();
	}

	@Override
	protected void checkMessageUnReadCount() {
		checkUnReadFansCount(getUnreadMineCount());
	}

	@Override
	public void onUserInfoChanged(UserBean ub) {
		// TODO Auto-generated method stub
	}


	@Override
	public void onUserLogin(final UserBean ub) {
		// TODO Auto-generated method stub
		rxPermissions.request(Manifest.permission.READ_PHONE_STATE)
				.subscribe(new Consumer<Boolean>() {
					@Override
					public void accept(@NonNull Boolean aBoolean) throws Exception {
						String alias = ub.getUserid();
						if(aBoolean){
							alias = Utils.getImei(MainActivity.this) + ub.getUserid();
						}
						SharedPreferencesUtils.putString(MainActivity.this, "JPush_alias", alias);
						JPushInterface.setAlias(MainActivity.this,1, alias);
					}
				});
	}


	@Override
	public void onUserLogout() {
		// TODO Auto-generated method stub
//		finish();
	}

	
	
	/**
	 * get
	 */
	@Override
	public void onMyNewFansGet(FansPushBean fansPushBean) {
		// TODO Auto-generated method stub
		checkUnReadFansCount(getUnreadMineCount());
	}

	@Override
	public void onNewCommentGet(CommentPushBean commentPushBean) {
		// TODO Auto-generated method stub
		checkUnReadActionCount(DBReq.getInstence(this).getTotalUnReadCommentCount());
	}


	
	/**
	 * read
	 */
	@Override
	public void onFansHadRead() {
		// TODO Auto-generated method stub
		checkUnReadFansCount(getUnreadMineCount());
	}

	@Override
	public void onCommentsHadRead() {
		// TODO Auto-generated method stub
		checkUnReadActionCount(0);
	}

	
	private void checkUnReadActionCount(int unReadActionCount) {
		unReadActionCountTV.setText("" + unReadActionCount);
		unReadActionCountTV.setVisibility(unReadActionCount == 0 ? View.INVISIBLE
						: View.VISIBLE);
	}

	private void checkUnReadFansCount(int unReadActionCount) {
		unReadFansCountTV.setText("" + unReadActionCount);
		unReadFansCountTV.setVisibility(unReadActionCount == 0 ? View.INVISIBLE
				: View.VISIBLE);
	}

	
	@Override
	public void isLastVersion() {
		// TODO Auto-generated method stub
	}

	@Override
	public void versionNetworkFail(String message) {
		// TODO Auto-generated method stub
	}

	@Override
	public void notLastVersion(VersionBean versionBean) {
		// TODO Auto-generated method stub
		// 不是最新版本。提示用户
		app.getVersionManager().downLoadNewVersion(versionBean, this);
	}



	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
//		UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
			case TopicReleaseActivity.RELEASE_SUCCESS:
				if (data != null && data.getStringExtra("brand") != null) {
					String brand = data.getStringExtra("brand");
					VoiceUtils.getInstance(this).speak("发布成功，买"+brand+"就上咚咚砸单");
				}
				if(topicFragment!=null){
					topicFragment.refreshList(0);
				}
				break;
			default:
				break;
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString("videoPath", videoPath);
		super.onSaveInstanceState(outState);
	}
}
