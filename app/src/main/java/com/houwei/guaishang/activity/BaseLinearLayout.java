package com.houwei.guaishang.activity;

import com.houwei.guaishang.layout.MProgressDialog;

import android.content.Context;
import android.content.Intent;
import android.widget.LinearLayout;
import android.widget.Toast;

public abstract class BaseLinearLayout extends LinearLayout {
	public Context mContext;
	public MProgressDialog progress;

	public BaseLinearLayout(Context context) {
		super(context);
		mContext = context;
	}

	public void initProgressDialog() {
		initProgressDialog(true, null);
	}

	public void initProgressDialog(boolean cancel, String message) {
		initProgressDialog(mContext, cancel, message);
	}

	public void initProgressDialog(Context mContext, boolean cancel,
			String message) {
		progress = new MProgressDialog(mContext, cancel);
		progress.setMessage(message);
	}

	public void showErrorToast() {
		Toast.makeText(mContext, "网络访问失败", Toast.LENGTH_SHORT).show();
	}

	public void showErrorToast(String err) {
		Toast.makeText(mContext, err, Toast.LENGTH_SHORT).show();
	}

	public void onDestory() {
		onActivityDestory();
	}

	//由子类重写
	public void refresh() {}
	
	
	public abstract void onActivityDestory();
}
