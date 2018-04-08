package com.houwei.guaishang.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.flyco.dialog.entity.DialogMenuItem;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/10/17.
 */

public class IndustryBean {
    /**
     * total : null
     * totalpage : 0
     * pagesize : 25
     * currentpage : 0
     * nextpage : 0
     * items : [{"id":"1","brandName":"蓝天"},{"id":"2","brandName":"白云"},{"id":"3","brandName":"小白兔"},{"id":"6","brandName":"123"},{"id":"7","brandName":"123"}]
     */

    private Object total;
    private int totalpage;
    private int pagesize;
    private int currentpage;
    private int nextpage;
    private ArrayList<ItemsBean> items;

    public Object getTotal() {
        return total;
    }

    public void setTotal(Object total) {
        this.total = total;
    }

    public int getTotalpage() {
        return totalpage;
    }

    public void setTotalpage(int totalpage) {
        this.totalpage = totalpage;
    }

    public int getPagesize() {
        return pagesize;
    }

    public void setPagesize(int pagesize) {
        this.pagesize = pagesize;
    }

    public int getCurrentpage() {
        return currentpage;
    }

    public void setCurrentpage(int currentpage) {
        this.currentpage = currentpage;
    }

    public int getNextpage() {
        return nextpage;
    }

    public void setNextpage(int nextpage) {
        this.nextpage = nextpage;
    }

    public ArrayList<ItemsBean> getItems() {
        return items;
    }

    public void setItems(ArrayList<ItemsBean> items) {
        this.items = items;
    }

    public static class ItemsBean extends DialogMenuItem {
        /**
         * id : 1
         * brandName : 蓝天
         */
        private String id;
        private String  brandName;
        private int position;

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public ItemsBean(String operName, int resId) {
            super(operName, resId);
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getBrandName() {
            return brandName;
        }

        public void setBrandName(String brandName) {
            this.brandName = brandName;
        }

        @Override
        public String toString() {
            return "ItemsBean{" +
                    "id='" + id + '\'' +
                    ", brandName='" + brandName + '\'' +
                    ", position=" + position +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "IndustryBean{" +
                "total=" + total +
                ", totalpage=" + totalpage +
                ", pagesize=" + pagesize +
                ", currentpage=" + currentpage +
                ", nextpage=" + nextpage +
                ", items=" + items +
                '}';
    }
}
