package com.houwei.guaishang.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.baidu.tts.tools.SharedPreferencesUtils;
import com.bumptech.glide.Glide;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChatManager;
import com.github.jdsjlzx.ItemDecoration.GridItemDecoration;
import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.houwei.guaishang.R;
import com.houwei.guaishang.activity.newui.MyInfoActivity;
import com.houwei.guaishang.adapter.GridMeAdapter;
import com.houwei.guaishang.bean.CommentPushBean;
import com.houwei.guaishang.bean.FansPushBean;
import com.houwei.guaishang.bean.FloatResponse;
import com.houwei.guaishang.bean.IntResponse;
import com.houwei.guaishang.bean.StringResponse;
import com.houwei.guaishang.bean.UserBean;
import com.houwei.guaishang.data.DBReq;
import com.houwei.guaishang.event.UpdateMoneyEvent;
import com.houwei.guaishang.inter.DeleteInter;
import com.houwei.guaishang.layout.DialogUtils;
import com.houwei.guaishang.layout.PhotoPopupWindow;
import com.houwei.guaishang.manager.ChatManager.OnMyActionMessageGetListener;
import com.houwei.guaishang.manager.ChatManager.OnMyActionMessageHadReadListener;
import com.houwei.guaishang.manager.MyUserBeanManager;
import com.houwei.guaishang.manager.MyUserBeanManager.CheckMoneyListener;
import com.houwei.guaishang.manager.MyUserBeanManager.CheckPointListener;
import com.houwei.guaishang.manager.MyUserBeanManager.UserStateChangeListener;
import com.houwei.guaishang.tools.ApplicationProvider;
import com.houwei.guaishang.tools.BitmapSelectorUtil;
import com.houwei.guaishang.tools.HttpUtil;
import com.houwei.guaishang.tools.JsonParser;
import com.houwei.guaishang.tools.SPUtils;
import com.houwei.guaishang.tools.ToastUtils;
import com.houwei.guaishang.tools.VoiceUtils;
import com.houwei.guaishang.view.CircleImageView;
import com.houwei.guaishang.views.SwitchView;
import com.luck.picture.lib.PictureSelectionModel;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.compress.Luban;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.dialog.CustomDialog;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MineFragmentNew extends BaseFragment implements OnClickListener,
        UserStateChangeListener, OnMyActionMessageGetListener,
        OnMyActionMessageHadReadListener, CheckPointListener, EMEventListener, CheckMoneyListener
        , DeleteInter {

    private final int HEAD_TYPE = 10;
    private final int CARD_TYPE = 11;
    private final int GRID_TYPE = 12;
    private int flag = 0;

    public final static int NETWORK_SUCCESS_DATA_ERROR = 0x06;
    public final static int NETWORK_SUCCESS_DATA_RIGHT = 0x01;

    private boolean fragmentIsHidden;

    private CircleImageView mUserHeadIv;
    private ImageView mLicenseIv;
    private TextView mUserNameTv;
    private TextView mMoneyTv, mTradeCountTv;
    private TextView mPhoneTv, mMobilePhoneTv, mAddressTv, mBankTv, mAccountTv, mAuthenticationTv;
    private ToggleButton toggleButton;
    private LRecyclerView recyclerView;
    private GridMeAdapter mAdapter;
    private LRecyclerViewAdapter lRecyclerViewAdapter;
    private Dialog mChangeHeadDialog;

    private List<LocalMedia> selectList1 = new ArrayList<>();
    private List<LocalMedia> selectList2 = new ArrayList<>();
    private List<LocalMedia> selectList3 = new ArrayList<>();

    private MyUserBeanManager myUserBeanManager;

    private MyHandler handler = new MyHandler(getActivity());

    private boolean isCheckingMoney;
    private String userid;
    private int moneyCount;

    private class MyHandler extends Handler {
        private WeakReference<Context> reference;

        public MyHandler(Context context) {
            reference = new WeakReference<Context>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            progress.dismiss();
            switch (msg.what) {
                case NETWORK_SUCCESS_DATA_RIGHT:
                    StringResponse retMap = (StringResponse) msg.obj;
                    if (retMap.isSuccess()) {
                        PictureFileUtils.deleteCacheDirFile(getActivity());
                        ToastUtils.toastForShort(getActivity(), "上传成功");

                        switch (flag) {
                            case HEAD_TYPE:
                                SPUtils.put(getActivity(), "headimage", HttpUtil.IP_NOAPI + retMap.getData());
                                UserBean ub = getITopicApplication().getMyUserBeanManager()
                                        .getInstance();
                                ub.getAvatar().setSmall(retMap.getData());
                                ub.getAvatar().setOriginal(retMap.getData());
//                                ub.setAvatar();
                                getITopicApplication().getMyUserBeanManager().storeUserInfo(ub);
                                getITopicApplication().getMyUserBeanManager().notityUserInfoChanged(ub);
                                break;
                            case CARD_TYPE:
                                SPUtils.put(getActivity(), "cardimage", HttpUtil.IP_NOAPI + retMap.getData());
                                break;
                            case GRID_TYPE:
                                String res = "";
                                List<StringResponse.PictureBean> lists = retMap.getPictures();
                                if (lists.isEmpty()) {
                                    return;
                                }
                                for (StringResponse.PictureBean bean : lists) {
                                    res = res + HttpUtil.IP_NOAPI + bean.getSmall() + ",";
                                }
                                if (res.endsWith(",")) {
                                    res = res.substring(0, res.length() - 1);
                                }
                                SPUtils.put(getActivity(), "gridimage", res);
                                break;
                        }

                    } else {
                        ToastUtils.toastForShort(getActivity(), retMap.getMessage());
                    }
                    break;
                case NETWORK_SUCCESS_DATA_ERROR:
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_mine_tab_personal_new, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        EventBus.getDefault().register(this);
        initView();
        initData();
        initListener();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        this.fragmentIsHidden = hidden;
        if (!hidden && !isCheckingMoney && myUserBeanManager.getInstance() != null) {
            refreshUI();
            isCheckingMoney = true;
            if (MyUserBeanManager.MISSION_ENABLE) {
                myUserBeanManager.startCheckPointRun();
            }
            myUserBeanManager.startCheckMoneyRun();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!fragmentIsHidden && !isCheckingMoney && myUserBeanManager.getInstance() != null) {
            isCheckingMoney = true;
            if (MyUserBeanManager.MISSION_ENABLE) {
                myUserBeanManager.startCheckPointRun();
            }
            myUserBeanManager.startCheckMoneyRun();
        }

        refreshUI();

        // register the event listener when enter the foreground
        EMChatManager.getInstance().registerEventListener(
                this, new EMNotifierEvent.Event[]{
                        EMNotifierEvent.Event.EventNewMessage,
                        EMNotifierEvent.Event.EventOfflineMessage,
                        EMNotifierEvent.Event.EventConversationListChanged});
    }


    @Override
    public void onStop() {
        EMChatManager.getInstance().unregisterEventListener(this);
        super.onStop();
    }

    @Override
    public void onCheckPointFinish(IntResponse intResponse) {
        // TODO Auto-generated method stub
        moneyCount = intResponse.getData();
        mMoneyTv.setText(moneyCount + "个");
        isCheckingMoney = false;
    }

    @Override
    public void onCheckMoneyFinish(FloatResponse intResponse) {
        // TODO Auto-generated method stub
        mTradeCountTv.setText(getActivity().getResources().getString(R.string.mine_trade_count, String.valueOf(intResponse.getData())));
        isCheckingMoney = false;
    }

    protected void initView() {
        // TODO Auto-generated method stub
        myUserBeanManager = getITopicApplication().getMyUserBeanManager();
        myUserBeanManager.addOnUserStateChangeListener(this);

        mUserHeadIv = (CircleImageView) getView().findViewById(R.id.iv_user_head);
        mUserHeadIv.setOnClickListener(this);
        mLicenseIv = (ImageView) getView().findViewById(R.id.iv_license);
        mLicenseIv.setOnClickListener(this);
        mUserNameTv = (TextView) getView().findViewById(R.id.tv_user_name);

        mChangeHeadDialog = DialogUtils.getCustomDialog(getActivity(),R.layout.fragment_mine_headpic_change_layout);
        TextView changeHeadConfirm = (TextView) mChangeHeadDialog.findViewById(R.id.tv_confirm);
        changeHeadConfirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mChangeHeadDialog.dismiss();
                showBottomPopupWin();
            }
        });

        mPhoneTv = (TextView) getView().findViewById(R.id.tv_phone);
        mMobilePhoneTv = (TextView) getView().findViewById(R.id.tv_mobile_phone);
        mAddressTv = (TextView) getView().findViewById(R.id.tv_address);
        mBankTv = (TextView) getView().findViewById(R.id.tv_bank);
        mAccountTv = (TextView) getView().findViewById(R.id.tv_account);
        mAuthenticationTv = (TextView) getView().findViewById(R.id.tv_authentication);

        mMoneyTv = (TextView) getView().findViewById(R.id.tv_money_count);
        mTradeCountTv = (TextView) getView().findViewById(R.id.tv_money_count);
        mTradeCountTv.setOnClickListener(this);

        recyclerView = (LRecyclerView) getView().findViewById(R.id.recycle_view);

        toggleButton = (ToggleButton) getView().findViewById(R.id.sv_view);

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                System.out.println("准备执行：onCheckedChanged");
                iniToggleBtn(isChecked);
            }
        });
        boolean needVoiceRemind
                = SharedPreferencesUtils.getBoolean(getActivity(),VoiceUtils.VOICE_REMIND,true);
        iniToggleBtn(!needVoiceRemind);//跟287行的有点不一样

        onUserInfoChanged(myUserBeanManager.getInstance());

        checkUnReadFansCount(DBReq.getInstence(getActivity()).getTotalUnReadFansCount());
        checkUnReadChatMessageCount(getUnreadMsgCountTotal());

        if (myUserBeanManager.getInstance() == null) {
            mMoneyTv.setText("0个");
        }

        initProgressDialog(false, null);
        initRcycle();
    }

    private void iniToggleBtn(boolean isChecked) {
        if(isChecked){
            toggleButton.setBackgroundResource(R.drawable.toggle_btn_unchecked);
            SharedPreferencesUtils.putBoolean(getActivity(),VoiceUtils.VOICE_REMIND,false);
        } else {
            toggleButton.setBackgroundResource(R.drawable.toggle_btn_checked_blue);
            SharedPreferencesUtils.putBoolean(getActivity(),VoiceUtils.VOICE_REMIND,true);
        }
    }

    private void initData() {
        userid = getITopicApplication().getMyUserBeanManager().getUserId();
    }

    protected void initListener() {
        // TODO Auto-generated method stub
        getITopicApplication().getChatManager()
                .addOnMyActionMessageGetListener(this);
        getITopicApplication().getChatManager()
                .addOnMyActionMessageHadReadListener(this);
        myUserBeanManager.addOnCheckPointListener(this);
        myUserBeanManager.addOnCheckMoneyListener(this);
        getView().findViewById(R.id.rl_money_count).setOnClickListener(this);
        getView().findViewById(R.id.rl_trade_record).setOnClickListener(this);
        getView().findViewById(R.id.rl_bought).setOnClickListener(this);
        getView().findViewById(R.id.rl_sell).setOnClickListener(this);

        getView().findViewById(R.id.rl_chat).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        // TODO Auto-generated method stub
                        Intent i = new Intent(getActivity(), ConversationActivity.class);
                        i.putExtra("showback", true);
                        startActivity(i);
                    }
                });

        getView().findViewById(R.id.rl_setting).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        // TODO Auto-generated method stub
