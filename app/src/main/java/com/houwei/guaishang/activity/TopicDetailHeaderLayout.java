package com.houwei.guaishang.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.GridLayoutManager;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.flyco.dialog.widget.base.BaseDialog;
import com.github.jdsjlzx.ItemDecoration.GridItemDecoration;
import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.houwei.guaishang.R;
import com.houwei.guaishang.activity.newui.TopicDetailComActivity;
import com.houwei.guaishang.adapter.GridAdapter;
import com.houwei.guaishang.bean.AvatarBean;
import com.houwei.guaishang.bean.BaseResponse;
import com.houwei.guaishang.bean.CommentBean;
import com.houwei.guaishang.bean.LocationBean;
import com.houwei.guaishang.bean.TopicBean;
import com.houwei.guaishang.bean.UserBean;
import com.houwei.guaishang.bean.event.TopicHomeEvent;
import com.houwei.guaishang.data.Contants;
import com.houwei.guaishang.layout.MProgressDialog;
import com.houwei.guaishang.layout.MenuDialog;
import com.houwei.guaishang.layout.OfferDialog;
import com.houwei.guaishang.layout.OfferDialog2;
import com.houwei.guaishang.layout.PraiseTextView;
import com.houwei.guaishang.manager.FaceManager;
import com.houwei.guaishang.tools.DealResult;
import com.houwei.guaishang.tools.HttpUtil;
import com.houwei.guaishang.tools.SPUtils;
import com.houwei.guaishang.tools.ShareUtil;
import com.houwei.guaishang.tools.ToastUtils;
import com.houwei.guaishang.view.NumberProgressBar;
import com.houwei.guaishang.views.SpannableTextView;
import com.houwei.guaishang.views.SpannableTextView.MemberClickListener;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

public class TopicDetailHeaderLayout extends LinearLayout {
	public TextView share_count_btn;
	public PraiseTextView zan_count_btn;
	public Button follow_btn;
	private BaseActivity context;
	private RxPermissions rxPermissions;

	private NumberProgressBar proBar;
	private  TopicBean topicBean;
	private boolean needPay;


	public TopicDetailHeaderLayout(Context context) {
		super(context);
		LayoutInflater.from(context).inflate(R.layout.layout_topic, this, true);
	}

	public void initView(final BaseActivity context, final TopicBean bean, final LocationBean currentLocationBean, boolean needPay) {
		this.context = context;
		this.topicBean = bean;
		this.needPay = needPay;
		rxPermissions=new RxPermissions(context);
		ImageView avator = (ImageView) findViewById(R.id.avator);
		LRecyclerView recyclerView = (LRecyclerView) findViewById(R.id.recyclerView);
		recyclerView.setLayoutManager(new GridLayoutManager(context,3));
		GridAdapter mAdapter=new GridAdapter(context);
		final List<AvatarBean> pictures = bean.getPicture();
//		if(pictures.size()<9){
//			for(int i=0;i<9;i++){
//				AvatarBean b=new AvatarBean();
//				b.setOriginal("");
//				b.setSmall("");
//				if(pictures.size()==9){
//					break;
//				}
//				pictures.add(b);
//			}
//		}
		mAdapter.setDataList(pictures);
		LRecyclerViewAdapter lRecyclerViewAdapter = new LRecyclerViewAdapter(mAdapter);
		recyclerView.setAdapter(lRecyclerViewAdapter);
		GridItemDecoration divider = new GridItemDecoration.Builder(context)
				.setHorizontal(R.dimen.default_divider_padding)
				.setVertical(R.dimen.default_divider_padding)
				.setColorResource(R.color.white_color)
				.build();
		recyclerView.setHasFixedSize(true);
		recyclerView.addItemDecoration(divider);
		recyclerView.setPullRefreshEnabled(false);
		recyclerView.setLoadMoreEnabled(false);
		recyclerView.refresh();
		lRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(View view, int position) {
				Intent intent=new Intent(context, GalleryActivity.class);
				ArrayList<String> lists=new ArrayList<String>();
				for(AvatarBean bean:pictures){
					lists.add(HttpUtil.IP_NOAPI+bean.getOriginal());
				}
				intent.putExtra(GalleryActivity.EXTRA_IMAGE_URLS, lists);
				intent.putExtra(GalleryActivity.EXTRA_IMAGE_INDEX, position);
				context.startActivity(intent);
			}
		});

		TextView header_time = (TextView) findViewById(R.id.header_time);
		TextView content = (TextView) findViewById(R.id.content);
		TextView header_name = (TextView) findViewById(R.id.header_name);
		proBar = (NumberProgressBar)findViewById(R.id.progress_bar);
		TextView header_location = (TextView)  findViewById(R.id.header_location);
		TextView price_tv = (TextView) findViewById(R.id.price_tv);
		Button order_btn = (Button) findViewById(R.id.order_btn);
		Button chat_btn = (Button) findViewById(R.id.chat_btn);
		follow_btn = (Button) findViewById(R.id.follow_btn);
		try {
			int max = Integer.valueOf(bean.getSetRob());
			int progress = Integer.valueOf(bean.getNowRob());
			proBar.setMax(max);
			proBar.setProgress(progress);
		} catch (Exception e) {
			e.printStackTrace();
		}

