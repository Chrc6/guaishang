package com.houwei.guaishang.huanxin;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMChatRoomChangeListener;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.EMValueCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatRoom;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.util.EMLog;
import com.easemob.util.PathUtil;
import com.houwei.guaishang.R;
import com.houwei.guaishang.activity.BaiduMapActivity;
import com.houwei.guaishang.activity.BaseFragment;
import com.houwei.guaishang.activity.ChatBaseActivity;
import com.houwei.guaishang.activity.ChatEaseActivity;
import com.houwei.guaishang.activity.Constant;
import com.houwei.guaishang.activity.GroupDetailsActivity;
import com.houwei.guaishang.activity.HisRootActivity;
import com.houwei.guaishang.bean.UserBean;
import com.houwei.guaishang.database.entity.ChatInfoData;
import com.houwei.guaishang.easemob.EaseChatExtendMenu;
import com.houwei.guaishang.easemob.EaseChatInputMenu;
import com.houwei.guaishang.easemob.EaseChatMessageList;
import com.houwei.guaishang.easemob.EaseCommonUtils;
import com.houwei.guaishang.easemob.EaseConstant;
import com.houwei.guaishang.easemob.EaseCustomChatRowProvider;
import com.houwei.guaishang.easemob.EaseEmojicon;
import com.houwei.guaishang.easemob.EaseEmojiconMenu;
import com.houwei.guaishang.easemob.EaseGroupRemoveListener;
import com.houwei.guaishang.easemob.EaseUI;
import com.houwei.guaishang.easemob.EaseVoiceRecorderView;
import com.houwei.guaishang.layout.MenuTwoButtonDialog;
import com.houwei.guaishang.layout.SureOrCancelDialog;
import com.houwei.guaishang.manager.FaceManager;
import com.houwei.guaishang.sql.ChatBindInfoDBHelper;
import com.houwei.guaishang.tools.JsonUtil;
import com.houwei.guaishang.tools.ToastUtils;
import com.luck.picture.lib.permissions.RxPermissions;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

/**
 * Created by lenovo on 2018/4/23.
 * 聊天页的fragment
 */

public class ChatFragment extends BaseFragment implements EMEventListener {
    protected static final int REQUEST_CODE_MAP = 1;
    protected static final int REQUEST_CODE_CAMERA = 2;
    protected static final int REQUEST_CODE_LOCAL = 3;
    private static final int REQUEST_CODE_GROUP_DETAIL = 21;
    // 阅后即焚id 避免和基类定义的常量可能发生的冲突，常量从11开始定义
    protected static final int ITEM_READFIRE = 15;
    protected View rootView;
    @BindView(R.id.title_right)
    ImageView ITitleRight;//右边跳转群聊的入口
    @BindView(R.id.title_layout)
    RelativeLayout titleLayout;
    @BindView(R.id.phone_et)
    ImageView IPhoneEt;//打电话按钮
    @BindView(R.id.edit_price)
    EditText editPrice;//输入报价输入框
    @BindView(R.id.edit_time)
    EditText editTime;//输入周期
    @BindView(R.id.sure)
    TextView sure;//提交报价
    @BindView(R.id.title_bottom_ll)
    View titleBottomLl;
    @BindView(R.id.message_list)
    EaseChatMessageList messageList;//消息列表
    @BindView(R.id.voice_recorder)
    EaseVoiceRecorderView voiceRecorder;//语音列表
    @BindView(R.id.input_menu)
    EaseChatInputMenu inputMenu;//菜单
    protected ListView listView;

    protected SwipeRefreshLayout swipeRefreshLayout;
    protected ClipboardManager clipboard;

    private ChatInfo chatInfo;

    protected int chatType;//聊天方式
    protected File cameraFile;
    protected EMConversation conversation;
    protected int pagesize = 20;
    protected boolean isloading;
    protected boolean haveMoreData = true;
    // 是否处于阅后即焚状态的标志，true为阅后即焚状态：此状态下发送的消息都是阅后即焚的消息，暂时实现了文字和图片，false表示正常状态
    public boolean isReadFire = false;

