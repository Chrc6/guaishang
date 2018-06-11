package com.houwei.guaishang.easemob;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.houwei.guaishang.R;
import com.houwei.guaishang.activity.AboutUsActivity;
import com.houwei.guaishang.activity.ChatBaseActivity;
import com.houwei.guaishang.activity.ChatEaseActivity;
import com.houwei.guaishang.activity.Constant;
import com.houwei.guaishang.activity.HisRootActivity;
import com.houwei.guaishang.activity.MainActivity;
import com.houwei.guaishang.activity.WelcomeActivity;
import com.houwei.guaishang.bean.AvatarBean;
import com.houwei.guaishang.easemob.EaseNotifier.EaseNotificationInfoProvider;
import com.houwei.guaishang.easemob.EaseUI.EaseEmojiconInfoProvider;
import com.houwei.guaishang.easemob.EaseUI.EaseSettingsProvider;
import com.houwei.guaishang.easemob.EaseUI.EaseUserProfileProvider;
import com.houwei.guaishang.huanxin.ChatActivity;
import com.houwei.guaishang.huanxin.ChatInfo;
import com.houwei.guaishang.manager.ITopicApplication;
import com.houwei.guaishang.tools.JsonParser;
import com.houwei.guaishang.tools.LogUtil;
import com.easemob.EMCallBack;
import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.easemob.EMEventListener;
import com.easemob.EMGroupChangeListener;
import com.easemob.EMNotifierEvent;
import com.easemob.EMValueCallBack;
import com.easemob.chat.CmdMessageBody;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.easemob.chat.EMContactListener;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.EMMessage.Type;
import com.easemob.chat.TextMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.EMLog;

public class DemoHelper {
	
	/**
     * 我们app自己定义的，用来把cmd穿透消息传递给ChatManager，让ChatManager来处理透传消息
     */
	private CmdMessageGetListener onCmdMessageGetListener;
	
    /**
     * 数据同步listener
     */
    static public interface DataSyncListener {
        /**
         * 同步完毕
         * @param success true：成功同步到数据，false失败
         */
        public void onSyncComplete(boolean success);
    }

    protected static final String TAG = "DemoHelper";
    
	private EaseUI easeUI;
	
    /**
     * EMEventListener
     */
    protected EMEventListener eventListener = null;

	
	private DemoModel demoModel = null;
	
	/**
     * HuanXin sync groups status listener
     */
    private List<DataSyncListener> syncGroupsListeners;
    /**
     * HuanXin sync contacts status listener
     */
    private List<DataSyncListener> syncContactsListeners;
    /**
     * HuanXin sync blacklist status listener
     */
    private List<DataSyncListener> syncBlackListListeners;

    private boolean isSyncingGroupsWithServer = false;
    private boolean isSyncingContactsWithServer = false;
    private boolean isSyncingBlackListWithServer = false;
    private boolean isGroupsSyncedWithServer = false;
    private boolean isContactsSyncedWithServer = false;
    private boolean isBlackListSyncedWithServer = false;
    
    private boolean alreadyNotified = false;
	
	public boolean isVoiceCalling;
    public boolean isVideoCalling;

	private String username;

    private Context appContext;


    private EMConnectionListener connectionListener;

    private LocalBroadcastManager broadcastManager;

    private boolean isGroupAndContactListenerRegisted;


//	public synchronized static DemoHelper getInstance() {
//		if (instance == null) {
//			instance = new DemoHelper();
//		}
//		return instance;
//	}