//		ImageLoader.getInstance().displayImage(bean.getCover(),jcVideoPlayer.thumbImageView,context.getITopicApplication().getOtherManage().getRectDisplayImageOptions());
		//这时候还没有访问网络获取点赞人，先把点赞人栏目去掉
		View bottom_praise_ll = findViewById(R.id.bottom_praise_ll);
		bottom_praise_ll.setVisibility(View.GONE);
		zan_count_btn = (PraiseTextView) findViewById(R.id.zan_count_btn);
		share_count_btn = (TextView) findViewById(R.id.share_count_btn);
		share_count_btn.setText(bean.getShareNum()+"");
		TextView commentBtn = (TextView) findViewById(R.id.comment_btn);
		ImageLoader.getInstance().displayImage(bean.getMemberAvatar().findSmallUrl(), avator, context.getITopicApplication().getOtherManage().getCircleOptionsDisplayImageOptions());
		content.setText(context.getITopicApplication().getFaceManager().
				convertNormalStringToSpannableString(context,bean.getContent()),
				BufferType.SPANNABLE);
		FaceManager.extractMention2Link(content);
		content.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View arg0) {
				// TODO Auto-generated method stub
				MenuDialog followDialog = new MenuDialog(context,
						new MenuDialog.ButtonClick() {

							@Override
							public void onSureButtonClick() {
								// TODO Auto-generated method stub
								ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
								clipboard.setText(bean.getContent());
							}
						});
				followDialog.show();
				return true;
			}
		});
		commentBtn.setText(bean.getCommentCount()+"");
		header_location.setText(bean.getAddress());
		header_name.setText(bean.getMemberName());
		header_time.setText(bean.getTimeString());
		price_tv.setText("￥" + bean.getPrice() );

		zan_count_btn.setText(bean.getSumPrice());
//		zan_count_btn.setPraiseState(context, bean);
		findViewById(R.id.praise_ll).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				PraiseTextView tv =  (PraiseTextView) v.findViewById(R.id.zan_count_btn);
