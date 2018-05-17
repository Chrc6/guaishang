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

import cn.sharesdk.onekeyshare.themes.classic.PRTHeader;

/**
 * Created by chrc on 2018/4/26.
 */

public class OrderChatViewPagerAdapter extends FragmentPagerAdapter {

    private  List<OffersBean.OfferBean> userBeans;
    private String sid;
    private String orderId;
    private String brand;
    public OrderChatViewPagerAdapter(FragmentManager fm, List<OffersBean.OfferBean> offerPriceList) {
        super(fm);
        this.userBeans = offerPriceList;
    }

    public OrderChatViewPagerAdapter(FragmentManager fm, List<OffersBean.OfferBean> userBeans, String sid, String orderId, String brand) {
        super(fm);
        this.userBeans = userBeans;
        this.sid = sid;
        this.orderId = orderId;
        this.brand = brand;
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
        chatInfo.setShowPriceInfo(false);
        chatInfo.setCid(offerBean.getUserid());
        chatInfo.setSid(sid);
        chatInfo.setOrderid(orderId);
        chatInfo.setBank(offerBean.getBank());
        chatInfo.setBankNum(offerBean.getBankNum());
        chatInfo.setBrand(brand);
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
