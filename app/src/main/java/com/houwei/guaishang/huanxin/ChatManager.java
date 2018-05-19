package com.houwei.guaishang.huanxin;

import android.content.Context;
import android.content.Intent;

import com.houwei.guaishang.activity.PayActivity;
import com.houwei.guaishang.bean.BaseResponse;
import com.houwei.guaishang.bean.LocationBean;
import com.houwei.guaishang.bean.event.TopicHomeEvent;
import com.houwei.guaishang.data.Contants;
import com.houwei.guaishang.huanxin.order.OrderInfoResponse;
import com.houwei.guaishang.sp.UserUtil;
import com.houwei.guaishang.tools.DealResult;
import com.houwei.guaishang.tools.HttpUtil;
import com.houwei.guaishang.tools.SPUtils;
import com.houwei.guaishang.tools.ToastUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by ${lei} on 2018/5/14.
 */

public class ChatManager {
    private Context context;

    public ChatManager(Context context) {
        this.context = context;
    }

    /**
     *
     * @param cid 抢单方id
     * @param sid 发单方id
     * @param orderid 订单id
     */
    private OrderInfoResponse orderInfoResponse;

    public OrderInfoResponse queryOffer(String cid, String sid, final String orderid, final ReqCallBack callBack) {

        OkGo.<String>get(HttpUtil.IP+"topic/queryoffer")
                .params("topicid",orderid)
                .params("user_id",cid)
//					.params("payMoney",payMoney)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        orderInfoResponse = null;
                        orderInfoResponse = DealResult.getInstace().dealBean(context, response, OrderInfoResponse.class);
                        if (callBack != null){
                            callBack.call(orderInfoResponse);
                        }
                    }
                });

        return orderInfoResponse;
    }

    //报价
    public void offer(LocationBean currentLocationBean,String price,String circle,String cid,String sid,String orderid) {
        String city;
        if (currentLocationBean == null) {
            city = (String) SPUtils.get(context, Contants.LOCATION_CITY_KEY, "上海市");

        } else {
            city = currentLocationBean.getCity() + currentLocationBean.getDistrict();
        }

        offerMsg(orderid,cid,sid,price,circle,circle,city);
    }
    private void offerMsg(String orderid,String cid,String sid,String money, String time,String remark, String address) {

        OkGo.<String>post(HttpUtil.IP+"topic/rob")
                .params("order_id",orderid)
                .params("user_id",sid)
                .params("offer_id", UserUtil.getUserInfo().getUserId())
                .params("price",money)
                .params("cycle",time)
                .params("address",address)
                .params("beizhu",remark)
//					.params("payMoney",payMoney)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {

                        BaseResponse baseResponse= DealResult.getInstace().dealBase(context,response);
                        if(baseResponse==null){
                            return;
                        }
                        if(baseResponse.getCode()==1){
                            ToastUtils.toastForShort(context,baseResponse.getMessage());
                            try {
                                EventBus.getDefault().post(new TopicHomeEvent());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }else {
                            ToastUtils.toastForShort(context,baseResponse.getMessage());
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                    }
                });
    }

    //打款
    public void remit(String brand,String price,String orderId,String sid,String cid,String name,String bank,String bankNum){
        Intent i=new Intent(context, PayActivity.class);
        i.putExtra("orderTitle", "");
        i.putExtra("cover", "");
        i.putExtra("brand", brand);
        i.putExtra("price",Float.valueOf(price));
        i.putExtra("topicId", orderId);
        i.putExtra("to_memberid", cid);
        i.putExtra("offer_id", sid);
        i.putExtra("name", name);
        i.putExtra("bank", bank);
        i.putExtra("bankNum", bankNum);
        context.startActivity(i);
    }


    public interface ReqCallBack{
        void call(OrderInfoResponse response);
    }


}
