package com.houwei.guaishang.easemob;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.houwei.guaishang.R;
import com.houwei.guaishang.bean.AvatarBean;
import com.houwei.guaishang.manager.FaceManager;

public class EaseChatMessageList extends RelativeLayout{
    
    protected static final String TAG = "EaseChatMessageList";
    protected ListView listView;
    protected SwipeRefreshLayout swipeRefreshLayout;
    protected Context context;
    protected EMConversation conversation;
    protected int chatType;
    protected String toChatUsername;
    protected EaseMessageAdapter messageAdapter;
    protected boolean showUserNick;
    protected boolean showAvatar;
    protected Drawable myBubbleBg;
    protected Drawable otherBuddleBg;

	public EaseChatMessageList(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs);
    }

    public EaseChatMessageList(Context context, AttributeSet attrs) {
    	super(context, attrs);
    	parseStyle(context, attrs);
    	init(context);
    }

    public EaseChatMessageList(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context){
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.ease_chat_message_list, this);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.chat_swipe_layout);
        listView = (ListView) findViewById(R.id.listview);
        
        listView.addFooterView(LayoutInflater.from(context).inflate(R.layout.empty_footview, null));
    }
    
    

    
    /**
     * init widget
     * @param toChatUsername
     * @param chatType
     * @param customChatRowProvider
     */
    public void init(String toChatUsername, int chatType, EaseCustomChatRowProvider customChatRowProvider) {
        this.chatType = chatType;
        this.toChatUsername = toChatUsername;
        
        conversation = EMChatManager.getInstance().getConversation(toChatUsername);
        messageAdapter = new EaseMessageAdapter(context, toChatUsername, chatType, listView);
        messageAdapter.setShowAvatar(showAvatar);
        messageAdapter.setShowUserNick(showUserNick);
        messageAdapter.setMyBubbleBg(myBubbleBg);
        messageAdapter.setOtherBuddleBg(otherBuddleBg);
        messageAdapter.setCustomChatRowProvider(customChatRowProvider);
      
    }
    
    
    /**
     * iTopic DQ  2016-03-08
     * 由ChatBaseActivity调用
     * 由于Adapter 需要很多对象，必须从ChatBaseActivity获取。直接修改init方法添加参数也不太好，所以放这里传参
     */
    public void setFaceManager(FaceManager faceManager){
    	messageAdapter.setFaceManager(faceManager);
    }
    
    //dq，添加我们自己的自定义参数
	public void setChaterInfo(String hisUserID,String hisRealName,AvatarBean hisAvatarBean,String myAvatarURL,boolean isGroup) {
		messageAdapter.setChaterInfo(hisUserID, hisRealName, hisAvatarBean, myAvatarURL, isGroup);
	}

    
    //dq，原来的demo里，这2代码是在init里的。但是我要自定义参数，调用方法setChaterInfo，不得不这样搞
    public void setAdapterAndSelectLast(){
    	  listView.setAdapter(messageAdapter);
          refreshSelectLast();
    }
    
    
    protected void parseStyle(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.EaseChatMessageList);
        showAvatar = ta.getBoolean(R.styleable.EaseChatMessageList_msgListShowUserAvatar, true);
        myBubbleBg = ta.getDrawable(R.styleable.EaseChatMessageList_msgListMyBubbleBackground);
        otherBuddleBg = ta.getDrawable(R.styleable.EaseChatMessageList_msgListMyBubbleBackground);
        showUserNick = ta.getBoolean(R.styleable.EaseChatMessageList_msgListShowUserNick, false);
        ta.recycle();
    }
    
    
    /**
     * 刷新列表
     */
    public void refresh(){
        messageAdapter.refresh();
    }
    
    /**
     * 刷新列表，并且跳至最后一个item
     */
    public void refreshSelectLast(){
        messageAdapter.refreshSelectLast();
    }
    
    /**
     * 刷新页面,并跳至给定position
     * @param position
     */
    public void refreshSeekTo(int position){
        messageAdapter.refreshSeekTo(position);;
    }
    
    

    /**
     * 获取listview
     * @return
     */
	public ListView getListView() {
		return listView;
	} 
	
	/**
	 * 获取SwipeRefreshLayout
	 * @return
	 */
	public SwipeRefreshLayout getSwipeRefreshLayout(){
	    return swipeRefreshLayout;
	}
	
	public EMMessage getItem(int position){
	    return messageAdapter.getItem(position);
	}
	
	/**
	 * 设置是否显示用户昵称
	 * @param showUserNick
	 */
	public void setShowUserNick(boolean showUserNick){
	    this.showUserNick = showUserNick;
	}
	
	public boolean isShowUserNick(){
	    return showUserNick;
	}
	
	public interface MessageListItemClickListener{
	    void onResendClick(EMMessage message);
	    /**
	     * 控件有对气泡做点击事件默认实现，如果需要自己实现，return true。
	     * 当然也可以在相应的chatrow的onBubbleClick()方法里实现点击事件
	     * @param message
	     * @return
	     */
	    boolean onBubbleClick(EMMessage message);
	    void onBubbleLongClick(EMMessage message);
	    void onUserAvatarClick(String username);
	}
	
	/**
	 * 设置list item里控件的点击事件
	 * @param listener
	 */
	public void setItemClickListener(MessageListItemClickListener listener){
	    messageAdapter.setItemClickListener(listener);
	}
	
	/**
	 * 设置自定义chatrow提供者
	 * @param rowProvider
	 */
	public void setCustomChatRowProvider(EaseCustomChatRowProvider rowProvider){
        messageAdapter.setCustomChatRowProvider(rowProvider);
    }
}
