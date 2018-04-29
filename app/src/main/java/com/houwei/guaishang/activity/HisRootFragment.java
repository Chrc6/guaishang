package com.houwei.guaishang.activity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.houwei.guaishang.R;
import com.houwei.guaishang.bean.AvatarBean;
import com.houwei.guaishang.bean.HisInfoResponse;
import com.houwei.guaishang.bean.IntResponse;
import com.houwei.guaishang.bean.TopicBean;
import com.houwei.guaishang.bean.UserBean;
import com.houwei.guaishang.easemob.EaseConstant;
import com.houwei.guaishang.layout.MenuDialog;
import com.houwei.guaishang.layout.MenuTwoButtonDialog;
import com.houwei.guaishang.layout.SureOrCancelInterfaceDialog;
import com.houwei.guaishang.layout.TopicAdapter;
import com.houwei.guaishang.manager.FollowManager.FollowListener;
import com.houwei.guaishang.manager.MyUserBeanManager.EditInfoListener;
import com.houwei.guaishang.manager.MyUserBeanManager.UserStateChangeListener;
import com.houwei.guaishang.tools.HttpUtil;
import com.houwei.guaishang.tools.JsonParser;
import com.houwei.guaishang.tools.ValueUtil;
import com.houwei.guaishang.views.ParallaxListView;
import com.easemob.chat.EMContactManager;
import com.easemob.exceptions.EaseMobException;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

