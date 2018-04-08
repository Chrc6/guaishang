package com.houwei.guaishang.manager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.houwei.guaishang.activity.BaseActivity;
import com.houwei.guaishang.activity.MissionActivity;
import com.houwei.guaishang.bean.AvatarBean;
import com.houwei.guaishang.bean.IntResponse;
import com.houwei.guaishang.bean.ModifyResponse;
import com.houwei.guaishang.bean.UserBean;
import com.houwei.guaishang.data.DBReq;
import com.houwei.guaishang.tools.HttpUtil;
import com.houwei.guaishang.tools.JsonParser;
import com.houwei.guaishang.tools.JsonUtil;
import com.houwei.guaishang.tools.LogUtil;
import com.houwei.guaishang.tools.ValueUtil;
import com.houwei.guaishang.bean.FloatResponse;
import com.houwei.guaishang.manager.MyUserBeanManager.CheckMoneyListener;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

public class MyUserBeanManager {

	/**
	 * 本app支持 积分和任务体系
	 */
	public static final boolean MISSION_ENABLE = true; 
	
	private UserBean instanceUser;

	private ITopicApplication mContext;
	private ArrayList<UserStateChangeListener> onUserStateChangeListenerList;
	private EditInfoListener onEditListener;
	private ArrayList<CheckPointListener> onCheckPointListenerList;
	private ArrayList<CheckMoneyListener> onCheckMoneyListenerList;
	private Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case BaseActivity.NETWORK_SUCCESS_DATA_RIGHT:
				ModifyResponse response = (ModifyResponse) msg.obj;
				if (response.isSuccess()) {
					//修改资料成功，重新修改userbean
					UserBean newUserBean = response.getData();
					instanceUser.setAge(newUserBean.getAge());
					instanceUser.setAvatar(newUserBean.getAvatar());
					instanceUser.setSex(newUserBean.getSex());
					instanceUser.setIntro(newUserBean.getIntro());
					instanceUser.setPersonalTags(newUserBean.getPersonalTags());
					instanceUser.setName(newUserBean.getName());
					instanceUser.setBackground(newUserBean.getBackground());
					
					//重新保存用户信息
					storeUserInfo(instanceUser);
					//用观察者模式通知全部监听用户资料的界面，告诉他们用户信息更改了
					notityUserInfoChanged(instanceUser);
					//回掉网络访问修改成功
					if (onEditListener != null) {
						onEditListener.onEditSuccess();
					}
				} else if (onEditListener != null) {
					onEditListener.onEditFail(response.getMessage());
				}
				break;
				
			case BaseActivity.NETWORK_SUCCESS_PAGER_RIGHT:
				IntResponse intResponse = (IntResponse) msg.obj;
				
