package com.houwei.guaishang.layout;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.houwei.guaishang.R;
import com.houwei.guaishang.bean.IndustryBean;
import com.houwei.guaishang.tools.SPUtils;
import com.houwei.guaishang.tools.ScreenUtils;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2017/10/19.
 */

public class IndustyPopWindow extends PopupWindow {
    private final View conentView;
    private final float sWidth;
    private Activity context;
    private PopInter inter;

    private TagFlowLayout flowlayout;
    private List<IndustryBean.ItemsBean> itemsBeans;
    private Set<String> hashSet=new HashSet<>();
    private Set<Integer> chooseSets;
    private String user_id;

    public IndustyPopWindow(Activity context, List<IndustryBean.ItemsBean> itemsBeans, PopInter inter, String user_id ) {
        super(context);
        this.context = context;
        this.inter= inter;
        this.itemsBeans=itemsBeans;
        this.user_id = user_id;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        conentView = inflater.inflate(R.layout.pop_industry, null);
        flowlayout= (TagFlowLayout) conentView.findViewById(R.id.id_flowlayout);
        int h = context.getWindowManager().getDefaultDisplay().getHeight();
        int w = context.getWindowManager().getDefaultDisplay().getWidth();
        // 设置SelectPicPopupWindow的View
        this.setContentView(conentView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(w);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        // 刷新状态
        this.update();
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0000000000);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(dw);
        int width=ScreenUtils.getScreenWidth(context);
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        sWidth=width;

        chooseSets= SPUtils.getSet(context,"sets",null);
        initFlowLayout();
    }

    private void initFlowLayout() {
        TagAdapter tagAdapter= new TagAdapter<IndustryBean.ItemsBean>(itemsBeans){
            @Override
            public View getView(FlowLayout parent, int position, IndustryBean.ItemsBean itemsBean) {
                TextView textView = (TextView) LayoutInflater.from(context)
                        .inflate(R.layout.tv, flowlayout, false);
                textView.setText(itemsBean.getBrandName());
                int pw=parent.getWidth();
                ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) textView.getLayoutParams();
                params.width = (int)(sWidth/4.8);
//                Log.d("CCC","w:"+(sWidth/4));
//                params.height = ;
                // params.setMargins(dip2px(MainActivity.this, 1), 0, 0, 0); // 可以实现设置位置信息，如居左距离，其它类推
                // params.leftMargin = dip2px(MainActivity.this, 1);
                textView.setLayoutParams(params);
                return textView;
            }
        };

        if(chooseSets!=null&&!chooseSets.isEmpty()){
            tagAdapter.setSelectedList(chooseSets);
        }
        flowlayout.setAdapter(tagAdapter);
        flowlayout.setOnSelectListener(new TagFlowLayout.OnSelectListener() {
            @Override
            public void onSelected(Set<Integer> selectPosSet) {
                hashSet.clear();
                for(int i : selectPosSet){
                    //Log.i("WXCH","itemsBeans Id:"+itemsBeans.get(i).getId()+",BrandName:"+itemsBeans.get(i).getBrandName());
                    hashSet.add(itemsBeans.get(i).getId());
                }
                //for(String i : hashSet){
                    //Log.i("WXCH","onSelected Id:" + i);
               // }
            }
        });
    }

    public void showPopupWindow(View parent) {
        if (!this.isShowing()) {
            this.showAsDropDown(parent, parent.getLayoutParams().width / 2, 18);
        } else {
            this.dismiss();
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if(flowlayout!=null){
            Set<Integer> sets  = flowlayout.getSelectedList();
            SPUtils.putSet(context,"sets",sets);

            String param = "";
            for(String i : hashSet){
                param = param+i+",";
            }
            Log.d("WXCH","param:"+param);
            inter.commit(param);

        }

    }
}
