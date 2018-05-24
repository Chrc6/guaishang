package com.houwei.guaishang.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.houwei.guaishang.MessageEvent;
import com.houwei.guaishang.R;
import com.houwei.guaishang.bean.FloatResponse;
import com.houwei.guaishang.bean.IntResponse;
import com.houwei.guaishang.bean.PraiseResponse;
import com.houwei.guaishang.bean.StringResponse;
import com.houwei.guaishang.bean.TopicBean;
import com.houwei.guaishang.bean.TopicListResponse;
import com.houwei.guaishang.bean.UserBean;
import com.houwei.guaishang.database.DaoHelper;
import com.houwei.guaishang.database.entity.HomeTopicCacheData;
import com.houwei.guaishang.database.entity.HomeTopicCacheDataDao;
import com.houwei.guaishang.easemob.PreferenceManager;
import com.houwei.guaishang.event.LoginSuccessEvent;
import com.houwei.guaishang.event.LogouSuccess;
import com.houwei.guaishang.layout.PictureGridLayout;
import com.houwei.guaishang.layout.PraiseTextView;
import com.houwei.guaishang.layout.RedPacketDialog;
import com.houwei.guaishang.layout.TopicAdapter;
import com.houwei.guaishang.manager.FollowManager.FollowListener;
import com.houwei.guaishang.manager.HomeManager.TopicPayRequireListener;
import com.houwei.guaishang.manager.HomeManager.TopicPayedListener;
import com.houwei.guaishang.manager.HomeManager.TopicPraiseCountChangeListener;
import com.houwei.guaishang.manager.ITopicApplication;
import com.houwei.guaishang.sp.DataStorage;
import com.houwei.guaishang.tools.ApplicationProvider;
import com.houwei.guaishang.tools.HttpUtil;
import com.houwei.guaishang.tools.JsonParser;
import com.houwei.guaishang.video.Utils;
import com.houwei.guaishang.views.PagedListView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseTopicFragment extends BaseFragment implements
		TopicPraiseCountChangeListener, TopicPayedListener, FollowListener  {
	
	protected PagedListView listView;
	protected ITopicApplication app;
	private final static int TOPIC_DELECT = 0x27;
	private int pageNumber = 1;
	protected List<TopicBean> list;
	protected TopicAdapter adapter;

	// -1:默认 0:首页"全部" 1:首页"已订单"
	protected int DEFAULT_REFRESH_TYPE = -1;

	private MyHandler handler = new MyHandler(this);

	private  class MyHandler extends Handler {

		private WeakReference<BaseTopicFragment> reference;

		public MyHandler(BaseTopicFragment context) {
			reference = new WeakReference<BaseTopicFragment>(context);
		}

		@Override
		public void handleMessage(Message msg) {
			final BaseTopicFragment activity = reference.get();
			if (activity == null || activity.getActivity()==null) {
				return;
			}
			activity.progress.dismiss();
			activity.onNetWorkFinish(msg);
			switch (msg.what) {
			case BaseActivity.NETWORK_SUCCESS_DATA_RIGHT: // 下拉刷新网络返回成功
				TopicListResponse response = (TopicListResponse) msg.obj;
				if (response.isSuccess()) {
					activity.list = response.getData().getItems();

					resetListOrder();

					Log.i("WXCH","list:"+activity.list);
					activity.listView.onFinishLoading(response.getData()
							.hasMore());
					activity.adapter = new TopicAdapter(activity.getBaseActivity(), activity.list,getJumpType(),DEFAULT_REFRESH_TYPE);
					activity.adapter
							.setOnTopicBeanDeleteListener(new TopicAdapter.TopicBeanDeleteListener() {

								@Override
								public void onTopicBeanDeleteClick(
										TopicBean topicBean) {
									// TODO Auto-generated method stub
									activity.progress.show();
									activity.deleteTopic(topicBean.getTopicId(),
											activity.getUserID());
								}
							});
					
					activity.adapter.setOnTopicBeanFollowClickListener(new TopicAdapter.TopicBeanFollowClickListener() {
						
						@Override
						public void onTopicBeanFollowClick(TopicBean topicBean) {
							// TODO Auto-generated method stub
							if (activity.checkLogined()) {
								activity.progress.show();
								activity.getITopicApplication().getFollowManager().followOnThread(activity.getUserID(),
										topicBean.getMemberId());
							}
						}
					});

					
					activity.adapter.setOnRedPacketClickListener(new PictureGridLayout.RedPacketClickListener() {
						
						@Override
						public void onRedPacketClick(final String topicId,final String to_memberid) {
							// TODO Auto-generated method stub
							activity.onRedPacketClick(topicId, to_memberid);
						}
					});

					/*activity.adapter.setOnTopicBeanFollowClickListener(new TopicAdapter.TopicBeanFollowClickListener() {

						@Override
						public void onTopicBeanFollowClick(TopicBean topicBean) {
							// TODO Auto-generated method stub
							if (activity.checkLogined()) {
								activity.progress.show();
								activity.getITopicApplication().getFollowManager().followOnThread(activity.getUserID(),
										topicBean.getMemberId());
							}
						}
					});*/

					activity.listView.setAdapter(activity.adapter);
					activity.pageNumber = 2;
					activity.onRefreshNetWorkSuccess(activity.list);
				} else {
					activity.listView.onFinishLoading(false);
					activity.showErrorToast(response.getMessage());
				}
				break;
			case BaseActivity.NETWORK_SUCCESS_PAGER_RIGHT: // 翻页网络返回成功
				TopicListResponse pageResponse = (TopicListResponse) msg.obj;
				if (pageResponse.isSuccess()) {
					List<TopicBean> tempList = pageResponse.getData()
							.getItems();
					activity.list.addAll(tempList);
					activity.adapter.notifyDataSetChanged();
					// 如果子类是hisRootActivity，不走下拉刷新
					if (activity.adapter.getOnTopicBeanDeleteListener() == null) {
						activity.adapter
								.setOnTopicBeanDeleteListener(new TopicAdapter.TopicBeanDeleteListener() {

									@Override
									public void onTopicBeanDeleteClick(
											TopicBean topicBean) {
										// TODO Auto-generated method stub
										activity.progress.show();
										activity.deleteTopic(
												topicBean.getTopicId(),
												activity.getUserID());
									}
								});
					}

					
					if(activity.adapter.getOnRedPacketClickListener() == null){
						activity.adapter.setOnRedPacketClickListener(new PictureGridLayout.RedPacketClickListener() {
							
							@Override
							public void onRedPacketClick(final String topicId,final String to_memberid) {
								// TODO Auto-generated method stub
								activity.onRedPacketClick(topicId, to_memberid);
							}
						});
					} 

					if(activity.adapter.getOnTopicBeanFollowClickListener() == null){
						activity.adapter.setOnTopicBeanFollowClickListener(new TopicAdapter.TopicBeanFollowClickListener() {
						
						@Override
						public void onTopicBeanFollowClick(TopicBean topicBean) {
							// TODO Auto-generated method stub
							if (activity.checkLogined()) {
								activity.progress.show();
								activity.getITopicApplication().getFollowManager().followOnThread(activity.getUserID(),
										topicBean.getMemberId());
							}
						}
						});
					}
					
					activity.listView.onFinishLoading(pageResponse.getData().hasMore());
					activity.pageNumber++;
					activity.onPagedNetWorkSuccess(activity.list);
				} else {
					activity.listView.onFinishLoading(false);
					activity.showErrorToast(pageResponse.getMessage());
				}
				break;
			case TOPIC_DELECT: // 删除某条动态 接口返回
				StringResponse topicResponse = (StringResponse) msg.obj;
				if (topicResponse.isSuccess() && activity.list != null) {
					for (TopicBean dynamicBean : activity.list) {
						if (topicResponse.getTag().equals(
								dynamicBean.getTopicId())) {
							activity.list.remove(dynamicBean);
							activity.adapter.notifyDataSetChanged();

							UserBean ub = activity.getITopicApplication()
									.getMyUserBeanManager().getInstance();
							ub.setTopicCount(ub.getTopicCount() - 1);
							activity.getITopicApplication()
									.getMyUserBeanManager().storeUserInfo(ub);
							activity.getITopicApplication()
									.getMyUserBeanManager()
									.notityUserInfoChanged(ub);

							break;
						}
					}
				} else {
					activity.showErrorToast(topicResponse.getMessage());
				}
				break;
			default:
				activity.showErrorToast();
				activity.listView.onFinishLoading(false);
				break;
			}

		}
	}

	//对返回的数据重新排序，自己发的单没有签合同的一律放到排序第一个
	private void resetListOrder() {
		if (list == null || list.size() == 0) return;
		ArrayList<TopicBean> beans = new ArrayList<>();
		String userId = "";
		if (getITopicApplication().getMyUserBeanManager() != null
				&& getITopicApplication().getMyUserBeanManager().getInstance() != null) {
			userId = getITopicApplication().getMyUserBeanManager().getInstance().getUserid();
		}
		for (int i = 0; i < list.size(); i++) {
			TopicBean bean = list.get(i);
			if (userId.equals(bean.getMemberId())) {
				beans.add(bean);
				list.remove(i);
				i--;
			}
		}
		list.addAll(0,beans);

		resetListOrderNext();
	}
	private void resetListOrderNext() {
		if (list == null || list.size() == 0) return;
		ArrayList<TopicBean> beans = new ArrayList<>();
		for (int i = 0; i < list.size(); i++) {
			TopicBean bean = list.get(i);
			if (bean.getIsOffer().equals("1")) {
				beans.add(bean);
				list.remove(i);
				i--;
			}
		}
		list.addAll(0,beans);
		resetListOrderFinal();
	}
	private void resetListOrderFinal() {
		if (list == null || list.size() == 0) return;
		String topicId = DataStorage.getCurrentTopicId();
		ArrayList<TopicBean> beans = new ArrayList<>();
		for (int i = 0; i < list.size(); i++) {
			TopicBean bean = list.get(i);
			if (bean.getTopicId().equals(topicId)) {
				beans.add(bean);
				list.remove(i);
				i--;
			}
		}
		list.addAll(0,beans);
	}

	;

	/**
	 * 网络访问成功
	 */
	public abstract void onNetWorkFinish(Message msg);

	/**
	 * 下拉刷新 且 网络访问成功
	 */
	public abstract void onRefreshNetWorkSuccess(List<TopicBean> list);

	/**
	 * 翻页 且 网络访问成功
	 */
	public void onPagedNetWorkSuccess(List<TopicBean> list) {

	}

	/**
	 * 如果重写并返回id，表示查询某人的发布过的动态列表
	 */
	public String getTargetMemberId() {
		return "0";
	}

	protected void initListener() {
		// TODO Auto-generated method stub
		listView.setOnLoadMoreListener(new PagedListView.OnLoadMoreListener() {

			@Override
			public void onLoadMoreItems() {
				// TODO Auto-generated method stub
				new Thread(pageRun).start();
			}
		});
	}

	protected void initView() {
		// TODO Auto-generated method stub
		if (!EventBus.getDefault().isRegistered(this)){
			EventBus.getDefault().isRegistered(this);
		}
		initProgressDialog();
		app = getITopicApplication();
		getITopicApplication().getHomeManager().addOnTopicPraiseCountChangeListener(this);
		getITopicApplication().getHomeManager().addOnTopicPayedListener(this);
		getITopicApplication().getFollowManager().addFollowListener(this);
		
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (EventBus.getDefault().isRegistered(this)){
			EventBus.getDefault().unregister(this);
		}
	}

	private void onRedPacketClick(final String topicId, final String to_memberid){
		if(checkLogined()){
			RedPacketDialog followDialog = new RedPacketDialog(
					getBaseActivity(), new RedPacketDialog.SureButtonClick() {

						@Override
						public void onSureButtonClick(float price) {
							// TODO Auto-generated method stub
							 //全用虚拟金钱抵消
							progress.show();

							Map<String, String> mapOptional = new HashMap<String, String>();
							mapOptional.put("userid", getUserID());
							mapOptional.put("topicid", topicId);
							mapOptional.put("price", ""+price);
							mapOptional.put("to_memberid", to_memberid);
							
							getITopicApplication().getHomeManager().payByIdeal(mapOptional,new RequireFinishListener());
						}
					});
			followDialog.show();
		}
	}
	
	protected Runnable run = new Runnable() {

		@SuppressWarnings("unchecked")
		public void run() {
			// TODO Auto-generated method stub

			if (Utils.isNetworkConnected(getContext())) {
				String responseData = "";
				TopicListResponse response = null;
				try {
					HashMap<String, String> data = new HashMap<String, String>();
					if(!TextUtils.isEmpty(BaseTopicFragment.this.getUserID())){
						data.put("userid", BaseTopicFragment.this.getUserID());
					}
					data.put("page", "1");
					data.put("showpraises", "0");
					data.put("showcomments", "1");
					data.put("memberid", "" + getTargetMemberId());
					data.put("brandid",  ((BaseActivity)getActivity()).getBrandId());
					data.putAll(getRequireHashMap());
					//Log.i("WXCH","url:"+(HttpUtil.IP + getApi() + "?" + HttpUtil.getData(data)));
					responseData = HttpUtil.getMsg(HttpUtil.IP + getApi() + "?" + HttpUtil.getData(data));
					response = JsonParser.getTopicListResponse(responseData);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (response != null) {
					if (DEFAULT_REFRESH_TYPE != -1) {
						beginCache(responseData,DEFAULT_REFRESH_TYPE);
					}
					handler.sendMessage(handler.obtainMessage(
							BaseActivity.NETWORK_SUCCESS_DATA_RIGHT, response));
				} else {
					handler.sendEmptyMessage(BaseActivity.NETWORK_FAIL);
				}
			} else  {
				String data = "";
				List<HomeTopicCacheData> homeTopicCacheDatas
						= DaoHelper.getDaoHelper().getHomeTopicCacheDataDao()
								.queryBuilder()
								.where(HomeTopicCacheDataDao.Properties.Type.eq(0)).build().list();
//				String data = StrategyDataBaseHelper.getInstance().getTopicHomeDataByType(0);
				if (homeTopicCacheDatas != null && homeTopicCacheDatas.size() > 0) {
					data = homeTopicCacheDatas.get(0).getData();
				}
				if (!TextUtils.isEmpty(data)) {
					TopicListResponse response = JsonParser.getTopicListResponse(data);
					handler.sendMessage(handler.obtainMessage(
							BaseActivity.NETWORK_SUCCESS_DATA_RIGHT, response));
				} else {
					handler.sendEmptyMessage(BaseActivity.NETWORK_FAIL);
				}
			}

		}
	};

	private void beginCache(String responseData,int type) {
		HomeTopicCacheDataDao homeTopicCacheDataDao = DaoHelper.getDaoHelper().getHomeTopicCacheDataDao();
		List<HomeTopicCacheData> homeTopicCacheDatas
				= homeTopicCacheDataDao.queryBuilder()
					.where(HomeTopicCacheDataDao.Properties.Type.eq(type)).build().list();
		for (HomeTopicCacheData homeTopicCacheData : homeTopicCacheDatas) {
			homeTopicCacheDataDao.delete(homeTopicCacheData);
		}

		HomeTopicCacheData homeTopicCacheData = new HomeTopicCacheData(responseData,type);
		DaoHelper.getDaoHelper().getHomeTopicCacheDataDao().insert(homeTopicCacheData);
//					StrategyDataBaseHelper.getInstance().insertTopicHomeData(responseData,0);
	}

	protected Runnable pageRun = new Runnable() {

		@SuppressWarnings("unchecked")
		public void run() {
			// TODO Auto-generated method stub
			TopicListResponse response = null;
			try {
				HashMap<String, String> data = new HashMap<String, String>();
				data.put("userid", BaseTopicFragment.this.getUserID());
				data.put("page", "" + pageNumber);
				data.put("showpraises", "0");
				data.put("showcomments", "1");
				data.put("memberid", "" + getTargetMemberId());
				data.putAll(getRequireHashMap());
				response = JsonParser.getTopicListResponse(HttpUtil
						.getMsg(HttpUtil.IP + getApi() + "?" + HttpUtil.getData(data)));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (response != null) {
				handler.sendMessage(handler.obtainMessage(
						BaseActivity.NETWORK_SUCCESS_PAGER_RIGHT, response));
			} else {
				handler.sendEmptyMessage(BaseActivity.NETWORK_FAIL);
			}
		}
	};

	private class RequireFinishListener implements TopicPayRequireListener{
		
		//支付红包照片网络返回监听
		@Override
		public void onRequireFinish(StringResponse response){
			if (response.isSuccess()) {
				progress.dismiss();
				showSuccessTips("赠送成功！");
			}else{
				progress.dismiss();
				showErrorToast(response.getMessage());
			}
		}
	}
	
	
	/**
	 * 表示调用的接口是getlist（默认） 子类可以修改，比如返回getpraiselist 表示查询我赞的动态
	 */
	protected String getApi() {
		return "topic/getlist";
	}

	/**
	 * 子类可以重写此类来添加请求网络时候的自定义参数
	 */
	protected HashMap<String, String> getRequireHashMap() {
		return new HashMap<String, String>();
	}

	/**
	 * 某条动态的点赞状态改变
	 */
	@Override
	public void onTopicPraiseCountChanged(PraiseResponse interestResponse) {
		// TODO Auto-generated method stub
		if (list == null) {
			return;
		}
		if (!interestResponse.isSuccess()) {
			showErrorToast(interestResponse.getMessage());
		}
		for (int i = 0; i < list.size(); i++) {
			TopicBean bean = list.get(i);
			if (bean.getTopicId().equals(interestResponse.getTopicid())) {
				bean.setPraiseCount(interestResponse.getPraiseCnt());
				bean.setPraised(interestResponse.isStillPraise());
				
				// 刷新界面（只刷新改变的那一个item就行）
				int firstVisiblePosition = listView.getFirstVisiblePosition()
						- listView.getHeaderViewsCount();
				int lastVisiblePosition = listView.getLastVisiblePosition();
				if (i >= firstVisiblePosition && i <= lastVisiblePosition) {
					View view = listView.getChildAt(i - firstVisiblePosition);
					if (view != null) {
						PraiseTextView tv = (PraiseTextView) view.findViewById(R.id.zan_count_btn);
						tv.setPraiseState(getBaseActivity(), bean);
					}
				}
				break;
			}
		}
	}

	@Override
	public void onTopicPayedFinish(String topicID){
		adapter.notifyDataSetChanged();
	}
	
	@Override
	public void onTopicPayedFail(String message){
	}
	
	@Override
	public void FollowChanged(IntResponse followResponse) {
		// TODO Auto-generated method stub
		progress.dismiss();
		if (followResponse.isSuccess()) {			
			if (list != null) {
				for (TopicBean bean : list) {
					if (bean.getMemberId().equals(followResponse.getTag())) {
						bean.setFriendship(followResponse.getData());
					}
				}
				adapter.notifyDataSetChanged();
			}
		}else {
			showErrorToast(followResponse.getMessage());
		}
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		app.getFollowManager().removeFollowListener(this);
		app.getHomeManager().removeOnTopicPraiseCountChangeListener(this);
		app.getHomeManager().removeOnTopicPayedListener(this);
		super.onDestroy();
	}

	// 如果我在动态评论界面里发表了评论。刷新一下这条动态的评论数量
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case TopicDetailActivity.COMMENT_COUNT_CHANGE:
			int topicId = data.getIntExtra("topicId", 0);
			int commentCnt = data.getIntExtra("commentCnt", -1);
			if (commentCnt != -1) {
				for (TopicBean bean : list) {
					if (bean.getTopicId().equals(topicId)) {
						bean.setCommentCount(commentCnt);
						adapter.notifyDataSetChanged();
						break;
					}
				}
			}
			break;
		default:
			break;
		}
	}

	// 刷新
	protected void refresh() {
		new Thread(run).start();
		if (DEFAULT_REFRESH_TYPE == 0) {
			new Thread(checkPointRunnable).start();
		}
	}

	// 删除某条动态
	private void deleteTopic(String topicID, String uid) {
		new Thread(new DeleteTopicRun(topicID, uid)).start();
	}

	private class DeleteTopicRun implements Runnable {
		private String topicID;
		private String uid;

		public DeleteTopicRun(String topicID, String uid) {
			this.topicID = topicID;
			this.uid = uid;
		}

		@SuppressWarnings("unchecked")
		public void run() {
			// TODO Auto-generated method stub
			StringResponse response = null;
			try {
				HashMap<String, String> data = new HashMap<String, String>();
				data.put("userid", uid);
				data.put("topicid", topicID);
				response = JsonParser.getStringResponse(HttpUtil.postMsg(
						HttpUtil.getData(data), HttpUtil.IP + "topic/delete"));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (response == null) {
				response = new StringResponse();
				response.setMessage("网络访问失败");
			}
			response.setTag(topicID);
			handler.sendMessage(handler.obtainMessage(TOPIC_DELECT, response));
		}
	};
//	根据不同type访问评论详情和我的订单详情
	protected  int getJumpType(){
		return 0;
	}

	public Runnable checkPointRunnable = new Runnable() {
		@Override
		public void run() {
			FloatResponse response = null;
			try {
//				HashMap<String, String> data = new HashMap<String, String>();
//				data.put("userid", getITopicApplication().getMyUserBeanManager().getInstance().getUserid());
//				response = JsonParser.getIntResponse(HttpUtil.getMsg(HttpUtil.IP + "mission/pointsearch?"+HttpUtil.getData(data)));
				HashMap<String, String> data = new HashMap<String, String>();
				data.put("userid", getITopicApplication().getMyUserBeanManager().getInstance().getUserid());
				response = JsonParser.getFloatResponse(HttpUtil.getMsg(HttpUtil.IP + "money/search?"+HttpUtil.getData(data)));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (response != null) {
				PreferenceManager.getInstance().setUserPoint(response.getData());
			}
		}
	};

	//接收登录登出事件
	@Subscribe(threadMode = ThreadMode.MAIN)
	public void on3EventMainThread(LoginSuccessEvent event){
		refresh();
	}
	@Subscribe(threadMode = ThreadMode.MAIN)
	public void on3EventMainThread(LogouSuccess event){
		refresh();
	}
}
