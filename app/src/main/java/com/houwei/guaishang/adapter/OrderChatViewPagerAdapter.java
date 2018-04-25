package com.houwei.guaishang.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.houwei.guaishang.bean.UserBean;
import com.houwei.guaishang.easemob.EaseConstant;
import com.houwei.guaishang.huanxin.ChatFragment;
import com.houwei.guaishang.huanxin.ChatInfo;

import java.util.List;

/**
 * Created by chrc on 2018/4/26.
 */

public class OrderChatViewPagerAdapter extends FragmentPagerAdapter {

    private List<UserBean> userBeans;

    public OrderChatViewPagerAdapter(FragmentManager fm, List<UserBean> userBeans) {
        super(fm);
        this.userBeans = userBeans;
    }

    @Override
    public Fragment getItem(int position) {
        UserBean userBean = userBeans.get(position);
        ChatInfo chatInfo = new ChatInfo();
        chatInfo.setMobile("");
        chatInfo.setHisUserID(userBean.getUserid());
        chatInfo.setHisRealName(userBean.getName());
        chatInfo.setChatType(EaseConstant.CHATTYPE_SINGLE);
        chatInfo.setHeadImageBean(userBean.getAvatar());
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
