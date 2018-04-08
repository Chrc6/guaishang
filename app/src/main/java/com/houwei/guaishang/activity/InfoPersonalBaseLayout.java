package com.houwei.guaishang.activity;

import android.content.Context;
import android.widget.TextView;

import com.houwei.guaishang.R;
import com.houwei.guaishang.bean.UserBean;
import com.houwei.guaishang.tools.ValueUtil;
import com.houwei.guaishang.views.CloudTagViewGroup;
/**
 * 目前只有 InfoPersonalMineLayout继承此类
 * InfoPersonalMineLayout 是在 我的模块 里的 个人资料模块 
 * 
 * 其实 把InfoPersonalMineLayout 和 InfoPersonalMineLayout  合成一个类也行
 *
 */
public class InfoPersonalBaseLayout extends BaseLinearLayout {

	private Context mContext;

	public InfoPersonalBaseLayout(Context context) {
		super(context);
	}
	
	public void initView(Context context,UserBean userBean) {
		TextView realname_tv = (TextView) findViewById(R.id.realname_tv);
		TextView age_tv = (TextView) findViewById(R.id.age_tv);
		TextView sex_tv = (TextView) findViewById(R.id.sex_tv);
		TextView introduce_tv = (TextView) findViewById(R.id.introduce_tv);
		TextView mobile_tv = (TextView) findViewById(R.id.mobile_tv);
		
		
		
		realname_tv.setText(userBean.getName());
		sex_tv.setText(ValueUtil.getSexString(userBean.getSex()));
		age_tv.setText("0".equals(userBean.getAge())?"未填":userBean.getAge());
		introduce_tv.setText("" + userBean.getIntro());
		mobile_tv.setText("" + userBean.getMobile());
		
		CloudTagViewGroup tagsViewGroup = (CloudTagViewGroup)findViewById(R.id.tagsViewGroup);
		tagsViewGroup.setTags(context, userBean.findPersonalTags(),null);
		if (userBean.findPersonalTags().isEmpty()) {			
			tagsViewGroup.addTag(context, "未填写");
		}
		
	}
	

	@Override
	public void onActivityDestory() {
		// TODO Auto-generated method stub

	}

}
