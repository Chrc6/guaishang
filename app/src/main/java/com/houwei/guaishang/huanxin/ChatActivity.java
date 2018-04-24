package com.houwei.guaishang.huanxin;

import android.content.Intent;
import android.os.Bundle;

import com.houwei.guaishang.R;
import com.houwei.guaishang.activity.BaseActivity;
import com.houwei.guaishang.tools.ToastUtils;

/**
 * Created by lenovo on 2018/4/23.
    聊天页面
 */

public class ChatActivity extends BaseActivity{

    public static final String Chat_info = "charInfo";

    private ChatInfo chatInfo;
    private  ChatFragment chatFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);
        parseIntent();
        addFragment();
    }

    private void addFragment(){
        if (chatFragment == null) {
            chatFragment = ChatFragment.getInstance(chatInfo);
            getSupportFragmentManager().beginTransaction().replace(R.id.container, chatFragment).commitAllowingStateLoss();
        }
    }
    private void parseIntent(){
        Intent intent = getIntent();
        if (intent != null){
            chatInfo = (ChatInfo) intent.getSerializableExtra(Chat_info);
        }

        if (chatInfo == null){
            ToastUtils.toastForShort(this,"聊天对象不能为空");
            finish();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (chatFragment != null) {
            chatFragment.onNewIntent(intent);
        }
    }

    public void onNewIntentForFragment(Intent intent){
        super.onNewIntent(intent);
    }

    @Override
    public void onBackPressed() {
        if (chatFragment !=null){
            chatFragment.onBackPressed();
        }
    }
}
