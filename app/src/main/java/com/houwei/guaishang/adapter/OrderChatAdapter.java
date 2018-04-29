package com.houwei.guaishang.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.houwei.guaishang.R;
import com.houwei.guaishang.bean.OffersBean;
import com.houwei.guaishang.bean.UserBean;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Collections;
import java.util.List;

/**
 * Created by *** on 2018/4/21.
 */

public class OrderChatAdapter extends RecyclerView.Adapter{

    private final int HEAD_TYPE = 1;
    private final int CONTENT_TYPE = 2;

    private  List<OffersBean.OfferBean> list;
    private AdapterItemClickListener listener;

    private int focusPosition = 1;

    public OrderChatAdapter(List list) {
        this.list = list;
    }

    public void setItemOnclickListener(AdapterItemClickListener listener) {
        this.listener = listener;
    }

    private void setFocusItemPosition(int position) {
        this.focusPosition = position;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return HEAD_TYPE;
        }
        return CONTENT_TYPE;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        if (viewType == HEAD_TYPE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_chat_head_layout,parent,false);
            holder = new HeadViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_chat_layout,parent,false);
            holder = new ViewHolder(view);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (getItemViewType(position) == HEAD_TYPE) {
            HeadViewHolder viewHolder = (HeadViewHolder) holder;
            int count = 0;
            if (list != null) {
                count = list.size();
            }
            viewHolder.nameTv.setText("抢单人/"+count+"人");
        } else {
            ViewHolder viewHolder = (ViewHolder) holder;
            final OffersBean.OfferBean offerBean = list.get(position - 1);
            boolean notify = offerBean.isNotify();
            ImageLoader.getInstance().displayImage(offerBean.getAvatar(), viewHolder.headIv);
            viewHolder.view_rot.setVisibility(notify ? View.VISIBLE : View.GONE);
            viewHolder.nameTv.setText(offerBean.getName());
            if (listener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onItemClick(position - 1, offerBean.getUserid());
                        setFocusItemPosition(position);
                    }
                });
            }
            if (focusPosition == position) {
                holder.itemView.setBackground(holder.itemView.getContext().getResources().getDrawable(R.drawable.order_chat_item_click));
            } else {
                if (position == getItemCount() - 1) {
                    holder.itemView.setBackground(holder.itemView.getContext().getResources().getDrawable(R.drawable.order_chat_last_item_normal));
                } else {
                    holder.itemView.setBackground(holder.itemView.getContext().getResources().getDrawable(R.drawable.order_chat_item_normal));
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        if (list != null && list.size() > 0) {
            return list.size() + 1;
        }
        return 1;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView headIv;
        public TextView nameTv;
        public View view_rot;

        public ViewHolder(View view) {
            super(view);
            headIv = (ImageView) view.findViewById(R.id.iv_head);
            nameTv = (TextView) view.findViewById(R.id.tv_name);
            view_rot = view.findViewById(R.id.view_rot);
        }
    }

    class HeadViewHolder extends RecyclerView.ViewHolder{
        public TextView nameTv;

        public HeadViewHolder(View view) {
            super(view);
            nameTv = (TextView) view.findViewById(R.id.tv_name);
        }
    }

    public interface AdapterItemClickListener {
        void onItemClick(int postion, String userId);
    }
}
