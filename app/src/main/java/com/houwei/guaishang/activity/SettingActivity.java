package com.houwei.guaishang.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.houwei.guaishang.R;
import com.houwei.guaishang.bean.UserBean;
import com.houwei.guaishang.bean.VersionResponse;
import com.houwei.guaishang.easemob.DemoModel;
import com.houwei.guaishang.event.LoginSuccessEvent;
import com.houwei.guaishang.event.LogouSuccess;
import com.houwei.guaishang.event.TopicSelectEvent;
import com.houwei.guaishang.layout.DialogUtils;
import com.houwei.guaishang.manager.VersionManager;
import com.houwei.guaishang.sp.UserUtil;
import com.houwei.guaishang.tools.ShareSDKUtils;

import org.greenrobot.eventbus.EventBus;

public class SettingActivity extends BaseActivity implements View.OnClickListener, VersionManager.LastVersion {

    private TextView mVersionTv;
    private Dialog mDialog;

    private DemoModel model;

    private TextView mExit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting2);
        if (!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().isRegistered(this);
        }
        initView();
        initListener();
    }


    private void initListener() {
        // TODO Auto-generated method stub
        BackButtonListener();
        getITopicApplication().getVersionManager().setOnLastVersion(this);
        findViewById(R.id.rl_function_desc).setOnClickListener(this);
        findViewById(R.id.rl_version_update).setOnClickListener(this);
        findViewById(R.id.tv_agreement).setOnClickListener(this);
        findViewById(R.id.tv_guide).setOnClickListener(this);
        findViewById(R.id.tv_exit).setOnClickListener(this);
        findViewById(R.id.tv_return).setOnClickListener(this);

    }

    private void initView() {
        model = getITopicApplication().getHuanXinManager().getHxSDKHelper().getModel();

        mVersionTv = (TextView) findViewById(R.id.tv_version);
        mVersionTv.setText("版本号：" + getVersionName(this));

        mDialog = DialogUtils.getCustomDialog(this,R.layout.dialog_exit_layout);
        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1.0f;
                getWindow().setAttributes(lp);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            }
        });
        mDialog.findViewById(R.id.tv_confirm).setOnClickListener(this);
        mDialog.findViewById(R.id.tv_cancle).setOnClickListener(this);

        mExit = (TextView) findViewById(R.id.tv_exit);

        if (UserUtil.isInLoginStata()){
            mExit.setVisibility(View.VISIBLE);
        }else {
            mExit.setVisibility(View.GONE);
        }
    }


    /**
     * 获得apk版本号
     */
    public static String getVersionName(Context context) {
        try {
            final String PackageName = context.getPackageName();
            return context.getPackageManager().getPackageInfo(PackageName, 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "未知";
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.tv_confirm:
                getITopicApplication().getMyUserBeanManager().clean();
                ShareSDKUtils.removeAccount();
                UserUtil.setUserInfo(null);

                getITopicApplication()
                        .getMyUserBeanManager().storeUserInfo(null);
                getITopicApplication()
                        .getMyUserBeanManager()
                        .notityUserInfoChanged(null);
                EventBus.getDefault().post(new LogouSuccess());
                finish();
                break;
            case R.id.tv_cancle:
                mDialog.dismiss();
                break;
            case R.id.tv_exit:
                if (mDialog != null && !mDialog.isShowing()) {
                    showDialog();
                }
                break;

            case R.id.rl_version_update:
                getITopicApplication().getVersionManager().checkNewVersion();
                break;

            case R.id.tv_return:
                finish();
                EventBus.getDefault().post(new TopicSelectEvent());
                break;

            case R.id.rl_function_desc:
                break;

            case R.id.tv_agreement:
                break;

            case R.id.tv_guide:
                break;

            default:
                break;
        }
    }

    private void showDialog() {
        mDialog.show();
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = mDialog.getWindow().getAttributes();
        lp.width = (int)(display.getWidth());
//        lp.height=(int)300;//设置宽度
        mDialog.getWindow().setAttributes(lp);

        WindowManager.LayoutParams lp2 = getWindow().getAttributes();
        lp2.alpha = 0.6f;
        getWindow().setAttributes(lp2);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        getITopicApplication().getVersionManager().removeListener(this);
        if (EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
        super.onDestroy();
    }



    @Override
    public void isLastVersion() {
        // TODO Auto-generated method stub
        showErrorToast("已经是最新版本了");
    }

    @Override
    public void versionNetworkFail(String message) {
        // TODO Auto-generated method stub
        showErrorToast();
    }

    @Override
    public void notLastVersion(VersionResponse.VersionBean versionBean) {
        // TODO Auto-generated method stub
        getITopicApplication().getVersionManager().downLoadNewVersion(versionBean, this);
    }


}
