/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.houwei.guaishang.widget;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.widget.RatingBar;


public class MaterialRatingBar extends RatingBar {

    private static final String TAG = MaterialRatingBar.class.getSimpleName();

    private MaterialRatingDrawable mDrawable;

    public MaterialRatingBar(Context context) {
        super(context);

        init(null, 0);
    }

    public MaterialRatingBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(attrs, 0);
    }

    public MaterialRatingBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyleAttr) {
        mDrawable = new MaterialRatingDrawable(getContext());
        mDrawable.setStarCount(getNumStars());
        setProgressDrawable(mDrawable);
    }

    @Override
    public void setNumStars(int numStars) {
        super.setNumStars(numStars);

        // mDrawable can be null during super class initialization.
        if (mDrawable != null) {
            mDrawable.setStarCount(numStars);
        }
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int height = getMeasuredHeight();
        int width = Math.round(height * mDrawable.getTileRatio() * getNumStars());
        setMeasuredDimension(ViewCompat.resolveSizeAndState(width, widthMeasureSpec, 0), height);
    }

}
