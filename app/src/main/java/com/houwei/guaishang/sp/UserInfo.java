package com.houwei.guaishang.sp;

import java.io.Serializable;

/**
 * Created by lenovo on 2018/4/26.
 */

public class UserInfo implements Serializable{
    private String userId;
    private String userName;
    private String avatar;
    private String license;
    private String mobile;
    private String gudingPhone;
    private String  bank;
    private String address;
    private String bankNum;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getGudingPhone() {
        return gudingPhone;
    }

    public void setGudingPhone(String gudingPhone) {
        this.gudingPhone = gudingPhone;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBankNum() {
        return bankNum;
    }

    public void setBankNum(String bankNum) {
        this.bankNum = bankNum;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }
}
