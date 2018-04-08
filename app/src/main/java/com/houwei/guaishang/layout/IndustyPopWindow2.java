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

public class IndustyPopWindow2 extends PopupWindow {
    private final View conentView;
    private final float sWidth;
    private Activity context;
    private PopInter inter;

    private TagFlowLayout flowlayout;
    private List<IndustryBean.ItemsBean> itemsBeens;
    private Set<String> hashSet=new HashSet<>();
    private String param="";
    private String paramIdS="";
    private Set<Integer> chooseSets;

    public IndustyPopWindow2(Activity context, List<IndustryBean.ItemsBean> itemsBeens, PopInter inter ) {
        super(context);
        this.context = context;
        this.inter= inter;
        this.itemsBeens=itemsBeens;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        conentView = inflater.inflate(R.layout.pop_industry2, null);
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
       TagAdapter tagAdapter= new TagAdapter<IndustryBean.ItemsBean>(itemsBeens){
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
                for(int i:selectPosSet){
                    hashSet.add(itemsBeens.get(i).getId());
                }
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

            List<Integer> ids = new ArrayList<>();
            for(Integer i : sets){
                ids.add(i);
            }
            for(int i=0; i<ids.size();i++){
                param=param+itemsBeens.get(i).getId()+",";
            }
            /*String s = "";
            for(String str : hashSet){
                s = s + str + ",";
            }
            Log.d("WXCH","s:"+s);*/

            /*for(Integer str:sets){
                param=param+str+",";
            }*/
            if(param.endsWith(",")){
                param=param.substring(0,param.length()-1);
            }
            Log.d("WXCH","para:"+param);
            inter.commit(param);

        }

    }
}
