package com.houwei.guaishang.layout;

import com.houwei.guaishang.R;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MProgressDialog extends AlertDialog {

	private TextView mMessageView;

	private boolean mCancelAble = true;

	public MProgressDialog(Context context, boolean cancelAble) {
		super(context, R.style.MDialog);

		mCancelAble = cancelAble;

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.progress_dialog);

		mMessageView = (TextView) findViewById(R.id.progress_message);

		setCancelable(mCancelAble);
		setCanceledOnTouchOutside(false);

		
	}

	public void setMessage(String message) {
		if (message != null) {
			mMessageView.setVisibility(View.VISIBLE);
			mMessageView.setText(message);
		}
	}
}
