package com.houwei.guaishang.activity;


import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.houwei.guaishang.R;
import com.houwei.guaishang.bean.UserBean;
import com.houwei.guaishang.tools.ValueUtil;
import com.houwei.guaishang.views.CloudTagViewGroup;
/**
 * 此类 是在 查看其他人主页 （HisRootActivity）用到的
 * 不用这个类，把代码都复制到HisRootActivity里也没啥问题。只是会显得HisRootActivity代码多
 */
public class InfoPersonalHisLayout extends LinearLayout {


	public InfoPersonalHisLayout(Context context) {
		super(context);
		LayoutInflater.from(context).inflate(R.layout.layout_user_info_personal, this, true);
	}
	
	//after network
	public void initView(Context context,UserBean userBean) {
		
		TextView name_tv = (TextView) findViewById(R.id.name_tv);
		TextView age_tv = (TextView) findViewById(R.id.age_tv);
		TextView sex_tv = (TextView) findViewById(R.id.sex_tv);
		TextView introduce_tv = (TextView) findViewById(R.id.introduce_tv);
		
		name_tv.setText(userBean.getName());
		sex_tv.setText(ValueUtil.getSexString(userBean.getSex()));
		age_tv.setText("0".equals(userBean.getAge())?"未填":userBean.getAge());
		introduce_tv.setText("" + userBean.getIntro());

		
		CloudTagViewGroup tagsViewGroup = (CloudTagViewGroup)findViewById(R.id.tagsViewGroup);
		tagsViewGroup.setTags(context, userBean.findPersonalTags(),null);
		if (userBean.findPersonalTags().isEmpty()) {			
			tagsViewGroup.addTag(context, "未填写");
		}
		
	}

}
