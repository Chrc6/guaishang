package com.houwei.guaishang.activity.newui;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.jdsjlzx.ItemDecoration.GridItemDecoration;
import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.houwei.guaishang.R;
import com.houwei.guaishang.activity.BaseActivity;
import com.houwei.guaishang.activity.BasePhotoGridActivity;
import com.houwei.guaishang.adapter.GridMeAdapter;
import com.houwei.guaishang.bean.MyInfoBean;
import com.houwei.guaishang.bean.StringResponse;
import com.houwei.guaishang.bean.UserBean;
import com.houwei.guaishang.inter.DeleteInter;
import com.houwei.guaishang.layout.PhotoPopupWindow;
import com.houwei.guaishang.tools.DealResult;
import com.houwei.guaishang.tools.HttpUtil;
import com.houwei.guaishang.tools.JsonParser;
import com.houwei.guaishang.tools.SPUtils;
import com.houwei.guaishang.tools.ToastUtils;
import com.houwei.guaishang.view.CircleImageView;
import com.luck.picture.lib.PictureSelectionModel;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.compress.Luban;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * 我的 个人资料 界面
 */
public class MyInfoActivity extends BaseActivity implements DeleteInter {

    @BindView(R.id.avatar_ll)
    LinearLayout avatarLl;
    @BindView(R.id.user_head)
    CircleImageView userHead;
    @BindView(R.id.tv_license)
    TextView tvLicense;
    @BindView(R.id.gridView_lisence)
    ImageView gridViewLisence;
    @BindView(R.id.textView3)
    TextView textView3;
    @BindView(R.id.tv_phone)
    EditText tvPhone;
    @BindView(R.id.gridView)
    LRecyclerView recyclerView;
    @BindView(R.id.scrollview)
    ScrollView scrollview;
    private final int HEAD_TYPE = 10;
    private final int CARD_TYPE = 11;
    private final int GRID_TYPE = 12;
    @BindView(R.id.tv_name)
    TextView tvName;
    private int flag = 0;
    private List<LocalMedia> selectList1 = new ArrayList<>();
    private List<LocalMedia> selectList2 = new ArrayList<>();
    private List<LocalMedia> selectList3 = new ArrayList<>();
    private GridMeAdapter mAdapter;
    private LRecyclerViewAdapter lRecyclerViewAdapter;
    public final static int PICTURE_UPDATE_ICON = R.drawable.picture_update_icon;
    private MyHandler handler = new MyHandler(this);
    private String userid;
    private String userName;


    private class MyHandler extends Handler {
        private WeakReference<Context> reference;

        public MyHandler(Context context) {
            reference = new WeakReference<Context>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            progress.dismiss();
            switch (msg.what) {
                case NETWORK_SUCCESS_DATA_RIGHT:
                    StringResponse retMap = (StringResponse) msg.obj;
                    if (retMap.isSuccess()) {
                        PictureFileUtils.deleteCacheDirFile(MyInfoActivity.this);
                        ToastUtils.toastForShort(MyInfoActivity.this, "上传成功");
                        switch (flag) {
                            case HEAD_TYPE:
                                SPUtils.put(MyInfoActivity.this, "headimage", HttpUtil.IP_NOAPI + retMap.getData());
                                UserBean ub = getITopicApplication().getMyUserBeanManager()
                                        .getInstance();
                                ub.getAvatar().setSmall(retMap.getData());
                                ub.getAvatar().setOriginal(retMap.getData());
//                                ub.setAvatar();
                                getITopicApplication().getMyUserBeanManager().storeUserInfo(ub);
                                getITopicApplication().getMyUserBeanManager().notityUserInfoChanged(ub);
                                break;
                            case CARD_TYPE:
                                SPUtils.put(MyInfoActivity.this, "cardimage", HttpUtil.IP_NOAPI + retMap.getData());
                                break;
                            case GRID_TYPE:
                                String res = "";
                                List<StringResponse.PictureBean> lists = retMap.getPictures();
                                if (lists.isEmpty()) {
                                    return;
                                }
                                for (StringResponse.PictureBean bean : lists) {
                                    res = res + HttpUtil.IP_NOAPI + bean.getSmall() + ",";
                                }
                                if (res.endsWith(",")) {
                                    res = res.substring(0, res.length() - 1);
                                }
                                SPUtils.put(MyInfoActivity.this, "gridimage", res);
                                break;
                        }
                    } else {
                        ToastUtils.toastForShort(MyInfoActivity.this, retMap.getMessage());
                    }
                    break;
                case NETWORK_SUCCESS_DATA_ERROR:
                    break;
                default:
                    break;
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info);
        userid = getITopicApplication().getMyUserBeanManager().getUserId();
        userName = getITopicApplication().getMyUserBeanManager().getInstance().getName();
        ButterKnife.bind(this);
        initProgressDialog(false, null);
        initRcycle();
        getDatas();
    }

