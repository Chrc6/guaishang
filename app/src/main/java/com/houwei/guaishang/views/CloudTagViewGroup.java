package com.houwei.guaishang.views;

import java.util.ArrayList;
import java.util.List;

import com.houwei.guaishang.R;
import com.houwei.guaishang.bean.NameIDBean;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

public class CloudTagViewGroup extends CloudViewGroup {


	
	public CloudTagViewGroup(Context context) {
		super(context);
	}

	public CloudTagViewGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public void setTags(Context context,List<String> tagStringList,String emptyString){
		setTags(context, tagStringList, emptyString, true);
	}
	
	public void setTags(Context context,List<String> tagStringList,String emptyString,boolean clickable) {
		removeAllViews();
		if (tagStringList == null) {
			tagStringList = new ArrayList<String>();
		}
		if (tagStringList.isEmpty() && emptyString!=null) {
			tagStringList.add(emptyString);
		}
		for (int i = 0; i < tagStringList.size(); i++) {			
			TextView tagItem =  (TextView)LayoutInflater.from(context).inflate(R.layout.tagitem_textview,null);
			tagItem.setText(tagStringList.get(i));
			tagItem.setFocusable(clickable);
			tagItem.setClickable(clickable);
			addView(tagItem);
		}
	}
	
	public void addTag(Context context,String tag) {
		TextView tagItem =  (TextView)LayoutInflater.from(context).inflate(R.layout.tagitem_textview,null);
		tagItem.setText(tag);
		tagItem.setFocusable(false);
		tagItem.setClickable(false);
		addView(tagItem);
	}
}
