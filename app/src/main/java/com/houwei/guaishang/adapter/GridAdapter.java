package com.houwei.guaishang.adapter;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.houwei.guaishang.R;
import com.houwei.guaishang.bean.AvatarBean;
import com.houwei.guaishang.layout.ListBaseAdapter;
import com.houwei.guaishang.layout.SuperViewHolder;
import com.houwei.guaishang.tools.HttpUtil;
import com.houwei.guaishang.tools.LogUtil;

/**
 * Created by Administrator on 2017/10/14.
 */

public class GridAdapter extends ListBaseAdapter<AvatarBean>{
    Context context;
    public GridAdapter(Context context) {
        super(context);
        this.context=context;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_grid;
    }

    @Override
    public void onBindItemHolder(SuperViewHolder holder, int position) {
        AvatarBean bean = getDataList().get(position);
        ImageView imageView=holder.getView(R.id.image);
        Glide.with(context).load(HttpUtil.IP_NOAPI+bean.getOriginal()).into(imageView);
    }
}
