/*
package com.houwei.guaishang.activity.newui;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.Toast;


import com.easemob.EMChatRoomChangeListener;
import com.easemob.EMEventListener;
import com.easemob.EMValueCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatRoom;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMMessage;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.EMLog;
import com.easemob.util.PathUtil;
import com.houwei.guaishang.R;
import com.houwei.guaishang.activity.BaiduMapActivity;
import com.houwei.guaishang.activity.ChatBaseActivity;
import com.houwei.guaishang.activity.HisRootActivity;
import com.houwei.guaishang.bean.AvatarBean;
import com.houwei.guaishang.bean.UserBean;
import com.houwei.guaishang.easemob.EaseChatExtendMenu;
import com.houwei.guaishang.easemob.EaseChatInputMenu;
import com.houwei.guaishang.easemob.EaseChatMessageList;
import com.houwei.guaishang.easemob.EaseCommonUtils;
import com.houwei.guaishang.easemob.EaseConstant;
import com.houwei.guaishang.easemob.EaseCustomChatRowProvider;
import com.houwei.guaishang.easemob.EaseEmojicon;
import com.houwei.guaishang.easemob.EaseUI;
import com.houwei.guaishang.easemob.EaseUser;
import com.houwei.guaishang.easemob.EaseUserUtils;
import com.houwei.guaishang.easemob.EaseVoiceRecorderView;
import com.houwei.guaishang.layout.SureOrCancelDialog;
import com.houwei.guaishang.manager.ITopicApplication;
import com.houwei.guaishang.tools.JsonUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.functions.Consumer;

*/
/**
 * you can new an EaseChatFragment to use or you can inherit it to expand.
 * You need call setArguments to pass chatType and userId 
 * <br/>
 * <br/>
 * you can see ChatActivity in demo for your reference
 *
 *//*

public class EaseChatFragment extends EaseBaseFragment implements EMEventListener {
    protected static final String TAG = "EaseChatFragment";
    protected static final int REQUEST_CODE_MAP = 1;
    protected static final int REQUEST_CODE_CAMERA = 2;
    protected static final int REQUEST_CODE_LOCAL = 3;
    */
/**
     * params to fragment
     *//*

    protected Bundle fragmentArgs;
    protected int chatType;
    protected String toChatUsername;
    protected EaseChatMessageList messageList;
    protected EaseChatInputMenu inputMenu;
    protected EMConversation conversation;
    protected InputMethodManager inputManager;
    protected ClipboardManager clipboard;
    protected Handler handler = new Handler();
    protected File cameraFile;
    protected EaseVoiceRecorderView voiceRecorderView;
    protected SwipeRefreshLayout swipeRefreshLayout;
    protected ListView listView;

    protected boolean isloading;
    protected boolean haveMoreData = true;
    protected int pagesize = 20;
    protected ChatRoomListener chatRoomListener;
    protected EMMessage contextMenuMessage;
    
    static final int ITEM_TAKE_PICTURE = 1;
    static final int ITEM_PICTURE = 2;
    static final int ITEM_LOCATION = 3;
    
//    protected int[] itemStrings = { R.string.attach_take_pic, R.string.attach_picture };
//    protected int[] itemdrawables = { R.drawable.ease_chat_takepic_selector, R.drawable.ease_chat_image_selector};
//    protected int[] itemIds = { ITEM_TAKE_PICTURE, ITEM_PICTURE };
    protected int[] itemStrings = { R.string.attach_take_pic,
            R.string.attach_picture, R.string.attach_location };
    protected int[] itemdrawables = { R.drawable.message_more_camera,
            R.drawable.message_more_pic,
            R.drawable.message_more_poi };
    protected int[] itemIds = { ITEM_TAKE_PICTURE, ITEM_PICTURE, ITEM_LOCATION };
//    protected int[] itemStrings = { R.string.attach_take_pic, R.string.attach_picture, R.string.attach_location };
//    protected int[] itemdrawables = { R.drawable.ease_chat_takepic_selector, R.drawable.ease_chat_image_selector,
//            R.drawable.ease_chat_location_selector };
//    protected int[] itemIds = { ITEM_TAKE_PICTURE, ITEM_PICTURE, ITEM_LOCATION };
    private boolean isMessageListInited;
    protected MyItemClickListener extendMenuItemClickListener;
    protected boolean isRoaming = false;
    private ExecutorService fetchQueue;
    private AvatarBean hisAvatarBean;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
//
        return inflater.inflate(R.layout.ease_fragment_chat, container, false);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, boolean roaming) {
        isRoaming = roaming;
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        return inflater.inflate(R.layout.ease_fragment_chat, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        fragmentArgs = getArguments();
        // check if single chat or group chat
        chatType = fragmentArgs.getInt(EaseConstant.EXTRA_CHATTYPE, EaseConstant.CHATTYPE_SINGLE);
        // userId you are chat with or group id
        toChatUsername = fragmentArgs.getString(EaseConstant.EXTRA_USER_ID);


        super.onActivityCreated(savedInstanceState);
    }

    */
