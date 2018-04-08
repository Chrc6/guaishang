package com.houwei.guaishang.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ProgressBar;

/**
 * Created by Administrator on 2017/10/11.
 */

public class CusProgressBar extends ProgressBar {
    public CusProgressBar(Context context) {
        this(context,null);
    }

    public CusProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CusProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
