package com.houwei.guaishang.activity;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.houwei.guaishang.R;
import com.houwei.guaishang.bean.HisInfoResponse;
import com.houwei.guaishang.bean.MissionProgressResponse;
import com.houwei.guaishang.bean.SearchedMemberBean;
import com.houwei.guaishang.bean.UserBean;
import com.houwei.guaishang.manager.MyUserBeanManager;
import com.houwei.guaishang.tools.HttpUtil;
import com.houwei.guaishang.tools.JsonParser;
import com.houwei.guaishang.tools.ValueUtil;
import com.houwei.guaishang.views.RevealColorView;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MissionActivity extends BaseActivity {

	
	public final static String MISSION_SIGN_ID = "1"; // 每日签到 任务标示id
	public final static String MISSION_SHARE_ID = "2"; // 每日分享 任务标示id
	public final static String MISSION_PRAISE_ID = "3"; // 每日点赞 任务标示id
	public final static String MISSION_AVATAR_ID = "101"; // 上传头像 任务标示id
	public final static String MISSION_FOLLOW_TEN_ID = "102"; // 关注十人 任务标示id
	
	
	private TextView sign_status_tv, share_status_tv, praise_status_tv,avatar_status_tv, follow_status_tv;


	private MyHandler handler = new MyHandler(this);
	private static class MyHandler extends Handler {
		
		private WeakReference<Context> reference;
		
	    public MyHandler(Context context) {
	    	reference = new WeakReference<Context>(context);
	    }
	    
	    @Override
		public void handleMessage(Message msg) {
	    	final MissionActivity activity = (MissionActivity) reference.get();
			if(activity == null){
				return;
			}
			switch (msg.what) {
			case BaseActivity.NETWORK_SUCCESS_DATA_RIGHT:
				final MissionProgressResponse response = (MissionProgressResponse) msg.obj;
				if (response.isSuccess()) {
					Map<String, Integer> bean = response.getData();
					activity.resetStatusTextView(activity.share_status_tv, bean.get(MISSION_SHARE_ID), 1);
					activity.resetStatusTextView(activity.praise_status_tv, bean.get(MISSION_PRAISE_ID), 1);
					activity.resetStatusTextView(activity.follow_status_tv, bean.get(MISSION_FOLLOW_TEN_ID), 10);
				} else {
				}

				break;
				
			default:
				activity.showErrorToast();
				break;
			}
		}
	};

	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mission);
		initView();
		initListener();
	}

	private void initListener() {
		// TODO Auto-generated method stub
		BackButtonListener();
		MyUserBeanManager userBeanManager =  getITopicApplication().getMyUserBeanManager();
		boolean hadSignedToday = userBeanManager.hadSignedToday();
		
		//是否签到，是在本地判断，不从服务器获取，但是签到成功的操作要提交给服务器
		resetStatusTextView(sign_status_tv,hadSignedToday?1:0, 1);
		
		//是否有头像，是在本地判断，其实这里也可以从服务器获取
		if (userBeanManager.getInstance()!=null && !userBeanManager.getInstance().getAvatar().isEmpty()) {
			 //已经有头像了
			resetStatusTextView(avatar_status_tv,1, 1);
		}else{
			resetStatusTextView(avatar_status_tv,0, 1);
			//还没头像，设置点击事件
			findViewById(R.id.avatar_ll).setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					if (checkLogined()) {
						Intent i = new Intent(MissionActivity.this,MinePersonalInfoDetailActivity.class);
						startActivity(i);
					}
				}
			});
		}
		
		findViewById(R.id.sign_ll).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						Intent i = new Intent(MissionActivity.this,MissionSignActivity.class);
						startActivityForResult(i, 1);
					}
				});
		
		if (!hadSignedToday) {
			//今天还没签到，立刻跳进 签到界面
			handler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Intent i = new Intent(MissionActivity.this,MissionSignActivity.class);
					startActivityForResult(i, 1);
					overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);    
				}
			}, 600);
		}
	}

	private void initView() {
		// TODO Auto-generated method stub
		
		sign_status_tv = (TextView)findViewById(R.id.sign_status_tv);
		share_status_tv = (TextView)findViewById(R.id.share_status_tv);
		praise_status_tv = (TextView)findViewById(R.id.praise_status_tv);
		avatar_status_tv = (TextView)findViewById(R.id.avatar_status_tv);
		follow_status_tv = (TextView)findViewById(R.id.follow_status_tv);
		
		if (getITopicApplication().getMyUserBeanManager().getInstance()!=null) {
			//已经登录了
			new Thread(inforun).start();
		}else{
			//还没登录
			resetStatusTextView(share_status_tv, 0, 1);
			resetStatusTextView(praise_status_tv, 0, 1);
			resetStatusTextView(follow_status_tv, 0, 10);
		}
	}

	private Runnable inforun = new Runnable() {

		public void run() {
			// TODO Auto-generated method stub
			MissionProgressResponse response = null;
			try {
				Map<String, String> data = new HashMap<String, String>();
				data.put("userid", getUserID());
				response = JsonParser.getMissionProgressResponse(HttpUtil
						.getMsg(HttpUtil.IP + "mission/progress?" + HttpUtil.getData(data)));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (response != null) {
				handler.sendMessage(handler.obtainMessage(
						BaseActivity.NETWORK_SUCCESS_DATA_RIGHT, response));
			} else {
				handler.sendEmptyMessage(BaseActivity.NETWORK_FAIL);
			}
		}
	};
	
	
	
	
	/**
	 * @param tv
	 * @param currentProgress 当前完成进度（1/10 的 1）
	 * @param maxProgress 总进度（1/10 的 10）
	 */
	private void resetStatusTextView(TextView tv,Integer currentProgress,int maxProgress) {
		// TODO Auto-generated method stub
		if (currentProgress == null) {
			currentProgress = 0;
		} 
		if (currentProgress >= maxProgress) {
			tv.setBackgroundResource(R.drawable.mission_complete);
			tv.setText("");
		} else {
			tv.setBackgroundDrawable(null);
			tv.setText(currentProgress+" / "+maxProgress);
		}
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == MissionSignActivity.SIGNED_CLICKED) {
			resetStatusTextView(sign_status_tv, 1, 1);
		}
	}
}