/**
     * init view
     *//*

    protected void initView() {
        // hold to record voice
        //noinspection ConstantConditions
        voiceRecorderView = (EaseVoiceRecorderView) getView().findViewById(R.id.voice_recorder);

        // message list layout
        messageList = (EaseChatMessageList) getView().findViewById(R.id.message_list);
//        messageList.init(toChatUsername,chatType, new CustomChatRowProvider(getActivity()) );
        if(chatType != EaseConstant.CHATTYPE_SINGLE)
            messageList.setShowUserNick(true);
//        messageList.setAvatarShape(1);
        listView = messageList.getListView();

        extendMenuItemClickListener = new MyItemClickListener();
        inputMenu = (EaseChatInputMenu) getView().findViewById(R.id.input_menu);
        registerExtendMenuItem();
        // init input menu
        inputMenu.init(null);
        inputMenu.setChatInputMenuListener(new EaseChatInputMenu.ChatInputMenuListener() {

            @Override
            public void onSendMessage(String content) {
                sendTextMessage(content);
            }

            @Override
            public boolean onPressToSpeakBtnTouch(View v, MotionEvent event) {
                return voiceRecorderView.onPressToSpeakBtnTouch(v, event, new EaseVoiceRecorderView.EaseVoiceRecorderCallback() {
                    
                    @Override
                    public void onVoiceRecordComplete(String voiceFilePath, int voiceTimeLength) {
                        sendVoiceMessage(voiceFilePath, voiceTimeLength);
                    }
                });
            }

            @Override
            public void onBigExpressionClicked(EaseEmojicon emojicon) {
                sendBigExpressionMessage(emojicon.getName(), emojicon.getIdentityCode());
            }
        });

        swipeRefreshLayout = messageList.getSwipeRefreshLayout();
        swipeRefreshLayout.setColorSchemeResources(R.color.holo_blue_bright, R.color.holo_green_light,
                R.color.holo_orange_light, R.color.holo_red_light);

        inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        if (isRoaming) {
            fetchQueue = Executors.newSingleThreadExecutor();
        }
    }

    protected void setUpView() {
//        titleBar.setTitle(toChatUsername);
        if (chatType == EaseConstant.CHATTYPE_SINGLE) {
            // set title
            if(EaseUserUtils.getUserInfo(toChatUsername) != null){
                EaseUser user = EaseUserUtils.getUserInfo(toChatUsername);
                if (user != null) {
//                    titleBar.setTitle(user.getNick());
                }
            }
//            titleBar.setRightImageResource(R.drawable.ease_mm_title_remove);
        } else {
//        	titleBar.setRightImageResource(R.drawable.ease_to_group_details_normal);
            if (chatType == EaseConstant.CHATTYPE_GROUP) {
                //group chat
              */
/*  EMGroup group = EMChatManager.getInstance().groupManager().getGroup(toChatUsername);
                if (group != null)
                    titleBar.setTitle(group.getGroupName());
                // listen the event that user moved out group or group is dismissed
                groupListener = new GroupListener();
                EMChatManager.getInstance().groupManager().addGroupChangeListener(groupListener);*//*

            } else {
                chatRoomListener = new ChatRoomListener();
                EMChatManager.getInstance().addChatRoomChangeListener(chatRoomListener);
//                EMChatManager.getInstance().chatroomManager().addChatRoomChangeListener(chatRoomListener);
                onChatRoomViewCreation();
            }

        }
        if (chatType != EaseConstant.CHATTYPE_CHATROOM) {
            onConversationInit();
            onMessageListInit();
        }

     */
/*   titleBar.setLeftLayoutClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        titleBar.setRightLayoutClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (chatType == EaseConstant.CHATTYPE_SINGLE) {
                    emptyHistory();
                } else {
                    toGroupDetails();
                }
            }
        });*//*


        setRefreshLayoutListener();
        
        // show forward message if the message is not null
        String forward_msg_id = getArguments().getString("forward_msg_id");
        if (forward_msg_id != null) {
            forwardMessage(forward_msg_id);
        }

      */
/*  hisAvatarBean = (AvatarBean) getIntent().getSerializableExtra(HisRootActivity.HIS_AVATAR_KEY);
        if (hisAvatarBean == null) {
            hisAvatarBean = new AvatarBean();
        }*//*

    }
    
    */
