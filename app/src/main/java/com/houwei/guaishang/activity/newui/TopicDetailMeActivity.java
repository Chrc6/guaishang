package com.houwei.guaishang.activity.newui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.github.jdsjlzx.ItemDecoration.GridItemDecoration;
import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.github.jdsjlzx.view.CommonHeader;
import com.houwei.guaishang.R;
import com.houwei.guaishang.activity.BaseActivity;
import com.houwei.guaishang.activity.Constant;
import com.houwei.guaishang.activity.GalleryActivity;
import com.houwei.guaishang.adapter.GridAdapter;
import com.houwei.guaishang.adapter.OfferAdapter;
import com.houwei.guaishang.bean.AvatarBean;
import com.houwei.guaishang.bean.IntResponse;
import com.houwei.guaishang.bean.LocationBean;
import com.houwei.guaishang.bean.OffersBean;
import com.houwei.guaishang.bean.TopicBean;
import com.houwei.guaishang.easemob.EaseConstant;
import com.houwei.guaishang.layout.OfferDialog;
import com.houwei.guaishang.manager.FaceManager;
import com.houwei.guaishang.manager.FollowManager;
import com.houwei.guaishang.manager.MyLocationManager;
import com.houwei.guaishang.tools.DealResult;
import com.houwei.guaishang.tools.HttpUtil;
import com.houwei.guaishang.tools.ToastUtils;
import com.houwei.guaishang.tools.ValueUtil;
import com.houwei.guaishang.view.NumberProgressBar;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;


/**
 * 动态评论界面
 */

public class TopicDetailMeActivity extends BaseActivity implements  FollowManager.FollowListener ,MyLocationManager.LocationListener{
	protected Handler handler = new Handler();
	protected ListView listView;
	NumberProgressBar progressBar;
	private TextView header_time;
	private TextView content;
	private TextView header_name;
	private TextView header_location;
	private TextView price_tv;
	private Button order_btn;
	private Button chat_btn;
	private Button follow_btn;
	private TopicBean topicBean;
	private RxPermissions rxPermissions;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private LRecyclerView recyclerViewOffer;
	private FaceManager faceManager;
	private LocationBean currentLocationBean;
	private OfferAdapter mAdapter;
	private TextView tvCount;
	private TextView tvMoney;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_topic_detail_me);
		rxPermissions=new RxPermissions(this);
		topicBean = (TopicBean) getIntent().getSerializableExtra("TopicBean");
		initHeadView();
		initView();
//		initListener();
		getDatas();
	}

	private void initView() {
		getITopicApplication().getLocationManager().addLocationListener(this);
		getITopicApplication().getLocationManager().startLoction(false);
		recyclerViewOffer = (LRecyclerView) findViewById(R.id.recyclerView_offer);
		recyclerViewOffer.setLayoutManager(new LinearLayoutManager(this));
		mAdapter=new OfferAdapter(this);
		mAdapter.setTopicBean(topicBean);
		mAdapter.setDataList(new ArrayList<OffersBean.OfferBean>());
		final LRecyclerViewAdapter lRecyclerViewAdapter=new LRecyclerViewAdapter(mAdapter);
		recyclerViewOffer.setAdapter(lRecyclerViewAdapter);
		recyclerViewOffer.setLoadMoreEnabled(false);
		lRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(View view, int position) {
				OffersBean.OfferBean bean = mAdapter.getDataList().get(position);
				if(TextUtils.equals(getUserID(),bean.getOfferId())){
					ToastUtils.toastForShort(TopicDetailMeActivity.this,"不能同自己聊天");
					return;
				}
				AvatarBean avatarBean=new AvatarBean();
				avatarBean.setOriginal(bean.getAvatar());
				avatarBean.setSmall(bean.getAvatar());
				jumpToChatActivityCom(topicBean,0,bean.getOfferId(), bean.getName(), avatarBean, EaseConstant.CHATTYPE_SINGLE);
			}
		});
		recyclerViewOffer.refresh();
		initHead(lRecyclerViewAdapter);
		recyclerViewOffer.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				mAdapter.clear();
				lRecyclerViewAdapter.notifyDataSetChanged();
				getDatas();
			}
		});

	}

	private void initHead(LRecyclerViewAdapter lRecyclerViewAdapter) {
		CommonHeader header = new CommonHeader(this, R.layout.head_me);
		ImageView avator = (ImageView)header.findViewById(R.id.avator);
		ImageView back = (ImageView) findViewById(R.id.back);
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				TopicDetailMeActivity.this.finish();
			}
		});
		LRecyclerView recyclerView = (LRecyclerView)header.findViewById(R.id.recyclerView);
		recyclerView.setLayoutManager(new GridLayoutManager(this,3));
		GridAdapter mAdapter=new GridAdapter(this);
		final List<AvatarBean> pictures = topicBean.getPicture();
		mAdapter.setDataList(pictures);
		LRecyclerViewAdapter lRecyclerViewAdapter1 = new LRecyclerViewAdapter(mAdapter);

		recyclerView.setAdapter(lRecyclerViewAdapter1);
		GridItemDecoration divider = new GridItemDecoration.Builder(this)
				.setHorizontal(R.dimen.default_divider_padding)
				.setVertical(R.dimen.default_divider_padding)
				.setColorResource(R.color.white_color)
				.build();

