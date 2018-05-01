package com.houwei.guaishang.layout;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.houwei.guaishang.R;
import com.houwei.guaishang.activity.BaseActivity;
import com.houwei.guaishang.activity.Constant;
import com.houwei.guaishang.activity.OrderChatActivity;
import com.houwei.guaishang.activity.TopicDetailActivity;
import com.houwei.guaishang.activity.newui.TopicDetailMeActivity;
import com.houwei.guaishang.adapter.OfferAdapter;
import com.houwei.guaishang.bean.AvatarBean;
import com.houwei.guaishang.bean.CommentBean;
import com.houwei.guaishang.bean.OffersBean;
import com.houwei.guaishang.bean.Payment;
import com.houwei.guaishang.bean.PraiseBean;
import com.houwei.guaishang.bean.TopicBean;
import com.houwei.guaishang.easemob.EaseConstant;
import com.houwei.guaishang.easemob.PreferenceManager;
import com.houwei.guaishang.layout.PictureGridLayout.RedPacketClickListener;
import com.houwei.guaishang.manager.FaceManager;
import com.houwei.guaishang.manager.ITopicApplication;
import com.houwei.guaishang.manager.MyUserBeanManager;
import com.houwei.guaishang.preview.PreviewActivity;
import com.houwei.guaishang.tools.DealResult;
import com.houwei.guaishang.tools.HttpUtil;
import com.houwei.guaishang.tools.ShareUtil2;
import com.houwei.guaishang.tools.ToastUtils;
import com.houwei.guaishang.tools.ValueUtil;
import com.houwei.guaishang.view.NumberProgressBar;
import com.houwei.guaishang.view.OrderBuyDialog;
import com.houwei.guaishang.view.ProgressView;
import com.houwei.guaishang.views.CircleBitmapDisplayer1;
import com.houwei.guaishang.views.SpannableTextView;
import com.houwei.guaishang.views.SpannableTextView.MemberClickListener;
import com.houwei.guaishang.widget.FloatButton;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;


public class TopicAdapter extends BaseAdapter {
    private final Drawable attention;
    private final Drawable attentionUn;
    private List<TopicBean> list;
    private LayoutInflater mInflater;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private BaseActivity mContext;
    private FaceManager faceManager;
    private String userId;
    private DisplayImageOptions options;
    private TopicBeanDeleteListener onTopicBeanDeleteListener;
    private RedPacketClickListener onRedPacketClickListener;
    private TopicBeanFollowClickListener onTopicBeanFollowClickListener;
    private TopicBeanBaojiaClickListener onTopicBeanBaojiaClickListener;
    private int face_item_size;
    private MProgressDialog dialog;
    private RxPermissions rxPermissions;

    //	设置list跳转不同详情页
    private int jumpType;

    //请求类型（0 是全部  1 是 已订单）
    private int type;
    //头像列表
    private ArrayList<String> mIconList = new ArrayList<>();
    public TopicAdapter(BaseActivity mContext, List<TopicBean> list, int jumpType,int type) {
        this.list = list;
        this.type = type;
        this.mInflater = LayoutInflater.from(mContext);
        this.mContext = mContext;
        this.userId = mContext.getITopicApplication().getMyUserBeanManager().getUserId();
        this.faceManager = mContext.getITopicApplication().getFaceManager();
        this.options = mContext.getITopicApplication().getOtherManage().getRectDisplayImageOptions();
        this.face_item_size = (int) mContext.getResources().getDimension(R.dimen.face_tiny_item_size);
        this.jumpType=jumpType;
        dialog = new MProgressDialog(mContext, true);
        rxPermissions = new RxPermissions(mContext);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            attention = mContext.getDrawable(R.mipmap.attention);
            attentionUn = mContext.getDrawable(R.mipmap.attention_un);
        } else {
            attention = mContext.getResources().getDrawable(R.mipmap.attention);
            attentionUn = mContext.getResources().getDrawable(R.mipmap.attention_un);
        }
        attention.setBounds(0, 0, attention.getIntrinsicWidth(), attention.getIntrinsicHeight());
        attentionUn.setBounds(0, 0, attentionUn.getIntrinsicWidth(), attentionUn.getIntrinsicHeight());
    }

    public int getCount() {
        return list.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_topic, null);
             View finalConvertView = convertView;

            holder.avator = (ImageView) convertView.findViewById(R.id.avator);
            holder.imageMyOrder = (ImageView) convertView.findViewById(R.id.image_myorder);
            holder.header_name = (TextView) convertView.findViewById(R.id.header_name);