    private boolean isMessageListInited;
    private RxPermissions rxPermissions;
    private String mobile;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected InputMethodManager inputManager;
    static final int ITEM_TAKE_PICTURE = 1;
    static final int ITEM_PICTURE = 2;
    static final int ITEM_LOCATION = 3;
    protected int[] itemStrings = { R.string.attach_take_pic, R.string.attach_picture, R.string.attach_location };
    protected int[] itemdrawables = { R.drawable.message_more_camera, R.drawable.message_more_pic, R.drawable.message_more_poi };
    protected int[] itemIds = { ITEM_TAKE_PICTURE, ITEM_PICTURE, ITEM_LOCATION };
    protected MyItemClickListener extendMenuItemClickListener;
    protected GroupListener groupListener;
    private EMChatRoomChangeListener chatRoomChangeListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (null == rootView) {
            rootView = inflater.inflate(R.layout.ease_fragment_chat, container, false);
            ButterKnife.bind(this, rootView);
            parseArgument();
            initView();
            initListener();
            initChatBindInfo();
        }

        ViewGroup p = (ViewGroup) rootView.getParent();
        if (null != p) {
            p.removeAllViews();
        }
        return rootView;
    }


    public void onNewIntent(Intent intent) {
        // 点击notification bar进入聊天页面，保证只有一个聊天页面
        String userid = intent.getStringExtra(HisRootActivity.HIS_ID_KEY);
        if (chatInfo.getHisUserID().equals(userid)) {
           if (getActivity() instanceof ChatActivity){
               ((ChatActivity) getActivity()).onNewIntentForFragment(intent);
           }
        } else {
            getActivity().finish();
            startActivity(intent);
        }
    }

    private void parseArgument() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            chatInfo = (ChatInfo) bundle.getSerializable(ChatActivity.Chat_info);
        }
        if (chatInfo == null) {
            ToastUtils.toastForShort(getActivity(), "聊天对象不能为空");
            getActivity().finish();
        }

        chatType = chatInfo.getChatType();
    }
    /**
     * 绑定头部订单信息的控件
     */
    private RelativeLayout VChatBindGp;
    private void initChatBindInfo() {
        VChatBindGp = (RelativeLayout)rootView.findViewById(R.id.edit_char_info_gp);
        //单聊初始化   群聊不初始化
        if (chatType != EaseConstant.CHATTYPE_SINGLE  || !chatInfo.isShowPriceInfo()) {
            VChatBindGp.setVisibility(View.GONE);
            return;
        }
        VChatBindGp.setVisibility(View.VISIBLE);
        ChatInfoData data = ChatBindInfoDBHelper.g().queryByKey(chatInfo.getHisUserID());
        if (data == null){
            mobile = chatInfo.getMobile();
            sure.setVisibility(View.VISIBLE);
            editPrice.setEnabled(true);
            editTime.setEnabled(true);
        }else {
            if (TextUtils.isEmpty(data.getMobile()) && TextUtils.isEmpty(chatInfo.getMobile())){
                VChatBindGp.setVisibility(View.GONE);
                return;
            }
            mobile = data.getMobile();
            sure.setVisibility(View.GONE);
            editPrice.setEnabled(false);
            editTime.setEnabled(false);
            editPrice.setText(data.getPrice());
            editTime.setText(data.getTime());
        }

        if (rxPermissions == null){
            rxPermissions = new RxPermissions(getActivity());
        }
        IPhoneEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rxPermissions.request(Manifest.permission.CALL_PHONE)
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(@NonNull Boolean aBoolean) throws Exception {
                                if (aBoolean) {
                                    //用intent启动拨打电话
                                    if(TextUtils.isEmpty(mobile)){
                                        ToastUtils.toastForShort(getActivity(),"电话号码不能为空");
                                        return;
                                    }
                                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + chatInfo.getMobile()));
                                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                                        startActivity(intent);
                                    }
                                }
                            }
                        });

            }
        });
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(editPrice.getText().toString())){
                    ToastUtils.toastForShort(getActivity(),"输入的价格不能为空");
                    return;
                }
                if (TextUtils.isEmpty(editTime.getText().toString())){
                    ToastUtils.toastForShort(getActivity(),"输入的交付日期不能为空");
                    return;
                }
                ChatBindInfoDBHelper.g().add(chatInfo.getHisUserID(),editPrice.getText().toString(),editTime.getText().toString(),chatInfo.getMobile());

                editPrice.setEnabled(false);
                editTime.setEnabled(false);
                sure.setVisibility(View.GONE);
            }
        });
    }
    private void initView(){
        listView = messageList.getListView();
        if (chatType == EaseConstant.CHATTYPE_SINGLE){
            initSingleView();
        }else if (chatType == EaseConstant.CHATTYPE_GROUP){
            initGroupView();
        }else {
            initRoom();
        }

        initMenu();
        getRefreshLayout();
        inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        rootView.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                hideKeyboard();
                getActivity().finish();
            }
        });
    }

    private void initListener() {
        View titleLaout = rootView.findViewById(R.id.title_layout);
        TextView  title = (TextView) rootView.findViewById(R.id.title);
        if (chatInfo.isHideTitle()){
               titleLaout.setVisibility(View.GONE);
        }else {
            title.setVisibility(View.VISIBLE);
            title.setText(chatInfo.getHisRealName());
        }
        setRefreshLayoutListener();
        if (chatFragmentListener == null) {
            chatFragmentListener = new ChatBaseActivity.EaseChatFragmentListener() {
                @Override
                public void onSetMessageAttributes(EMMessage message) {
                    // 根据当前状态是否是阅后即焚状态来设置发送消息的扩展
                    if (isReadFire && (message.getType() == EMMessage.Type.TXT)
                            && message.getStringAttribute(EaseConstant.MESSAGE_ATTR_EXPRESSION_ID, null) == null) {
                        //gif大表情消息，不支持阅后即焚
                        message.setAttribute(EaseConstant.EASE_ATTR_READFIRE, true);
                    }
                    if (isReadFire && (message.getType() == EMMessage.Type.IMAGE
                            || message.getType() == EMMessage.Type.VOICE)) {
                        message.setAttribute(EaseConstant.EASE_ATTR_READFIRE, true);
                    }
                }

                @Override
                public void onEnterToChatDetails() {
                    if (chatType == Constant.CHATTYPE_GROUP) {
                        startActivityForResult((new Intent(getActivity(), GroupDetailsActivity.class).putExtra("groupId", chatInfo.getHisUserID())),
                                REQUEST_CODE_GROUP_DETAIL);
                    }
                }

                @Override
                public void onAvatarClick(String username) {

                }

                @Override
                public boolean onMessageBubbleClick(EMMessage message) {
                    return false;
                }

                @Override
                public void onMessageBubbleLongClick(final EMMessage message) {
//消息框长按
                    MenuTwoButtonDialog dialog = new MenuTwoButtonDialog(getActivity(), new MenuTwoButtonDialog.ButtonClick() {

                        @SuppressWarnings("deprecation")
                        @Override
                        public void onSureButtonClick(int index) {
                            // TODO Auto-generated method stub
                            switch (index) {
                                case 0://复制
                                    if (message.getType() == EMMessage.Type.TXT) {
                                        clipboard.setText(((TextMessageBody) message.getBody()).getMessage());
                                    }
                                    break;
                                default://删除
                                    conversation.removeMessage(message.getMsgId());
                                    messageList.refresh();
                                    break;
                            }
                        }
                    });
                    dialog.title_tv.setText("复制");
                    dialog.tv2.setText("删除");
                    dialog.show();
                }

                @Override
                public boolean onExtendMenuItemClick(int itemId, View view) {
                    switch (itemId) {
                        case ITEM_READFIRE:
                            swapReadFire();
                            break;
                        default:
                            break;
                    }
                    //不覆盖已有的点击事件
                    return false;
                }

                @Override
                public EaseCustomChatRowProvider onSetCustomChatRowProvider() {
                    return null;
                }
            };
        }
    }

    //初始化单聊View
    private void initSingleView(){
        messageList.setShowUserNick(false);
        ITitleRight.setVisibility(View.GONE);
        onConversationInit();
        onMessageListInit();
        inputMenu.registerExtendMenuItem(R.string.attach_read_fire,
                R.drawable.message_read_fire, ITEM_READFIRE,
                extendMenuItemClickListener);
    }
    //初始化群聊view
    private void initGroupView(){
        registerExtendMenuItem();
        messageList.setShowUserNick(true);
        groupListener = new GroupListener();
        EMGroupManager.getInstance().addGroupChangeListener(groupListener);
        ITitleRight.setVisibility(View.VISIBLE);
        ITitleRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toGroupDetails();
            }
        });
        onConversationInit();
        onMessageListInit();
    }
    private void initRoom(){
        onChatRoomViewCreation();
        registerExtendMenuItem();
    }
    /**
     * 点击进入群组详情
     *
     */
    protected void toGroupDetails() {
        if (chatType == EaseConstant.CHATTYPE_GROUP) {
            EMGroup group = EMGroupManager.getInstance().getGroup(
                    chatInfo.getHisUserID());
            if (group == null) {
                Toast.makeText(getActivity(), R.string.gorup_not_found, Toast.LENGTH_SHORT)
                        .show();
                return;
            }
            if (chatFragmentListener != null) {
                chatFragmentListener.onEnterToChatDetails();
            }
        } else if (chatType == EaseConstant.CHATTYPE_CHATROOM) {
            if (chatFragmentListener != null) {
                chatFragmentListener.onEnterToChatDetails();
            }
        }
    }

    protected void onChatRoomViewCreation() {
        final ProgressDialog pd = ProgressDialog.show(getActivity(), "",
                "Joining......");
        EMChatManager.getInstance().joinChatRoom(chatInfo.getHisUserID(),
                new EMValueCallBack<EMChatRoom>() {

                    @Override
                    public void onSuccess(final EMChatRoom value) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (getActivity().isFinishing()
                                        || !chatInfo.getHisUserID().equals(value
                                        .getUsername()))
                                    return;
                                pd.dismiss();
                                EMChatRoom room = EMChatManager.getInstance()
                                        .getChatRoom(chatInfo.getHisUserID());

                                addChatRoomChangeListenr();
                                onConversationInit();
                                onMessageListInit();
                            }
                        });
                    }

                    @Override
                    public void onError(final int error, String errorMsg) {
                        // TODO Auto-generated method stub

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

    private void onMessageListInit() {
        messageList.init(
                chatInfo.getHisUserID(),
                chatType,
                chatFragmentListener != null ? chatFragmentListener
                        .onSetCustomChatRowProvider() : null);
        messageList.setFaceManager(getITopicApplication().getFaceManager());
        messageList.setChaterInfo(chatInfo.getHisUserID(),
                chatInfo.getHisRealName(),
                chatInfo.getHeadImageBean(),
                getITopicApplication().getMyUserBeanManager().getInstance().getAvatar().findSmallUrl(),
                conversation.isGroup());
        messageList.setAdapterAndSelectLast();
        // 设置list item里的控件的点击事件
        setListItemClickListener();

        messageList.getListView().setOnTouchListener(new View.OnTouchListener() {

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
        messageList
                .setItemClickListener(new EaseChatMessageList.MessageListItemClickListener() {

                    @Override
                    public void onUserAvatarClick(String username) {
                        if (chatFragmentListener != null) {
                            chatFragmentListener.onAvatarClick(username);
                        }
                    }

                    @Override
                    public void onResendClick(final EMMessage message) {
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
//						contextMenuMessage = message;
                        if (chatFragmentListener != null) {
                            chatFragmentListener
                                    .onMessageBubbleLongClick(message);
                        }
                    }

                    @Override
                    public boolean onBubbleClick(EMMessage message) {
                        if (chatFragmentListener != null) {
                            return chatFragmentListener
                                    .onMessageBubbleClick(message);
                        }
                        return false;
                    }
                });
    }

    public void resendMessage(EMMessage message) {
        message.status = EMMessage.Status.CREATE;
        addAttribute(message);
        EMChatManager.getInstance().sendMessage(message, null);
        messageList.refresh();
    }
    protected void onConversationInit() {
        // 获取当前conversation对象
        conversation = EMChatManager.getInstance().getConversation(
                chatInfo.getHisUserID());
        // 把此会话的未读数置为0
        conversation.markAllMessagesAsRead();
        // 初始化db时，每个conversation加载数目是getChatOptions().getNumberOfMessagesLoaded
        // 这个数目如果比用户期望进入会话界面时显示的个数不一样，就多加载一些
        final List<EMMessage> msgs = conversation.getAllMessages();
        int msgCount = msgs != null ? msgs.size() : 0;
        if (msgCount < conversation.getAllMsgCount() && msgCount < pagesize) {
            String msgId = null;
            if (msgs != null && msgs.size() > 0) {
                msgId = msgs.get(0).getMsgId();
            }
            if (chatType == EaseConstant.CHATTYPE_SINGLE) {
                conversation.loadMoreMsgFromDB(msgId, pagesize - msgCount);
            } else {
                conversation.loadMoreGroupMsgFromDB(msgId, pagesize - msgCount);
            }
        }

    }

    protected void addChatRoomChangeListenr() {
        chatRoomChangeListener = new EMChatRoomChangeListener() {

            @Override
            public void onChatRoomDestroyed(String roomId, String roomName) {
                if (roomId.equals(chatInfo.getHisUserID())) {
                    showChatroomToast(" room : " + roomId
                            + " with room name : " + roomName
                            + " was destroyed");
                    getActivity().finish();
                }
            }

            @Override
            public void onMemberJoined(String roomId, String participant) {
                showChatroomToast("member : " + participant
                        + " join the room : " + roomId);
            }

            @Override
            public void onMemberExited(String roomId, String roomName,
                                       String participant) {
                showChatroomToast("member : " + participant
                        + " leave the room : " + roomId + " room name : "
                        + roomName);
            }

            @Override
            public void onMemberKicked(String roomId, String roomName,
                                       String participant) {
                if (roomId.equals(chatInfo.getHisUserID())) {
                    String curUser = EMChatManager.getInstance()
                            .getCurrentUser();
                    if (curUser.equals(participant)) {
                        EMChatManager.getInstance().leaveChatRoom(
                                chatInfo.getHisUserID());
                        getActivity().finish();
                    } else {
                        showChatroomToast("member : " + participant
                                + " was kicked from the room : " + roomId
                                + " room name : " + roomName);
                    }
                }
            }

        };

        EMChatManager.getInstance().addChatRoomChangeListener(
                chatRoomChangeListener);
    }

    protected void showChatroomToast(final String toastContent) {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getActivity(), toastContent, Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }
    private void initMenu(){
        inputMenu.init(null);
        inputMenu.setChatInputMenuListener(new EaseChatInputMenu.ChatInputMenuListener() {

            @Override
            public void onSendMessage(String content) {
                // 发送文本消息
                sendTextMessage(content);
            }

            @Override
            public boolean onPressToSpeakBtnTouch(View v, MotionEvent event) {
                return voiceRecorder.onPressToSpeakBtnTouch(v, event,
                        new EaseVoiceRecorderView.EaseVoiceRecorderCallback() {

                            @Override
                            public void onVoiceRecordComplete(
                                    String voiceFilePath, int voiceTimeLength) {
                                // 发送语音消息
                                sendVoiceMessage(voiceFilePath, voiceTimeLength);
                            }
                        });
            }

            @Override
            public void onBigExpressionClicked(EaseEmojicon emojicon) {
                // 发送大表情(动态表情)
                sendBigExpressionMessage(emojicon.getName(),
                        emojicon.getIdentityCode(),chatInfo.getHisRealName());
            }
        });

        FaceManager faceManager = getITopicApplication().getFaceManager();
        //添加gif表情，如果不需要，请删除掉这些代码，并在drawable-hdpi里把对应的gif图片资源全删掉，能节省apk 8M的大小
        ((EaseEmojiconMenu)inputMenu.getEmojiconMenu()).addEmojiconGroup(faceManager.gifTuzkiGroupEntity());
        ((EaseEmojiconMenu)inputMenu.getEmojiconMenu()).addEmojiconGroup(faceManager.gifPaopaobingGroupEntity());
        ((EaseEmojiconMenu)inputMenu.getEmojiconMenu()).addEmojiconGroup(faceManager.gifBaozouGroupEntity());
        ((EaseEmojiconMenu)inputMenu.getEmojiconMenu()).addEmojiconGroup(faceManager.gifWorkGroupEntity());
    }


    private void getRefreshLayout(){
        swipeRefreshLayout = messageList.getSwipeRefreshLayout();
    }
    /**
     * 注册底部菜单扩展栏item; 覆盖此方法时如果不覆盖已有item，item的id需大于3
     */
    protected void registerExtendMenuItem() {
        for (int i = 0; i < itemStrings.length; i++) {
            inputMenu.registerExtendMenuItem(itemStrings[i], itemdrawables[i],
                    itemIds[i], extendMenuItemClickListener);
        }
    }

    public static ChatFragment getInstance(ChatInfo chatInfo) {
        ChatFragment chatFragment = new ChatFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ChatActivity.Chat_info, chatInfo);
        chatFragment.setArguments(bundle);
        return chatFragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isMessageListInited)
            messageList.refresh();

        getITopicApplication().getHuanXinManager().getHxSDKHelper().pushActivity(getActivity());
        // register the event listener when enter the foreground
        EMChatManager.getInstance().registerEventListener(
                this,
                new EMNotifierEvent.Event[] {
                        EMNotifierEvent.Event.EventNewMessage,
                        EMNotifierEvent.Event.EventOfflineMessage,
                        EMNotifierEvent.Event.EventDeliveryAck,
                        EMNotifierEvent.Event.EventReadAck });
    }

    @Override
    public void onStop() {
        super.onStop();
        EMChatManager.getInstance().unregisterEventListener(this);
        // 把此activity 从foreground activity 列表里移除
        getITopicApplication().getHuanXinManager().getHxSDKHelper().popActivity(getActivity());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
    public void onBackPressed() {
        if (inputMenu.onBackPressed()) {
           getActivity().finish();
            if (chatType == EaseConstant.CHATTYPE_CHATROOM) {
                EMChatManager.getInstance().leaveChatRoom(chatInfo.getHisUserID());
            }
        }
    }
    /**
     * 事件监听,registerEventListener后的回调事件
     *
     * see {@link EMNotifierEvent}
     */
    @Override
    public void onEvent(EMNotifierEvent event) {
        switch (event.getEvent()) {
            case EventNewMessage:
                // 获取到message
                EMMessage message = (EMMessage) event.getData();

                String username = null;
                // 群组消息
                if (message.getChatType() == EMMessage.ChatType.GroupChat
                        || message.getChatType() == EMMessage.ChatType.ChatRoom) {
                    username = message.getTo();
                } else {
                    // 单聊消息
                    username = message.getFrom();
                }

                // 如果是当前会话的消息，刷新聊天页面
                if (username.equals(chatInfo.getHisUserID())) {
                    messageList.refreshSelectLast();
                    // 声音和震动提示有新消息
                    EaseUI.getInstance().getNotifier().viberateAndPlayTone(message);
                } else {
                    // 如果消息不是和当前聊天ID的消息
                    EaseUI.getInstance().getNotifier().onNewMsg(message);
                }

                break;
            case EventDeliveryAck:
            case EventReadAck:
                // 获取到message
                // 获取到message
                EMMessage ackMessage = (EMMessage) event.getData();
                // 判断接收到ack的这条消息是不是阅后即焚的消息，如果是，则说明对方看过消息了，对方会销毁，这边也删除(现在只有txt iamge file三种消息支持 )
                if(ackMessage.getBooleanAttribute(EaseConstant.EASE_ATTR_READFIRE, false)
                        && (ackMessage.getType() == EMMessage.Type.TXT || ackMessage.getType() == EMMessage.Type.VOICE || ackMessage.getType() == EMMessage.Type.IMAGE)){
                    conversation.removeMessage(ackMessage.getMsgId());
                }
                messageList.refresh();
                break;
            case EventOfflineMessage:
                // a list of offline messages
                // List<EMMessage> offlineMessages = (List<EMMessage>)
                // event.getData();
                messageList.refresh();
                break;
            default:
                break;
        }

    }

    public void hideKeyboard() {
        if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getActivity().getCurrentFocus() != null)
                inputManager.hideSoftInputFromWindow(getActivity()
                                .getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    /**
     * by lzan13
     * 设置阅后即焚模式的开关，在easeui中默认是关闭状态，需要在Demo层面调用此方法
     * @param
     */
    public void swapReadFire(){
        if(!isReadFire){
            //之前没开启，现在开启
            isReadFire = true;
            showErrorToast(getResources().getString(R.string.toast_read_fire_opened));
            inputMenu.getExtendMenu().reloadChatMenuItemModel(ITEM_READFIRE,"关闭即焚",R.drawable.message_read_fire_red);
        }else{
            //之前开启，现在关闭
            isReadFire = false;
            showErrorToast(getResources().getString(R.string.toast_read_fire_close));
            inputMenu.getExtendMenu().reloadChatMenuItemModel(ITEM_READFIRE,"阅后即焚",R.drawable.message_read_fire);
        }
        inputMenu.getExtendMenu().notifyDataSetChanged();
    }
    /**
     * 扩展菜单栏item点击事件
     *
     */
    class MyItemClickListener implements
            EaseChatExtendMenu.EaseChatExtendMenuItemClickListener {

        @Override
        public void onClick(int itemId, View view) {
            if (chatFragmentListener != null) {
                if (chatFragmentListener.onExtendMenuItemClick(itemId, view)) {
                    return;
                }
            }
            switch (itemId) {
                case ITEM_TAKE_PICTURE: // 拍照
                    selectPicFromCamera();
                    break;
                case ITEM_PICTURE:
                    selectPicFromLocal(); // 图库选择图片
                    break;
                case ITEM_LOCATION: // 位置
                    startActivityForResult(new Intent(getActivity(),
                            BaiduMapActivity.class), REQUEST_CODE_MAP);
                    break;

                default:
                    break;
            }
        }
    }

    /**
     * 照相获取图片
     */
    protected void selectPicFromCamera() {
        if (!EaseCommonUtils.isExitsSdcard()) {
            Toast.makeText(getActivity(), R.string.sd_card_does_not_exist, Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        cameraFile = new File(PathUtil.getInstance().getImagePath(),
                EMChatManager.getInstance().getCurrentUser()
                        + System.currentTimeMillis() + ".jpg");
        cameraFile.getParentFile().mkdirs();
        startActivityForResult(
                new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(
                        MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile)),
                REQUEST_CODE_CAMERA);
    }

    /**
     * 从图库获取图片
     */
    protected void selectPicFromLocal() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");

        } else {
            intent = new Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        startActivityForResult(intent, REQUEST_CODE_LOCAL);
    }

    protected ChatBaseActivity.EaseChatFragmentListener chatFragmentListener;


    public interface EaseChatFragmentListener {
        /**
         * 设置消息扩展属性
         */
        void onSetMessageAttributes(EMMessage message);

        /**
         * 进入会话详情
         */
        void onEnterToChatDetails();

        /**
         * 用户头像点击事件
         *
         * @param username
         */
        void onAvatarClick(String username);

        /**
         * 消息气泡框点击事件
         */
        boolean onMessageBubbleClick(EMMessage message);

        /**
         * 消息气泡框长按事件
         */
        void onMessageBubbleLongClick(EMMessage message);

        /**
         * 扩展输入栏item点击事件,如果要覆盖EaseChatFragment已有的点击事件，return true
         *
         * @param view
         * @param itemId
         * @return
         */
        boolean onExtendMenuItemClick(int itemId, View view);

        /**
         * 设置自定义chatrow提供者
         *
         * @return
         */
        EaseCustomChatRowProvider onSetCustomChatRowProvider();
    }


    // 发送消息方法
    // ==========================================================================
    protected void sendTextMessage(String content) {
        EMMessage message = EMMessage.createTxtSendMessage(content,
                chatInfo.getHisUserID());
        sendMessage(message);
    }
    protected void sendVoiceMessage(String filePath, int length) {
        EMMessage message = EMMessage.createVoiceSendMessage(filePath, length,
                chatInfo.getHisUserID());
        sendMessage(message);
    }

    protected void sendImageMessage(String imagePath) {
        EMMessage message = EMMessage.createImageSendMessage(imagePath, false,
                chatInfo.getHisUserID());
        sendMessage(message);
    }
    protected void sendLocationMessage(double latitude, double longitude,
                                       String locationAddress) {
        EMMessage message = EMMessage.createLocationSendMessage(latitude,
                longitude, locationAddress, chatInfo.getHisUserID());
        sendMessage(message);
    }

    /**
     * 根据图库图片uri发送图片
     *
     * @param selectedImage
     */
    protected void sendPicByUri(Uri selectedImage) {
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                filePathColumn, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            cursor = null;

            if (picturePath == null || picturePath.equals("null")) {
                Toast toast = Toast.makeText(getActivity(),
                        R.string.cant_find_pictures, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }
            sendImageMessage(picturePath);
        } else {
            File file = new File(selectedImage.getPath());
            if (!file.exists()) {
                Toast toast = Toast.makeText(getActivity(),
                        R.string.cant_find_pictures, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;

            }
            sendImageMessage(file.getAbsolutePath());
        }

    }

    protected void sendMessage(EMMessage message) {
        if (chatFragmentListener != null) {
            // 设置扩展属性
            chatFragmentListener.onSetMessageAttributes(message);
        }
        // 如果是群聊，设置chattype,默认是单聊
        if (chatType == EaseConstant.CHATTYPE_GROUP) {
            message.setChatType(EMMessage.ChatType.GroupChat);
        } else if (chatType == EaseConstant.CHATTYPE_CHATROOM) {
            message.setChatType(EMMessage.ChatType.ChatRoom);
        }
        addAttribute(message);
        // 发送消息
        EMChatManager.getInstance().sendMessage(message, null);
        // 刷新ui
        messageList.refreshSelectLast();
    }
    protected void sendBigExpressionMessage(String name, String identityCode,String toChatUsername) {
        EMMessage message = EaseCommonUtils.createExpressionMessage(
                toChatUsername, name, identityCode);
        sendMessage(message);
    }
    private void addAttribute(EMMessage message) {
        UserBean instanceUser = getITopicApplication()
                .getMyUserBeanManager().getInstance();
        message.setAttribute(HisRootActivity.SENDER_ID_KEY, instanceUser.getUserid());
        message.setAttribute(HisRootActivity.SENDER_NAME_KEY, instanceUser.getName());
        message.setAttribute(HisRootActivity.SENDER_AVATAR_KEY, JsonUtil.getJson(instanceUser.getAvatar()));
        message.setAttribute(HisRootActivity.RECEIVER_ID_KEY, chatInfo.getHisUserID());
        message.setAttribute(HisRootActivity.RECEIVER_NAME_KEY, chatInfo.getHisRealName());
        message.setAttribute(HisRootActivity.RECEIVER_AVATAR_KEY, JsonUtil.getJson(chatInfo.getHeadImageBean()));
    }


    class GroupListener extends EaseGroupRemoveListener {

        @Override
        public void onUserRemoved(final String groupId, String groupName) {
            getActivity().runOnUiThread(new Runnable() {

                public void run() {
                    if (chatInfo.getHisUserID().equals(groupId)) {
                        Toast.makeText(getActivity(), R.string.you_are_group, Toast.LENGTH_SHORT)
                                .show();
                        getActivity().finish();
                    }
                }
            });
        }

        @Override
        public void onGroupDestroy(final String groupId, String groupName) {
            // 群组解散正好在此页面，提示群组被解散，并finish此页面
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    if (chatInfo.getHisUserID().equals(groupId)) {
                        Toast.makeText(getActivity(),
                                R.string.the_current_group, Toast.LENGTH_SHORT).show();
                        getActivity().finish();
                    }
                }
            });
        }
    }


    protected void setRefreshLayoutListener() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        if (listView.getFirstVisiblePosition() == 0
                                && !isloading && haveMoreData) {
                            List<EMMessage> messages;
                            try {
                                if (chatType == EaseConstant.CHATTYPE_SINGLE) {
                                    messages = conversation.loadMoreMsgFromDB(
                                            messageList.getItem(0).getMsgId(),
                                            pagesize);
                                } else {
                                    messages = conversation
                                            .loadMoreGroupMsgFromDB(messageList
                                                            .getItem(0).getMsgId(),
                                                    pagesize);
                                }
                            } catch (Exception e1) {
                                swipeRefreshLayout.setRefreshing(false);
                                return;
                            }
                            if (messages.size() > 0) {
                                messageList.refreshSeekTo(messages.size() - listView.getHeaderViewsCount() - 1);
                                if (messages.size() != pagesize) {
                                    haveMoreData = false;
                                }
                            } else {
                                haveMoreData = false;
                            }

                            isloading = false;

                        } else {
                            Toast.makeText(
                                    getActivity(),
                                    getResources().getString(
                                            R.string.no_more_messages),
                                    Toast.LENGTH_SHORT).show();
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 600);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == GroupDetailsActivity.GROUP_DELETE_RESPONSE) {
            //删除群组
            getActivity().setResult(getActivity().RESULT_OK);
            getActivity().finish();
            return;
        }
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_CAMERA) { // 发送照片
                if (cameraFile != null && cameraFile.exists())
                    sendImageMessage(cameraFile.getAbsolutePath());
            } else if (requestCode == REQUEST_CODE_LOCAL) { // 发送本地图片
                if (data != null) {
                    Uri selectedImage = data.getData();
                    if (selectedImage != null) {
                        sendPicByUri(selectedImage);
                    }
                }
            } else if (requestCode == REQUEST_CODE_MAP) { // 地图
                double latitude = data.getDoubleExtra("latitude", 0);
                double longitude = data.getDoubleExtra("longitude", 0);
                String locationAddress = data.getStringExtra("address");
                if (locationAddress != null && !locationAddress.equals("")) {
                    sendLocationMessage(latitude, longitude, locationAddress);
                } else {
                    Toast.makeText(getActivity(),
                            R.string.unable_to_get_loaction, Toast.LENGTH_SHORT).show();
                }

            }
        }
    }
}
