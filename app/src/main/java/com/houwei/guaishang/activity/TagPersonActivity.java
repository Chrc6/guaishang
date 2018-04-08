package com.houwei.guaishang.activity;

import java.util.ArrayList;
import java.util.List;

import com.houwei.guaishang.R;
import com.houwei.guaishang.bean.NameIDBean;
import com.houwei.guaishang.layout.AddTagDialog;
import com.houwei.guaishang.tools.JsonParser;
import com.houwei.guaishang.tools.JsonUtil;
import com.houwei.guaishang.tools.ValueUtil;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

//编辑个人标签界面
public class TagPersonActivity extends BaseActivity {
	public static final int TAG_SELECT = 0x75;
	public static final String ADD_TAG_STRING = "+ 添加";
	private GridView gridView;
	private List<String> totalList, currentSelectList;
	private PhotoGridAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tags_personal);

		initView();
		initListener();
	}

	private void initView() {
		// TODO Auto-generated method stub
		
		String currentSelectListJson = getIntent().getStringExtra("currentSelectListJson");
		currentSelectList  = ValueUtil.StringToArrayList(currentSelectListJson);
		
		gridView = (GridView) findViewById(R.id.gridView);


		totalList = ValueUtil.getPersonalTags();

		for (String currentString : currentSelectList) {
			if (!totalList.contains(currentString)) {
				totalList.add(currentString);
			}
		}
		
		totalList.add(ADD_TAG_STRING);
		
		adapter = new PhotoGridAdapter(totalList,LayoutInflater.from(TagPersonActivity.this));
		gridView.setAdapter(adapter);
	}

	private void initListener() {
		// TODO Auto-generated method stub
		BackButtonListener();

		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				String selectString =totalList.get(arg2);
				if (selectString.equals(ADD_TAG_STRING)) {
					// 点击 + 添加自定义标签
					final AddTagDialog dialog = new AddTagDialog(
							TagPersonActivity.this,
							new AddTagDialog.SureButtonClick() {

								@Override
								public void onSureButtonClick(String tag) {
									// TODO Auto-generated method stub
									if (!currentSelectList.contains(tag)) {
										totalList.add(totalList.size()-1, tag);
										currentSelectList.add(tag);
										adapter.notifyDataSetChanged();
									}
								}
							});
					dialog.setOnShowListener(new OnShowListener() {
						
						@Override
						public void onShow(DialogInterface dialog2) {
							// TODO Auto-generated method stub
							showKeyboard(dialog.tag_et);
						}
					});
					dialog.show();
					
				} else {
					
					if (currentSelectList.contains(selectString)) {
						currentSelectList.remove(selectString);
					}else{
						currentSelectList.add(selectString);
					}
					adapter.notifyDataSetChanged();
				}
				
			
			}
		});
	
		findViewById(R.id.title_right).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (currentSelectList.size()>50) {
					showErrorToast("最多只能选择50个标签");
					return ;
				}
				Intent data = new Intent();
				data.putExtra("currentSelectListJson", ValueUtil.ArrayListToString(currentSelectList));
				setResult(TAG_SELECT, data);
				finish();
			}
		});
	}

	
	private class PhotoGridAdapter extends BaseAdapter {
		private List<String> investorFoucusDirectionList;

		private LayoutInflater mLayoutInflater;

		public PhotoGridAdapter(List<String> investorFoucusDirectionList,
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
		public String getItem(int position) {
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

			viewHolder.add_tag_btn.setText(getItem(position));
			
			if (currentSelectList.contains(getItem(position))) {
				viewHolder.add_tag_btn.setBackgroundResource( R.drawable.blue_light_drawable);
				viewHolder.add_tag_btn.setTextColor(getResources().getColor(R.color.white_color));
			} else if (viewHolder.add_tag_btn.getText().equals(ADD_TAG_STRING)) {
				viewHolder.add_tag_btn.setBackgroundResource( R.drawable.white_rect_stroke);
				viewHolder.add_tag_btn.setTextColor(getResources().getColor(R.color.text_black_color));
			} else{
				viewHolder.add_tag_btn.setBackgroundResource( R.drawable.white_rect_stroke_normal);
				viewHolder.add_tag_btn.setTextColor(getResources().getColor(R.color.text_black_color));
			}
			return convertView;
		}

		private class MyGridViewHolder {
			TextView add_tag_btn;
		}
	}

}
