package com.houwei.guaishang.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.houwei.guaishang.R;
import com.houwei.guaishang.base.holder.ViewHolder;
import com.houwei.guaishang.bean.AvatarBean;
import com.houwei.guaishang.layout.ListBaseAdapter;
import com.houwei.guaishang.layout.SuperViewHolder;
import com.houwei.guaishang.preview.PreviewActivity;
import com.houwei.guaishang.tools.HttpUtil;
import com.houwei.guaishang.tools.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/10/14.
 */

public class HomeOrderGridAdapter extends BaseAdapter implements View.OnClickListener {

    Context mContext;
    List<AvatarBean> mDatas;

    float itemWidth;
    float itemHeight;

    public HomeOrderGridAdapter(Context context, List<AvatarBean> datas) {
        this.mContext=context;
        this.mDatas = datas;
        calculateWidthAndHeight();
    }

    private void calculateWidthAndHeight() {
        int screenWidth = Utils.getScreenWidth(mContext);
        //187是gridview的总高度
        float totalHeight = Utils.dip2px(mContext, 187);
        //16: 是父控件(viewpager)左右padding之和16dp
        float leftAndRightPadding = Utils.dip2px(mContext, 16);

        int column = getCount() >= 3 ? 3 : getCount();
        int lines = getCount() / 3 + (getCount() % 3 > 0 ? 1 : 0);
        lines = lines > 3 ? 3 : lines;

        float horizontalSpacing = (column - 1) * Utils.dip2px(mContext, 10);
        float verticalSpacing = (lines - 1) * Utils.dip2px(mContext, 4);

        itemWidth = (screenWidth - leftAndRightPadding - horizontalSpacing) / column;
        itemHeight = (totalHeight - verticalSpacing) / lines;
    }

    @Override
    public int getCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    @Override
    public AvatarBean getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        GridViewholder viewHolder = null;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.home_order_grid_item, null);
            viewHolder = new GridViewholder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (GridViewholder) convertView.getTag();
        }
        viewHolder.imageView.setOnClickListener(this);
        viewHolder.setData(mDatas.get(position).getSmall());

        return convertView;
    }

    @Override
    public void onClick(View v) {
        ArrayList<String> list = new ArrayList<String>();
        if (mDatas != null) {
            int size = mDatas.size();
            for (int i = 0; i < size; i++) {
                list.add(HttpUtil.IP_NOAPI + mDatas.get(i).getOriginal());
            }
        }

        Intent intent = new Intent(mContext, PreviewActivity.class);
        intent.putExtra("list",list);
        mContext.startActivity(intent);
    }

    private class GridViewholder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        private Context context;

        public GridViewholder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            imageView = (ImageView) itemView.findViewById(R.id.image);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) imageView.getLayoutParams();
            params.width = (int) itemWidth;
            params.height = (int) itemHeight;
            imageView.setLayoutParams(params);
        }

        public void setData(String url) {
            Glide.with(context).load(HttpUtil.IP_NOAPI + url).into(imageView);
        }
    }
}