    private void initListener() {
        tvPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String phone=s.toString();
                if(s.length()==11){
                    Log.d("CCC","-->"+s.toString());
                    updatePhoneNum(phone);
                }
            }
        });
    }

    private void updatePhoneNum(String phone) {
        OkGo.<String>post(HttpUtil.IP + "user/modify1")
                .params("userid", userid)
                .params("event", "mobile")
                .params("value", phone)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                    }
                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                    }
                });
    }

    private void getDatas() {
        OkGo.<String>post(HttpUtil.IP + "user/profile")
                .params("memberid", userid)
                .params("userid", userid)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        MyInfoBean bean = DealResult.getInstace().dealBean(MyInfoActivity.this, response, MyInfoBean.class);
                        if (bean.getCode() == 1) {
                            MyInfoBean.DataBean data = bean.getData();
                            tvPhone.setText(data.getMobile());
                            initListener();
                            Glide.with(MyInfoActivity.this).load( data.getAvatar().getSmall()).into(userHead);
                            List<MyInfoBean.DataBean.IdCardBean> idCards = data.getIdCard();
                            if (!idCards.isEmpty()) {
                                if(!MyInfoActivity.this.isFinishing()){
                                    Glide.with(MyInfoActivity.this).load(HttpUtil.IP_NOAPI + idCards.get(0).getSmall()).into(gridViewLisence);
                                }
                            }
                            List<MyInfoBean.DataBean.PictureBean> pictureBeanList = data.getPicture();
                            if (pictureBeanList.isEmpty()) {
                                return;
                            }
                            selectList3.clear();
                            for (MyInfoBean.DataBean.PictureBean picBean : pictureBeanList) {
                                LocalMedia localMedia = new LocalMedia();
                                localMedia.setPath(HttpUtil.IP_NOAPI + picBean.getOriginal());
                                selectList3.add(localMedia);
                            }
                            if (selectList3.size() < 9) {
                                LocalMedia localMedia = new LocalMedia();
                                localMedia.setPath("");
                                selectList3.add(localMedia);
                            }
                            mAdapter.setDataList(selectList3);
                            recyclerView.refresh();
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                    }
                });
    }


    private void initRcycle() {
        tvName.setText(userName);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mAdapter = new GridMeAdapter(this, this);
        selectList3 = new ArrayList<LocalMedia>();
        LocalMedia localMedia = new LocalMedia();
        localMedia.setPath("");
        selectList3.add(localMedia);
        mAdapter.setDataList(selectList3);
        lRecyclerViewAdapter = new LRecyclerViewAdapter(mAdapter);
        recyclerView.setAdapter(lRecyclerViewAdapter);
        GridItemDecoration divider = new GridItemDecoration.Builder(this)
                .setHorizontal(R.dimen.radius_corner)
//                .setVertical(R.dimen.radius_corner)
                .setColorResource(R.color.white_color)
                .build();
		recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(divider);
        recyclerView.setLoadMoreEnabled(false);
        recyclerView.setPullRefreshEnabled(false);
        recyclerView.refresh();
        lRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (TextUtils.isEmpty( mAdapter.getDataList().get(position).getPath().trim())) {
                    flag = GRID_TYPE;
                    showBottomPopupWin();
                } else {
                    PictureSelector.create(MyInfoActivity.this).externalPicturePreview(position, selectList3);
                }
            }
        });
    }


    @OnClick({R.id.avatar_ll, R.id.user_head, R.id.gridView_lisence, R.id.gridView})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.avatar_ll:
                flag = HEAD_TYPE;
                selectList1.clear();
                showBottomPopupWin();
                break;
            case R.id.user_head:
                flag = HEAD_TYPE;
                selectList2.clear();
                showBottomPopupWin();
                break;
            case R.id.gridView_lisence:
//                selectList3.clear();
                flag = CARD_TYPE;
                showBottomPopupWin();
                break;
            case R.id.gridView:
//                flag=GRID_TYPE;
//                showBottomPopupWin();
                break;
            default:
                break;

        }
    }


    public void showBottomPopupWin() {
        hideKeyboard();
        LayoutInflater mLayoutInfalter = (LayoutInflater) this
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        LinearLayout mPopView = (LinearLayout) mLayoutInfalter.inflate(
                R.layout.bottom_photo_select_popupwindow, null);
        PhotoPopupWindow mPopupWin = new PhotoPopupWindow(this, mPopView);
        mPopupWin.setAnimationStyle(R.style.BottomPopupAnimation);
        mPopupWin.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
        mPopupWin.setOnSelectPhotoListener(new PhotoPopupWindow.SelectPhotoListener() {
            @Override
            public void onGallery(View v) {
                switch (flag) {
                    case HEAD_TYPE:
                        gotoPic(1, 3, true, true, HEAD_TYPE);
                        break;
                    case CARD_TYPE:
                        gotoPic(1, 3, false, false, CARD_TYPE);
                        break;
                    case GRID_TYPE:
                        gotoPic(9, 3, false, false, GRID_TYPE);
                        break;
                }
            }

            @Override
            public void onCamera(View v) {
                switch (flag) {
                    case HEAD_TYPE:
                        gotoCamer(HEAD_TYPE);
                        break;
                    case CARD_TYPE:
                        gotoCamer(CARD_TYPE);
                        break;
                    case GRID_TYPE:
                        Log.d("CCC", "GRID_TYPE" + GRID_TYPE);
                        gotoCamer(GRID_TYPE);
                        break;
                }
            }
        });
    }

    public void gotoPic(int max, int spancount, boolean crop, boolean cropCircle, int requesCode) {
        // 进入相册 以下是例子：用不到的api可以不写
//        if(!selectList3.isEmpty()){
//            for(LocalMedia bean: selectList3){
//                if(TextUtils.isEmpty(bean.getPath())){
//                    selectList3.remove(bean);
//                }
//            }
//        }
        PictureSelectionModel pictureModel = PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()
//                .theme()//主题样式(不设置为默认样式) 也可参考demo values/styles下 例如：R.style.picture.white.style
                .maxSelectNum(max)// 最大图片选择数量 int
                .minSelectNum(0)// 最小选择数量 int
                .imageSpanCount(spancount)// 每行显示个数 int
                .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                .previewImage(true)// 是否可预览图片 true or false
                .enablePreviewAudio(false) // 是否可播放音频 true or false
                .compressGrade(Luban.THIRD_GEAR)// luban压缩档次，默认3档 Luban.THIRD_GEAR、Luban.FIRST_GEAR、Luban.CUSTOM_GEAR
                .isCamera(true)// 是否显示拍照按钮 true or false
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                .sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                .setOutputCameraPath("/CustomPath")// 自定义拍照保存路径,可不填
                .enableCrop(crop)// 是否裁剪 true or false
                .compress(true)// 是否压缩 true or false
                .cropWH(20, 20)// 裁剪宽高比，设置如果大于图片本身宽高则无效 int
                .compressMode(PictureConfig.SYSTEM_COMPRESS_MODE)//系统自带 or 鲁班压缩 PictureConfig.SYSTEM_COMPRESS_MODE or LUBAN_COMPRESS_MODE
                .freeStyleCropEnabled(false)// 裁剪框是否可拖拽 true or false
                .circleDimmedLayer(cropCircle)// 是否圆形裁剪 true or false
                .showCropFrame(false)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
                .previewEggs(false);// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true or false
//                if(flag==GRID_TYPE){
//                    pictureModel .selectionMedia(selectList3);// 是否传入已选图片 List<LocalMedia> list
//                }
        pictureModel.forResult(requesCode);//结果回调onActivityResult code
    }


    public void gotoCamer(int requesCode) {
        //包括裁剪和压缩后的缓存，要在上传成功后调用，注意：需要系统sd卡权限
//        PictureFileUtils.deleteCacheDirFile(MainActivity.this);
        PictureSelector.create(this)
                .openCamera(PictureMimeType.ofImage())
//                .openGallery(PictureMimeType.ofImage())
                .forResult(requesCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Log.d("CCC", "GRID_TYPE" + requestCode);
            switch (requestCode) {
                case HEAD_TYPE:
                    selectList1.clear();
                    selectList1 = PictureSelector.obtainMultipleResult(data);
                    Glide.with(MyInfoActivity.this).load(selectList1.get(0).getPath()).into(userHead);
                    LocalMedia m = selectList1.get(0);
//                    if(m.isCut()){
//                        upLoadPicture(m.getCutPath(),"user/upload");
//                    }else{
//                    }
                    upLoadPicture(m.getPath(), "user/upload");
                    // 图片选择结果回调
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                    // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
//                    adapter.setList(selectList);
//                    adapter.notifyDataSetChanged();
//                    DebugUtil.i(TAG, "onActivityResult:" + selectList.size());
                    break;
                case CARD_TYPE:
                    selectList2.clear();
                    selectList2 = PictureSelector.obtainMultipleResult(data);
                    Glide.with(MyInfoActivity.this).load(selectList2.get(0).getPath()).into(gridViewLisence);
                    upLoadPicture(selectList2.get(0).getPath(), "user/id_card");
                    break;
                case GRID_TYPE:
                    selectList3.clear();
                    selectList3 = PictureSelector.obtainMultipleResult(data);
                    if (selectList3.isEmpty()) {
                        return;
                    }
                    if (selectList3.size() < 9) {
                        for (LocalMedia bean : selectList3) {
                            if (TextUtils.isEmpty(bean.getPath())) {
                                selectList3.remove(bean);
                            }
                        }
                        LocalMedia localMedia = new LocalMedia();
                        localMedia.setPath("");
                        selectList3.add(localMedia);
                    }
                    mAdapter.clear();
                    lRecyclerViewAdapter.notifyDataSetChanged();
                    mAdapter.setDataList(selectList3);
                    recyclerView.refresh();
                    uploadMul(selectList3);
                    break;
            }
        }
    }


    // 如果不是切割的upLoadBitmap就很大
    private void upLoadPicture(String newPicturePath, String port) {
        progress.show();
        new Thread(new UpdateStringRun(newPicturePath, port)).start();
    }
