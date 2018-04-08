package com.houwei.guaishang.layout;


import com.houwei.guaishang.R;
import com.houwei.guaishang.activity.BaseActivity;
import com.houwei.guaishang.activity.TagPersonActivity;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AddTagDialog extends Dialog {
	public EditText tag_et;
	public AddTagDialog(BaseActivity context, SureButtonClick onSureButtonClick) {
		super(context,R.style.loading_dialog);
		initView(context, onSureButtonClick);
		// TODO Auto-generated constructor stub
	}
	 
	
	
	
	private void initView(BaseActivity context,final SureButtonClick onSureButtonClick) {
		// TODO Auto-generated method stub
		
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.dialog_edittext, null);
		
		tag_et  = (EditText)v.findViewById(R.id.tag_et);
	
		TextView cancel_tv = (TextView)v.findViewById(R.id.cancel_tv);
		cancel_tv.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dismiss();
			}
		});
		TextView sure_tv = (TextView)v.findViewById(R.id.sure_tv);
		sure_tv.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dismiss();
				if (onSureButtonClick!=null && !tag_et.getText().toString().trim().equals("")
						&& !tag_et.getText().toString().trim().equals(TagPersonActivity.ADD_TAG_STRING)) {
					onSureButtonClick.onSureButtonClick(tag_et.getText().toString().trim());
				}
			}
		});
	
		
		
		setContentView(v, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));

	
	}
	

	public void setTitle(String title)
	{
		TextView title_tv = (TextView)findViewById(R.id.title_tv);
		title_tv.setText(title);
	}
	
	public void setEditText(String title)
	{
		tag_et.setText(title);
	}
	
	
	public interface SureButtonClick{
		public  void onSureButtonClick(String tag);
	}
}