/**
     * register extend menu, item id need > 3 if you override this method and keep exist item
     *//*

    protected void registerExtendMenuItem(){
        for(int i = 0; i < itemStrings.length; i++){
            inputMenu.registerExtendMenuItem(itemStrings[i], itemdrawables[i], itemIds[i], extendMenuItemClickListener);
        }
    }
    
    
    protected void onConversationInit(){
        EMChatManager.getInstance().getConversation(toChatUsername);
//        conversation = EMChatManager.getInstance().chatManager().getConversation(toChatUsername, EaseCommonUtils.getConversationType(chatType), true);
        conversation.markAllMessagesAsRead();
        // the number of messages loaded into conversation is getChatOptions().getNumberOfMessagesLoaded
        // you can change this number

        if (!isRoaming) {
            final List<EMMessage> msgs = conversation.getAllMessages();
            int msgCount = msgs != null ? msgs.size() : 0;
            if (msgCount < conversation.getAllMsgCount() && msgCount < pagesize) {
                String msgId = null;
                if (msgs != null && msgs.size() > 0) {
                    msgId = msgs.get(0).getMsgId();
                }
                conversation.loadMoreMsgFromDB(msgId, pagesize - msgCount);
            }
        } else {
            fetchQueue.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        EMChatManager.getInstance().fetchChatRoomFromServer(toChatUsername);
//                        EMChatManager.getInstance().chatManager().fetchHistoryMessages(
//                                toChatUsername, EaseCommonUtils.getConversationType(chatType), pagesize, "");
                        final List<EMMessage> msgs = conversation.getAllMessages();
                        int msgCount = msgs != null ? msgs.size() : 0;
                        if (msgCount < conversation.getAllMsgCount() && msgCount < pagesize) {
                            String msgId = null;
                            if (msgs != null && msgs.size() > 0) {
                                msgId = msgs.get(0).getMsgId();
                            }
                            conversation.loadMoreMsgFromDB(msgId, pagesize - msgCount);
                        }
                        messageList.refreshSelectLast();
                    } catch (EaseMobException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
    
    protected void onMessageListInit(){
        messageList.init(toChatUsername, chatType, null );
        setListItemClickListener();
        
        messageList.getListView().setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                inputMenu.hideExtendMenuContainer();
                return false;
            }
        });
        
        isMessageListInited = true;
    }
    
    protected void setListItemClickListener() {
        messageList.setItemClickListener(new EaseChatMessageList.MessageListItemClickListener() {
            
            @Override
            public void onUserAvatarClick(String username) {
                if (chatFragmentListener != null) {
                    chatFragmentListener.onAvatarClick(username);
                }
            }
            
            */
/*@Override
            public void onUserAvatarLongClick(String username) {
                if(chatFragmentHelper != null){
                    chatFragmentHelper.onAvatarLongClick(username);
                }
            }*//*

            
            @Override
            public void onResendClick(final EMMessage message) {
              */
/*  new EaseAlertDialog(getActivity(), R.string.resend, R.string.confirm_resend, null, new AlertDialogUser() {
                    @Override
                    public void onResult(boolean confirmed, Bundle bundle) {
                        if (!confirmed) {
                            return;
                        }
                        resendMessage(message);
                    }
                }, true).show();*//*

                //确认重发该信息？
                SureOrCancelDialog followDialog = new SureOrCancelDialog(
                        getActivity(), getResources().getString(R.string.confirm_resend), "确定",
                        new SureOrCancelDialog.SureButtonClick() {

                            @Override
                            public void onSureButtonClick() {
                                // TODO Auto-generated method stub
                                resendMessage(message);
                            }
                        });
                followDialog.show();
            }
            
            @Override
            public void onBubbleLongClick(EMMessage message) {
                contextMenuMessage = message;
                if(chatFragmentHelper != null){
                    chatFragmentHelper.onMessageBubbleLongClick(message);
                }
            }
            
            @Override
            public boolean onBubbleClick(EMMessage message) {
                if(chatFragmentHelper == null){
                    return false;
                }
                return chatFragmentHelper.onMessageBubbleClick(message);
            }

        });
    }

    protected void setRefreshLayoutListener() {
        swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        if (!isRoaming) {
                            loadMoreLocalMessage();
                        } else {
                            loadMoreRoamingMessages();
                        }
                    }
                }, 600);
            }
        });
    }

    private void loadMoreLocalMessage() {
        if (listView.getFirstVisiblePosition() == 0 && !isloading && haveMoreData) {
            List<EMMessage> messages;
            try {
                messages = conversation.loadMoreMsgFromDB(conversation.getAllMessages().size() == 0 ? "" : conversation.getAllMessages().get(0).getMsgId(),
                        pagesize);
            } catch (Exception e1) {
                swipeRefreshLayout.setRefreshing(false);
                return;
            }
            if (messages.size() > 0) {
                messageList.refreshSeekTo(messages.size() - 1);
                if (messages.size() != pagesize) {
                    haveMoreData = false;
                }
            } else {
                haveMoreData = false;
            }

            isloading = false;
        } else {
            Toast.makeText(getActivity(), getResources().getString(R.string.no_more_messages),
                    Toast.LENGTH_SHORT).show();
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    private void loadMoreRoamingMessages() {
        if (!haveMoreData) {
            Toast.makeText(getActivity(), getResources().getString(R.string.no_more_messages),
                    Toast.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false);
            return;
        }
        if (fetchQueue != null) {
            fetchQueue.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        List<EMMessage> messages = conversation.getAllMessages();
                        EMChatManager.getInstance().fetchChatRoomFromServer(toChatUsername);
                        */