//				tv.clickPraise(context, bean);
			}
		});

		share_count_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ShareUtil shareUtil = new ShareUtil(context);
				shareUtil.setContent(bean.getContent());
				//shareUtil.setMemberId(topicBean.getMemberId());
				shareUtil.setUrl(HttpUtil.SHARE_TOPIC_IP+bean.getTopicId());
				if (bean.getPicture()!=null && !bean.getPicture().isEmpty()) {
					shareUtil.setImageUrl(bean.getPicture().get(0).findOriginalUrl());
				}
				shareUtil.showBottomPopupWin();

				new Thread(runnable).start();
			}
		});
		
		order_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (context.checkLogined()) {
					if(currentLocationBean == null){
						String city = (String) SPUtils.get(context, Contants.LOCATION_CITY_KEY,"上海市");
						OfferDialog4 d=new OfferDialog4(context,bean,city);
						d.show();
					}else {
						OfferDialog4 d=new OfferDialog4(context,bean,currentLocationBean.getCity()+currentLocationBean.getDistrict());
						d.show();
					}

//				Intent i = new Intent(context, PayActivity.class);
//				i.putExtra("orderTitle", bean.getContent());
//				i.putExtra("cover", bean.getCover());
//				i.putExtra("price", bean.getPrice());
//				i.putExtra("topicId", bean.getTopicId());
//				i.putExtra("to_memberid", bean.getMemberId());
//				context.startActivity(i);
				}
			}
		});

		chat_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
