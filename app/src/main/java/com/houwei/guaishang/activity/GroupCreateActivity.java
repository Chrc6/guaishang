/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.houwei.guaishang.activity;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.houwei.guaishang.R;
import com.houwei.guaishang.bean.SearchedMemberBean;

import com.easemob.chat.EMGroupManager;
import com.easemob.exceptions.EaseMobException;
import com.nostra13.universalimageloader.core.ImageLoader;

public class GroupCreateActivity extends BasePhotoActivity {
	private EditText groupNameEditText;
	private CheckBox checkBox;
	private String currentPhotoUrl = ""; 
	private CheckBox memberCheckbox;
	private LinearLayout openInviteContainer;
	private TextView public_title_tv;
	private ImageView group_avator;
	
	private final static int MEMBER_SELECT_REQUITE = 0x83;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_new);
		initProgressDialog();
		BackButtonListener();
		public_title_tv = (TextView) findViewById(R.id.public_title_tv);
		groupNameEditText = (EditText) findViewById(R.id.name_et);
		checkBox = (CheckBox) findViewById(R.id.public_check_box);
		memberCheckbox = (CheckBox) findViewById(R.id.member_check_box);
		openInviteContainer = (LinearLayout) findViewById(R.id.ll_open_invite);
		group_avator = (ImageView) findViewById(R.id.user_head);
		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					public_title_tv.setText("创建公开群，别人可以搜索到且直接进入您的群");
					openInviteContainer.setVisibility(View.GONE);
					findViewById(R.id.line_view).setVisibility(View.GONE);
				}else{
					public_title_tv.setText("创建私有群，别人只能通过被邀请的方式入群");
					openInviteContainer.setVisibility(View.VISIBLE);
					findViewById(R.id.line_view).setVisibility(View.VISIBLE);
				}
			}
		});
		
		findViewById(R.id.change_groupavatar_btn).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				showBottomPopupWin(group_avator,"user/upload");
			}
		});
		
		findViewById(R.id.title_right).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				save(arg0);
			}
		});
	}

	/**
	 * @param v
	 */
	private void save(View v) {
		// 进通讯录选人
		startActivityForResult(new Intent(this, GroupMemberSelectActivity.class).putExtra("disableIdlist", getUserID()), MEMBER_SELECT_REQUITE);
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode ==  MEMBER_SELECT_REQUITE && resultCode == RESULT_OK) {
			//新建群组
			progress.show();

			new Thread(new Runnable() {
				@Override
				public void run() {
					// 调用sdk创建群组方法
					String groupName = groupNameEditText.getText().toString().trim();
					
					List<SearchedMemberBean> selectedList = (List<SearchedMemberBean>) data.getSerializableExtra("selectedList");
					
					if(groupName.equals("")){
						StringBuffer sb = new StringBuffer();
						sb.append(getITopicApplication().getMyUserBeanManager().getInstance().getName());
						sb.append("、");
						for (int i = 0; i < selectedList.size(); i++) {
							if (sb.length() < 10) {
								sb.append(selectedList.get(i).getMemberName());
								if (i != selectedList.size()-1) {
									sb.append("、");
								}
							}else{
								sb.append("...");
								break;
							}
						}
						groupName = sb.toString();
					}
					
					final String[] members = new String[selectedList.size()];
					for (int i = 0; i < selectedList.size(); i++) {
						members[i] = selectedList.get(i).getMemberId();
					}
					try {
						if(checkBox.isChecked()){
							//创建公开群，此种方式创建的群，可以自由加入
							//创建公开群，此种方式创建的群，用户需要申请，true表示等群主同意后才能加入此群
						    EMGroupManager.getInstance().createPublicGroup(groupName, currentPhotoUrl, members, false,200);
						}else{
							//创建不公开群
						    EMGroupManager.getInstance().createPrivateGroup(groupName, currentPhotoUrl, members, memberCheckbox.isChecked(),200);
						}
						runOnUiThread(new Runnable() {
							public void run() {
								progress.dismiss();
								setResult(RESULT_OK);
								finish();
							}
						});
					} catch (final EaseMobException e) {
						runOnUiThread(new Runnable() {
							public void run() {
								progress.dismiss();
								showErrorToast( e.getLocalizedMessage());
							}
						});
					}
					
				}
			}).start();
		}
	}
	
	@Override
	public void onPhotoSelectSuccess(String picturePath,
			ImageView currentImageView) {
		ImageLoader.getInstance().displayImage("file://" + picturePath,currentImageView);
	}

	@Override
	public void onPhotoUploadSuccess(String imageUrl, String picturePath,
			ImageView currentImageView) {
		currentPhotoUrl = imageUrl; 
	}

	@Override
	public void onPhotoUploadFail(ImageView currentImageView) {
		currentPhotoUrl = "";
		currentImageView.setImageResource(R.drawable.group_avator_default);
	}
}
