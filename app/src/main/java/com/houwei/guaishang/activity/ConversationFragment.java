package com.houwei.guaishang.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import com.houwei.guaishang.R;
import com.houwei.guaishang.bean.AvatarBean;
import com.houwei.guaishang.bean.UserBean;
import com.houwei.guaishang.easemob.ChatAllHistoryAdapter;
import com.houwei.guaishang.easemob.EaseConstant;
import com.houwei.guaishang.layout.SureOrCancelDialog;
import com.houwei.guaishang.manager.MyUserBeanManager.UserStateChangeListener;
import com.houwei.guaishang.tools.JsonParser;
import com.easemob.EMEventListener;
import com.easemob.EMGroupChangeListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.ConnectionListener;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMConversation.EMConversationType;
import com.easemob.util.NetUtils;

public class ConversationFragment extends BaseFragment implements
		UserStateChangeListener, ConnectionListener, EMEventListener {

	private boolean fragmentIsHidden;
	
	private ListView listView;
	private ChatAllHistoryAdapter adapter;

	public RelativeLayout errorItem;
	public TextView errorText;
	private List<EMConversation> conversationList = new ArrayList<EMConversation>();

	private MyGroupChangeListener groupChangeListener = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.activity_conversation_huanxin,container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
		initListener();
	}

	private void initListener() {
		// TODO Auto-generated method stub
		
		if (getActivity().getIntent().getBooleanExtra("showback", false)) {
			getView().findViewById(R.id.back).setVisibility(View.VISIBLE);
			BackButtonListener();
		}else{
			getView().findViewById(R.id.back).setVisibility(View.GONE);
		}
		
		getView().findViewById(R.id.title_right).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						if (checkLogined()) {
							Intent i = new Intent(
									getActivity(),
									GroupMineActivity.class);
							startActivity(i);
						}
					}
				});

		getView().findViewById(R.id.search_layout).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						Intent i = new Intent(getActivity(),
								SearchActivity.class);
						startActivity(i);
					}
				});

		EMChatManager.getInstance().addConnectionListener(this);

	}

	public void initView() {
		getITopicApplication().getMyUserBeanManager()
				.addOnUserStateChangeListener(this);

		errorItem = (RelativeLayout) getView().findViewById(R.id.rl_error_item);
		errorText = (TextView) errorItem.findViewById(R.id.tv_connect_errormsg);

		conversationList.addAll(loadConversationsWithRecentChat());
		listView = (ListView) getView().findViewById(R.id.listview);
		adapter = new ChatAllHistoryAdapter(getActivity(), 1, conversationList);
		// 设置adapter
		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				EMConversation conversation = adapter.getItem(position
						- listView.getHeaderViewsCount());
				String userid = conversation.getUserName();// 如果是群聊，那userid
															// 就是groupid

				if (conversation.isGroup()) {
					EMGroup group = EMGroupManager.getInstance().getGroup(
							userid);

					if (group != null) {
						jumpToChatActivity(userid, group.getGroupName(), null,
								EaseConstant.CHATTYPE_GROUP);
					} else {
						showErrorToast("错误的群组");
					}

				} else {
					// 看最后一条对话信息，是我主动发送创建的，还是别人发过来的
					String hisRealName = "";
					AvatarBean hisAvatarBean = null;
					EMMessage lastMessage = conversation.getLastMessage();
					if (lastMessage.direct == EMMessage.Direct.SEND) {
						// 是我发出的,这里头像应该显示是接收人
						hisRealName = lastMessage.getStringAttribute(
								HisRootActivity.RECEIVER_NAME_KEY, "");
						hisAvatarBean = JsonParser.getAvatarBean(lastMessage
								.getStringAttribute(
										HisRootActivity.RECEIVER_AVATAR_KEY, ""));
						if (hisAvatarBean == null) {
							hisAvatarBean = new AvatarBean();
						}

					} else {
						// 是我收到的，这里头像应该是发送人
						hisRealName = lastMessage.getStringAttribute(
								HisRootActivity.SENDER_NAME_KEY, "");
						hisAvatarBean = JsonParser.getAvatarBean(lastMessage
								.getStringAttribute(
										HisRootActivity.SENDER_AVATAR_KEY, ""));
						if (hisAvatarBean == null) {
							hisAvatarBean = new AvatarBean();
						}
					}
					jumpToChatActivity(userid, hisRealName, hisAvatarBean,
							EaseConstant.CHATTYPE_SINGLE);

				}

			}
		});

		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					final int arg2, long arg3) {
				// TODO Auto-generated method stub

				SureOrCancelDialog followDialog = new SureOrCancelDialog(
						getActivity(), "删除此对话", "好",
						new SureOrCancelDialog.SureButtonClick() {

							@Override
							public void onSureButtonClick() {
								// TODO Auto-generated method stub

								EMConversation tobeDeleteCons = conversationList
										.get(arg2
												- listView
														.getHeaderViewsCount());
								// 删除此会话
								EMChatManager.getInstance().deleteConversation(
										tobeDeleteCons.getUserName(),
										tobeDeleteCons.isGroup());

								adapter.remove(tobeDeleteCons);
								adapter.notifyDataSetChanged();

							}
						});
				followDialog.show();
				return true;
			}
		});
		// 注册上下文菜单
		registerForContextMenu(listView);

		groupChangeListener = new MyGroupChangeListener();
		// 注册群聊相关的listener
		EMGroupManager.getInstance()
				.addGroupChangeListener(groupChangeListener);

		onUserLogin(getITopicApplication().getMyUserBeanManager().getInstance());
	}

	
	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		this.fragmentIsHidden = hidden;
		if (!hidden) {
			refresh();
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if (!fragmentIsHidden) {
			refresh();
		}
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
	
	/**
	 * 监听事件
     */
	@Override
	public void onEvent(EMNotifierEvent event) {
		switch (event.getEvent()) {
		case EventNewMessage: // 普通消息
		{
			refresh();
			break;
		}

		case EventOfflineMessage: {
			refresh();
			break;
		}

		case EventConversationListChanged: {
			refresh();
		    break;
		}
		case EventReadAck:{
			refresh();
		    break;
		}
		default:
			break;
		}
	}
	
	/**
	 * 刷新页面
	 */
	private void refresh() {
		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				conversationList.clear();
				conversationList.addAll(loadConversationsWithRecentChat());
				if (adapter != null)
					adapter.notifyDataSetChanged();
				listView.requestLayout();
			}
		});

	}

	/**
	 * 获取所有会话
	 * 
	 * @param context
	 * @return +
	 */
	private List<EMConversation> loadConversationsWithRecentChat() {
		// 获取所有会话，包括陌生人
		Hashtable<String, EMConversation> conversations = EMChatManager
				.getInstance().getAllConversations();
		// 过滤掉messages size为0的conversation
		/**
		 * 如果在排序过程中有新消息收到，lastMsgTime会发生变化 影响排序过程，Collection.sort会产生异常
		 * 保证Conversation在Sort过程中最后一条消息的时间不变 避免并发问题
		 */
		List<Pair<Long, EMConversation>> sortList = new ArrayList<Pair<Long, EMConversation>>();
		synchronized (conversations) {
			for (EMConversation conversation : conversations.values()) {
				if (conversation.getAllMessages().size() != 0) {
					if (conversation.getType() == EMConversationType.GroupChat) {
						// 去掉已经不存在的聊天群的消息
						EMGroup group = EMGroupManager.getInstance().getGroup(
								conversation.getUserName());
						if (group == null) {
							continue;
						}
					}
					sortList.add(new Pair<Long, EMConversation>(conversation
							.getLastMessage().getMsgTime(), conversation));
				}
			}
		}
		try {
			// Internal is TimSort algorithm, has bug
			sortConversationByLastChatTime(sortList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		List<EMConversation> list = new ArrayList<EMConversation>();
		for (Pair<Long, EMConversation> sortItem : sortList) {
			list.add(sortItem.second);
		}
		return list;
	}

	/**
	 * 根据最后一条消息的时间排序
	 * 
	 * @param usernames
	 */
	private void sortConversationByLastChatTime(
			List<Pair<Long, EMConversation>> conversationList) {
		Collections.sort(conversationList,
				new Comparator<Pair<Long, EMConversation>>() {
					@Override
					public int compare(final Pair<Long, EMConversation> con1,
							final Pair<Long, EMConversation> con2) {

						if (con1.first == con2.first) {
							return 0;
						} else if (con2.first > con1.first) {
							return 1;
						} else {
							return -1;
						}
					}

				});
	}


	private class MyGroupChangeListener implements EMGroupChangeListener {

		@Override
		public void onApplicationAccept(String arg0, String arg1, String arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onApplicationDeclined(String arg0, String arg1,
				String arg2, String arg3) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onApplicationReceived(String arg0, String arg1,
				String arg2, String arg3) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onGroupDestroy(String arg0, String arg1) {
			// TODO Auto-generated method stub
			refresh();
		}

		@Override
		public void onInvitationAccpted(String arg0, String arg1, String arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onInvitationDeclined(String arg0, String arg1, String arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onInvitationReceived(String arg0, String arg1, String arg2,
				String arg3) {
			// TODO Auto-generated method stub
			// 我被邀请进入群聊（直接进入）
			refresh();
		}

		@Override
		public void onUserRemoved(String groupId, String groupName) {
			// TODO Auto-generated method stub
			// 提示用户被T了，demo省略此步骤
		}

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		getITopicApplication().getMyUserBeanManager()
				.removeUserStateChangeListener(this);

		EMChatManager.getInstance().removeConnectionListener(this);
		if (groupChangeListener != null) {
			EMGroupManager.getInstance().removeGroupChangeListener(
					groupChangeListener);
		}

		super.onDestroy();
	}


	@Override
	public void onUserInfoChanged(UserBean ub) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUserLogin(UserBean ub) {
		// TODO Auto-generated method stub
		// 如果走的是回调，这里ub不可能是null
		// onCreated还可以主动调用这个方法，这时候可能是null
		if (ub == null) {
			conversationList.clear();
			adapter.notifyDataSetChanged();
		}
		getView().findViewById(R.id.empty_textview).setVisibility(
				ub == null ? View.VISIBLE : View.GONE);
	}

	@Override
	public void onUserLogout() {
		// TODO Auto-generated method stub
		conversationList.clear();
		adapter.notifyDataSetChanged();
		getView().findViewById(R.id.empty_textview).setVisibility(View.VISIBLE);
	}

	@Override
	public void onConnected() {
		// TODO Auto-generated method stub
		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				getView().findViewById(R.id.rl_error_item).setVisibility(View.GONE);
			}
		});
	}

	@Override
	public void onConnecting(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDisConnected(String arg0) {
		// TODO Auto-generated method stub
		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (getITopicApplication().getMyUserBeanManager().getInstance() != null) {
					// 自己的逻辑是处于登录状态。但是环信DisConnected
					getView().findViewById(R.id.rl_error_item)
							.setVisibility(View.VISIBLE);
					TextView tv_connect_errormsg = (TextView) getView().findViewById(R.id.tv_connect_errormsg);
					if (NetUtils.hasNetwork(getActivity())) {
						String st1 = getResources().getString(
								R.string.Less_than_chat_server_connection);
						tv_connect_errormsg.setText(st1);
					} else {
						String st2 = getResources().getString(
								R.string.the_current_network);
						tv_connect_errormsg.setText(st2);
					}
				} else {
					// 账号未登录
					getView().findViewById(R.id.rl_error_item).setVisibility(View.GONE);
				}

			}
		});
	}

	@Override
	public void onReConnected() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onReConnecting() {
		// TODO Auto-generated method stub

	}

}