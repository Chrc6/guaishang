package com.houwei.guaishang.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;
import com.houwei.guaishang.R;
import com.houwei.guaishang.activity.BaseActivity;
import com.houwei.guaishang.sp.UserUtil;
import com.houwei.guaishang.util.InflateService;
import com.houwei.guaishang.util.LoginJumperUtil;
import com.houwei.guaishang.view.CircleImageView;
import com.houwei.guaishang.widget.holder.FloatFiveHolder;
import com.houwei.guaishang.widget.holder.FloatFourHolder;
import com.houwei.guaishang.widget.holder.FloatOneHolder;
import com.houwei.guaishang.widget.holder.FloatThreeHolder;
import com.houwei.guaishang.widget.holder.FloatTwoHolder;
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
    private static final int Has_Galb = 4;//已抢
    private static final int Wait_Galb = 5;//等待抢单

    private Context context;
    private List<String> mAvatarList;//头像列表
    private int status;

    private TextView galb,brief;
    private RelativeLayout galb_layout;
    private RelativeLayout galb_self;
    private RelativeLayout iconLayout;
    private RelativeLayout rootView;
    public FloatButton(Context context) {
        super(context);
        init(context);
    }

    public FloatButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }


    private void initView(final Context context){
        View view = LayoutInflater.from(context).inflate(R.layout.layout_float,null);
        rootView = (RelativeLayout) view.findViewById(R.id.root_view);
        galb = (TextView) view.findViewById(R.id.galb);
        brief = (TextView) view.findViewById(R.id.brief);
        galb_layout = (RelativeLayout) view.findViewById(R.id.galb_layout);
        galb_self = (RelativeLayout) view.findViewById(R.id.galb_self);
        iconLayout = (RelativeLayout) view.findViewById(R.id.icon_layout);
        rootView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!UserUtil.isInLoginStata()){
                    LoginJumperUtil.jumperLogin(context);
                    return;
                }
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
                        case Has_Galb:
                            floatBtnClickListener.chatAlone();
                            break;
                        case Wait_Galb:
                            floatBtnClickListener.doNothing();
                            break;
                    }
                }
            }
        });
        addView(view);


    }

    private void init(Context context){
       this.context = context;
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
            rootView.setBackground(context.getResources().getDrawable(R.drawable.bg_float_btn_finish));
        }else if (statu == Galb_self){
            galb_self.setVisibility(VISIBLE);
            galb_layout.setVisibility(GONE);
            rootView.setBackground(context.getResources().getDrawable(R.drawable.bg_float_btn_white));
        }else if (statu == Has_Galb){
            galb_self.setVisibility(VISIBLE);
            galb_layout.setVisibility(GONE);
            rootView.setBackground(context.getResources().getDrawable(R.drawable.bg_float_btn_white));
            setSelf();
        }else if (statu == Wait_Galb){
            galb_self.setVisibility(GONE);
            brief.setVisibility(GONE);
            galb_layout.setVisibility(VISIBLE);
            galb.setText("等待抢单");
            rootView.setBackground(context.getResources().getDrawable(R.drawable.bg_float_btn_red));
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
        mAvatarList.clear();
        for (int i = 0; i < 1; i++) {
            mAvatarList.add("http://www.guaishangfaming.com//media/topic/photo/2018-06-02/878fd1ecf1d0e4b03304d466b514390e.jpg");
        }
            notifyAvatarRefresh();
    }
    private void setSelf(){
        ArrayList<String> list = new ArrayList<>();
        if (UserUtil.isInLoginStata()){
            String avatar = UserUtil.getUserInfo().getAvatar();
            list.add(avatar);
        }
        setmAvatarList(list);
    }

    private void notifyAvatarRefresh(){
        iconLayout.removeAllViews();

        int size = mAvatarList.size();
        if (size == 1){
            FloatOneHolder oneHolder = new FloatOneHolder(context);
            oneHolder.setData(mAvatarList);
            iconLayout.addView(oneHolder.getRootView());
        }else if (size == 2){
            FloatTwoHolder twoHolder = new FloatTwoHolder(context);
            twoHolder.setData(mAvatarList);
            iconLayout.addView(twoHolder.getRootView());
        }else if (size == 3){
            FloatThreeHolder threeHolder = new FloatThreeHolder(context);
            threeHolder.setData(mAvatarList);
            iconLayout.addView(threeHolder.getRootView());
        }else if (size == 4){
            FloatFourHolder fourHolder = new FloatFourHolder(context);
            fourHolder.setData(mAvatarList);
            iconLayout.addView(fourHolder.getRootView());
        }else if (size >= 5){
//            LayoutInflater from = LayoutInflater.from(context);
//            View inflate = LayoutInflater.from(context).inflate(R.layout.float_five,null);
//            ImageView one = (ImageView) inflate.findViewById(R.id.float_one);
//            ImageView two = (ImageView) inflate.findViewById(R.id.float_two);
//            ImageView three = (ImageView) inflate.findViewById(R.id.float_three);
//            ImageView four = (ImageView) inflate.findViewById(R.id.float_four);
//            ImageView five = (ImageView) inflate.findViewById(R.id.float_five);
//            ImageLoader.getInstance().displayImage(mAvatarList.get(0), one);
//            ImageLoader.getInstance().displayImage(mAvatarList.get(1), two);
//            ImageLoader.getInstance().displayImage(mAvatarList.get(2), three);
//            ImageLoader.getInstance().displayImage(mAvatarList.get(3), four);
//            ImageLoader.getInstance().displayImage(mAvatarList.get(4), five);
            FloatFiveHolder fiveHolder = new FloatFiveHolder(context);
            fiveHolder.setData(mAvatarList);
            iconLayout.addView(fiveHolder.getRootView());
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
        void chatAlone();
    }
}
