package com.houwei.guaishang.util;

import android.app.Service;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.houwei.guaishang.manager.ITopicApplication;


/**
 * Created by alexlong on 16/3/21.
 */
public class InflateService {

    LayoutInflater layoutInflater;

    private static class SingletonHolder {
        private static final InflateService INSTANCE = new InflateService();
    }
    public static InflateService g() {
        return InflateService.SingletonHolder.INSTANCE;
    }

    public static InflateService g(Context context) {
        return new InflateService(context);
    }

    public static InflateService g(LayoutInflater inflator) {
        return new InflateService(inflator);
    }

    public InflateService(LayoutInflater inflator) {
        if (inflator != null) {
            layoutInflater = inflator;
        } else {
            layoutInflater = (LayoutInflater) ITopicApplication.getContext().getSystemService(Service.LAYOUT_INFLATER_SERVICE);
        }
    }

    public InflateService(Context context) {
        if (context != null) {
            layoutInflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
        } else {
            layoutInflater = (LayoutInflater) ITopicApplication.getContext().getSystemService(Service.LAYOUT_INFLATER_SERVICE);
        }
    }

    private InflateService() {
        layoutInflater = (LayoutInflater) ITopicApplication.getContext().getSystemService(Service.LAYOUT_INFLATER_SERVICE);
    }

    public View inflate(int viewid, ViewGroup parent) {
        return layoutInflater.inflate(viewid, parent, false);
    }

    public View inflate(int viewid) {
        return inflate(viewid, null);
    }

}
