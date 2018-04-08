package com.houwei.guaishang.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.houwei.guaishang.R;
import com.houwei.guaishang.layout.VideoAdapter;
import com.houwei.guaishang.manager.MyUserBeanManager;
import com.houwei.guaishang.video.JCVideoPlayer;
import com.houwei.guaishang.views.ViewPagerTabButton;
import com.houwei.guaishang.views.ViewPagerTabsAdapter;
import com.houwei.guaishang.views.ViewPagerTabsView;
import com.houwei.guaishang.views.ViewPagerTabsView.PageSelectedListener;

public class VideoRootFragment extends BaseFragment implements
		PageSelectedListener {

	private String[] mTopTitles = { "奇葩", "萌物", "精品", "美女" , "原创" };// "原创"

	private List<BaseLinearLayout> listViews;
//	private VideoLinearLayout videoLayout1,videoLayout2,videoLayout3,videoLayout4;
//	private OriginalVideoLinearLayout originalVideoLinearLayout;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.activity_video, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
		initListener();
	}

	private void initListener() {
		// TODO Auto-generated method stub
		getView().findViewById(R.id.title_right).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						if (checkLogined()) {
							Intent i = new Intent(getActivity(),VideoReleaseActivity.class);
							startActivityForResult(i, 1);
						}
					}
				});
	}

	private void initView() {
		
		VideoLinearLayout videoLayout1 = new VideoLinearLayout(getBaseActivity(),"VAP4BFE3U");
		VideoLinearLayout videoLayout2 = new VideoLinearLayout(getBaseActivity(),"VAP4BFR16");
		VideoLinearLayout videoLayout3 = new VideoLinearLayout(getBaseActivity(),"VATL2LQO4");
		VideoLinearLayout videoLayout4 = new VideoLinearLayout(getBaseActivity(),"VAP4BG6DL");
		OriginalVideoLinearLayout originalVideoLinearLayout = new OriginalVideoLinearLayout(getBaseActivity());
		
		listViews = new ArrayList<BaseLinearLayout>();
		listViews.add(videoLayout1);
		listViews.add(videoLayout2);
		listViews.add(videoLayout3);
		listViews.add(videoLayout4);
		listViews.add(originalVideoLinearLayout);
		
		ViewPager mPager = (ViewPager) getView().findViewById(R.id.viewpager);
		mPager.setOffscreenPageLimit(mTopTitles.length);
		PagerAdapter mPagerAdapter = new ExamplePagerAdapter(getActivity(),listViews);
		mPager.setAdapter(mPagerAdapter);
		mPager.setCurrentItem(0);
		mPager.setPageMargin(1);

		ViewPagerTabsView mFixedTabs = (ViewPagerTabsView) getView().findViewById(R.id.fixed_tabs);
		mFixedTabs.setOnPageSelectedListener(this);
		ViewPagerTabsAdapter mFixedTabsAdapter = new FixedTabsAdapter(getActivity());
		mFixedTabs.setAdapter(mFixedTabsAdapter);
		mFixedTabs.setViewPager(mPager);
		
		videoLayout1.refresh();
	}

	@Override
	public void onPageSelected(int position) {
		// TODO Auto-generated method stub
		JCVideoPlayer.releaseAllVideos();
		listViews.get(position).refresh();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onPause() {
		super.onPause();
		JCVideoPlayer.releaseAllVideos();
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (hidden) {
			JCVideoPlayer.releaseAllVideos();
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case TopicReleaseActivity.RELEASE_SUCCESS:
			// 发布成功
			ViewPager mPager = (ViewPager) getView().findViewById(R.id.viewpager);
			mPager.setCurrentItem(listViews.size()-1);
			
			OriginalVideoLinearLayout originalVideoLinearLayout = (OriginalVideoLinearLayout) listViews.get(listViews.size()-1);
			
			originalVideoLinearLayout.refreshEnforced();
			
			break;
		default:
			break;
		}
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
//			return 5;
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

			if (position < mTopTitles.length)
				tab.setText(mTopTitles[position]);

			return tab;
		}

	}

	@Override
	public void onPageScrollStateChanged(int state) {
		// TODO Auto-generated method stub

	}


}