package com.houwei.guaishang.activity;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import com.houwei.guaishang.R;
import com.houwei.guaishang.bean.BaseResponse;
import com.houwei.guaishang.bean.SearchResponse;
import com.houwei.guaishang.tools.HttpUtil;
import com.houwei.guaishang.tools.JsonParser;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class WarningReportActivity extends BaseActivity {
	private String hisUserName;
	private EditText editText1;
	
	
	private MyHandler handler = new MyHandler(this);
	private static class MyHandler extends Handler {
		
		private WeakReference<Context> reference;
		
	    public MyHandler(Context context) {
	    	reference = new WeakReference<Context>(context);
	    }
	    
	    @Override
		public void handleMessage(Message msg) {
	    	final WarningReportActivity activity = (WarningReportActivity) reference.get();
			if(activity == null){
				return;
			}
			switch (msg.what) {
			case NETWORK_OTHER:
				BaseResponse retMap = (BaseResponse) msg.obj;
				if (retMap.isSuccess()) {
					activity.showErrorToast("提交成功");
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
		setContentView(R.layout.activity_warning_report);
		initView();
		initListener();
	}

	private void initView() {
		// TODO Auto-generated method stub
		initProgressDialog();
	}

	private void initListener() {
		// TODO Auto-generated method stub
		hisUserName = getIntent().getStringExtra("hisUserID");
		 Button update_btn = (Button) findViewById(R.id.reg_btn);
		editText1= (EditText) findViewById(R.id.editText1);
		update_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (editText1.getText().toString().trim().equals("")) {
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
			BaseResponse retMap = null;
			try {
				Map<String, String> data = new HashMap<String, String>();
				data.put("userid", getUserID());
				data.put("to_userid", hisUserName==null?"":hisUserName);
				data.put("content", editText1.getText().toString());
				retMap = JsonParser.getBaseResponse(HttpUtil.postMsg( HttpUtil.getData(data),HttpUtil.IP
						+ "user/opinion"));
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
	};

}