//            holder.recyclerView = (LRecyclerView) convertView.findViewById(R.id.recyclerView_offer);
            holder.header_location = (TextView) convertView.findViewById(R.id.header_location);
            holder.imgTitle = (ImageView) convertView.findViewById(R.id.img_title);
            holder.tvCount = (TextView) convertView.findViewById(R.id.tv_count);
            holder.barNum = (NumberProgressBar) convertView.findViewById(R.id.bar_num);
            holder.content = (TextView) convertView.findViewById(R.id.content);
//            holder.header_time = (TextView) convertView.findViewById(R.id.header_time);
//            holder.zan_count_btn = (PraiseTextView) convertView
//                    .findViewById(R.id.zan_count_btn);

            holder.delete_btn = (TextView) convertView
                    .findViewById(R.id.delete_btn);
            holder.price_tv = (TextView) convertView
                    .findViewById(R.id.price_tv);
//            holder.praise_ll = convertView.findViewById(R.id.praise_ll);
//            holder.comment_ll = convertView.findViewById(R.id.comment_ll);
            holder.share_ll = convertView.findViewById(R.id.share_ll);
            holder.share_count_btn = (TextView) convertView.findViewById(R.id.share_count_btn);
            holder.order_btn = (FloatButton)convertView.findViewById(R.id.order_btn);
            holder.order_count = (TextView) convertView.findViewById(R.id.count);
            holder.ratingBar = (RatingBar) convertView.findViewById(R.id.bar);
            holder.progressView = (ProgressView) convertView.findViewById(R.id.bar_status);
            holder.VProdectLayout = (LinearLayout) convertView.findViewById(R.id.product_layout);
            holder.price = (TextView) convertView.findViewById(R.id.price);
            holder.time = (TextView) convertView.findViewById(R.id.time);
