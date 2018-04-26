package com.houwei.guaishang.huanxin;

import com.houwei.guaishang.bean.AvatarBean;

import java.io.Serializable;

/**
 * Created by lenovo on 2018/4/23.
 * 聊天页面交互传递的数据集合类
 */

public class ChatInfo implements Serializable {
    private String hisUserID;
    private String hisRealName;
    private AvatarBean headImageBean;
    private int chatType;//聊天方式 群聊 还是单聊
    private String mobile;
    private boolean hideTitle;

    public ChatInfo() {
    }

    public String getHisUserID() {
        return hisUserID;
    }

    public void setHisUserID(String hisUserID) {
        this.hisUserID = hisUserID;
    }

    public String getHisRealName() {
        return hisRealName;
    }

    public void setHisRealName(String hisRealName) {
        this.hisRealName = hisRealName;
    }

    public AvatarBean getHeadImageBean() {
        return headImageBean;
    }

    public void setHeadImageBean(AvatarBean headImageBean) {
        this.headImageBean = headImageBean;
    }

    public int getChatType() {
        return chatType;
    }

    public void setChatType(int chatType) {
        this.chatType = chatType;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public boolean isHideTitle() {
        return hideTitle;
    }

    public void setHideTitle(boolean hideTitle) {
        this.hideTitle = hideTitle;
    }
}
