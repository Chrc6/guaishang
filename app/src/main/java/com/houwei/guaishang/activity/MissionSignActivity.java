package com.houwei.guaishang.activity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.houwei.guaishang.R;
import com.houwei.guaishang.manager.MyUserBeanManager;
import com.houwei.guaishang.views.RevealColorView;

import android.animation.Animator;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

public class MissionSignActivity extends BaseActivity {

	public static final int SIGNED_CLICKED = 0x50;
	private ImageView sun_sad_imageview,sun_smile_imageview,mission_z_iv;

	private TextView status_tv,status_remark_tv; 
	
	private boolean hadSignedToday;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign);
		initView();
		initListener();
	}

	private void initListener() {
		// TODO Auto-generated method stub

	}

	private void initView() {
		// TODO Auto-generated method stub
		TextView date_tv = (TextView)findViewById(R.id.date_tv);
		
		SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日");
		String dateString = sdf.format(new Date());
		
		Calendar c = Calendar.getInstance();
		int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
		switch (dayOfWeek) {
		case 1:
			date_tv.setText(dateString + " " + "星期日");
			break;
		case 2:
			date_tv.setText(dateString + " " + "星期一");
			break;
		case 3:
			date_tv.setText(dateString + " " + "星期二");
			break;
		case 4:
			date_tv.setText(dateString + " " + "星期三");
			break;
		case 5:
			date_tv.setText(dateString + " " + "星期四");
			break;
		case 6:
			date_tv.setText(dateString + " " + "星期五");
			break;
		case 7:
			date_tv.setText(dateString + " " + "星期六");
			break;
		}

		RevealColorView revealColorView = (RevealColorView) findViewById(R.id.revealColorView);
		mission_z_iv = (ImageView) findViewById(R.id.mission_z_iv);
		sun_sad_imageview = (ImageView) findViewById(R.id.sun_sad_imageview);
		sun_smile_imageview = (ImageView) findViewById(R.id.sun_smile_imageview);
		status_remark_tv = (TextView) findViewById(R.id.status_remark_tv);
		status_tv = (TextView) findViewById(R.id.status_tv);
		
		MyUserBeanManager userBeanManager =  getITopicApplication().getMyUserBeanManager();
		hadSignedToday = userBeanManager.hadSignedToday();
		
		if (!hadSignedToday) {
			//尚未签到
			sun_smile_imageview.setAlpha(0.0f);
			sun_sad_imageview.setOnClickListener(listener);
			
			AnimationDrawable animationDrawable = (AnimationDrawable) getResources().getDrawable( 
					R.drawable.mission_z);
			mission_z_iv.setImageDrawable(animationDrawable);
		}else{
			 //已经签到过了
			sun_sad_imageview.setVisibility(View.INVISIBLE);
			mission_z_iv.setVisibility(View.INVISIBLE);
			revealColorView.setBackgroundColor(getResources().getColor(R.color.yellow_color));
			
			status_tv.setText("今日已签到");
			
			//根据当前登录的账号，获取他的签到日期list<String>
			SharedPreferences pref = getSharedPreferences("MISSION_SIGN",Activity.MODE_PRIVATE);
			Set<String> signArray = pref.getStringSet(userBeanManager.getUserId()+"_LASTSIGN", new HashSet<String>());
			status_remark_tv.setText("累计签到 "+signArray.size()+" 天，继续加油哦");
		}
	}

	private View.OnClickListener listener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (!checkLogined()) {
				return;
			}
			
			if (hadSignedToday) {
				return;
			}
			
			final int signCount = getITopicApplication().getMyUserBeanManager().signedToday();
			
			//告诉服务器，今日已经签到，好让服务器涨积分
			getITopicApplication().getMyUserBeanManager().startPointActionRun(MissionActivity.MISSION_SIGN_ID);
			
			hadSignedToday = true;
			
			sun_sad_imageview.setOnClickListener(null);
			
			//两个小太阳的切换动画
			AlphaAnimation dismissAnimation = new AlphaAnimation(1.0f, 0.0f);  
			dismissAnimation.setDuration(1200);
			dismissAnimation.setFillAfter(true);
			sun_sad_imageview.startAnimation(dismissAnimation);  
			
			sun_smile_imageview.setAlpha(1.0f);
			Animation showAnimation = new AlphaAnimation(0.0f, 1.0f);  
			showAnimation.setDuration(1200);  
			showAnimation.setFillAfter(true);
			sun_smile_imageview.startAnimation(showAnimation); 
			
			AnimationDrawable animationDrawable = (AnimationDrawable) mission_z_iv.getDrawable();  
            animationDrawable.stop(); 
            mission_z_iv.setVisibility(View.INVISIBLE);
            
			RevealColorView revealColorView = (RevealColorView) findViewById(R.id.revealColorView);
			
			int[] location = new int[2];
			v.getLocationOnScreen(location);
			int x = location[0];
			int y = location[1];

			int centerX = v.getLeft() + (v.getRight() - v.getLeft()) / 2;
			int centerY = y + v.getMeasuredHeight() / 2;

			revealColorView.reveal(centerX, centerY, getResources()
					.getColor(R.color.yellow_color),
					new Animator.AnimatorListener() {

						@Override
						public void onAnimationStart(Animator arg0) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onAnimationRepeat(Animator arg0) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onAnimationEnd(Animator arg0) {
							// TODO Auto-generated method stub
							status_tv.setText("今日已签到");
							
							status_remark_tv.setText("累计签到 "+signCount+" 天，继续加油哦");
							
							setResult(MissionSignActivity.SIGNED_CLICKED);
						}

						@Override
						public void onAnimationCancel(Animator arg0) {
							// TODO Auto-generated method stub

						}
					});

			
		}
	};
}
