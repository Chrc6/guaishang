package com.houwei.guaishang.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import com.houwei.guaishang.R;
import com.houwei.guaishang.activity.BaseActivity;
import com.houwei.guaishang.bean.PraiseData;
import com.houwei.guaishang.bean.PraiseResponse;
import com.houwei.guaishang.bean.StringResponse;
import com.houwei.guaishang.bean.TopicBean;
import com.houwei.guaishang.tools.HttpUtil;
import com.houwei.guaishang.tools.JsonParser;
import com.houwei.guaishang.tools.LogUtil;

public class HomeManager {


	private final static int TOPIC_DELECT = 0x07;
	private final static int TOPIC_DELECT_FAIL = 0x08;
	private final static int TOPIC_Praise = 0x10;
	private final static int TOPIC_IDEAPAY = 0x39;
	
	//删除动态完成的 的观察者 
	private ArrayList<TopicDeleteListener> onTopicDeleteListenerList;
	
	//查看红包动态完成 + 本地缓存好已付款动态之后 的观察者 
	private ArrayList<TopicPayedListener> onTopicPayedListenerList;

	//点赞动作 + 本地数据处理好之后 的观察者
	private ArrayList<TopicPraiseCountChangeListener> onTopicPraiseCountChangeListenerList;
	 
	//支付红包照片网络返回监听
	private TopicPayRequireListener topicPayRequireListener;
	
	private JsonParser jp = new JsonParser();
	private ITopicApplication mApp;

	private Set<String>  paidTopicPhotoArray;
	
	public HomeManager(ITopicApplication mApp) {
		this.mApp = mApp;
		this.onTopicDeleteListenerList = new ArrayList<HomeManager.TopicDeleteListener>();
		this.onTopicPraiseCountChangeListenerList = new ArrayList<HomeManager.TopicPraiseCountChangeListener>();
		
		this.onTopicPayedListenerList = new ArrayList<HomeManager.TopicPayedListener>();
		
		this.resetPaidTopicPhotoArray();
	}

	private Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			
			case TOPIC_DELECT:
				for (TopicDeleteListener onTopicDeleteListener : onTopicDeleteListenerList) {
					onTopicDeleteListener.onTopicDeleteFinish(msg.arg1, true);
				}
				break;
			case TOPIC_DELECT_FAIL:
				for (TopicDeleteListener onTopicDeleteListener : onTopicDeleteListenerList) {
					onTopicDeleteListener.onTopicDeleteFinish(msg.arg1, false);
				}
				break;
			
			case TOPIC_Praise:
				PraiseResponse praiseResult = (PraiseResponse) msg.obj;
				if (!praiseResult.isSuccess()) {
					//点赞网络失败
					for (TopicPraiseCountChangeListener onTopicPraiseCountChangeListener : onTopicPraiseCountChangeListenerList) {
						onTopicPraiseCountChangeListener.onTopicPraiseCountChanged(praiseResult);
					}
				}
				
