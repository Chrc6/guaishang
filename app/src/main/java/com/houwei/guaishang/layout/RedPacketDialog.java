package com.houwei.guaishang.layout;
import java.util.Random;

import com.houwei.guaishang.R;
import com.houwei.guaishang.views.NumAnim;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RedPacketDialog extends Dialog {
	
	public RedPacketDialog(Activity context, SureButtonClick onSureButtonClick) {
		super(context,R.style.loading_dialog);
		initView(context,onSureButtonClick);
		// TODO Auto-generated constructor stub
	}
	
	private void initView(Activity context,final SureButtonClick onSureButtonClick) {
		// TODO Auto-generated method stub
		
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.dialog_redpacket, null);
		
		final TextView price_tv = (TextView)v.findViewById(R.id.price_tv);
		
		int a = (int) (Math.random() * 100);
		
		NumAnim.startAnim(price_tv, a);   
	
		setContentView(v, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));
	
		findViewById(R.id.pay_btn).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dismiss();
				if (onSureButtonClick!=null) {
					onSureButtonClick.onSureButtonClick(Float.parseFloat(price_tv.getText().toString()));
				}
			}
		});
	
		
        Window dialogWindow = getWindow();
        WindowManager wm = context.getWindowManager();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = wm.getDefaultDisplay().getWidth() * 3 / 4; // 宽度
        lp.height = lp.width * 2/3; // 高度
        dialogWindow.setAttributes(lp);
	}
	
	public interface SureButtonClick{
		public  void onSureButtonClick(float price);
	}
}