/*EMChatManager.getInstance().chatManager().fetchHistoryMessages(
                                toChatUsername, EaseCommonUtils.getConversationType(chatType), pagesize,
                                (messages != null && messages.size() > 0) ? messages.get(0).getMsgId() : "");*//*

                    } catch (EaseMobException e) {
                        e.printStackTrace();
                    } finally {
                        Activity activity = getActivity();
                        if (activity != null) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    loadMoreLocalMessage();
                                }
                            });
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("CCC",requestCode+"---"+resultCode);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_CAMERA) { // capture new image
                if (cameraFile != null && cameraFile.exists())
                    sendImageMessage(cameraFile.getAbsolutePath());
            } else if (requestCode == REQUEST_CODE_LOCAL) { // send local image
                if (data != null) {
                    Uri selectedImage = data.getData();
                    if (selectedImage != null) {
                        sendPicByUri(selectedImage);
                    }
                }
            } else if (requestCode == REQUEST_CODE_MAP) { // location
                double latitude = data.getDoubleExtra("latitude", 0);
                double longitude = data.getDoubleExtra("longitude", 0);
                String locationAddress = data.getStringExtra("address");
                if (locationAddress != null && !locationAddress.equals("")) {
                    sendLocationMessage(latitude, longitude, locationAddress);
                } else {
                    Toast.makeText(getActivity(), R.string.unable_to_get_loaction, Toast.LENGTH_SHORT).show();
                }
                
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if(isMessageListInited)
            messageList.refresh();
        EaseUI.getInstance().pushActivity(getActivity());
        // register the event listener when enter the foreground
        EMChatManager.getInstance().registerEventListener(this);
//                .addMessageListener(this);

      */
/*  if(chatType == EaseConstant.CHATTYPE_GROUP){
            EaseAtMessageHelper.get().removeAtMeGroup(toChatUsername);
        }*//*

    }
    
    @Override
    public void onStop() {
        super.onStop();
        // unregister this event listener when this activity enters the
        // background
        EMChatManager.getInstance().unregisterEventListener(this);
//        EMChatManager.getInstance().chatManager().removeMessageListener(this);

        // remove activity from foreground activity list
        EaseUI.getInstance().popActivity(getActivity());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

      */
/*  if (groupListener != null) {
            EMChatManager.getInstance().remo
            EMChatManager.getInstance().groupManager().removeGroupChangeListener(groupListener);
        }*//*


        if (chatRoomListener != null) {
            EMChatManager.getInstance().removeChatRoomChangeListener(chatRoomListener);
//            EMChatManager.getInstance().removeChatRoomListener(chatRoomListener);
        }

        if(chatType == EaseConstant.CHATTYPE_CHATROOM){
            EMChatManager.getInstance().leaveChatRoom(toChatUsername);
//            EMChatManager.getInstance().leaveChatRoom(toChatUsername);
        }
    }

    public void onBackPressed() {
        if (inputMenu.onBackPressed()) {
            getActivity().finish();
            if(chatType == EaseConstant.CHATTYPE_GROUP){
             */
/*   EaseAtMessageHelper.get().removeAtMeGroup(toChatUsername);
                EaseAtMessageHelper.get().cleanToAtUserList();*//*

            }
            if (chatType == EaseConstant.CHATTYPE_CHATROOM) {
                EMChatManager.getInstance().leaveChatRoom(toChatUsername);
//            	EMChatManager.getInstance().leaveChatRoom(toChatUsername);
            }
        }
    }

    protected void onChatRoomViewCreation() {
        final ProgressDialog pd = ProgressDialog.show(getActivity(), "", "Joining......");
        EMChatManager.getInstance().joinChatRoom(toChatUsername, new EMValueCallBack<EMChatRoom>() {

            @Override
            public void onSuccess(final EMChatRoom value) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(getActivity().isFinishing() || !toChatUsername.equals(value.getId()))
                            return;
                        pd.dismiss();
                        EMChatRoom room = EMChatManager.getInstance().getChatRoom(toChatUsername);
                        if (room != null) {
                       */
