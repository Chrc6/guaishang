package com.houwei.guaishang.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView.BufferType;
import android.widget.ImageView;
import android.widget.TextView;

import com.houwei.guaishang.bean.StringResponse;
import com.houwei.guaishang.bean.event.TopicHomeEvent;
import com.houwei.guaishang.tools.ApplicationProvider;
import com.houwei.guaishang.tools.BitmapSelectorUtil;
import com.houwei.guaishang.tools.HttpUtil;
import com.houwei.guaishang.tools.JsonParser;
import com.houwei.guaishang.tools.ToastUtils;
import com.houwei.guaishang.tools.VoiceUtils;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.entity.LocalMedia;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.houwei.guaishang.R;

import org.greenrobot.eventbus.EventBus;

public class PayActivity extends PayBaseActivity implements View.OnClickListener {
	
	private String  to_memberid;
	private String  offer_id;
	private float price;
	private String  topicId;
	private String  orderTitle;
	private String  brand;
	private String  name;
	private String  bank;
	private String  bankNum;

	private CheckBox balance_cb;

	private TextView result_tv;
	private TextView customerNameTv, customerBankTv, customerBankNumTv;
	private ImageView dongdongIV,customerIv,offlineIv;

	private boolean dongdongDrawable, customerDrawable, offlineDrawable;
	private String picPath;

	private Handler handler = new Handler();
	
