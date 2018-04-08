package com.houwei.guaishang.layout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.houwei.guaishang.R;
import com.houwei.guaishang.bean.NameIDBean;
import com.houwei.guaishang.manager.ITopicApplication;
import com.houwei.guaishang.views.LetterListViewNoHot;



import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class ListPopupWindow extends PopupWindow {
	private Context mContext;
	private SetItemListener onSetItemListener;
	private ListView expand_lv;
	private List<NameIDBean> list;
	private String defaultCheckedID;

	
	
	public ListPopupWindow(Context mContext, LinearLayout mPopView,List<NameIDBean> list,String defaultCheckedID) {
		super(mPopView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT,
				true);
		this.mContext = mContext;
		this.list = list;
		this.defaultCheckedID = defaultCheckedID;
		setBackgroundDrawable(new BitmapDrawable());
		initView(mPopView);
	}

	private void initView(LinearLayout mPopView) {
		// TODO Auto-generated method stub
	
		expand_lv = (ListView) mPopView.findViewById(R.id.listView);

		expand_lv.setAdapter(new MyAdapter(mContext, list));
		expand_lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				if (arg2 < expand_lv.getHeaderViewsCount()) {
					return ;
				}
				dismiss();
				if (onSetItemListener != null) {
					onSetItemListener.onItemSet(list.get(arg2 - expand_lv.getHeaderViewsCount()));
				}
			}
		});
	}


	public SetItemListener getOnSetItemListener() {
		return onSetItemListener;
	}

	public void setOnSetItemListener(SetItemListener onSetItemListener) {
		this.onSetItemListener = onSetItemListener;
	}



	private class MyAdapter extends BaseAdapter {
		private List<NameIDBean> groupList;
		private Context mContext;

		public MyAdapter(Context mContext, List<NameIDBean> groupList) {
			this.mContext = mContext;
			this.groupList = groupList;
		}

		public int getCount() {
			return groupList.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.listitem_textview_checkbox, null);
				viewHolder.tv = (TextView) convertView
						.findViewById(R.id.tv);
				viewHolder.checked_iv = (ImageView) convertView
						.findViewById(R.id.checked_iv);
			
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			viewHolder.tv.setText(groupList.get(position).getName());
			viewHolder.checked_iv.setVisibility(groupList.get(position).getId().equals(defaultCheckedID)?View.VISIBLE:View.INVISIBLE);
			return convertView;
		}

		class ViewHolder {
			private TextView tv;
			private ImageView checked_iv;
		}
	}



	
	public interface SetItemListener {
		public void onItemSet(NameIDBean ab);
	}



}