/*     titleBar.setTitle(room.getName());
                            EMLog.d(TAG, "join room success : " + room.getName());
                        } else {
                            titleBar.setTitle(toChatUsername);*//*

                        }
                        onConversationInit();
                        onMessageListInit();
                    }
                });
            }

            @Override
            public void onError(final int error, String errorMsg) {
                // TODO Auto-generated method stub
                EMLog.d(TAG, "join room failure : " + error);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pd.dismiss();
                    }
                });
                getActivity().finish();
            }
        });
    }

    // implement methods in EMMessageListener
    @Override
    public void onMessageReceived(List<EMMessage> messages) {
        for (EMMessage message : messages) {
            String username = null;
            // group message
            if (message.getChatType() == EMMessage.ChatType.GroupChat || message.getChatType() == EMMessage.ChatType.ChatRoom) {
                username = message.getTo();
            } else {
                // single chat message
                username = message.getFrom();
            }

            // if the message is for current conversation
            
            if (username.equals(toChatUsername) || message.getTo().equals(toChatUsername) ) {
//            if (username.equals(toChatUsername) || message.getTo().equals(toChatUsername) || message.conversationId().equals(toChatUsername)) {
                messageList.refreshSelectLast();
                EaseUI.getInstance().getNotifier().viberateAndPlayTone(message);
//                EaseUI.getInstance().getNotifier().vibrateAndPlayTone(message);
                conversation.markMessageAsRead(message.getMsgId());
            } else {
                EaseUI.getInstance().getNotifier().onNewMsg(message);
            }
        }
    }

  

    @Override
    public void onMessageRead(List<EMMessage> messages) {
        if(isMessageListInited) {
            messageList.refresh();
        }
    }

    @Override
    public void onMessageDelivered(List<EMMessage> messages) {
        if(isMessageListInited) {
            messageList.refresh();
        }
    }

    @Override
    public void onMessageRecalled(List<EMMessage> messages) {
        if(isMessageListInited) {
            messageList.refresh();
        }
    }

    @Override
    public void onMessageChanged(EMMessage emMessage, Object change) {
        if(isMessageListInited) {
            messageList.refresh();
        }
    }

    */
/**
     * handle the click event for extend menu
     *
     *//*

    class MyItemClickListener implements EaseChatExtendMenu.EaseChatExtendMenuItemClickListener{

        @Override
        public void onClick(final int itemId, View view) {
            if(chatFragmentHelper != null){
                if(chatFragmentHelper.onExtendMenuItemClick(itemId, view)){
                    return;
                }
            }
            RxPermissions rxPermissions = new RxPermissions(getActivity());
            rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA).subscribe(new Consumer<Boolean>() {
                @Override
                public void accept(Boolean aBoolean) throws Exception {
                    if(aBoolean){
                        switch (itemId) {
                            case ITEM_TAKE_PICTURE:
                                selectPicFromCamera();
                                break;
                            case ITEM_PICTURE:
                                selectPicFromLocal();
                                break;
                            case ITEM_LOCATION:
                                startActivityForResult(new Intent(getActivity(), BaiduMapActivity.class), REQUEST_CODE_MAP);
                                break;
                            default:
                                break;
                        }
                    }
                }
            });
        }

    }
    
    */
/**
     * input @
     * @param username
     *//*

    protected void inputAtUsername(String username, boolean autoAddAtSymbol){
        if(EMChatManager.getInstance().getCurrentUser().equals(username) ||
                chatType != EaseConstant.CHATTYPE_GROUP){
            return;
        }
//        EaseAtMessageHelper.get().addAtUser(username);
        EaseUser user = EaseUserUtils.getUserInfo(username);
        if (user != null){
            username = user.getNick();
        }
//        if(autoAddAtSymbol)
//
////            inputMenu.insertText("@" + username + " ");
//        else
////            inputMenu.insertText(username + " ");
    }
    
    
    */
/**
     * input @
     * @param username
     *//*

    protected void inputAtUsername(String username){
        inputAtUsername(username, true);
    }
    

    //send message
    protected void sendTextMessage(String content) {
//        if(EaseAtMessageHelper.get().containsAtUsername(content)){
//            sendAtMessage(content);
//        }else{
            EMMessage message = EMMessage.createTxtSendMessage(content, toChatUsername);
            sendMessage(message);
//        }
    }
    
    */
