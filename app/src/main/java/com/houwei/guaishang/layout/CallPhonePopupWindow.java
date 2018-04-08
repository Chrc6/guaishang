package com.houwei.guaishang.layout;

import com.houwei.guaishang.R;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

public class CallPhonePopupWindow extends PopupWindow {

	private View v;
	private SelectPhotoListener  onSelectPhotoListener;
	private String phone;
	private Context mContext;
	public CallPhonePopupWindow(Context mContext, LinearLayout mPopView,String phone) {
		super(mPopView, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT,
				true);
	
		this.mContext = mContext;
		setBackgroundDrawable(new BitmapDrawable());
		initView(mPopView,phone);
	}

	private void initView(LinearLayout mPopView,final String phone) {
		// TODO Auto-generated method stub
		
		
		
		
		Button btn_take_photo=(Button)mPopView.findViewById(R.id.btn_phone);
		btn_take_photo.setText("拨号："+phone);
		btn_take_photo.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dismiss();
				callPhone(phone);
			}
		});
		Button btn_pick_photo=(Button)mPopView.findViewById(R.id.btn_sms);
		btn_pick_photo.setText("发短信："+phone);
		btn_pick_photo.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dismiss();
				sms(phone);
				
			}
		});
		Button btn_cancel=(Button)mPopView.findViewById(R.id.btn_cancel);
		btn_cancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dismiss();
			}
		});
	}
	
	public void setRectSize(View v)
	{
		this.v = v;
	}

	
	public SelectPhotoListener getOnSelectPhotoListener() {
		return onSelectPhotoListener;
	}

	public void setOnSelectPhotoListener(SelectPhotoListener onSelectPhotoListener) {
		this.onSelectPhotoListener = onSelectPhotoListener;
	}


	public interface SelectPhotoListener {
		public void onCallPhone(View v);
		public void onSms(View v);
	}
	
	
	private void callPhone(String phone){
		Uri uri = Uri.parse("tel:" + phone);
		Intent intent = new Intent(Intent.ACTION_CALL, uri);
		mContext.startActivity(intent);
	}
	
	private void sms(String phone) {
		Uri smsToUri = Uri.parse("smsto:" + phone);// 联系人地址 
		Intent mIntent = new Intent(Intent.ACTION_SENDTO, smsToUri);     
        mIntent.putExtra("sms_body","");   
        mContext. startActivity(mIntent);
	}
}
