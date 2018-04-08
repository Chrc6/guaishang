package com.houwei.guaishang.layout;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyco.dialog.widget.base.BaseDialog;
import com.houwei.guaishang.R;
import com.houwei.guaishang.activity.TopicDetailActivity;
import com.houwei.guaishang.activity.newui.TopicDetailComActivity;
import com.houwei.guaishang.bean.BaseResponse;
import com.houwei.guaishang.bean.TopicBean;
import com.houwei.guaishang.bean.UserBean;
import com.houwei.guaishang.tools.DealResult;
import com.houwei.guaishang.tools.HttpUtil;
import com.houwei.guaishang.tools.ToastUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by Administrator on 2017/10/18.
 */

public class OfferDialog2 extends BaseDialog {
    Context context;
    TopicBean topicBean;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private TextView tvClose;
    private TextView tvConfirm;
    private ImageView imageAvatar;
    private TextView orderName;
    private TextView orderPrice;
    private EditText etMoney;
    private EditText etTime;
    final	MProgressDialog progress ;
    private EditText etRemark;
    private String address;


    public OfferDialog2(Context context, TopicBean bean, String address) {
        super(context);
        this.context=context;
        this.topicBean=bean;
        this.address=address;
        progress = new MProgressDialog(context, false);
    }

    @Override
    public View onCreateView() {
        widthScale(0.95f);
//        showAnim(new Swing());

        // dismissAnim(this, new ZoomOutExit());
        View inflate = View.inflate(context, R.layout.dialog_offer, null);
        tvClose=(TextView)inflate.findViewById(R.id.tv_close);
        imageAvatar=(ImageView)inflate.findViewById(R.id.avatar);
        orderName=(TextView)inflate.findViewById(R.id.order_name);
        orderPrice=(TextView)inflate.findViewById(R.id.order_price);
        etMoney=(EditText)inflate.findViewById(R.id.et_money);
        etTime=(EditText)inflate.findViewById(R.id.et_time);
        etRemark=(EditText)inflate.findViewById(R.id.et_remark);
        tvConfirm=(TextView)inflate.findViewById(R.id.tv_confirm);
        return inflate;
    }

    @Override
    public void setUiBeforShow() {
        imageLoader.displayImage(topicBean.getCover(), imageAvatar,
                ((TopicDetailActivity)context).getITopicApplication().getOtherManage().getCircleOptionsDisplayImageOptions());
        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        orderName.setText(topicBean.getContent());
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String money=etMoney.getText().toString().trim();
                String time=etTime.getText().toString().trim();
                String remark=etTime.getText().toString().trim();
                if(TextUtils.isEmpty(money)){
                    ToastUtils.toastForShort(context,"金额不能为空");
                    return;
                }
                if(TextUtils.isEmpty(time)){
                    ToastUtils.toastForShort(context,"工期不能为空");
                    return;
                }
                offerMsg(money,time,remark);
            }
        });
    }

    private void offerMsg(String money, String time,String remark) {
        progress.show();
        OkGo.<String>post(HttpUtil.IP+"topic/rob")
                .params("order_id",topicBean.getTopicId())
                .params("user_id",topicBean.getMemberId())
                .params("offer_id",getUserID())
                .params("price",money)
                .params("cycle",time)
                .params("address",address)
                .params("beizhu",remark)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        progress.dismiss();
                        BaseResponse baseResponse=DealResult.getInstace().dealBase(context,response);
                        if(baseResponse==null){
                            return;
                        }
                        if(baseResponse.getCode()==1){
                            ToastUtils.toastForShort(context,baseResponse.getMessage());
                            dismiss();
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        progress.dismiss();
                        super.onError(response);
                    }
                });
    }

    public String getUserID() {
        UserBean instanceUser =  ((TopicDetailActivity)context).getITopicApplication()
                .getMyUserBeanManager().getInstance();
        return instanceUser == null ? "" : instanceUser.getUserid();
    }
}
