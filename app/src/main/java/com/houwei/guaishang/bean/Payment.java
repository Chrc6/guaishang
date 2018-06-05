package com.houwei.guaishang.bean;

import java.io.Serializable;

/**
 * Created by lenovo on 2018/4/27.
 */

public class Payment implements Serializable {
    private String cycle;
    private int status;
    private String price;
    private String created_at;
    private String id;
    public Payment() {
    }

    public String getCycle() {
        return cycle;
    }

    public void setCycle(String cycle) {
        this.cycle = cycle;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
