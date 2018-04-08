package com.houwei.guaishang.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChatManager;
import com.houwei.guaishang.R;
import com.houwei.guaishang.activity.newui.MyInfoActivity;
import com.houwei.guaishang.bean.CommentPushBean;
import com.houwei.guaishang.bean.FansPushBean;
import com.houwei.guaishang.bean.FloatResponse;
import com.houwei.guaishang.bean.IntResponse;
import com.houwei.guaishang.bean.UserBean;
import com.houwei.guaishang.data.DBReq;
import com.houwei.guaishang.manager.MyUserBeanManager;
import com.houwei.guaishang.manager.ChatManager.OnMyActionMessageGetListener;
import com.houwei.guaishang.manager.ChatManager.OnMyActionMessageHadReadListener;
import com.houwei.guaishang.manager.MyUserBeanManager.CheckMoneyListener;
import com.houwei.guaishang.manager.MyUserBeanManager.CheckPointListener;
import com.houwei.guaishang.manager.MyUserBeanManager.UserStateChangeListener;
import com.houwei.guaishang.view.CircleImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MineFragment extends BaseFragment implements OnClickListener,
		UserStateChangeListener, OnMyActionMessageGetListener,
		OnMyActionMessageHadReadListener, CheckPointListener ,EMEventListener, CheckMoneyListener {
	
	private boolean fragmentIsHidden;
	
	private CircleImageView avator_iv;
	private TextView following_count_tv, fans_count_tv, header_name_tv,header_mobile_tv;
	private TextView point_tv,message_tv,money_tv;
	private MyUserBeanManager myUserBeanManager;
	private TextView unReadFansCountTV;
	private TextView timeline_tv;
	
	private boolean isCheckingMoney;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.activity_mine_tab_personal,container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
		initListener();
	}
	
	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		this.fragmentIsHidden = hidden;
		if (!hidden && !isCheckingMoney && myUserBeanManager.getInstance()!=null) {
			refreshUI();
			isCheckingMoney = true;
			if (MyUserBeanManager.MISSION_ENABLE) {
				myUserBeanManager.startCheckPointRun();
			}
			myUserBeanManager.startCheckMoneyRun();
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if (!fragmentIsHidden && !isCheckingMoney && myUserBeanManager.getInstance()!=null) {
			isCheckingMoney = true;
			if (MyUserBeanManager.MISSION_ENABLE) {
				myUserBeanManager.startCheckPointRun();
			}
			myUserBeanManager.startCheckMoneyRun();
		}
		
		refreshUI();
		
		// register the event listener when enter the foreground
		EMChatManager.getInstance().registerEventListener(
				this,new EMNotifierEvent.Event[] {
						EMNotifierEvent.Event.EventNewMessage,
						EMNotifierEvent.Event.EventOfflineMessage,
						EMNotifierEvent.Event.EventConversationListChanged });
	}
	

	@Override
	public void onStop() {
		EMChatManager.getInstance().unregisterEventListener(this);
		super.onStop();
	}
	
	@Override
	public void onCheckPointFinish(IntResponse intResponse) {
		// TODO Auto-generated method stub
		point_tv.setText(intResponse.getData()+"金币");
		isCheckingMoney = false;
	}
	
	@Override
	public void onCheckMoneyFinish(FloatResponse intResponse) {
		// TODO Auto-generated method stub
		money_tv.setText("余额："+intResponse.getData()+"元");
		isCheckingMoney = false;
	}
	
	protected void initView() {
		// TODO Auto-generated method stub
		myUserBeanManager = getITopicApplication().getMyUserBeanManager();
		myUserBeanManager.addOnUserStateChangeListener(this);

		avator_iv = (CircleImageView) getView().findViewById(R.id.avator);
		following_count_tv = (TextView)  getView().findViewById(R.id.following_count_tv);
		timeline_tv = (TextView)  getView().findViewById(R.id.timeline_tv);
		fans_count_tv = (TextView)  getView().findViewById(R.id.fans_count_tv);
		header_name_tv = (TextView)  getView().findViewById(R.id.header_name_tv);
		header_mobile_tv = (TextView)  getView().findViewById(R.id.header_mobile_tv);
		point_tv  = (TextView)  getView().findViewById(R.id.point_tv);
		message_tv = (TextView) getView().findViewById(R.id.message_tv);
		money_tv  = (TextView)  getView().findViewById(R.id.money_tv);
		
		onUserInfoChanged(myUserBeanManager.getInstance());

		unReadFansCountTV = (TextView)  getView().findViewById(R.id.unReadFansCountTV);
		checkUnReadFansCount(DBReq.getInstence(getActivity()).getTotalUnReadFansCount());
		checkUnReadChatMessageCount(getUnreadMsgCountTotal());
		
		if (myUserBeanManager.getInstance() == null) {
			point_tv.setText("0金币");
		}
	}

	protected void initListener() {
		// TODO Auto-generated method stub
		getITopicApplication().getChatManager()
				.addOnMyActionMessageGetListener(this);
		getITopicApplication().getChatManager()
				.addOnMyActionMessageHadReadListener(this);
		 myUserBeanManager.addOnCheckPointListener(this);
		 myUserBeanManager.addOnCheckMoneyListener(this);
		 getView().findViewById(R.id.following_ll).setOnClickListener(this);
		 getView().findViewById(R.id.fans_ll).setOnClickListener(this);
		 getView().findViewById(R.id.timeline_cell_ll).setOnClickListener(this);
		 getView().findViewById(R.id.myinfo_ll).setOnClickListener(this);
		 getView().findViewById(R.id.mine_ll).setOnClickListener(this);
		 getView().findViewById(R.id.praise_ll).setOnClickListener(this);
		 getView().findViewById(R.id.comment_ll).setOnClickListener(this);
		 getView().findViewById(R.id.takemoney_ll).setOnClickListener(this);
		 getView().findViewById(R.id.moneylog_ll).setOnClickListener(this);
		 getView().findViewById(R.id.my_payed_ll).setOnClickListener(this);
		 getView().findViewById(R.id.my_customer_ll).setOnClickListener(this);
		 
		 getView().findViewById(R.id.chat_ll).setOnClickListener(
					new View.OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							Intent i = new Intent(getActivity(),ConversationActivity.class);
							i.putExtra("showback", true);
							startActivity(i);
						}
					});
		 getView().findViewById(R.id.near_ll).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						Intent i = new Intent(getActivity(),
								MineNearMemberActivity.class);
						startActivity(i);
					}
				});

		 getView().findViewById(R.id.setting_ll).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						Intent i = new Intent(getActivity(),
								MineSystemActivity.class);
						startActivity(i);
					}
				});
		 
		 getView().findViewById(R.id.mission_ll).setOnClickListener(
					new View.OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							Intent i = new Intent(getActivity(),MissionActivity.class);
							startActivity(i);
						}
					});
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		if (!checkLogined()) {
			return;
		}
		Intent i = null;
		switch (arg0.getId()) {
		case R.id.mine_ll:
//			UserBean bean = myUserBeanManager.getInstance();
//			jumpToHisInfoActivity(bean.getUserid(), bean.getName(),
//					bean.getAvatar());
//			new OrderBuyDialog(getActivity()).show();
			Intent intent = new Intent(getActivity(),RechargeDialogActivity.class);
			getActivity().startActivity(intent);
			break;
		case R.id.fans_ll:
			i = new Intent(getActivity(), FriendShipActivity.class);
			i.putExtra("isFansList", true);
			startActivity(i);
			break;
		case R.id.following_ll:
			i = new Intent(getActivity(), FriendShipActivity.class);
			i.putExtra("isFansList", false);
			startActivity(i);
			break;
		case R.id.myinfo_ll:
			i = new Intent(getActivity(),MyInfoActivity.class);
			startActivity(i);
//			VoiceUtils.getInstance(getActivity()).getSyntheszer().speak("发布成功  买法兰就上网购");
			break;
		case R.id.timeline_cell_ll:
			i = new Intent(getActivity(), TopicMineActivity.class);
			i.putExtra(HisRootActivity.HIS_ID_KEY, getUserID());
			i.putExtra("title", "我的商品");
			i.putExtra("api", "topic/getlist");
			startActivity(i);
			break;
		case R.id.praise_ll:
			i = new Intent(getActivity(), TopicMineActivity.class);
			i.putExtra(HisRootActivity.HIS_ID_KEY, getUserID());
			i.putExtra("title", "我赞过的");
			i.putExtra("api", "topic/getpraiselist");
			startActivity(i);
			break;
		case R.id.comment_ll:
			i = new Intent(getActivity(), TopicMineActivity.class);
			i.putExtra(HisRootActivity.HIS_ID_KEY, getUserID());
			i.putExtra("title", "我评论的");
			i.putExtra("api", "topic/getmycommentedlist");
			startActivity(i);
			break;
		case R.id.takemoney_ll:
			i = new Intent(getActivity(),MineTakeMoneyActivity.class);
			startActivity(i);
			break;
		case R.id.moneylog_ll:
			i = new Intent(getActivity(),MineMoneyLogRootActivity.class);
			startActivity(i);
			break;
		case R.id.my_payed_ll:
			i = new Intent(getActivity(),MinePaidActivity.class);
			startActivity(i);
			break;
		case R.id.my_customer_ll:
			i = new Intent(getActivity(),MineCustomerActivity.class);
			startActivity(i);
			break;
			
		default:
			break;
		}
	}

	@Override
	public void onUserInfoChanged(final UserBean ub) {
		// 如果走的是回调，这里ub不可能是null
		// onCreated还可以主动调用这个方法，这时候可能是null
		if (ub == null) {
			return ;
		}
		following_count_tv.setText("" + ub.getFollowsCount());
		fans_count_tv.setText("" + ub.getFansCount());
		header_name_tv.setText("" + ub.getName());
		timeline_tv.setText("" + ub.getTopicCount());
		if(TextUtils.isEmpty(ub.getMobile())){
			header_mobile_tv.setText("");
		}else {
			header_mobile_tv.setText("" + ub.getMobile());
		}


		ImageLoader.getInstance().displayImage(ub.getAvatar().findOriginalUrl(), avator_iv);

		avator_iv.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(),
						GalleryActivity.class);
				ArrayList<String> urls = new ArrayList<String>();
				urls.add(ub.getAvatar().findOriginalUrl());
				intent.putExtra(GalleryActivity.EXTRA_IMAGE_URLS, urls);
				intent.putExtra(GalleryActivity.EXTRA_IMAGE_INDEX, 0);
				startActivity(intent);
			}
		});
	}

	@Override
	public void onUserLogin(UserBean ub) {
		// TODO Auto-generated method stub
		onUserInfoChanged(ub);
	}

	@Override
	public void onUserLogout() {
		// TODO Auto-generated method stub
		// 用户退出，按道理讲，这里应该把所有控件设为“未登录”
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		getITopicApplication().getChatManager()
				.removeOnMyActionMessageGetListener(this);
		getITopicApplication().getChatManager()
				.removeOnMyActionMessageHadReadListener(this);
		myUserBeanManager.removeUserStateChangeListener(this);
		myUserBeanManager.removeCheckPointListener(this);
		myUserBeanManager.removeOnCheckMoneyListener(this);
		super.onDestroy();
	}

	private void checkUnReadFansCount(int unReadActionCount) {
		unReadFansCountTV.setText("" + unReadActionCount);
		unReadFansCountTV.setVisibility(unReadActionCount == 0 ? View.INVISIBLE
				: View.VISIBLE);
	}


	private void checkUnReadChatMessageCount(int unReadActionCount) {
		message_tv.setText("" + unReadActionCount);
		message_tv.setVisibility(unReadActionCount == 0 ? View.INVISIBLE
				: View.VISIBLE);
	}
	
	@Override
	public void onMyNewFansGet(FansPushBean fansPushBean) {
		// TODO Auto-generated method stub
		checkUnReadFansCount(DBReq.getInstence(getActivity()).getTotalUnReadFansCount());
	}

	@Override
	public void onNewCommentGet(CommentPushBean commentPushBean) {
		// TODO Auto-generated method stub

	}

	/**
	 * read
	 */
	@Override
	public void onFansHadRead() {
		// TODO Auto-generated method stub
		checkUnReadFansCount(0);
	}

	@Override
	public void onCommentsHadRead() {
		// TODO Auto-generated method stub

	}
	
	/**
	 * 获取未读聊天消息数
	 * 
	 * @return
	 */
	private int getUnreadMsgCountTotal() {
		MainActivity mainAC = (MainActivity) getActivity();
		return mainAC.getUnreadMsgCountTotal();
	}
	
	/**
	 * 监听事件
	 */
	@Override
	public void onEvent(EMNotifierEvent event) {
		switch (event.getEvent()) {
		case EventNewMessage: // 普通消息
		{
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

		default:
			break;
		}
	}
	
	private void refreshUI() {
		getActivity().runOnUiThread(new Runnable() {
			public void run() {
				// 刷新LinearLayout上的红圈
				checkUnReadChatMessageCount(getUnreadMsgCountTotal());
			}
		});
	}



}
