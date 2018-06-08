package com.houwei.guaishang.widget.holder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import com.facebook.drawee.view.SimpleDraweeView;
import com.houwei.guaishang.R;
import com.houwei.guaishang.base.holder.BaseHolder;
import com.houwei.guaishang.util.InflateService;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lei on 2018/6/9.
 */

public class FloatFiveHolder extends BaseHolder<List<String>> {


    @BindView(R.id.float_one)
    SimpleDraweeView floatOne;
    @BindView(R.id.float_two)
    SimpleDraweeView floatTwo;
    @BindView(R.id.float_three)
    SimpleDraweeView floatThree;
    @BindView(R.id.float_four)
    SimpleDraweeView floatFour;
    @BindView(R.id.float_five)
    SimpleDraweeView floatFive;

    public FloatFiveHolder(Context context) {
        super(context);
    }

    @NonNull
    @Override
    protected View initView(Context context) {
        mRootView = InflateService.g().inflate(R.layout.float_five);
        ButterKnife.bind(this, mRootView);
        return mRootView;
    }

    @Override
    protected void updateUI(Context context, List<String> data) {
        floatOne.setImageURI(data.get(0));
        floatTwo.setImageURI(data.get(1));
        floatThree.setImageURI(data.get(2));
        floatFour.setImageURI(data.get(3));
        floatFive.setImageURI(data.get(4));
//        ImageLoader.getInstance().displayImage(data.get(0), floatOne);
//        ImageLoader.getInstance().displayImage(data.get(1), floatTwo);
//        ImageLoader.getInstance().displayImage(data.get(2), floatThree);
//        ImageLoader.getInstance().displayImage(data.get(3), floatFour);
//        ImageLoader.getInstance().displayImage(data.get(4), floatFive);
    }
}