//		recyclerView.setHasFixedSize(true);
		recyclerView.addItemDecoration(divider);
		recyclerView.setLoadMoreEnabled(false);
		recyclerView.setPullRefreshEnabled(false);
		recyclerView.refresh();
		lRecyclerViewAdapter1.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(View view, int position) {
				Intent intent=new Intent(TopicDetailMeActivity.this, GalleryActivity.class);
				ArrayList<String> lists=new ArrayList<String>();
				for(AvatarBean bean:pictures){
					lists.add(HttpUtil.IP_NOAPI+bean.getOriginal());
//					Log.d("CCC", HttpUtil.IP_NOAPI+bean.getSmall());
				}
				intent.putExtra(GalleryActivity.EXTRA_IMAGE_URLS, lists);
				intent.putExtra(GalleryActivity.EXTRA_IMAGE_INDEX, position);
				startActivity(intent);
			}
		});

		header_time = (TextView)header. findViewById(R.id.header_time);
		content = (TextView)header. findViewById(R.id.content);
		header_name = (TextView)header. findViewById(R.id.header_name);
		progressBar = (NumberProgressBar)header.findViewById(R.id.progress_bar);
		header_location = (TextView)header.findViewById(R.id.header_location);
		price_tv = (TextView)header. findViewById(R.id.price_tv);
		tvCount = (TextView)header. findViewById(R.id.tv_count);
		tvMoney = (TextView)header. findViewById(R.id.tv_money);
		price_tv = (TextView)header. findViewById(R.id.price_tv);
		order_btn = (Button)header. findViewById(R.id.order_btn);
		order_btn.setVisibility(View.INVISIBLE);
		chat_btn = (Button) header.findViewById(R.id.chat_btn);
		follow_btn = (Button) header.findViewById(R.id.follow_btn);
		faceManager = getITopicApplication().getFaceManager();
		content.setText(faceManager.
						convertNormalStringToSpannableString(this, topicBean.getContent()),
				TextView.BufferType.SPANNABLE);
        tvCount.setText(topicBean.getDealNum());
		tvMoney.setText(topicBean.getSumPrice()+"元");
		header_name.setText(topicBean.getMemberName());
		header_time.setText(topicBean.getTimeString());
		header_location.setText(topicBean.getDistance() != null ? topicBean.getDistanceString() : topicBean.getAddress());
		imageLoader.displayImage(topicBean.getMemberAvatar().findSmallUrl(), avator, getITopicApplication().getOtherManage().getCircleOptionsDisplayImageOptions());