//
//        // 预览图片 可自定长按保存路径
//    PictureSelector.create(MainActivity.this).externalPicturePreview(position, "/custom_file", selectList);
//    PictureSelector.create(MainActivity.this).externalPicturePreview(position, selectList);


    private class UpdateStringRun implements Runnable {
        private File upLoadBitmapFile;
        private String newPicturePath;
        String port;

        public UpdateStringRun(String newPicturePath, String port) {
            this.newPicturePath = newPicturePath;
            this.upLoadBitmapFile = new File(newPicturePath);
            this.port = port;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            StringResponse retMap = null;
            try {
                String url = HttpUtil.IP + port;
                // 如果不是切割的upLoadBitmap就很大,在这里压缩
                retMap = JsonParser.getStringResponse2(HttpUtil.uploadFile(url,
                        upLoadBitmapFile, userid));
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (retMap != null) {
                retMap.setTag(newPicturePath);
                handler.sendMessage(handler.obtainMessage(
                        NETWORK_SUCCESS_DATA_RIGHT, retMap));
            } else {
                handler.sendMessage(handler
                        .obtainMessage(NETWORK_SUCCESS_DATA_ERROR));
            }
        }
    }

    @Override
    public void delete(int positon) {
        mAdapter.remove(positon);
        lRecyclerViewAdapter.notifyDataSetChanged();
    }


    private void uploadMul(List<LocalMedia> selectList) {
        progress.show();
        ArrayList<String> thumbPictures = new ArrayList<>();
        for (LocalMedia localMedia : selectList) {
            if (!TextUtils.isEmpty(localMedia.getPath())) {
                thumbPictures.add(localMedia.getPath());
            }
        }
        new Thread(new UpdateImagesRun(thumbPictures)).start();
    }

    // 一次HTTP请求上传多张图片 + 各种参数
    private class UpdateImagesRun implements Runnable {
        private ArrayList<String> thumbPictures;

        // thumbPictures 是 List<压缩图路径>
        public UpdateImagesRun(ArrayList<String> thumbPictures) {
            this.thumbPictures = new ArrayList<String>();
            for (String string : thumbPictures) {
                if (!string.equals("" + BasePhotoGridActivity.PICTURE_UPDATE_ICON)) {
                    //去掉最后一个 +图片
                    this.thumbPictures.add(string);
                }
            }
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            StringResponse retMap = null;
            try {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("userid", MyInfoActivity.this.getUserID());
                Log.d("CCC", MyInfoActivity.this.getUserID());
                // 一次http请求将所有图片+参数上传
                retMap = JsonParser.getStringResponse2(HttpUtil.upload(data, thumbPictures, HttpUtil.IP + "user/myself"));
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (retMap != null) {
                handler.sendMessage(handler.obtainMessage(
                        BaseActivity.NETWORK_SUCCESS_DATA_RIGHT, retMap));
            } else {
                handler.sendEmptyMessage(BaseActivity.NETWORK_FAIL);
            }
        }
    }

}
