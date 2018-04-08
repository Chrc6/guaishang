package com.houwei.guaishang.activity;

import android.content.Context;
import android.view.LayoutInflater;
import com.houwei.guaishang.R;
import com.houwei.guaishang.bean.UserBean;
import com.houwei.guaishang.views.CloudTagViewGroup;

// 此类 是在 我的模块 里的 个人资料模块 
public class InfoPersonalMineLayout extends InfoPersonalBaseLayout {

	private Context mContext;

	public InfoPersonalMineLayout(Context context) {
		super(context);
		 LayoutInflater.from(context).inflate(R.layout.layout_user_info_editable_personal, this, true);
	}
	

	public void initView(Context context,UserBean userBean) {
		super.initView(context, userBean);
		
		CloudTagViewGroup tagsViewGroup = (CloudTagViewGroup)findViewById(R.id.tagsViewGroup);
		tagsViewGroup.setTags(context, userBean.findPersonalTags(),null);
		tagsViewGroup.addTag(context, "编辑个人标签");
	}
	

	@Override
	public void onActivityDestory() {
		// TODO Auto-generated method stub

	}

}