	/**
	 * init helper
	 * 
	 * @param context
	 *            application context
	 */
	public void init(Context context) {
		if (EaseUI.getInstance().init(context)) {
		    appContext = context;
		    
		    //if your app is supposed to user Google Push, please set project number
//            String projectNumber = "562451699741";
            //不使用GCM推送的注释掉这行
//            EMChatManager.getInstance().setGCMProjectNumber(projectNumber);
            //在小米手机上当app被kill时使用小米推送进行消息提示，同GCM一样不是必须的
//            EMChatManager.getInstance().setMipushConfig("2882303761517370134", "5131737040134");
		    
		    //设为调试模式，打成正式包时，最好设为false，以免消耗额外的资源
		    EMChat.getInstance().setDebugMode(false);
		    //get easeui instance
		    easeUI = EaseUI.getInstance();
		    //调用easeui的api设置providers
		    setEaseUIProviders();
		    demoModel = new DemoModel(context);
		    //设置chat options
		    setChatoptions();
			//初始化PreferenceManager
			PreferenceManager.init(context);
			
			//设置全局监听
			setGlobalListeners();
			broadcastManager = LocalBroadcastManager.getInstance(appContext);
	        
		}
	}

	private void setChatoptions(){
	    //easeui库默认设置了一些options，可以覆盖
	    EMChatOptions options = EMChatManager.getInstance().getChatOptions();
	    options.allowChatroomOwnerLeave(getModel().isChatroomOwnerLeaveAllowed());  
	}

