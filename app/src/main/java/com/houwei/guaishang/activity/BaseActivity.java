package com.houwei.guaishang.activity;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.Type;
import com.easemob.util.EasyUtils;
import com.houwei.guaishang.MessageEvent;
import com.houwei.guaishang.R;
import com.houwei.guaishang.activity.newui.TopicDetailComActivity;
import com.houwei.guaishang.bean.AvatarBean;
import com.houwei.guaishang.bean.TopicBean;
import com.houwei.guaishang.bean.UserBean;
import com.houwei.guaishang.easemob.EaseCommonUtils;
import com.houwei.guaishang.easemob.EaseConstant;
import com.houwei.guaishang.easemob.EaseUI;
import com.houwei.guaishang.huanxin.ChatActivity;
import com.houwei.guaishang.huanxin.ChatFragment;
import com.houwei.guaishang.huanxin.ChatInfo;
import com.houwei.guaishang.layout.MProgressDialog;
import com.houwei.guaishang.manager.ITopicApplication;
import com.houwei.guaishang.manager.MyUserBeanManager;
import com.houwei.guaishang.manager.HuanXinManager.HuanXinLoginListener;
import com.houwei.guaishang.tools.JsonUtil;
import com.houwei.guaishang.tools.SPUtils;
import com.houwei.guaishang.tools.ValueUtil;
import com.houwei.guaishang.views.TipsToast;

import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 除了每次打开的欢迎页面之外 的所有activity都需要继承本类。
 * 功能：
 * 1、强行在onCreate里先验证是否初始化成功：调用checkInit()。若没有，先在本类里初始化，才能往下走子类的onCreate
 * 2、强行在onResume 和 onPause里插入友盟的统计API
 * 3、提供公共方法checkLogin，检查用户是否登录
 * 4、提供公共方法initProgressDialog，需要弹出加载中弹框的activity子类，需要先在oncreate里调用initProgressDialog
 * 5、提供获取当前登录用户的id方法getUserID，若没登录，返回""
 * 6、提供其他方法，比如跳转都聊天界面，跳转到个人主页，隐藏弹出键盘
 */
public  class BaseActivity extends FragmentActivity {
	private static final int notifiId = 11;

	public MProgressDialog progress;
	public final static int NETWORK_SUCCESS_DATA_RIGHT = 0x01;
	public final static int NETWORK_SUCCESS_PAGER_RIGHT = 0x02;
	public final static int NETWORK_OTHER = 0x19;
	public final static int NETWORK_SUCCESS_DATA_ERROR = 0x06;
	public final static int NETWORK_FAIL = 0x05;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		checkInit();
	}
	

	/**
	 * 如果登录了，返回true，否则返回false并去登录
	 * @return
	 */
	public boolean checkLogined(){
		if ("".equals(getUserID()) || getUserID() == null) {
			Intent i = new Intent(this, UserRegMobileActivity.class);
			startActivity(i);
			return false;
		}else{
			return true;
		}
	}

	
	private void checkInit() {
		ITopicApplication app = getITopicApplication();
		app.checkInit();
	}

	@Override
	protected void onResume() {
		super.onResume();
		  // onresume时，取消notification显示
		EaseUI.getInstance().getNotifier().reset();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
	public void initProgressDialog() {
		initProgressDialog(true, null);
	}

	public void initProgressDialog(boolean cancel, String message) {
		initProgressDialog(this, cancel, message);
	}

	public void initProgressDialog(Context mContext, boolean cancel,
			String message) {
		progress = new MProgressDialog(mContext, cancel);
	}

	public void showErrorToast() {
		showFailTips("无法连接到网络\n请稍后再试");
	}

	public void showFailTips(String content) {

		TipsToast tipsToast = TipsToast.makeText(getApplication().getBaseContext(),content, TipsToast.LENGTH_SHORT);	
		tipsToast.show();
	}

	public void showSuccessTips(String content) {

		TipsToast tipsToast = TipsToast.makeText(getApplication().getBaseContext(), content, TipsToast.LENGTH_SHORT);	
		tipsToast.setIcon(R.drawable.tips_success);
		tipsToast.show();
	}
	
	public void showErrorToast(String err) {
		Toast.makeText(getApplicationContext(), err, Toast.LENGTH_SHORT).show();
	}

	public void BackButtonListener() {
		findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				hideKeyboard();
				finish();
			}
		});
	}

	public ITopicApplication getITopicApplication() {
		return (ITopicApplication) getApplication();
	}


	

	public void jumpToHisInfoActivity( String UserID,
			String realName,AvatarBean headImageBean) {

		 Intent i = new Intent(this, HisRootActivity.class);
		i.putExtra(HisRootActivity.HIS_AVATAR_KEY, headImageBean);
		i.putExtra(HisRootActivity.HIS_ID_KEY, UserID);
		i.putExtra(HisRootActivity.HIS_NAME_KEY, realName);
		startActivity(i);
	}
	

	
	
	
	public void jumpToChatActivity(final String hisUserID, final String hisRealName,
								   final AvatarBean headImageBean,
								   final int chatType, final String mobile, final boolean showPrice) {
		if(!checkLogined()){
			return;
		}
		if (getITopicApplication().getHuanXinManager().getHxSDKHelper().isLoggedIn()) {			
			intentToChatActivity(hisUserID, hisRealName, headImageBean,chatType,mobile,showPrice);
		} else{
		
			final	MProgressDialog progress = new MProgressDialog(this, false);
			progress.show();
			progress.setMessage("连接服务器中...");
			//两方注册都成功
			UserBean bean = getITopicApplication().getMyUserBeanManager().getInstance();
			 getITopicApplication().getHuanXinManager().loginHuanXinService(this,bean.getUserid(),bean.getName(),new HuanXinLoginListener() {
				
				@Override
				public void onHuanXinLoginSuccess() {
					// TODO Auto-generated method stub
					progress.dismiss();
					intentToChatActivity(hisUserID, hisRealName, headImageBean,chatType,mobile,showPrice);
				}
				
				@Override
				public void onHuanXinLoginFail(int code, String message) {
					// TODO Auto-generated method stub
					progress.dismiss();
					showFailTips(getString(R.string.Login_failed) + message);
				}
			});
		
		}
	}
