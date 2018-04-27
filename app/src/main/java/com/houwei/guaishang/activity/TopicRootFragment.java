package com.houwei.guaishang.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.gson.reflect.TypeToken;
import com.houwei.guaishang.R;
import com.houwei.guaishang.bean.BaseBean;
import com.houwei.guaishang.bean.IndustryBean;
import com.houwei.guaishang.layout.IndustyPopWindow;
import com.houwei.guaishang.layout.PopInter;
import com.houwei.guaishang.tools.DealResult;
import com.houwei.guaishang.tools.HttpUtil;
import com.houwei.guaishang.tools.SPUtils;
import com.houwei.guaishang.video.JCVideoPlayer;
import com.houwei.guaishang.views.ViewPagerTabButton;
import com.houwei.guaishang.views.ViewPagerTabsAdapter;
import com.houwei.guaishang.views.ViewPagerTabsView;
import com.houwei.guaishang.views.ViewPagerTabsView.PageSelectedListener;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.List;

public class TopicRootFragment extends BaseFragment implements PageSelectedListener,PopInter {

	private String[] mTopTitles = {  "全部", "已订单" };// "原创"

	private List<TopicHomeFragment> listViews;
	IndustyPopWindow pop;
	private ImageView imageAdd;
	private TopicHomeFragment videoLayout1;
//	private TopicFollowedFragment videoLayout2;

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
		getView().findViewById(R.id.title_right_iv).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						Intent i = new Intent(getActivity(),
								SearchActivity.class);
						startActivity(i);
					}
				});
		getView().findViewById(R.id.image_add).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						if(pop!=null&&pop.isShowing()){
							pop.dismiss();
						}else{
							getIndustryData();
						}
					}
				});
	}
    String user_id = "";
	private void getIndustryData() {
		Intent intent = new Intent(getActivity(),BrandSelectActivity.class);
		startActivityForResult(intent,BrandSelectActivity.SELECT_BRAND);
//		progress.show();
//        String url = "";
//        if(checkLogined()){
//            //url = HttpUtil.IP+"topic/my_brand";
//            url = HttpUtil.IP+"topic/brand";
//            user_id = getUserID();
//        }
//		url = HttpUtil.IP+"topic/brand";
//		//IndustryBean.ItemsBean itemsBean = new IndustryBean.ItemsBean();
//
//		//List<IndustryBean.ItemsBean> itemsBeen = new ArrayList<>();
//
//
//		OkGo.<String>post(url)
//                //.params("user_id",userId)
//				.execute(new StringCallback() {
//					@Override
//					public void onSuccess(Response<String> response) {
//						progress.dismiss();
//						DealResult dr=new DealResult();
//						IndustryBean bean=dr.dealData(getActivity(),response,new TypeToken<BaseBean<IndustryBean>>(){}.getType());
//						if(bean==null){
//							return;
//						}
//						final List<IndustryBean.ItemsBean> itemsBeans =bean.getItems();
//						if(itemsBeans.isEmpty()){
//							return;
//						}
//						//Log.i("WXCH","itemsBeen:"+itemsBeans);
//						for(int i=0; i<itemsBeans.size();i++){
//							IndustryBean.ItemsBean item = itemsBeans.get(i);
//							Log.i("WXCH","ItemsBean Id:"+item.getId()+",BrandName:"+item.getBrandName());
//						}
//						showPop(itemsBeans,user_id);
//
//					}
//
//					@Override
//					public void onError(Response<String> response) {
//						super.onError(response);
//						progress.dismiss();
//					}
//				});
	}

	private void showPop(List<IndustryBean.ItemsBean> itemsBeen, String user_id) {
		IndustyPopWindow pop=new IndustyPopWindow(getActivity(),itemsBeen,this,user_id);
		pop.showPopupWindow(imageAdd);
	}

	private void initView() {
		initProgressDialog();
		 videoLayout1 = new TopicHomeFragment();

//		 videoLayout2 = new TopicFollowedFragment();
		TopicFootprintFragment videoLayout3 = new TopicFootprintFragment();


		listViews = new ArrayList<TopicHomeFragment>();
		listViews.add(videoLayout1);
//		listViews.add(videoLayout2);
		listViews.add(videoLayout3);

		ViewPager mPager = (ViewPager) getView().findViewById(R.id.viewpager);
		imageAdd = (ImageView) getView().findViewById(R.id.image_add);
		mPager.setOffscreenPageLimit(mTopTitles.length);
		PagerAdapter mPagerAdapter = new ExamplePagerAdapter(getChildFragmentManager());
		mPager.setAdapter(mPagerAdapter);
		mPager.setCurrentItem(0);
		mPager.setPageMargin(1);

		ViewPagerTabsView mFixedTabs = (ViewPagerTabsView) getView().findViewById(R.id.fixed_tabs);
		mFixedTabs.setOnPageSelectedListener(this);
		ViewPagerTabsAdapter mFixedTabsAdapter = new FixedTabsAdapter(getActivity());
		mFixedTabs.setAdapter(mFixedTabsAdapter);
		mFixedTabs.setViewPager(mPager);

		videoLayout1.refresh();

		//listViews.get(position).refresh();
	}


	public void refreshList(int position){
		ViewPager mPager = (ViewPager) getView().findViewById(R.id.viewpager);
		mPager.setCurrentItem(0);
		listViews.get(position).refresh();
	}

	@Override
	public void onPageSelected(int position) {
		// TODO Auto-generated method stub
		JCVideoPlayer.releaseAllVideos();
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


	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);

	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		Log.d("CCC","fa:"+resultCode);
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case TopicReleaseActivity.RELEASE_SUCCESS:
			refreshList(0);
			break;
		case BrandSelectActivity.SELECT_BRAND:
			String brandParam = data.getStringExtra(BrandSelectActivity.BRAND_PARAM);
			commit(brandParam);
			break;
		default:
			break;
		}
	}

	@Override
	public void commit(String params) {
		SPUtils.put(getActivity(),"brand_id",params);
		videoLayout1.refresh();
	}


	/**
	 * view pager
	 */

	public class ExamplePagerAdapter extends FragmentPagerAdapter {

		public ExamplePagerAdapter(FragmentManager fm) {
			super(fm);
			// TODO Auto-generated constructor stub
		}

		protected transient Activity mContext;

		private List<TopicHomeFragment> viewLists;

		@Override
		public Fragment getItem(int arg0) {
			// TODO Auto-generated method stub
			return listViews.get(arg0);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return listViews.size();
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

	}


}