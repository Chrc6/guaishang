package com.houwei.guaishang.manager;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.houwei.guaishang.easemob.DemoHelper;
import com.houwei.guaishang.tools.HttpUtil;



public class HuanXinManager {

	private DemoHelper hxSDKHelper;
	private ITopicApplication mApp;
	private HuanXinLoginListener onHuanXinLoginListener;
	
	public HuanXinManager(ITopicApplication mApp) {
		this.mApp = mApp;
		  /**
		   * 环信初始化SDK帮助函数
		   * 环信官方demo将hxSDKHelper做成单例，DQ觉得不合适，改成这样普通初始化了
		   */
		 hxSDKHelper = new DemoHelper();
		 hxSDKHelper.init(mApp);
	}

	/**
	 * 环信官方demo是用的单例获取DemoHelper，这里我改成了通过Application
	 * @return
	 */
	public DemoHelper getHxSDKHelper(){
		return hxSDKHelper;
	}
	
	//为了能EMChatManager.getInstance().getAllConversations()
	public void loadAllConversations(){
		if (hxSDKHelper.isLoggedIn()) {
			EMChatManager.getInstance().loadAllConversations();
		}
	}
	
	

	/**
	 * 获取当前登陆用户名
	 *
	 * @return
	 */
//	public String getUserName() {
//	    return hxSDKHelper.getHXId();
//	}

	/**
	 * 获取密码
	 *
	 * @return
	 */
//	public String getPassword() {
//		return hxSDKHelper.getPassword();
//	}

	/**
	 * 设置用户名
	 *
	 * @param user
	 */
//	public void setUserName(String username) {
//	    hxSDKHelper.setHXId(username);
//	}

	/**
	 * 设置密码 下面的实例代码 只是demo，实际的应用中需要加password 加密后存入 preference 环信sdk
	 * 内部的自动登录需要的密码，已经加密存储了
	 *
	 * @param pwd
	 */
//	public void setPassword(String pwd) {
//	    hxSDKHelper.setPassword(pwd);
//	}

	/**
	 * 退出登录,清空数据
	 */
	public void logout(final EMCallBack emCallBack) {
		// 先调用sdk logout，在清理app中自己的数据
	    hxSDKHelper.logout(false,emCallBack);
	}


	/**
	 * 网络状态
	 * @author acer
	 *
	 */
	public interface HuanXinConnectionListener{
		public void onConnected();
		public void onDisconnected(int error);
	}
	
	


	/**
	 * 本app 服务器注册用户时候，用userid当做环信id。   md5后的 （userid+秘钥）当做环信密码
     * 手机端也要按照这个规则登陆环信服务器做长连接
	 * 
	 * @param mBaseActivity
	 * @param currentUsername 要登录的当前用户的userid
	 * @param realName 要登录的当前用户的昵称
	 * @param huanXinLoginListener 登录环信长连接回调
	 */
	public void loginHuanXinService(final Activity mBaseActivity,final String currentUsername,final String realName,HuanXinLoginListener huanXinLoginListener){
		this.onHuanXinLoginListener = huanXinLoginListener;
		//登录环信服务器用的密码（userid+秘钥） md5
		final String huanXinPW = HttpUtil.getMD5WithCatch(currentUsername+HttpUtil.SIG_KEY);

		//调用sdk登陆方法登陆聊天服务器
		EMChatManager.getInstance().login(currentUsername, huanXinPW, new EMCallBack() {
			
			@Override
			public void onSuccess() {
				
				try {
					// ** 第一次登录或者之前logout后再登录，加载所有本地群和回话
					EMChatManager.getInstance().loadAllConversations();
				} catch (Exception e) {
					e.printStackTrace();
				}
				//更新当前用户的nickname 此方法的作用是在ios离线推送时能够显示用户nick
				boolean updatenick = EMChatManager.getInstance().updateCurrentUserNick(realName);
				
				mBaseActivity.runOnUiThread(new Runnable() {
					public void run() {
						if (onHuanXinLoginListener!=null) {
							onHuanXinLoginListener.onHuanXinLoginSuccess();
						}
					}
				});
				
			}
			
			@Override
			public void onProgress(int progress, String status) {
			}
			
			@Override
			public void onError(final int code, final String message) {
				//code = -1005 message = invalid user or password
				mBaseActivity.runOnUiThread(new Runnable() {
					public void run() {
						if (onHuanXinLoginListener!=null) {
						onHuanXinLoginListener.onHuanXinLoginFail(code, message);
						}
					}
				});	
			}
		});
	}
	
	/**
	 * 登录环信
	 * @author acer
	 *
	 */
	public interface HuanXinLoginListener{
		public void onHuanXinLoginSuccess();
		public void onHuanXinLoginFail(final int code, final String message);
	}
	
	public void setOnHuanXinLoginListener(HuanXinLoginListener onHuanXinLoginListener) {
		this.onHuanXinLoginListener = onHuanXinLoginListener;
	}



	/**
	 * 注册
	 * @author acer
	 *
	 */
	public interface HuanXinRegListener{
		public void onHuanXinRegSuccess();
		public void onHuanXinRegFail(final String message);
	}
	

	/**
	 * 由于php服务器无法异步向环信发出http请求以推送消息，异步只能由手机端来做
	 * 这个方法是告诉php服务器：我刚才做了什么操作。目前是：注册=1，评论=2，点赞=3，关注=4
	 * param是根据不同的操作，传不同的参数
	 */
	public void doPushAction(int actionType,Map<String, String> paramMap){
		new Thread(new DoPushActionRun(actionType,paramMap)).start();
	}
	

	private class DoPushActionRun implements Runnable {
		private int actionType;
		private Map<String, String> paramMap;
		
		public DoPushActionRun(int actionType,Map<String, String> paramMap) {
			this.actionType = actionType;
			this.paramMap = paramMap;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				HashMap<String, String> data = new HashMap<String, String>();
				data.put("userid", mApp.getMyUserBeanManager().getUserId());
				data.put("actiontype", ""+this.actionType);
				data.putAll(paramMap);
				HttpUtil.postMsg(HttpUtil.getData(data), HttpUtil.IP+ "user/doaction");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	

}
