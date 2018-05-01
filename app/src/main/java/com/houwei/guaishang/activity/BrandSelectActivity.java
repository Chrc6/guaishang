package com.houwei.guaishang.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.houwei.guaishang.R;
import com.houwei.guaishang.bean.BaseBean;
import com.houwei.guaishang.bean.IndustryBean;
import com.houwei.guaishang.layout.PopInter;
import com.houwei.guaishang.tools.DealResult;
import com.houwei.guaishang.tools.HttpUtil;
import com.houwei.guaishang.tools.SPUtils;
import com.houwei.guaishang.tools.ScreenUtils;
import com.houwei.guaishang.video.Utils;
import com.houwei.guaishang.views.ViewPagerTabsView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by *** on 2018/4/6.
 */

public class BrandSelectActivity extends BaseActivity implements View.OnClickListener {

    public final static int SELECT_BRAND = 0x24;
    public final static String BRAND_PARAM = "brand_param";
    public final static String EXTRAN_BRAND = "extra_brand";

    private TagFlowLayout flowlayout;
    private EditText mBrandEt;

    private ArrayList<IndustryBean.ItemsBean> itemsBeans;
    private Set<String> hashSet=new HashSet<>();
    private Set<Integer> chooseSets;
    private String user_id;
    private float sWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.pop_industry);
        initView();
        initData();
    }

    private void initData() {
        this.user_id = getUserID();
        itemsBeans = new ArrayList<>();
        sWidth = ScreenUtils.getScreenWidth(this);
        chooseSets= SPUtils.getSet(this,"sets",null);
        getIndustryData();
    }


    protected void initView() {
        initProgressDialog();

        flowlayout= (TagFlowLayout) findViewById(R.id.id_flowlayout);
        mBrandEt = (EditText) findViewById(R.id.et_brand);

        findViewById(R.id.ll_container).setOnClickListener(this);
        findViewById(R.id.tv_confirm).setOnClickListener(this);

//        initFlowLayout();
    }

    private void initFlowLayout() {
        TagAdapter tagAdapter= new TagAdapter<IndustryBean.ItemsBean>(itemsBeans){
            @Override
            public View getView(FlowLayout parent, int position, IndustryBean.ItemsBean itemsBean) {
                TextView textView = (TextView) LayoutInflater.from(BrandSelectActivity.this)
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_confirm:
                if (mBrandEt != null && mBrandEt.getText() != null) {
                    String extraBrand = mBrandEt.getText().toString();
                    if (!"".equals(extraBrand) && !"null".equals(extraBrand)) {
                        commitNewBrand(extraBrand);
                    }
                }
                finish();
                break;
            case R.id.ll_container:
                finish();
                break;

        }
    }

    private void getIndustryData() {
        progress.show();
        String url = "";
        if(checkLogined()){
            //url = HttpUtil.IP+"topic/my_brand";
            url = HttpUtil.IP+"topic/brand";
            user_id = getUserID();
        }
        url = HttpUtil.IP+"topic/brand";
        //IndustryBean.ItemsBean itemsBean = new IndustryBean.ItemsBean();

        //List<IndustryBean.ItemsBean> itemsBeen = new ArrayList<>();


        OkGo.<String>post(url)
                //.params("user_id",userId)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        progress.dismiss();
                        DealResult dr=new DealResult();
                        IndustryBean bean=dr.dealData(BrandSelectActivity.this,response,new TypeToken<BaseBean<IndustryBean>>(){}.getType());
                        if(bean==null){
                            return;
                        }
                        final List<IndustryBean.ItemsBean> itemsBeans =bean.getItems();
                        if(itemsBeans.isEmpty()){
                            return;
                        }
                        //Log.i("WXCH","itemsBeen:"+itemsBeans);
                        for(int i=0; i<itemsBeans.size();i++){
                            IndustryBean.ItemsBean item = itemsBeans.get(i);
                            Log.i("WXCH","ItemsBean Id:"+item.getId()+",BrandName:"+item.getBrandName());
                        }
                        BrandSelectActivity.this.itemsBeans.addAll(itemsBeans);
                        initFlowLayout();
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        progress.dismiss();
                    }
                });
    }

    private void commitNewBrand(String extraBrand) {
        if (Utils.isNetworkConnected(this)) {
            OkGo.<String>post(HttpUtil.IP+"user/useraddbrand")
                    .tag(this)
                    .params("userid", getUserID())
                    .params("name", extraBrand)
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {
                            String res=response.body().toString().trim();
                            //Log.i("WXCH","SSSSSS:" + res);
                            if(res.contains("1")){

                            }
                        }

                        @Override
                        public void onError(Response<String> response) {
                            super.onError(response);
                        }
                    });
        }
    }

    @Override
    public void finish() {
        String param = "";
        if(flowlayout!=null){
            Set<Integer> sets  = flowlayout.getSelectedList();
            SPUtils.putSet(this,"sets",sets);

            for(String i : hashSet){
                param = param+i+",";
            }
            Log.d("WXCH","param:"+param);

        }

        Intent intent = new Intent();
        intent.putExtra(BRAND_PARAM,param);

        setResult(SELECT_BRAND,intent);
        super.finish();
    }
}
