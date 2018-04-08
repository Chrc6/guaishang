package com.houwei.guaishang.activity;

import com.houwei.guaishang.R;
import com.houwei.guaishang.bean.VersionResponse.VersionBean;
import com.houwei.guaishang.easemob.DemoModel;
import com.houwei.guaishang.manager.VersionManager.LastVersion;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.houwei.guaishang.tools.ShareSDKUtils;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class MineSystemActivity extends BaseActivity implements OnClickListener, LastVersion {

	private ImageView sound_iv,rock_iv;
	private DemoModel model;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		initView();
		initListener();
	}

	private void initListener() {
		// TODO Auto-generated method stub
		BackButtonListener();
		getITopicApplication().getVersionManager().setOnLastVersion(this);
		findViewById(R.id.exit_btn).setOnClickListener(this);
		
		findViewById(R.id.setting_sound_ll).setOnClickListener(this);
		findViewById(R.id.setting_rock_ll).setOnClickListener(this);
		
		findViewById(R.id.backlist_ll).setOnClickListener(this);
		findViewById(R.id.setting_ll3).setOnClickListener(this);
		findViewById(R.id.setting_opinion).setOnClickListener(this);
		findViewById(R.id.setting_ll5).setOnClickListener(this);

	}

	private void initView() {
		sound_iv = (ImageView)findViewById(R.id.sound_iv); 
		rock_iv = (ImageView)findViewById(R.id.rock_iv);
		
		model = getITopicApplication().getHuanXinManager().getHxSDKHelper().getModel();
		
		// 是否打开声音
		if (model.getSettingMsgSound()) {
			sound_iv.setBackgroundResource(R.drawable.check_round_checked);
		} else {
			sound_iv.setBackgroundResource(R.drawable.check_round_uncheck);
		}
		
		// 是否打开震动
		// vibrate notification is switched on or not?
		if (model.getSettingMsgVibrate()) {
			rock_iv.setBackgroundResource(R.drawable.check_round_checked);
		} else {
			rock_iv.setBackgroundResource(R.drawable.check_round_uncheck);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent i = null;
		switch (v.getId()) {
	
		case R.id.exit_btn:
			getITopicApplication().getMyUserBeanManager().clean();
			ShareSDKUtils.removeAccount();
			finish();
			break;
		case R.id.setting_sound_ll:{
			EMChatOptions chatOptions = EMChatManager.getInstance().getChatOptions();
			
			if (model.getSettingMsgSound()) {
				chatOptions.setNoticeBySound(false);
				EMChatManager.getInstance().setChatOptions(chatOptions);
				model.setSettingMsgSound(false);
				sound_iv.setBackgroundResource(R.drawable.check_round_uncheck);
			}else{
				chatOptions.setNoticeBySound(true);
				EMChatManager.getInstance().setChatOptions(chatOptions);
				model.setSettingMsgSound(true);
				sound_iv.setBackgroundResource(R.drawable.check_round_checked);
			}
		}
			break;
		case R.id.setting_rock_ll:
		{
			EMChatOptions chatOptions = EMChatManager.getInstance().getChatOptions();
			
			if (model.getSettingMsgVibrate()) {
				chatOptions.setNoticedByVibrate(false);
				EMChatManager.getInstance().setChatOptions(chatOptions);
				model.setSettingMsgVibrate(false);
				rock_iv.setBackgroundResource(R.drawable.check_round_uncheck);
			}else{
				chatOptions.setNoticedByVibrate(true);
				EMChatManager.getInstance().setChatOptions(chatOptions);
				model.setSettingMsgVibrate(true);
				rock_iv.setBackgroundResource(R.drawable.check_round_checked);
			}
		}
			break;
		case R.id.backlist_ll:
			if (checkLogined()) {
				 i = new Intent(MineSystemActivity.this, BlacklistActivity.class);
				 startActivity(i);
			}
			break;
		case R.id.setting_ll3:
			getITopicApplication().getVersionManager().checkNewVersion();
			break;
	
		case R.id.setting_ll5:
			//关于我们界面，可以做native的（跳转AboutUsActivity已经做好了），缺点是文字内容（比如联系方式）改了，旧版本用户不知道
			 //推荐自己的服务器做个html5
			//现在暂时采用 “易企秀” 做的html5模板，优点是界面酷炫，缺点是只能是他们的链接，拿不到html源码
			 i = new Intent(MineSystemActivity.this, AboutUsActivity.class);
			 i.putExtra("url", "http://u.eqxiu.com/s/Iqu2BV0q");
			 startActivity(i);
			
			break;
		case R.id.setting_opinion:
			 i = new Intent(MineSystemActivity.this, WarningReportActivity.class);
			 startActivity(i);
			break;
			
		default:
			break;
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		getITopicApplication().getVersionManager().removeListener(this);
		super.onDestroy();
	}

	

	@Override
	public void isLastVersion() {
		// TODO Auto-generated method stub
		showErrorToast("已经是最新版本了");
	}

	@Override
	public void versionNetworkFail(String message) {
		// TODO Auto-generated method stub
		showErrorToast();
	}

	@Override
	public void notLastVersion(VersionBean versionBean) {
		// TODO Auto-generated method stub
		getITopicApplication().getVersionManager().downLoadNewVersion(versionBean, this);
	}
	
}