//            holder.chat_btn = (Button) convertView.findViewById(R.id.chat_btn);
//            holder.linearLayoutForListView = (LinearLayoutForListView) convertView.findViewById(R.id.linearLayoutForListView);
//            holder.linearLayoutForListView.setDisableDivider(true);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final TopicBean bean = list.get(position);

        //处理下全部订单和已订单的ui差别
        if (type == 0){
            //全部订单
            holder.ratingBar.setVisibility(View.VISIBLE);
            holder.progressView.setVisibility(View.GONE);
            holder.VProdectLayout.setVisibility(View.GONE);
        }else {
            //已订单
            Payment payment = bean.getPayment();
            if (payment != null){
                holder.ratingBar.setVisibility(View.GONE);
                holder.progressView.setVisibility(View.VISIBLE);
                holder.VProdectLayout.setVisibility(View.VISIBLE);
                holder.progressView.setProgress(payment.getStatus());
                holder.price.setText(payment.getPrice());
                holder.time.setText(payment.getCycle());
            }else {
                holder.ratingBar.setVisibility(View.VISIBLE);
                holder.progressView.setVisibility(View.GONE);
                holder.VProdectLayout.setVisibility(View.GONE);
            }


        }

        final String memberId = bean.getMemberId();
        try {
            int max = Integer.valueOf(bean.getSetRob());
            int progress = Integer.valueOf(bean.getNowRob());
            holder.barNum.setMax(max);
            holder.barNum.setProgress(progress);
            if(progress==max){
                //已结束
                holder.order_btn.setStatu(3);
                holder.order_btn.setBrief("");
                holder.order_count.setVisibility(View.GONE);
            }else if (TextUtils.equals(mContext.getUserID(),memberId)){
                //自己发的单
                //测试
                mIconList.clear();
                List<OffersBean.OfferBean> offerPrice = bean.getOfferPrice();
                int size = offerPrice.size();
                if (size == 0){
                    holder.order_count.setVisibility(View.GONE);
                    holder.order_btn.setStatu(5);
                }else {
                    for (int i = 0; i < size; i++) {
                        String avatar = offerPrice.get(i).getAvatar();
                        if (!mIconList.contains(avatar)) {
                            mIconList.add(avatar);
                        }
                    }
                    holder.order_count.setVisibility( View.VISIBLE);
                    holder.order_count.setText(mIconList.size() + "");
                    holder.order_btn.setStatu(2);
                    holder.order_btn.setmAvatarList(mIconList);
                }
            }else if (Integer.valueOf(Integer.valueOf(bean.getIsOffer())) == 1){
                holder.order_count.setVisibility(View.GONE);
                holder.order_btn.setStatu(4);
            }else {
                holder.order_count.setVisibility(View.GONE);
                holder.order_btn.setStatu(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(TextUtils.equals(mContext.getUserID(),memberId)){
            holder.imageMyOrder.setVisibility(View.VISIBLE);
//            holder.recyclerView.setVisibility(View.VISIBLE);
//            initRecyclerView(bean,holder.recyclerView,bean.getOfferPrice());
        }else{
//            holder.recyclerView.setVisibility(View.GONE);
            holder.imageMyOrder.setVisibility(View.INVISIBLE);
        }

        //imageLoader.displayImage(bean.getMemberAvatar().findSmallUrl(), holder.avator);
//        imageLoader.displayImage(bean.getMemberAvatar().findSmallUrl(), holder.avator, mContext.getITopicApplication().getOtherManage().getCircleOptionsDisplayImageOptions());
        ImageLoader.getInstance().displayImage(bean.getMemberAvatar().findSmallUrl(), holder.avator);
        imageLoader.displayImage(bean.getCover(), holder.imgTitle, mContext.getITopicApplication().getOtherManage().getRectDisplayImageOptions());
        holder.content.setText("详情： "+faceManager.
                        convertNormalStringToSpannableString(mContext, bean.getContent()),
                BufferType.SPANNABLE);
        FaceManager.extractMention2Link(holder.content);

        holder.content.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View arg0) {
                // TODO Auto-generated method stub
                MenuDialog followDialog = new MenuDialog(mContext,
                        new MenuDialog.ButtonClick() {

                            @Override
                            public void onSureButtonClick() {
                                // TODO Auto-generated method stub
                                ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                                clipboard.setText(bean.getContent());
                            }
                        });
                followDialog.show();
                return true;
            }
        });

//        holder.linearLayoutForListView.setAdapter(new CommentItemAdapter(
//                mContext, bean.getComments()));


        String locationTemp = bean.getDistance() != null ? bean.getDistanceString() : bean.getAddress();
        String location = "";

        if(locationTemp.contains("省")){
            location = locationTemp.substring((locationTemp.indexOf("省")+1), locationTemp.length());
        }else {
            location = locationTemp;
        }
        if(locationTemp.contains("市")){
            location = location.substring(0, (location.indexOf("市")+1));
        }
        // TODO: 2018/4/21 设置评分
        if (!TextUtils.isEmpty(bean.getJifen())) {
            Integer jifen = Integer.valueOf(bean.getJifen());
            holder.ratingBar.setRating(jifen);
        }else {
            holder.ratingBar.setRating(0);
        }
        holder.ratingBar.setIsIndicator(true);
        holder.header_location.setText(location);
        holder.header_name.setText(bean.getMemberName());
