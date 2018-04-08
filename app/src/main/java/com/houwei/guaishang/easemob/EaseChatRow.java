package com.houwei.guaishang.easemob;

import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.EMMessage.Direct;
import com.houwei.guaishang.R;
import com.houwei.guaishang.activity.BaseActivity;
import com.houwei.guaishang.activity.HisRootActivity;
import com.houwei.guaishang.bean.AvatarBean;
import com.houwei.guaishang.bean.UserBean;
import com.houwei.guaishang.easemob.EaseChatMessageList.MessageListItemClickListener;
import com.houwei.guaishang.tools.JsonParser;
import com.houwei.guaishang.tools.LogUtil;
import com.easemob.util.DateUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

public abstract class EaseChatRow extends LinearLayout {
    protected static final String TAG = EaseChatRow.class.getSimpleName();

    protected LayoutInflater inflater;
    protected Context context;
    protected EaseMessageAdapter adapter;
    protected EMMessage message;
    protected int position;

    protected TextView timeStampView;
    protected ImageView userAvatarView;
    protected View bubbleLayout;

    protected TextView percentageView;
    protected ProgressBar progressBar;
    protected ImageView statusView;
    protected BaseActivity activity;

    protected TextView ackedView;
    protected TextView deliveredView;
    private TextView name_tv;

    protected EMCallBack messageSendCallback;
    protected EMCallBack messageReceiveCallback;

    protected MessageListItemClickListener itemClickListener;

    public EaseChatRow(Context context, EMMessage message, int position, EaseMessageAdapter adapter) {
        super(context);
        this.context = context;
        this.activity = (BaseActivity) context;
        this.message = message;
        this.position = position;
        this.adapter = adapter;
        inflater = LayoutInflater.from(context);

        initView();
    }

    private void initView() {
        onInflatView();
        timeStampView = (TextView) findViewById(R.id.timestamp);
        userAvatarView = (ImageView) findViewById(R.id.iv_userhead);
        bubbleLayout = findViewById(R.id.bubble);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        statusView = (ImageView) findViewById(R.id.msg_status);
        ackedView = (TextView) findViewById(R.id.tv_ack);
        deliveredView = (TextView) findViewById(R.id.tv_delivered);
        name_tv = (TextView) findViewById(R.id.name_tv);
        
        onFindViewById();
    }

    /**
     * 根据当前message和position设置控件属性等
     * 
     * @param message
     * @param position
     */
    public void setUpView(EMMessage message, int position,
            EaseChatMessageList.MessageListItemClickListener itemClickListener) {
        this.message = message;
        this.position = position;
        this.itemClickListener = itemClickListener;

        setUpBaseView();
        onSetUpView();
        setClickListener();
    }

    private void setUpBaseView() {
        // 设置用户昵称头像，bubble背景等
        TextView timestamp = (TextView) findViewById(R.id.timestamp);
        if (timestamp != null) {
            if (position == 0) {
                timestamp.setText(DateUtils.getTimestampString(new Date(message.getMsgTime())));
                timestamp.setVisibility(View.VISIBLE);
            } else {
                // 两条消息时间离得如果稍长，显示时间
                EMMessage prevMessage = (EMMessage) adapter.getItem(position - 1);
                if (prevMessage != null && DateUtils.isCloseEnough(message.getMsgTime(), prevMessage.getMsgTime())) {
                    timestamp.setVisibility(View.GONE);
                } else {
                    timestamp.setText(DateUtils.getTimestampString(new Date(message.getMsgTime())));
                    timestamp.setVisibility(View.VISIBLE);
                }
            }
        }
        //设置头像和nick
        setUserAvatar(message, userAvatarView, name_tv);
        
        if(deliveredView != null){
            if (message.isDelivered) {
                deliveredView.setVisibility(View.VISIBLE);
            } else {
                deliveredView.setVisibility(View.INVISIBLE);
            }
        }
        
        if(ackedView != null){
            if (message.isAcked) {
                if (deliveredView != null) {
                    deliveredView.setVisibility(View.INVISIBLE);
                }
                ackedView.setVisibility(View.VISIBLE);
                if(message.getBooleanAttribute(EaseConstant.EASE_ATTR_READFIRE, false)){
                    // 因为当某一条消息不在内存中时，removeMessage方法无效，所以在当前聊天界面显示消息的时候去判断此消息是否是阅后即焚类型，并且已读，这样来进行删除
                    EMChatManager.getInstance().getConversation(message.getTo()).removeMessage(message.getMsgId());
                    onUpdateView();
                }
            } else {
                ackedView.setVisibility(View.INVISIBLE);
            }
        }
        

        if (adapter instanceof EaseMessageAdapter) {
            if (((EaseMessageAdapter) adapter).isShowAvatar())
                userAvatarView.setVisibility(View.VISIBLE);
            else
                userAvatarView.setVisibility(View.GONE);
            if (message.direct == Direct.SEND) {
                if (((EaseMessageAdapter) adapter).getMyBubbleBg() != null)
                    bubbleLayout.setBackgroundDrawable(((EaseMessageAdapter) adapter).getMyBubbleBg());
                // else
                // bubbleLayout.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.chatto_bg));
            } else if (message.direct == Direct.RECEIVE) {
                if (((EaseMessageAdapter) adapter).getOtherBuddleBg() != null)
                    bubbleLayout.setBackgroundDrawable(((EaseMessageAdapter) adapter).getOtherBuddleBg());
//                else
//                    bubbleLayout.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.ease_chatfrom_bg));
            }
        }
    }

