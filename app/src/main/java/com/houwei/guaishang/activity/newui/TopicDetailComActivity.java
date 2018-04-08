package com.houwei.guaishang.activity.newui;

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
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.util.EMLog;
import com.easemob.util.PathUtil;
import com.flyco.dialog.widget.base.BaseDialog;
import com.github.jdsjlzx.ItemDecoration.GridItemDecoration;
import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.houwei.guaishang.R;
import com.houwei.guaishang.activity.BaiduMapActivity;
import com.houwei.guaishang.activity.BaseActivity;
import com.houwei.guaishang.activity.GalleryActivity;
import com.houwei.guaishang.activity.HisRootActivity;
import com.houwei.guaishang.activity.MainActivity;
import com.houwei.guaishang.activity.MissionActivity;
import com.houwei.guaishang.activity.TopicDetailHeaderLayout;
import com.houwei.guaishang.activity.TopicReleaseActivity;
import com.houwei.guaishang.adapter.GridAdapter;
import com.houwei.guaishang.bean.AvatarBean;
import com.houwei.guaishang.bean.BaseResponse;
import com.houwei.guaishang.bean.IntResponse;
import com.houwei.guaishang.bean.LocationBean;
import com.houwei.guaishang.bean.TopicBean;
import com.houwei.guaishang.bean.UserBean;
import com.houwei.guaishang.bean.event.TopicHomeEvent;
import com.houwei.guaishang.data.Contants;
import com.houwei.guaishang.easemob.EaseChatExtendMenu;
import com.houwei.guaishang.easemob.EaseChatInputMenu;
import com.houwei.guaishang.easemob.EaseChatMessageList;
import com.houwei.guaishang.easemob.EaseCommonUtils;
import com.houwei.guaishang.easemob.EaseConstant;
import com.houwei.guaishang.easemob.EaseCustomChatRowProvider;
import com.houwei.guaishang.easemob.EaseEmojicon;
import com.houwei.guaishang.easemob.EaseEmojiconMenu;
import com.houwei.guaishang.easemob.EaseGroupRemoveListener;
import com.houwei.guaishang.easemob.EaseImageUtils;
import com.houwei.guaishang.easemob.EaseUI;
import com.houwei.guaishang.easemob.EaseVoiceRecorderView;
import com.houwei.guaishang.layout.MProgressDialog;
import com.houwei.guaishang.layout.MenuTwoButtonDialog;
import com.houwei.guaishang.layout.OfferDialog;
import com.houwei.guaishang.layout.SureOrCancelDialog;
import com.houwei.guaishang.manager.FaceManager;
import com.houwei.guaishang.manager.FollowManager;
import com.houwei.guaishang.manager.MyLocationManager;
import com.houwei.guaishang.manager.MyUserBeanManager;
import com.houwei.guaishang.tools.DealResult;
import com.houwei.guaishang.tools.HttpUtil;
import com.houwei.guaishang.tools.JsonUtil;
import com.houwei.guaishang.tools.SPUtils;
import com.houwei.guaishang.tools.ShareUtil;
import com.houwei.guaishang.tools.ShareUtil2;
import com.houwei.guaishang.tools.ToastUtils;
import com.houwei.guaishang.tools.ValueUtil;
import com.houwei.guaishang.view.NumberProgressBar;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;


/**
 * 动态评论界面
 */

public class TopicDetailComActivity  extends BaseActivity implements EMEventListener , FollowManager.FollowListener,MyLocationManager.LocationListener {
	protected static final String TAG = "EaseChatFragment";
	protected static final int REQUEST_CODE_MAP = 1;
	protected static final int REQUEST_CODE_CAMERA = 2;
	protected static final int REQUEST_CODE_LOCAL = 3;

	// 是否处于阅后即焚状态的标志，true为阅后即焚状态：此状态下发送的消息都是阅后即焚的消息，暂时实现了文字和图片，false表示正常状态
	public boolean isReadFire = false;
	/**
	 * 传入fragment的参数
	 */
	protected int chatType;
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
	protected TopicDetailComActivity.GroupListener groupListener;
//	protected EMMessage contextMenuMessage;

	static final int ITEM_TAKE_PICTURE = 1;
	static final int ITEM_PICTURE = 2;
	static final int ITEM_LOCATION = 3;


	// 阅后即焚id 避免和基类定义的常量可能发生的冲突，常量从11开始定义
	protected static final int ITEM_READFIRE = 15;

	protected int[] itemStrings = { R.string.attach_take_pic, R.string.attach_picture, R.string.attach_location };
	protected int[] itemdrawables = { R.drawable.message_more_camera, R.drawable.message_more_pic, R.drawable.message_more_poi };
	protected int[] itemIds = { ITEM_TAKE_PICTURE, ITEM_PICTURE, ITEM_LOCATION };
	private EMChatRoomChangeListener chatRoomChangeListener;
	private boolean isMessageListInited;
	protected TopicDetailComActivity.MyItemClickListener extendMenuItemClickListener;

