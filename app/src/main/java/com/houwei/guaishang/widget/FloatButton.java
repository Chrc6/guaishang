package com.houwei.guaishang.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;
import com.houwei.guaishang.R;
import com.houwei.guaishang.activity.BaseActivity;
import com.houwei.guaishang.view.CircleImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2018/4/21.
 */

public class FloatButton extends RelativeLayout {

    private static final int  Galb = 1;//抢单状态
    private static final int Galb_self = 2;//自己发的单状态
    private static final int  Finish = 3;//结束状态

    private BaseActivity context;
    private List<String> mAvatarList;//头像列表
    private int status;

    private TextView galb,brief;
    private RelativeLayout galb_layout;
    private RelativeLayout galb_self;
    private FlexboxLayout iconLayout;
    private RelativeLayout rootView;
    public FloatButton(Context context) {
        super(context);
        init(context);
    }

    public FloatButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }


    private void initView(Context context){
        View view = LayoutInflater.from(context).inflate(R.layout.layout_float,null);
        rootView = (RelativeLayout) view.findViewById(R.id.root_view);
        galb = (TextView) view.findViewById(R.id.galb);
        brief = (TextView) view.findViewById(R.id.brief);
        galb_layout = (RelativeLayout) view.findViewById(R.id.galb_layout);
        galb_self = (RelativeLayout) view.findViewById(R.id.galb_self);
        iconLayout = (FlexboxLayout) view.findViewById(R.id.icon_layout);
        rootView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (floatBtnClickListener != null){
                    switch (status){
                        case Galb:
                            floatBtnClickListener.galb();
                            break;
                        case Galb_self:
                            floatBtnClickListener.goChatView();
                            break;
                        case Finish:
                            floatBtnClickListener.doNothing();
                            break;
                    }
                }
            }
        });
        addView(view);


    }

    private void init(Context context){
        if (context instanceof BaseActivity){
            this.context = (BaseActivity) context;
        }
        if (mAvatarList == null){
            mAvatarList = new ArrayList<>();
        }
        initView(context);
    }

    public void setStatu(int statu){
        this.status = statu;
        if (statu == Galb){
            galb_self.setVisibility(GONE);
            brief.setVisibility(GONE);
            galb_layout.setVisibility(VISIBLE);
            galb.setText("抢单");
            rootView.setBackground(context.getResources().getDrawable(R.drawable.bg_float_btn_red));
        }else if (statu == Finish){
            galb_self.setVisibility(GONE);
            galb_layout.setVisibility(VISIBLE);
            galb.setText("已结束");
            rootView.setBackground(context.getResources().getDrawable(R.drawable.bg_float_btn_gral));
        }else if (statu == Galb_self){
            galb_self.setVisibility(VISIBLE);
            galb_layout.setVisibility(GONE);
            rootView.setBackground(context.getResources().getDrawable(R.drawable.bg_float_btn_white));
        }
    }

    public void setBrief(String content){
        if ( null != brief){
            brief.setText(content);
        }
    }

    public void setmAvatarList(ArrayList<String> list){
        if (mAvatarList != null){
            mAvatarList.clear();
            mAvatarList.addAll(list);
        }
        notifyAvatarRefresh();
    }

    private void notifyAvatarRefresh(){
        iconLayout.setFlexDirection(FlexboxLayout.SCROLL_AXIS_HORIZONTAL);
        iconLayout.setFlexWrap(FlexboxLayout.FLEX_WRAP_WRAP);
        iconLayout.setJustifyContent(FlexboxLayout.ALIGN_ITEMS_CENTER);
        int size = mAvatarList.size();
        if (size == 1){
            CircleImageView imageView = new CircleImageView(context);
            FlexboxLayout.LayoutParams layoutParams = new FlexboxLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            imageView.setLayoutParams(layoutParams);
            ImageLoader.getInstance().displayImage(mAvatarList.get(0), imageView);
            iconLayout.addView(imageView);
        }else {
            for (int i = 0; i < size; i++) {
                if (i > 7) {
                    break;
                }
                CircleImageView imageView = new CircleImageView(context);
                FlexboxLayout.LayoutParams layoutParams = new FlexboxLayout.LayoutParams(dip2px(20), dip2px(20));
                if (i == 2 || i == 5) {
                    layoutParams.wrapBefore = true;
                } else {
                    layoutParams.wrapBefore = false;
                }
                imageView.setLayoutParams(layoutParams);
                ImageLoader.getInstance().displayImage(mAvatarList.get(i), imageView);
                iconLayout.addView(imageView);
            }
        }
    }


    private int dip2px( float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private FloatBtnClickListener floatBtnClickListener;

    public void setFloatBtnClickListener(FloatBtnClickListener floatBtnClickListener) {
        this.floatBtnClickListener = floatBtnClickListener;
    }

    public interface FloatBtnClickListener{
        void galb();
        void goChatView();
        void doNothing();
    }
}
