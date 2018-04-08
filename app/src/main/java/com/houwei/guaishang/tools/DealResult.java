package com.houwei.guaishang.tools;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.houwei.guaishang.R;
import com.houwei.guaishang.activity.Constant;
import com.houwei.guaishang.bean.AttenttionBean;
import com.houwei.guaishang.bean.BaseBean;
import com.houwei.guaishang.bean.BaseResponse;
import com.lzy.okgo.model.Response;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/8/30.
 */

public class DealResult {

    private static DealResult instace=new DealResult();

    public static DealResult getInstace(){
        return  instace;
    }
    public DealResult() {

    }

    /**
     *解析数组json数据
     * @param context
     * @param response
     * @param type
     * @param <T>
     * @return
     */
    public  <T> List<T> dealDatas(Context context, Response<String> response, Type type){
        List<T> datas=new ArrayList<>();
        String res=response.body().toString().trim();
        if(TextUtils.isEmpty(res)){
            ToastUtils.toastForShort(context,context.getString(R.string.empty_datas));
            return datas;
        }
        try {
            BaseBean<T> bean=new Gson().fromJson(res,type);
            if(bean.getStatus()== 1){
                 datas.addAll(bean.getDatas());
                if(datas.isEmpty()){
                    ToastUtils.toastForShort(context,context.getString(R.string.empty_datas));
                }
                return datas;
            }else{
                ToastUtils.toastForShort(context,bean.getMsg());
                return datas;
            }
        }catch (Exception e){
            ToastUtils.toastForShort(context,context.getString(R.string.analysis_error));
            e.printStackTrace();
            return datas;
        }
    }

    /**
     *析json数据
     * @param context
     * @param response
     * @param type
     * @param <T>
     * @return
     */
    public  <T>T dealData(Context context, Response<String> response, Type type){
        T object=null;
        String res=response.body().toString().trim();
        if(TextUtils.isEmpty(res)){
            ToastUtils.toastForShort(context,context.getString(R.string.empty_datas));
            return object;
        }
        try {
            BaseBean<T> bean=new Gson().fromJson(res,type);
            if(bean.getCode()== Constant.SUCESS){
                object=bean.getData();
            }else{
                ToastUtils.toastForShort(context,bean.getMsg());
            }
            return object;
        }catch (Exception e){
            ToastUtils.toastForShort(context,context.getString(R.string.analysis_error));
            e.printStackTrace();
            return object;
        }
    }
    /**
     *析json数据
     * @param context
     * @param response
     * @param
     * @param <T>
     * @return
     */
    public  <T>T dealData(Context context, Response<String> response, Class<T> obj){
        T object=null;
        String res=response.body().toString().trim();
        if(TextUtils.isEmpty(res)){
            ToastUtils.toastForShort(context,context.getString(R.string.empty_datas));
            return object;
        }
        try {
            object=new Gson().fromJson(res,obj);
             return object;
        }catch (Exception e){
            ToastUtils.toastForShort(context,context.getString(R.string.analysis_error));
            e.printStackTrace();
            return object;
        }
    }
    /**
     *析json数据
     * @param context
     * @param response
     * @param
     * @param
     */
    public BaseResponse dealBase(Context context, Response<String> response){
        BaseResponse object=null;
        String res=response.body().toString().trim();
        if(TextUtils.isEmpty(res)){
            ToastUtils.toastForShort(context,context.getString(R.string.empty_datas));
            return object;
        }
        try {
            object=new Gson().fromJson(res,BaseResponse.class);
             return object;
        }catch (Exception e){
            ToastUtils.toastForShort(context,context.getString(R.string.analysis_error));
            e.printStackTrace();
            return object;
        }
    }
    /**
     *析json数据
     * @param context
     * @param response
     * @param
     * @param
     */
    public <T>T dealBean(Context context, Response<String> response, Class<T> obj){
        T object=null;
        String res=response.body().toString().trim();
        if(TextUtils.isEmpty(res)){
            ToastUtils.toastForShort(context,context.getString(R.string.empty_datas));
            return object;
        }
        try {
            object=new Gson().fromJson(res,obj);
             return object;
        }catch (Exception e){
            ToastUtils.toastForShort(context,context.getString(R.string.analysis_error));
            e.printStackTrace();
            return object;
        }
    }


}