    protected void setEaseUIProviders() {
        //需要easeui库显示用户头像和昵称设置此provider
        
        //不设置，则使用easeui默认的
        easeUI.setSettingsProvider(new EaseSettingsProvider() {
            
            @Override
            public boolean isSpeakerOpened() {
                return demoModel.getSettingMsgSpeaker();
            }
            
            @Override
            public boolean isMsgVibrateAllowed(EMMessage message) {
                return demoModel.getSettingMsgVibrate();
            }
            
            @Override
            public boolean isMsgSoundAllowed(EMMessage message) {
                return demoModel.getSettingMsgSound();
            }
            
            @Override
            public boolean isMsgNotifyAllowed(EMMessage message) {
                if(message == null){
                    return demoModel.getSettingMsgNotification();
                }
                if(!demoModel.getSettingMsgNotification()){
                    return false;
                }else{
                    //如果允许新消息提示
                    //屏蔽的用户和群组不提示用户，dq 2016-07-19
                    String chatUsename = null;
                    Set<String> notNotifyIds = null;
                    // 获取设置的不提示新消息的用户或者群组ids
                    if (message.getChatType() == ChatType.Chat) {
//                        chatUsename = message.getFrom();
//                        notNotifyIds = demoModel.getDisabledIds();
                    } else {
                        chatUsename = message.getTo();
                        notNotifyIds = demoModel.getDisabledGroups();
                    }

                    if (notNotifyIds == null || !notNotifyIds.contains(chatUsename)) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        });
        
        //DQ 2016-03-09
        //设置表情provider，环信官方这里有设置匿名内部类，他是为了聊天气泡里关联出表情
        //我们iTopic采用我们自己的一套方式，这里不用他的
        easeUI.setEmojiconInfoProvider(null);
        
        //不设置，则使用easeui默认的
        easeUI.getNotifier().setNotificationInfoProvider(new EaseNotificationInfoProvider() {
            
            @Override
            public String getTitle(EMMessage message) {
              //修改标题,这里使用默认
                return null;
            }
            
            @Override
            public int getSmallIcon(EMMessage message) {
              //设置小图标，这里为默认
                return 0;
            }
            
            @Override
            public String getDisplayedText(EMMessage message) {
                // 设置状态栏的消息提示，可以根据message的类型做相应提示
                String ticker = EaseCommonUtils.getMessageDigest(message, appContext);
                if(message.getType() == Type.TXT){
                    ticker = ticker.replaceAll("\\[.{2,3}\\]", "[表情]");
                }
                return message.getFrom() + ": " + ticker;
            }
            
            @Override
            public String getLatestText(EMMessage message, int fromUsersNum, int messageNum) {
                return null;
                // return fromUsersNum + "个基友，发来了" + messageNum + "条消息";
            }
            
            @Override
            public Intent getLaunchIntent(EMMessage message) {
                //设置点击通知栏跳转事件 DQDEBUG
            	 Intent intent = new Intent(appContext, ChatActivity.class);
            	
                //有电话时优先跳转到通话页面
                if(isVideoCalling){
                }else if(isVoiceCalling){
                }else{
                    ChatType chatType = message.getChatType();
                    if (chatType == ChatType.Chat) {
                        // 单聊信息
                        
                    	if (message.getBooleanAttribute("toMain", false)) {
                    		intent.setClass(appContext, MainActivity.class);
						}
                    	
                    	String hisRealName = message.getStringAttribute(
								HisRootActivity.SENDER_NAME_KEY, "");
                    	AvatarBean hisAvatarBean = JsonParser.getAvatarBean(message
								.getStringAttribute(HisRootActivity.SENDER_AVATAR_KEY, ""));
                    	if (hisAvatarBean == null) {
                    		hisAvatarBean = new AvatarBean();
                		}

                        ChatInfo chatInfo = new ChatInfo();
                        chatInfo.setMobile("");
                        chatInfo.setHisUserID(message.getFrom());
                        chatInfo.setHisRealName(hisRealName);
                        chatInfo.setChatType(EaseConstant.CHATTYPE_SINGLE);
                        chatInfo.setHeadImageBean(hisAvatarBean);
                        intent.putExtra(ChatActivity.Chat_info,chatInfo);
                    } else { // 群聊信息
                        // message.getTo()为群聊id
                    	String hisRealName = message.getStringAttribute(
								HisRootActivity.RECEIVER_NAME_KEY, "");

                        ChatInfo chatInfo = new ChatInfo();
                        chatInfo.setMobile("");
                        chatInfo.setHisUserID(message.getTo());
                        chatInfo.setHisRealName(hisRealName);
                        chatInfo.setChatType(chatType == ChatType.GroupChat? Constant.CHATTYPE_GROUP:Constant.CHATTYPE_CHATROOM);
                        chatInfo.setHeadImageBean(new AvatarBean());
                        intent.putExtra(ChatActivity.Chat_info,chatInfo);
                    }
                }
                return intent;
            }
        });
    }
    
    /**
     * 设置全局事件监听
     */
    protected void setGlobalListeners(){
        syncGroupsListeners = new ArrayList<DataSyncListener>();
        syncContactsListeners = new ArrayList<DataSyncListener>();
        syncBlackListListeners = new ArrayList<DataSyncListener>();
        
        isGroupsSyncedWithServer = demoModel.isGroupsSynced();
        isContactsSyncedWithServer = demoModel.isContactSynced();
        isBlackListSyncedWithServer = demoModel.isBacklistSynced();
        
        // create the global connection listener
        connectionListener = new EMConnectionListener() {
            @Override
            public void onDisconnected(int error) {
                if (error == EMError.USER_REMOVED) {
                    onCurrentAccountRemoved();
                }else if (error == EMError.CONNECTION_CONFLICT) {
                    onConnectionConflict();
                }
            }

            @Override
            public void onConnected() {
                
                // in case group and contact were already synced, we supposed to notify sdk we are ready to receive the events
                if(isGroupsSyncedWithServer && isContactsSyncedWithServer){
                    new Thread(){
                        @Override
                        public void run(){
                            notifyForRecevingEvents();
                        }
                    }.start();
                }else{
                    if(!isGroupsSyncedWithServer){
                        asyncFetchGroupsFromServer(null);
                    }
                    
                    if(!isContactsSyncedWithServer){
                        asyncFetchContactsFromServer(null);
                    }
                    
                    if(!isBlackListSyncedWithServer){
                        asyncFetchBlackListFromServer(null);
                    }
                }
                // 当连接到服务器之后，这里开始检查是否有没有发送的ack回执消息，
                EaseACKUtil.getInstance(appContext).checkACKData();
            }
        };
        
        //注册连接监听
        EMChatManager.getInstance().addConnectionListener(connectionListener);       
        //注册群组和联系人监听
        registerGroupAndContactListener();
        //注册消息事件监听
        registerEventListener();
        
    }
    
    
    /**
     * 注册群组和联系人监听，由于logout的时候会被sdk清除掉，再次登录的时候需要再注册一下
     */
    public void registerGroupAndContactListener(){
        if(!isGroupAndContactListenerRegisted){
            //注册群组变动监听
            EMGroupManager.getInstance().addGroupChangeListener(new MyGroupChangeListener());
            isGroupAndContactListenerRegisted = true;
        }
        
    }
    
    /**
     * 群组变动监听
     */
    class MyGroupChangeListener implements EMGroupChangeListener {

        @Override
        public void onInvitationReceived(String groupId, String groupName, String inviter, String reason) {
            
            boolean hasGroup = false;
            for (EMGroup group : EMGroupManager.getInstance().getAllGroups()) {
                if (group.getGroupId().equals(groupId)) {
                    hasGroup = true;
                    break;
                }
            }
            if (!hasGroup)
                return;

        	//我被邀请进入群聊（直接进入）这里的代码是发出一条 “XXX邀请加入群” 的
//            String st3 = appContext.getString(R.string.Invite_you_to_join_a_group_chat);
//            EMMessage msg = EMMessage.createReceiveMessage(Type.TXT);
//            msg.setChatType(ChatType.GroupChat);
//            msg.setFrom(inviter);
//            msg.setTo(groupId);
//            msg.setMsgId(UUID.randomUUID().toString());
//            msg.addBody(new TextMessageBody(inviter + " " +st3));
//            // 保存邀请消息
//            EMChatManager.getInstance().saveMessage(msg);
//            // 提醒新消息
//            getNotifier().viberateAndPlayTone(msg);
            //发送local广播
            broadcastManager.sendBroadcast(new Intent(Constant.ACTION_GROUP_CHANAGED));
        }

        @Override
        public void onInvitationAccpted(String groupId, String inviter, String reason) {
        }
        @Override
        public void onInvitationDeclined(String groupId, String invitee, String reason) {
        }

        @Override
        public void onUserRemoved(String groupId, String groupName) {
            //TODO 提示用户被T了，demo省略此步骤
            broadcastManager.sendBroadcast(new Intent(Constant.ACTION_GROUP_CHANAGED));
        }

        @Override
        public void onGroupDestroy(String groupId, String groupName) {
            // 群被解散
            //TODO 提示用户群被解散,demo省略
            broadcastManager.sendBroadcast(new Intent(Constant.ACTION_GROUP_CHANAGED));
        }

        @Override
        public void onApplicationReceived(String groupId, String groupName, String applyer, String reason) {
            // 用户申请加入群聊
        }

        @Override
        public void onApplicationAccept(String groupId, String groupName, String accepter) {
        	 // 加群申请被同意
            broadcastManager.sendBroadcast(new Intent(Constant.ACTION_GROUP_CHANAGED));
        }

        @Override
        public void onApplicationDeclined(String groupId, String groupName, String decliner, String reason) {
            // 加群申请被拒绝，demo未实现
        }
    }
    
    
    
    /**
     * 账号在别的设备登录
     */
    protected void onConnectionConflict(){
        Intent intent = new Intent(appContext, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constant.ACCOUNT_CONFLICT, true);
        appContext.startActivity(intent);
    }
    
    /**
     * 账号被移除
     */
    protected void onCurrentAccountRemoved(){
        Intent intent = new Intent(appContext, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constant.ACCOUNT_REMOVED, true);
        appContext.startActivity(intent);
    }
	
	 /**
     * 全局事件监听
     * 因为可能会有UI页面先处理到这个消息，所以一般如果UI页面已经处理，这里就不需要再次处理
     * activityList.size() <= 0 意味着所有页面都已经在后台运行，或者已经离开Activity Stack
     */
    protected void registerEventListener() {
        eventListener = new EMEventListener() {
            
            @Override
            public void onEvent(EMNotifierEvent event) {
                EMMessage message = null;
                if(event.getData() instanceof EMMessage){
                    message = (EMMessage)event.getData();
                    EMLog.d(TAG, "receive the event : " + event.getEvent() + ",id : " + message.getMsgId());
                }
                
                switch (event.getEvent()) {
                case EventNewMessage:
                    //应用在后台，不需要刷新UI,通知栏提示新消息
                    if(!easeUI.hasForegroundActivies()){
                        getNotifier().onNewMsg(message);
                    }
                    break;
                case EventOfflineMessage:
                    if(!easeUI.hasForegroundActivies()){
                        EMLog.d(TAG, "received offline messages");
                        List<EMMessage> messages = (List<EMMessage>) event.getData();
                        getNotifier().onNewMesg(messages);
                    }
                    break;
                // below is just giving a example to show a cmd toast, the app should not follow this
                // so be careful of this
                case EventNewCMDMessage:
                { 
                    //获取消息body
                    CmdMessageBody cmdMsgBody = (CmdMessageBody) message.getBody();
                    final String action = cmdMsgBody.action;//获取自定义action
                    
                    if(onCmdMessageGetListener!=null){
                    	onCmdMessageGetListener.onCmdMessageGet(action);
                    }
                    break;
                }
                case EventDeliveryAck:
                    message.setDelivered(true);
                    break;
                case EventReadAck:
                	 // TODO 这里当此消息未加载到内存中时，ackMessage会为null，消息的删除会失败
                    message.setAcked(true);
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
                // add other events in case you are interested in
                default:
                    break;
                }
                
            }
        };
        
        EMChatManager.getInstance().registerEventListener(eventListener);
    }

	/**
	 * 是否登录成功过
	 * 
	 * @return
	 */
	public boolean isLoggedIn() {
		return EMChat.getInstance().isLoggedIn();
	}

	/**
	 * 退出登录
	 * 
	 * @param unbindDeviceToken
	 *            是否解绑设备token(使用GCM才有)
	 * @param callback
	 *            callback
	 */
	public void logout(boolean unbindDeviceToken, final EMCallBack callback) {
		endCall();
		EMChatManager.getInstance().logout(unbindDeviceToken, new EMCallBack() {

			@Override
			public void onSuccess() {
			    reset();
				if (callback != null) {
					callback.onSuccess();
				}

			}

			@Override
			public void onProgress(int progress, String status) {
				if (callback != null) {
					callback.onProgress(progress, status);
				}
			}

			@Override
			public void onError(int code, String error) {
				if (callback != null) {
					callback.onError(code, error);
				}
			}
		});
	}
	
	/**
	 * 获取消息通知类
	 * @return
	 */
	public EaseNotifier getNotifier(){
	    return easeUI.getNotifier();
	}
	
	public DemoModel getModel(){
        return (DemoModel) demoModel;
    }
	

	/**
     * 设置当前用户的环信id
     * @param username
     */
    public void setCurrentUserName(String username){
    	this.username = username;
    	demoModel.setCurrentUserName(username);
    }
    
    /**
     * 获取当前用户的环信id
     */
    public String getCurrentUsernName(){
    	if(username == null){
    		username = demoModel.getCurrentUsernName();
    	}
    	return username;
    }

	void endCall() {
		try {
			EMChatManager.getInstance().endCall();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	  public void addSyncGroupListener(DataSyncListener listener) {
	        if (listener == null) {
	            return;
	        }
	        if (!syncGroupsListeners.contains(listener)) {
	            syncGroupsListeners.add(listener);
	        }
	    }

	    public void removeSyncGroupListener(DataSyncListener listener) {
	        if (listener == null) {
	            return;
	        }
	        if (syncGroupsListeners.contains(listener)) {
	            syncGroupsListeners.remove(listener);
	        }
	    }

	    public void addSyncContactListener(DataSyncListener listener) {
	        if (listener == null) {
	            return;
	        }
	        if (!syncContactsListeners.contains(listener)) {
	            syncContactsListeners.add(listener);
	        }
	    }

	    public void removeSyncContactListener(DataSyncListener listener) {
	        if (listener == null) {
	            return;
	        }
	        if (syncContactsListeners.contains(listener)) {
	            syncContactsListeners.remove(listener);
	        }
	    }

	    public void addSyncBlackListListener(DataSyncListener listener) {
	        if (listener == null) {
	            return;
	        }
	        if (!syncBlackListListeners.contains(listener)) {
	            syncBlackListListeners.add(listener);
	        }
	    }

	    public void removeSyncBlackListListener(DataSyncListener listener) {
	        if (listener == null) {
	            return;
	        }
	        if (syncBlackListListeners.contains(listener)) {
	            syncBlackListListeners.remove(listener);
	        }
	    }
	
	/**
    * 同步操作，从服务器获取群组列表
    * 该方法会记录更新状态，可以通过isSyncingGroupsFromServer获取是否正在更新
    * 和isGroupsSyncedWithServer获取是否更新已经完成
    * @throws EaseMobException
    */
   public synchronized void asyncFetchGroupsFromServer(final EMCallBack callback){
       if(isSyncingGroupsWithServer){
           return;
       }
       
       isSyncingGroupsWithServer = true;
       
       new Thread(){
           @Override
           public void run(){
               try {
                   EMGroupManager.getInstance().getGroupsFromServer();
                   
                   // in case that logout already before server returns, we should return immediately
                   if(!EMChat.getInstance().isLoggedIn()){
                       return;
                   }
                   
                   demoModel.setGroupsSynced(true);
                   
                   isGroupsSyncedWithServer = true;
                   isSyncingGroupsWithServer = false;
                   
                   //通知listener同步群组完毕
                   noitifyGroupSyncListeners(true);
                   if(isContactsSyncedWithServer()){
                       notifyForRecevingEvents();
                   }
                   if(callback != null){
                       callback.onSuccess();
                   }
               } catch (EaseMobException e) {
                   demoModel.setGroupsSynced(false);
                   isGroupsSyncedWithServer = false;
                   isSyncingGroupsWithServer = false;
                   noitifyGroupSyncListeners(false);
                   if(callback != null){
                       callback.onError(e.getErrorCode(), e.toString());
                   }
               }
           
           }
       }.start();
   }

   public void noitifyGroupSyncListeners(boolean success){
       for (DataSyncListener listener : syncGroupsListeners) {
           listener.onSyncComplete(success);
       }
   }
   
   public void asyncFetchContactsFromServer(final EMValueCallBack<List<String>> callback){
       if(isSyncingContactsWithServer){
           return;
       }
       
       isSyncingContactsWithServer = true;
       
       new Thread(){
           @Override
           public void run(){
               List<String> usernames = null;
               try {
                   usernames = EMContactManager.getInstance().getContactUserNames();
                   // in case that logout already before server returns, we should return immediately
                   if(!EMChat.getInstance().isLoggedIn()){
                       return;
                   }
                  
                   Map<String, EaseUser> userlist = new HashMap<String, EaseUser>();
                   for (String username : usernames) {
                       EaseUser user = new EaseUser(username);
                       EaseCommonUtils.setUserInitialLetter(user);
                       userlist.put(username, user);
                   }
                   List<EaseUser> users = new ArrayList<EaseUser>(userlist.values());

                   demoModel.setContactSynced(true);
                   EMLog.d(TAG, "set contact syn status to true");
                   
                   isContactsSyncedWithServer = true;
                   isSyncingContactsWithServer = false;
                   
                   //通知listeners联系人同步完毕
                   notifyContactsSyncListener(true);
                   if(isGroupsSyncedWithServer()){
                       notifyForRecevingEvents();
                   }
                   
                   
                   if(callback != null){
                       callback.onSuccess(usernames);
                   }
               } catch (EaseMobException e) {
                   demoModel.setContactSynced(false);
                   isContactsSyncedWithServer = false;
                   isSyncingContactsWithServer = false;
                   noitifyGroupSyncListeners(false);
                   e.printStackTrace();
                   if(callback != null){
                       callback.onError(e.getErrorCode(), e.toString());
                   }
               }
               
           }
       }.start();
   }

   public void notifyContactsSyncListener(boolean success){
       for (DataSyncListener listener : syncContactsListeners) {
           listener.onSyncComplete(success);
       }
   }
   
   public void asyncFetchBlackListFromServer(final EMValueCallBack<List<String>> callback){
       
       if(isSyncingBlackListWithServer){
           return;
       }
       
       isSyncingBlackListWithServer = true;
       
       new Thread(){
           @Override
           public void run(){
               try {
                   List<String> usernames = EMContactManager.getInstance().getBlackListUsernamesFromServer();
                   
                   // in case that logout already before server returns, we should return immediately
                   if(!EMChat.getInstance().isLoggedIn()){
                       return;
                   }
                   
                   demoModel.setBlacklistSynced(true);
                   
                   isBlackListSyncedWithServer = true;
                   isSyncingBlackListWithServer = false;
                   
                   EMContactManager.getInstance().saveBlackList(usernames);
                   notifyBlackListSyncListener(true);
                   if(callback != null){
                       callback.onSuccess(usernames);
                   }
               } catch (EaseMobException e) {
                   demoModel.setBlacklistSynced(false);
                   
                   isBlackListSyncedWithServer = false;
                   isSyncingBlackListWithServer = true;
                   e.printStackTrace();
                   
                   if(callback != null){
                       callback.onError(e.getErrorCode(), e.toString());
                   }
               }
               
           }
       }.start();
   }
	
	public void notifyBlackListSyncListener(boolean success){
        for (DataSyncListener listener : syncBlackListListeners) {
            listener.onSyncComplete(success);
        }
    }
    
    public boolean isSyncingGroupsWithServer() {
        return isSyncingGroupsWithServer;
    }

    public boolean isSyncingContactsWithServer() {
        return isSyncingContactsWithServer;
    }

    public boolean isSyncingBlackListWithServer() {
        return isSyncingBlackListWithServer;
    }
    
    public boolean isGroupsSyncedWithServer() {
        return isGroupsSyncedWithServer;
    }

    public boolean isContactsSyncedWithServer() {
        return isContactsSyncedWithServer;
    }

    public boolean isBlackListSyncedWithServer() {
        return isBlackListSyncedWithServer;
    }
	
	public synchronized void notifyForRecevingEvents(){
        if(alreadyNotified){
            return;
        }
        
        // 通知sdk，UI 已经初始化完毕，注册了相应的receiver和listener, 可以接受broadcast了
        EMChat.getInstance().setAppInited();
        alreadyNotified = true;
    }
	
    synchronized void reset(){
        isSyncingGroupsWithServer = false;
        isSyncingContactsWithServer = false;
        isSyncingBlackListWithServer = false;
        
        demoModel.setGroupsSynced(false);
        demoModel.setContactSynced(false);
        demoModel.setBlacklistSynced(false);
        
        isGroupsSyncedWithServer = false;
        isContactsSyncedWithServer = false;
        isBlackListSyncedWithServer = false;
        
        alreadyNotified = false;
        isGroupAndContactListenerRegisted = false;
        
    }

    public void pushActivity(Activity activity) {
        easeUI.pushActivity(activity);
    }

    public void popActivity(Activity activity) {
        easeUI.popActivity(activity);
    }

	/**
     * 我们app自己定义的，用来把cmd穿透消息传递给ChatManager，让ChatManager来处理透传消息
     */
	public void setOnCmdMessageGetListener(
			CmdMessageGetListener onTmpNewMessageGetListener) {
		onCmdMessageGetListener = onTmpNewMessageGetListener;
	}
    
	public interface CmdMessageGetListener {
		public void onCmdMessageGet(String cmdAction);

	}
}
