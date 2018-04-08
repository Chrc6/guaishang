package com.houwei.guaishang.activity;


import java.util.Timer;
import java.util.TimerTask;

import com.houwei.guaishang.R;
import com.houwei.guaishang.manager.ITopicApplication;
import com.umeng.analytics.MobclickAgent;



import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
public class WelcomeActivity extends Activity {
	
	private boolean isFirst;
	private	SharedPreferences.Editor firstEditor;
	private Handler handler = new Handler();
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		SharedPreferences FirstPref = getSharedPreferences("FIRST", Activity.MODE_PRIVATE);
		firstEditor = FirstPref.edit();
		isFirst = FirstPref.getBoolean("FIRST", true);
		
		handler.postDelayed(initRun, 150);

	}

	private Runnable initRun = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			getITopicApplication().checkInit();
			Timer timer = new Timer();
			TimerTask timerTask = new TimerTask() {
				@Override
				public void run() {
					launchHome();
				}
			};
			timer.schedule(timerTask, 1300); 
		}
	};
	
	
	private void launchHome() {
		// MainActivity TicketRootGroup
		Intent iLogin = new Intent();

		
		//默认跳转到第几个tab
		iLogin.putExtra("relink",getIntent().getIntExtra("relink", -1));
		iLogin.setClass(WelcomeActivity.this, MainActivity.class);
		
		startActivity(iLogin);
		finish();
	}


	public ITopicApplication getITopicApplication() {
		return (ITopicApplication) getApplication();
	}
	

}
