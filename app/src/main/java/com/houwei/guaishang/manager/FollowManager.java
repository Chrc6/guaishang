package com.houwei.guaishang.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.houwei.guaishang.activity.BaseActivity;
import com.houwei.guaishang.bean.IntResponse;
import com.houwei.guaishang.bean.UserBean;
import com.houwei.guaishang.layout.SureOrCancelDialog;
import com.houwei.guaishang.tools.HttpUtil;
import com.houwei.guaishang.tools.JsonParser;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

public class FollowManager {
	public final static int NONE = 0;
	public final static int MY_FOLLOWING = 1;
	public final static int MY_FANS = 2;
	public final static int EACH = 3;

	private JsonParser jp = new JsonParser();
	private ITopicApplication mContext;
	private ArrayList<FollowListener> followListenerList;
	private FollowStartListener followStartListener;
	private Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case BaseActivity.NETWORK_SUCCESS_DATA_RIGHT:
				IntResponse intResponse = (IntResponse) msg.obj;

				for (FollowListener followListener : followListenerList) {
					followListener.FollowChanged(intResponse);
				}
				if (intResponse.isSuccess()) {	
					UserBean ub = mContext.getMyUserBeanManager().getInstance();
					if (MY_FOLLOWING == intResponse.getData()|| EACH == intResponse.getData()) {
						ub.setFollowsCount(ub.getFollowsCount()+1);
					} else {
						ub.setFollowsCount(ub.getFollowsCount()-1);
					}
					mContext.getMyUserBeanManager().storeUserInfo(ub);
					mContext.getMyUserBeanManager().notityUserInfoChanged(ub);
				}

				break;

			default:
				break;
			}
		}
	};

	public FollowManager(ITopicApplication mContext) {
		this.mContext = mContext;
		followListenerList = new ArrayList<FollowManager.FollowListener>();
	}

	public void followOnThread(String mineUserID, String toUserID) {

		if (followStartListener != null)
			followStartListener.FollowStart(toUserID);

		new Thread(new FollowRun(mineUserID, toUserID)).start();
	}

	public void showFollowDialog(Context mContext1, final String mineUserID,
			final String toUserID, final int preFoucusType, String dialogTitle) {
		SureOrCancelDialog followDialog = new SureOrCancelDialog(mContext1,
				dialogTitle, "关注TA", new SureOrCancelDialog.SureButtonClick() {

					@Override
					public void onSureButtonClick() {
						// TODO Auto-generated method stub
						followOnThread(mineUserID, toUserID);
					}
				});
		followDialog.show();
	}

	private class FollowRun implements Runnable {
		private String toUserID;
		private String mineUserID;

		public FollowRun(String mineUserID, String toUserID) {
			this.toUserID = toUserID;
			this.mineUserID = mineUserID;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			IntResponse response = null;
			try {
				HashMap<String, String> data = new HashMap<String, String>();
				data.put("userid", mineUserID);
				data.put("memberid", toUserID);
				response = JsonParser.getIntResponse(HttpUtil.postMsg(
						HttpUtil.getData(data), HttpUtil.IP + "friendship/follow"));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (response == null) {
				response = new IntResponse();
				response.setMessage("网络访问失败");
			}
			response.setTag(toUserID);
			handler.sendMessage(handler.obtainMessage(
					BaseActivity.NETWORK_SUCCESS_DATA_RIGHT, response));
		}
	}

	public interface FollowListener {

		public void FollowChanged(IntResponse followResponse);
	}

	public interface FollowStartListener {
		public void FollowStart(String toUserID);
	}

	public void addFollowListener(FollowListener onFollowListener) {
		if (onFollowListener != null
				&& !followListenerList.contains(onFollowListener)) {
			followListenerList.add(onFollowListener);
		}
	}

	public void removeFollowListener(FollowListener onFollowListener) {
		if (onFollowListener != null
				&& followListenerList.contains(onFollowListener)) {
			followListenerList.remove(onFollowListener);
		}
	}

	public FollowStartListener getFollowStartListener() {
		return followStartListener;
	}

	public void setFollowStartListener(FollowStartListener followStartListener) {
		this.followStartListener = followStartListener;
	}
}
