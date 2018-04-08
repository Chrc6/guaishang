package com.houwei.guaishang.activity;

import java.util.HashMap;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.houwei.guaishang.R;

public class ConversationActivity extends BaseActivity {
	

	private ConversationFragment conversationFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_framelayout);
		initView();
		initListener();
	}

	protected void initListener() {
		// TODO Auto-generated method stub

	}

	protected void initView() {
		// TODO Auto-generated method stub
		conversationFragment = new ConversationFragment();
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.add(R.id.container,conversationFragment);    
		transaction.commit();
	}
}
