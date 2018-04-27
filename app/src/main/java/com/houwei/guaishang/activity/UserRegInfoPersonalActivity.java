package com.houwei.guaishang.activity;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Random;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.houwei.guaishang.event.LoginSuccessEvent;
import com.houwei.guaishang.sp.UserInfo;
import com.houwei.guaishang.sp.UserUtil;
import com.houwei.guaishang.tools.ToastUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.houwei.guaishang.R;
import com.houwei.guaishang.bean.NameIDBean;
import com.houwei.guaishang.bean.UserResponse;
import com.houwei.guaishang.data.DBReq;
import com.houwei.guaishang.layout.NameIDDialog;
import com.houwei.guaishang.layout.NumberPickerDialog;
import com.houwei.guaishang.layout.NameIDDialog.AnswerListener;
import com.houwei.guaishang.layout.NumberPickerDialog.DateSelectedListener;
import com.houwei.guaishang.manager.MyUserBeanManager;
import com.houwei.guaishang.tools.HttpUtil;
import com.houwei.guaishang.tools.JsonParser;
import com.houwei.guaishang.tools.ValueUtil;
import com.houwei.guaishang.views.AnimationYoYo;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.EventBus;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

public class UserRegInfoPersonalActivity extends UserRegInfoBaseActivity implements
		DateSelectedListener {
	private NameIDBean currentSexBean;
	private TextView sex_tv,age_tv;
	private ImageView user_icon;
	private String currentPhotoUrl;
	private EditText user_name_et,password_et;
	private String mobile;

	private MyHandler handler = new MyHandler(this);
	private RxPermissions rxPermissions;

	private static class MyHandler extends Handler {
		
		private WeakReference<Context> reference;
		
	    public MyHandler(Context context) {
	    	reference = new WeakReference<Context>(context);
	    }
	    
	    @Override
		public void handleMessage(Message msg) {
	    	final UserRegInfoPersonalActivity activity = (UserRegInfoPersonalActivity) reference.get();
			if(activity == null){
				return;
			}

			switch (msg.what) {
			case NETWORK_SUCCESS_DATA_RIGHT:
				UserResponse response = (UserResponse) msg.obj;

				if (response.isSuccess()) {
					activity.showErrorToast("注册成功");
					activity.progress.setMessage("自动登录中");
					// 保存用户信息并开启推送
					activity.getITopicApplication().getMyUserBeanManager().storeUserInfoAndNotity(response.getData());
					// 展开数据库
					DBReq.getInstence(activity.getITopicApplication());
					UserInfo info = new UserInfo();
					info.setUserId(response.getData().getUserid());
					info.setUserName(response.getData().getName());
					UserUtil.setUserInfo(info);
					EventBus.getDefault().post(new LoginSuccessEvent());
					//告诉php服务器我刚才注册成功了，php服务器收到这个接口后，会推送小秘书默认消息
		            //为什么不是在php收到注册接口之后直接推送消息呢？因为php服务器推送消息无法异步，会导致注册超时
					//activity.getITopicApplication().getHuanXinManager().doPushAction(1, new HashMap<String, String>());
					
					if (MyUserBeanManager.MISSION_ENABLE && activity.currentPhotoUrl!=null) {
			             //用户上传了头像，记录他完成了 上传头像 任务
						activity.getITopicApplication().getMyUserBeanManager().startPointActionRun(MissionActivity.MISSION_AVATAR_ID);
			        }
					
					//与环信服务器长连接
					activity.loginHuanXinService( response.getData().getUserid(),response.getData().getName());
				
				} 
				else {
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
		setContentView(R.layout.activity_user_reg_info_personal);
		 rxPermissions=new RxPermissions(this);
		initView();
		initListener();
		checkIfAutoReg();
	}

	private void checkIfAutoReg() {
		// TODO Auto-generated method stub
		if(getIntent().getBooleanExtra("auto", false)){
			String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";  
			Random random = new Random();  
		    StringBuffer sb = new StringBuffer();  
		          
		    for(int i = 0 ; i < 5; ++i){  
		        int number = random.nextInt(62);//[0,62)  
		        sb.append(str.charAt(number));  
		    }  
		    
		    password_et.setText("123");
		    user_name_et.setText(sb.toString());
		    
			progress.show();
			new Thread(run).start();
		}
	}

	private void initView() {
		// TODO Auto-generated method stub
		initProgressDialog();
		
		currentSexBean = new NameIDBean("0", "未填");
		mobile = getIntent().getStringExtra("mobile");
		user_name_et = (EditText) findViewById(R.id.realname_et);
		user_icon = (ImageView) findViewById(R.id.user_head);
		password_et = (EditText) findViewById(R.id.password_et);
		age_tv = (TextView) findViewById(R.id.age_tv);
		sex_tv = (TextView) findViewById(R.id.sex_tv);
	}

	private void initListener() {
		// TODO Auto-generated method stub
		BackButtonListener();

		Button reg_btn = (Button) findViewById(R.id.reg_btn);
		reg_btn.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub				
				if (user_name_et.getText().toString().trim().equals("")) {
					AnimationYoYo.shakeView(findViewById(R.id.user_name_ll));
					return;
				}
				
				if (password_et.getText().toString().trim().equals("")) {
					AnimationYoYo.shakeView(findViewById(R.id.password_ll));
					return;
				}
				
				progress.show();
				new Thread(run).start();
			}
		});

		user_icon.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				hideKeyboard();
				rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA)
						.subscribe(new Consumer<Boolean>() {
							@Override
							public void accept(@NonNull Boolean aBoolean) throws Exception {
								if(aBoolean){
									showBottomPopupWin(user_icon, "user/upload1");
								}else{
									ToastUtils.toastForShort(UserRegInfoPersonalActivity.this,"你拒绝了相册权限");
								}
							}
						});


			}
		});
		
		findViewById(R.id.age_ll).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						NumberPickerDialog dialog = new NumberPickerDialog(UserRegInfoPersonalActivity.this);
						dialog.setOnDateSelectedListener(null,new DateSelectedListener() {
							
							@Override
							public void onDateSelected(TextView tv, int result) {
								// TODO Auto-generated method stub
								age_tv.setText(""+result);
							}
						});
						dialog.show();
					}
				});
		
		findViewById(R.id.sex_ll).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						NameIDDialog sexdialog = new NameIDDialog(
								UserRegInfoPersonalActivity.this, ValueUtil.getSexTypeList(),
								"选择性别", "",
								new AnswerListener() {

									@Override
									public void onAnswer(NameIDBean selectBean) {
										// TODO Auto-generated method stub
										currentSexBean = selectBean;
										sex_tv.setText(selectBean.getName());
									}
								});
						sexdialog.show();
					}
				});
		
	

		findViewById(R.id.scrollview).setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				hideKeyboard(getWindow().getDecorView());
				return false;
			}
		});

	}

	
	private Runnable run = new Runnable() {
		@SuppressWarnings("unchecked")
		public void run() {
			// TODO Auto-generated method stub
			UserResponse response = null;
			try {
				HashMap<String, String> data = new HashMap<String, String>();
				data.put("mobile", mobile);
				data.put("password", password_et.getText().toString().trim());
				data.put("name", user_name_et.getText().toString().trim());
				data.put("avatar", currentPhotoUrl);
				data.put("sex", currentSexBean.getId());
				data.put("age", age_tv.getText().toString().trim());
				response = JsonParser.getUserResponse(HttpUtil.postMsg(
						HttpUtil.getData(data), HttpUtil.IP + "user/register"));
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


	
	@Override
	public void onPhotoSelectSuccess(String picturePath,
			ImageView currentImageView) {
		ImageLoader.getInstance().displayImage("file://" + picturePath,
				user_icon);
	}

	@Override
	public void onPhotoUploadSuccess(String imageUrl, String picturePath,
			ImageView currentImageView) {
		// this.photo_src = photo_src.replaceAll("\\\\", "/");
		currentPhotoUrl = imageUrl;
	}

	@Override
	public void onPhotoUploadFail(ImageView currentImageView) {
		currentImageView.setImageResource(R.drawable.user_reg_photo);
		currentPhotoUrl = null;
	}



	@Override
	public void onDateSelected(TextView tv, int result) {
		// TODO Auto-generated method stub
		tv.setText("" + result);
	}

}
