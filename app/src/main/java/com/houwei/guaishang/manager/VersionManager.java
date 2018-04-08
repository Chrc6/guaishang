package com.houwei.guaishang.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.houwei.guaishang.activity.BaseActivity;
import com.houwei.guaishang.bean.VersionResponse;
import com.houwei.guaishang.bean.VersionResponse.VersionBean;
import com.houwei.guaishang.layout.SureOrCancelDialog;
import com.houwei.guaishang.tools.HttpUtil;
import com.houwei.guaishang.tools.JsonParser;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;


public class VersionManager {
	private ITopicApplication mApp;
	private JsonParser jp = new JsonParser();
	private ArrayList<LastVersion> onLastVersionList;

	private Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case BaseActivity.NETWORK_SUCCESS_DATA_RIGHT:

				VersionResponse versionResponse = (VersionResponse) msg.obj;

				if (versionResponse.isSuccess()) {
					if (versionResponse.getData().getCode() > getVersionCode()) {

						for (LastVersion onLastVersion : onLastVersionList)
							onLastVersion.notLastVersion(versionResponse.getData());
					} else {
						for (LastVersion onLastVersion : onLastVersionList)
							onLastVersion.isLastVersion();
					}
				} else {
					for (LastVersion onLastVersion : onLastVersionList)
						onLastVersion.versionNetworkFail(versionResponse.getMessage());
				}
				break;
			default:
				break;
			}
		}
	};

	public VersionManager(ITopicApplication mApp) {
		this.mApp = mApp;
		onLastVersionList = new ArrayList<LastVersion>();
	}

	public void checkNewVersion() {
		new Thread(run).start();
	}

	private Runnable run = new Runnable() {

		@SuppressWarnings("unchecked")
		public void run() {
			// TODO Auto-generated method stub
			VersionResponse versionResponse = null;
			try {
				Map<String, String> data = new HashMap<String, String>();
				data.put("platform", "android");
				data.put("current", "" + getVersionCode());
				versionResponse = jp.getVersionResponse(HttpUtil
						.getMsg(HttpUtil.IP + "version/getlastversion?"
								+ HttpUtil.getData(data)));

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (versionResponse == null) {
				versionResponse = new VersionResponse();
				versionResponse.setMessage("网络访问失败");
			}
			handler.sendMessage(handler.obtainMessage(
					BaseActivity.NETWORK_SUCCESS_DATA_RIGHT, versionResponse));
		}
	};

	// 跳转下载新版本界面
	public void downLoadNewVersion(final VersionBean data,
			final Context mContext) {
		
		SureOrCancelDialog followDialog = new SureOrCancelDialog(
				mContext, "发现新版本" + data.getName(), "下载",
				new SureOrCancelDialog.SureButtonClick() {

					@Override
					public void onSureButtonClick() {
						// TODO Auto-generated method stub
						Uri uri = Uri.parse(data.getPackageUrl());
						Intent it = new Intent(Intent.ACTION_VIEW, uri);
						it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						mContext.startActivity(it);
					}
				});
		followDialog.show();
	}

	public void removeListener(LastVersion onLastVersion) {
		if (onLastVersion != null && onLastVersionList.contains(onLastVersion)) {
			onLastVersionList.remove(onLastVersion);
		}
	}

	public void setOnLastVersion(LastVersion onLastVersion) {
		if (onLastVersion != null && !onLastVersionList.contains(onLastVersion)) {
			onLastVersionList.add(onLastVersion);
		}
	}

	public interface LastVersion {
		public void notLastVersion(VersionBean versionBean);

		public void isLastVersion();

		public void versionNetworkFail(String message);
	}

	// 获取当前版本号
	private int getVersionCode() {
		PackageManager packageManager = mApp.getPackageManager();
		// getPackageName()是你当前类的包名，0代表是获取版本信息
		try {
			PackageInfo packInfo = packageManager.getPackageInfo(
					mApp.getPackageName(), 0);

			return packInfo.versionCode;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}
}
