package com.houwei.guaishang.huanxin;

import com.houwei.guaishang.bean.BaseResponse;

/**
 * Created by ${lei} on 2018/5/14.
 */

public class OrderInfoResponse extends BaseResponse{

    private String price;//订单报价
    private String circle;//订单周期
    private boolean isOfferid;//是否被抢单了
    public OrderInfoResponse() {
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCircle() {
        return circle;
    }

    public void setCircle(String circle) {
        this.circle = circle;
    }

    public boolean isOfferd() {
        return isOfferid;
    }

    public void setOfferd(boolean offerd) {
        isOfferid = offerd;
    }
}
