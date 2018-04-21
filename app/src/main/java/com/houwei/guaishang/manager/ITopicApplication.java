package com.houwei.guaishang.manager;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.baidu.mapapi.SDKInitializer;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.stetho.Stetho;
import com.houwei.guaishang.tools.ApplicationProvider;
import com.houwei.guaishang.view.MyImageLoader;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.view.CropImageView;
import com.mob.MobApplication;
import com.mob.MobSDK;
import com.umeng.analytics.MobclickAgent;


import cn.beecloud.BeeCloud;
import cn.jpush.android.api.JPushInterface;

public class ITopicApplication extends MobApplication {

	private static Application application;

	// 定位功能想关的Manager管理类
	private MyLocationManager locationManager;
	// 其他一些杂七杂八的Manager管理类，现在是只有Imageloader
	private OtherManager otherManage;
	// 处理当前登录的用户相关的信息
	private MyUserBeanManager myUserBeanManager;
	// 处理用户关注他人的管理器
	private FollowManager followManager;
	// 处理首页上的动态相关的，比如点赞评论
	private HomeManager homeManager;
	// 处理推送来的穿透消息（cmd消息）
	private ChatManager chatManager;
	// 处理版本更新问题
	private VersionManager versionManager;
	// 环信相关的类库
	private HuanXinManager huanXinManager;
	// 表情
	private FaceManager faceManager;
	private boolean hadInit; // 如果为false
								// 说明Application以及被系统回收了，需要重新初始化一遍所有的Manager


	@Override
	public void onCreate() {
		super.onCreate();
		MultiDex.install(this);
		MobSDK.init(this,"15b5b9e067b56","7b60e80917dd1d9b1f90223b02215b9b");

		//极光初始化
		JPushInterface.setDebugMode(true);
		JPushInterface.init(this);

//		Stetho.initializeWithDefaults(this);
		ApplicationProvider.init(this);

//		okgo初始化
//		OkGo.getInstance().init(this);
		ImagePicker imagePicker = ImagePicker.getInstance();
		imagePicker.setImageLoader(new MyImageLoader());   //设置图片加载器
		imagePicker.setMultiMode(true);  //图片选择模式 默认多选
		imagePicker.setShowCamera(true);  //显示拍照按钮
		imagePicker.setCrop(false);        //允许裁剪（单选才有效）
		imagePicker.setSelectLimit(7);    //选中数量限制
		imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
		imagePicker.setSaveRectangle(true);  //true 按照矩形保存，false 按照裁剪框形状保存
		imagePicker.setFocusWidth(800);   //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
		imagePicker.setFocusHeight(800);  //裁剪框的高度。单位像素（圆形自动取宽高最小值）


//

//		PublicStaticData.myShareSDK= new ShareSDK();
//		PublicStaticData.myShareSDK.initSDK(getApplicationContext());


		SDKInitializer.initialize(getApplicationContext());//百度地图
		Fresco.initialize(this);
	}

	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();
	}

	public void checkInit() {
		if (!hadInit) {
			myUserBeanManager = new MyUserBeanManager(this);
			locationManager = new MyLocationManager(this);
			otherManage = new OtherManager(this);
			followManager = new FollowManager(this);
			homeManager = new HomeManager(this);
			versionManager = new VersionManager(this);
			huanXinManager = new HuanXinManager(this);
			chatManager = new ChatManager(this);
			faceManager = new FaceManager(this);
			otherManage.initOther();
			faceManager.initFaceMap();
			myUserBeanManager.checkUserInfo();
			homeManager.resetPaidTopicPhotoArray();
			huanXinManager.loadAllConversations();
//			短信

//友盟
			MobclickAgent.setDebugMode(true);
			MobclickAgent.setCatchUncaughtExceptions(true); //是否需要错误统计功能
			MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
//			MobclickAgent.updateOnlineConfig(this);
//            Log.d("CCC",UmengUtil.getDeviceInfo(this));
			BeeCloud.setAppIdAndSecret("375dfba1-50cf-419d-8f95-a0933f92ce0b",
	                "00b0dc19-571b-44cd-9563-cc8811f93a15");
			hadInit = true;
		}
	}

	public OtherManager getOtherManage() {
		return otherManage;
	}

	public MyLocationManager getLocationManager() {
		return locationManager;
	}

	public MyUserBeanManager getMyUserBeanManager() {
		return myUserBeanManager;
	}

	public FollowManager getFollowManager() {
		return followManager;
	}

	public HomeManager getHomeManager() {
		return homeManager;
	}

	public ChatManager getChatManager() {
		return chatManager;
	}

	public VersionManager getVersionManager() {
		return versionManager;
	}

	public HuanXinManager getHuanXinManager() {
		return huanXinManager;
	}

	public FaceManager getFaceManager() {
		return faceManager;
	}

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		MultiDex.install(this);
	}
}