package com.houwei.guaishang.activity;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.houwei.guaishang.R;
import com.houwei.guaishang.views.ViewPagerTabButton;
import com.houwei.guaishang.views.ViewPagerTabsAdapter;
import com.houwei.guaishang.views.ViewPagerTabsView;
import com.houwei.guaishang.views.ViewPagerTabsView.PageSelectedListener;

public class MineMoneyLogRootActivity extends BaseActivity implements
		PageSelectedListener {

	private List<String> mTopTitles;

	private List<BaseLinearLayout> listViews;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_money_log_root);
		initView();
		initListener();
	}

	@SuppressLint("WrongViewCast")
	private void initListener() {
		// TODO Auto-generated method stub
		BackButtonListener();
		
		TextView title_right = (TextView)findViewById(R.id.title_right);
		title_right.setText("提现");
		
		findViewById(R.id.title_right).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						if (checkLogined()) {
							Intent i = new Intent(MineMoneyLogRootActivity.this,MineTakeMoneyActivity.class);
							startActivity(i);
						}
					}
				});
	}

	private void initView() {
		mTopTitles = new ArrayList<String>();
		
		setTitleName("交易记录");
		mTopTitles.add("我的花销");
		mTopTitles.add("我的收入");
		
		MoneylogLinearLayout videoLayout1 = new MoneylogLinearLayout(this,"order/spendinglog");
		MoneylogLinearLayout videoLayout2 = new MoneylogLinearLayout(this,"order/redpacketlog");
		
		listViews = new ArrayList<BaseLinearLayout>();
		listViews.add(videoLayout1);
		listViews.add(videoLayout2);
		
		ViewPager mPager = (ViewPager) findViewById(R.id.viewpager);
		mPager.setOffscreenPageLimit(mTopTitles.size());
		PagerAdapter mPagerAdapter = new ExamplePagerAdapter(MineMoneyLogRootActivity.this,listViews);
		mPager.setAdapter(mPagerAdapter);
		mPager.setCurrentItem(0);
		mPager.setPageMargin(1);

		ViewPagerTabsView mFixedTabs = (ViewPagerTabsView) findViewById(R.id.fixed_tabs);
		mFixedTabs.setOnPageSelectedListener(this);
		ViewPagerTabsAdapter mFixedTabsAdapter = new FixedTabsAdapter(MineMoneyLogRootActivity.this);
		mFixedTabs.setAdapter(mFixedTabsAdapter,false);
		mFixedTabs.setViewPager(mPager,false);
		
	}

	@Override
	public void onPageSelected(int position) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	/**
	 * view pager
	 */
	public class ExamplePagerAdapter extends PagerAdapter {

		protected transient Activity mContext;


		private List<BaseLinearLayout> viewLists;
		  
		public ExamplePagerAdapter(Activity context, List<BaseLinearLayout> viewLists) {
			this.mContext = context;
			this.viewLists = viewLists;
		}

		@Override
		public int getCount() {
			return viewLists.size();
		}

		@Override
		public void destroyItem(View view, int position, Object object) {
			((ViewPager) view).removeView(viewLists.get(position));
		}

		@Override
		public Object instantiateItem(View view, int position) {
			((ViewPager) view).addView(viewLists.get(position));
			return viewLists.get(position);
		}
		
		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == ((View) object);
		}

		@Override
		public void finishUpdate(View container) {
		}

		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View container) {
		}

	}

	public class FixedTabsAdapter implements ViewPagerTabsAdapter {

		private Activity mContext;

		public FixedTabsAdapter(Activity ctx) {
			this.mContext = ctx;
		}

		@Override
		public View getView(int position) {
			ViewPagerTabButton tab;

			LayoutInflater inflater = mContext.getLayoutInflater();
			tab = (ViewPagerTabButton) inflater.inflate(
					R.layout.viewpager_tab_button, null);

			if (position < mTopTitles.size())
				tab.setText(mTopTitles.get(position));

			return tab;
		}

	}

	@Override
	public void onPageScrollStateChanged(int state) {
		// TODO Auto-generated method stub

	}


}