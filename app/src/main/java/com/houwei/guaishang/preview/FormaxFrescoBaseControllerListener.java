package com.houwei.guaishang.preview;

import android.net.Uri;

import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.facebook.imagepipeline.request.ImageRequest;

/**
 * Created by cai on 2017-01-06. <br/>
 */
public class FormaxFrescoBaseControllerListener<INFO> extends BaseControllerListener<INFO> {
    private Uri mUri;

    public FormaxFrescoBaseControllerListener(Uri uri) {
        mUri = uri;
    }

    public FormaxFrescoBaseControllerListener(ImageRequest imageRequest) {
        mUri = imageRequest.getSourceUri();
    }

    @Override
    public void onFailure(String id, Throwable throwable) {
        super.onFailure(id, throwable);
//        Log.i("jie","load image fail! id="+id+ " throwable="+throwable,throwable);
        //图片加载失败的话, 清掉对应的缓存
        if(mUri != null){
            ImagePipelineFactory.getInstance().getImagePipeline().evictFromCache(mUri);
        }
    }
}
