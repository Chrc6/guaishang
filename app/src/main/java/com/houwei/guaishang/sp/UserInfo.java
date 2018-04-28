package com.houwei.guaishang.sp;

import java.io.Serializable;

/**
 * Created by lenovo on 2018/4/26.
 */

public class UserInfo implements Serializable{
    private String userId;
    private String userName;
    private String avatar;

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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
