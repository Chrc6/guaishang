package com.houwei.guaishang.layout;

import com.houwei.guaishang.R;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SureOrCancelInterfaceDialog extends Dialog {

	public SureOrCancelInterfaceDialog(Context context,String message,String sureTextView,String cancelTextView, ButtonClick onSureButtonClick) {
		super(context,R.style.loading_dialog);
		initView(context, message, sureTextView,cancelTextView, onSureButtonClick);
		// TODO Auto-generated constructor stub
	}
	 
	
	
	
	private void initView(Context context,String message,String sureTextView,String cancelTextView,final ButtonClick onSureButtonClick) {
		// TODO Auto-generated method stub
		
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.dialog_sure, null);
		
		TextView message_tv = (TextView)v.findViewById(R.id.message_tv);
		message_tv.setText(message);
		TextView sure_tv = (TextView)v.findViewById(R.id.sure_tv);
		sure_tv.setText(sureTextView);
		
		TextView cancel_tv = (TextView)v.findViewById(R.id.cancel_tv);
		cancel_tv.setText(cancelTextView);
		
		cancel_tv.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dismiss();
				if (onSureButtonClick!=null) {
					onSureButtonClick.onCancelButtonClick();
				}
			}
		});
	
		sure_tv.setOnClickListener(new View.OnClickListener() {
			
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
	
	public interface ButtonClick{
		public  void onSureButtonClick();
		public  void onCancelButtonClick();
	}
}
