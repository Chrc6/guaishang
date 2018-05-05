package com.houwei.guaishang.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.houwei.guaishang.R;
import com.houwei.guaishang.bean.UserBean;
import com.houwei.guaishang.manager.MyUserBeanManager;
import com.houwei.guaishang.profile.ProfileEditActivity;
import com.houwei.guaishang.sp.UserInfo;
import com.houwei.guaishang.sp.UserUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by lenovo on 2018/5/5.
 */

public class ProfileInfoActivity extends BaseActivity implements MyUserBeanManager.UserStateChangeListener {


    @BindView(R.id.name_group)
    RelativeLayout nameGroup;
    @BindView(R.id.phone_group)
    RelativeLayout phoneGroup;
    @BindView(R.id.guding_phone_group)
    RelativeLayout gudingPhoneGroup;
    @BindView(R.id.address_group)
    RelativeLayout addressGroup;
    @BindView(R.id.bank_group)
    RelativeLayout bankGroup;
    @BindView(R.id.bank_num_group)
    RelativeLayout bankNumGroup;

    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.phone)
    TextView phone;
    @BindView(R.id.guding_phone)
    TextView gudingPhone;
    @BindView(R.id.address)
    TextView address;
    @BindView(R.id.bank)
    TextView bank;
    @BindView(R.id.bank_num)
    TextView bankNum;
    @BindView(R.id.sex)
    TextView sex;
    @BindView(R.id.age)
    TextView age;

    private MyUserBeanManager myUserBeanManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_profile_activity);
        ButterKnife.bind(this);
        initListener();
    }

    private void initListener() {
        myUserBeanManager = getITopicApplication().getMyUserBeanManager();
        myUserBeanManager.addOnUserStateChangeListener(this);
        onUserInfoChanged(myUserBeanManager.getInstance());
    }

    @OnClick({R.id.name_group, R.id.phone_group, R.id.guding_phone_group, R.id.address_group, R.id.bank_group, R.id.bank_num_group})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.name_group:
                jump(name.getText().toString(), ProfileEditActivity.NAME);
                break;
            case R.id.phone_group:
                jump(phone.getText().toString(), ProfileEditActivity.Phone);
                break;
            case R.id.guding_phone_group:
                jump(gudingPhone.getText().toString(), ProfileEditActivity.GuDing_Phone);
                break;
            case R.id.address_group:
                jump(address.getText().toString(), ProfileEditActivity.Address);
                break;
            case R.id.bank_group:
                jump(bank.getText().toString(), ProfileEditActivity.Bank);
                break;
            case R.id.bank_num_group:
                jump(bankNum.getText().toString(), ProfileEditActivity.Bank_Num);
                break;
        }
    }

    private void jump(String str, int code) {
        Intent intent = new Intent(this, ProfileEditActivity.class);
        intent.putExtra(ProfileEditActivity.Parse_intent, 1);
        intent.putExtra(ProfileEditActivity.Parse_extra, str);
        startActivityForResult(intent, code);
    }

    private UserBean ub;
    private UserInfo userInfo;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case ProfileEditActivity.NAME:
                    String nameRes = data.getStringExtra("result");
                    name.setText(nameRes);
                    ub = getITopicApplication()
                            .getMyUserBeanManager().getInstance();
                    ub.setName(nameRes);
                    //同时改变sp
                    userInfo = UserUtil.getUserInfo();
                    userInfo.setUserName(nameRes);
                    UserUtil.setUserInfo(userInfo);
                    break;

                case ProfileEditActivity.Phone:
                    String phoneRes = data.getStringExtra("result");
                    phone.setText(phoneRes);
                    ub = getITopicApplication()
                            .getMyUserBeanManager().getInstance();
                    ub.setMobile(phoneRes);
                    userInfo = UserUtil.getUserInfo();
                    userInfo.setMobile(phoneRes);
                    UserUtil.setUserInfo(userInfo);
                    break;
                case ProfileEditActivity.GuDing_Phone:
                    String guDingphoneRes = data.getStringExtra("result");
                    gudingPhone.setText(guDingphoneRes);
                    ub = getITopicApplication()
                            .getMyUserBeanManager().getInstance();
                    ub.setMobile(guDingphoneRes);
                    userInfo = UserUtil.getUserInfo();
                    userInfo.setGudingPhone(guDingphoneRes);
                    UserUtil.setUserInfo(userInfo);
                    break;

                case ProfileEditActivity.Address:
                    String addressRes = data.getStringExtra("result");
                    address.setText(addressRes);
                    ub = getITopicApplication()
                            .getMyUserBeanManager().getInstance();
                    //设置地址
                    ub.setAddress(addressRes);
                    userInfo = UserUtil.getUserInfo();
                    userInfo.setAddress(addressRes);
                    UserUtil.setUserInfo(userInfo);
                    break;
                case ProfileEditActivity.Bank:
                    String bankRes = data.getStringExtra("result");
                    bank.setText(bankRes);
                    ub = getITopicApplication()
                            .getMyUserBeanManager().getInstance();
                    //设置开户行
                    ub.setBank(bankRes);
                    userInfo = UserUtil.getUserInfo();
                    userInfo.setBank(bankRes);
                    UserUtil.setUserInfo(userInfo);
                    break;
                case ProfileEditActivity.Bank_Num:
                    String bankNumRes = data.getStringExtra("result");
                    bankNum.setText(bankNumRes);
                    ub = getITopicApplication()
                            .getMyUserBeanManager().getInstance();
                    //设置开户行账号
                    ub.setBankNum(bankNumRes);
                    userInfo = UserUtil.getUserInfo();
                    userInfo.setBankNum(bankNumRes);
                    UserUtil.setUserInfo(userInfo);
                    break;
            }

            getITopicApplication()
                    .getMyUserBeanManager().storeUserInfo(ub);
            getITopicApplication()
                    .getMyUserBeanManager()
                    .notityUserInfoChanged(ub);
        }
    }

    @Override
    public void onUserInfoChanged(UserBean ub) {
        if (ub != null) {
            name.setText(ub.getName());
            age.setText(ub.getAge());
            sex.setText(ub.getSex());
            phone.setText(ub.getMobile());
            address.setText(ub.getAddress());
            gudingPhone.setText(ub.getGudingPhone());
            bank.setText(ub.getBank());
            bankNum.setText(ub.getBankNum());
        }
    }

    @Override
    public void onUserLogin(UserBean ub) {

    }

    @Override
    public void onUserLogout() {

    }
}