//查看他人主页activity
public class HisRootFragment extends BaseTopicFragment implements
		OnClickListener, FollowListener, EditInfoListener, UserStateChangeListener{

	private String hisUserID;
	private String mobile;
	private TextView follow_tv;
	private InfoPersonalHisLayout infoLinearLayout;

	private String currentBackground = "";//当前查看的用户的背景图链接
	private ImageView backgroundImageView;//当前查看的用户的背景图
	private TextView header_name_tv, header_fans_tv, header_follows_tv;

	private MyHandler userhandler = new MyHandler(this);

	private static class MyHandler extends Handler {

		private WeakReference<BaseTopicFragment> reference;

		public MyHandler(BaseTopicFragment context) {
			reference = new WeakReference<BaseTopicFragment>(context);
		}


		@Override
		public void handleMessage(Message msg) {
			final HisRootFragment activity = (HisRootFragment) reference.get();
			if (activity == null) {
				return;
			}
			switch (msg.what) {
			case BaseActivity.NETWORK_OTHER:
				final HisInfoResponse response = (HisInfoResponse) msg.obj;
				if (response.isSuccess()) {

					UserBean bean = response.getData();

					Drawable drawable = null;
					switch (Integer.parseInt(bean.getSex())) {
					case ValueUtil.SEX_MALE:
						drawable = activity.getResources().getDrawable(
								R.drawable.userinfo_icon_male);
						drawable.setBounds(0, 0, drawable.getMinimumWidth(),
								drawable.getMinimumHeight());
						break;
					case ValueUtil.SEX_FEMALE:
						drawable = activity.getResources().getDrawable(
								R.drawable.userinfo_icon_female);
						drawable.setBounds(0, 0, drawable.getMinimumWidth(),
								drawable.getMinimumHeight());
						break;
					default:
						break;
					}

					if (!bean.getBackground().equals(activity.currentBackground)) {
						activity.currentBackground = bean.getBackground();
						ImageLoader.getInstance().displayImage(bean.getBackground(),
								activity.backgroundImageView,activity.getBackgroundOptions());
					}

					activity.header_name_tv.setCompoundDrawables(null, null,
							drawable, null);
					activity.header_fans_tv
							.setText("粉丝 " + bean.getFansCount());
					activity.header_follows_tv.setText("关注 "
							+ bean.getFollowsCount());
					activity.follow_tv.setText(ValueUtil
							.getRelationTypeString(bean.getFriendship()));
					activity.mobile = bean.getMobile();
					if(activity.getActivity()!=null){

						activity.infoLinearLayout.initView(activity.getActivity(), bean);
					}
				} else {
					activity.showFailTips(response.getMessage());
				}

				break;
				
			default:
				activity.showErrorToast();
				break;
			}
		}
	};


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    return inflater.inflate(R.layout.activity_his_root, container, false);
	}
		
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
		initListener();
	}
	
	@Override
	protected void initListener() {
		// TODO Auto-generated method stub
		super.initListener();
		BackButtonListener();
		getView().findViewById(R.id.chat_tv).setOnClickListener(this);
		getView().findViewById(R.id.follow_tv).setOnClickListener(this);
		getView().findViewById(R.id.warn_iv).setOnClickListener(this);
		
		listView.startToGetMore();
		new Thread(inforun).start();
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		super.initView();
		listView = (ParallaxListView) getView().findViewById(R.id.listView);
		ParallaxListView parallaxListView = (ParallaxListView) listView;

		View headerView = LayoutInflater.from(getActivity()).inflate(
				R.layout.layout_parallaxlistview_header, null);
		com.houwei.guaishang.view.CircleImageView avator = (com.houwei.guaishang.view.CircleImageView) headerView.findViewById(R.id.avator);
		header_name_tv = (TextView) headerView
				.findViewById(R.id.header_name_tv);
		header_fans_tv = (TextView) headerView
				.findViewById(R.id.header_fans_tv);
		header_follows_tv = (TextView) headerView
				.findViewById(R.id.header_follows_tv);

		header_name_tv.setText(getActivity().getIntent().getStringExtra(HisRootActivity.HIS_NAME_KEY));

		final AvatarBean avatarBean = (AvatarBean) getActivity().getIntent()
				.getSerializableExtra(HisRootActivity.HIS_AVATAR_KEY);

		ImageLoader.getInstance().displayImage(avatarBean.findOriginalUrl(), avator);
		avator.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(),
						GalleryActivity.class);
				ArrayList<String> urls = new ArrayList<String>();
				urls.add(avatarBean.findOriginalUrl());
				intent.putExtra(GalleryActivity.EXTRA_IMAGE_URLS, urls);
				intent.putExtra(GalleryActivity.EXTRA_IMAGE_INDEX, 0);
				startActivity(intent);
			}
		});

		backgroundImageView = (ImageView)headerView.findViewById(R.id.imageview);
		backgroundImageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				MenuDialog followDialog = new MenuDialog(getActivity(),
						new MenuDialog.ButtonClick() {

							@Override
							public void onSureButtonClick() {
								// TODO Auto-generated method stub
								if (hisUserID.equals(getUserID())) {
									//"更换封面"
									HisRootActivity activty = (HisRootActivity)getActivity();
									activty.pickPhotoFromGallery();
								}else{
									//"设置为我的封面"
									if (checkLogined()) {
										progress.show();
										getITopicApplication().getMyUserBeanManager().
											startEditInfoRun("background", currentBackground, HisRootFragment.this);
									}
								}
							}
						});
				followDialog.title_tv.setText(hisUserID.equals(getUserID())?"更换封面":"设置为我的封面");
				followDialog.show();
				
			}
		});

		headerView.findViewById(R.id.following_ll).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(getActivity(),
								FriendShipActivity.class);
						intent.putExtra("memberid", hisUserID);
						intent.putExtra("isFansList", false);
						startActivity(intent);
					}
				});

		headerView.findViewById(R.id.fans_ll).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(getActivity(),
								FriendShipActivity.class);
						intent.putExtra("memberid", hisUserID);
						intent.putExtra("isFansList", true);
						startActivity(intent);
					}
				});

		infoLinearLayout = new InfoPersonalHisLayout(getActivity());

		parallaxListView.addHeaderView(headerView);
		parallaxListView.addHeaderView(infoLinearLayout);

		list = new ArrayList<TopicBean>();
		adapter = new TopicAdapter(getBaseActivity(), list, 0,0);
		parallaxListView.setAdapter(adapter);

		parallaxListView.setImageViewToParallax(backgroundImageView);

		app.getFollowManager().addFollowListener(this);
		hisUserID = getActivity().getIntent().getStringExtra(HisRootActivity.HIS_ID_KEY);

		if (hisUserID.equals(getUserID())) {
			//当前看的人是我自己，去掉关注，聊天的条
			getView().findViewById(R.id.bottom_rg).setVisibility(View.GONE);
			//去掉举报黑名单
			getView().findViewById(R.id.warn_iv).setVisibility(View.GONE);
			//加载背景图
			currentBackground = getITopicApplication().getMyUserBeanManager().getInstance().getBackground();
			getITopicApplication().getMyUserBeanManager().addOnUserStateChangeListener(this);
			ImageLoader.getInstance().displayImage(currentBackground,backgroundImageView,getBackgroundOptions());
		}

		follow_tv = (TextView) getView().findViewById(R.id.follow_tv);
	}

	private Runnable inforun = new Runnable() {

		public void run() {
			// TODO Auto-generated method stub
			HisInfoResponse response = null;
			try {
				Map<String, String> data = new HashMap<String, String>();
				data.put("memberid", hisUserID);
				data.put("userid", getUserID());
				response = JsonParser.getHisInfoResponse(HttpUtil
						.getMsg(HttpUtil.IP + "user/profile?"
								+ HttpUtil.getData(data)));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (response != null) {
				userhandler.sendMessage(userhandler.obtainMessage(
						BaseActivity.NETWORK_OTHER, response));
			} else {
				userhandler.sendEmptyMessage(BaseActivity.NETWORK_FAIL);
			}
		}
	};

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.chat_tv:
			jumpToChatActivity(hisUserID,getActivity().getIntent().getStringExtra(HisRootActivity.HIS_NAME_KEY),
					(AvatarBean) getActivity().getIntent().getSerializableExtra(HisRootActivity.HIS_AVATAR_KEY),
					EaseConstant.CHATTYPE_SINGLE,mobile,true);
			break;
		case R.id.follow_tv:
			progress.show();
			getITopicApplication().getFollowManager().followOnThread(
					getUserID(), hisUserID);
			break;
		case R.id.warn_iv:
			MenuTwoButtonDialog dialog = new MenuTwoButtonDialog(getActivity(), new MenuTwoButtonDialog.ButtonClick() {
				
				@Override
				public void onSureButtonClick(int index) {
					// TODO Auto-generated method stub
					switch (index) {
					case 0:
						//举报
						Intent i = new Intent(getActivity(), WarningReportActivity.class);
						i.putExtra("hisUserID", hisUserID);
						startActivity(i);
						break;
					default:
						//拉黑
						progress.show();
						new Thread(new Runnable() {
							public void run() {
								try {
									EMContactManager.getInstance().addUserToBlackList(hisUserID, false);
									getActivity().runOnUiThread(new Runnable() {
										public void run() {
											progress.dismiss();
											
											SureOrCancelInterfaceDialog dialog = new SureOrCancelInterfaceDialog(getActivity(), 
													getResources().getString(R.string.black_success), 
													"去看看", "知道了", new SureOrCancelInterfaceDialog.ButtonClick() {
														
														@Override
														public void onSureButtonClick() {
															// TODO Auto-generated method stub
															Intent i = new Intent(getActivity(), BlacklistActivity.class);
															 startActivity(i);
														}
														
														@Override
														public void onCancelButtonClick() {
															// TODO Auto-generated method stub
															
														}
													});
											dialog.show();
										}
									});
								} catch (EaseMobException e) {
									e.printStackTrace();
									getActivity().runOnUiThread(new Runnable() {
										public void run() {
											progress.dismiss();
										}
									});
								}
							}
						}).start();
						break;
					}
				}
			});
			dialog.show();
			break;
		default:
			break;
		}
	}

	@Override
	public void FollowChanged(IntResponse followResponse) {
		// TODO Auto-generated method stub
		progress.dismiss();
		if (followResponse.isSuccess()) {
			if (hisUserID.equals(followResponse.getTag())) {
				follow_tv.setText(ValueUtil
						.getRelationTypeString(followResponse.getData()));
			}
			
			if (list != null) {
				for (TopicBean bean : list) {
					if (bean.getMemberId().equals(followResponse.getTag())) {
						bean.setFriendship(followResponse.getData());
					}
				}
				adapter.notifyDataSetChanged();
			}
		} else {
			showErrorToast(followResponse.getMessage());
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		getITopicApplication().getMyUserBeanManager().removeUserStateChangeListener(this);
		getITopicApplication().getFollowManager().removeFollowListener(this);
		super.onDestroy();
	}

	@Override
	public void onNetWorkFinish(Message msg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRefreshNetWorkSuccess(List<TopicBean> list) {
		// TODO Auto-generated method stub

	}

	/**
	 * 翻页 且 网络访问成功
	 */
	@Override
	public void onPagedNetWorkSuccess(List<TopicBean> list) {
		if (list.isEmpty()) {
			listView.showEmptyFooter(getActivity(), "没有发布话题");
		}

	}

	/**
	 * 如果重写并返回id，表示查询某人的发布过的动态列表
	 */
	public String getTargetMemberId() {
		return hisUserID;
	}

	@Override
	public void onUserInfoChanged(UserBean ub) {
		// TODO Auto-generated method stub
		ImageLoader.getInstance().displayImage(ub.getBackground(),
				backgroundImageView,getBackgroundOptions());
	}

	@Override
	public void onUserLogin(UserBean ub) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onUserLogout() {
		// TODO Auto-generated method stub
	}
	
	
	@Override
	public void onEditFail(String message) {
		// TODO Auto-generated method stub
		progress.dismiss();
		showFailTips(message);
	}

	@Override
	public void onEditSuccess() {
		// TODO Auto-generated method stub
		progress.dismiss();
		if (!hisUserID.equals(getUserID())) {
			showSuccessTips("设置成功");
		}
	}
	
	private DisplayImageOptions getBackgroundOptions() {
		DisplayImageOptions backgroundOptions = new DisplayImageOptions.Builder()
		.cacheInMemory(true).cacheOnDisk(true)
		.showImageForEmptyUri(R.drawable.user_info_picture_4)
		.showImageOnFail(R.drawable.user_info_picture_4)
		.showImageOnLoading(R.drawable.user_info_picture_4)
		.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)// 设置图片以如何的编码方式显示
		.build();
		return backgroundOptions;
	}

}
