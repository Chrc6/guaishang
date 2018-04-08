package com.houwei.guaishang.layout;

import com.houwei.guaishang.R;


import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ApplySuccessDialog extends Dialog {
	public ApplySuccessDialog(Context context, SureButtonClick onSureButtonClick) {
		super(context,R.style.loading_dialog);
		initView(context,onSureButtonClick);
		// TODO Auto-generated constructor stub
	}
	 
	
	
	
	private void initView(Context context,final SureButtonClick onSureButtonClick) {
		// TODO Auto-generated method stub
		
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.dialog_apply_success, null);
		
		
		
		TextView cancel_tv = (TextView)v.findViewById(R.id.cancel_tv);
		cancel_tv.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dismiss();
				if (onSureButtonClick!=null) {
					onSureButtonClick.onSureButtonClick();
				}
			}
		});
	
		setContentView(v, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));
	
	}
	
	public interface SureButtonClick{
		public  void onSureButtonClick();
	}
}
