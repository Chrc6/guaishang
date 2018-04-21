package com.houwei.guaishang.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.houwei.guaishang.R;
import com.houwei.guaishang.activity.newui.MyInfoActivity;
import com.houwei.guaishang.inter.DeleteInter;
import com.houwei.guaishang.layout.ListBaseAdapter;
import com.houwei.guaishang.layout.SuperViewHolder;
import com.luck.picture.lib.entity.LocalMedia;

/**
 * Created by Administrator on 2017/10/14.
 */

public class GridMeAdapter extends ListBaseAdapter<LocalMedia>{
    Context context;
    private DeleteInter inter;
    private boolean hideDelete;

    public GridMeAdapter(Context context,DeleteInter inter) {
        super(context);
        init(context,inter,false);
    }

    public GridMeAdapter(Context context,DeleteInter inter, boolean hideDelete) {
        super(context);
        init(context,inter,hideDelete);
    }

    private void init(Context context,DeleteInter inter, boolean hideDelete) {
        this.context=context;
        this.inter=inter;
        this.hideDelete = hideDelete;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_grid_me;
    }

    @Override
    public void onBindItemHolder(SuperViewHolder holder, final int position) {
        LocalMedia bean = getDataList().get(position);
        ImageView imageView=holder.getView(R.id.image);
        Button btnDelete=holder.getView(R.id.delete_btn);
        if(TextUtils.isEmpty(bean.getPath()) || hideDelete){
            Glide.with(context).load(R.drawable.picture_update_icon).into(imageView);
            btnDelete.setVisibility(View.GONE);
        }else{
            Glide.with(context).load(bean.getPath()).into(imageView);
            btnDelete.setVisibility(View.VISIBLE);
        }
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inter.delete(position);
            }
        });
    }
}
