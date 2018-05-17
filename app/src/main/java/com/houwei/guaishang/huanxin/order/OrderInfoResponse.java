package com.houwei.guaishang.huanxin.order;

import com.houwei.guaishang.bean.BaseResponse;

import java.util.ArrayList;

/**
 * Created by ${lei} on 2018/5/14.
 */

public class OrderInfoResponse extends BaseResponse{

    private OrderEntity data;


    public OrderInfoResponse() {
    }

    public OrderEntity getData() {
        return data;
    }

    public void setData(OrderEntity data) {
        this.data = data;
    }
}
