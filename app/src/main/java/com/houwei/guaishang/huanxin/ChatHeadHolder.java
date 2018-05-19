package com.houwei.guaishang.huanxin;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.houwei.guaishang.R;
import com.houwei.guaishang.base.holder.BaseHolder;
import com.houwei.guaishang.bean.LocationBean;
import com.houwei.guaishang.huanxin.order.OrderEntity;
import com.houwei.guaishang.huanxin.order.OrderInfoResponse;
import com.houwei.guaishang.sp.UserUtil;
import com.houwei.guaishang.tools.ToastUtils;
import com.luck.picture.lib.permissions.RxPermissions;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;

/**
 * Created by ${lei} on 2018/5/17.
 */

public class ChatHeadHolder extends BaseHolder<OrderEntity> {
    @BindView(R.id.phone_et)
    ImageView phoneEt;
    @BindView(R.id.edit_price)
    EditText editPrice;
    @BindView(R.id.edit_time)
    EditText editTime;
    @BindView(R.id.sure)
    TextView sure;
    @BindView(R.id.container)
    RelativeLayout container;
    @BindView(R.id.order_tv)
    TextView orderTv;
    @BindView(R.id.order_info_gp)
    LinearLayout orderInfoGp;

    private ChatManager mManager;
    private ChatInfo chatInfo;
    private RxPermissions rxPermissions;
    private LocationBean currentLocationBean;

    public ChatHeadHolder(Context context) {
        super(context);
    }

    public ChatHeadHolder(Context context, ChatInfo chatInfo) {
        super(context);
        this.chatInfo = chatInfo;
    }

    @NonNull
    @Override
    protected View initView(Context context) {
        mRootView = LayoutInflater.from(context).inflate(R.layout.ease_fragment_top, null, false);
        ButterKnife.bind(this, mRootView);
        if (mManager == null) {
            mManager = new ChatManager(context);
        }
        return mRootView;
    }

    @Override
    protected void updateUI(final Context context, final OrderEntity data) {
        if (data == null) {
            container.setVisibility(View.GONE);
            return;
        }
        container.setVisibility(View.VISIBLE);

        if (data.getIsOfferid().equals("1") && chatInfo.getSid().equals(UserUtil.getUserInfo().getUserId())) {
            sure.setVisibility(View.VISIBLE);
            orderTv.setVisibility(View.VISIBLE);
            orderInfoGp.setVisibility(View.GONE);
            sure.setText("打款订货");
            editPrice.setEnabled(false);
            editTime.setEnabled(false);
            orderTv.setText("交货期"+data.getData().get(0).getCycle()+"天 报价"+data.getData().get(0).getPrice()+"元");
        } else if (data.getIsOfferid().equals("1")) {
            //查询结果已报价 处理成无按钮样式
            orderTv.setVisibility(View.GONE);
            orderInfoGp.setVisibility(View.VISIBLE);
            sure.setText("已报价");
            editPrice.setEnabled(false);
            editTime.setEnabled(false);
            editPrice.setText(data.getData().get(0).getPrice()+"元");
            editTime.setText("交期"+data.getData().get(0).getCycle()+"天");
        } else {
            sure.setVisibility(View.VISIBLE);
            orderTv.setVisibility(View.GONE);
            orderInfoGp.setVisibility(View.VISIBLE);
            editPrice.setEnabled(true);
            editTime.setEnabled(true);
        }


        if (rxPermissions == null) {
            rxPermissions = new RxPermissions((Activity) context);
        }
        phoneEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rxPermissions.request(Manifest.permission.CALL_PHONE)
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(@io.reactivex.annotations.NonNull Boolean aBoolean) throws Exception {
                                if (aBoolean) {
                                    //用intent启动拨打电话
                                    if (TextUtils.isEmpty(chatInfo.getMobile())) {
                                        ToastUtils.toastForShort(context, "电话号码不能为空");
                                        return;
                                    }
                                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + chatInfo.getMobile()));
                                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                                        context.startActivity(intent);
                                    }
                                }
                            }
                        });

            }
        });
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (data.getIsOfferid().equals("1") && chatInfo.getSid().equals(UserUtil.getUserInfo().getUserId())) {
                    //打款订货
                    mManager.remit(chatInfo.getBrand(), data.getData().get(0).getPrice(), chatInfo.getOrderid(), chatInfo.getSid(),
                            chatInfo.getCid(), chatInfo.getHisRealName(), chatInfo.getBank(), chatInfo.getBankNum());

                } else {
                    if (TextUtils.isEmpty(editPrice.getText().toString())) {
                        ToastUtils.toastForShort(context, "输入的价格不能为空");
                        return;
                    }
                    if (TextUtils.isEmpty(editTime.getText().toString())) {
                        ToastUtils.toastForShort(context, "输入的交付日期不能为空");
                        return;
                    }

                    mManager.offer(currentLocationBean, editPrice.getText().toString(),
                            editTime.getText().toString(), chatInfo.getCid(),
                            chatInfo.getSid(), chatInfo.getOrderid());

                    editPrice.setText(editPrice.getText().toString()+"元");
                    editTime.setText("交期"+editTime.getText().toString()+"天");
                    editPrice.setEnabled(false);
                    editTime.setEnabled(false);
                    sure.setText("已报价");
                }
            }
        });

    }


    public void sendReq() {

        mManager.queryOffer(chatInfo.getCid(), chatInfo.getSid(), chatInfo.getOrderid(), new ChatManager.ReqCallBack() {
            @Override
            public void call(OrderInfoResponse response) {
                if (response != null) {
                    updateUI(getContext(), response.getData());
                }
            }
        });

//        Observable.create(new ObservableOnSubscribe<String>() {
//            @Override
//            public void subscribe(ObservableEmitter<String> e) throws Exception {
//                e.onNext("do");
//            }
//        }).subscribeOn(Schedulers.io()).map(new Function<String, OrderInfoResponse>() {
//            @Override
//            public OrderInfoResponse apply(String s) throws Exception {
//                OrderInfoResponse response = mManager.queryOffer(chatInfo.getCid(), chatInfo.getSid(), chatInfo.getOrderid());
//                if (response == null) {
//                    container.setVisibility(View.GONE);
//                    return new OrderInfoResponse();
//                }
//                return response;
//            }
//        }).filter(new Predicate<OrderInfoResponse>() {
//            @Override
//            public boolean test(OrderInfoResponse orderInfoResponse) throws Exception {
//                if (orderInfoResponse.getData() != null){
//                    return true;
//                }
//                return false;
//            }
//        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<OrderInfoResponse>() {
//            @Override
//            public void accept(OrderInfoResponse orderInfoResponse) throws Exception {
//                OrderEntity orderEntity = orderInfoResponse.getData();
//                updateUI(getContext(),orderEntity);
//            }
//        });
    }

    public LocationBean getCurrentLocationBean() {
        return currentLocationBean;
    }

    public void setCurrentLocationBean(LocationBean currentLocationBean) {
        this.currentLocationBean = currentLocationBean;
    }
}
