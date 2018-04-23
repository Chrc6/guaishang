package com.houwei.guaishang.base.holder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;


public abstract class BaseHolder<T> {

    protected Context mContext;
    protected HolderParent mHolderParent;
    protected View mRootView;
    protected T mData;
    protected RecyclerView.ViewHolder mSysTemHolder;

    public BaseHolder(Context context) {
        mContext = context;
        mRootView = initView(mContext);
        if(mRootView == null){
            mRootView = new View(mContext);
        }
        ViewHolder.setHolderTag(mRootView, this);
    }

    public BaseHolder(Context context, HolderParent parent) {
        mContext = context;
        mHolderParent = parent;
        mRootView = initView(mContext);
        if(mRootView == null){
            mRootView = new View(mContext);
        }
        ViewHolder.setHolderTag(mRootView, this);
    }

    public View getRootView() {
        return mRootView;
    }

    public Context getContext() {
        return mContext;
    }

    public HolderParent getHolderParent() {
        return mHolderParent;
    }

    public void setHolderParent(HolderParent parent) {
        mHolderParent = parent;
    }

    public void setData(T data) {
        mData = data;
        updateUI(mContext, data);
    }

    public T getData() {
        return mData;
    }

    @NonNull
    protected abstract View initView(Context context);

    protected abstract void  updateUI(Context context, T data);

    public RecyclerView.ViewHolder getSysTemHolder() {
        return mSysTemHolder;
    }

    public void setSysTemHolder(RecyclerView.ViewHolder sysTemHolder) {
        mSysTemHolder = sysTemHolder;
    }
}
