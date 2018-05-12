package com.houwei.guaishang.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.houwei.guaishang.R;
import com.houwei.guaishang.activity.PayActivity;
import com.houwei.guaishang.bean.OffersBean;
import com.houwei.guaishang.bean.TopicBean;
import com.houwei.guaishang.layout.ListBaseAdapter;
import com.houwei.guaishang.layout.SuperViewHolder;
import com.houwei.guaishang.tools.HttpUtil;
import com.houwei.guaishang.view.CircleImageView;

import java.util.List;

/**
 * Created by Administrator on 2017/10/19.
 */

public class OfferAdapter extends ListBaseAdapter<OffersBean.OfferBean> {
    private Context context;
    private TopicBean topicBean;


    public OfferAdapter(Context context) {
        super(context);
        this.context=context;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_offer;
    }

    @Override
    public void onBindItemHolder(SuperViewHolder holder, int position) {
        final OffersBean.OfferBean bean = getDataList().get(position);
        CircleImageView imageAvatart=holder.getView(R.id.avator);
        final LinearLayout llContent=holder.getView(R.id.ll_content);
        llContent.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                itemHeight=llContent.getMeasuredHeight();
                return true;
            }
        });
        TextView tvName=holder.getView(R.id.header_name);
        TextView tvMessage=holder.getView(R.id.tv_message);
        TextView tvLocation=holder.getView(R.id.header_location);
        TextView tvLevel=holder.getView(R.id.tv_level);
        TextView tvTime=holder.getView(R.id.tv_time);
        TextView tvMoney=holder.getView(R.id.tv_money);
        TextView tvContent=holder.getView(R.id.content);
        Button btnOrder=holder.getView(R.id.order_btn);
        Glide.with(context).load(bean.getAvatar()).into(imageAvatart);
//        Log.d("CCC","-->"+HttpUtil.IP+bean.getAvatar());
        tvName.setText(bean.getName());
        tvLocation.setText(bean.getAddress());
        tvTime.setText(bean.getCycle());
        Log.i("WXCH","tvContent:"+bean.getBeizhu());
        tvMoney.setText(bean.getPrice()+"元");
        tvContent.setText(bean.getBeizhu());
        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(context, PayActivity.class);
                i.putExtra("orderTitle", topicBean.getContent());
                i.putExtra("cover", topicBean.getCover());
                i.putExtra("brand", topicBean.getBrand());
                i.putExtra("price",Float.valueOf(bean.getPrice()));
                i.putExtra("topicId", bean.getOrderId());
                i.putExtra("to_memberid", bean.getOfferId());
                i.putExtra("offer_id", bean.getId());
                i.putExtra("name", bean.getName());
                i.putExtra("bank", bean.getBank());
                i.putExtra("bankNum", bean.getBankNum());
                context.startActivity(i);
            }
        });

//获取聊天记录最后一句话
        EMConversation conversation= EMChatManager.getInstance().getConversation(bean.getOfferId());
//        Log.d("CCC","聊天;"+bean.getOfferId());
        List<EMMessage> messages = conversation.getAllMessages();
        if(messages==null||messages.isEmpty()){
            return;
        }
//        convertView = createChatRow(context, message, position);
        tvMessage.setText(bean.getName()+":"+messages.get(messages.size()-1).getBody().toString());
    }

    public void setTopicBean(TopicBean topicBean) {
        this.topicBean=topicBean;
    }
    private int itemHeight;
    public int getItemHeight() {
        return itemHeight;
    }
}
