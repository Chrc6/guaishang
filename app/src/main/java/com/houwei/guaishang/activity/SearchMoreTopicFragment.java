package com.houwei.guaishang.activity;

import java.util.HashMap;

import android.view.View;

import com.houwei.guaishang.R;

public class SearchMoreTopicFragment extends TopicMineFragment {
	
	
	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		super.initView();
		setTitleName("搜索结果");
		getView().findViewById(R.id.title_right).setVisibility(View.GONE);
	}
	
	/**
	 * 表示调用的接口是getlist（默认）
	 * 子类可以修改，比如返回getpraiselist 表示查询我赞的动态
	 */
	@Override
	protected String getApi(){
		return "search/topic";
	}
	

	
	/**
	 * 如果重写并返回id，表示查询某人的发布过的动态列表
	 */
	@Override
	public String getTargetMemberId(){
		return "0";
	}
	
	/**
	 * 子类可以重写此类来添加请求网络时候的自定义参数
	 */
	@Override
	protected HashMap<String,String> getRequireHashMap(){
		HashMap<String, String> map =  new HashMap<String, String>();
		map.put("keyword", getActivity().getIntent().getStringExtra("keyword"));
		return map;
	}
}
