package com.houwei.guaishang.layout;


import com.houwei.guaishang.R;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;


public class SharePopupWindow extends PopupWindow implements OnClickListener {

	private ShareClickedListener  onShareClickedListener;
	
	public SharePopupWindow(Context mContext, LinearLayout mPopView) {
		super(mPopView, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT,true);
		setBackgroundDrawable(new BitmapDrawable());
		initView(mPopView);
	}

	private void initView(LinearLayout mPopView) {
		// TODO Auto-generated method stub
		mPopView.findViewById(R.id.share_weixin_friends_ll).setOnClickListener(this);
		mPopView.findViewById(R.id.share_weixin_moments_ll).setOnClickListener(this);
		mPopView.findViewById(R.id.share_QQfriends_ll).setOnClickListener(this);
		mPopView.findViewById(R.id.share_qzone_ll).setOnClickListener(this);
		
		
		Button btn_cancel=(Button)mPopView.findViewById(R.id.btn_cancel);
		btn_cancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dismiss();
			}
		});
	}


	
	public ShareClickedListener getOnShareClickedListener() {
		return onShareClickedListener;
	}

	public void setOnShareClickedListener(ShareClickedListener onShareClickedListener) {
		this.onShareClickedListener = onShareClickedListener;
	}


	public interface ShareClickedListener {
		public void onShareClicked(View v);
	}


	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		dismiss();
		if (onShareClickedListener!=null) {
			onShareClickedListener.onShareClicked(arg0);
		}
	}
}
