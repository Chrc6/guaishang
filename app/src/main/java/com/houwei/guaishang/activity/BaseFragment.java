package com.houwei.guaishang.activity;

import com.houwei.guaishang.R;
import com.houwei.guaishang.bean.AvatarBean;
import com.houwei.guaishang.bean.UserBean;
import com.houwei.guaishang.easemob.EaseConstant;
import com.houwei.guaishang.layout.MProgressDialog;
import com.houwei.guaishang.manager.ITopicApplication;
import com.houwei.guaishang.manager.HuanXinManager.HuanXinLoginListener;
import com.houwei.guaishang.views.TipsToast;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

public class BaseFragment extends Fragment {

	public MProgressDialog progress;


	/**
	 * 如果登录了，返回true，否则返回false并去登录
	 * @return
	 */
	public boolean checkLogined(){
		return getBaseActivity().checkLogined();
	}

	
	
	public void initProgressDialog() {
		initProgressDialog(true, null);
	}

	public void initProgressDialog(boolean cancel, String message) {
		initProgressDialog(getActivity(), cancel, message);
	}

	public void initProgressDialog(Context mContext, boolean cancel,
			String message) {
		progress = new MProgressDialog(mContext, cancel);
	}

	public void showErrorToast() {
		showFailTips("无法连接到网络\n请稍后再试");
	}

	public void showFailTips(String content) {

		TipsToast tipsToast = TipsToast.makeText(getActivity(),content, TipsToast.LENGTH_SHORT);	
		tipsToast.show();
	}

	public void showSuccessTips(String content) {

		TipsToast tipsToast = TipsToast.makeText(getActivity(), content, TipsToast.LENGTH_SHORT);	
		tipsToast.setIcon(R.drawable.tips_success);
		tipsToast.show();
	}
	
	public void showErrorToast(String err) {
		Toast.makeText(getActivity(), err, Toast.LENGTH_SHORT).show();
	}

	public void BackButtonListener() {
		getBaseActivity().BackButtonListener();
	}

	public ITopicApplication getITopicApplication() {
		return (ITopicApplication) getActivity().getApplication();
	}

	public void jumpToHisInfoActivity( String UserID,
			String realName,AvatarBean headImageBean) {
		getBaseActivity().jumpToHisInfoActivity(UserID, realName, headImageBean);
	}
	
	public void jumpToChatActivity(final String hisUserID,
			final String hisRealName,final AvatarBean headImageBean,final int chatType,final  String mobile) {
		getBaseActivity().jumpToChatActivity(hisUserID, hisRealName, headImageBean, chatType,mobile);
	}


	public void setTitleName(String titleName) {
		getBaseActivity().setTitleName(titleName);
	}


	public void hideKeyboard(View v) {
		((InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE))
				.hideSoftInputFromWindow(v.getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
	}

	public void hideKeyboard() {
		hideKeyboard(getActivity().getWindow().getDecorView());
	}
	
	public void showKeyboard(EditText et) {
		((InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE))
				.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
	}
	
	public String getUserID() {
		return getBaseActivity().getUserID();
	}
	
	//DQ
	public BaseActivity getBaseActivity(){
		return (BaseActivity)getActivity();
	}
	
}
