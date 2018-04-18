package com.houwei.guaishang.database.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by lenovo on 2018/4/18.
 */
@Entity
public class ChatInfoData {
    @Id
    private Long id;
    @Property(nameInDb = "NAME")
    private String name;
    @Property(nameInDb = "PRICE")
    private String price;
    @Property(nameInDb = "TIME")
    private String time;
    public String getTime() {
        return this.time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public String getPrice() {
        return this.price;
    }
    public void setPrice(String price) {
        this.price = price;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    @Generated(hash = 1449433244)
    public ChatInfoData(Long id, String name, String price, String time) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.time = time;
    }
    @Generated(hash = 1455114105)
    public ChatInfoData() {
    }
}