/**
     * send @ message, only support group chat message
     * @param content
     *//*

    @SuppressWarnings("ConstantConditions")
    private void sendAtMessage(String content){
//        if(chatType != EaseConstant.CHATTYPE_GROUP){
//            EMLog.e(TAG, "only support group chat message");
//            return;
//        }
//        EMMessage message = EMMessage.createTxtSendMessage(content, toChatUsername);
//        EMGroup group = EMChatManager.getInstance().groupManager().getGroup(toChatUsername);
//        if(EMChatManager.getInstance().getCurrentUser().equals(group.getOwner()) && EaseAtMessageHelper.get().containsAtAll(content)){
//            message.setAttribute(EaseConstant.MESSAGE_ATTR_AT_MSG, EaseConstant.MESSAGE_ATTR_VALUE_AT_MSG_ALL);
//        }else {
//            message.setAttribute(EaseConstant.MESSAGE_ATTR_AT_MSG,
//                    EaseAtMessageHelper.get().atListToJsonArray(EaseAtMessageHelper.get().getAtMessageUsernames(content)));
//        }
//        sendMessage(message);
        
    }
    
    
    protected void sendBigExpressionMessage(String name, String identityCode){
        EMMessage message = EaseCommonUtils.createExpressionMessage(toChatUsername, name, identityCode);
        sendMessage(message);
    }

    protected void sendVoiceMessage(String filePath, int length) {
        EMMessage message = EMMessage.createVoiceSendMessage(filePath, length, toChatUsername);
        sendMessage(message);
    }

    protected void sendImageMessage(String imagePath) {
        EMMessage message = EMMessage.createImageSendMessage(imagePath, false, toChatUsername);
        sendMessage(message);
    }

    protected void sendLocationMessage(double latitude, double longitude, String locationAddress) {
        EMMessage message = EMMessage.createLocationSendMessage(latitude, longitude, locationAddress, toChatUsername);
        sendMessage(message);
    }

    protected void sendVideoMessage(String videoPath, String thumbPath, int videoLength) {
        EMMessage message = EMMessage.createVideoSendMessage(videoPath, thumbPath, videoLength, toChatUsername);
        sendMessage(message);
    }

    protected void sendFileMessage(String filePath) {
        EMMessage message = EMMessage.createFileSendMessage(filePath, toChatUsername);
        sendMessage(message);
    }
    
    
    protected void sendMessage(EMMessage message){
        if (message == null) {
            return;
        }
        if(chatFragmentHelper != null){
            //set extension
            chatFragmentHelper.onSetMessageAttributes(message);
        }
        if (chatType == EaseConstant.CHATTYPE_GROUP){
            message.setChatType(EMMessage.ChatType.GroupChat);
        }else if(chatType == EaseConstant.CHATTYPE_CHATROOM){
            message.setChatType(EMMessage.ChatType.ChatRoom);
        }
        //send message
        try {
            EMChatManager.getInstance().sendMessage(message);
        } catch (EaseMobException e) {
            e.printStackTrace();
        }
        //refresh ui
        if(isMessageListInited) {
            messageList.refreshSelectLast();
        }
    }
    
    
    public void resendMessage(EMMessage message){
        message.status = EMMessage.Status.CREATE;
        addAttribute(message);
        EMChatManager.getInstance().sendMessage(message, null);
        messageList.refresh();
        try {
            EMChatManager.getInstance().sendMessage(message);
        } catch (EaseMobException e) {
            e.printStackTrace();
        }
        messageList.refresh();
    }


    private void addAttribute(EMMessage message) {

        UserBean instanceUser =((ITopicApplication)getActivity().getApplication())
                .getMyUserBeanManager().getInstance();
        message.setAttribute(HisRootActivity.SENDER_ID_KEY, instanceUser.getUserid());
        message.setAttribute(HisRootActivity.SENDER_NAME_KEY, instanceUser.getName());
        message.setAttribute(HisRootActivity.SENDER_AVATAR_KEY, JsonUtil.getJson(instanceUser.getAvatar()));

        message.setAttribute(HisRootActivity.RECEIVER_ID_KEY, getActivity().getIntent().getStringExtra(HisRootActivity.HIS_ID_KEY));
        message.setAttribute(HisRootActivity.RECEIVER_NAME_KEY, getActivity().getIntent().getStringExtra(HisRootActivity.HIS_NAME_KEY));
//        message.setAttribute(HisRootActivity.RECEIVER_AVATAR_KEY, JsonUtil.getJson(hisAvatarBean));
    }


    //===================================================================================
    

    */
/**
     * send image
     * 
     * @param selectedImage
     *//*

    protected void sendPicByUri(Uri selectedImage) {
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            cursor = null;

            if (picturePath == null || picturePath.equals("null")) {
                Toast toast = Toast.makeText(getActivity(), R.string.cant_find_pictures, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }
            sendImageMessage(picturePath);
        } else {
            File file = new File(selectedImage.getPath());
            if (!file.exists()) {
                Toast toast = Toast.makeText(getActivity(), R.string.cant_find_pictures, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;

            }
            sendImageMessage(file.getAbsolutePath());
        }

    }
    
    */
