package com.houwei.guaishang.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/8/29.
 */

public class BaseBean<T> {
//    {"code":1,"message":"成功","data":0}
    private int status;
    private int code;
    private String msg;
    private T data;
    private List<T> datas;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public List<T> getDatas() {
        return datas;
    }

    public void setDatas(List<T> datas) {
        this.datas = datas;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

}
