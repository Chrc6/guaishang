package com.houwei.guaishang.activity;

import java.io.File;
import java.io.FileOutputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Toast;

import com.houwei.guaishang.R;
import com.houwei.guaishang.easemob.EaseConstant;
import com.houwei.guaishang.easemob.EaseCustomChatRowProvider;
import com.houwei.guaishang.layout.MenuTwoButtonDialog;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.PathUtil;

/**
 * 聊天页面
 */
public class ChatEaseActivity extends ChatBaseActivity implements ChatBaseActivity.EaseChatFragmentListener{
  

	
	private static final int REQUEST_CODE_GROUP_DETAIL = 21;
	
    @Override
	protected void initView(){
    	super.initView();
        setChatFragmentListener(this);
    }
    
    @Override
    protected void registerExtendMenuItem() {
        //demo这里不覆盖基类已经注册的item,item点击listener沿用基类的
        super.registerExtendMenuItem();
        //增加扩展item
		if (chatType == Constant.CHATTYPE_SINGLE) {
			// 阅后即焚开关菜单
			inputMenu.registerExtendMenuItem(R.string.attach_read_fire,
					R.drawable.message_read_fire, ITEM_READFIRE,
					extendMenuItemClickListener);
		}
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    	if (resultCode == GroupDetailsActivity.GROUP_DELETE_RESPONSE) {
    		//删除群组
			setResult(RESULT_OK);
			finish();
			return;
		}
    }
    
    @Override
    public void onSetMessageAttributes(EMMessage message) {
        // 根据当前状态是否是阅后即焚状态来设置发送消息的扩展
        if (isReadFire && (message.getType() == EMMessage.Type.TXT)
				&& message.getStringAttribute(EaseConstant.MESSAGE_ATTR_EXPRESSION_ID, null)==null) {
			//gif大表情消息，不支持阅后即焚
			 message.setAttribute(EaseConstant.EASE_ATTR_READFIRE, true);
		}
        if(isReadFire && (message.getType() == EMMessage.Type.IMAGE
                || message.getType() == EMMessage.Type.VOICE)){
            message.setAttribute(EaseConstant.EASE_ATTR_READFIRE, true);
        }
    }
    
    @Override
    public EaseCustomChatRowProvider onSetCustomChatRowProvider() {
        //设置自定义listview item提供者
        return null;
    }
  

    @Override
    public void onEnterToChatDetails() {
        if (chatType == Constant.CHATTYPE_GROUP) {
        	startActivityForResult((new Intent(this, GroupDetailsActivity.class).putExtra("groupId", getToChatUsername())),
					REQUEST_CODE_GROUP_DETAIL);
        }else if(chatType == Constant.CHATTYPE_CHATROOM){
        }
    }

    @Override
    public void onAvatarClick(String username) {
        //头像点击事件，DQ在adapter里已经处理过了，这里不要重写
    }
    
    @Override
    public boolean onMessageBubbleClick(EMMessage message) {
        //消息框点击事件，demo这里不做覆盖，如需覆盖，return true
        return false;
    }

    @Override
    public void onMessageBubbleLongClick(final EMMessage message) {
        //消息框长按
    	MenuTwoButtonDialog dialog = new MenuTwoButtonDialog(ChatEaseActivity.this, new MenuTwoButtonDialog.ButtonClick() {
			
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
}
