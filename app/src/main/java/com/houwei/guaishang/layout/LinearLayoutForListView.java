package com.houwei.guaishang.layout;

import com.houwei.guaishang.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

public class LinearLayoutForListView extends LinearLayout {
	private android.widget.BaseAdapter adapter;
	private OnItemClickLinstener OnItemClickListener = null;
	private Context context;

	private boolean disableDivider;

	public void fillLinearLayout() {
		int count = adapter.getCount();
		for (int i = 0; i < count; i++) {
			final int pos = i;
			View v = adapter.getView(i, null, null);
			// v.setOnClickListener(new View.OnClickListener() {
			//
			// @Override
			// public void onClick(View v) {
			// // TODO Auto-generated method stub
			// if (OnItemClickListener!=null) {
			// OnItemClickListener.OnItemClickLinstener(v, pos);
			// }
			// }
			// });
			addView(v);

	
			View lineView = new View(context);
			if (!disableDivider) {
				lineView.setBackgroundColor(context.getResources().getColor(
						R.color.divider_color));
				
				LayoutParams param2 = new LayoutParams(LayoutParams.MATCH_PARENT,
						(int) getResources().getDimension(R.dimen.line_height));
				addView(lineView, param2);
				
			} else {
				LayoutParams param2 = new LayoutParams(LayoutParams.MATCH_PARENT,
						(int) getResources().getDimension(R.dimen.comment_tiny_cell_padding));
				addView(lineView, param2);
			}

		}

	}

	public LinearLayoutForListView(Context context) {
		super(context);
		this.context = context;
	}

	public LinearLayoutForListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	public android.widget.BaseAdapter getAdpater() {
		return adapter;
	}

	public void setAdapter(android.widget.BaseAdapter adpater) {
		this.adapter = adpater;
		removeAllViews();
		fillLinearLayout();
	}

	public OnItemClickLinstener getOnItemClickListner() {
		return OnItemClickListener;
	}

	public interface OnItemClickLinstener {
		public void OnItemClickLinstener(View itemView, int postion);
	}

	public void setOnItemClickLinstener(OnItemClickLinstener OnItemClickListener) {
		this.OnItemClickListener = OnItemClickListener;
	}

	public void setDisableDivider(boolean disableDivider) {
		this.disableDivider = disableDivider;
	}
}
