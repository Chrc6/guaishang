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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnShowListener;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.houwei.guaishang.R;
import com.houwei.guaishang.activity.BasePhotoGridActivity.PhotoReleaseGridAdapter;
import com.houwei.guaishang.bean.BaseResponse;
import com.houwei.guaishang.bean.GroupDetailResponse;
import com.houwei.guaishang.bean.LocationBean;
import com.houwei.guaishang.bean.SearchedMemberBean;
import com.houwei.guaishang.bean.UserBean;
import com.houwei.guaishang.bean.UserResponse;
import com.houwei.guaishang.easemob.DemoModel;
import com.houwei.guaishang.layout.AddTagDialog;
import com.houwei.guaishang.layout.MenuTwoButtonDialog;
import com.houwei.guaishang.layout.SureOrCancelDialog;
import com.houwei.guaishang.tools.HttpUtil;
import com.houwei.guaishang.tools.JsonParser;
import com.houwei.guaishang.tools.LogUtil;
import com.houwei.guaishang.tools.ValueUtil;
import com.houwei.guaishang.views.CircleBitmapDisplayer;
import com.houwei.guaishang.views.SlipSwitch;
import com.houwei.guaishang.views.UnScrollGridView;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.EMLog;
import com.easemob.util.NetUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class GroupDetailsActivity extends BasePhotoActivity implements OnClickListener {
	private static final int MEMBER_INFO_RESPONSE = 0x102;
	private static final int UPDETE_DESC_RESPONSE = 0x103;
	
	private static final int REQUEST_CODE_ADD_USER = 0x101;
	public static final int GROUP_DELETE_RESPONSE = 0x104;
	
	
	private DisplayImageOptions groupAvatarOptions ;
	private UnScrollGridView memberGridview;
	private String groupId;
	private ProgressBar loadingPB;
	private ImageView group_avator;
	private EMGroup group;
	private MemberGridAdapter adapter;
	private List<SearchedMemberBean> memberlist;
    
	private SlipSwitch switchButton,notity_disable_switch;
    
	private MyHandler handler = new MyHandler(this);
	private static class MyHandler extends Handler {
		
		private WeakReference<Context> reference;
		
	    public MyHandler(Context context) {
	    	reference = new WeakReference<Context>(context);
	    }
	    
	    @Override
		public void handleMessage(Message msg) {
	    	final GroupDetailsActivity activity = (GroupDetailsActivity) reference.get();
			if(activity == null){
				return;
			}
			activity.progress.dismiss();
			switch (msg.what) {
			case MEMBER_INFO_RESPONSE:
				//访问我们自己的服务器，返回用户信息
				activity.loadingPB.setVisibility(View.GONE);
				GroupDetailResponse response = (GroupDetailResponse)msg.obj;
				if (response.isSuccess()) {
					activity.memberlist = response.getData().getItems();
					activity.adapter = activity.new MemberGridAdapter(activity.memberlist, activity);
					activity.memberGridview.setAdapter(activity.adapter);
				}else{
					activity.showErrorToast(response.getMessage());
				}
			
				break;
			case UPDETE_DESC_RESPONSE:
				//访问我们自己的服务器，修改了群头像
				BaseResponse baseresponse = (BaseResponse)msg.obj;
				if (baseresponse.isSuccess()) {
					
				}else{
					activity.showErrorToast(baseresponse.getMessage());
				}
				break;
			default:
				break;
			}
		}
	};
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_details);
		initProgressDialog();
		BackButtonListener();
	    // 获取传过来的groupid
        groupId = getIntent().getStringExtra("groupId");
        group = EMGroupManager.getInstance().getGroup(groupId);

        // we are not supposed to show the group if we don't find the group
        if(group == null){
            finish();
            return;
        }

		groupAvatarOptions = new DisplayImageOptions.Builder()
			.cacheInMemory(true).cacheOnDisk(true)
			.showImageForEmptyUri(R.drawable.group_avator_default)
			.showImageOnFail(R.drawable.group_avator_default)
			.showImageOnLoading(R.drawable.group_avator_default)
			.imageScaleType(ImageScaleType.EXACTLY)// 设置图片以如何的编码方式显示，EXACTLY_STRETCHED会比较卡
			.build();
        
		memberGridview = (UnScrollGridView) findViewById(R.id.gridview);
		loadingPB = (ProgressBar) findViewById(R.id.progressBar);
		switchButton = (SlipSwitch)findViewById(R.id.block_switch);
		notity_disable_switch = (SlipSwitch)findViewById(R.id.notity_disable_switch);
		group_avator = (ImageView)findViewById(R.id.user_head);
		group_avator.setOnClickListener(this);
		Button exitBtn = (Button)findViewById(R.id.exit_btn);
		
		if (group.getOwner() == null || "".equals(group.getOwner())
				|| !group.getOwner().equals(EMChatManager.getInstance().getCurrentUser())) {
			exitBtn.setText("离开群组");
			exitBtn.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					SureOrCancelDialog followDialog = new SureOrCancelDialog(
							GroupDetailsActivity.this, "确定要退出该群", "好",
							new SureOrCancelDialog.SureButtonClick() {

								@Override
								public void onSureButtonClick() {
									// TODO Auto-generated method stub
									exitGrop();
								}
							});
					followDialog.show();
				}
			});
		}
		// 如果自己是群主，显示解散按钮
		if (EMChatManager.getInstance().getCurrentUser().equals(group.getOwner())) {
			findViewById(R.id.name_ll).setOnClickListener(this);
			exitBtn.setText("解散群组");
			exitBtn.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					SureOrCancelDialog followDialog = new SureOrCancelDialog(
							GroupDetailsActivity.this, "确定要解散该群", "好",
							new SureOrCancelDialog.SureButtonClick() {

								@Override
								public void onSureButtonClick() {
									// TODO Auto-generated method stub
									deleteGrop();
								}
							});
					followDialog.show();
				}
			});
			findViewById(R.id.change_groupavatar_btn).setVisibility(View.VISIBLE);
			findViewById(R.id.change_groupavatar_btn).setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					showBottomPopupWin(group_avator,"user/upload");
				}
			});
			//群主无法屏蔽群消息
			findViewById(R.id.notify_ll).setVisibility(View.GONE);
		}
		
		setGroupInfo();
		
		switchButton.setOnSwitchListener(new SlipSwitch.OnSwitchListener() {
			
			@Override
			public void onSwitched(boolean isSwitchOn) {
				// TODO Auto-generated method stub
				progress.show();
				if(isSwitchOn){
					//屏蔽掉
					new Thread(new Runnable() {
						@Override
	                    public void run() {
	                        try {
	                            EMGroupManager.getInstance().blockGroupMessage(groupId);
	                            runOnUiThread(new Runnable() {
	                                public void run() {
	                                    progress.dismiss();
	                                }
	                            });
	                        } catch (Exception e) {
	                            e.printStackTrace();
	                        }
	                    }
	                }).start();
				}else{
					new Thread(new Runnable() {
						@Override
	                    public void run() {
	                        try {
	                            EMGroupManager.getInstance().unblockGroupMessage(groupId);
	                            runOnUiThread(new Runnable() {
	                                public void run() {
	                                    progress.dismiss();
	                                }
	                            });
	                        } catch (Exception e) {
	                            e.printStackTrace();
	                        }
	                    }
	                }).start();
				}
			}
		});
		
		memberGridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				if(position < memberlist.size()){
					SearchedMemberBean bean = memberlist.get(position);
					jumpToHisInfoActivity(bean.getMemberId(), bean.getMemberName(), bean.getMemberAvatar());
				}else{
					// 进入选人页面
					startActivityForResult(new Intent(GroupDetailsActivity.this, GroupMemberSelectActivity.class)
						.putExtra("disableIdlist",  ValueUtil.ArrayListToString(group.getMembers())), REQUEST_CODE_ADD_USER);
				
				}
			}
		});
		
		//DQ 2016-07-19 接收但不提示消息 是走我们自定义的逻辑
		final DemoModel demoModel = getITopicApplication().getHuanXinManager().getHxSDKHelper().getModel();
		notity_disable_switch.setSwitchOn(demoModel.getDisabledGroups().contains(groupId));
		
		
		notity_disable_switch.setOnSwitchListener(new SlipSwitch.OnSwitchListener() {
			
			@Override
			public void onSwitched(boolean isSwitchOn) {
				// TODO Auto-generated method stub
				demoModel.switchDisabledGroup(groupId);
			}
		});
		
		// 保证每次进详情看到的都是最新的group
		updateGroup();
	
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUEST_CODE_ADD_USER:// 添加群成员
				List<SearchedMemberBean> selectedList = (List<SearchedMemberBean>) data.getSerializableExtra("selectedList");
				addMembersToGroup(selectedList);
				break;
			default:
				break;
			}
		}
	}
	

	/**
	 * 退出群组
	 * 
	 * @param groupId
	 */
	private void exitGrop() {
		progress.show();
		new Thread(new Runnable() {
			public void run() {
				try {
				    EMGroupManager.getInstance().exitFromGroup(groupId);
					runOnUiThread(new Runnable() {
						public void run() {
							progress.dismiss();
							setResult(GROUP_DELETE_RESPONSE);
							finish();
						}
					});
				} catch (final Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
							progress.dismiss();
							showErrorToast(e.getMessage());
						}
					});
				}
			}
		}).start();
	}

	/**
	 * 解散群组
	 * 
	 * @param groupId
	 */
	private void deleteGrop() {
		progress.show();
		new Thread(new Runnable() {
			public void run() {
				try {
				    EMGroupManager.getInstance().exitAndDeleteGroup(groupId);
					runOnUiThread(new Runnable() {
						public void run() {
							progress.dismiss();
							setResult(GROUP_DELETE_RESPONSE);
							finish();
						}
					});
				} catch (final Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
							progress.dismiss();
							showErrorToast(e.getMessage());
						}
					});
				}
			}
		}).start();
	}

	/**
	 * 增加群成员
	 * 
	 * @param newmembers
	 */
	private void addMembersToGroup(final List<SearchedMemberBean> selectedList) {
		progress.show();
		final String[] newmembers = new String[selectedList.size()];
		for (int i = 0; i < selectedList.size(); i++) {
			newmembers[i] = selectedList.get(i).getMemberId();
		}
		
		final String st6 = getResources().getString(R.string.Add_group_members_fail);
		new Thread(new Runnable() {
			
			public void run() {
				try {
					// 创建者调用add方法
					if (EMChatManager.getInstance().getCurrentUser().equals(group.getOwner())) {
					    EMGroupManager.getInstance().addUsersToGroup(groupId, newmembers);
					} else {
						// 一般成员调用invite方法
					    EMGroupManager.getInstance().inviteUser(groupId, newmembers, null);
					}
					runOnUiThread(new Runnable() {
						public void run() {
							progress.dismiss();
							memberlist.addAll(selectedList);
							adapter.notifyDataSetChanged();
						}
					});
				} catch (final Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
							progress.dismiss();
							Toast.makeText(getApplicationContext(), st6 + e.getMessage(), 1).show();
						}
					});
				}
			}
		}).start();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.name_ll:
			// 修改群名称
			final AddTagDialog dialog = new AddTagDialog(
					GroupDetailsActivity.this,
					new AddTagDialog.SureButtonClick() {

						@Override
						public void onSureButtonClick(final String tag) {
							// TODO Auto-generated method stub
							progress.show();
							new Thread(new Runnable() {
								public void run() {
									try {
									    EMGroupManager.getInstance().changeGroupName(groupId, tag);
										runOnUiThread(new Runnable() {
											public void run() {
												progress.dismiss();
												TextView name_tv = (TextView)findViewById(R.id.name_tv);
												name_tv.setText(group.getGroupName());
											}
										});
									} catch (EaseMobException e) {
										e.printStackTrace();
										runOnUiThread(new Runnable() {
											public void run() {
												progress.dismiss();
											}
										});
									}
								}
							}).start();
						}
					});
			dialog.setTitle("群名称");
			dialog.setOnShowListener(new OnShowListener() {
				
				@Override
				public void onShow(DialogInterface dialog2) {
					// TODO Auto-generated method stub
					showKeyboard(dialog.tag_et);
				}
			});
			dialog.show();
			break;
		case R.id.user_head:
			Intent intent = new Intent(GroupDetailsActivity.this, GalleryActivity.class);
			ArrayList<String> urls = new ArrayList<String>();
			urls.add(HttpUtil.IP_NOAPI+ group.getDescription());
			intent.putExtra(GalleryActivity.EXTRA_IMAGE_URLS, urls);
			intent.putExtra(GalleryActivity.EXTRA_IMAGE_INDEX, 0);
			startActivity(intent);
			break;
		default:
			break;
		}

	}

	/**
	 * 群组成员gridadapter
	 */
	public class MemberGridAdapter extends BaseAdapter {
		private LayoutInflater mLayoutInflater;
		private List<SearchedMemberBean> memberList;
		private boolean isOwner;
		public MemberGridAdapter(List<SearchedMemberBean> memberList,Context mContext) {
			this.memberList = memberList;
			this.mLayoutInflater = LayoutInflater.from(mContext);
			this.isOwner = group.getOwner().equals(EMChatManager.getInstance().getCurrentUser()) || group.isAllowInvites();
		}

		@Override
		public int getCount() {
			// 如果不是创建者或者没有相应权限，不提供加减人按钮
			return memberList.size() + (isOwner?1:0);
		}

		@Override
		public String getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			MyGridViewHolder viewHolder;
			if (convertView == null) {
				viewHolder = new MyGridViewHolder();
				convertView = mLayoutInflater.inflate(R.layout.griditem_member_delete,null);
				viewHolder.imageView = (ImageView) convertView
						.findViewById(R.id.imageview);
				viewHolder.owner_iv = (ImageView) convertView
						.findViewById(R.id.owner_iv);
				
				viewHolder.name_tv = (TextView) convertView
						.findViewById(R.id.name_tv);
				viewHolder.delete_btn = convertView
						.findViewById(R.id.delete_btn);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (MyGridViewHolder) convertView.getTag();
			}
			if(position < memberList.size()){
				//这里面都是安全的 用户bean
				final SearchedMemberBean memberBean = memberList.get(position);

				viewHolder.owner_iv.setVisibility(memberBean.getMemberId().equals(group.getOwner())?View.VISIBLE:View.GONE);
				
				
				if(getUserID().equals(group.getOwner())){
					//我是群主 
					viewHolder.delete_btn.setVisibility(memberBean.getMemberId().equals(group.getOwner())?View.GONE:View.VISIBLE);
					
					
					viewHolder.delete_btn.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							MenuTwoButtonDialog dialog = new MenuTwoButtonDialog(GroupDetailsActivity.this, new MenuTwoButtonDialog.ButtonClick() {
								
								@Override
								public void onSureButtonClick(int index) {
									// TODO Auto-generated method stub
									progress.show();
									switch (index) {
									case 0:
										//踢出
										new Thread(new RemoveMemberRun(position, memberBean.getMemberId(),false)).start();
										break;
									default:
										//踢出并拉黑
										new Thread(new RemoveMemberRun(position, memberBean.getMemberId(),true)).start();
										break;
									}
								}
							});
							dialog.title_tv.setText("踢出");
							dialog.tv2.setText("踢出并拉黑");
							dialog.show();
						
						}
					});
				}else{
					//去掉删除按钮
					viewHolder.delete_btn.setVisibility(View.GONE);
					
				}
			
				
				viewHolder.name_tv.setText(""+memberBean.getMemberName());
				
				ImageLoader.getInstance().displayImage(memberBean.getMemberAvatar().findSmallUrl(),
						viewHolder.imageView);

			}else{
				viewHolder.owner_iv.setVisibility(View.GONE);
				
				//加号图片 去掉删除按钮
				viewHolder.delete_btn.setVisibility(View.GONE);
				
				viewHolder.name_tv.setText("");
				
				viewHolder.imageView.setImageResource(R.drawable.group_edit_member_add);
			}
		
			
			return convertView;
		}
	}
	private static class MyGridViewHolder {
		private ImageView imageView,owner_iv;
		private View delete_btn;
		private TextView name_tv;
	}
	



	private class RemoveMemberRun implements Runnable {
		private String hisUserID;
		private int index;
		private boolean blockUser;
		public RemoveMemberRun(int index,String hisUserID,boolean blockUser) {
			this.hisUserID =  hisUserID;
			this.index = index;
			this.blockUser = blockUser;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				EMGroupManager.getInstance().removeUserFromGroup(groupId, hisUserID);
				if (blockUser) {
					EMGroupManager.getInstance().blockUser(groupId, hisUserID);
				}
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						progress.dismiss();
						//群主踢出别人成功
						memberlist.remove(index);
						adapter.notifyDataSetChanged();
					}
				});
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}



	 /**
	  * 访问环信的服务器，更新这个群的信息（同时更新本地缓存）
	  */
	private void updateGroup() {
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					final EMGroup returnGroup = EMGroupManager.getInstance().getGroupFromServer(groupId);
					// 更新本地数据
					EMGroupManager.getInstance().createOrUpdateLocalGroup(returnGroup);

					runOnUiThread(new Runnable() {
						public void run() {
							setGroupInfo();
							new Thread(new GetMemberInfoRun(ValueUtil.ArrayListToString(group.getMembers()), groupId)).start();
						}
					});

				} catch (Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
							loadingPB.setVisibility(View.INVISIBLE);
						}
					});
				}
			}
		}).start();
	}

	private void setGroupInfo(){
		 TextView count_tv = (TextView)findViewById(R.id.count_tv);
		 if (group.getAffiliationsCount()>0) {			
			 count_tv.setText(group.getAffiliationsCount() +"人");
		}
		 TextView name_tv = (TextView)findViewById(R.id.name_tv);
		 name_tv.setText(group.getGroupName());
		onPhotoUploadFail(group_avator);
		switchButton.setSwitchOn(group.isMsgBlocked());
	}
	
	/**
	 * 访问我们自己的服务器获取群组里所有用户的信息
	 */
	private class GetMemberInfoRun implements Runnable {
		private String groupId;
		private String memberIds;
		public GetMemberInfoRun(String memberIds,String groupId) {
			this.memberIds =  memberIds;
			this.groupId = groupId;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			GroupDetailResponse response = null;
	
			try {
				Map<String, String> data = new HashMap<String, String>();
				data.put("groupid", groupId);
				data.put("memberids", memberIds);
				data.put("userid", getUserID());
				response = JsonParser.getGroupDetailResponse(HttpUtil.postMsg(
						HttpUtil.getData(data), HttpUtil.IP + "chat/groupdetail"));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (response == null ) {
				response = new GroupDetailResponse();
				response.setMessage("网络访问失败");
			}
			handler.sendMessage(handler.obtainMessage(MEMBER_INFO_RESPONSE, response));
		}
	}

	
	/**
	 * 访问我们自己的服务器需改群简介
	 */
	private class UpdateGroupDescriptionRun implements Runnable {
		private String groupId;
		private String description;
		public UpdateGroupDescriptionRun(String groupId,String description) {
			this.description =  description;
			this.groupId = groupId;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			BaseResponse response = null;
	
			try {
				Map<String, String> data = new HashMap<String, String>();
				data.put("groupid", groupId);
				data.put("description", description);
				data.put("userid", getUserID());
				response = JsonParser.getBaseResponse(HttpUtil.postMsg(
						HttpUtil.getData(data), HttpUtil.IP + "chat/updategroup"));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (response == null ) {
				response = new BaseResponse();
				response.setMessage("网络访问失败");
			}
			handler.sendMessage(handler.obtainMessage(UPDETE_DESC_RESPONSE, response));
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
			//更新环信
			progress.show();
			new Thread(new UpdateGroupDescriptionRun(groupId, imageUrl)).start();
	}

	@Override
	public void onPhotoUploadFail(ImageView currentImageView) {
		ImageLoader.getInstance().displayImage(HttpUtil.IP_NOAPI+group.getDescription() ,currentImageView ,groupAvatarOptions);
	}

}