//				context.jumpToChatActivity(bean.getMemberId(),
//						bean.getMemberName(), bean.getMemberAvatar(),EaseConstant.CHATTYPE_SINGLE);
				rxPermissions.request(Manifest.permission.CALL_PHONE)
						.subscribe(new Consumer<Boolean>() {
							@Override
							public void accept(@NonNull Boolean aBoolean) throws Exception {
								if (aBoolean) {
									//用intent启动拨打电话
									String number = bean.getMobile();
									if(TextUtils.isEmpty(number)){
										ToastUtils.toastForShort(context,"电话号码不能为空");
										return;
									}
									Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
									if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
										context.startActivity(intent);
									}
								}
							}
						});
			}
		});
		
		avator.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				context.jumpToHisInfoActivity(bean.getMemberId(), bean.getMemberName(), bean.getMemberAvatar());
			}
		});
		
	/*	if (context.getUserID().equals(bean.getMemberId())) {
			chat_btn.setVisibility(View.GONE);
			order_btn.setVisibility(View.GONE);
			follow_btn.setVisibility(View.GONE);
		}else{
			chat_btn.setVisibility(View.VISIBLE);
			order_btn.setVisibility(View.VISIBLE);
			follow_btn.setVisibility(View.VISIBLE);
		}*/
	}
	
	//展示点赞人列表
	public void setPraiseList(List<CommentBean> praiselist){
		View bottom_praise_ll = findViewById(R.id.bottom_praise_ll);
		bottom_praise_ll.setVisibility((praiselist == null || praiselist.isEmpty())?View.GONE:View.VISIBLE);
		
		SpannableTextView bottom_textview = (SpannableTextView) findViewById(R.id.bottom_textview);
		bottom_textview.setPraiseText(praiselist, new MemberClickListener() {
			
			@Override
			public void onMemberClick(CommentBean commentBean) {
				// TODO Auto-generated method stub
				context.jumpToHisInfoActivity(commentBean.getMemberId(), commentBean.getMemberName(), commentBean.getMemberAvatar());
			}
		});
		
		TextView praise_count_textview = (TextView)findViewById(R.id.praise_count_textview);
		praise_count_textview.setText(praiselist.size()+"人点赞");
	}
	
	//重新设置点赞按钮状态，只在手动点赞之后调用
	public void setPraiseStatus(final TopicBean bean){
		zan_count_btn.setPraiseState(context, bean);
	}

	Runnable runnable = new Runnable(){
		@Override
		public void run() {
			try {
				HashMap<String, String> data = new HashMap<String, String>();
				data.put("id", topicBean.getTopicId());
				String s = HttpUtil.postMsg(HttpUtil.getDataUnSig(data), HttpUtil.IP + "mission/Sharing/");
				int num = topicBean.getShareNum()+1;
				Log.i("WXCH","num:" + num);
				TextView textView = (TextView) findViewById(R.id.share_count_btn);
				textView.setText(num+"");
				textView.invalidate();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	public class OfferDialog4 extends BaseDialog {
		Context context;
		TopicBean topicBean;
		private ImageLoader imageLoader = ImageLoader.getInstance();
		private TextView tvClose;
		private TextView tvConfirm;
		private ImageView imageAvatar;
		private TextView orderName;
		private TextView orderPrice;
		private EditText etMoney;
		private EditText etTime;
		final MProgressDialog progress ;
		private EditText etRemark;
		private String address;


		public OfferDialog4(Context context, TopicBean bean, String address) {
			super(context);
			this.context=context;
			this.topicBean=bean;
			this.address=address;
			progress = new MProgressDialog(context, false);
		}

		@Override
		public View onCreateView() {
			widthScale(0.95f);
//        showAnim(new Swing());

			// dismissAnim(this, new ZoomOutExit());
			View inflate = View.inflate(context, R.layout.dialog_offer, null);
			tvClose=(TextView)inflate.findViewById(R.id.tv_close);
			imageAvatar=(ImageView)inflate.findViewById(R.id.avatar);
			orderName=(TextView)inflate.findViewById(R.id.order_name);
			orderPrice=(TextView)inflate.findViewById(R.id.order_price);
			etMoney=(EditText)inflate.findViewById(R.id.et_money);
			etTime=(EditText)inflate.findViewById(R.id.et_time);
			etRemark=(EditText)inflate.findViewById(R.id.et_remark);
			tvConfirm=(TextView)inflate.findViewById(R.id.tv_confirm);
			return inflate;
		}

		@Override
		public void setUiBeforShow() {
			imageLoader.displayImage(topicBean.getCover(), imageAvatar,
					((TopicDetailActivity)context).getITopicApplication().getOtherManage().getCircleOptionsDisplayImageOptions());
			tvClose.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dismiss();
				}
			});
			orderName.setText(topicBean.getContent());
			tvConfirm.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					String money=etMoney.getText().toString().trim();
					String time=etTime.getText().toString().trim();
					String remark=etTime.getText().toString().trim();
					if(TextUtils.isEmpty(money)){
						ToastUtils.toastForShort(context,"金额不能为空");
						return;
					}
					if(TextUtils.isEmpty(time)){
						ToastUtils.toastForShort(context,"工期不能为空");
						return;
					}
					offerMsg(money,time,remark,needPay);
				}
			});
		}

		private void offerMsg(String money, String time,String remark, boolean needPay) {
			progress.show();
			OkGo.<String>post(HttpUtil.IP+"topic/rob")
					.params("order_id",topicBean.getTopicId())
					.params("user_id",topicBean.getMemberId())
					.params("offer_id",getUserID())
					.params("price",money)
					.params("cycle",time)
					.params("address",address)
					.params("beizhu",remark)
//					.params("payMoney",payMoney)
					.execute(new StringCallback() {
						@Override
						public void onSuccess(Response<String> response) {
							progress.dismiss();
							BaseResponse baseResponse= DealResult.getInstace().dealBase(context,response);
							if(baseResponse==null){
								return;
							}
							if(baseResponse.getCode()==1){
								ToastUtils.toastForShort(context,baseResponse.getMessage());
								dismiss();
								try {
									int max = Integer.valueOf(topicBean.getSetRob());
									int progress = Integer.valueOf(topicBean.getNowRob());
									proBar.setMax(max);
									proBar.setProgress(progress+1);
									EventBus.getDefault().post(new TopicHomeEvent());
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}

						@Override
						public void onError(Response<String> response) {
							progress.dismiss();
							super.onError(response);
						}
					});
		}

		public String getUserID() {
			UserBean instanceUser =  ((TopicDetailActivity)context).getITopicApplication()
					.getMyUserBeanManager().getInstance();
			return instanceUser == null ? "" : instanceUser.getUserid();
		}
	}

}
