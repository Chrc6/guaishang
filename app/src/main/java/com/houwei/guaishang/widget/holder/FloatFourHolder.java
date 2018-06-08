package com.houwei.guaishang.widget.holder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;

import com.facebook.drawee.view.SimpleDraweeView;
import com.houwei.guaishang.R;
import com.houwei.guaishang.base.holder.BaseHolder;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lei on 2018/6/9.
 */

public class FloatFourHolder extends BaseHolder<List<String>> {

    @BindView(R.id.float_two)
    SimpleDraweeView floatTwo;
    @BindView(R.id.float_three)
    SimpleDraweeView floatThree;
    @BindView(R.id.float_four)
    SimpleDraweeView floatFour;
    @BindView(R.id.float_five)
    SimpleDraweeView floatFive;

    public FloatFourHolder(Context context) {
        super(context);
    }

    @NonNull
    @Override
    protected View initView(Context context) {
        mRootView = LayoutInflater.from(context).inflate(R.layout.float_four, null);
        ButterKnife.bind(this, mRootView);
        return mRootView;
    }

    @Override
    protected void updateUI(Context context, List<String> data) {
        floatTwo.setImageURI(data.get(0));
        floatThree.setImageURI(data.get(1));
        floatFour.setImageURI(data.get(2));
        floatFive.setImageURI(data.get(3));
    }
}
