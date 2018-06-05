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
    private boolean showPriceInfo;
    private boolean hideTitle;

    private String cid;
    private String sid;
    private String orderid;
    private String brand;
    private String bank;
    private String bankNum;
    private boolean shouldOffer;
    private String offer_id;
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

    public boolean isShowPriceInfo() {
        return showPriceInfo;
    }

    public void setShowPriceInfo(boolean showPriceInfo) {
        this.showPriceInfo = showPriceInfo;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getBankNum() {
        return bankNum;
    }

    public void setBankNum(String bankNum) {
        this.bankNum = bankNum;
    }

    public boolean isShouldOffer() {
        return shouldOffer;
    }

    public void setShouldOffer(boolean shouldOffer) {
        this.shouldOffer = shouldOffer;
    }


    public String getOffer_id() {
        return offer_id;
    }

    public void setOffer_id(String offer_id) {
        this.offer_id = offer_id;
    }
}
