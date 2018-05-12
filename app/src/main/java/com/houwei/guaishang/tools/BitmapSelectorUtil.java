package com.houwei.guaishang.tools;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.luck.picture.lib.PictureSelectionModel;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.compress.Luban;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;

/**
 * Created by *** on 2018/4/21.
 */

public class BitmapSelectorUtil {

    public static void gotoPic(Fragment fragment, int max, int spancount, boolean crop, boolean cropCircle, int requesCode) {
        // 进入相册 以下是例子：用不到的api可以不写
//        if(!selectList3.isEmpty()){
//            for(LocalMedia bean: selectList3){
//                if(TextUtils.isEmpty(bean.getPath())){
//                    selectList3.remove(bean);
//                }
//            }
//        }
        PictureSelectionModel pictureModel = PictureSelector.create(fragment)
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

    public static void gotoPic(Activity activity, int max, int spancount, boolean crop, boolean cropCircle, int requesCode) {
        PictureSelectionModel pictureModel = PictureSelector.create(activity)
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


    public static void gotoCamer(Fragment fragment, int requesCode) {
        //包括裁剪和压缩后的缓存，要在上传成功后调用，注意：需要系统sd卡权限
//        PictureFileUtils.deleteCacheDirFile(MainActivity.this);
        PictureSelector.create(fragment)
                .openCamera(PictureMimeType.ofImage())
//                .openGallery(PictureMimeType.ofImage())
                .forResult(requesCode);
    }
}