//                        Intent i = new Intent(getActivity(), MineSystemActivity.class);
                        Intent i = new Intent(getActivity(), SettingActivity.class);
                        startActivity(i);
                    }
                });

    }

    private void initRcycle() {
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        mAdapter = new GridMeAdapter(getActivity(), this, true);
        selectList3 = new ArrayList<LocalMedia>();
        LocalMedia localMedia = new LocalMedia();
        localMedia.setPath("");
        selectList3.add(localMedia);
        mAdapter.setDataList(selectList3);
        lRecyclerViewAdapter = new LRecyclerViewAdapter(mAdapter);
        recyclerView.setAdapter(lRecyclerViewAdapter);
        GridItemDecoration divider = new GridItemDecoration.Builder(getActivity())
                .setHorizontal(R.dimen.radius_corner)
//                .setVertical(R.dimen.radius_corner)
                .setColorResource(R.color.white_color)
                .build();
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(divider);
        recyclerView.setLoadMoreEnabled(false);
        recyclerView.setPullRefreshEnabled(false);
        recyclerView.refresh();
        lRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (TextUtils.isEmpty(mAdapter.getDataList().get(position).getPath().trim())) {
                    flag = GRID_TYPE;
                    showBottomPopupWin();
                } else {
                    PictureSelector.create(getActivity()).externalPicturePreview(position, selectList3);
                }
            }
        });
    }

    public void showBottomPopupWin() {
        hideKeyboard();
        LayoutInflater mLayoutInfalter = (LayoutInflater) getActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout mPopView = (LinearLayout) mLayoutInfalter.inflate(
                R.layout.bottom_photo_select_popupwindow, null);
        PhotoPopupWindow mPopupWin = new PhotoPopupWindow(getActivity(), mPopView);
        mPopupWin.setAnimationStyle(R.style.BottomPopupAnimation);
        mPopupWin.showAtLocation(getActivity().getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
        mPopupWin.setOnSelectPhotoListener(new PhotoPopupWindow.SelectPhotoListener() {
            @Override
            public void onGallery(View v) {
                switch (flag) {
                    case HEAD_TYPE:
                        BitmapSelectorUtil.gotoPic(MineFragmentNew.this, 1, 3, true, true, HEAD_TYPE);
                        break;
                    case CARD_TYPE:
                        BitmapSelectorUtil.gotoPic(MineFragmentNew.this, 1, 3, false, false, CARD_TYPE);
                        BitmapSelectorUtil.gotoPic(MineFragmentNew.this, 9, 3, false, false, GRID_TYPE);
                        break;
                    case GRID_TYPE:
                        BitmapSelectorUtil.gotoPic(MineFragmentNew.this, 9, 3, false, false, GRID_TYPE);
                        break;
                }
            }

            @Override
            public void onCamera(View v) {
                BitmapSelectorUtil.gotoCamer(MineFragmentNew.this, flag);

            }
        });
    }

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        if (!checkLogined()) {
            return;
        }
        Intent i = null;
        switch (arg0.getId()) {
            case R.id.iv_user_head:
                if (mChangeHeadDialog != null && !mChangeHeadDialog.isShowing()) {
                    flag = HEAD_TYPE;
                    selectList1.clear();
                    showDialog();
                }
//                Intent intent = new Intent(getActivity(),OrderChatActivity.class);
//                getActivity().startActivity(intent);
                break;
            case R.id.iv_license:
                if (mChangeHeadDialog != null && !mChangeHeadDialog.isShowing()) {
                    flag = CARD_TYPE;
                    selectList2.clear();
                    showDialog();
                }
                break;
            case R.id.rl_sell:
                i = new Intent(getActivity(), TopicMineActivity.class);
                i.putExtra(HisRootActivity.HIS_ID_KEY, getUserID());
                i.putExtra("title", "我的商品");
                i.putExtra("api", "topic/getlist");
                startActivity(i);
                break;
            case R.id.rl_bought:
                i = new Intent(getActivity(), MinePaidActivity.class);
                startActivity(i);
                break;
            case R.id.rl_money_count:
                i = new Intent(getActivity(), MineTakeMoneyActivity.class);
                startActivity(i);
                break;
            case R.id.rl_trade_record:
                i = new Intent(getActivity(), MineMoneyLogRootActivity.class);
                startActivity(i);
                break;

            default:
                break;
        }
    }

    private void showDialog() {
        mChangeHeadDialog.show();
        WindowManager windowManager = getActivity().getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = mChangeHeadDialog.getWindow().getAttributes();
        lp.width = (int)(display.getWidth());
        lp.height=(int)300;//设置宽度
        mChangeHeadDialog.getWindow().setAttributes(lp);
    }

    @Override
    public void onUserInfoChanged(final UserBean ub) {
        // 如果走的是回调，这里ub不可能是null
        // onCreated还可以主动调用这个方法，这时候可能是null
        if (ub == null) {
            return;
        }

        mUserNameTv.setText("" + ub.getName());
        mPhoneTv.setText(getUserInfoStr(R.string.mine_phone, ""));
        mMobilePhoneTv.setText(getUserInfoStr(R.string.mine_mobile_phone, ub.getMobile()));
        mAddressTv.setText(getUserInfoStr(R.string.mine_address, ""));
        mBankTv.setText(getUserInfoStr(R.string.mine_bank, ""));
        mAccountTv.setText(getUserInfoStr(R.string.mine_account, ""));
        mAuthenticationTv.setText(getUserInfoStr(R.string.mine_authentication, ""));

        if (ub.getAvatar() != null && ub.getAvatar().findOriginalUrl() != null) {
            ImageLoader.getInstance().displayImage(ub.getAvatar().findOriginalUrl(), mUserHeadIv);
        } else {
            mUserHeadIv.setImageResource(R.drawable.user_photo);
        }

//        mUserHeadIv.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//                Intent intent = new Intent(getActivity(),
//                        GalleryActivity.class);
//                ArrayList<String> urls = new ArrayList<String>();
//                urls.add(ub.getAvatar().findOriginalUrl());
//                intent.putExtra(GalleryActivity.EXTRA_IMAGE_URLS, urls);
//                intent.putExtra(GalleryActivity.EXTRA_IMAGE_INDEX, 0);
//                startActivity(intent);
//            }
//        });
    }

    private String getUserInfoStr(int resId, String str) {
        if (TextUtils.isEmpty(str)) {
            return getActivity().getResources().getString(resId, "--");
        }
        return getActivity().getResources().getString(resId, str);
    }

    @Override
    public void onUserLogin(UserBean ub) {
        // TODO Auto-generated method stub
        onUserInfoChanged(ub);
    }

    @Override
    public void onUserLogout() {
        // TODO Auto-generated method stub
        // 用户退出，按道理讲，这里应该把所有控件设为“未登录”
        onUserInfoChanged(new UserBean());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateMoney(UpdateMoneyEvent event) {
        myUserBeanManager.startCheckMoneyRun();
    }


    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        getITopicApplication().getChatManager()
                .removeOnMyActionMessageGetListener(this);
        getITopicApplication().getChatManager()
                .removeOnMyActionMessageHadReadListener(this);
        myUserBeanManager.removeUserStateChangeListener(this);
        myUserBeanManager.removeCheckPointListener(this);
        myUserBeanManager.removeOnCheckMoneyListener(this);
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private void checkUnReadFansCount(int unReadActionCount) {

    }


    private void checkUnReadChatMessageCount(int unReadActionCount) {

    }

    @Override
    public void onMyNewFansGet(FansPushBean fansPushBean) {
        // TODO Auto-generated method stub
        checkUnReadFansCount(DBReq.getInstence(getActivity()).getTotalUnReadFansCount());
    }

    @Override
    public void onNewCommentGet(CommentPushBean commentPushBean) {
        // TODO Auto-generated method stub

    }

    /**
     * read
     */
    @Override
    public void onFansHadRead() {
        // TODO Auto-generated method stub
        checkUnReadFansCount(0);
    }

    @Override
    public void onCommentsHadRead() {
        // TODO Auto-generated method stub

    }

    /**
     * 获取未读聊天消息数
     *
     * @return
     */
    private int getUnreadMsgCountTotal() {
        MainActivity mainAC = (MainActivity) getActivity();
        return mainAC.getUnreadMsgCountTotal();
    }

    /**
     * 监听事件
     */
    @Override
    public void onEvent(EMNotifierEvent event) {
        switch (event.getEvent()) {
            case EventNewMessage: // 普通消息
            {
                refreshUI();
                break;
            }

            case EventOfflineMessage: {
                refreshUI();
                break;
            }

            case EventConversationListChanged: {
                refreshUI();
                break;
            }

            default:
                break;
        }
    }

    private void refreshUI() {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                // 刷新LinearLayout上的红圈
                checkUnReadChatMessageCount(getUnreadMsgCountTotal());
            }
        });
    }

    private void uploadMul(List<LocalMedia> selectList) {
        progress.show();
        ArrayList<String> thumbPictures = new ArrayList<>();
        for (LocalMedia localMedia : selectList) {
            if (!TextUtils.isEmpty(localMedia.getPath())) {
                thumbPictures.add(localMedia.getPath());
            }
        }
        new Thread(new UpdateImagesRun(thumbPictures)).start();
    }

    // 一次HTTP请求上传多张图片 + 各种参数
    private class UpdateImagesRun implements Runnable {
        private ArrayList<String> thumbPictures;

        // thumbPictures 是 List<压缩图路径>
        public UpdateImagesRun(ArrayList<String> thumbPictures) {
            this.thumbPictures = new ArrayList<String>();
            for (String string : thumbPictures) {
                if (!string.equals("" + BasePhotoGridActivity.PICTURE_UPDATE_ICON)) {
                    //去掉最后一个 +图片
                    this.thumbPictures.add(string);
                }
            }
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            StringResponse retMap = null;
            try {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("userid", MineFragmentNew.this.getUserID());
                Log.d("CCC", MineFragmentNew.this.getUserID());
                // 一次http请求将所有图片+参数上传
                retMap = JsonParser.getStringResponse2(HttpUtil.upload(data, thumbPictures, HttpUtil.IP + "user/myself"));
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (retMap != null) {
                handler.sendMessage(handler.obtainMessage(
                        BaseActivity.NETWORK_SUCCESS_DATA_RIGHT, retMap));
            } else {
                handler.sendEmptyMessage(BaseActivity.NETWORK_FAIL);
            }
        }
    }

    // 如果不是切割的upLoadBitmap就很大
    private void upLoadPicture(String newPicturePath, String port) {
        progress.show();
        new Thread(new UpdateStringRun(newPicturePath, port)).start();
    }

    private class UpdateStringRun implements Runnable {
        private File upLoadBitmapFile;
        private String newPicturePath;
        String port;

        public UpdateStringRun(String newPicturePath, String port) {
            this.newPicturePath = newPicturePath;
            this.upLoadBitmapFile = new File(newPicturePath);
            this.port = port;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            StringResponse retMap = null;
            try {
                String url = HttpUtil.IP + port;
                // 如果不是切割的upLoadBitmap就很大,在这里压缩
                retMap = JsonParser.getStringResponse2(HttpUtil.uploadFile(url,
                        upLoadBitmapFile, userid));
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (retMap != null) {
                retMap.setTag(newPicturePath);
                handler.sendMessage(handler.obtainMessage(
                        NETWORK_SUCCESS_DATA_RIGHT, retMap));
            } else {
                handler.sendMessage(handler
                        .obtainMessage(NETWORK_SUCCESS_DATA_ERROR));
            }
        }
    }

    @Override
    public void delete(int positon) {
        mAdapter.remove(positon);
        lRecyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case HEAD_TYPE:
                    selectList1.clear();
                    selectList1 = PictureSelector.obtainMultipleResult(data);
                    Glide.with(getActivity()).load(selectList1.get(0).getPath()).into(mUserHeadIv);
                    LocalMedia m = selectList1.get(0);
                    upLoadPicture(m.getPath(), "user/upload");
                    // 图片选择结果回调
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                    // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
//                    adapter.setList(selectList);
//                    adapter.notifyDataSetChanged();
//                    DebugUtil.i(TAG, "onActivityResult:" + selectList.size());
                    break;
                case CARD_TYPE:
                    selectList2.clear();
                    selectList2 = PictureSelector.obtainMultipleResult(data);
                    Glide.with(getActivity()).load(selectList2.get(0).getPath()).into(mLicenseIv);
                    upLoadPicture(selectList2.get(0).getPath(), "user/id_card");
                    break;
                case GRID_TYPE:
//                    selectList3.clear();
                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                    selectList3.addAll(selectList);
                    if (selectList3.isEmpty()) {
                        return;
                    }

                    if (selectList3.size() >= 9) {
                        int count = 9;
                        List tempList = new ArrayList();
                        for (int i = 0; i < count; i++) {
                            tempList.add(selectList3.get(i));
                        }
                        selectList3.clear();
                        selectList3.addAll(tempList);
                    }

                    for (int i = 0; i < selectList3.size(); i++) {
                        LocalMedia localMedia = selectList3.get(i);
                        if (TextUtils.isEmpty(localMedia.getPath())) {
                            selectList3.remove(localMedia);
                            i--;
                        }
                    }
                    LocalMedia localMedia = new LocalMedia();
                    localMedia.setPath("");
                    selectList3.add(localMedia);

                    mAdapter.clear();
                    lRecyclerViewAdapter.notifyDataSetChanged();
                    mAdapter.setDataList(selectList3);
                    recyclerView.refresh();
                    uploadMul(selectList3);
                    break;
            }
        }
    }
}