//
	public void jumpToChatActivityCom(final TopicBean topicBean, final int i, final String hisUserID, final String hisRealName, final AvatarBean headImageBean, final int chatType) {
		jumpToChatActivityCom(topicBean,i,hisUserID,hisRealName,headImageBean,chatType,false);
	}

	public void jumpToChatActivityCom(final TopicBean topicBean, final int i, final String hisUserID,
									  final String hisRealName, final AvatarBean headImageBean, final int chatType, final boolean needPay) {
		if(!checkLogined()){
			return;
		}
		if (getITopicApplication().getHuanXinManager().getHxSDKHelper().isLoggedIn()) {
			intentToChatActivityCom(topicBean,i,hisUserID, hisRealName, headImageBean,chatType,needPay);
		} else{

			final	MProgressDialog progress = new MProgressDialog(this, false);
			progress.show();
			progress.setMessage("连接服务器中...");
			//两方注册都成功
			final UserBean bean = getITopicApplication().getMyUserBeanManager().getInstance();
			 getITopicApplication().getHuanXinManager().loginHuanXinService(this,bean.getUserid(),bean.getName(),new HuanXinLoginListener() {

				@Override
				public void onHuanXinLoginSuccess() {
					// TODO Auto-generated method stub
					progress.dismiss();
					intentToChatActivityCom(topicBean,i,hisUserID, hisRealName, headImageBean,chatType,false);
				}

				@Override
				public void onHuanXinLoginFail(int code, String message) {
					// TODO Auto-generated method stub
					progress.dismiss();
					showFailTips(getString(R.string.Login_failed) + message);
				}
			});

		}
	}

	private void intentToChatActivity(String hisUserID,
			String hisRealName,AvatarBean headImageBean,int chatType,String mobile,boolean showPrice){
		Intent i = new Intent(this, ChatActivity.class);
		if (headImageBean == null) {
			headImageBean = new AvatarBean();
		}
        ChatInfo chatInfo = new ChatInfo();
        chatInfo.setMobile(mobile);
        chatInfo.setHisUserID(hisUserID);
        chatInfo.setHisRealName(hisRealName);
        chatInfo.setChatType(chatType);
        chatInfo.setHeadImageBean(headImageBean);
		chatInfo.setShowPriceInfo(showPrice);
		i.putExtra(ChatActivity.Chat_info,chatInfo);
//        i.putExtra(ChatActivity.Chat_info,chatInfo);
//		i.putExtra(HisRootActivity.HIS_ID_KEY, hisUserID);
//		i.putExtra(HisRootActivity.HIS_NAME_KEY, hisRealName);
//		i.putExtra(HisRootActivity.HIS_AVATAR_KEY, headImageBean);
//		i.putExtra(EaseConstant.EXTRA_CHATTYPE, chatType);
//		i.putExtra(HisRootActivity.HIS_MOBILE_KEY,mobile);
		startActivity(i);
	}
	private void intentToChatActivityCom(TopicBean bean, int position,String hisUserID,
										 String hisRealName,AvatarBean headImageBean,int chatType, boolean needPay){
		Intent i = new Intent(this, TopicDetailComActivity.class);
		if (headImageBean == null) {
			headImageBean = new AvatarBean();
		}
		i.putExtra("TopicBean", bean);
		i.putExtra("position", position);
		i.putExtra(HisRootActivity.HIS_ID_KEY, hisUserID);
		i.putExtra(HisRootActivity.HIS_NAME_KEY, hisRealName);
		i.putExtra(HisRootActivity.HIS_AVATAR_KEY, headImageBean);
		i.putExtra(HisRootActivity.HIS_AVATAR_KEY, headImageBean);
		i.putExtra(EaseConstant.EXTRA_NEED_PAY, needPay);
		i.putExtra(EaseConstant.EXTRA_CHATTYPE, chatType);
		startActivity(i);
	}



	public void setTitleName(String titleName) {
		TextView title = (TextView) findViewById(R.id.title);
		title.setText(titleName);
	}


	public void hideKeyboard(View v) {
		((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
				.hideSoftInputFromWindow(v.getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
	}

	public void hideKeyboard() {
		hideKeyboard(getWindow().getDecorView());
	}
	
	public void showKeyboard(EditText et) {
		((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
				.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
	}
	
	public String getUserID() {
		UserBean instanceUser = getITopicApplication()
				.getMyUserBeanManager().getInstance();
		return instanceUser == null ? "" : instanceUser.getUserid();
	}
	public String getBrandId() {
		String brandId= (String) SPUtils.get(this,"brand_id","");
		return brandId;
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void on3EventMainThread(MessageEvent messageEvent){

	}
}