				break;
			case TOPIC_IDEAPAY://支付网络返回
				StringResponse payResponse = (StringResponse)msg.obj;
				if (topicPayRequireListener!=null) {
					//最后一次网络调用者，给他回调，一般用来弹出错误提示，和dialog dismiss 
					topicPayRequireListener.onRequireFinish(payResponse);
				}
				if (payResponse.isSuccess()) {
					//更新NSUserDefaults 里记录的已付款
					topicPhotoHadPaid(payResponse.getData());
					//发布广播者，一般用来展示图片
					for (TopicPayedListener onTopicPayedListener : onTopicPayedListenerList) {
						onTopicPayedListener.onTopicPayedFinish(payResponse.getData());
					}
				}else{
					//发布广播者
					for (TopicPayedListener onTopicPayedListener : onTopicPayedListenerList) {
						onTopicPayedListener.onTopicPayedFail(payResponse.getMessage());
					}
				}
				break;
			}
		}
	};



	/**
	 * 处理的比较好的 点赞取消赞效果（微信微博） 是不弹出dialog的但是带动画，但是点赞需要通知服务器同步
	 * 
	 * 先立刻调用动画效果，然后本地直接模拟一个网络成功返回数据，再用观察者模式刷新所有activity包含该动态的点赞状态
	 * 然后发出网络请求
	 * 如果网络请求成功，什么都不做
	 * 如果网络失败，状态弹回之前的，再用观察者模式刷新所有activity包含该动态的点赞状态
	 * @param bean
	 * @param uid
	 */
	public void startPraiseTopic(TopicBean bean, String uid) {
		PraiseResponse response  =  new PraiseResponse();
		response.setCode(1);
		response.setTopicid(bean.getTopicId());
		if (bean.isPraised()) {//之前是点过的
			response.setStillPraise(false);
			response.setPraiseCnt(bean.getPraiseCount()-1);
		} else {
			response.setStillPraise(true);
			response.setPraiseCnt(bean.getPraiseCount()+1);
		}
		
		for (TopicPraiseCountChangeListener onTopicPraiseCountChangeListener : onTopicPraiseCountChangeListenerList) {
			onTopicPraiseCountChangeListener.onTopicPraiseCountChanged(response);
		}
		new Thread(new PraiseTopicRun(response, uid)).start();
	}

	private class PraiseTopicRun implements Runnable {
		private PraiseResponse preResponse;
		private String uid;
		public PraiseTopicRun(PraiseResponse preResponse,  String uid) {
			this.preResponse = preResponse;
			this.uid = uid;
		}

		@SuppressWarnings("unchecked")
		public void run() {
			// TODO Auto-generated method stub
			PraiseResponse response = null;
			try {
				HashMap<String, String> data = new HashMap<String, String>();
				data.put("userid", uid);
				data.put("topicid", ""+preResponse.getTopicid());
				response = jp.getPraiseResponse(HttpUtil.postMsg(
						HttpUtil.getData(data), HttpUtil.IP + "topic/praise"));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (response == null) {
				response = new PraiseResponse();
				response.setMessage("网络访问失败");
			}
			
			response.setTopicid(preResponse.getTopicid());
			if (!response.isSuccess()) {
				//	 * 如果网络失败，状态弹回之前的，再用观察者模式刷新该动态的点赞状态
				response.setStillPraise(!preResponse.isStillPraise());
				response.setPraiseCnt(preResponse.isStillPraise()?preResponse.getPraiseCnt()-1:preResponse.getPraiseCnt()+1);
			}

			handler.sendMessage(handler.obtainMessage(TOPIC_Praise, response));
		}
	};

	
	/**********
	 * 付款看红包照片 start
	 * @param mapOptional
	 */
	public interface TopicPayRequireListener{
		//支付红包照片网络返回监听
		public void onRequireFinish(StringResponse response);
	}
	
	public void payByIdeal(Map<String, String> mapOptional,TopicPayRequireListener topicPayRequireListener) {
		this.topicPayRequireListener = topicPayRequireListener;
		new Thread(new PayByIdealRun(mapOptional)).start();
	}

	private class PayByIdealRun implements Runnable {
		private Map<String, String> mapOptional;

		public PayByIdealRun(Map<String, String> mapOptional) {
			this.mapOptional = mapOptional;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			StringResponse response = null;
			try {
				response = JsonParser.getStringResponse(HttpUtil.postMsg(
						HttpUtil.getData(mapOptional), HttpUtil.IP
								+ "order/payideal"));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (response == null) {
				response = new StringResponse();
				response.setMessage("网络访问失败");
			}
			response.setData(mapOptional.get("topicid"));
			handler.sendMessage(handler
					.obtainMessage(TOPIC_IDEAPAY, response));
		}
	}
	/**
	 * 付款看红包照片 END
	 * ************
	 */

	
	/**
	 * 话题删除 start
	 * 
	 * @author acer
	 * 
	 */
	public interface TopicDeleteListener {
		public void onTopicDeleteFinish(int topicID, boolean success);
	}

	public void addOnTopicDeleteListener(
			TopicDeleteListener onTopicDeleteListener) {
		if (!onTopicDeleteListenerList.contains(onTopicDeleteListener)) {
			onTopicDeleteListenerList.add(onTopicDeleteListener);
		}
	}

	public void removeOnTopicDeleteListener(
			TopicDeleteListener onTopicDeleteListener) {
		if (onTopicDeleteListener != null
				&& onTopicDeleteListenerList.contains(onTopicDeleteListener)) {
			this.onTopicDeleteListenerList.remove(onTopicDeleteListener);
		}
	}

	/**
	 * 话题删除 end
	 * 
	 * @author acer
	 */

	/*********************************************************************************/

	
	/**
	 * 赞 start
	 * 
	 * @author acer
	 * 
	 */
	public interface TopicPraiseCountChangeListener {
		public void onTopicPraiseCountChanged(PraiseResponse regResult);
	}

	public void addOnTopicPraiseCountChangeListener(
			TopicPraiseCountChangeListener onTopicPraiseCountChangeListener) {
		if (!onTopicPraiseCountChangeListenerList
				.contains(onTopicPraiseCountChangeListener)) {
			this.onTopicPraiseCountChangeListenerList
					.add(onTopicPraiseCountChangeListener);
		}
	}

	public void removeOnTopicPraiseCountChangeListener(
			TopicPraiseCountChangeListener onTopicPraiseCountChangeListener) {
		if (onTopicPraiseCountChangeListener != null
				&& onTopicPraiseCountChangeListenerList
						.contains(onTopicPraiseCountChangeListener)) {
			this.onTopicPraiseCountChangeListenerList
					.remove(onTopicPraiseCountChangeListener);
		}
	}

	/**
	 * 赞 end
	 * 
	 * @author acer
	 * 
	 */
	/*********************************************************************************/
	
	
	/**
	 * 话题支付成功 start
	 * 
	 * @author acer
	 * 
	 */
	
	public interface TopicPayedListener {
		public void onTopicPayedFinish(String topicID);
		public void onTopicPayedFail(String message);
	}

	public void addOnTopicPayedListener(
			TopicPayedListener onTopicPayedListener) {
		if (!onTopicPayedListenerList.contains(onTopicPayedListener)) {
			onTopicPayedListenerList.add(onTopicPayedListener);
		}
	}

	public void removeOnTopicPayedListener(
			TopicPayedListener onTopicPayedListener) {
		if (onTopicPayedListener != null
				&& onTopicPayedListenerList.contains(onTopicPayedListener)) {
			this.onTopicPayedListenerList.remove(onTopicPayedListener);
		}
	}

	/**
	 * 话题支付成功 end
	 * 
	 * @author acer
	 */

	/*********************************************************************************/
	
	
	
	/**
	 * 动态红包 start
	 */
	public boolean checkNeedPay(String topicId,String ownerId){
		if (ownerId.equals(mApp.getMyUserBeanManager().getUserId())) {
			return false;
		}
		return !paidTopicPhotoArray.contains(topicId);
	}

	//通过NSUserDefaults 记录已经付款的红包
	public void resetPaidTopicPhotoArray() {
		if (mApp.getMyUserBeanManager().getInstance() == null) {
			paidTopicPhotoArray = new HashSet<String>();
		}else{
			//根据当前登录的账号，获取他的支付记录list<String>
			SharedPreferences pref = mApp.getSharedPreferences(mApp.getMyUserBeanManager().getUserId()+"_PAYED_TOPIC_PHOTO",Activity.MODE_PRIVATE);
			paidTopicPhotoArray = pref.getStringSet("PAYED_TOPIC_PHOTO", new HashSet<String>());
		}
	}

	/**
	 * //更新NSUserDefaults 里记录的已付款
	 */
	public void topicPhotoHadPaid(String topicId) {
		
		String topicPhotoKey = mApp.getMyUserBeanManager().getUserId()+"_PAYED_TOPIC_PHOTO";
		
		//根据当前登录的账号，获取他的签到日期list<String>
		SharedPreferences pref = mApp.getSharedPreferences(topicPhotoKey,Activity.MODE_PRIVATE);
		Set<String> signArray = pref.getStringSet("PAYED_TOPIC_PHOTO", new HashSet<String>());
		signArray.add(topicId);
		
		paidTopicPhotoArray = signArray;
		
		Editor editor =  pref.edit().clear();
		editor.putStringSet("PAYED_TOPIC_PHOTO", signArray);
		editor.commit();
		
	}
	/**
	 * 动态红包 end
	 */


	
}
