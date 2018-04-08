package com.houwei.guaishang.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.houwei.guaishang.R;
import com.houwei.guaishang.bean.CommentPushBean;
import com.houwei.guaishang.bean.FansPushBean;
import com.houwei.guaishang.bean.LocationBean;
import com.houwei.guaishang.bean.NameIDBean;
import com.houwei.guaishang.bean.TopicBean;
import com.houwei.guaishang.bean.UserBean;
import com.houwei.guaishang.data.DBReq;
import com.houwei.guaishang.layout.NameIDDialog;
import com.houwei.guaishang.layout.NameIDDialog.AnswerListener;
import com.houwei.guaishang.manager.ChatManager.OnMyActionMessageGetListener;
import com.houwei.guaishang.manager.ChatManager.OnMyActionMessageHadReadListener;
import com.houwei.guaishang.manager.MyLocationManager.LocationListener;
import com.houwei.guaishang.manager.MyUserBeanManager.UserStateChangeListener;
import com.houwei.guaishang.views.PullToRefreshBase;
import com.houwei.guaishang.views.PullToRefreshPagedListView;
import com.easemob.chat.EMMessage;

public class TopicFragment extends BaseTopicFragment implements
		OnMyActionMessageGetListener, OnMyActionMessageHadReadListener,
		LocationListener, UserStateChangeListener {

	private List<NameIDBean> menuList;// 菜单项
	private LocationBean currentLocationBean;// 当前经纬度位置
	private String targetApi;// 选择菜单后要访问不同的接口api
	private String orderString;// 排序方式，@""表示默认，praise表示按点赞排序
	private String currentSelectId;

	private View mEmptyLayout;// 内容为空显示的emptyview
	private View locationFailLayout;// 定位失败的emptyview
	private PullToRefreshPagedListView pullToRefreshView;
	private TextView unReadReviewTextView;
	private LinearLayout unReadTopicReviewLL;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_topic, container, false);
    }
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
		initListener();
		Log.d("CCC","onActivityCreated");
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		super.initView();
		
		initProgressDialog();

		// 菜单项数据。id是要访问的接口api，name是显示的文本
		NameIDBean model1 = new NameIDBean("0", "全部商品");
		NameIDBean model2 = new NameIDBean("1", "我关注的");
		NameIDBean model3 = new NameIDBean("2", "周边商品");
		NameIDBean model4 = new NameIDBean("3", "热门商品");

		menuList = new ArrayList<NameIDBean>();
		menuList.add(model1);
		menuList.add(model2);
		menuList.add(model3);
		menuList.add(model4);
		targetApi = "topic/getlist";
		orderString = "";
		currentSelectId = "0";// 默认选中id是0
		setTitleName(model1.getName());

		unReadTopicReviewLL = (LinearLayout) getView().findViewById(R.id.unReadTopicReviewLL);
		unReadReviewTextView = (TextView) getView().findViewById(R.id.unReadReviewTextView);

		app.getChatManager().addOnMyActionMessageGetListener(this);
		app.getChatManager().addOnMyActionMessageHadReadListener(this);
		app.getMyUserBeanManager().addOnUserStateChangeListener(this);
		
		checkUnReadActionCount(DBReq.getInstence(getActivity())
				.getTotalUnReadCommentCount());

		pullToRefreshView = (PullToRefreshPagedListView) getView().findViewById(R.id.listView);
		listView = pullToRefreshView.getRefreshableView();

		mEmptyLayout = LayoutInflater.from(getActivity()).inflate(
				R.layout.listview_empty, null);
		locationFailLayout = LayoutInflater.from(getActivity()).inflate(
				R.layout.listview_empty, null);
		TextView textViewMessage = (TextView) locationFailLayout
				.findViewById(R.id.textViewMessage);
		textViewMessage.setText("定位失败！\n请检查是否开启定位权限");

		pullToRefreshView.setRefreshing();
		refresh();
	}

	@Override
	protected void initListener() {
		// TODO Auto-generated method stub
		super.initListener();

		getView().findViewById(R.id.title).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				NameIDDialog sexdialog = new NameIDDialog(getActivity(),
						menuList, "", currentSelectId, new AnswerListener() {

							@Override
							public void onAnswer(NameIDBean selectBean) {
								// TODO Auto-generated method stub
								switch (Integer.parseInt(selectBean.getId())) {
								case 0:
									// @"全部商品"
									targetApi = "topic/getlist";
									orderString = "";
									break;
								case 1:
									// @"我关注的"
									targetApi = "topic/followlist";
									orderString = "";
									break;
								case 2:
									// @"周边商品"
									targetApi = "topic/near";
									orderString = "";
									break;
								case 3:
									// @"热门商品"
									targetApi = "topic/getlist";
									orderString = "praise";
									break;
								default:
									break;
								}

								if (targetApi.equals("topic/followlist")) {
									// 如果点选的是 我关注的商品，需要登录
									if (!checkLogined()) {
										return;
									}
								}
								currentSelectId = selectBean.getId();
								setTitleName(selectBean.getName());
								pullToRefreshView.setRefreshing();
								refresh();
							}
						});
				sexdialog.show();
			}
		});

		pullToRefreshView
				.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2() {

					@Override
					public void onPullDownToRefresh() {
						// 当前查询的是附近的商品，需要先定位
						refresh();
					}

					@Override
					public void onPullUpToRefresh() {

					}
				});

		getView().findViewById(R.id.title_right).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (checkLogined()) {
							Intent i = new Intent(getActivity(),VideoReleaseActivity.class);
							startActivityForResult(i, 1);
						}
					}
				});
		unReadTopicReviewLL.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(getActivity(),
						CommentUnReadActivity.class);
				startActivity(i);
			}
		});
	}

	private void checkUnReadActionCount(int unReadActionCount) {
		// TODO Auto-generated method stub
		unReadReviewTextView.setText("" + unReadActionCount + "条新评论（赞）");
		unReadTopicReviewLL.setVisibility(unReadActionCount == 0 ? View.GONE
				: View.VISIBLE);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		app.getChatManager().removeOnMyActionMessageGetListener(this);
		app.getChatManager().removeOnMyActionMessageHadReadListener(this);
		app.getLocationManager().removeLocationListener(this);
		app.getMyUserBeanManager().removeUserStateChangeListener(this);
		super.onDestroy();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case TopicReleaseActivity.RELEASE_SUCCESS:
			pullToRefreshView.setRefreshing();
			refresh();
			break;
		default:
			break;
		}
	}

	@Override
	public void onNetWorkFinish(Message msg) {
		// TODO Auto-generated method stub
		pullToRefreshView.onRefreshComplete();
	}

	@Override
	public void onRefreshNetWorkSuccess(List<TopicBean> list) {
		// TODO Auto-generated method stub
		pullToRefreshView.setEmptyView(list.isEmpty() ? mEmptyLayout : null);
	}

	@Override
	public void onMyNewFansGet(FansPushBean fansPushBean) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNewCommentGet(CommentPushBean commentPushBean) {
		// TODO Auto-generated method stub
		checkUnReadActionCount(DBReq.getInstence(getActivity())
				.getTotalUnReadCommentCount());
	}

	@Override
	public void onFansHadRead() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCommentsHadRead() {
		// TODO Auto-generated method stub
		checkUnReadActionCount(0);
	}

	/**
	 * 表示调用的接口是getlist（默认） 子类可以修改，比如返回getpraiselist 表示查询我赞的商品
	 */
	@Override
	protected String getApi() {
		return targetApi;
	}

	/**
	 * 子类可以重写此类来添加请求网络时候的自定义参数
	 */
	@Override
	protected HashMap<String, String> getRequireHashMap() {
		HashMap<String, String> params = new HashMap<String, String>();
		if (currentLocationBean != null) {
			params.put("latitude", "" + currentLocationBean.getLatitude());
			params.put("longitude", "" + currentLocationBean.getLongitude());
		}
		params.put("order", orderString);
		return params;
	}

	// 重新请求数据
	@Override
	protected void refresh() {
		// 当前查询的是附近的商品，需要先定位
		if (targetApi.equals("topic/near") && currentLocationBean == null) {
			app.getLocationManager().addLocationListener(this);
			app.getLocationManager().startLoction(false);
		} else {
			new Thread(run).start();
		}
	}

	@Override
	public void onLocationFail() {
		// TODO Auto-generated method stub
		// 结束下拉刷新
		pullToRefreshView.onRefreshComplete();
		// 移除定位监听
		app.getLocationManager().removeLocationListener(this);
		// 让列表清空
		if (list != null) {
			list.clear();
		}
		// 如果一上来就没有走success，adapter是空的
		if (adapter != null) {
			adapter.notifyDataSetChanged();
		}
		// 显示定位失败的emptyview
		pullToRefreshView.setEmptyView(locationFailLayout);
	}

	@Override
	public void onLocationSuccess(LocationBean currentLocationBean) {
		// TODO Auto-generated method stub
		getITopicApplication().getLocationManager()
				.removeLocationListener(this);
		this.currentLocationBean = currentLocationBean;
		new Thread(run).start();
	}

	@Override
	public void onUserInfoChanged(UserBean ub) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUserLogin(UserBean ub) {
		// TODO Auto-generated method stub
		// 登录登出 重新刷新一下，为了重置点赞和红包照片
		pullToRefreshView.setRefreshing();
		refresh();
	}

	@Override
	public void onUserLogout() {
		// TODO Auto-generated method stub
		// 登录登出 重新刷新一下，为了重置点赞和红包照片
		pullToRefreshView.setRefreshing();
		refresh();
	}

}
