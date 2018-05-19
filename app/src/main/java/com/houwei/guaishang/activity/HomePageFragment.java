package com.houwei.guaishang.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.jdsjlzx.ItemDecoration.GridItemDecoration;
import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.houwei.guaishang.R;
import com.houwei.guaishang.adapter.GridMeAdapter;
import com.houwei.guaishang.bean.AvatarBean;
import com.houwei.guaishang.bean.HisInfoResponse;
import com.houwei.guaishang.bean.HisUserBean;
import com.houwei.guaishang.bean.HomeInfoResponse;
import com.houwei.guaishang.bean.UserBean;
import com.houwei.guaishang.bean.VersionResponse;
import com.houwei.guaishang.tools.HttpUtil;
import com.houwei.guaishang.tools.JsonParser;
import com.houwei.guaishang.tools.JsonUtil;
import com.houwei.guaishang.tools.ValueUtil;
import com.houwei.guaishang.util.AvatarChangeUtil;
import com.houwei.guaishang.view.CircleImageView;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.entity.LocalMedia;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by ${lei} on 2018/5/19.
 */

public class HomePageFragment extends BaseFragment {

    @BindView(R.id.iv_user_head)
    CircleImageView ivUserHead;
    @BindView(R.id.tv_user_name)
    TextView tvUserName;
    @BindView(R.id.iv_license)
    ImageView ivLicense;
    @BindView(R.id.tv_phone)
    TextView tvPhone;
    @BindView(R.id.tv_mobile_phone)
    TextView tvMobilePhone;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.tv_bank)
    TextView tvBank;
    @BindView(R.id.tv_account)
    TextView tvAccount;
    @BindView(R.id.tv_authentication)
    TextView tvAuthentication;
    @BindView(R.id.ll_info)
    LinearLayout llInfo;
    @BindView(R.id.recycle_view)
    LRecyclerView recycleView;
    Unbinder unbinder;


    private String hisUserID;
    private GridMeAdapter mAdapter;
    private LRecyclerViewAdapter lRecyclerViewAdapter;
    private List<LocalMedia> selectList3 = new ArrayList<>();//只处理页面相关的
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_homepage, container, false);
        parseIntent();
        unbinder = ButterKnife.bind(this, view);
        initRcycle();
        new Thread(inforun).start();
        return view;
    }

    private void parseIntent(){
        hisUserID = getActivity().getIntent().getStringExtra(HomePageActivity.HIS_ID_KEY);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
    private void initRcycle() {
        recycleView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        mAdapter = new GridMeAdapter(getActivity(), null, true);
        selectList3 = new ArrayList<LocalMedia>();
        mAdapter.setDataList(selectList3);
        lRecyclerViewAdapter = new LRecyclerViewAdapter(mAdapter);
        recycleView.setAdapter(lRecyclerViewAdapter);
        GridItemDecoration divider = new GridItemDecoration.Builder(getActivity())
                .setHorizontal(R.dimen.radius_corner)
//                .setVertical(R.dimen.radius_corner)
                .setColorResource(R.color.white_color)
                .build();
        recycleView.setHasFixedSize(true);
        recycleView.addItemDecoration(divider);
        recycleView.setLoadMoreEnabled(false);
        recycleView.setPullRefreshEnabled(false);
        recycleView.refresh();

    }
    private HomePageFragment.MyHandler userhandler = new HomePageFragment.MyHandler(this);

    private class MyHandler extends Handler {

        private WeakReference<BaseFragment> reference;

        public MyHandler(BaseFragment context) {
            reference = new WeakReference<BaseFragment>(context);
        }


        @Override
        public void handleMessage(Message msg) {
            final HomePageFragment
                    activity = (HomePageFragment) reference.get();
            if (activity == null) {
                return;
            }
            switch (msg.what) {
                case BaseActivity.NETWORK_OTHER:
                    final HomeInfoResponse response = (HomeInfoResponse) msg.obj;
                    if (response.isSuccess()) {

                        HisUserBean bean = response.getData();
                        updateUi(bean);
                        break;
                    }
            }
        }
    }

    private void updateUi(HisUserBean ub){
        ImageLoader.getInstance().displayImage(ub.getAvatar().findOriginalUrl(), ivUserHead);
        if (ub.getLicense() != null){
            ImageLoader.getInstance().displayImage(AvatarChangeUtil.findOriginalUrl(ub.getLicense()),ivLicense);
        } else {
            ivLicense.setImageResource(R.drawable.picture_update_icon);
        }
        if (TextUtils.isEmpty(ub.getName())) {
            tvUserName.setText("");
        } else {
            tvUserName.setText(ub.getName());
        }

        tvPhone.setText(getUserInfoStr(R.string.mine_phone, ub.getGudingPhone()));
        tvMobilePhone.setText(getUserInfoStr(R.string.mine_mobile_phone, ub.getMobile()));
        tvAddress.setText(getUserInfoStr(R.string.mine_address, ub.getAddress()));
        tvBank.setText(getUserInfoStr(R.string.mine_bank, ub.getBank()));
        tvAccount.setText(getUserInfoStr(R.string.mine_account, ub.getBankNum()));
        tvAuthentication.setText(getUserInfoStr(R.string.mine_authentication, ""));

        List<AvatarBean> picture = ub.getPicture();
        if (picture == null || picture.size() <= 0){
            recycleView.setVisibility(View.GONE);
        }else {
            selectList3.clear();
            int size = picture.size();
            for (int i = 0; i <size; i++) {
                if (!TextUtils.isEmpty(picture.get(i).getOriginal())){
                    LocalMedia media = new LocalMedia();
//                media.setPath(AvatarChangeUtil.findOriginalUrl(picArray[i]));
                    media.setPath(picture.get(i).getOriginal());
                    selectList3.add(media);
                }
            }
            mAdapter.setDataList(selectList3);
            lRecyclerViewAdapter.notifyDataSetChanged();
            recycleView.refresh();
        }

    }
    private String getUserInfoStr(int resId, String str) {
        if (TextUtils.isEmpty(str)) {
            return getActivity().getResources().getString(resId, "--");
        }
        return getActivity().getResources().getString(resId, str);
    }
    private Runnable inforun = new Runnable() {

        public void run() {
            // TODO Auto-generated method stub
            HomeInfoResponse response = null;
            try {
                Map<String, String> data = new HashMap<String, String>();
                data.put("memberid", hisUserID);
                data.put("userid", getUserID());
                response = JsonParser.getHomeInfoResponse(HttpUtil
                        .getMsg(HttpUtil.IP + "user/profile?"
                                + HttpUtil.getData(data)));
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (response != null) {
                userhandler.sendMessage(userhandler.obtainMessage(
                        BaseActivity.NETWORK_OTHER, response));
            } else {
                userhandler.sendEmptyMessage(BaseActivity.NETWORK_FAIL);
            }
        }
    };
}
