package com.houwei.guaishang.preview;

import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.houwei.guaishang.R;
import com.houwei.guaishang.activity.BaseActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import me.relex.photodraweeview.OnPhotoTapListener;
import me.relex.photodraweeview.PhotoDraweeView;

/**
 * Created by lenovo on 2018/4/21.
 */

public class PreviewActivity extends BaseActivity {

    private MultiTouchViewPager VPager;
    protected ArrayList<String> mUrlList = new ArrayList<>();//图片的URL列表
    protected int mLocation = 0;//刚开始的时候指向第几页
    private final ArrayList<PhotoDraweeView> mPhotoList = new ArrayList<>();//viewpager的每个页面
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        VPager = (MultiTouchViewPager) findViewById(R.id.view_pager);
        parseIntent();
//        ImageLoader.getInstance().displayImage(mUrlList.get(0), VPager);
        initPhotoViews();
        if (mUrlList.size() > 0) {
            loadImage();//初始化viewpager并加载图片
        }
    }

    private void parseIntent(){
        mUrlList = getIntent().getStringArrayListExtra("list");
    }

    /*初始化views*/
    private void initPhotoViews() {
        if (mUrlList.size() <= 0)
            return;
        mPhotoList.clear();
        for (int i = 0; i < mUrlList.size(); i++) {
            PhotoDraweeView photoview = new PhotoDraweeView(this);
            mPhotoList.add(i, photoview);
        }
    }

    /*初始化viewpager并加载图片*/
    private void loadImage() {
        MyPageAdapter adapter = new MyPageAdapter(mUrlList);
        VPager.setAdapter(adapter);
        //设置PageChangeListener一定要在setAdapter之后
        VPager.addOnPageChangeListener(mPageChangeListener);
        VPager.setPageMargin(10);
        VPager.setCurrentItem(mLocation, false);
    }
    private final ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() {

        public void onPageSelected(int position) {//当进入一个页面后，更换标题，加载图片
            mLocation = position;
            initPhotoIfNeed(mPhotoList.get(position), position);
        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        public void onPageScrollStateChanged(int arg0) {
        }
    };
    class MyPageAdapter extends PagerAdapter {

        private final int size;

        public MyPageAdapter(ArrayList<String> list) {
            size = list == null ? 0 : list.size();
        }

        public int getCount() {
            return size;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
           final PhotoDraweeView photoDraweeView = mPhotoList.get(position);
            PipelineDraweeControllerBuilder controller = Fresco.newDraweeControllerBuilder();
            Uri heighResUri = null;
            if (mUrlList.size() >= position && mUrlList.get(position) != null) {
                heighResUri = Uri.parse(mUrlList.get(position));
            }
//        controller.setUri(ImageRequest.fromUri(""));
            if (heighResUri != null) {
                controller.setImageRequest(ImageRequest.fromUri(heighResUri));
            }

            controller.setOldController(photoDraweeView.getController());
            controller.setControllerListener(new FormaxFrescoBaseControllerListener<ImageInfo>(heighResUri) {
                @Override
                public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                    super.onFinalImageSet(id, imageInfo, animatable);
                    if (imageInfo == null) {
                        return;
                    }
                    photoDraweeView.update(imageInfo.getWidth(), imageInfo.getHeight());
                }
            });
            photoDraweeView.setController(controller.build());

            //加载刚进来时对应的图片
            if (mLocation == position)
                initPhotoIfNeed(photoDraweeView, position);

            container.addView(photoDraweeView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            return photoDraweeView;
        }

        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }
    }


    /*加载图片*/
    private void initPhotoIfNeed(final PhotoDraweeView photoDraweeView, int position) {
        if (photoDraweeView == null || photoDraweeView.getController() != null)//当已经有Controller后就不要管了
            return;
        //设置进度条
//        photoDraweeView.getHierarchy().setProgressBarImage(new CircleProgressDrawable());
        //开始加载图片
        PipelineDraweeControllerBuilder controller = Fresco.newDraweeControllerBuilder();
        Uri heighResUri = null;
        if (mUrlList.size() >= position && mUrlList.get(position) != null) {
            heighResUri = Uri.parse(mUrlList.get(position));
        }
//        controller.setUri(ImageRequest.fromUri(""));
        if (heighResUri != null) {
            controller.setImageRequest(ImageRequest.fromUri(heighResUri));
        }

        controller.setOldController(photoDraweeView.getController());
        controller.setControllerListener(new FormaxFrescoBaseControllerListener<ImageInfo>(heighResUri) {
            @Override
            public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                super.onFinalImageSet(id, imageInfo, animatable);
                if (imageInfo == null) {
                    return;
                }
                photoDraweeView.update(imageInfo.getWidth(), imageInfo.getHeight());

            }
        });
        photoDraweeView.setController(controller.build());
    }
}
