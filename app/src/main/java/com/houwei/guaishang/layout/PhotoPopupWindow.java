package com.houwei.guaishang.layout;

import com.houwei.guaishang.R;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

public class PhotoPopupWindow extends PopupWindow {

	private View v;
	private SelectPhotoListener  onSelectPhotoListener;
	public PhotoPopupWindow(Context mContext, LinearLayout mPopView) {
		super(mPopView, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT,
				true);
	

		setBackgroundDrawable(new BitmapDrawable());
		initView(mPopView);
	}

	private void initView(LinearLayout mPopView) {
		// TODO Auto-generated method stub
		
		
		
		
		Button btn_take_photo=(Button)mPopView.findViewById(R.id.btn_take_photo);
		btn_take_photo.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dismiss();
				if(onSelectPhotoListener!=null){
					onSelectPhotoListener.onCamera(v);
				}
			}
		});
		Button btn_pick_photo=(Button)mPopView.findViewById(R.id.btn_pick_photo);
		btn_pick_photo.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dismiss();
				if(onSelectPhotoListener!=null){
					onSelectPhotoListener.onGallery(v);
				}
				
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
		public void onGallery(View v);
		public void onCamera(View v);
	}
}
