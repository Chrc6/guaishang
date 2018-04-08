package com.houwei.guaishang.manager;

import java.util.ArrayList;

import com.houwei.guaishang.R;
import com.houwei.guaishang.activity.BaseActivity;
import com.houwei.guaishang.activity.MainActivity;
import com.houwei.guaishang.activity.WelcomeActivity;
import com.houwei.guaishang.bean.BasePushResult;
import com.houwei.guaishang.bean.CommentPushBean;
import com.houwei.guaishang.bean.FansPushBean;
import com.houwei.guaishang.bean.PraiseResponse;
import com.houwei.guaishang.bean.UserBean;
import com.houwei.guaishang.data.DBReq;
import com.houwei.guaishang.easemob.DemoHelper.CmdMessageGetListener;
import com.houwei.guaishang.manager.HomeManager.TopicDeleteListener;
import com.houwei.guaishang.manager.HomeManager.TopicPraiseCountChangeListener;
import com.houwei.guaishang.tools.JsonParser;
import com.houwei.guaishang.tools.LogUtil;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;


public class ChatManager implements CmdMessageGetListener  {
	private ITopicApplication mApp;
	private ArrayList<OnMyActionMessageGetListener> onMyActionMessageGetListenerList;
	private ArrayList<OnMyActionMessageHadReadListener> onMyActionMessageHadReadList;

	//MainActivty用这个来判断 是否ChatActivity存活 并获取当前对话人
	private String currentChatToUserId;
	
	private Handler handler = new Handler();
	
	
	public ChatManager(ITopicApplication mApp) {
		this.mApp = mApp;
		onMyActionMessageGetListenerList = new ArrayList<ChatManager.OnMyActionMessageGetListener>();
		onMyActionMessageHadReadList = new ArrayList<ChatManager.OnMyActionMessageHadReadListener>();
		mApp.getHuanXinManager().getHxSDKHelper().setOnCmdMessageGetListener(this);
	}

	public void closeNewMessageGetListener() {
		mApp.getHuanXinManager().getHxSDKHelper().setOnCmdMessageGetListener(null);
	}






	public static boolean isConnected(Context context) {
		ConnectivityManager conn = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = conn.getActiveNetworkInfo();
		return (info != null && info.isConnected());
	}

	

	public void onNewFansGet(FansPushBean fansPushBean) {
		// TODO Auto-generated method stub
		//更新本地保存的之前的粉丝数
		MyUserBeanManager myUserBeanManager = mApp.getMyUserBeanManager();
		if (myUserBeanManager != null && myUserBeanManager.getInstance() != null) {
			UserBean ub = myUserBeanManager.getInstance();
			ub.setFansCount(ub.getFansCount()+1);
			myUserBeanManager.storeUserInfo(ub);
			myUserBeanManager.notityUserInfoChanged(ub);
		}
		
		for (OnMyActionMessageGetListener onMyActionMessageGetListener : onMyActionMessageGetListenerList) {
			onMyActionMessageGetListener.onMyNewFansGet(fansPushBean);
		}
	}

	public void onNewDynamicCommentGet(CommentPushBean commentPushBean) {
		// TODO Auto-generated method stub
		for (OnMyActionMessageGetListener onMyActionMessageGetListener : onMyActionMessageGetListenerList) {
			onMyActionMessageGetListener.onNewCommentGet(commentPushBean);
		}
	}



	/**
	 * 收到的关注和评论消息
	 * @author acer
	 */
	public interface OnMyActionMessageGetListener {
		
		public void onMyNewFansGet(FansPushBean fansPushBean);
		
		public void onNewCommentGet(CommentPushBean commentPushBean);
		
	}
	
	
	public void addOnMyActionMessageGetListener(OnMyActionMessageGetListener onMyActionMessageGetListener) {
		if (onMyActionMessageGetListener != null
				&& !onMyActionMessageGetListenerList.contains(onMyActionMessageGetListener)) {
			onMyActionMessageGetListenerList.add(onMyActionMessageGetListener);
		}
	}

	public void removeOnMyActionMessageGetListener(OnMyActionMessageGetListener onMyActionMessageGetListener) {
		if (onMyActionMessageGetListener != null
				&& onMyActionMessageGetListenerList.contains(onMyActionMessageGetListener)) {
			onMyActionMessageGetListenerList.remove(onMyActionMessageGetListener);
		}
	}
	
	
	/**
	 * 动态消息已读，目前是单点 由CommentUnReadActivity调用thisMessageHadRead方法， thisMessageHadRead
	 * 做数据库刷新通知 再 给HomeActivity 和mainActivity
	 * 
	 * @author acer
	 * 
	 */
	public interface OnMyActionMessageHadReadListener {
		public void onFansHadRead();
		public void onCommentsHadRead();
	}

	public void addOnMyActionMessageHadReadListener(OnMyActionMessageHadReadListener onMyActionMessageHadRead) {
		if (onMyActionMessageHadRead != null
				&& !onMyActionMessageHadReadList.contains(onMyActionMessageHadRead)) {
			onMyActionMessageHadReadList.add(onMyActionMessageHadRead);
		}
	}

	public void removeOnMyActionMessageHadReadListener(OnMyActionMessageHadReadListener onMyActionMessageHadRead) {
		if (onMyActionMessageHadRead != null
				&& onMyActionMessageHadReadList.contains(onMyActionMessageHadRead)) {
			onMyActionMessageHadReadList.remove(onMyActionMessageHadRead);
		}
	}

	public void readFansAction(){
		DBReq.getInstence(mApp).deleteFansHadRead();
		for (OnMyActionMessageHadReadListener onMyActionMessageHadRead : onMyActionMessageHadReadList) {
			onMyActionMessageHadRead.onFansHadRead();
		}
	}
	
	public void readCommentAction(){
		DBReq.getInstence(mApp).deleteCommentHadRead();
		for (OnMyActionMessageHadReadListener onMyActionMessageHadRead : onMyActionMessageHadReadList) {
			onMyActionMessageHadRead.onCommentsHadRead();
		}
	}
	
	

	public String getCurrentChatToUserId() {
		return currentChatToUserId;
	}

	public void setCurrentChatToUserId(String currentChatToUserId) {
		this.currentChatToUserId = currentChatToUserId;
	}

	@Override
	public void onCmdMessageGet(String action) {
		// TODO Auto-generated method stub

		BasePushResult basePushResult = JsonParser.getBasePushResult(action);
		if (basePushResult == null) {
			return;
		}

		switch (basePushResult.getPushType()) {
		case BasePushResult.FANS_PUSH:
			final FansPushBean fansPushBean = JsonParser.getFansPushBean(action);
			if (fansPushBean != null) {
				fansPushBean.setUnReaded(true);
				if (!DBReq.getInstence(mApp).CheckFansBeanExist(
						fansPushBean.getFansId())) {
					DBReq.getInstence(mApp).addFansPushBean(fansPushBean);
					
					//由于这里是子线程，要回到主线程操作
					handler.post(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							onNewFansGet(fansPushBean);
						}
					});
				}
			}
			break;
		case BasePushResult.TOPIC_COMMENT_PUSH:
			// 话题
			final CommentPushBean commentBean = JsonParser.getCommentPushBean(action);

			if (commentBean != null) {
				commentBean.setUnReaded(true);
				DBReq.getInstence(mApp).addComment(commentBean);

				//由于这里是子线程，要回到主线程操作
				handler.post(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						onNewDynamicCommentGet(commentBean);
					}
				});
		
			}
			break;
		
		default:
			break;
		}

	}
	



}
