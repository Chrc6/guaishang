package com.houwei.guaishang.activity;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.easemob.EMCallBack;
import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.easemob.EMEventListener;
import com.easemob.EMGroupChangeListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.CmdMessageBody;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactListener;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMNotifier;
import com.easemob.chat.GroupChangeListener;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.EMMessage.Type;

import com.easemob.util.EMLog;
import com.easemob.util.HanziToPinyin;
import com.easemob.util.NetUtils;
import com.houwei.guaishang.R;
import com.houwei.guaishang.easemob.DemoHelper;
import com.houwei.guaishang.easemob.EaseCommonUtils;
import com.houwei.guaishang.easemob.EaseConstant;
import com.houwei.guaishang.event.LogouSuccess;
import com.houwei.guaishang.sp.UserUtil;
import com.houwei.guaishang.tools.LogUtil;
import com.houwei.guaishang.tools.ShareSDKUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

/**
 * Root界面，在这里只处理与环信长连接相关的代码
 * 
 * @author dongjin
 * 
 */
public class MainHuanXinActivity extends BaseActivity implements
		EMEventListener {
	
	
	protected DemoHelper demoEaseHelper;
	// 账号在别处登录
	public boolean isConflict = false;
	// 账号被移除
	private boolean isCurrentAccountRemoved = false;

	/**
	 * 检查当前用户是否被删除
	 */
	public boolean getCurrentAccountRemoved() {
		return isCurrentAccountRemoved;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null
				&& savedInstanceState.getBoolean(
						Constant.ACCOUNT_REMOVED, false)) {
			// 防止被移除后，没点确定按钮然后按了home键，长期在后台又进app导致的crash
			// 三个fragment里加的判断同理
			getITopicApplication().getHuanXinManager().logout(null);
			startActivity(new Intent(this, UserLoginActivity.class));
			finish();
			return;
		} else if (savedInstanceState != null
				&& savedInstanceState.getBoolean("isConflict", false)) {
			// 防止被T后，没点确定按钮然后按了home键，长期在后台又进app导致的crash
			// 三个fragment里加的判断同理
			startActivity(new Intent(this, UserLoginActivity.class));
			finish();
			return;
		}
		demoEaseHelper = getITopicApplication().getHuanXinManager().getHxSDKHelper();
	}

	
	protected void afterContentView() {

		if (getIntent().getBooleanExtra(Constant.ACCOUNT_CONFLICT, false) && !isConflictDialogShow) {
			showConflictDialog();
		} else if (getIntent().getBooleanExtra(Constant.ACCOUNT_REMOVED, false) && !isAccountRemovedDialogShow) {
			showAccountRemovedDialog();
		}

		//2016-03-07 2.0淘汰
//		groupChangeListener = new MyGroupChangeListener();
//		// 注册群聊相关的listener
//		EMGroupManager.getInstance().addGroupChangeListener(groupChangeListener);
//		
//		// 通知sdk，UI 已经初始化完毕，注册了相应的receiver和listener, 可以接受broadcast了
//		HXSDKHelper.getInstance().notifyForRecevingEvents();
		
		// 注册群组和联系人监听
		demoEaseHelper.registerGroupAndContactListener();
		registerBroadcastReceiver();
	}


	/**
	 * 监听事件
	 */
	@Override
	public void onEvent(EMNotifierEvent event) {
		switch (event.getEvent()) {
		case EventNewMessage: // 普通消息
		{
			EMMessage message = (EMMessage) event.getData();

			// 提示新消息
			demoEaseHelper.getNotifier().onNewMsg(message);

			refreshUI();
			break;
		}

		case EventOfflineMessage: {
			refreshUI();
			break;
		}

		case EventConversationListChanged: {
			refreshUI();
			break;
		}
		case EventReadAck:
            // TODO 这里当此消息未加载到内存中时，ackMessage会为null，消息的删除会失败
		    EMMessage ackMessage = (EMMessage) event.getData();
		    EMConversation conversation = EMChatManager.getInstance().getConversation(ackMessage.getTo());
		    // 判断接收到ack的这条消息是不是阅后即焚的消息，如果是，则说明对方看过消息了，对方会销毁，这边也删除(现在只有txt iamge file三种消息支持 )
            if(ackMessage.getBooleanAttribute(EaseConstant.EASE_ATTR_READFIRE, false) 
                    && (ackMessage.getType() == Type.TXT 
                    || ackMessage.getType() == Type.VOICE 
                    || ackMessage.getType() == Type.IMAGE)){
                // 判断当前会话是不是只有一条消息，如果只有一条消息，并且这条消息也是阅后即焚类型，当对方阅读后，这边要删除，会话会被过滤掉，因此要加载上一条消息
                if(conversation.getAllMessages().size() == 1 && conversation.getLastMessage().getMsgId().equals(ackMessage.getMsgId())){
                    if (ackMessage.getChatType() == ChatType.Chat) {
                        conversation.loadMoreMsgFromDB(ackMessage.getMsgId(), 1);
                    } else {
                        conversation.loadMoreGroupMsgFromDB(ackMessage.getMsgId(), 1);
                    }
                }
                conversation.removeMessage(ackMessage.getMsgId());
            }
		    break;
		default:
			break;
		}
	}

	private void refreshUI() {
		runOnUiThread(new Runnable() {
			public void run() {

				// 刷新bottom bar消息未读数
				checkMessageUnReadCount();

			}
		});
	}

	
	private void registerBroadcastReceiver() {
        broadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.ACTION_CONTACT_CHANAGED);
        intentFilter.addAction(Constant.ACTION_GROUP_CHANAGED);
        broadcastReceiver = new BroadcastReceiver() {
            
            @Override
            public void onReceive(Context context, Intent intent) {
            	//我被邀请进入群聊、用户被T、群被解散
            	checkMessageUnReadCount();
            }
        };
        broadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }
	
	private void unregisterBroadcastReceiver(){
	    broadcastManager.unregisterReceiver(broadcastReceiver);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 注销广播接收者

		if (conflictBuilder != null) {
			conflictBuilder.create().dismiss();
			conflictBuilder = null;
		}

		unregisterBroadcastReceiver();

	}

	/**
	 * 获取未读消息数
	 * 
	 * @return
	 */
	public int getUnreadMsgCountTotal() {
		int unreadMsgCountTotal = 0;
		unreadMsgCountTotal = EMChatManager.getInstance().getUnreadMsgsCount();
		
		//减去userid为 2 （通知）的数量
		Hashtable<String, EMConversation> conversations = EMChatManager.getInstance().getAllConversations();
		for (EMConversation conversation : conversations.values()) {
			if (EaseConstant.NOTICE_USERID.equals(conversation.getUserName())) {
				unreadMsgCountTotal -= conversation.getUnreadMsgCount();
				break;
			}
		}
		return unreadMsgCountTotal;
	}

	/**
	 * 由子类（mainActivity 重写）
	 */
	protected void checkMessageUnReadCount() {}


	/**
	 * MyGroupChangeListener
	 */
	public class MyGroupChangeListener implements EMGroupChangeListener {

		@Override
		public void onInvitationReceived(String groupId, String groupName,
				String inviter, String reason) {
			//我被邀请进入群聊（直接进入）
			boolean hasGroup = false;
			for (EMGroup group : EMGroupManager.getInstance().getAllGroups()) {
				if (group.getGroupId().equals(groupId)) {
					hasGroup = true;
					break;
				}
			}
			if (!hasGroup)
				return;
		}

		@Override
		public void onInvitationAccpted(String groupId, String inviter,
				String reason) {

		}

		@Override
		public void onInvitationDeclined(String groupId, String invitee,
				String reason) {

		}

		@Override
		public void onUserRemoved(String groupId, String groupName) {

			// 提示用户被T了，demo省略此步骤
			// 刷新ui
			runOnUiThread(new Runnable() {
				public void run() {
					try {
					} catch (Exception e) {
					}
				}
			});
		}

		@Override
		public void onGroupDestroy(String groupId, String groupName) {

			// 群被解散
			// 提示用户群被解散,demo省略
			// 刷新ui
			runOnUiThread(new Runnable() {
				public void run() {
				}
			});

		}

		@Override
		public void onApplicationReceived(String groupId, String groupName,
				String applyer, String reason) {

			// 用户申请加入群聊
		}

		@Override
		public void onApplicationAccept(String groupId, String groupName,
				String accepter) {

			String st4 = getResources().getString(
					R.string.Agreed_to_your_group_chat_application);
			// 加群申请被同意
			EMMessage msg = EMMessage.createReceiveMessage(Type.TXT);
			msg.setChatType(ChatType.GroupChat);
			msg.setFrom(accepter);
			msg.setTo(groupId);
			msg.setMsgId(UUID.randomUUID().toString());
			msg.addBody(new TextMessageBody(accepter + " " + st4));
			// 保存同意消息
			EMChatManager.getInstance().saveMessage(msg);
			// 提醒新消息
			demoEaseHelper.getNotifier().viberateAndPlayTone(msg);

			runOnUiThread(new Runnable() {
				public void run() {

				}
			});
		}

		@Override
		public void onApplicationDeclined(String groupId, String groupName,
				String decliner, String reason) {
			// 加群申请被拒绝，demo未实现
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!isConflict || !isCurrentAccountRemoved) {
			checkMessageUnReadCount();
		}
		// unregister this event listener when this activity enters the
		// background
		getITopicApplication().getHuanXinManager().getHxSDKHelper().pushActivity(this);

		// register the event listener when enter the foreground
		EMChatManager.getInstance().registerEventListener(
				this,
				new EMNotifierEvent.Event[] {
						EMNotifierEvent.Event.EventNewMessage,
						EMNotifierEvent.Event.EventOfflineMessage,
						EMNotifierEvent.Event.EventConversationListChanged });
	}

	@Override
	protected void onStop() {
		EMChatManager.getInstance().unregisterEventListener(this);
		getITopicApplication().getHuanXinManager().getHxSDKHelper().popActivity(this);
		super.onStop();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean("isConflict", isConflict);
		outState.putBoolean(Constant.ACCOUNT_REMOVED, isCurrentAccountRemoved);
		super.onSaveInstanceState(outState);
	}

	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			//类似QQ微信，挂起app，而不是退出
			moveTaskToBack(false);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private android.app.AlertDialog.Builder conflictBuilder;
	private android.app.AlertDialog.Builder accountRemovedBuilder;
	private boolean isConflictDialogShow;
	private boolean isAccountRemovedDialogShow;
    private BroadcastReceiver broadcastReceiver;
    private LocalBroadcastManager broadcastManager;
	
	/**
	 * 显示帐号在别处登录dialog
	 */
	private void showConflictDialog() {
		isConflictDialogShow = true;
		getITopicApplication().getHuanXinManager().logout(null);
		String st = getResources().getString(R.string.Logoff_notification);
		if (!MainHuanXinActivity.this.isFinishing()) {
			// clear up global variables
			try {
				if (conflictBuilder == null)
					conflictBuilder = new android.app.AlertDialog.Builder(
							MainHuanXinActivity.this);
				conflictBuilder.setTitle(st);
				conflictBuilder.setMessage(R.string.connect_conflict);
				conflictBuilder.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								conflictBuilder = null;
								//清楚上个用户缓存，刷新首页和我的页
								getITopicApplication().getMyUserBeanManager().clean();
								ShareSDKUtils.removeAccount();
								UserUtil.setUserInfo(null);
								EventBus.getDefault().post(new LogouSuccess());

								finish();
								startActivity(new Intent(
										MainHuanXinActivity.this,
										UserLoginActivity.class));
							}
						});
				conflictBuilder.setCancelable(false);
				conflictBuilder.create().show();
				isConflict = true;
			} catch (Exception e) {
			}

		}

	}

	/**
	 * 帐号被移除的dialog
	 */
	private void showAccountRemovedDialog() {
		isAccountRemovedDialogShow = true;
		getITopicApplication().getHuanXinManager().logout(null);
		String st5 = getResources().getString(R.string.Remove_the_notification);
		if (!MainHuanXinActivity.this.isFinishing()) {
			// clear up global variables
			try {
				if (accountRemovedBuilder == null)
					accountRemovedBuilder = new android.app.AlertDialog.Builder(
							MainHuanXinActivity.this);
				accountRemovedBuilder.setTitle(st5);
				accountRemovedBuilder.setMessage(R.string.em_user_remove);
				accountRemovedBuilder.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								accountRemovedBuilder = null;
								finish();
								startActivity(new Intent(
										MainHuanXinActivity.this,
										UserLoginActivity.class));
							}
						});
				accountRemovedBuilder.setCancelable(false);
				accountRemovedBuilder.create().show();
				isCurrentAccountRemoved = true;
			} catch (Exception e) {
			}

		}

	}


	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (intent.getBooleanExtra(Constant.ACCOUNT_CONFLICT, false) && !isConflictDialogShow) {
			showConflictDialog();
		} else if (intent.getBooleanExtra(Constant.ACCOUNT_REMOVED, false) && !isAccountRemovedDialogShow) {
			showAccountRemovedDialog();
		}
	}

}