	// 给谁发送消息
	private String toChatUsername;
	private AvatarBean hisAvatarBean;

	private boolean needPay;

	NumberProgressBar proBar;
	private TextView header_time;
	private TextView content;
	private TextView header_name;
	private TextView header_location;
	private TextView price_tv;
	private Button order_btn;
	private Button chat_btn;
	private Button follow_btn;
	private TopicBean topicBean;
	private RxPermissions rxPermissions;//获取相机权限
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private FaceManager faceManager;
	private LocationBean currentLocationBean;
	private TextView share_count_btn;
	private TextView commentBtn
			;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_topic_detail_com);

		rxPermissions=new RxPermissions(this);
		topicBean = (TopicBean) getIntent().getSerializableExtra("TopicBean");
		needPay = getIntent().getBooleanExtra(EaseConstant.EXTRA_NEED_PAY,false);
		initHeadView();
		initView();
		initListener();
	}

	private void initHeadView() {
		getITopicApplication().getFollowManager().addFollowListener(this);
		ImageView avator = (ImageView) findViewById(R.id.avator);
		ImageView back = (ImageView) findViewById(R.id.back);
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				TopicDetailComActivity.this.finish();
			}
		});
		LRecyclerView recyclerView = (LRecyclerView) findViewById(R.id.recyclerView);
		recyclerView.setLayoutManager(new GridLayoutManager(this,3));
		GridAdapter mAdapter=new GridAdapter(this);
		final List<AvatarBean> pictures = topicBean.getPicture();
//		if(pictures.size()<9){
//			for(int i=0;i<9;i++){
//				AvatarBean b=new AvatarBean();
//				b.setOriginal("");
//				b.setSmall("");
//				if(pictures.size()==9){
//					break;
//				}
//				pictures.add(b);
//			}
//		}
		mAdapter.setDataList(pictures);
		LRecyclerViewAdapter lRecyclerViewAdapter = new LRecyclerViewAdapter(mAdapter);
		recyclerView.setAdapter(lRecyclerViewAdapter);
		GridItemDecoration divider = new GridItemDecoration.Builder(this)
				.setHorizontal(R.dimen.default_divider_padding)
				.setVertical(R.dimen.default_divider_padding)
				.setColorResource(R.color.white_color)
				.build();

