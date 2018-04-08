package com.houwei.guaishang.database.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by *** on 2018/4/6.
 */

@Entity
public class HomeTopicCacheData {

    @Id
    private Long id;
    @Property(nameInDb = "DATA")
    private String data;
    @Property(nameInDb = "TYPE")
    private int type;

    public HomeTopicCacheData(String data, int type) {
        this.data = data;
        this.type = type;
    }
    
    public String getData() {
        return this.data;
    }
    public void setData(String data) {
        this.data = data;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }
    @Generated(hash = 1621000214)
    public HomeTopicCacheData(Long id, String data, int type) {
        this.id = id;
        this.data = data;
        this.type = type;
    }

    @Generated(hash = 1891883518)
    public HomeTopicCacheData() {
    }
}
