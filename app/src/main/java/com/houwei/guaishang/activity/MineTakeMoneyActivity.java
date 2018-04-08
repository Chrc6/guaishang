package com.houwei.guaishang.activity;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import com.houwei.guaishang.R;
import com.houwei.guaishang.bean.BaseResponse;
import com.houwei.guaishang.bean.IntResponse;
import com.houwei.guaishang.layout.ApplySuccessDialog;
import com.houwei.guaishang.manager.MyUserBeanManager.CheckPointListener;
import com.houwei.guaishang.tools.HttpUtil;
import com.houwei.guaishang.tools.JsonParser;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

public class MineTakeMoneyActivity extends BaseActivity implements CheckPointListener {
	private EditText user_name_et;
	private CheckBox alipay_cb;
	private MyHandler handler = new MyHandler(this);
	private static class MyHandler extends Handler {
		
		private WeakReference<Context> reference;
		
	    public MyHandler(Context context) {
	    	reference = new WeakReference<Context>(context);
	    }
	    
	    @Override
		public void handleMessage(Message msg) {
	    	final MineTakeMoneyActivity activity = (MineTakeMoneyActivity) reference.get();
			if(activity == null){
				return;
			}
			activity.progress.dismiss();
			switch (msg.what) {
			
			case NETWORK_OTHER:
				BaseResponse retMap = (BaseResponse) msg.obj;
				if (retMap.isSuccess()) {
					ApplySuccessDialog dialog = new ApplySuccessDialog(activity, new ApplySuccessDialog.SureButtonClick() {
						
						@Override
						public void onSureButtonClick() {
							// TODO Auto-generated method stub
							activity.finish();
						}
					});	
					dialog.show();
				} else {
					activity.showErrorToast(retMap.getMessage());
				}
			
				break;
			default:
				activity.showErrorToast();
				break;
			}
		}
	};

	
	@Override
	public void onCheckPointFinish(IntResponse response) {
		// TODO Auto-generated method stub
		if (response.isSuccess()) {
			TextView balance_tv = (TextView) findViewById(R.id.balance_tv);
			balance_tv.setText("账户余额："+response.getData()+" 金币");
		} else {
			showErrorToast(response.getMessage());
		}
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		getITopicApplication().getMyUserBeanManager().removeCheckPointListener(this);
		super.onDestroy();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mine_takemoney);
		initView();
		initListener();
	}

	private void initView() {
		// TODO Auto-generated method stub
		initProgressDialog();
		
		alipay_cb = (CheckBox) findViewById(R.id.alipay_cb);
		user_name_et = (EditText) findViewById(R.id.user_name_et);
		alipay_cb.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (alipay_cb.isChecked()) {
				}
			}
		});
	}

	private void initListener() {
		// TODO Auto-generated method stub
		BackButtonListener();
		findViewById(R.id.pay_tv).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (user_name_et.getText().toString().trim().equals("")) {
					showErrorToast("输入姓名");
					return;
				}
				
				String account = null;
				int type = 0;
				if(alipay_cb.isChecked()){
					type = 1;
					EditText alipay_et = (EditText)findViewById(R.id.alipay_et);
					account = alipay_et.getText().toString().trim();
					if (account.trim().equals("")) {
						showErrorToast("请输入支付宝账号");
						return;
					}
				}else{
					showErrorToast("请选择一种支付方式");
					return;
				}
				
				new Thread(new UpdateRun(account, type)).start();
			}
		});
		
		getITopicApplication().getMyUserBeanManager().addOnCheckPointListener(this);
		getITopicApplication().getMyUserBeanManager().startCheckPointRun();
	}

	
	private class UpdateRun implements Runnable {
		private int type;
		private String account;
		
		public UpdateRun(String account,int type){
			this.account = account;
			this.type = type;
		}
		
		public void run() {
			// TODO Auto-generated method stub
			BaseResponse retMap = null;
			try {
				Map<String, String> data = new HashMap<String, String>();
				data.put("userid", getUserID());
				data.put("account", account);
				data.put("realname", user_name_et.getText().toString().trim());
				data.put("type", ""+type);
				
				retMap = JsonParser.getBaseResponse(HttpUtil.postMsg(HttpUtil.getData(data),HttpUtil.IP
						+ "mission/takemoney"));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (retMap != null) {
				handler.sendMessage(handler.obtainMessage(
						NETWORK_OTHER, retMap));
			} else {
				handler.sendEmptyMessage(NETWORK_SUCCESS_DATA_ERROR);
			}
		}
	}

	
}