//		recyclerView.setHasFixedSize(true);
		recyclerView.addItemDecoration(divider);
		recyclerView.setLoadMoreEnabled(false);
		recyclerView.setPullRefreshEnabled(false);
		recyclerView.refresh();
		lRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(View view, int position) {
				Intent intent=new Intent(TopicDetailComActivity.this, GalleryActivity.class);
				ArrayList<String> lists=new ArrayList<String>();
				for(AvatarBean bean:pictures){
					lists.add(HttpUtil.IP_NOAPI+bean.getOriginal());
				}
				intent.putExtra(GalleryActivity.EXTRA_IMAGE_URLS, lists);
				intent.putExtra(GalleryActivity.EXTRA_IMAGE_INDEX, position);
				startActivity(intent);
			}
		});

		 header_time = (TextView) findViewById(R.id.header_time);

		 content = (TextView) findViewById(R.id.content);
		share_count_btn = (TextView) findViewById(R.id.share_count_btn);
		commentBtn = (TextView) findViewById(R.id.comment_btn);
		 header_name = (TextView) findViewById(R.id.header_name);
		 proBar = (NumberProgressBar)findViewById(R.id.progress_bar);
		 header_location = (TextView)  findViewById(R.id.header_location);
		header_name.setText(topicBean.getMemberName());
		header_time.setText(topicBean.getTimeString());
		faceManager = getITopicApplication().getFaceManager();
		content.setText(faceManager.
						convertNormalStringToSpannableString(this, topicBean.getContent()),
				TextView.BufferType.SPANNABLE);
		header_location.setText(topicBean.getDistance() != null ? topicBean.getDistanceString() : topicBean.getAddress());
		 price_tv = (TextView) findViewById(R.id.price_tv);
		 order_btn = (Button) findViewById(R.id.order_btn);
		 chat_btn = (Button) findViewById(R.id.chat_btn);
		follow_btn = (Button) findViewById(R.id.follow_btn);
		commentBtn.setText(topicBean.getCommentCount()+"");
		imageLoader.displayImage(topicBean.getMemberAvatar().findSmallUrl(), avator, getITopicApplication().getOtherManage().getCircleOptionsDisplayImageOptions());
		try {
			int max = Integer.valueOf(topicBean.getSetRob());
			int progress = Integer.valueOf(topicBean.getNowRob());
			proBar.setMax(max);
			proBar.setProgress(progress);
		} catch (Exception e) {
			e.printStackTrace();
		}

		findViewById(R.id.order_btn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(currentLocationBean == null){
					String city = (String) SPUtils.get(TopicDetailComActivity.this, Contants.LOCATION_CITY_KEY,"上海市");
					OfferDialog3 d=new OfferDialog3(TopicDetailComActivity.this,topicBean,city);
					d.show();
				}else {
					OfferDialog3 d=new OfferDialog3(TopicDetailComActivity.this,topicBean,currentLocationBean.getCity()+currentLocationBean.getDistrict());
					d.show();
				}


			}
		});

		avator.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				jumpToHisInfoActivity(topicBean.getMemberId(), topicBean.getMemberName(), topicBean.getMemberAvatar());
			}
		});
		share_count_btn.setText(topicBean.getShareNum()+"");
		share_count_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				/*ShareUtil shareUtil = new ShareUtil(TopicDetailComActivity.this);
                shareUtil.setShareNum(topicBean.getShareNum());
                shareUtil.setTextView(share_count_btn);
                shareUtil.setContent(topicBean.getContent());
				shareUtil.setUrl(HttpUtil.SHARE_TOPIC_IP + topicBean.getTopicId());
				if (topicBean.getPicture() != null && !topicBean.getPicture().isEmpty()) {
					shareUtil.setImageUrl(topicBean.getPicture().get(0).findOriginalUrl());
				}
				shareUtil.showBottomPopupWin();*/
				ShareUtil2 shareUtil2 = new ShareUtil2(TopicDetailComActivity.this, new PlatformActionListener() {
					@Override
					public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
						Log.i("WXCH","onComplete");
						/*if (MyUserBeanManager.MISSION_ENABLE && getITopicApplication().getMyUserBeanManager().getInstance()!=null) {
							getITopicApplication().getMyUserBeanManager().startPointActionRun(MissionActivity.MISSION_SHARE_ID);
						}*/

						myHandler.sendEmptyMessage(1);
					}

					@Override
					public void onError(Platform platform, int i, Throwable throwable) {

					}

					@Override
					public void onCancel(Platform platform, int i) {

					}
				});
				shareUtil2.setContent(topicBean.getContent());
				shareUtil2.setUrl(HttpUtil.SHARE_TOPIC_IP + topicBean.getTopicId());
				if (topicBean.getPicture() != null && !topicBean.getPicture().isEmpty()) {
					shareUtil2.setImageUrl(topicBean.getPicture().get(0).findOriginalUrl());
				}
				shareUtil2.showBottomPopupWin();
			}
		});

		chat_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
                /*mContext.jumpToChatActivity(bean.getMemberId(),
                        bean.getMemberName(), bean.getMemberAvatar(), EaseConstant.CHATTYPE_SINGLE);*/

				rxPermissions.request(Manifest.permission.CALL_PHONE)
						.subscribe(new Consumer<Boolean>() {
							@Override
							public void accept(@NonNull Boolean aBoolean) throws Exception {
								if (aBoolean) {
									//用intent启动拨打电话
									String number = topicBean.getMobile();
									if(TextUtils.isEmpty(number)){
										ToastUtils.toastForShort(TopicDetailComActivity.this,"电话号码不能为空");
										return;
									}
									Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
									if (ActivityCompat.checkSelfPermission(TopicDetailComActivity.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
										TopicDetailComActivity.this.startActivity(intent);
									}
								}
							}
						});

			}
		});


		follow_btn.setText(ValueUtil.getRelationTypeStringSimple(topicBean.getFriendship()));
		follow_btn.setBackgroundResource(ValueUtil.getRelationTypeDrawableSimple(topicBean.getFriendship()));
		follow_btn.setTextColor(ValueUtil.getRelationTextColorSimple(topicBean.getFriendship()));
		follow_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (TopicDetailComActivity.this.checkLogined()) {
					TopicDetailComActivity.this.progress.show();
					TopicDetailComActivity.this.getITopicApplication().getFollowManager().followOnThread(TopicDetailComActivity.this.getUserID(),
							topicBean.getMemberId());
				}
			}
		});

	}

	protected void initView() {
		// TODO Auto-generated method stub
		initProgressDialog();

		// 判断单聊还是群聊
		chatType = getIntent().getIntExtra(EaseConstant.EXTRA_CHATTYPE, EaseConstant.CHATTYPE_SINGLE);
		toChatUsername = getIntent().getStringExtra(HisRootActivity.HIS_ID_KEY);

		// 按住说话录音控件
		voiceRecorderView = (EaseVoiceRecorderView) findViewById(R.id.voice_recorder);

		// 消息列表layout
		messageList = (EaseChatMessageList) findViewById(R.id.message_list);
		if (chatType != EaseConstant.CHATTYPE_SINGLE)
			messageList.setShowUserNick(true);
		listView = messageList.getListView();


		extendMenuItemClickListener = new TopicDetailComActivity.MyItemClickListener();
		inputMenu = (EaseChatInputMenu) findViewById(R.id.input_menu);
		registerExtendMenuItem();
		// init input menu
		inputMenu.init(null);
		inputMenu.setChatInputMenuListener(new EaseChatInputMenu.ChatInputMenuListener() {

			@Override
			public void onSendMessage(String content) {
				// 发送文本消息
				sendTextMessage(content);
			}

			@Override
			public boolean onPressToSpeakBtnTouch(View v, MotionEvent event) {
				return voiceRecorderView.onPressToSpeakBtnTouch(v, event,
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
						emojicon.getIdentityCode());
			}
		});

		FaceManager faceManager = getITopicApplication().getFaceManager();
		//添加gif表情，如果不需要，请删除掉这些代码，并在drawable-hdpi里把对应的gif图片资源全删掉，能节省apk 8M的大小
		((EaseEmojiconMenu)inputMenu.getEmojiconMenu()).addEmojiconGroup(faceManager.gifTuzkiGroupEntity());
		((EaseEmojiconMenu)inputMenu.getEmojiconMenu()).addEmojiconGroup(faceManager.gifPaopaobingGroupEntity());
		((EaseEmojiconMenu)inputMenu.getEmojiconMenu()).addEmojiconGroup(faceManager.gifBaozouGroupEntity());
		((EaseEmojiconMenu)inputMenu.getEmojiconMenu()).addEmojiconGroup(faceManager.gifWorkGroupEntity());


		swipeRefreshLayout = messageList.getSwipeRefreshLayout();
//		swipeRefreshLayout.setColorSchemeResources(R.color.holo_blue_bright,
//				R.color.holo_green_light, R.color.holo_orange_light,
//				R.color.holo_red_light);

		inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}

	private void initListener() {
		// TODO Auto-generated method stub
//		setTitleName(getIntent().getStringExtra(HisRootActivity.HIS_NAME_KEY));
		getITopicApplication().getLocationManager().addLocationListener(this);
		getITopicApplication().getLocationManager().startLoction(false);
		hisAvatarBean = (AvatarBean) getIntent().getSerializableExtra(HisRootActivity.HIS_AVATAR_KEY);
		if (hisAvatarBean == null) {
			hisAvatarBean = new AvatarBean();
		}

		if (chatType != EaseConstant.CHATTYPE_SINGLE) {
			//群聊
			if (chatType == EaseConstant.CHATTYPE_GROUP) {
				// 监听当前会话的群聊解散被T事件
				groupListener = new TopicDetailComActivity.GroupListener();
				EMGroupManager.getInstance().addGroupChangeListener(groupListener);

				findViewById(R.id.title_right).setVisibility(View.VISIBLE);
				findViewById(R.id.title_right).setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						toGroupDetails();
					}
				});
			} else {
				onChatRoomViewCreation();
			}
		}

		if (chatType != EaseConstant.CHATTYPE_CHATROOM) {
			onConversationInit();
			onMessageListInit();
		}

		findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				hideKeyboard();
				finish();
			}
		});

		setRefreshLayoutListener();

		// show forward message if the message is not null
		String forward_msg_id =  getIntent().getStringExtra("forward_msg_id");
		if (forward_msg_id != null) {
			// 发送要转发的消息
			forwardMessage(forward_msg_id);
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// 点击notification bar进入聊天页面，保证只有一个聊天页面
		String userid = intent.getStringExtra(HisRootActivity.HIS_ID_KEY);
		if (toChatUsername.equals(userid))
			super.onNewIntent(intent);
		else {
			finish();
			startActivity(intent);
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
	 * 注册底部菜单扩展栏item; 覆盖此方法时如果不覆盖已有item，item的id需大于3
	 */
	protected void registerExtendMenuItem() {
		for (int i = 0; i < itemStrings.length; i++) {
			inputMenu.registerExtendMenuItem(itemStrings[i], itemdrawables[i],
					itemIds[i], extendMenuItemClickListener);
		}
	}

	protected void onConversationInit() {
		// 获取当前conversation对象
		conversation = EMChatManager.getInstance().getConversation(
				toChatUsername);
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

	private void onMessageListInit() {
		messageList.init(
				toChatUsername,
				chatType,
				chatFragmentListener != null ? chatFragmentListener
						.onSetCustomChatRowProvider() : null);
		messageList.setFaceManager(getITopicApplication().getFaceManager());
		messageList.setChaterInfo(getIntent().getStringExtra(HisRootActivity.HIS_ID_KEY),
				getIntent().getStringExtra(HisRootActivity.HIS_NAME_KEY),
				hisAvatarBean,
				getITopicApplication().getMyUserBeanManager().getInstance().getAvatar().findSmallUrl(),
				conversation.isGroup());
		messageList.setAdapterAndSelectLast();
		// 设置list item里的控件的点击事件
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
								TopicDetailComActivity.this, getResources().getString(R.string.confirm_resend), "确定",
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
									TopicDetailComActivity.this,
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
					Toast.makeText(TopicDetailComActivity.this,
							R.string.unable_to_get_loaction, Toast.LENGTH_SHORT).show();
				}

			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (isMessageListInited)
			messageList.refresh();

		getITopicApplication().getHuanXinManager().getHxSDKHelper().pushActivity(this);

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
		// unregister this event listener when this activity enters the
		// background
		EMChatManager.getInstance().unregisterEventListener(this);

		// 把此activity 从foreground activity 列表里移除
		getITopicApplication().getHuanXinManager().getHxSDKHelper().popActivity(this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		getITopicApplication().getFollowManager().removeFollowListener(this);
		getITopicApplication().getLocationManager().removeLocationListener(this);
		if (groupListener != null) {
			EMGroupManager.getInstance().removeGroupChangeListener(
					groupListener);
		}
		if (chatType == EaseConstant.CHATTYPE_CHATROOM) {
			EMChatManager.getInstance().leaveChatRoom(toChatUsername);
		}

		if (chatRoomChangeListener != null) {
			EMChatManager.getInstance().removeChatRoomChangeListener(
					chatRoomChangeListener);
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
				if (username.equals(toChatUsername)) {
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

	/**
	 * 暂时只给子类（ChatAvitivity）用
	 * @return
	 */
	protected String getToChatUsername(){
		return toChatUsername;
	}

	public void onBackPressed() {
		if (inputMenu.onBackPressed()) {
			TopicDetailComActivity.this.finish();
			if (chatType == EaseConstant.CHATTYPE_CHATROOM) {
				EMChatManager.getInstance().leaveChatRoom(toChatUsername);
			}
		}
	}

	protected void onChatRoomViewCreation() {
		final ProgressDialog pd = ProgressDialog.show(TopicDetailComActivity.this, "",
				"Joining......");
		EMChatManager.getInstance().joinChatRoom(toChatUsername,
				new EMValueCallBack<EMChatRoom>() {

					@Override
					public void onSuccess(final EMChatRoom value) {
						TopicDetailComActivity.this.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (TopicDetailComActivity.this.isFinishing()
										|| !toChatUsername.equals(value
										.getUsername()))
									return;
								pd.dismiss();
								EMChatRoom room = EMChatManager.getInstance()
										.getChatRoom(toChatUsername);
								EMLog.d(TAG,
										"join room success : " + room.getName());
								addChatRoomChangeListenr();
								onConversationInit();
								onMessageListInit();
							}
						});
					}

					@Override
					public void onError(final int error, String errorMsg) {
						// TODO Auto-generated method stub
						EMLog.d(TAG, "join room failure : " + error);
						TopicDetailComActivity.this.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								pd.dismiss();
							}
						});
						TopicDetailComActivity.this.finish();
					}
				});
	}

	protected void addChatRoomChangeListenr() {
		chatRoomChangeListener = new EMChatRoomChangeListener() {

			@Override
			public void onChatRoomDestroyed(String roomId, String roomName) {
				if (roomId.equals(toChatUsername)) {
					showChatroomToast(" room : " + roomId
							+ " with room name : " + roomName
							+ " was destroyed");
					TopicDetailComActivity.this.finish();
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
				if (roomId.equals(toChatUsername)) {
					String curUser = EMChatManager.getInstance()
							.getCurrentUser();
					if (curUser.equals(participant)) {
						EMChatManager.getInstance().leaveChatRoom(
								toChatUsername);
						TopicDetailComActivity.this.finish();
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
		TopicDetailComActivity.this.runOnUiThread(new Runnable() {
			public void run() {
				Toast.makeText(TopicDetailComActivity.this, toastContent, Toast.LENGTH_SHORT)
						.show();
			}
		});
	}

	@Override
	public void FollowChanged(IntResponse followResponse) {
		progress.dismiss();
		if (followResponse.isSuccess()) {
//			if(topicBean.getFriendship())
			if(topicBean.getFriendship()==1){
				follow_btn.setBackgroundResource(R.mipmap.attention_un1);
				topicBean.setFriendship(0);
			}else{
				follow_btn.setBackgroundResource(R.mipmap.attenttion1);
				topicBean.setFriendship(1);
			}

		}else {
			showErrorToast(followResponse.getMessage());
		}
	}

	@Override
	public void onLocationFail() {
//		ToastUtils.toastForShort(this,"定位失败");
	}

	@Override
	public void onLocationSuccess(LocationBean currentLocationBean) {
		this.currentLocationBean = currentLocationBean;
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
					Log.i("WXCH","ITEM_TAKE_PICTURE");
					rxPermissions.request(Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE)
							.subscribe(new Consumer<Boolean>() {
								@Override
								public void accept(@NonNull Boolean aBoolean) throws Exception {
									if(aBoolean){
										selectPicFromCamera();
									}
								}
							});
					break;
				case ITEM_PICTURE:
					rxPermissions.request(Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE)
							.subscribe(new Consumer<Boolean>() {
								@Override
								public void accept(@NonNull Boolean aBoolean) throws Exception {
									if(aBoolean){
										selectPicFromLocal(); // 图库选择图片
									}
								}
							});
					break;
				case ITEM_LOCATION: // 位置
					rxPermissions.request(Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.READ_PHONE_STATE)
							.subscribe(new Consumer<Boolean>() {
								@Override
								public void accept(@NonNull Boolean aBoolean) throws Exception {
									if(aBoolean){
										startActivityForResult(new Intent(TopicDetailComActivity.this,
												BaiduMapActivity.class), REQUEST_CODE_MAP);
									}
								}
							});
					break;

				default:
					break;
			}
		}

	}


	private void addAttribute(EMMessage message) {
		UserBean instanceUser = getITopicApplication()
				.getMyUserBeanManager().getInstance();
		message.setAttribute(HisRootActivity.SENDER_ID_KEY, instanceUser.getUserid());
		message.setAttribute(HisRootActivity.SENDER_NAME_KEY, instanceUser.getName());
		message.setAttribute(HisRootActivity.SENDER_AVATAR_KEY, JsonUtil.getJson(instanceUser.getAvatar()));

		message.setAttribute(HisRootActivity.RECEIVER_ID_KEY, getIntent().getStringExtra(HisRootActivity.HIS_ID_KEY));
		message.setAttribute(HisRootActivity.RECEIVER_NAME_KEY, getIntent().getStringExtra(HisRootActivity.HIS_NAME_KEY));
		message.setAttribute(HisRootActivity.RECEIVER_AVATAR_KEY, JsonUtil.getJson(hisAvatarBean));
	}

	// 发送消息方法
	// ==========================================================================
	protected void sendTextMessage(String content) {
		EMMessage message = EMMessage.createTxtSendMessage(content,
				toChatUsername);
		sendMessage(message);
	}

	protected void sendBigExpressionMessage(String name, String identityCode) {
		EMMessage message = EaseCommonUtils.createExpressionMessage(
				toChatUsername, name, identityCode);
		sendMessage(message);
	}

	protected void sendVoiceMessage(String filePath, int length) {
		EMMessage message = EMMessage.createVoiceSendMessage(filePath, length,
				toChatUsername);
		sendMessage(message);
	}

	protected void sendImageMessage(String imagePath) {
		EMMessage message = EMMessage.createImageSendMessage(imagePath, false,
				toChatUsername);
		sendMessage(message);
	}

	protected void sendLocationMessage(double latitude, double longitude,
									   String locationAddress) {
		EMMessage message = EMMessage.createLocationSendMessage(latitude,
				longitude, locationAddress, toChatUsername);
		sendMessage(message);
	}

	protected void sendVideoMessage(String videoPath, String thumbPath,
									int videoLength) {
		EMMessage message = EMMessage.createVideoSendMessage(videoPath,
				thumbPath, videoLength, toChatUsername);
		sendMessage(message);
	}

	protected void sendFileMessage(String filePath) {
		EMMessage message = EMMessage.createFileSendMessage(filePath,
				toChatUsername);
		sendMessage(message);
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

	public void resendMessage(EMMessage message) {
		message.status = EMMessage.Status.CREATE;
		addAttribute(message);
		EMChatManager.getInstance().sendMessage(message, null);
		messageList.refresh();
	}

	// ===================================================================================

	/**
	 * 根据图库图片uri发送图片
	 *
	 * @param selectedImage
	 */
	protected void sendPicByUri(Uri selectedImage) {
		String[] filePathColumn = { MediaStore.Images.Media.DATA };
		Cursor cursor = TopicDetailComActivity.this.getContentResolver().query(selectedImage,
				filePathColumn, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();
			cursor = null;

			if (picturePath == null || picturePath.equals("null")) {
				Toast toast = Toast.makeText(TopicDetailComActivity.this,
						R.string.cant_find_pictures, Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;
			}
			sendImageMessage(picturePath);
		} else {
			File file = new File(selectedImage.getPath());
			if (!file.exists()) {
				Toast toast = Toast.makeText(TopicDetailComActivity.this,
						R.string.cant_find_pictures, Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;

			}
			sendImageMessage(file.getAbsolutePath());
		}

	}

	/**
	 * 根据uri发送文件
	 *
	 * @param uri
	 */
	protected void sendFileByUri(Uri uri) {
		String filePath = null;
		if ("content".equalsIgnoreCase(uri.getScheme())) {
			String[] filePathColumn = { MediaStore.Images.Media.DATA };
			Cursor cursor = null;
			try {
				cursor = TopicDetailComActivity.this.getContentResolver().query(uri,
						filePathColumn, null, null, null);
				int column_index = cursor.getColumnIndexOrThrow("_data");
				if (cursor.moveToFirst()) {
					filePath = cursor.getString(column_index);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if ("file".equalsIgnoreCase(uri.getScheme())) {
			filePath = uri.getPath();
		}
		File file = new File(filePath);
		if (file == null || !file.exists()) {
			Toast.makeText(TopicDetailComActivity.this, R.string.File_does_not_exist, Toast.LENGTH_SHORT)
					.show();
			return;
		}
		// 大于10M不让发送
		if (file.length() > 10 * 1024 * 1024) {
			Toast.makeText(TopicDetailComActivity.this,
					R.string.The_file_is_not_greater_than_10_m, Toast.LENGTH_SHORT).show();
			return;
		}
		sendFileMessage(filePath);
	}

	/**
	 * 照相获取图片
	 */
	protected void selectPicFromCamera() {
		if (!EaseCommonUtils.isExitsSdcard()) {
			Toast.makeText(TopicDetailComActivity.this, R.string.sd_card_does_not_exist, Toast.LENGTH_SHORT)
					.show();
			return;
		}
		Log.i("WXCH","cameraFile");
		cameraFile = new File(PathUtil.getInstance().getImagePath(),
				EMChatManager.getInstance().getCurrentUser()
						+ System.currentTimeMillis() + ".jpg");
		cameraFile.getParentFile().mkdirs();
		/*startActivityForResult(
				new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(
						MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile)),
				REQUEST_CODE_CAMERA);*/
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		Uri photoUri = Uri.fromFile(cameraFile); // 传递路径
		intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);// 更改系统默认存储路径
		startActivityForResult(intent, REQUEST_CODE_CAMERA);
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

	/**
	 * DQ 2016-03-10，不做
	 * 点击清空聊天记录
	 *
	 */
	private void emptyHistory() {

	}

	/**
	 * 点击进入群组详情
	 *
	 */
	protected void toGroupDetails() {
		if (chatType == EaseConstant.CHATTYPE_GROUP) {
			EMGroup group = EMGroupManager.getInstance().getGroup(
					toChatUsername);
			if (group == null) {
				Toast.makeText(TopicDetailComActivity.this, R.string.gorup_not_found, Toast.LENGTH_SHORT)
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

	/**
	 * 隐藏软键盘
	 */
	@Override
	public void hideKeyboard() {
		if (TopicDetailComActivity.this.getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (TopicDetailComActivity.this.getCurrentFocus() != null)
				inputManager.hideSoftInputFromWindow(TopicDetailComActivity.this
								.getCurrentFocus().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	/**
	 * 转发消息
	 *
	 * @param forward_msg_id
	 */
	protected void forwardMessage(String forward_msg_id) {
		final EMMessage forward_msg = EMChatManager.getInstance().getMessage(
				forward_msg_id);
		EMMessage.Type type = forward_msg.getType();
		switch (type) {
			case TXT:
				String gifEmotion = forward_msg.getStringAttribute(EaseConstant.MESSAGE_ATTR_EXPRESSION_ID, null);
				//gifEmotion为null说明普通文字，不为null说明gif表情（类似：paopaobing3）
				if (gifEmotion!=null) {
					sendBigExpressionMessage(
							((TextMessageBody) forward_msg.getBody()).getMessage(),gifEmotion);
				} else {
					// 获取消息内容，发送消息
					String content = ((TextMessageBody) forward_msg.getBody())
							.getMessage();
					sendTextMessage(content);
				}
				break;
			case IMAGE:
				// 发送图片
				String filePath = ((ImageMessageBody) forward_msg.getBody())
						.getLocalUrl();
				if (filePath != null) {
					File file = new File(filePath);
					if (!file.exists()) {
						// 不存在大图发送缩略图
						filePath = EaseImageUtils.getThumbnailImagePath(filePath);
					}
					sendImageMessage(filePath);
				}
				break;
			default:
				break;
		}

		if (forward_msg.getChatType() == EMMessage.ChatType.ChatRoom) {
			EMChatManager.getInstance().leaveChatRoom(forward_msg.getTo());
		}
	}

	/**
	 * 监测群组解散或者被T事件
	 *
	 */
	class GroupListener extends EaseGroupRemoveListener {

		@Override
		public void onUserRemoved(final String groupId, String groupName) {
			TopicDetailComActivity.this.runOnUiThread(new Runnable() {

				public void run() {
					if (toChatUsername.equals(groupId)) {
						Toast.makeText(TopicDetailComActivity.this, R.string.you_are_group, Toast.LENGTH_SHORT)
								.show();
						TopicDetailComActivity.this.finish();
					}
				}
			});
		}

		@Override
		public void onGroupDestroy(final String groupId, String groupName) {
			// 群组解散正好在此页面，提示群组被解散，并finish此页面
			TopicDetailComActivity.this.runOnUiThread(new Runnable() {
				public void run() {
					if (toChatUsername.equals(groupId)) {
						Toast.makeText(TopicDetailComActivity.this,
								R.string.the_current_group, Toast.LENGTH_SHORT).show();
						TopicDetailComActivity.this.finish();
					}
				}
			});
		}

	}

	protected TopicDetailComActivity.EaseChatFragmentListener chatFragmentListener;

	public void setChatFragmentListener(TopicDetailComActivity.EaseChatFragmentListener chatFragmentListener) {
		this.chatFragmentListener = chatFragmentListener;
	}

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


	Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 1:
					try {
						HashMap<String, String> data = new HashMap<String, String>();
						data.put("id", topicBean.getMemberId());
						String s = HttpUtil.postMsg(HttpUtil.getData(data), HttpUtil.IP + "mission/Sharing/");
						Log.i("WXCH","SSSSSS:" + s);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Log.i("WXCH","myHandler topic:"+(topicBean.getShareNum()+ 1));
					share_count_btn.setText((topicBean.getShareNum()+ 1)+ "");
					break;
			}
			super.handleMessage(msg);
		}
	};
	public class OfferDialog3 extends BaseDialog {
		Context context;
		TopicBean topicBean;
		private ImageLoader imageLoader = ImageLoader.getInstance();
		private TextView tvClose;
		private TextView tvConfirm;
		private ImageView imageAvatar;
		private TextView orderName;
		private TextView orderPrice;
		private EditText etMoney;
		private EditText etTime;
		final MProgressDialog progress ;
		private EditText etRemark;
		private String address;


		public OfferDialog3(Context context,TopicBean bean,String address) {
			super(context);
			this.context=context;
			this.topicBean=bean;
			this.address=address;
			progress = new MProgressDialog(context, false);
		}

		@Override
		public View onCreateView() {
			widthScale(0.95f);
//        showAnim(new Swing());

			// dismissAnim(this, new ZoomOutExit());
			View inflate = View.inflate(context, R.layout.dialog_offer, null);
			tvClose=(TextView)inflate.findViewById(R.id.tv_close);
			imageAvatar=(ImageView)inflate.findViewById(R.id.avatar);
			orderName=(TextView)inflate.findViewById(R.id.order_name);
			orderPrice=(TextView)inflate.findViewById(R.id.order_price);
			etMoney=(EditText)inflate.findViewById(R.id.et_money);
			etTime=(EditText)inflate.findViewById(R.id.et_time);
			etRemark=(EditText)inflate.findViewById(R.id.et_remark);
			tvConfirm=(TextView)inflate.findViewById(R.id.tv_confirm);
			return inflate;
		}

		@Override
		public void setUiBeforShow() {
			imageLoader.displayImage(topicBean.getCover(), imageAvatar,
					((TopicDetailComActivity)context).getITopicApplication().getOtherManage().getCircleOptionsDisplayImageOptions());
			tvClose.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dismiss();
				}
			});
			orderName.setText(topicBean.getContent());
			tvConfirm.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					String money=etMoney.getText().toString().trim();
					String time=etTime.getText().toString().trim();
					String remark=etTime.getText().toString().trim();
					if(TextUtils.isEmpty(money)){
						ToastUtils.toastForShort(context,"金额不能为空");
						return;
					}
					if(TextUtils.isEmpty(time)){
						ToastUtils.toastForShort(context,"工期不能为空");
						return;
					}
					offerMsg(money,time,remark,needPay);
				}
			});
		}

		private void offerMsg(String money, String time,String remark,boolean needPay) {
			progress.show();
			int payMoney = needPay ? 1 * 100 : 0;//单位：个，目前1元 = 100金币

			OkGo.<String>post(HttpUtil.IP+"topic/rob")
					.params("order_id",topicBean.getTopicId())
					.params("user_id",topicBean.getMemberId())
					.params("offer_id",getUserID())
					.params("price",money)
					.params("cycle",time)
					.params("address",address)
					.params("beizhu",remark)
//					.params("payMoney",payMoney)
					.execute(new StringCallback() {
						@Override
						public void onSuccess(Response<String> response) {
							progress.dismiss();
							BaseResponse baseResponse= DealResult.getInstace().dealBase(context,response);
							if(baseResponse==null){
								return;
							}
							if(baseResponse.getCode()==1){
								ToastUtils.toastForShort(context,baseResponse.getMessage());
								dismiss();
								try {
									int max = Integer.valueOf(topicBean.getSetRob());
									int progress = Integer.valueOf(topicBean.getNowRob());
									proBar.setMax(max);
									proBar.setProgress(progress+1);
									EventBus.getDefault().post(new TopicHomeEvent());
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}

						@Override
						public void onError(Response<String> response) {
							progress.dismiss();
							super.onError(response);
						}
					});
		}

		public String getUserID() {
			UserBean instanceUser =  ((TopicDetailComActivity)context).getITopicApplication()
					.getMyUserBeanManager().getInstance();
			return instanceUser == null ? "" : instanceUser.getUserid();
		}
	}

}