    /**
     * 设置消息发送callback
     */
    protected void setMessageSendCallback(){
        if(messageSendCallback == null){
            messageSendCallback = new EMCallBack() {
                
                @Override
                public void onSuccess() {
                    updateView();
                }
                
                @Override
                public void onProgress(final int progress, String status) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(percentageView != null)
                                percentageView.setText(progress + "%");

                        }
                    });
                }
                
                @Override
                public void onError(int code, String error) {
                    updateView();
                }
            };
        }
        message.setMessageStatusCallback(messageSendCallback);
    }
    
    /**
     * 设置消息接收callback
     */
    protected void setMessageReceiveCallback(){
        if(messageReceiveCallback == null){
            messageReceiveCallback = new EMCallBack() {
                
                @Override
                public void onSuccess() {
                    updateView();
                }
                
                @Override
                public void onProgress(final int progress, String status) {
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            if(percentageView != null){
                                percentageView.setText(progress + "%");
                            }
                        }
                    });
                }
                
                @Override
                public void onError(int code, String error) {
                    updateView();
                }
            };
        }
        message.setMessageStatusCallback(messageReceiveCallback);
    }
    
    
    private void setClickListener() {
        if(bubbleLayout != null){
            bubbleLayout.setOnClickListener(new OnClickListener() {
    
                @Override
                public void onClick(View v) {
                	LogUtil.e("BaseRow onBubbleClick 0");
                    if (itemClickListener != null){
                        if(!itemClickListener.onBubbleClick(message)){
                            //如果listener返回false不处理这个事件，执行lib默认的处理
                            onBubbleClick();
                        }
                    }
                }
            });
    
            bubbleLayout.setOnLongClickListener(new OnLongClickListener() {
    
                @Override
                public boolean onLongClick(View v) {
                	LogUtil.e("ROW onBubbleLongClick 0 ");
                    if (itemClickListener != null) {
                    	LogUtil.e("ROW onBubbleLongClick 1 ");
                        itemClickListener.onBubbleLongClick(message);
                    }
                    return true;
                }
            });
        }

        if (statusView != null) {
            statusView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.onResendClick(message);
                    }
                }
            });
        }

    }


    protected void updateView() {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                if (message.status == EMMessage.Status.FAIL) {

                    if (message.getError() == EMError.MESSAGE_SEND_INVALID_CONTENT) {
                        Toast.makeText(activity,activity.getString(R.string.send_fail) + activity.getString(R.string.error_send_invalid_content), 0).show();
                    } else if (message.getError() == EMError.MESSAGE_SEND_NOT_IN_THE_GROUP) {
                        Toast.makeText(activity,activity.getString(R.string.send_fail) + activity.getString(R.string.error_send_not_in_the_group), 0).show();
                    } else {
                        Toast.makeText(activity,activity.getString(R.string.send_fail) + activity.getString(R.string.connect_failuer_toast), 0).show();
                    }
                }

                onUpdateView();
            }
        });

    }

    
	/**
	 * 显示用户头像
	 * 
	 * @param message
	 * @param imageView
	 */
	private void setUserAvatar(final EMMessage message, ImageView imageView, TextView name_tv) {
		
		if (name_tv != null) { //如果是我发的消息（右气泡，name_tv为null）
			if (adapter.isGroup()) {
				//群消息显示姓名
				name_tv.setVisibility(View.VISIBLE);
				name_tv.setText(""+message.getStringAttribute(
						HisRootActivity.SENDER_NAME_KEY, ""));
			}else{
				name_tv.setVisibility(View.GONE);
			}
		}
		
		if (message.direct == Direct.SEND) {
			// 显示自己头像
			ImageLoader.getInstance().displayImage(adapter.getMyAvatarURL(), imageView);
			// 环信官方提供 itemClickListener.onUserAvatarClick(message.getFrom());我们不用
			imageView.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					UserBean mineBean = activity.getITopicApplication().getMyUserBeanManager().getInstance();
					activity.jumpToHisInfoActivity(mineBean.getUserid(), mineBean.getName(), mineBean.getAvatar());
			
				}
			});
		} else {
			//别人发的，气泡在左边
			if (message.getChatType() == ChatType.Chat ){
				//单聊
				ImageLoader.getInstance().displayImage(adapter.getHisAvatarBean().findSmallUrl(),
						imageView);	
				imageView.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						activity.jumpToHisInfoActivity(adapter.getHisUserID(), adapter.getHisRealName(), adapter.getHisAvatarBean());
					}
				});
			}else{

				//群聊，每次都解析
				final AvatarBean memberAvatarBean = JsonParser.getAvatarBean(message
						.getStringAttribute(HisRootActivity.SENDER_AVATAR_KEY, ""));
				
				ImageLoader.getInstance().displayImage(memberAvatarBean.findSmallUrl(),
						imageView);	
				imageView.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						activity.jumpToHisInfoActivity(message.getFrom(), message.getStringAttribute(HisRootActivity.SENDER_NAME_KEY, ""), memberAvatarBean);
					}
				});
				
			}

		}
	}

    
    /**
     * 填充layout
     */
    protected abstract void onInflatView();

    /**
     * 查找chatrow里的控件
     */
    protected abstract void onFindViewById();

    /**
     * 消息状态改变，刷新listview
     */
    protected abstract void onUpdateView();

    /**
     * 设置更新控件属性
     */
    protected abstract void onSetUpView();
    
    /**
     * 聊天气泡被点击事件
     */
    protected abstract void onBubbleClick();

}
