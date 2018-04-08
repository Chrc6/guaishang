package com.houwei.guaishang.views;

import android.content.Context;
import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

/**
 * 显示原型图片的ImageLoader使用的显示器
 *
 */
public class CircleBitmapDisplayer1 implements BitmapDisplayer {

    protected  final int margin ;
Context context;
    public CircleBitmapDisplayer1(Context context) {
        this(0);
        this.context=context;
    }

    public CircleBitmapDisplayer1(int margin) {
        this.margin = margin;
    }

    @Override
    public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {
        if (!(imageAware instanceof ImageViewAware)) {
            throw new IllegalArgumentException("ImageAware should wrap ImageView. ImageViewAware is expected.");
        }

        imageAware.setImageDrawable(new CircleDrawable1(bitmap, margin,context));
    }


}
