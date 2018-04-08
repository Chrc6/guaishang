package com.houwei.guaishang.layout;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.flyco.dialog.widget.popup.base.BasePopup;
import com.houwei.guaishang.R;
import com.houwei.guaishang.bean.IndustryBean;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2017/10/19.
 */

public class IndustryPop extends BasePopup{
    Activity context;
    private TagFlowLayout flowlayout;
    private List<IndustryBean.ItemsBean> itemsBeens;
    private   Set<String> hashSet=new HashSet<>();
    private String param="";

    public IndustryPop(Activity context,List<IndustryBean.ItemsBean> itemsBeens) {
        super(context);
        this.context=context;
        this.itemsBeens=itemsBeens;
    }

    @Override
    public View onCreatePopupView() {
        View inflate = View.inflate(context, R.layout.pop_industry2, null);
        flowlayout= (TagFlowLayout) inflate.findViewById(R.id.id_flowlayout);
        return inflate;
    }

    @Override
    public void setUiBeforShow() {
        flowlayout.setAdapter(new TagAdapter<IndustryBean.ItemsBean>(itemsBeens){
            @Override
            public View getView(FlowLayout parent, int position, IndustryBean.ItemsBean itemsBean) {
                TextView textView = (TextView) LayoutInflater.from(context)
                        .inflate(R.layout.tv, flowlayout, false);
                textView.setText(itemsBean.getBrandName());
                return textView;
            }
        });
        flowlayout.setOnSelectListener(new TagFlowLayout.OnSelectListener() {
            @Override
            public void onSelected(Set<Integer> selectPosSet) {
                for(int i:selectPosSet){
                    hashSet.add(itemsBeens.get(i).getId());
                }
            }
        });
       /* for(String str:hashSet){
            param=param+str+",";
        }*/
    }

}
