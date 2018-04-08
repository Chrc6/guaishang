package com.houwei.guaishang.activity;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.houwei.guaishang.R;
import com.houwei.guaishang.easemob.EaseConstant;
import com.houwei.guaishang.tools.HttpUtil;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupInfo;
import com.easemob.chat.EMGroupManager;
import com.easemob.exceptions.EaseMobException;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class GroupPublicDetailsActivity extends BaseActivity {

	private Button reg_btn;
	private EMGroupInfo groupInfo;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_public_detail);
		initView();
		initListener();
	}

	private void initView() {
		// TODO Auto-generated method stub
		initProgressDialog();
		
		reg_btn = (Button)findViewById(R.id.reg_btn);
		groupInfo = (EMGroupInfo) getIntent().getSerializableExtra("groupinfo");
		
		TextView item_name = (TextView)findViewById(R.id.item_name);
		item_name.setText(""+groupInfo.getGroupName());
		
		new Thread(new Runnable() {

			public void run() {
				//从服务器获取详情
				try {
					final EMGroup group = EMGroupManager.getInstance().getGroupFromServer(groupInfo.getGroupId());
					runOnUiThread(new Runnable() {
						public void run() {
							ImageView user_head = (ImageView)findViewById(R.id.user_head);
							DisplayImageOptions groupAvatarOptions = new DisplayImageOptions.Builder()
								.cacheInMemory(true).cacheOnDisk(true)
								.showImageForEmptyUri(R.drawable.group_avator_default)
								.showImageOnFail(R.drawable.group_avator_default)
								.showImageOnLoading(R.drawable.group_avator_default)
								.imageScaleType(ImageScaleType.EXACTLY)// 设置图片以如何的编码方式显示，EXACTLY_STRETCHED会比较卡
								.build();
				        
							ImageLoader.getInstance().displayImage(HttpUtil.IP_NOAPI+group.getDescription(), user_head, groupAvatarOptions);
						
							 if(group.getMembers().contains(EMChatManager.getInstance().getCurrentUser())){
							
								 reg_btn.setOnClickListener(new View.OnClickListener() {
										
										@Override
										public void onClick(View arg0) {
											// TODO Auto-generated method stub
											jumpToChatActivity(group.getGroupId(), group.getGroupName(), null, EaseConstant.CHATTYPE_GROUP);
										}
									});
							 }
						}
					});
				} catch (final EaseMobException e) {
					e.printStackTrace();
				}
			}
		}).start();
		
	}

	private void initListener() {
		// TODO Auto-generated method stub
		BackButtonListener();
		findViewById(R.id.reg_btn).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				progress.show();
				new Thread(new Runnable() {
					public void run() {
						try {
							//如果是membersOnly的群，需要申请加入，不能直接join
							EMGroupManager.getInstance().joinGroup(groupInfo.getGroupId());
							runOnUiThread(new Runnable() {
								public void run() {
									progress.dismiss();
									jumpToChatActivity(groupInfo.getGroupId(), groupInfo.getGroupName(), null,EaseConstant.CHATTYPE_GROUP);
								}
							});
						} catch (final EaseMobException e) {
							e.printStackTrace();
							runOnUiThread(new Runnable() {
								public void run() {
									progress.dismiss();
									showErrorToast(""+e.getMessage());
								}
							});
						}
					}
				}).start();
			}
		});
	}
}
