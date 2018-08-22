package com.houwei.guaishang.activity;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.houwei.guaishang.R;
import com.houwei.guaishang.bean.StringResponse;
import com.houwei.guaishang.tools.HttpUtil;
import com.houwei.guaishang.tools.JsonParser;
import com.houwei.guaishang.tools.Utils;
import com.houwei.guaishang.views.AnimationYoYo;

import android.content.Context;
import android.content.Intent;
import android.media.MediaRouter.UserRouteInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class UserFindPasswordActivity extends BaseActivity {

	public final static int REG_LOGIN_SUCCESS = 0x99;
	private EditText phone_et, check_pw_et, pw_et_1,new_password_et2;
	private Button check_pw_get_btn;
	private Timer timer;
	private TimerTask task;
	private int CURRENTDELAYTIME;
	private final int DELAYTIME = 60;

	private MyHandler handler = new MyHandler(this);
	private static class MyHandler extends Handler {
		
		private WeakReference<Context> reference;
		
	    public MyHandler(Context context) {
	    	reference = new WeakReference<Context>(context);
	    }
	    
	    @Override
		public void handleMessage(Message msg) {
	    	final UserFindPasswordActivity activity = (UserFindPasswordActivity) reference.get();
			if(activity == null){
				return;
			}
		
			switch (msg.what) {
			case BaseActivity.NETWORK_SUCCESS_DATA_RIGHT: // 验证码效验成功
				activity.progress.dismiss();
				StringResponse retMap = (StringResponse) msg.obj;
				if (retMap.isSuccess()) {
					activity.showErrorToast("修改成功");
					activity.finish();				
				} else {
					activity.showErrorToast(retMap.getMessage());
				}
				break;
				
			case NETWORK_OTHER: // 验证码倒计时
				if (activity.CURRENTDELAYTIME <= 0) {
					activity.cancelTime();
				} else {
					activity.CURRENTDELAYTIME--;
					activity.check_pw_get_btn.setText(activity.CURRENTDELAYTIME + "秒后重获");
				}
				break;
			}
		}
	};

	private SMShandler smshandler = new SMShandler(this);
	private static class SMShandler extends Handler {
		
		private WeakReference<Context> reference;
		
	    public SMShandler(Context context) {
	    	reference = new WeakReference<Context>(context);
	    }
	    
	    @Override
		public void handleMessage(Message msg) {
	    	final UserFindPasswordActivity activity = (UserFindPasswordActivity) reference.get();
			if(activity == null){
				return;
			}
			activity.progress.dismiss();
			int event = msg.arg1;
			int result = msg.arg2;
			Object data = msg.obj;
			if (result == SMSSDK.RESULT_COMPLETE) {
				//短信注册成功后，返回MainActivity,然后提示新好友
				if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {//提交验证码成功
					//验证码效验争取
					activity.progress.show();
					 new Thread(activity.ValidateRun).start();
				} else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
					activity.showErrorToast("验证码已经发送");
					activity.check_pw_get_btn.setClickable(false);
					activity.check_pw_get_btn.setText(activity.DELAYTIME+"秒后重获");
					activity.startTime();
				} else if (event ==SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){//返回支持发送验证码的国家列表
					activity.showErrorToast("获取国家列表成功");
				}
			} else {
				activity.showErrorToast("验证码错误");
			}
	    }
	};

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if (task != null) {
			task.cancel();
			timer.cancel();
			timer = null;
			task = null;
		}
		super.onDestroy();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_passward_reset);
		initView();
		initListener();
	}

	private void initView() {
		// TODO Auto-generated method stub
//		SMSSDK.initSDK(this,UserRegMobileActivity.APPKEY,UserRegMobileActivity.APPSECRET);
		timer = new Timer();
		phone_et = (EditText) findViewById(R.id.phone_et);
		check_pw_et = (EditText) findViewById(R.id.check_pw_et);
//		 new_password_et2= (EditText) findViewById(R.id.new_password_et2);
		pw_et_1 = (EditText) findViewById(R.id.pw_et_1);
		pw_et_1.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String data = Utils.stringFilter(String.valueOf(s));
				if (!String.valueOf(s).equals(data)) {
					pw_et_1.setText(data);
				}
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		initProgressDialog(false, null);
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				showKeyboard(phone_et);
			}
		}, 150);
		
		EventHandler eh=new EventHandler(){

			@Override
			public void afterEvent(int event, int result, Object data) {
				
				Message msg = new Message();
				msg.arg1 = event;
				msg.arg2 = result;
				msg.obj = data;
				smshandler.sendMessage(msg);
			}
		};
		SMSSDK.registerEventHandler(eh);
	}

	private void startTime() {
		CURRENTDELAYTIME = DELAYTIME;
		task = new TimerTask() {

			@Override
			public void run() {
				handler.sendEmptyMessage(NETWORK_OTHER);
			}
		};
		timer.schedule(task, 0, 1000);
	}

	private void cancelTime() {
		task.cancel();
		check_pw_get_btn.setClickable(true);
		check_pw_get_btn.setText("获取验证码");
	}

	private void initListener() {
		// TODO Auto-generated method stub
		check_pw_get_btn = (Button) findViewById(R.id.check_pw_get_btn);
		check_pw_get_btn.setText("获取验证码");
		check_pw_get_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (phone_et.getText().toString().equals("")) {
					showErrorToast("请输入手机号码");
					return;
				}
				check_pw_get_btn.setClickable(false);
				check_pw_get_btn.setText("60秒后重获");
				startTime();
				progress.show();
				SMSSDK.getVerificationCode("86",phone_et.getText().toString().trim());
			}
		});

		findViewById(R.id.release_btn).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						if (phone_et.getText().toString().trim().equals("")){
							AnimationYoYo.shakeView(findViewById(R.id.phone_et));
							showErrorToast("请输入手机号");
							return;
						}
						if (pw_et_1.getText().toString().trim().equals("")){
							AnimationYoYo.shakeView(findViewById(R.id.pw_et_1));
							showErrorToast("请输入密码");
							return;
						}
						if (check_pw_et.getText().toString().trim().equals("")){
							AnimationYoYo.shakeView(findViewById(R.id.check_pw_et));
							showErrorToast("请输入验证码");
							return;
						}
