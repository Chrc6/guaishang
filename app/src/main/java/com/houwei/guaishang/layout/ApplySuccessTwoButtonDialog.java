package com.houwei.guaishang.layout;



import com.houwei.guaishang.R;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ApplySuccessTwoButtonDialog extends Dialog {
	
	public TextView comment_tv;
	
	public ApplySuccessTwoButtonDialog(Context context, ButtonClick onSureButtonClick) {
		super(context,R.style.loading_dialog);
		initView(context,onSureButtonClick);
		// TODO Auto-generated constructor stub
	}
	 
	
	
	
	private void initView(Context context,final ButtonClick onSureButtonClick) {
		// TODO Auto-generated method stub
		
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.dialog_success_towbutton, null);
		
		
		
		TextView home_tv = (TextView)v.findViewById(R.id.home_tv);
		home_tv.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dismiss();
				if (onSureButtonClick!=null) {
					onSureButtonClick.onSureButtonClick(0);
				}
			}
		});
	
		comment_tv =  (TextView)v.findViewById(R.id.comment_tv);
		comment_tv.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dismiss();
				if (onSureButtonClick!=null) {
					onSureButtonClick.onSureButtonClick(1);
				}
			}
		});
		
		setContentView(v, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));
	
	}
	
	public interface ButtonClick{
		public  void onSureButtonClick(int index);
	}
}