//		Log.d("CCC","-->"+topicBean.getMemberAvatar().findSmallUrl());
		try {
			int max = Integer.valueOf(topicBean.getSetRob());
			int progress = Integer.valueOf(topicBean.getNowRob());
			Log.d("CCC","max:"+max+"-p:"+progress);
			progressBar.setMax(max);
			progressBar.setProgress(progress);
		} catch (Exception e) {
			e.printStackTrace();
		}

		header.findViewById(R.id.order_btn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				OfferDialog d=new OfferDialog(TopicDetailMeActivity.this,topicBean,currentLocationBean==null?"":currentLocationBean.getAddress());
				d.show();
			}
		});

		avator.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				jumpToHisInfoActivity(topicBean.getMemberId(), topicBean.getMemberName(), topicBean.getMemberAvatar());
			}
		});

		chat_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
                /*mContext.jumpToChatActivity(bean.getMemberId(),
                        bean.getMemberName(), bean.getMemberAvatar(), EaseConstant.CHATTYPE_SINGLE);*/

				rxPermissions.request(Manifest.permission.CALL_PHONE)
						.subscribe(new Consumer<Boolean>() {
							@Override
							public void accept(@NonNull Boolean aBoolean) throws Exception {
								if (aBoolean) {
									//用intent启动拨打电话
									String number = topicBean.getMobile();
									if(TextUtils.isEmpty(number)){
										return;
									}
									Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
									if (ActivityCompat.checkSelfPermission(TopicDetailMeActivity.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
										TopicDetailMeActivity.this.startActivity(intent);
									}
								}
							}
						});

			}
		});


		follow_btn.setText(ValueUtil.getRelationTypeStringSimple(topicBean.getFriendship()));
		follow_btn.setBackgroundResource(ValueUtil.getRelationTypeDrawableSimple(topicBean.getFriendship()));
		follow_btn.setTextColor(ValueUtil.getRelationTextColorSimple(topicBean.getFriendship()));
		follow_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (checkLogined()) {
					if(!topicBean.getMemberId().equals(getUserID())){
						progress.show();
						getITopicApplication().getFollowManager().followOnThread(TopicDetailMeActivity.this.getUserID(),
								topicBean.getMemberId());
					}

				}
			}
		});

		lRecyclerViewAdapter.addHeaderView(header);
	}

	private void getDatas() {
		OkGo.<String>post(HttpUtil.IP+"user/lookrob")
				.params("order_id",topicBean.getTopicId())
				.execute(new StringCallback() {
					@Override
					public void onSuccess(Response<String> response) {
						OffersBean bean=DealResult.getInstace().dealBean(TopicDetailMeActivity.this,response,OffersBean.class);
						if(bean.getCode()== Constant.SUCESS){
							List<OffersBean.OfferBean> beans = bean.getData();
							mAdapter.setDataList(beans);
							recyclerViewOffer.refreshComplete(10);
						}else{
							ToastUtils.toastForShort(TopicDetailMeActivity.this,bean.getMessage());
						}
					}

					@Override
					public void onError(Response<String> response) {
						ToastUtils.toastForShort(TopicDetailMeActivity.this,getString(R.string.bad_net));
						super.onError(response);
					}
				});
	}

	private void initHeadView() {
		getITopicApplication().getFollowManager().addFollowListener(this);
	}







	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onStop() {
		super.onStop();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		getITopicApplication().getFollowManager().removeFollowListener(this);
		getITopicApplication().getFollowManager().removeFollowListener(this);
	}


	@Override
	public void FollowChanged(IntResponse followResponse) {
		progress.dismiss();
		if (followResponse.isSuccess()) {
//			if(topicBean.getFriendship())
			if(topicBean.getFriendship()==1){
				follow_btn.setBackgroundResource(R.mipmap.attention_un1);
				topicBean.setFriendship(0);
			}else{
				follow_btn.setBackgroundResource(R.mipmap.attenttion1);
				topicBean.setFriendship(1);
			}

		}else {
			showErrorToast(followResponse.getMessage());
		}
	}


	@Override
	public void onLocationFail() {
//		ToastUtils.toastForShort(this,"定位失败");
	}

	@Override
	public void onLocationSuccess(LocationBean currentLocationBean) {

		this.currentLocationBean = currentLocationBean;
	}

}
