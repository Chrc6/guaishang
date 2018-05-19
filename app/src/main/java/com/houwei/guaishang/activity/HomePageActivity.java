package com.houwei.guaishang.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;

import com.houwei.guaishang.R;

/**
 * Created by ${lei} on 2018/5/19.
 他人页面
 */

public class HomePageActivity extends BaseActivity {

    private HomePageFragment fragment;
    public static final String HIS_ID_KEY = "hisUserId";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_framelayout);
        initView();
    }


    protected void initView() {
        // TODO Auto-generated method stub
        initProgressDialog();
        fragment = new HomePageFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.container,fragment);
        transaction.commit();
    }
}