/**
     * send file
     * @param uri
     *//*

    protected void sendFileByUri(Uri uri){
        String filePath = null;
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = null;

            try {
                cursor = getActivity().getContentResolver().query(uri, filePathColumn, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(column_index);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            filePath = uri.getPath();
        }
        if (filePath == null) {
            return;
        }
        File file = new File(filePath);
        if (!file.exists()) {
            Toast.makeText(getActivity(), R.string.File_does_not_exist, Toast.LENGTH_SHORT).show();
            return;
        }
        sendFileMessage(filePath);
    }

    */
/**
     * capture new image
     *//*

    protected void selectPicFromCamera() {
        if (!EaseCommonUtils.isExitsSdcard()) {
            Toast.makeText(getActivity(), R.string.sd_card_does_not_exist, Toast.LENGTH_SHORT).show();
            return;
        }

        cameraFile = new File(PathUtil.getInstance().getImagePath(), EMChatManager.getInstance().getCurrentUser()
                + System.currentTimeMillis() + ".jpg");
//        noinspection ResultOfMethodCallIgnored
//        cameraFile.getParentFile().mkdirs();
//        startActivityForResult(
//                new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile)),
//                REQUEST_CODE_CAMERA);



        ContentValues contentValues = new ContentValues(1);
        contentValues.put(MediaStore.Images.Media.DATA, cameraFile.getAbsolutePath());
        Uri uri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
        startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, uri), REQUEST_CODE_CAMERA);
    }

    */
/**
     * select local image
     *//*

    protected void selectPicFromLocal() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image*/
/*");

        } else {
            intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        Log.d("CCC","start");
        startActivityForResult(intent, REQUEST_CODE_LOCAL);
    }



    */
/**
     * clear the conversation history
     * 
     *//*

    protected void emptyHistory() {
//        String msg = getResources().getString(R.string.Whether_to_empty_all_chats);
//        new EaseAlertDialog(getActivity(),null, msg, null,new AlertDialogUser() {
//
//            @Override
//            public void onResult(boolean confirmed, Bundle bundle) {
//                if(confirmed){
//                    if (conversation != null) {
//                        conversation.clearAllMessages();
//                    }
//                    messageList.refresh();
//                    haveMoreData = true;
//                }
//            }
//        }, true).show();
    }

    */
/**
     * open group detail
     * 
     *//*

    protected void toGroupDetails() {
       */
/* if (chatType == EaseConstant.CHATTYPE_GROUP) {
            EMGroup group = EMChatManager.getInstance().groupManager().getGroup(toChatUsername);
            if (group == null) {
                Toast.makeText(getActivity(), R.string.gorup_not_found, Toast.LENGTH_SHORT).show();
                return;
            }
            if(chatFragmentHelper != null){
                chatFragmentHelper.onEnterToChatDetails();
            }
        }else if(chatType == EaseConstant.CHATTYPE_CHATROOM){
        	if(chatFragmentHelper != null){
        	    chatFragmentHelper.onEnterToChatDetails();
        	}
        }*//*

    }

    */
/**
     * hide
     *//*

    protected void hideKeyboard() {
        if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getActivity().getCurrentFocus() != null)
                inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
    
    */
/**
     * forward message
     * 
     * @param forward_msg_id
     *//*

    protected void forwardMessage(String forward_msg_id) {
        final EMMessage forward_msg = EMChatManager.getInstance().getMessage(forward_msg_id);
        EMMessage.Type type = forward_msg.getType();
        switch (type) {
        case TXT:
            if(forward_msg.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_BIG_EXPRESSION, false)){
                sendBigExpressionMessage(((EMTextMessageBody) forward_msg.getBody()).getMessage(),
                        forward_msg.getStringAttribute(EaseConstant.MESSAGE_ATTR_EXPRESSION_ID, null));
            }else{
                // get the content and send it
                String content = ((EMTextMessageBody) forward_msg.getBody()).getMessage();
                sendTextMessage(content);
            }
            break;
        case IMAGE:
            // send image
            String filePath = ((EMImageMessageBody) forward_msg.getBody()).getLocalUrl();
            if (filePath != null) {
                File file = new File(filePath);
                if (!file.exists()) {
                    // send thumb nail if original image does not exist
                    filePath = ((EMImageMessageBody) forward_msg.getBody()).thumbnailLocalPath();
                }
                sendImageMessage(filePath);
            }
            break;
        default:
            break;
        }
        
        if(forward_msg.getChatType() == ChatType.ChatRoom){
            EMChatManager.getInstance().leaveChatRoom(forward_msg.getTo());
        }
    }

    */
