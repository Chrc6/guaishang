/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.houwei.guaishang.easemob;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.easemob.chat.EMContact;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.EMConversation.EMConversationType;
import com.easemob.chat.EMMessage.Direct;
import com.easemob.util.DateUtils;
import com.houwei.guaishang.R;
import com.houwei.guaishang.activity.Constant;
import com.houwei.guaishang.activity.HisRootActivity;
import com.houwei.guaishang.bean.AvatarBean;
import com.houwei.guaishang.manager.OtherManager;
import com.houwei.guaishang.tools.HttpUtil;
import com.houwei.guaishang.tools.JsonParser;
import com.houwei.guaishang.view.CircleImageView;
import com.houwei.guaishang.view.CircleImageView2;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 显示所有聊天记录adpater
 * 
 */
public class ChatAllHistoryAdapter extends ArrayAdapter<EMConversation> {

	private LayoutInflater inflater;
	private List<EMConversation> conversationList;
	private List<EMConversation> copyConversationList;
	private ConversationFilter conversationFilter;
    private boolean notiyfyByFilter;

	public ChatAllHistoryAdapter(Context context, int textViewResourceId, List<EMConversation> objects) {
		super(context, textViewResourceId, objects);
		this.conversationList = objects;
		copyConversationList = new ArrayList<EMConversation>();
		copyConversationList.addAll(objects);
		inflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.row_chat_history, parent, false);
		}
		ViewHolder holder = (ViewHolder) convertView.getTag();
		if (holder == null) {
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.unreadLabel = (TextView) convertView.findViewById(R.id.unread_msg_number);
			holder.message = (TextView) convertView.findViewById(R.id.message);
			holder.time = (TextView) convertView.findViewById(R.id.time);
			holder.avatar = (CircleImageView2) convertView.findViewById(R.id.avatar);
			holder.msgState = convertView.findViewById(R.id.msg_state);
			holder.list_item_layout = (RelativeLayout) convertView.findViewById(R.id.list_item_layout);
			convertView.setTag(holder);
		}

		// 获取与此用户/群组的会话
		EMConversation conversation = getItem(position);

		if (conversation.getUnreadMsgCount() > 0 && !conversation.getUserName().equals(EaseConstant.NOTICE_USERID)) {
			// 显示与此用户的消息未读数
			holder.unreadLabel.setText(String.valueOf(conversation.getUnreadMsgCount()));
			holder.unreadLabel.setVisibility(View.VISIBLE);
		} else {
			holder.unreadLabel.setVisibility(View.INVISIBLE);
		}

		if (conversation.getMsgCount() != 0) {
			// 把最后一条消息的内容作为item的message内容
			EMMessage lastMessage = conversation.getLastMessage();
			
			// 这里判断下当前消息是否为阅后即焚类型，如果是并且同时是最后一条消息，在会话列表就要替换下消息显示内容
			if(lastMessage.getBooleanAttribute(EaseConstant.EASE_ATTR_READFIRE, false)
					&& lastMessage.direct == Direct.RECEIVE){
				holder.message.setText(R.string.readfire_message);
			}else{
				holder.message.setText(getMessageDigest(lastMessage, (this.getContext())));
			}
			


			holder.time.setText(DateUtils.getTimestampString(new Date(lastMessage.getMsgTime())));
			if (lastMessage.direct == EMMessage.Direct.SEND && lastMessage.status == EMMessage.Status.FAIL) {
				holder.msgState.setVisibility(View.VISIBLE);
			} else {
				holder.msgState.setVisibility(View.GONE);
			}
			
			if (conversation.getType() == EMConversationType.GroupChat) {
				//这是一条群组消息
				EMGroup group = EMGroupManager.getInstance().getGroup(conversation.getUserName());
				if (group!=null) {
					holder.name.setText(group.getName());
					ImageLoader.getInstance().displayImage(HttpUtil.IP_NOAPI+group.getDescription(),holder.avatar);
				}else{
					holder.name.setText("群组");
					holder.avatar.setImageResource(R.drawable.group_avator_default);
				}
			}else{
				//看第一条对话信息，是我主动发送创建的，来说过来的
//				EMMessage firstMessage  = conversation.getAllMessages().get(0);
				if (lastMessage.direct == EMMessage.Direct.SEND ){
					//是我发出的,这里头像应该显示是接收人
					holder.name.setText(lastMessage.getStringAttribute(HisRootActivity.RECEIVER_NAME_KEY, ""));
					AvatarBean bean = JsonParser.getAvatarBean(lastMessage.getStringAttribute(HisRootActivity.RECEIVER_AVATAR_KEY, ""));
					
					if (bean != null) {
						ImageLoader.getInstance().displayImage(bean.findSmallUrl(),holder.avatar);
					}else{
						holder.avatar.setImageResource(R.drawable.user_photo);
					}
				} else {
					//是我收到的，这里头像应该是发送人
					holder.name.setText(lastMessage.getStringAttribute(HisRootActivity.SENDER_NAME_KEY, ""));
					AvatarBean bean = JsonParser.getAvatarBean(lastMessage.getStringAttribute(HisRootActivity.SENDER_AVATAR_KEY, ""));
					
					if (bean != null) {
						ImageLoader.getInstance().displayImage(bean.findSmallUrl(),holder.avatar);
					}else{
						holder.avatar.setImageResource(R.drawable.user_photo);
					}
				}
			}
			
		
		}

		return convertView;
	}

	/**
	 * 根据消息内容和消息类型获取消息内容提示
	 * 
	 * @param message
	 * @param context
	 * @return
	 */
	private String getMessageDigest(EMMessage message, Context context) {
		String digest = "";
		switch (message.getType()) {
		case LOCATION: // 位置消息
			if (message.direct == EMMessage.Direct.RECEIVE) {
				// 从sdk中提到了ui中，使用更简单不犯错的获取string的方法
				// digest = EasyUtils.getAppResourceString(context,
				// "location_recv");
				digest = getContextString(context, R.string.location_recv);
				digest = String.format(digest, message.getStringAttribute(
						HisRootActivity.SENDER_NAME_KEY, ""));
				return digest;
			} else {
				// digest = EasyUtils.getAppResourceString(context,
				// "location_prefix");
				digest = getContextString(context, R.string.location_prefix);
			}
			break;
		case IMAGE: // 图片消息
			digest = getContextString(context, R.string.picture);
			break;
		case VOICE:// 语音消息
			digest = getContextString(context, R.string.voice);
			break;
		case VIDEO: // 视频消息
			digest = getContextString(context, R.string.video);
			break;
		case TXT: // 文本消息
			if(!message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL,false)){
				TextMessageBody txtBody = (TextMessageBody) message.getBody();
				digest = txtBody.getMessage();
			}else{
				TextMessageBody txtBody = (TextMessageBody) message.getBody();
				digest = getContextString(context, R.string.voice_call) + txtBody.getMessage();
			}
			break;
		case FILE: // 普通文件消息
			digest = getContextString(context, R.string.file);
			break;
		default:
			System.err.println("error, unknow type");
			return "";
		}

		return digest;
	}

	private static class ViewHolder {
		/** 和谁的聊天记录 */
		TextView name;
		/** 消息未读数 */
		TextView unreadLabel;
		/** 最后一条消息的内容 */
		TextView message;
		/** 最后一条消息的时间 */
		TextView time;
		/** 用户头像 */
		CircleImageView2 avatar;
		/** 最后一条消息的发送状态 */
		View msgState;
		/** 整个list中每一行总布局 */
		RelativeLayout list_item_layout;

	}

	String getContextString(Context context, int resId) {
		return context.getResources().getString(resId);
	}
	
	

	@Override
	public Filter getFilter() {
		if (conversationFilter == null) {
			conversationFilter = new ConversationFilter(conversationList);
		}
		return conversationFilter;
	}
	
	private class ConversationFilter extends Filter {
		List<EMConversation> mOriginalValues = null;

		public ConversationFilter(List<EMConversation> mList) {
			mOriginalValues = mList;
		}

		@Override
		protected FilterResults performFiltering(CharSequence prefix) {
			FilterResults results = new FilterResults();

			if (mOriginalValues == null) {
				mOriginalValues = new ArrayList<EMConversation>();
			}
			if (prefix == null || prefix.length() == 0) {
				results.values = copyConversationList;
				results.count = copyConversationList.size();
			} else {
				String prefixString = prefix.toString();
				final int count = mOriginalValues.size();
				final ArrayList<EMConversation> newValues = new ArrayList<EMConversation>();

				for (int i = 0; i < count; i++) {
					final EMConversation value = mOriginalValues.get(i);
					String username = value.getUserName();
					
					EMGroup group = EMGroupManager.getInstance().getGroup(username);
					if(group != null){
						username = group.getGroupName();
					}

					// First match against the whole ,non-splitted value
					if (username.startsWith(prefixString)) {
						newValues.add(value);
					} else{
						  final String[] words = username.split(" ");
	                        final int wordCount = words.length;

	                        // Start at index 0, in case valueText starts with space(s)
	                        for (int k = 0; k < wordCount; k++) {
	                            if (words[k].startsWith(prefixString)) {
	                                newValues.add(value);
	                                break;
	                            }
	                        }
					}
				}

				results.values = newValues;
				results.count = newValues.size();
			}
			return results;
		}

		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			conversationList.clear();
			conversationList.addAll((List<EMConversation>) results.values);
			if (results.count > 0) {
			    notiyfyByFilter = true;
				notifyDataSetChanged();
			} else {
				notifyDataSetInvalidated();
			}

		}

	}
	
	@Override
	public void notifyDataSetChanged() {
	    super.notifyDataSetChanged();
	    if(!notiyfyByFilter){
            copyConversationList.clear();
            copyConversationList.addAll(conversationList);
            notiyfyByFilter = false;
        }
	}
}
