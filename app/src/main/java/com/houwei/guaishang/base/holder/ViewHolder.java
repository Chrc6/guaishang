package com.houwei.guaishang.base.holder;

import android.util.SparseArray;
import android.view.View;


public class ViewHolder {

    private static int ID_HOLDER = -1;

    @SuppressWarnings("unchecked")
    public static <T extends View> T get(View view, int id) {
        SparseArray<Object> viewHolder = (SparseArray<Object>) view.getTag();
        if (viewHolder == null) {
            viewHolder = new SparseArray<Object>();
            view.setTag(viewHolder);
        }
        if (ID_HOLDER != id) {
            View childView = (View) viewHolder.get(id);
            if (childView == null) {
                childView = view.findViewById(id);
                viewHolder.put(id, childView);
            }
            return (T) childView;
        } else {
            return null;
        }
    }

    public static void setHolderTag(View view, BaseHolder<?> holder) {
        if (view == null) {
            return;
        }
        SparseArray<Object> viewHolder = (SparseArray<Object>) view.getTag();
        if (viewHolder == null) {
            viewHolder = new SparseArray<Object>();
            view.setTag(viewHolder);
        }
        viewHolder.put(ID_HOLDER, holder);
    }

    public static BaseHolder getHolderTag(View view) {
        SparseArray<Object> viewHolder = (SparseArray<Object>) view.getTag();
        if (viewHolder == null) {
            viewHolder = new SparseArray<Object>();
            view.setTag(viewHolder);
        }
        return (BaseHolder) viewHolder.get(ID_HOLDER);
    }
}
