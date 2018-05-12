package com.houwei.guaishang.layout;

import com.houwei.guaishang.R;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

public class PhotoPopupWindow extends PopupWindow {

	private View v;
	private SelectPhotoListener  onSelectPhotoListener;
	public PhotoPopupWindow(Context mContext, LinearLayout mPopView) {
		super(mPopView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
				true);
	

		setBackgroundDrawable(new BitmapDrawable());
		initView(mPopView);
	}

	private void initView(LinearLayout mPopView) {
		// TODO Auto-generated method stub

		final CheckBox cb_take_photo = (CheckBox) mPopView.findViewById(R.id.cb_take_photo);
		cb_take_photo.setChecked(true);
		final CheckBox cb_pick_photo = (CheckBox) mPopView.findViewById(R.id.cb_pick_photo);
		cb_pick_photo.setChecked(false);

		LinearLayout ll_take_photo=(LinearLayout)mPopView.findViewById(R.id.ll_take_photo);
		ll_take_photo.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dismiss();
				if(onSelectPhotoListener!=null){
					onSelectPhotoListener.onCamera(v);
					cb_take_photo.setChecked(true);
					cb_pick_photo.setChecked(false);
				}
			}
		});

		LinearLayout ll_pick_photo=(LinearLayout)mPopView.findViewById(R.id.ll_pick_photo);
		ll_pick_photo.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dismiss();
				if(onSelectPhotoListener!=null){
					onSelectPhotoListener.onGallery(v);
					cb_take_photo.setChecked(false);
					cb_pick_photo.setChecked(true);
				}
				
			}
		});
		mPopView.findViewById(R.id.pop_layout).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
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
