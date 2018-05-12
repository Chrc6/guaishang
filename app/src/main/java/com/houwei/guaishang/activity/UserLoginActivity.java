package com.houwei.guaishang.activity;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import com.baidu.tts.tools.SharedPreferencesUtils;
import com.houwei.guaishang.R;
import com.houwei.guaishang.bean.UserResponse;
import com.houwei.guaishang.data.DBReq;
import com.houwei.guaishang.event.LoginSuccessEvent;
import com.houwei.guaishang.manager.HuanXinManager;
import com.houwei.guaishang.manager.MyUserBeanManager;
import com.houwei.guaishang.manager.HuanXinManager.HuanXinLoginListener;
import com.houwei.guaishang.sp.UserInfo;
import com.houwei.guaishang.sp.UserUtil;
import com.houwei.guaishang.tools.HttpUtil;
import com.houwei.guaishang.tools.JsonParser;
import com.houwei.guaishang.tools.PublicStaticData;
import com.houwei.guaishang.tools.ShareSDKUtils;
import com.houwei.guaishang.tools.Utils;
import com.houwei.guaishang.tools.ValueUtil;
import com.mob.tools.utils.SharePrefrenceHelper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import cn.jpush.android.api.JPushInterface;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;

public class UserLoginActivity extends BaseActivity implements HuanXinLoginListener {
	private EditText user_name_et, user_pw_et;
	
	private MyHandler handler = new MyHandler(this);
	private ImageView imageQQ;
	private ImageView imageWeibo;
	private ImageView imageWechat;

	public static class MyHandler extends Handler {
		
		private WeakReference<Context> reference;
		
	    public MyHandler(Context context) {
	    	reference = new WeakReference<Context>(context);
	    }
	    
	    @Override
		public void handleMessage(Message msg) {
	    	final UserLoginActivity activity = (UserLoginActivity) reference.get();
			if(activity == null){
				return;
			}
			switch (msg.what) {
			case NETWORK_SUCCESS_DATA_RIGHT:
				Log.d("CCC","-->"+msg.obj);
				UserResponse response = (UserResponse) msg.obj;
				if (response.isSuccess()) {
				
					// 保存用户信息并开启推送
					activity.getITopicApplication().getMyUserBeanManager()
							.storeUserInfoAndNotity(response.getData());

					// 展开数据库
					 DBReq.getInstence(activity.getITopicApplication());
				
					 activity.getITopicApplication().getHuanXinManager().loginHuanXinService(activity, response.getData().getUserid(),response.getData().getName(), activity);
					UserInfo info = new UserInfo();
					info.setUserId(response.getData().getUserid());
					info.setUserName(response.getData().getName());
					info.setAvatar(response.getData().getAvatar().findOriginalUrl());
					UserUtil.setUserInfo(info);
                    EventBus.getDefault().post(new LoginSuccessEvent());
				} else {
					activity.progress.dismiss();
					activity.showErrorToast(response.getMessage());
				}
				break;
			default:
				activity.progress.dismiss();
				activity.showErrorToast();
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
        if (!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().isRegistered(this);
        }
		initView();
		initListener();		
	}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
    }

    private void initView() {
		// TODO Auto-generated method stub


		initProgressDialog(false, null);
		user_name_et = (EditText) findViewById(R.id.username_et);
		user_pw_et = (EditText) findViewById(R.id.check_pw_et);
		imageQQ = (ImageView) findViewById(R.id.image_qq);
		imageWeibo = (ImageView) findViewById(R.id.image_weibo);
		imageWechat = (ImageView) findViewById(R.id.image_wechat);
		user_name_et.setText(getITopicApplication().getMyUserBeanManager().getMobile());
		if (!user_name_et.getText().toString().trim().equals("")) {
			user_pw_et.requestFocus();
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					showKeyboard(user_pw_et);
				}
			}, 150);
		} else{
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					showKeyboard(user_name_et);
				}
			}, 150);
		}
		
	}

	private Runnable run = new Runnable() {

		@SuppressWarnings("unchecked")
		public void run() {
			// TODO Auto-generated method stub
			UserResponse response = null;
			String loginUserName = user_name_et.getText().toString().trim();
			String password = user_pw_et.getText().toString().trim();
			try {
				Map<String, String> data = new HashMap<String, String>();
				data.put("mobile", loginUserName);
				data.put("password", password);
				response = JsonParser.getUserResponse(HttpUtil.postMsg(
						HttpUtil.getData(data), HttpUtil.IP + "user/login"));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (response != null) {
				handler.sendMessage(handler.obtainMessage(
						NETWORK_SUCCESS_DATA_RIGHT, response));
			} else {
				handler.sendEmptyMessage(NETWORK_FAIL);
			}
		}
	};

	private void initListener() {
		// TODO Auto-generated method stub
		BackButtonListener();
		Button login_login = (Button) findViewById(R.id.login_btn);
		login_login.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				hideKeyboard();
				progress.show();
				new Thread(run).start();
			}
		});
		
//		findViewById(R.id.lost_password_tv).setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
//				Intent i = new Intent(UserLoginActivity.this,
//						UserFindPasswordActivity.class);
//				startActivity(i);
//			}
//		});
		findViewById(R.id.image_qq).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				ShareSDKUtils utils=new ShareSDKUtils(UserLoginActivity.this,handler);
				utils.Login(QQ.NAME);
			}
		});
		findViewById(R.id.image_wechat).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				ShareSDKUtils utils=new ShareSDKUtils(UserLoginActivity.this,handler);
				utils.Login(Wechat.NAME);

			}
		});
		findViewById(R.id.image_weibo).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				ShareSDKUtils utils=new ShareSDKUtils(UserLoginActivity.this,handler);
				utils.Login(SinaWeibo.NAME);
			}
		});

		TextView go_reg_tv = (TextView) findViewById(R.id.regist_btn);
				go_reg_tv.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent(UserLoginActivity.this,
						UserRegMobileActivity.class);
				startActivity(i);
			}
		});
	}
	
	
	@Override
	public void onHuanXinLoginSuccess() {
		// TODO Auto-generated method stub
		progress.dismiss();
		// 进入主页面
		Intent i = new Intent(UserLoginActivity.this,
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
		Intent i = new Intent(UserLoginActivity.this,
				MainActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(i);
		finish();
	}

}
