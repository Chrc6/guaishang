package com.houwei.guaishang.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.houwei.guaishang.R;
import com.houwei.guaishang.activity.BaseActivity;
import com.houwei.guaishang.activity.MainActivity;
import com.houwei.guaishang.activity.RechargeDialogActivity;
import com.houwei.guaishang.activity.TopicReleaseActivity;
import com.houwei.guaishang.bean.TopicBean;
import com.houwei.guaishang.easemob.EaseConstant;
import com.houwei.guaishang.tools.HttpUtil;
import com.houwei.guaishang.tools.ShareUtil2;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;

/**
 * 发布订单dialog
 *
 */

public class PublishOrderDialog extends Dialog implements OnClickListener {

    private static PublishOrderDialog instance;

    private LinearLayout llCamera,llPhoto;
    private CheckBox cbCamera,cbPhoto;

    private Activity mActivity;

    private TopicBean bean;

    public PublishOrderDialog(Activity activity) {
        super(activity,R.style.RechargiDialog);
        mActivity = activity;
    }

    public static PublishOrderDialog getInstance(Activity activity) {
        if (instance == null) {
            synchronized (OrderBuyDialog.class) {
                if (instance == null) {
                    instance = new PublishOrderDialog(activity);
                }
            }
        }
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = getLayoutInflater().inflate(R.layout.view_dialog_order_publish,
                null);
        setContentView(view);

        //一定要在setContentView之后调用，否则无效
        getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        setCanceledOnTouchOutside(true);
        initViews(view);
    }

    private void initViews(View view) {

        llCamera = (LinearLayout) view.findViewById(R.id.ll_camera);
        llPhoto = (LinearLayout) view.findViewById(R.id.ll_photo_albumn);

        cbCamera = (CheckBox) view.findViewById(R.id.cb_camera);
        cbCamera.setChecked(true);
        cbPhoto = (CheckBox) view.findViewById(R.id.cb_photo_albumn);
        cbPhoto.setChecked(false);

        llCamera.setOnClickListener(this);
        llPhoto.setOnClickListener(this);
        view.findViewById(R.id.fl_container).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent(mActivity,TopicReleaseActivity.class);
        switch (v.getId()) {
            case R.id.ll_camera:
                cbCamera.setChecked(true);
                cbPhoto.setChecked(false);
                i.putExtra("type",0);
                mActivity.startActivityForResult(i,0);
                break;
            case R.id.ll_photo_albumn:
                cbPhoto.setChecked(true);
                cbCamera.setChecked(false);
                i.putExtra("type",1);
                mActivity.startActivityForResult(i,0);
                break;
            case R.id.fl_container:
                break;
        }
        dismiss();
    }

    @Override
    public void show() {
        if (instance != null && !instance.isShowing()) {
            super.show();
        }
    }
}
