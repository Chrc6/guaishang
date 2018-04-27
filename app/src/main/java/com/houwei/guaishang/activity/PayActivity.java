package com.houwei.guaishang.activity;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView.BufferType;
import android.widget.ImageView;
import android.widget.TextView;

import com.houwei.guaishang.bean.event.TopicHomeEvent;
import com.houwei.guaishang.sp.UserUtil;
import com.houwei.guaishang.tools.ApplicationProvider;
import com.houwei.guaishang.tools.VoiceUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.houwei.guaishang.R;

import com.houwei.guaishang.bean.AvatarBean;
import com.houwei.guaishang.bean.TopicBean;
import com.houwei.guaishang.tools.ValueUtil;

import org.greenrobot.eventbus.EventBus;

public class PayActivity extends PayBaseActivity {
	
	private String  to_memberid;
	private float price;
	private String  topicId;
	private String  orderTitle;
	private String  brand;

	private CheckBox balance_cb;

	private TextView result_tv;
	
	//访问余额网络成功
	@Override
	protected void getBalenceMoneyFromNetwork(float balanceMoney){
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
		brand = intent.getStringExtra("brand");
		String cover = intent.getStringExtra("cover");
		moneyRequire = price;// 默认全部支付

		ImageView avatar = (ImageView) findViewById(R.id.avatar);
		TextView order_name = (TextView) findViewById(R.id.order_name);
		TextView order_price = (TextView) findViewById(R.id.order_price);
		
		ImageLoader.getInstance().displayImage(cover,avatar);
		
		
		result_tv = (TextView) findViewById(R.id.result_tv);
		
		balance_cb = (CheckBox) findViewById(R.id.balance_cb);

		int face_item_size = (int) this.getResources().getDimension(R.dimen.face_tiny_item_size);
		order_name.setText(getITopicApplication().getFaceManager().
				convertNormalStringToSpannableString(this,orderTitle,face_item_size),
				BufferType.SPANNABLE);
		order_price.setText("支付金额：" + price + "元");
		result_tv.setText("还需支付：" + price + "元");
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
		result_tv.setText("" + moneyRequire + "元");
	}

	// 支付成功（无论是虚拟币还是支付渠道）,由子类处理
	@Override
	protected void paySuccess() {
		showSuccessTips("支付成功！");
		EventBus.getDefault().post(new TopicHomeEvent());
		VoiceUtils.getInstance(ApplicationProvider.privode())
				.getSyntheszer()
				.speak("砸单成功，买"+brand+"就上咚咚砸单");
		Intent i = new Intent(PayActivity.this, MainActivity.class);
		startActivity(i);
		finish();
		
		// 跳转支付成功 debug
//		 Intent i = new Intent(PayActivity.this, PaySuccessActivity.class);
//		 i.putExtra("TopicBean", topicBean);
//		 i.putExtra("to_memberid", to_memberid);
//		 startActivity(i);
	}
	
}
