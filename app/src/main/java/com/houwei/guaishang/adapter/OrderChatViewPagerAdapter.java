package com.houwei.guaishang.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.houwei.guaishang.bean.AvatarBean;
import com.houwei.guaishang.bean.OffersBean;
import com.houwei.guaishang.bean.UserBean;
import com.houwei.guaishang.easemob.EaseConstant;
import com.houwei.guaishang.huanxin.ChatFragment;
import com.houwei.guaishang.huanxin.ChatInfo;

import java.util.List;

/**
 * Created by chrc on 2018/4/26.
 */

public class OrderChatViewPagerAdapter extends FragmentPagerAdapter {

    private  List<OffersBean.OfferBean> userBeans;

    public OrderChatViewPagerAdapter(FragmentManager fm, List<OffersBean.OfferBean> offerPriceList) {
        super(fm);
        this.userBeans = offerPriceList;
    }

    @Override
    public Fragment getItem(int position) {
        OffersBean.OfferBean offerBean = userBeans.get(position);
        ChatInfo chatInfo = new ChatInfo();
        chatInfo.setMobile(offerBean.getMobile());
        chatInfo.setHisUserID(offerBean.getUserid());
        chatInfo.setHisRealName(offerBean.getName());
        chatInfo.setChatType(EaseConstant.CHATTYPE_SINGLE);
        AvatarBean avatarBean = new AvatarBean();
        avatarBean.setOriginal(offerBean.getAvatar());
        avatarBean.setSmall(offerBean.getAvatar());
        chatInfo.setHeadImageBean(avatarBean);
        chatInfo.setHideTitle(true);
        ChatFragment fragment = ChatFragment.getInstance(chatInfo);
        return fragment;
    }

    @Override
    public int getCount() {
        if (userBeans != null && userBeans.size() > 0) {
            return userBeans.size();
        }
        return 0;
    }
}
