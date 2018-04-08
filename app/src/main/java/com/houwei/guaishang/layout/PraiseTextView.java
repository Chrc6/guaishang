package com.houwei.guaishang.layout;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.houwei.guaishang.R;
import com.houwei.guaishang.activity.BaseActivity;
import com.houwei.guaishang.bean.TopicBean;
import com.houwei.guaishang.tools.HttpUtil;
import com.houwei.guaishang.tools.ToastUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;


/**
 * 赞textview 包含一个大拇指和赞数字
 */
public class PraiseTextView extends TextView {

    private boolean isAnimation;

    public PraiseTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub

    }

    /**
     * activity 设置点击事件，直接调用该方法就行，已经封装好了
     * <p>
     * 处理的比较好的 点赞取消赞效果（微信微博） 是不弹出dialog的但是带动画，但是点赞需要通知服务器同步
     * <p>
     * 我们的处理方法是：
     * 先立刻调用动画效果，然后本地直接模拟一个网络成功返回数据，再用观察者模式刷新所有activity包含该动态的点赞状态
     * 然后发出网络请求。如果网络请求成功，什么都不做
     * 如果网络失败，模拟一个之前的数据，并拼接上服务器返回的错误message，再用观察者模式刷新所有activity包含该动态的点赞状态
     *
     * @param bean
     * @param uid
     */
    public void clickPraise(final BaseActivity mContext, final TopicBean bean) {
        if (isAnimation) {//正在动画中，防止快速连点
            return;
        }
        if (!mContext.checkLogined()) {//用户未登录，自动跳转登录界面
            return;
        }
        //立刻执行放大的点赞动画
        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.praise_scale);
        animation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation arg0) {
                // TODO Auto-generated method stub
                isAnimation = true;
                //改方法会先模拟一个网络成功的数据刷新界面，然后再访问网络请求
                mContext.getITopicApplication().getHomeManager().startPraiseTopic(bean, mContext.getUserID());
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                // TODO Auto-generated method stub
                isAnimation = false;

            }
        });
        this.startAnimation(animation);
    }

    public void setPraiseState(BaseActivity mContext, TopicBean bean) {
        this.setText("" + bean.getPraiseCount());
        this.setTextColor(bean.getZanColor());
        Drawable drawable = mContext.getResources().getDrawable(bean.isPraised() ? R.drawable.l_zang_true : R.drawable.l_zang_false);
        drawable.setBounds(0, 0,drawable.getMinimumWidth(),
                drawable.getMinimumHeight());
        setCompoundDrawablePadding(0);
        this.setCompoundDrawables(null, drawable, null, null);

    }
}
