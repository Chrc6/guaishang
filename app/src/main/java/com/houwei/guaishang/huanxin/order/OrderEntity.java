package com.houwei.guaishang.huanxin.order;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${lei} on 2018/5/17.
 */

public class OrderEntity {

    private String isOfferid;//是否被抢单了
    private List<OrderUserEntity> data;

    public OrderEntity() {
    }

    public String getIsOfferid() {
        return isOfferid;
    }

    public void setIsOfferid(String isOfferid) {
        this.isOfferid = isOfferid;
    }

    public List<OrderUserEntity> getData() {
        return data;
    }

    public void setData(List<OrderUserEntity> data) {
        this.data = data;
    }

    public class OrderUserEntity{
        private String price;//订单报价
        private String cycle;//订单周期
        private String offerld;//报价人id


        public OrderUserEntity() {
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

        public String getOfferld() {
            return offerld;
        }

        public void setOfferld(String offerld) {
            this.offerld = offerld;
        }
    }
}