//						if (!pw_et_1.getText().toString().trim().equals(new_password_et2.getText().toString().trim())) {
//							AnimationYoYo.shakeView(findViewById(R.id.pw_et_1));
//							AnimationYoYo.shakeView(findViewById(R.id.new_password_et2));
//							showErrorToast("两次密码不一致");
//							return;
//						}
						
						progress.show();
						SMSSDK.submitVerificationCode("86", phone_et.getText().toString().trim(), check_pw_et.getText().toString().trim());

					}
				});
		BackButtonListener();
	}

	
	private Runnable ValidateRun = new Runnable() {

		@SuppressWarnings("unchecked")
		public void run() {
			// TODO Auto-generated method stub
			StringResponse retMap = null;
			String mobile = phone_et.getText().toString().trim();
			String password = pw_et_1.getText().toString().trim();
			try {
				Map<String, String> data = new HashMap<String, String>();
				data.put("mobile", mobile);
				data.put("password", password);
				
				retMap = JsonParser.getStringResponse2(HttpUtil.postMsg(
						HttpUtil.getData(data), HttpUtil.IP
								+ "user/findpassword"));

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (retMap != null) {
				retMap.setTag(password);
				handler.sendMessage(handler.obtainMessage(
						BaseActivity.NETWORK_SUCCESS_DATA_RIGHT, retMap));
			} else {
				handler.sendEmptyMessage(BaseActivity.NETWORK_FAIL);
			}
		}
	};

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (getCurrentFocus() != null) {
			((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(
							getCurrentFocus().getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
		}
		return super.onTouchEvent(event);
	}


}
