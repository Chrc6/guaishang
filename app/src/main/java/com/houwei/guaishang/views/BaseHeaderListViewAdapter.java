package com.houwei.guaishang.views;

import java.util.List;

import com.houwei.guaishang.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

public abstract  class BaseHeaderListViewAdapter extends BaseAdapter {
	public static final int TAB_STATE_LODING = 7; //公用 加载中
	public static final int TAB_STATE_EMPTY= 8;  //公用 size == 0
	public static final int TAB_STATE_FINISH = 9;//公用 有数据
	public int state = TAB_STATE_LODING;
//	public LayoutInflater mInflater;
//	
//	@Override
//	public View getView(int position, View convertView, ViewGroup parent) {
//		if (state == TAB_STATE_LODING) {
//			return mInflater.inflate(R.layout.footer_loading_view, null);
//		}
//		if (state == TAB_STATE_EMPTY) {
//			return mInflater.inflate(R.layout.listitem_empty_padding, null);
//		}
//		return getView( position,  convertView,  parent);
//	}
	

	
	public void notifyDataSetChanged(ListView listView,List list){
		if (list == null || list.isEmpty()) {
			this.state = TAB_STATE_EMPTY;
		}else{
			this.state = TAB_STATE_FINISH;
		}
		listView.setAdapter(this);
	}
	
	public View getLoadView(LayoutInflater mInflater){
		return mInflater.inflate(R.layout.footer_loading_view, null);
	}
	
	public View getEmptyView(LayoutInflater mInflater){
		return mInflater.inflate(R.layout.listitem_empty_padding, null);
	}
}
