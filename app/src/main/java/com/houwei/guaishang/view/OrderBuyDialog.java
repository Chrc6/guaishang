package com.houwei.guaishang.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.houwei.guaishang.R;
import com.houwei.guaishang.activity.BaseActivity;
import com.houwei.guaishang.activity.RechargeDialogActivity;
import com.houwei.guaishang.activity.TopicDetailActivity;
import com.houwei.guaishang.bean.TopicBean;
import com.houwei.guaishang.event.UpdateMoneyEvent;
import com.houwei.guaishang.tools.HttpUtil;
import com.houwei.guaishang.tools.ShareUtil2;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;

/**
 * 充值对话框
 *
 */

public class OrderBuyDialog extends Dialog implements OnClickListener {

    private static OrderBuyDialog instance;

    private LinearLayout llShare,llPay;
    private CheckBox rbShare,rbPay;
    private TextView tvRecharge,tvPay;

    private Context mContext;

    private float money = -1;//余额
    private TopicBean bean;
    private BaseActivity activity;

    public OrderBuyDialog(Context context) {
        super(context,R.style.OrderBugDialog);
        mContext = context;
    }

    public OrderBuyDialog(BaseActivity context, float money, TopicBean bean,FinishCallBack callBack) {
        super(context,R.style.OrderBugDialog);
        mContext = context;
        activity = context;
        this.money = money;
        this.bean = bean;
        this.callBack = callBack;
    }

    public static OrderBuyDialog getInstance(Context context) {
        if (instance == null) {
            synchronized (OrderBuyDialog.class) {
                if (instance == null) {
                    instance = new OrderBuyDialog(context);
                }
            }
        }
        return instance;
    };

    public OrderBuyDialog setData(float money,TopicBean bean,BaseActivity activity,FinishCallBack callBack) {
//        return instance;
        return new OrderBuyDialog(activity,money,bean,callBack);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = getLayoutInflater().inflate(R.layout.view_dialog_order_buy,
                null);
        setContentView(view);

        //一定要在setContentView之后调用，否则无效
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        setCanceledOnTouchOutside(true);
        initViews(view);
    }

    private void initViews(View view) {

        llShare = (LinearLayout) view.findViewById(R.id.ll_share);
        llPay = (LinearLayout) view.findViewById(R.id.ll_pay);

        tvRecharge = (TextView)view.findViewById(R.id.tv_recharge);
        tvPay = (TextView)view.findViewById(R.id.tv_pay);
        if (money >= 0) {
            tvRecharge.setText("现有余额"+money+"元，点此充值");
        }
        tvPay.setText("抢单支付"+bean.getRobbingPrice()+"个金币");

        rbShare = (CheckBox) view.findViewById(R.id.rb_share);
        rbShare.setSelected(true);
        rbPay = (CheckBox) view.findViewById(R.id.rb_pay);
        rbPay.setSelected(false);

        llShare.setOnClickListener(this);
        llPay.setOnClickListener(this);
        rbShare.setOnClickListener(this);
        rbPay.setOnClickListener(this);
        tvRecharge.setOnClickListener(this);
        view.findViewById(R.id.fl_container).setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_pay:
            case R.id.rb_pay:
                rbShare.setChecked(true);
                rbPay.setChecked(false);
                float robPrice = 10;
                if (bean != null) {
                    robPrice = bean.getRobbingPrice();
                }
                if (money < robPrice) {
                    goToRechargeActivity();
                } else {
                    if (callBack != null){
                        callBack.call();
                    }
//                    Intent i=new Intent(activity, TopicDetailActivity.class);
//                    i.putExtra("TopicBean", bean);
//                    i.putExtra("position", 0);
//                    i.putExtra("needPay", Integer.valueOf(bean.getIsOffer()));
//                    activity.startActivity(i);
//                    Intent i = new Intent();
//                    i.putExtra("TopicBean", bean);
//                    i.putExtra("position", 0);
//                    ((BaseActivity)mContext).jumpToChatActivityCom(bean,0,bean.getMemberId(),
//                            bean.getMemberName(), bean.getMemberAvatar(), EaseConstant.CHATTYPE_SINGLE,true);
                }
                break;
            case R.id.ll_share:
            case R.id.rb_share:
                rbPay.setChecked(true);
                rbShare.setChecked(false);
                PlatformActionListener platformActionListener = new PlatformActionListener() {
                    @Override
                    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                            //送10元券
                        OkGo.<String>post(HttpUtil.IP + "user/modify")
                                .params("userid", activity.getUserID())
                                .params("topicid", bean.getTopicId())
                                .params("event", "is_share")
                                .params("value", "1")
                                .execute(new StringCallback() {
                                    @Override
                                    public void onSuccess(Response<String> response) {
                                        if (callBack != null){
                                            callBack.call();
                                        }
                                        EventBus.getDefault().post(new UpdateMoneyEvent());
                                    }
                                    @Override
                                    public void onError(Response<String> response) {
                                        super.onError(response);
                                    }
                                });

                    }

                    @Override
                    public void onError(Platform platform, int i, Throwable throwable) {

                    }

                    @Override
                    public void onCancel(Platform platform, int i) {

                    }
                };
                String content = "分享朋友圈送10元券";
                String url = HttpUtil.SHARE_TOPIC_IP + bean.getTopicId();
                ShareUtil2.shareToWXmomentsForOrderBuy(mContext,content,url,"",platformActionListener);
                break;
            case R.id.tv_recharge:
                goToRechargeActivity();
                break;
            case R.id.fl_container:
                break;
        }
        dismiss();
    }

    private void goToRechargeActivity() {
        Intent intent = new Intent(mContext, RechargeDialogActivity.class);
        mContext.startActivity(intent);
    }

    @Override
    public void show() {
//        if (instance != null && !instance.isShowing() && activity != null) {
            super.show();
//        }
    }

    private FinishCallBack callBack;
    public interface  FinishCallBack{
        void call();
    }
}
