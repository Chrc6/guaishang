package com.houwei.guaishang.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.houwei.guaishang.base.holder.BaseHolder;
import com.houwei.guaishang.base.holder.ViewHolder;


/**
 * Created by cai on 2016-05-18.<br/>
 */
public abstract class BaseRVAdapter extends RecyclerView.Adapter<BaseRVAdapter.ListHolder> {

    private Context mContext;

    public BaseRVAdapter(Context context) {
        mContext = context;
    }

    @Override
    public ListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseHolder holder = getViewHolder(mContext, parent, viewType);
            return new ListHolder(holder.getRootView());
    }

    @Override
    public void onBindViewHolder(ListHolder holder, int position) {
        View itemView = holder.getView();
        BaseHolder itemHolder = ViewHolder.getHolderTag(itemView);
        itemHolder.setSysTemHolder(holder);
        onBindItemHolder(itemHolder, position);
        itemHolder.setData(getItem(position));
    }

    public void onBindItemHolder(BaseHolder itemHolder, int position) {

    }

    @Override
    public int getItemCount() {
        return getCount();
    }

    public Context getContext() {
        return mContext;
    }

    /**
     * 返回对应position位的数据.
     * 必须重写.
     * @param position 在列表中的第几个位.
     * @return
     */
    protected abstract Object getItem(int position);

    /**
     * 返回这个列表有多少个条目.
     * 必须重写.
     * @return
     */
    protected abstract int getCount();

    /**
     * 返回某种viewType对应的 BaseHolder 是哪一种.
     * @param context
     * @param parent
     * @param viewType
     * @return
     */
    protected abstract BaseHolder getViewHolder(Context context,  ViewGroup parent, int viewType);

    public static class ListHolder extends RecyclerView.ViewHolder {
        private View mItemView;

        public ListHolder(View itemView) {
            super(itemView);
            mItemView = itemView;
        }

        public View getView() {
            return mItemView;
        }
    }

    @Override
    public void onViewDetachedFromWindow(ListHolder holder) {
        super.onViewDetachedFromWindow(holder);
    }


}