	//访问余额网络成功
	@Override
	protected void getBalenceMoneyFromNetwork(float balanceMoney){
		balanceMoney = 0;//金币支付取消
		moneyRequire = price - balanceMoney ;
		moneyRequire = moneyRequire > 0 ? moneyRequire : 0;
		moneyRequire = moneyRequire > price ? price : moneyRequire;
            
		TextView balance_tv = (TextView) findViewById(R.id.balance_tv);
		TextView offset_tv = (TextView) findViewById(R.id.offset_tv);
		balance_tv.setText("账户余额 "+balanceMoney+" 元");
		offset_tv.setText("可使用钱包抵消 "+(price - moneyRequire)+" 元");
		balance_cb.setVisibility(View.VISIBLE);
		findViewById(R.id.progress).setVisibility(View.GONE);
		
		resetResultButton();
	}
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pay);
		initView();
		initListener();
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		super.initView();
		Intent intent = getIntent();
		price = intent.getFloatExtra("price", (float)0.01);
		topicId = intent.getStringExtra("topicId");
		orderTitle = intent.getStringExtra("orderTitle");
		to_memberid = intent.getStringExtra("to_memberid");
		offer_id = intent.getStringExtra("offer_id");
		brand = intent.getStringExtra("brand");

		name = intent.getStringExtra("name");
		bank = intent.getStringExtra("bank");
		bankNum = intent.getStringExtra("bankNum");

		String cover = intent.getStringExtra("cover");
		moneyRequire = price;// 默认全部支付

		ImageView avatar = (ImageView) findViewById(R.id.avatar);
		TextView order_name = (TextView) findViewById(R.id.order_name);
		TextView order_price = (TextView) findViewById(R.id.order_price);
		
		ImageLoader.getInstance().displayImage(cover,avatar);
		
		
		result_tv = (TextView) findViewById(R.id.result_tv);

		customerNameTv = (TextView) findViewById(R.id.tv_customer_name);
		customerBankTv = (TextView) findViewById(R.id.tv_customer_bank_address);
		customerBankNumTv = (TextView) findViewById(R.id.tv_bank_num);
		customerNameTv.setText("商家："+name);
		customerBankTv.setText("开户行："+bank);
		customerBankNumTv.setText("账号："+bankNum);

		balance_cb = (CheckBox) findViewById(R.id.balance_cb);

		dongdongIV = (ImageView) findViewById(R.id.iv_dongdong);
		customerIv = (ImageView) findViewById(R.id.iv_customer);
		offlineIv = (ImageView) findViewById(R.id.iv_offline);

		dongdongIV.setOnClickListener(this);
		customerIv.setOnClickListener(this);
		offlineIv.setOnClickListener(this);

		int face_item_size = (int) this.getResources().getDimension(R.dimen.face_tiny_item_size);
		order_name.setText(getITopicApplication().getFaceManager().
				convertNormalStringToSpannableString(this,orderTitle,face_item_size),
				BufferType.SPANNABLE);
		order_price.setText("支付金额：" + price + "元");
		result_tv.setText(price + "");
		resetResultButton();
		
	}

	@Override
	protected void initListener() {
		// TODO Auto-generated method stub
		super.initListener();
		BackButtonListener();
		findViewById(R.id.pay_tv).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Map<String, String> mapOptional = new HashMap<String, String>();
				mapOptional.put("userid", getUserID());
				mapOptional.put("topicid", topicId);
				mapOptional.put("price", ""+price);
				mapOptional.put("to_memberid", to_memberid);
				mapOptional.put("offer_id", offer_id);
				String title = "购买商品";
				pay(title, mapOptional);
			}
		});
		
		findViewById(R.id.topic_ll).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
					}
				});
		balance_cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean switchON) {
				// TODO Auto-generated method stub
				if (switchON) {
					// 使用余额抵扣
					moneyRequire = price - getBalanceMoney() ;
					moneyRequire = moneyRequire > 0 ? moneyRequire : 0;
					moneyRequire = moneyRequire > price ? price : moneyRequire;
				} else {
					// 不使用余额抵扣
					moneyRequire = price ;
					moneyRequire = moneyRequire > 0 ? moneyRequire : 0;
				}
				resetResultButton();
			}
		});
		getBalenceMoneyFromNetwork(getBalanceMoney());
	}

	// 重新设置还需要多少钱
	private void resetResultButton() {
		result_tv.setText("" + moneyRequire);
	}

	// 支付成功（无论是虚拟币还是支付渠道）,由子类处理
	@Override
	protected void paySuccess() {
		showSuccessTips("支付成功！");
		EventBus.getDefault().post(new TopicHomeEvent());
		VoiceUtils.getInstance(ApplicationProvider.privode())
				.getSyntheszer()
				.speak("砸单成功，买"+brand+"就上怪商抢单");
		Intent i = new Intent(PayActivity.this, MainActivity.class);
		startActivity(i);
		finish();
		
		// 跳转支付成功 debug
//		 Intent i = new Intent(PayActivity.this, PaySuccessActivity.class);
//		 i.putExtra("TopicBean", topicBean);
//		 i.putExtra("to_memberid", to_memberid);
//		 startActivity(i);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.iv_dongdong:
				BitmapSelectorUtil.gotoPic(PayActivity.this, 1, 3, false, false, PAY_TYPE_DONGDONG);
				break;
			case R.id.iv_customer:
				BitmapSelectorUtil.gotoPic(PayActivity.this, 1, 3, false, false, PAY_TYPE_CUSTOMER);
				break;
			case R.id.iv_offline:
				BitmapSelectorUtil.gotoPic(PayActivity.this, 1, 3, false, false, PAY_TYPE_OFFLINE);
				break;
			case R.id.alipay_cb:
			case R.id.ll_ali:
				alipay_cb.setChecked(true);
				weixin_cb.setChecked(false);
				dongdon_cb.setChecked(false);
				customer_cb.setChecked(false);
				offline_cb.setChecked(false);
				payType = PAY_TYPE_ALI;
				break;
			case R.id.weixin_cb:
			case R.id.ll_weixin:
				weixin_cb.setChecked(true);
				alipay_cb.setChecked(false);
				dongdon_cb.setChecked(false);
				customer_cb.setChecked(false);
				offline_cb.setChecked(false);
				payType = PAY_TYPE_WEIXIN;
				break;
			case R.id.cb_company:
			case R.id.ll_dongdong:
				dongdon_cb.setChecked(true);
				alipay_cb.setChecked(false);
				weixin_cb.setChecked(false);
				customer_cb.setChecked(false);
				offline_cb.setChecked(false);
				payType = PAY_TYPE_DONGDONG;
				break;
			case R.id.cb_customer:
			case R.id.ll_customer:
				customer_cb.setChecked(true);
				alipay_cb.setChecked(false);
				dongdon_cb.setChecked(false);
				weixin_cb.setChecked(false);
				offline_cb.setChecked(false);
				payType = PAY_TYPE_CUSTOMER;
				break;
			case R.id.cb_offline:
			case R.id.ll_offline:
				offline_cb.setChecked(true);
				alipay_cb.setChecked(false);
				dongdon_cb.setChecked(false);
				customer_cb.setChecked(false);
				weixin_cb.setChecked(false);
				payType = PAY_TYPE_OFFLINE;
				break;
		}

	}

	@Override
	public void payBankCardPre(int type) {
		super.payBankCardPre(type);
		switch (type) {
			case PAY_TYPE_DONGDONG:
				payBankCard(dongdongDrawable);
				break;
			case PAY_TYPE_CUSTOMER:
				payBankCard(customerDrawable);
				break;
			case PAY_TYPE_OFFLINE:
				payBankCard(offlineDrawable);
				break;

		}
	}

	public void payBankCard(boolean uploadDrawableParam){
		if (uploadDrawableParam) {
			//其他三类支付 操作开始执行，目前等待接口
			//moneyRequire
			new Thread(new UpdateStringRun(picPath,"topic/paymentimg")).start();
		} else {
			ToastUtils.toastForShort(this,"请上传转账图片");
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			List<LocalMedia> pictures = PictureSelector.obtainMultipleResult(data);
			String newPicturePath = pictures.get(0).getPath();
			picPath = newPicturePath;
			switch (requestCode) {
				case PAY_TYPE_DONGDONG:
					dongdongDrawable = true;
					ivLoadBitmap(dongdongIV,newPicturePath);
					break;
				case PAY_TYPE_CUSTOMER:
					customerDrawable = true;
					ivLoadBitmap(customerIv,newPicturePath);
					break;
				case PAY_TYPE_OFFLINE:
					offlineDrawable = true;
					ivLoadBitmap(offlineIv,newPicturePath);
					break;
			}
		}
	}

	public void ivLoadBitmap(ImageView imageView, String newPicturePath) {
		ImageLoader.getInstance().displayImage(
				"file://"+newPicturePath,
				imageView,getITopicApplication().getOtherManage().getRectDisplayImageOptions());
	}

	private class UpdateStringRun implements Runnable {
		private File upLoadBitmapFile;
		private String newPicturePath;
		String port;

		public UpdateStringRun(String newPicturePath, String port) {
			this.newPicturePath = newPicturePath;
			this.upLoadBitmapFile = new File(newPicturePath);
			this.port = port;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			StringResponse retMap = null;
			try {
				String url = HttpUtil.IP + port;
				// 如果不是切割的upLoadBitmap就很大,在这里压缩
				retMap = JsonParser.getStringResponse2(HttpUtil.uploadPayFile(url,
						upLoadBitmapFile, offer_id));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (retMap != null && retMap.isSuccess()) {
				handler.post(new Runnable() {
					@Override
					public void run() {
						paySuccess();
					}
				});
			} else {
				handler.post(new Runnable() {
					@Override
					public void run() {
						progress.dismiss();
						showSuccessTips("支付失败！");
					}
				});
			}
		}
	}
}
