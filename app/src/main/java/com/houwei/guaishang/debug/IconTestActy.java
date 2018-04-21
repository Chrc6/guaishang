package com.houwei.guaishang.debug;

import android.os.Bundle;

import com.houwei.guaishang.R;
import com.houwei.guaishang.activity.BaseActivity;
import com.houwei.guaishang.widget.FloatButton;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lenovo on 2018/4/21.
 */

public class IconTestActy extends BaseActivity {

    @BindView(R.id.float_btn)
    FloatButton floatBtn;

    private ArrayList<String> mList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_test_icon);
        ButterKnife.bind(this);
        for (int i = 0; i < 7; i++) {
            mList.add("http://www.guaishangfaming.com//media/topic/photo/2018-04-21/3b2f505d68fc31dbbb40e456a5f573cf.jpg");
        }
        floatBtn.setStatu(3);
        floatBtn.setBrief("哇哈哈款求生");
        floatBtn.setmAvatarList(mList);
    }
}
