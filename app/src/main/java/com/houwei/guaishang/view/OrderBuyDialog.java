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
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.houwei.guaishang.R;
import com.houwei.guaishang.activity.BaseActivity;
import com.houwei.guaishang.activity.RechargeDialogActivity;
import com.houwei.guaishang.bean.TopicBean;
import com.houwei.guaishang.easemob.EaseConstant;
import com.houwei.guaishang.tools.HttpUtil;
import com.houwei.guaishang.tools.ShareUtil2;

import java.util.HashMap;
import java.util.List;

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
    private TextView tvRecharge;

    private Context mContext;

    private float money = -1;//余额
    private TopicBean bean;

    public OrderBuyDialog(Context context) {
        super(context,R.style.RechargiDialog);
        mContext = context;
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

    public OrderBuyDialog setData(float money,TopicBean bean) {
        this.money = money;
        this.bean = bean;
        return instance;
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
        if (money >= 0) {
            tvRecharge.setText("现有余额"+money+"元，点此充值");
        }

        rbShare = (CheckBox) view.findViewById(R.id.rb_share);
        rbShare.setSelected(true);
        rbPay = (CheckBox) view.findViewById(R.id.rb_pay);

        llShare.setOnClickListener(this);
        llPay.setOnClickListener(this);
        rbShare.setOnClickListener(this);
        rbPay.setOnClickListener(this);
        tvRecharge.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_pay:
            case R.id.rb_pay:
                rbShare.setChecked(true);
                rbPay.setChecked(false);
                if (money < 1) {
                    goToRechargeActivity();
                } else {
                    Intent i = new Intent();
                    i.putExtra("TopicBean", bean);
                    i.putExtra("position", 0);
                    ((BaseActivity)mContext).jumpToChatActivityCom(bean,0,bean.getMemberId(),
                            bean.getMemberName(), bean.getMemberAvatar(), EaseConstant.CHATTYPE_SINGLE,true);
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
        }
        dismiss();
    }

    private void goToRechargeActivity() {
        Intent intent = new Intent(mContext, RechargeDialogActivity.class);
        mContext.startActivity(intent);
    }

    @Override
    public void show() {
        if (instance != null && !instance.isShowing()) {
            super.show();
        }
    }
}
