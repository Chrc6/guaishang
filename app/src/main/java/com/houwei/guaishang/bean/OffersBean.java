package com.houwei.guaishang.bean;

import com.houwei.guaishang.tools.HttpUtil;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/10/19.
 */

public class OffersBean extends BaseResponse implements Serializable{
    private List<OfferBean> data;

    public List<OfferBean> getData() {
        return data;
    }

    public void setData(List<OfferBean> data) {
        this.data = data;
    }

    /**
     * id : 12
     * orderId : 30
     * userId : 2
     * offerId : 2
     * price : 66
     * cycle : 66
     * time : 2017-10-18 18:33:15
     * userid : 2
     * mobile : 13810240001
     * password : 123
     * name : 通知
     * avatar : /media/user/photo/2017-10-12/3a83151613bd8346de1eb630fb2a4618.640_640.png
     * age : 0
     * sex : 0
     * personalTags : null
     * intro : null
     * background : null
     * createdAt : 2017-08-15 00:00:00
     * openid : 987654
     * license : null
     * picture : null
     * idCard : null
     */
    public static class OfferBean implements Serializable{
        private String id;
        private String orderId;
        private String offerId;
        private String price;
        private String cycle;
        private String time;
        private String userid;
        private String mobile;
        private String password;
        private String name;
        private String avatar;
        private String age;
        private String sex;
        private Object personalTags;
        private Object intro;
        private Object background;
        private String address;
        private String beizhu;
        private String createdAt;
        private String openid;
        private Object license;
        private Object picture;
        private Object idCard;
        private String bank;
        private String bankNum;

        private boolean isNotify;
        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getBeizhu() {
            return beizhu;
        }

        public void setBeizhu(String beizhu) {
            this.beizhu = beizhu;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }


        public String getOfferId() {
            return offerId;
        }

        public void setOfferId(String offerId) {
            this.offerId = offerId;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getCycle() {
            return cycle;
        }

        public void setCycle(String cycle) {
            this.cycle = cycle;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getUserid() {
            return userid;
        }

        public void setUserid(String userid) {
            this.userid = userid;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAvatar() {
            if (avatar==null || avatar.startsWith("http") || avatar.equals("")) {
                //为null 或者 “” 或者全路径。直接返回原始图片
                return avatar;
            } else {
                return HttpUtil.IP_NOAPI+avatar;
            }
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getAge() {
            return age;
        }

        public void setAge(String age) {
            this.age = age;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public Object getPersonalTags() {
            return personalTags;
        }

        public void setPersonalTags(Object personalTags) {
            this.personalTags = personalTags;
        }

        public Object getIntro() {
            return intro;
        }

        public void setIntro(Object intro) {
            this.intro = intro;
        }

        public Object getBackground() {
            return background;
        }

        public void setBackground(Object background) {
            this.background = background;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getOpenid() {
            return openid;
        }

        public void setOpenid(String openid) {
            this.openid = openid;
        }

        public Object getLicense() {
            return license;
        }

        public void setLicense(Object license) {
            this.license = license;
        }

        public Object getPicture() {
            return picture;
        }

        public void setPicture(Object picture) {
            this.picture = picture;
        }

        public Object getIdCard() {
            return idCard;
        }

        public void setIdCard(Object idCard) {
            this.idCard = idCard;
        }

        public boolean isNotify() {
            return isNotify;
        }

        public void setNotify(boolean notify) {
            isNotify = notify;
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
    }
}
