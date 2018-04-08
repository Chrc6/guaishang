package com.houwei.guaishang.layout;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.houwei.guaishang.adapter.OfferAdapter;

/**
 * Created by Administrator on 2017/10/27.
 */

public class TopicLinearLayoutManager extends LinearLayoutManager {
    OfferAdapter adapter;
    public TopicLinearLayoutManager(Context context,OfferAdapter adapter) {
        super(context);
    }

    @Override
    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {
        try {
            //不能使用   View view = recycler.getViewForPosition(0);
            //measureChild(view, widthSpec, heightSpec);
            // int measuredHeight  view.getMeasuredHeight();  这个高度不准确
            if(adapter!=null&&adapter.getItemHeight()>0) {
                int measuredWidth = View.MeasureSpec.getSize(widthSpec);
                int line = adapter.getItemCount();
//                if (adapter.getItemCount()  > 0) {
                    int measuredHeight = adapter.getItemHeight()*2 ;
//                    int measuredHeight = adapter.getItemHeight()*(adapter.getItemCount()) ;
                    setMeasuredDimension(measuredWidth, measuredHeight);
//                }
            }else{
                super.onMeasure(recycler,state,widthSpec,heightSpec);
            }
        }catch (Exception e){
            super.onMeasure(recycler,state,widthSpec,heightSpec);
        }
    }

}
