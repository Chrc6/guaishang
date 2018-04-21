package com.houwei.guaishang.debug;

import android.os.Bundle;
import android.widget.TextView;

import com.houwei.guaishang.R;
import com.houwei.guaishang.activity.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by lenovo on 2018/4/21.
 */

public class DebugActivity extends BaseActivity {

    @BindView(R.id.test_icon)
    TextView testIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.debug_layout);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.test_icon)
    public void onClick() {

    }
}