//        holder.header_time.setText(bean.getTimeString());
//        holder.linearLayoutForListView.setVisibility((bean.getComments() == null || bean.getComments().isEmpty()) ? View.GONE : View.VISIBLE);
//        holder.linearLayoutForListView.setVisibility(View.GONE);
        holder.price_tv.setText("" + (int)bean.getPrice());
        holder.share_count_btn.setText(bean.getShareNum()+"");

//        holder.zan_count_btn.setText(bean.getSumPrice());


        holder.avator.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Log.i("WXCH","jumpToHisInfoActivity");
                mContext.jumpToHisInfoActivity(bean.getMemberId(), bean.getMemberName(), bean.getMemberAvatar());
            }
        });

        holder.share_ll.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ShareUtil2 shareUtil2 = new ShareUtil2(mContext, new PlatformActionListener() {
                    @Override
                    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                        Log.i("onComplete","platform="+platform.getName()+" 分享成功");
                        //Message message=new Message();
                        //Bundle bundle=new Bundle();
                        //bundle.putString("memberId", bean.getMemberId());
                        //message.setData(bundle);
                        //message.what=1;
                        //holder.share_count_btn.setText((bean.getShareNum()+1)+"");
                        //notifyDataSetChanged();

                        //HashMap<String, String> data = new HashMap<String, String>();
                        //data.put("id", msg.getData().getString("memberId"));
                        //String s = HttpUtil.postMsg(HttpUtil.getData(data), HttpUtil.IP + "mission/Sharing/");
                        //Log.i("WXCH","SSSSSS:" + s);
                    }

                    @Override
                    public void onError(Platform platform, int i, Throwable throwable) {

                    }

                    @Override
                    public void onCancel(Platform platform, int i) {

                    }
                });
                shareUtil2.setContent(bean.getContent());
                shareUtil2.setUrl(HttpUtil.SHARE_TOPIC_IP + bean.getTopicId());
                if (bean.getPicture() != null && !bean.getPicture().isEmpty()) {
                    shareUtil2.setImageUrl(bean.getPicture().get(0).findOriginalUrl());
                }
                shareUtil2.showBottomPopupWin();
            }
        });

       /* if (userId.equals(bean.getMemberId())) {
            holder.delete_btn.setVisibility(View.VISIBLE);
            holder.chat_btn.setVisibility(View.GONE);
            holder.order_btn.setVisibility(View.GONE);
            holder.follow_btn.setVisibility(View.GONE);
        } else {
            holder.delete_btn.setVisibility(View.GONE);
            holder.chat_btn.setVisibility(View.VISIBLE);
            holder.order_btn.setVisibility(View.VISIBLE);
            holder.follow_btn.setVisibility(View.VISIBLE);
        }*/

        holder.delete_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                SureOrCancelDialog followDialog = new SureOrCancelDialog(
                        mContext, "删除掉该条商品", "好",
                        new SureOrCancelDialog.SureButtonClick() {

                            @Override
                            public void onSureButtonClick() {
                                // TODO Auto-generated method stub
                                if (onTopicBeanDeleteListener != null) {
                                    onTopicBeanDeleteListener.onTopicBeanDeleteClick(bean);
                                }
                            }
                        });
                followDialog.show();
            }
        });


//		holder.order_btn.setText(ValueUtil.getTopicTypeBuyButtonString(bean.getType()));
        holder.order_btn.setFloatBtnClickListener(new FloatButton.FloatBtnClickListener() {
            @Override
            public void galb() {
                orderBuyOrNextPage(bean,true);
            }

            @Override
            public void goChatView() {
                // TODO: 2018/4/21 跳转到聊天页面
                Intent intent = new Intent(mContext, OrderChatActivity.class);
                List<OffersBean.OfferBean> offerPriceList = bean.getOfferPrice();
                intent.putExtra(OrderChatActivity.Parse_List,(Serializable) offerPriceList);
                mContext.startActivity(intent);
            }

            @Override
            public void doNothing() {
            }
        });
        holder.order_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Log.i("WXCH","userId:"+userId+",getMemberId:"+bean.getMemberId());
                // TODO Auto-generated method stub
                if (mContext.checkLogined()) {
                    if(!bean.getMemberId().equals(mContext.getUserID())){
                        orderBuyOrNextPage(bean,true);
                    }
                }
            }
        });