				for (int i = onCheckPointListenerList.size() -1; i >= 0 ; i--) {
					onCheckPointListenerList.get(i).onCheckPointFinish(intResponse);
				}
				break;
			case BaseActivity.NETWORK_OTHER:
				FloatResponse moneyResponse = (FloatResponse) msg.obj;
				for (CheckMoneyListener onCheckMoneyListener : onCheckMoneyListenerList) {
					onCheckMoneyListener.onCheckMoneyFinish(moneyResponse);
				}
				break;
			}
		}
	};

	public MyUserBeanManager(ITopicApplication mContext) {
		this.mContext = mContext;
		onUserStateChangeListenerList = new ArrayList<UserStateChangeListener>();
		onCheckPointListenerList = new ArrayList<CheckPointListener>();
		onCheckMoneyListenerList = new ArrayList<CheckMoneyListener>();
	}

	public UserBean getInstance() {
		return instanceUser;
	}

	public String getUserId() {
		return instanceUser == null ? "" : instanceUser.getUserid();
	}

	public String getMobile() {
		return instanceUser == null ? "" : instanceUser.getMobile();
	}

	/**
	 * 只运行初始化app调用
	 */
	public void checkUserInfo() {
		SharedPreferences pref = mContext.getSharedPreferences("USER",
				Activity.MODE_PRIVATE);
		instanceUser = JsonUtil.getObject(pref.getString("USERJSON", ""),
				UserBean.class);
		if (instanceUser == null) {
			storeMineUserID(mContext, null);
		} else {
			// 展开数据库
			storeMineUserID(mContext, instanceUser.getUserid());
			DBReq.getInstence(mContext);
		}
	}

	/**
	 * 保存新的用户json并发出观察者通知
	 * 目前只有登录 和 注册 之后会调用，并且是在环信登录之前
	 */
	public void storeUserInfoAndNotity(UserBean ub) {
		//保存用户json到SharedPreferences，和全局变量
		storeUserInfo(ub);
		//刷新全局变量：当前用户付款过的红包动态
		mContext.getHomeManager().resetPaidTopicPhotoArray();
		//由于需要在broadcast里接受穿透消息，然后存入当前userid的数据库，但是广播里不确定能application在存活，所以这里存下静态的userid
		storeMineUserID(mContext, instanceUser.getUserid());
		//发出登录广播
		notityUserLogin(ub);
	}

	/**
	 * 仅仅保存新的用户json
	 */
	public void storeUserInfo(UserBean ub) {
		instanceUser = ub;
		SharedPreferences pref = mContext.getSharedPreferences("USER",
				Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString("USERJSON", JsonUtil.getJson(ub));
		editor.commit();
	}

	/**
	 * 当前用户信息改变（性别，年龄，昵称 等）包括用户发的动态数，粉丝数，关注数
	 * @param ub
	 */
	public void notityUserInfoChanged(UserBean ub) {
		for (UserStateChangeListener onUserStateChangeListener : onUserStateChangeListenerList) {
			onUserStateChangeListener.onUserInfoChanged(ub);
		}
	}

	/**
	 * 当前用户登录 发出通知
	 * @param ub
	 */
	public void notityUserLogin(UserBean ub) {
		for (UserStateChangeListener onUserStateChangeListener : onUserStateChangeListenerList) {
			onUserStateChangeListener.onUserLogin(ub);
		}
	}
	
	/**
	 * 当前用户退出 发出通知
	 * 发出这个通知的时候，userBean == null
	 */
	public void notityUserLogout() {
		for (UserStateChangeListener onUserStateChangeListener : onUserStateChangeListenerList) {
			onUserStateChangeListener.onUserLogout();
		}
	}
	
	public void clean() {
		instanceUser = null;
		mContext.getHuanXinManager().logout(null);
		mContext.getHomeManager().resetPaidTopicPhotoArray();
		SharedPreferences pref = mContext.getSharedPreferences("USER",Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.clear();
		editor.commit();
		storeMineUserID(mContext, null);
		DBReq.getInstence(mContext).close();
		notityUserLogout();
	}

	private static void storeMineUserID(Context mContext, String userID) {
		SharedPreferences pref = mContext.getSharedPreferences("USERID",
				Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString("USERID", userID);
		editor.commit();
	}

	public static String getMineUserID(Context mContext) {
		SharedPreferences pref = mContext.getSharedPreferences("USERID",
				Activity.MODE_PRIVATE);
		return pref.getString("USERID", null);
	}

	/**
	 * 修改个人资料
	 * 
	 * @param editEvent
	 * @param newContent
	 */
	public void startEditInfoRun(String editEvent, String newContent,
			EditInfoListener onEditListener) {
		this.onEditListener = onEditListener;
		new Thread(new EditInfoRun(editEvent, newContent)).start();
	}

	private class EditInfoRun implements Runnable {

		private String editEvent;
		private String newContent;

		public EditInfoRun(String editEvent, String newContent) {
			this.editEvent = editEvent;
			this.newContent = newContent;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			ModifyResponse response = null;
			try {
				HashMap<String, String> data = new HashMap<String, String>();
				data.put("userid", instanceUser.getUserid());
				data.put("event", editEvent);
				data.put("value", newContent);
				response = JsonParser.getModifyResponse(HttpUtil.postMsg(
						HttpUtil.getData(data), HttpUtil.IP + "user/modify"));
				
				//看看是不是第一次上传头像
				if ("avatar".equals(editEvent) && MyUserBeanManager.MISSION_ENABLE && instanceUser.getAvatar().isEmpty()) {
					startPointActionRun(MissionActivity.MISSION_AVATAR_ID);
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (response == null) {
				response = new ModifyResponse();
				response.setMessage("网络访问异常");
			}
		
			handler.sendMessage(handler.obtainMessage(
					BaseActivity.NETWORK_SUCCESS_DATA_RIGHT, response));
		}
	}

	public void addOnUserStateChangeListener(
			UserStateChangeListener onUserStateChangeListener) {
		if (onUserStateChangeListener != null
				&& !onUserStateChangeListenerList
						.contains(onUserStateChangeListener)) {
			onUserStateChangeListenerList.add(onUserStateChangeListener);
		}
	}

	public void removeUserStateChangeListener(
			UserStateChangeListener onUserStateChangeListener) {
		if (onUserStateChangeListener != null
				&& onUserStateChangeListenerList
						.contains(onUserStateChangeListener)) {
			onUserStateChangeListenerList.remove(onUserStateChangeListener);
		}
	}

	public EditInfoListener getOnEditListener() {
		return onEditListener;
	}

	public void setOnEditListener(EditInfoListener onEditListener) {
		this.onEditListener = onEditListener;
	}

	//用户状态改变，观察者
	public interface UserStateChangeListener {
		//当前用户信息改变（性别，年龄，昵称 等）包括用户发的动态数，粉丝数，关注数
		public void onUserInfoChanged(UserBean ub);
		//用户输入密码登录（或者注册后自动登录）
		public void onUserLogin(UserBean ub);
		//用户登出
		public void onUserLogout();
	}

	//用户修改自己的信息，网络监听
	public interface EditInfoListener {
		public void onEditFail(String message);

		public void onEditSuccess();
	}


	/**
	 * 通过SharedPreferences 记录的最后一天签到时间 判断今天是不是已经签到了
	 * 所有任务里，只有每日签到（missionid = 1）需要在本地存储一下状态，别的都是网络实时获取是否已经完成
	 * 为何要这样做，是因为一进入MissionActivity界面，就要立刻通过判断没签到 没签到就直接进入这个签到界面
	 * 如果没有这个“直接进入”需求，本地是不需要存的，每次从网络获取是否今日已签到
	 */
	public boolean hadSignedToday() {
		if (instanceUser == null) {
			return false;
		}
		//获取今天的日期 “2016-06-09”
		String currentDateString = ValueUtil.getSimpleDate(new Date(System.currentTimeMillis()));
		
		//根据当前登录的账号，获取他的签到日期list<String>
		SharedPreferences pref = mContext.getSharedPreferences("MISSION_SIGN",Activity.MODE_PRIVATE);
		Set<String> signArray = pref.getStringSet(instanceUser.getUserid()+"_LASTSIGN", new HashSet<String>());

		return signArray.contains(currentDateString);
	}

	/**
	 * 更新SharedPreferences 里记录的已签到状态 ,返回一共签到了几次（天）
	 */
	public int signedToday() {
		//获取今天的日期 “2016-06-09”
		String currentDateString = ValueUtil.getSimpleDate(new Date(System.currentTimeMillis()));
		
		//根据当前登录的账号，获取他的签到日期list<String>
		SharedPreferences pref = mContext.getSharedPreferences("MISSION_SIGN",Activity.MODE_PRIVATE);
		Set<String> signArray = pref.getStringSet(instanceUser.getUserid()+"_LASTSIGN", new HashSet<String>());
		signArray.add(currentDateString);
		
		Editor editor =  pref.edit().clear();
		editor.putStringSet(instanceUser.getUserid()+"_LASTSIGN", signArray);
		editor.commit();
		
		//返回一共签到了几次（天）
		return signArray.size();
	}
	
	/**
	 * 告诉服务器 我刚才完成了某项任务（目前只有 1=每日签到 2=每日分享 101=更新头像 ）
	 * 3=赞一个 102=关注十人 这两个由服务器在对应的接口里检查判断，不单独走这里的代码
	 * 
	 * @param
	 * @param missionid
	 */
	public void startPointActionRun(String missionid){
		new Thread(new PointActionRun(missionid)).start();
	}
	
	private class PointActionRun implements Runnable{

		private String missionid;
		
		public PointActionRun(String missionid){
			this.missionid = missionid;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				HashMap<String, String> data = new HashMap<String, String>();
				data.put("userid", instanceUser.getUserid());
				data.put("missionid", missionid);
				HttpUtil.postMsg(HttpUtil.getData(data), HttpUtil.IP + "mission/action");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void addOnCheckPointListener(CheckPointListener onCheckPointListener) {
		if (onCheckPointListener != null
				&& !onCheckPointListenerList.contains(onCheckPointListener)) {
			onCheckPointListenerList.add(onCheckPointListener);
		}
	}

	public void removeCheckPointListener(CheckPointListener onCheckPointListener) {
		if (onCheckPointListener != null
				&& onCheckPointListenerList.contains(onCheckPointListener)) {
			onCheckPointListenerList.remove(onCheckPointListener);
		}
	}

	public interface CheckPointListener {
		public void onCheckPointFinish(IntResponse intResponse);
	}
	
	public void startCheckPointRun() {
		new Thread(new CheckPointRun()).start();
	}

	private class CheckPointRun implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			IntResponse response = null;
			try {
				HashMap<String, String> data = new HashMap<String, String>();
				data.put("userid", instanceUser.getUserid());
				response = JsonParser.getIntResponse(HttpUtil.getMsg(HttpUtil.IP + "mission/pointsearch?"+HttpUtil.getData(data)));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (response == null) {
				response = new IntResponse();
				response.setMessage("网络访问异常");
			}
			handler.sendMessage(handler.obtainMessage(
					BaseActivity.NETWORK_SUCCESS_PAGER_RIGHT, response));
		}
	}

	/**
	 * 获取最新的积分 start
	 */
	public void startCheckMoneyRun() {
		new Thread(new CheckMoneyRun()).start();
	}

	private class CheckMoneyRun implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			FloatResponse response = null;
			try {
				HashMap<String, String> data = new HashMap<String, String>();
				data.put("userid", instanceUser.getUserid());
				response = JsonParser.getFloatResponse(HttpUtil.getMsg(HttpUtil.IP + "money/search?"+HttpUtil.getData(data)));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (response == null) {
				response = new FloatResponse();
				response.setMessage("网络访问异常");
			}
			handler.sendMessage(handler.obtainMessage(
					BaseActivity.NETWORK_OTHER, response));
		}
	}

	public void addOnCheckMoneyListener(CheckMoneyListener onCheckMoneyListener) {
		if (onCheckMoneyListener != null
				&& !onCheckMoneyListenerList.contains(onCheckMoneyListener)) {
			onCheckMoneyListenerList.add(onCheckMoneyListener);
		}
	}

	public void removeOnCheckMoneyListener(CheckMoneyListener onCheckMoneyListener) {
		if (onCheckMoneyListener != null
				&& onCheckMoneyListenerList.contains(onCheckMoneyListener)) {
			onCheckMoneyListenerList.remove(onCheckMoneyListener);
		}
	}

	public interface CheckMoneyListener {
		public void onCheckMoneyFinish(FloatResponse intResponse);
	}
	/**
	 * 获取最新的积分 end
	 */
}
