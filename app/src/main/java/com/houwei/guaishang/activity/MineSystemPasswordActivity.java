package com.houwei.guaishang.activity;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.houwei.guaishang.R;
import com.houwei.guaishang.bean.BaseResponse;
import com.houwei.guaishang.bean.StringResponse;
import com.houwei.guaishang.manager.MyUserBeanManager;
import com.houwei.guaishang.tools.HttpUtil;
import com.houwei.guaishang.tools.JsonParser;

public class MineSystemPasswordActivity extends BaseActivity {

	private EditText old_password_et,new_password_et1,new_password_et2;
	private MyHandler handler = new MyHandler(this);
	private static class MyHandler extends Handler {
		
		private WeakReference<Context> reference;
		
	    public MyHandler(Context context) {
	    	reference = new WeakReference<Context>(context);
	    }
	    
	    @Override
		public void handleMessage(Message msg) {
	    	final MineSystemPasswordActivity activity = (MineSystemPasswordActivity) reference.get();
			if(activity == null){
				return;
			}
			activity.progress.dismiss();
		
			switch (msg.what) {
			
			case NETWORK_OTHER:
				StringResponse retMap = (StringResponse) msg.obj;
				if (retMap.isSuccess()) {
					activity.showErrorToast("修改成功");
					activity.finish();				
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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting_password);
		initView();
		initListener();
	}

	private void initView() {
		// TODO Auto-generated method stub
		initProgressDialog();
	}

	private void initListener() {
		// TODO Auto-generated method stub
	
		 Button update_btn = (Button) findViewById(R.id.release_btn);
		 old_password_et= (EditText) findViewById(R.id.old_password_et);
		 new_password_et1= (EditText) findViewById(R.id.new_password_et1);
		 new_password_et2= (EditText) findViewById(R.id.new_password_et2);
		update_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (old_password_et.getText().toString().trim().equals("")
						||new_password_et1.getText().toString().trim().equals("")
						||new_password_et2.getText().toString().trim().equals("")) {
					showErrorToast("密码不能为空");
					return;
				}
				if (!new_password_et1.getText().toString().trim().equals(new_password_et2.getText().toString().trim())) {
					showErrorToast("两次密码不一致");
					return;
				}
				hideKeyboard();
				progress.show();
				new Thread(CheckRun).start();
			}
		});
		BackButtonListener();
	}


	private Runnable CheckRun = new Runnable() {

		@SuppressWarnings("unchecked")
		public void run() {
			// TODO Auto-generated method stub
			StringResponse retMap = null;
			String password = new_password_et1.getText().toString().trim();
			try {
				Map<String, String> data = new HashMap<String, String>();
				data.put("userid", getUserID());
				data.put("old_password", old_password_et.getText().toString());
				data.put("new_password", password);
				retMap = JsonParser.getStringResponse2(HttpUtil.postMsg( HttpUtil.getData(data),HttpUtil.IP
						+ "user/update_password"));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (retMap != null) {
				retMap.setTag(password);
				handler.sendMessage(handler.obtainMessage(
						NETWORK_OTHER, retMap));
			} else {
				handler.sendEmptyMessage(NETWORK_SUCCESS_DATA_ERROR);
			}
		}
	};

}
