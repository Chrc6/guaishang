package com.houwei.guaishang.layout;

import com.houwei.guaishang.R;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MenuTwoButtonDialog extends Dialog {

	
	public TextView title_tv;
	public TextView tv2;
	public MenuTwoButtonDialog(Context context, ButtonClick onSureButtonClick) {
		super(context,R.style.loading_dialog);
		initView(context,  onSureButtonClick);
		// TODO Auto-generated constructor stub
	}
	 

	
	private void initView(Context context,final ButtonClick onSureButtonClick) {
		// TODO Auto-generated method stub
		
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.dialog_menu_twobutton, null);
		title_tv = (TextView)v.findViewById(R.id.tv1);
		title_tv.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dismiss();
				if (onSureButtonClick!=null) {
					onSureButtonClick.onSureButtonClick(0);
				}
			}
		});
	
		tv2 = (TextView)v.findViewById(R.id.tv2);
		tv2.setOnClickListener(new View.OnClickListener() {
			
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
