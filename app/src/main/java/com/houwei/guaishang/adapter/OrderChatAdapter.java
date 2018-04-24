package com.houwei.guaishang.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.houwei.guaishang.R;
import com.houwei.guaishang.bean.UserBean;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Collections;
import java.util.List;

/**
 * Created by *** on 2018/4/21.
 */

public class OrderChatAdapter extends RecyclerView.Adapter<OrderChatAdapter.ViewHolder>{

    private List<UserBean> list;
    private AdapterItemClickListener listener;

    public OrderChatAdapter(List list) {
        this.list = list;
    }

    public void setItemOnclickListener(AdapterItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_chat_layout,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final UserBean userBean = list.get(position);
        ImageLoader.getInstance().displayImage(userBean.getAvatar().findOriginalUrl(), holder.headIv);
        holder.nameTv.setText(userBean.getName());
        if (listener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(position, userBean.getUserid());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (list != null && list.size() > 0) {
            return list.size();
        }
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView headIv;
        public TextView nameTv;

        public ViewHolder(View view) {
            super(view);
            headIv = (ImageView) view.findViewById(R.id.iv_head);
            nameTv = (TextView) view.findViewById(R.id.tv_name);
        }
    }

    public interface AdapterItemClickListener {
        void onItemClick(int postion, String userId);
    }
}