/**
     * listen the group event
     * 
     *//*

//    class GroupListener extends EaseGroupListener {
//
//        @Override
//        public void onUserRemoved(final String groupId, String groupName) {
//            getActivity().runOnUiThread(new Runnable() {
//
//                public void run() {
//                    if (toChatUsername.equals(groupId)) {
//                        Toast.makeText(getActivity(), R.string.you_are_group, Toast.LENGTH_LONG).show();
//                        Activity activity = getActivity();
//                        if (activity != null && !activity.isFinishing()) {
//                            activity.finish();
//                        }
//                    }
//                }
//            });
//        }

//        @Override
//        public void onGroupDestroyed(final String groupId, String groupName) {
//        	// prompt group is dismissed and finish this activity
//            getActivity().runOnUiThread(new Runnable() {
//                public void run() {
//                    if (toChatUsername.equals(groupId)) {
//                        Toast.makeText(getActivity(), R.string.the_current_group_destroyed, Toast.LENGTH_LONG).show();
//                        Activity activity = getActivity();
//                        if (activity != null && !activity.isFinishing()) {
//                            activity.finish();
//                        }
//                    }
//                }
//            });
//        }
    }

    */
/**
     * listen chat room event
     *//*

    class ChatRoomListener implements EMChatRoomChangeListener {
        private Context context;

        public ChatRoomListener(Context context) {
            this.context = context;
        }

        @Override
        public void onChatRoomDestroyed(final String roomId, final String roomName) {
            context.runOnUiThread(new Runnable() {
                public void run() {
                    if (roomId.equals(toChatUsername)) {
                        Toast.makeText(getActivity(), R.string.the_current_chat_room_destroyed, Toast.LENGTH_LONG).show();
                        Activity activity = getActivity();
                        if (activity != null && !activity.isFinishing()) {
                            activity.finish();
                        }
                    }
                }
            });
        }

        @Override
        public void onRemovedFromChatRoom(final String roomId, final String roomName, final String participant) {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    if (roomId.equals(toChatUsername)) {
                        Toast.makeText(getActivity(), R.string.quiting_the_chat_room, Toast.LENGTH_LONG).show();
                        Activity activity = getActivity();
                        if (activity != null && !activity.isFinishing()) {
                            activity.finish();
                        }
                    }
                }
            });
        }



        @Override
        public void onMemberJoined(final String roomId, final String participant) {
            if (roomId.equals(toChatUsername)) {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getActivity(), "member join:" + participant, Toast.LENGTH_LONG).show();
                    }
                });
            }
        }

        @Override
        public void onMemberExited(final String roomId, final String roomName, final String participant) {
            if (roomId.equals(toChatUsername)) {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getActivity(), "member exit:" + participant, Toast.LENGTH_LONG).show();
                    }
                });
            }
        }

        @Override
        public void onMemberKicked(String s, String s1, String s2) {

        }


    }

    protected EaseChatFragmentHelper chatFragmentHelper;
    public void setChatFragmentHelper(EaseChatFragmentHelper chatFragmentHelper){
        this.chatFragmentHelper = chatFragmentHelper;
    }




interface EaseChatFragmentHelper{
        */
/**
         * set message attribute
         *//*

        void onSetMessageAttributes(EMMessage message);

        */
/**
         * enter to chat detail
         *//*

        void onEnterToChatDetails();
        
        */
/**
         * on avatar clicked
         * @param username
         *//*

        void onAvatarClick(String username);
        
        */
/**
         * on avatar long pressed
         * @param username
         *//*

        void onAvatarLongClick(String username);
        
        */
/**
         * on message bubble clicked
         *//*

        boolean onMessageBubbleClick(EMMessage message);
        
        */
/**
         * on message bubble long pressed
         *//*

        void onMessageBubbleLongClick(EMMessage message);
        
        */
/**
         * on extend menu item clicked, return true if you want to override
         * @param view 
         * @param itemId 
         * @return
         *//*

        boolean onExtendMenuItemClick(int itemId, View view);
        
        */
/**
         * on set custom chat row provider
         * @return
         *//*

        EaseCustomChatRowProvider onSetCustomChatRowProvider();
    }

    interface EaseChatFragmentListener {
        */
/**
         * 设置消息扩展属性
         *//*

        void onSetMessageAttributes(EMMessage message);

        */
/**
         * 进入会话详情
         *//*

        void onEnterToChatDetails();

        */
/**
         * 用户头像点击事件
         *
         * @param username
         *//*

        void onAvatarClick(String username);

        */
/**
         * 消息气泡框点击事件
         *//*

        boolean onMessageBubbleClick(EMMessage message);

        */
/**
         * 消息气泡框长按事件
         *//*

        void onMessageBubbleLongClick(EMMessage message);

        */
/**
         * 扩展输入栏item点击事件,如果要覆盖EaseChatFragment已有的点击事件，return true
         *
         * @param view
         * @param itemId
         * @return
         *//*

        boolean onExtendMenuItemClick(int itemId, View view);

        */
/**
         * 设置自定义chatrow提供者
         *
         * @return
         *//*

        EaseCustomChatRowProvider onSetCustomChatRowProvider();
    }


    
}
*/
