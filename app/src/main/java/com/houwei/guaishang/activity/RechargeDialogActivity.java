package com.houwei.guaishang.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.houwei.guaishang.R;
import com.houwei.guaishang.bean.event.TopicHomeEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by *** on 2018/4/6.
 */

public class RechargeDialogActivity extends RechargeBaseActivity implements View.OnClickListener {

    private EditText etInput;
    private CheckBox rbALi,rbWX;
    private TextView tvCancle,tvConfirm,tvMoneyCount;

    private String  to_memberid;
    private long money = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_dialog_recharge);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        etInput = (EditText)findViewById(R.id.ev_money_input);
        etInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    money = Long.valueOf(String.valueOf(s));
                    if (money <= 0) {
                        etInput.setText(String.valueOf(money));
                    }
                    tvMoneyCount.setText(String.valueOf(money * 100L));
                } catch (Exception e) {
                    money = 1;
                }
            }
        });

        tvMoneyCount = (TextView)findViewById(R.id.tv_dongdong_money_count);
        tvCancle = (TextView) findViewById(R.id.tv_cancle);
        tvConfirm = (TextView) findViewById(R.id.tv_confirm);
        rbALi = (CheckBox) findViewById(R.id.rb_ali);
//        rbALi.setSelected(true);
        rbWX = (CheckBox) findViewById(R.id.rb_wx);
//        rbWX.setSelected(false);

        rbALi.setOnClickListener(this);
        rbWX.setOnClickListener(this);
        tvCancle.setOnClickListener(this);
        tvConfirm.setOnClickListener(this);
        findViewById(R.id.fl_container).setOnClickListener(this);
        findViewById(R.id.ll_ali).setOnClickListener(this);
        findViewById(R.id.ll_wx).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_ali:
            case R.id.rb_ali:
                rbALi.setChecked(true);
                rbWX.setChecked(false);
                break;
            case R.id.ll_wx:
            case R.id.rb_wx:
                rbWX.setChecked(true);
                rbALi.setChecked(false);
                break;
            case R.id.tv_cancle:
                finish();
                break;
            case R.id.tv_confirm:
                //跳到支付
               mobilePay();
                break;
            case R.id.fl_container:
                finish();
                break;
        }
    }

    private void mobilePay() {
        moneyRequire = money * 1.0f;//money单位是元，moneyRequired单位是分
        Map<String, String> mapOptional = new HashMap<String, String>();
        mapOptional.put("userid", getUserID());
//        mapOptional.put("topicid", topicId);
        mapOptional.put("price", ""+ money);
//        mapOptional.put("to_memberid", to_memberid);
        mapOptional.put("isrecharge", "1");
        String title = "购买商品";
        pay(title, mapOptional);
    }

    @Override
    protected boolean chooseALiPay() {
        return rbALi != null && rbALi.isChecked();
    }

    @Override
    protected boolean chooseWXPay() {
        return rbWX != null && rbWX.isChecked();
    }

    @Override
    protected void paySuccess() {
        super.paySuccess();
        progress.dismiss();
        showSuccessTips("支付成功！");
        EventBus.getDefault().post(new TopicHomeEvent());
        Intent i = new Intent(RechargeDialogActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }
}
