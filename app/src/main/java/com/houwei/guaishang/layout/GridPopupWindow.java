package com.houwei.guaishang.layout;

import java.util.List;

import com.houwei.guaishang.R;
import com.houwei.guaishang.bean.NameIDBean;
import com.houwei.guaishang.layout.ListPopupWindow.SetItemListener;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class GridPopupWindow extends PopupWindow {
	private Context mContext;
	private SetItemListener onSetItemListener;
	private GridView expand_lv;
	private List<NameIDBean> list;
	private String defaultCheckedID;

	
	
	public GridPopupWindow(Context mContext, LinearLayout mPopView,List<NameIDBean> list,String defaultCheckedID) {
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
	
		expand_lv = (GridView) mPopView.findViewById(R.id.gridView);

		expand_lv.setAdapter(new PhotoGridAdapter(list,LayoutInflater.from(mContext)));
		expand_lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				dismiss();
				if (onSetItemListener != null) {
					onSetItemListener.onItemSet(list.get(arg2));
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




	private class PhotoGridAdapter extends BaseAdapter {
		private List<NameIDBean> investorFoucusDirectionList;

		private LayoutInflater mLayoutInflater;

		public PhotoGridAdapter(List<NameIDBean> investorFoucusDirectionList,
				LayoutInflater mLayoutInflater) {
			this.investorFoucusDirectionList = investorFoucusDirectionList;
			this.mLayoutInflater = mLayoutInflater;
		}

		@Override
		public int getCount() {
			return investorFoucusDirectionList == null ? 0
					: investorFoucusDirectionList.size();
		}

		@Override
		public NameIDBean getItem(int position) {
			return investorFoucusDirectionList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			MyGridViewHolder viewHolder;
			if (convertView == null) {
				viewHolder = new MyGridViewHolder();
				convertView = mLayoutInflater.inflate(R.layout.griditem_textview_stroke,null);
				viewHolder.add_tag_btn = (TextView) convertView
						.findViewById(R.id.tag_tv);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (MyGridViewHolder) convertView.getTag();
			}

			viewHolder.add_tag_btn.setText(getItem(position).getName());
			
			if (investorFoucusDirectionList.get(position).getId().equals(defaultCheckedID)) {
				viewHolder.add_tag_btn.setBackgroundResource( R.drawable.blue_light_drawable);
				viewHolder.add_tag_btn.setTextColor(mContext.getResources().getColor(R.color.white_color));
			}  else{
				viewHolder.add_tag_btn.setBackgroundResource( R.drawable.white_rect_stroke_normal);
				viewHolder.add_tag_btn.setTextColor(mContext.getResources().getColor(R.color.text_black_color));
			}
			return convertView;
		}

		private class MyGridViewHolder {
			TextView add_tag_btn;
		}
	}

	
	public interface SetItemListener {
		public void onItemSet(NameIDBean ab);
	}


}
