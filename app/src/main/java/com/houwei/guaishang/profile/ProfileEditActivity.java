package com.houwei.guaishang.profile;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.houwei.guaishang.R;
import com.houwei.guaishang.activity.BaseActivity;
import com.houwei.guaishang.sp.UserUtil;
import com.houwei.guaishang.tools.HttpUtil;
import com.houwei.guaishang.tools.ToastUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by lenovo on 2018/4/27.
 * 修改个人信息的页面
 */

public class ProfileEditActivity extends BaseActivity {

    public static final int NAME = 10001;//修改名字
    public static final String Parse_intent = "ParseIntent";
    public static final String Parse_extra = "extra";
    @BindView(R.id.save)
    TextView save;
    @BindView(R.id.edit_tv)
    EditText editTv;


    private int type;//1是名字

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);
        ButterKnife.bind(this);

        parseIntent();
    }

    private void parseIntent() {
        if (getIntent() != null) {
            type = getIntent().getIntExtra(Parse_intent, 0);
            editTv.setText(getIntent().getStringExtra(Parse_extra));
        }
    }

    private void modifyName() {
        if (!TextUtils.isEmpty(editTv.getText().toString())){
            OkGo.<String>post(HttpUtil.IP + "user/modify")
                    .params("userid", UserUtil.getUserInfo().getUserId())
                    .params("event", "name")
                    .params("value", editTv.getText().toString())
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {
                            Intent intent = new Intent();
                            intent.putExtra("result",editTv.getText().toString());
                            setResult(RESULT_OK,intent);
                            finish();
                        }
                        @Override
                        public void onError(Response<String> response) {
                            super.onError(response);
                            ToastUtils.toastForShort(ProfileEditActivity.this,"修改名字失败");
                        }
                    });
        }else {
            ToastUtils.toastForShort(this,"请输入内容");
        }
    }

    @OnClick(R.id.save)
    public void onClick() {
        switch (type) {
            case 1:
                modifyName();
                break;
        }
    }

}
