package com.houwei.guaishang.view;

import android.app.Activity;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.houwei.guaishang.R;
import com.lzy.imagepicker.loader.ImageLoader;


import java.io.File;

/**
 * Created by Administrator on 2017/6/8.
 */

public class MyImageLoader implements ImageLoader {
    @Override
    public void displayImage(Activity activity, String path, ImageView imageView, int width, int height) {
        Glide.with(activity)
                .load(Uri.fromFile(new File(path)))
                .into(imageView);
    }

    @Override
    public void clearMemoryCache() {

    }


}
