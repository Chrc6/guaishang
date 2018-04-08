package com.houwei.guaishang.activity;

import android.content.Intent;

import com.houwei.guaishang.manager.HuanXinManager;
import com.houwei.guaishang.manager.MyUserBeanManager;
import com.houwei.guaishang.manager.HuanXinManager.HuanXinLoginListener;
import com.houwei.guaishang.manager.HuanXinManager.HuanXinRegListener;

public class UserRegInfoBaseActivity extends BasePhotoActivity implements HuanXinLoginListener, HuanXinRegListener {

	//登录环信
	public void loginHuanXinService(String id,String realName){
		 getITopicApplication().getHuanXinManager().loginHuanXinService(this, id,realName, this);
	}
	
	

	@Override
	public void onHuanXinRegSuccess() {
		// TODO Auto-generated method stub
		progress.dismiss();
		Intent i = new Intent(UserRegInfoBaseActivity.this,
				UserLoginActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(i);
		finish();
	}

	@Override
	public void onHuanXinRegFail(String message) {
		// TODO Auto-generated method stub
		progress.dismiss();
		showFailTips(message);
	}

	@Override
	public void onHuanXinLoginSuccess() {
		// TODO Auto-generated method stub
		progress.dismiss();
		Intent i = new Intent(UserRegInfoBaseActivity.this,
				MainActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(i);
		finish();
	}

	@Override
	public void onHuanXinLoginFail(int code, String message) {
		// TODO Auto-generated method stub
		progress.dismiss();
		//失败了也进去，再进入ChatActivity的时候再判断重新登录一下
		Intent i = new Intent(UserRegInfoBaseActivity.this,
				MainActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(i);
		finish();
	}

}
