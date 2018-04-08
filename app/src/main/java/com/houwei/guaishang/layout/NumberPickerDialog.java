package com.houwei.guaishang.layout;


import com.houwei.guaishang.R;
import com.houwei.guaishang.views.NumberPicker;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NumberPickerDialog extends Dialog {

	private DateSelectedListener onDateSelectedListener;
	private TextView tv;
	private NumberPicker number_picker_startyear;
	private String[] displayedValues;
	public NumberPickerDialog(Context context) {
		super(context, R.style.DateDialog);
		initView(context);
		// TODO Auto-generated constructor stub
	}

	private void initView(Context context) {
		// TODO Auto-generated method stub

		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.dialog_number_picker,null);

		TextView cancel_tv = (TextView) v.findViewById(R.id.cancel_tv);
		cancel_tv.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dismiss();
			}
		});
		TextView sure_tv = (TextView) v.findViewById(R.id.sure_tv);
		sure_tv.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dismiss();
				if (onDateSelectedListener != null) {
					if (displayedValues != null) {
						onDateSelectedListener.onDateSelected(tv,
								Integer.parseInt(displayedValues[number_picker_startyear.getValue()]));
					}else{
						
					onDateSelectedListener.onDateSelected(tv,
							number_picker_startyear.getValue());
					}
				}
			}
		});

		setContentView(v, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));

		number_picker_startyear = (NumberPicker) v.findViewById(R.id.number_picker_startyear);
		
		number_picker_startyear.setMaxValue(60);
		number_picker_startyear.setMinValue(15);
		number_picker_startyear.setFocusable(true);
		number_picker_startyear.setFocusableInTouchMode(true);
		number_picker_startyear.setValue(25);
		number_picker_startyear.setWrapSelectorWheel(false);
	
	}
	
	public void setDisplayedValues(String[] displayedValues){
		this. displayedValues=  displayedValues;
		number_picker_startyear.setMaxValue(displayedValues.length-1);
		number_picker_startyear.setMinValue(0);
		number_picker_startyear.setValue(0);
		number_picker_startyear.setDisplayedValues(displayedValues);
		number_picker_startyear.setWrapSelectorWheel(false);
	}

	public void setOnDateSelectedListener(TextView tv,
			DateSelectedListener onDateSelectedListener) {
		this.tv = tv;
		this.onDateSelectedListener = onDateSelectedListener;
	}

	public interface SureButtonClick {
		public void onSureButtonClick();
	}

	public interface DateSelectedListener {
		public void onDateSelected(TextView tv, int result);
	}
}
