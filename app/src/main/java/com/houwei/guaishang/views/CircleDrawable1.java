package com.houwei.guaishang.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

import com.easemob.util.DensityUtil;

/**
 * Created With Android Studio
 * User @47
 * Date 2014-07-28
 * Time 0:32
 */
public class CircleDrawable1 extends Drawable {


    protected final Paint paint;

    protected final int margin;
    protected  BitmapShader bitmapShader;
    protected float radius;
    protected Bitmap oBitmap;//原图

    public CircleDrawable1(Bitmap bitmap) {
        this(bitmap, 0, null);
    }

    Context context;

    public CircleDrawable1(Bitmap bitmap, int margin, Context context) {
        System.gc();
        this.margin = margin;
        this.oBitmap = bitmap;
        bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(bitmapShader);
        //paint.setColor(Color.parseColor("#0E3464"));
        this.context = context;
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        computeBitmapShaderSize();
        computeRadius();

    }

    @Override
    public void draw(Canvas canvas) {
        Rect bounds = getBounds();//画一个圆圈
//        float[] outerR = new float[] { 20, 20, 20, 20,0, 0, 0, 0 };
//        RoundRectShape rr = new RoundRectShape(outerR, null, null);
//        ShapeDrawable drawable = new ShapeDrawable(rr);

//		float[] outerR = new float[] { 120, 120, 120, 120, 120, 120, 120, 120 };
//		RoundRectShape rectShape=new RoundRectShape(outerR, null, null);
//		ShapeDrawable mDrawables= new ShapeDrawable(rectShape);
//		mDrawables.draw(canvas);
//        canvas.drawRect(bounds,paint);
//        canvas.drawColor(paint.getColor(), PorterDuff.Mode.MULTIPLY);
        int i = DensityUtil.dip2px(context, 10);
        canvas.drawRoundRect(new RectF(bounds.left, bounds.top, bounds.right, bounds.bottom), i, i, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        paint.setShader(bitmapShader);
        canvas.drawRect(bounds.left, bounds.top + i, bounds.right, bounds.bottom, paint);
        oBitmap=null;
        bitmapShader=null;
//        canvas.drawCircle(bounds.width() / 2F,bounds.height() / 2F,radius,paint);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        paint.setColorFilter(cf);
    }


    /**
     * 计算Bitmap shader 大小
     */
    public void computeBitmapShaderSize() {
        Rect bounds = getBounds();
        if (bounds == null) return;
        //选择缩放比较多的缩放，这样图片就不会有图片拉伸失衡
        Matrix matrix = new Matrix();
        float scaleX = bounds.width() / (float)oBitmap.getWidth();
        float scaleY = bounds.height() / (float)oBitmap.getHeight();
        float scale = scaleX > scaleY ? scaleX : scaleY;
        matrix.postScale(scale,scale);
        bitmapShader.setLocalMatrix(matrix);
    }

    /**
     * 计算半径的大小
     */
    public void computeRadius() {
        Rect bounds = getBounds();
        radius = bounds.width() < bounds.height() ?
                bounds.width() / 2F - margin :
                bounds.height() / 2F - margin;
    }
}