//        if(jumpType!=0){
//            holder.order_btn.setVisibility(View.INVISIBLE);
//        }


//        holder.follow_btn.setText(ValueUtil.getRelationTypeStringSimple(bean.getFriendship()));
//        holder.follow_btn.setBackgroundResource(ValueUtil.getRelationTypeDrawableSimple(bean.getFriendship()));
//        holder.follow_btn.setTextColor(ValueUtil.getRelationTextColorSimple(bean.getFriendship()));
//        holder.follow_btn.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//                if (mContext.checkLogined()) {
//                    if(!bean.getMemberId().equals(mContext.getUserID())){
//                        if (onTopicBeanFollowClickListener != null) {
//                            onTopicBeanFollowClickListener.onTopicBeanFollowClick(bean);
//                        }
//                    }
//
//                }
//                /*if (onTopicBeanFollowClickListener != null) {
//                    onTopicBeanFollowClickListener.onTopicBeanFollowClick(bean);
//                }*/
//            }
//        });

        holder.content.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if(TextUtils.equals(mContext.getUserID(),memberId)){
                    Intent intent=new Intent(mContext,TopicDetailMeActivity.class);
                    intent.putExtra("TopicBean", bean);
                    intent.putExtra("position", 0);
                    mContext.startActivityForResult(intent, 0);
                    return;
                }
                Intent i = new Intent();
                if(jumpType==0){
                    i.setClass(mContext, TopicDetailActivity.class);
                }else{
                    i.setClass(mContext, TopicDetailMeActivity.class);

                }
                i.putExtra("TopicBean", bean);
                i.putExtra("position", 0);
                i.putExtra("needPay", Integer.valueOf(bean.getIsOffer()));
                mContext.startActivityForResult(i, 0);

            }
        });

        holder.imgTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> list = new ArrayList<String>();
                list.add(bean.getCover());
                Intent intent = new Intent(mContext, PreviewActivity.class);
                intent.putExtra("list",list);
                mContext.startActivity(intent);
            }
        });
        convertView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //Log.i("WXCH","bean:"+bean);
                if(TextUtils.equals(mContext.getUserID(),memberId)){
                    Intent intent=new Intent(mContext,TopicDetailMeActivity.class);
                    intent.putExtra("TopicBean", bean);
                    intent.putExtra("position", 0);
                    mContext.startActivityForResult(intent, 0);
                    return;
                }
                Intent i = new Intent();
                if(jumpType==0){
                    i.setClass(mContext, TopicDetailActivity.class);
                }else{
                    i.setClass(mContext, TopicDetailMeActivity.class);

                }
                i.putExtra("TopicBean", bean);
                i.putExtra("position", 0);
                i.putExtra("needPay", Integer.valueOf(bean.getIsOffer()));
                mContext.startActivityForResult(i, 0);

            }
        });
        return convertView;
    }

    private void orderBuyOrNextPage(final TopicBean bean, final boolean fromBtnClick) {
        OkGo.<String>post(HttpUtil.IP+"Topic/is_rob")
                .tag(this)
                .params("user_id", userId)
                .params("topicid", bean.getTopicId())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String res=response.body().toString().trim();
                        //Log.i("WXCH","SSSSSS:" + res);
                        if(res.contains("1")){
//                            OrderBuyDialog.getInstance(mContext)
//                                    .setData(PreferenceManager.getInstance().getUserCoins(), bean, mContext)
//                                    .show();
                            new OrderBuyDialog(mContext, PreferenceManager.getInstance().getUserCoins(), bean).show();
                        }else if (fromBtnClick){
                            ToastUtils.toastForShort(mContext, "此单您已抢过");
                        } else {
                            Intent i = new Intent();
                            i.putExtra("TopicBean", bean);
                            i.putExtra("position", 0);
                            mContext.jumpToChatActivityCom(bean,0,bean.getMemberId(), bean.getMemberName(), bean.getMemberAvatar(),EaseConstant.CHATTYPE_SINGLE);
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {

                        super.onError(response);
                    }
                });
    }

    private void initRecyclerView(final TopicBean topicBean, LRecyclerView recyclerViewOffer, List<OffersBean.OfferBean> beans) {
        if(beans==null||beans.isEmpty()){
            return;
        }
        LinearLayoutManager manager=new LinearLayoutManager(mContext);
        manager.setAutoMeasureEnabled(true);
        final OfferAdapter mAdapter = new OfferAdapter(mContext);
//        TopicLinearLayoutManager manager1=new TopicLinearLayoutManager(mContext,mAdapter);
//        recyclerViewOffer.setLayoutManager(manager);
        mAdapter.setDataList(beans);
        mAdapter.setTopicBean(topicBean);
        final LRecyclerViewAdapter lRecyclerViewAdapter=new LRecyclerViewAdapter(mAdapter);
        recyclerViewOffer.setAdapter(lRecyclerViewAdapter);
        recyclerViewOffer.setLoadMoreEnabled(false);
        lRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                OffersBean.OfferBean bean = mAdapter.getDataList().get(position);
                if(TextUtils.equals(mContext.getUserID(),bean.getOfferId())){
                    ToastUtils.toastForShort(mContext,"不能同自己聊天");
                    return;
                }
                AvatarBean avatarBean=new AvatarBean();
                avatarBean.setOriginal(bean.getAvatar());
                avatarBean.setSmall(bean.getAvatar());
                mContext.jumpToChatActivityCom(topicBean,0,bean.getOfferId(), bean.getName(), avatarBean, EaseConstant.CHATTYPE_SINGLE);
            }
        });
        recyclerViewOffer.setPullRefreshEnabled(false);
        recyclerViewOffer.setLoadMoreEnabled(false);
        recyclerViewOffer.refresh();
    }

    private void dealPraise(final PraiseTextView zan_count_btn, final TopicBean topicBean) {
        dialog.show();
        OkGo.<String>post(HttpUtil.IP+"topic/praise")
                .tag(this)
                .params("userid", userId)
                .params("topicid", topicBean.getTopicId())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        dialog.dismiss();
                        PraiseBean bean=DealResult.getInstace().dealData(mContext,response,PraiseBean.class);
                        if(bean==null){return;}
                        if(bean.getCode()==Constant.SUCESS){
                            if(topicBean.isPraised()){
                                zan_count_btn.setText((topicBean.getPraiseCount()-1)+"");
                                topicBean.setPraiseCount(topicBean.getPraiseCount()-1);
                                zan_count_btn.setCompoundDrawables(null,attentionUn,null,null);
                                topicBean.setPraised(false);
                            }else{
                                zan_count_btn.setText((topicBean.getPraiseCount()+1)+"");
                                topicBean.setPraiseCount(topicBean.getPraiseCount()+1);
                                zan_count_btn.setCompoundDrawables(null,attention,null,null);
                                topicBean.setPraised(true);
                            }
//                            topicBean.setFriendship(bean.getData());
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        dialog.dismiss();
                        super.onError(response);
                    }
                });
    }

    public static class ViewHolder {
        private TextView content, header_name,  header_location,tvCount;
        private PraiseTextView zan_count_btn;
        private ImageView avator,imgTitle,imgIndicate,imageMyOrder;
        private TextView  delete_btn,tvProgress;
        private View share_ll;
        private TextView share_count_btn;
//        private LinearLayoutForListView linearLayoutForListView;
        private NumberProgressBar barNum;
//        private LRecyclerView recyclerView;
        private ProgressView progressView;
        private LinearLayout VProdectLayout;
        private TextView price,time;
        private FloatButton order_btn;
        private TextView order_count;
        private TextView price_tv;
        private RatingBar ratingBar;

    }

    private DisplayImageOptions kkk(Context context) {

        return new DisplayImageOptions.Builder()
                .displayer(new CircleBitmapDisplayer1(context)).build();

    }

    public interface TopicBeanDeleteListener {
        public void onTopicBeanDeleteClick(TopicBean topicBean);
    }

    public void setOnTopicBeanDeleteListener(TopicBeanDeleteListener onTopicBeanDeleteListener) {
        this.onTopicBeanDeleteListener = onTopicBeanDeleteListener;
    }

    public TopicBeanDeleteListener getOnTopicBeanDeleteListener() {
        return onTopicBeanDeleteListener;
    }

    public interface TopicBeanFollowClickListener {
        public void onTopicBeanFollowClick(TopicBean topicBean);
    }
    public interface TopicBeanBaojiaClickListener {
        public void TopicBeanBaojiaClick(TopicBean topicBean);
    }


    public class CommentItemAdapter extends BaseAdapter {

        private BaseActivity mContext;
        private List<CommentBean> cellReviewList;
        private LayoutInflater mLayoutInflater;

        public CommentItemAdapter(BaseActivity context, List<CommentBean> cellReviewList) {
            this.mContext = context;
            this.cellReviewList = cellReviewList;
            this.mLayoutInflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public int getCount() {
            return cellReviewList == null ? 0 : cellReviewList.size();
        }

        @Override
        public String getItem(int index) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.listview_review_textview, null);
            }
            final CommentBean cellBean = cellReviewList.get(position);
            SpannableTextView comment_tiny_tv = (SpannableTextView) convertView
                    .findViewById(R.id.comment_tiny_tv);


            comment_tiny_tv.setCommentItem(cellBean, new MemberClickListener() {

                @Override
                public void onMemberClick(CommentBean commentBean) {
                    // TODO Auto-generated method stub

                    mContext.jumpToHisInfoActivity(commentBean.getMemberId(),
                            commentBean.getMemberName(),
                            commentBean.getMemberAvatar());
                }
            }, new MemberClickListener() {

                @Override
                public void onMemberClick(CommentBean commentBean) {
                    // TODO Auto-generated method stub

                    mContext.jumpToHisInfoActivity(commentBean.getToMemberId(),
                            commentBean.getToMemberName(),
                            commentBean.getToMemberAvatar());

                }
            });

            comment_tiny_tv.append(faceManager.
                    convertNormalStringToSpannableString(mContext, cellBean.getContent(), face_item_size));

            return convertView;
        }
    }

    public RedPacketClickListener getOnRedPacketClickListener() {
        return onRedPacketClickListener;
    }

    public void setOnRedPacketClickListener(RedPacketClickListener onRedPacketClickListener) {
        this.onRedPacketClickListener = onRedPacketClickListener;
    }

    public TopicBeanFollowClickListener getOnTopicBeanFollowClickListener() {
        return onTopicBeanFollowClickListener;
    }

    public void setOnTopicBeanFollowClickListener(
            TopicBeanFollowClickListener onTopicBeanFollowClickListener) {
        this.onTopicBeanFollowClickListener = onTopicBeanFollowClickListener;
    }

    public TopicBeanBaojiaClickListener getTopicBeanBaojiaClickListener() {
        return onTopicBeanBaojiaClickListener;
    }

    public void setTopicBeanBaojiaClickListener(
            TopicBeanBaojiaClickListener onTopicBeanBaojiaClickListener) {
        this.onTopicBeanBaojiaClickListener = onTopicBeanBaojiaClickListener;
    }


}

