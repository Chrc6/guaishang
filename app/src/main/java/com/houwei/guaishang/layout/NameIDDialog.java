package com.houwei.guaishang.layout;

import java.util.List;

import com.houwei.guaishang.R;
import com.houwei.guaishang.bean.NameIDBean;


import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class NameIDDialog extends Dialog {

	public NameIDDialog(Context context, List<NameIDBean> ans,
		String title,String defaultCheckedID,AnswerListener onAnswerListener) {
		super(context, R.style.loading_dialog);
		initView(context, ans,title,defaultCheckedID, onAnswerListener);
		// TODO Auto-generated constructor stub
	}

	private void initView(Context context,final  List<NameIDBean> ans,String title,String defaultCheckedID,
			final AnswerListener onAnswerListener) {
		// TODO Auto-generated method stub

		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.dialog_question, null);

		final RadioGroup radio_group = (RadioGroup) v . findViewById(R.id.radio_group);
		int padding =  (int) context.getResources().getDimension(R.dimen.radio_margin);
		for (int i = 0; i < ans.size(); i++) {
			RadioButton radioButton = (RadioButton) inflater.inflate(
					R.layout.listitem_radiobutton, null);
			final NameIDBean bean = ans.get(i);
			radioButton.setText(bean.getName());
			radioButton.setId(i);
			radioButton.setChecked(bean.getId().equals(defaultCheckedID)?true:false);
			radioButton.setPadding(0, padding, 0, padding);
			radioButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (onAnswerListener != null ) {
						onAnswerListener.onAnswer(bean);
					}
					dismiss();
				}
			});
			radio_group.addView(radioButton);
		}

		TextView question_tv = (TextView) v.findViewById(R.id.question_tv);
		question_tv.setText(title);
		if (title.equals("")) {
			question_tv.setVisibility(View.GONE);
		}
//		TextView sure_btn = (TextView) v.findViewById(R.id.sure_btn);
//		sure_btn.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
//				if (radio_group.getCheckedRadioButtonId()<0 || 
//						radio_group.getCheckedRadioButtonId() >=  ans.getAnswer().size()) {
//					return ;
//				}
//				if (onAnswerListener != null) {
//					onAnswerListener.onAnswer(radio_group
//							.getCheckedRadioButtonId());
//				}
//				dismiss();
//			}
//		});

		setContentView(v, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));

	}


	public interface AnswerListener {
		public void onAnswer(NameIDBean selectBean);
	}

}
