package com.houwei.guaishang.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import com.houwei.guaishang.util.DeviceUtils;


/**
 * Created by lei on 2018/4/27.
 */
public class ProgressView extends View {


    private final int mLeftLineBeginColor = Color.parseColor("#f7a167");//左边的起始颜色
    private final int getmLeftLineAfterColor = Color.parseColor("#f7a167");//左边进度的最终颜色
    private final int mRightBeginColor = Color.parseColor("#f7a167");//右边的起始颜色
    private final int mRightAfterColor = Color.parseColor("#fb8379");//右边的最终颜色

    private final int mNormalPaintColor = Color.parseColor("#ededed");
    private final int mSelectedPaintColor = Color.parseColor("#ffff4444");
    private final int mTextColor = Color.parseColor("#666666");

    private  int progress ;//进度（分4步）
    private int status;

    private Paint mNormalCirclePaint;
    private Paint mSelectedCirclePaint;
    private Paint mLeftLinePaint;
    private Paint mRightLinePaint;
    private Paint mTextPaint;

    public ProgressView(Context context) {
        super(context);
        initPaint(context);
    }

    public ProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint(context);
    }

    public ProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint(context);
    }




    private void initPaint(Context context){
        mNormalCirclePaint = new Paint();
        mNormalCirclePaint.setColor(mNormalPaintColor);
        mNormalCirclePaint.setAntiAlias(true);

        mSelectedCirclePaint = new Paint();
        mSelectedCirclePaint.setColor(mSelectedPaintColor);
        mSelectedCirclePaint.setAntiAlias(true);

        mLeftLinePaint = new Paint();
        mLeftLinePaint.setStyle(Paint.Style.FILL);


        mRightLinePaint = new Paint();
        mRightLinePaint.setStyle(Paint.Style.FILL);

        mTextPaint = new Paint();
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(DeviceUtils.dip2px(context,7));
//        mTextPaint.setTextAlign(Paint.Align.CENTER);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


//        //如果是0或者100 纯色的进度条不需要进度条的过度区间
//        if (progress == 100 || progress == 0) cutLength = 0;
        //首先需要画个圆

        //绘制进度
        drawRightLine(canvas);
        drawMiddleCircle(canvas);
        //绘制左边进度
        drawLeftLine(canvas);

        drawText(canvas);


    }

    private final int radius = DeviceUtils.dip2px(getContext(),10);//圆的半径
//    private final int mLeftComplementLength = DeviceUtils.dip2px(getContext(),2);//左补足的长度  使得进度条能嵌入圆内
//    private final int mRightComplementLenght = DeviceUtils.dip2px(getContext(),2);//右补足的长度  使得进度条能嵌入圆内
//    private int cutLength =DeviceUtils.dip2px(getContext(),4);//2个颜色之间需要过度，所以目前定个 过度区域
    private final int mLineHeight = DeviceUtils.dip2px(getContext(),3);

    private final int mTextPanding = DeviceUtils.dip2px(getContext(),3);

    private void drawText(Canvas canvas){
        float textWidths = (mTextPaint.measureText("打款"))/2;
        canvas.drawText("打款",radius-textWidths,viewHeight/2+mTextPanding,mTextPaint);

        canvas.drawText("发货",viewWitdth-radius-textWidths,viewHeight/2+mTextPanding,mTextPaint);
        canvas.drawText("收货",radius+(viewWitdth-4*radius)/3-textWidths,viewHeight/2+mTextPanding,mTextPaint);
        canvas.drawText("评价",radius+(viewWitdth-4*radius)*2/3-textWidths,viewHeight/2+mTextPanding,mTextPaint);
    }

    private void drawMiddleCircle(Canvas canvas){
        switch (status){
            case 1:
                canvas.drawCircle(radius,viewHeight/2,radius, mSelectedCirclePaint);
                canvas.drawCircle(radius+(viewWitdth-4*radius)/3,viewHeight/2,radius, mNormalCirclePaint);
                canvas.drawCircle(radius+(viewWitdth-4*radius)*2/3,viewHeight/2,radius, mNormalCirclePaint);
                canvas.drawCircle(viewWitdth-radius,viewHeight/2,radius, mNormalCirclePaint);
                break;
            case 2:
                canvas.drawCircle(radius,viewHeight/2,radius, mNormalCirclePaint);
                canvas.drawCircle(radius+(viewWitdth-4*radius)/3,viewHeight/2,radius, mSelectedCirclePaint);
                canvas.drawCircle(radius+(viewWitdth-4*radius)*2/3,viewHeight/2,radius, mNormalCirclePaint);
                canvas.drawCircle(viewWitdth-radius,viewHeight/2,radius, mNormalCirclePaint);
                break;
            case 3:
                canvas.drawCircle(radius,viewHeight/2,radius, mNormalCirclePaint);
                canvas.drawCircle(radius+(viewWitdth-4*radius)/3,viewHeight/2,radius, mNormalCirclePaint);
                canvas.drawCircle(radius+(viewWitdth-4*radius)*2/3,viewHeight/2,radius, mSelectedCirclePaint);
                canvas.drawCircle(viewWitdth-radius,viewHeight/2,radius, mNormalCirclePaint);
                break;
            case 4:
                canvas.drawCircle(radius,viewHeight/2,radius, mNormalCirclePaint);
                canvas.drawCircle(radius+(viewWitdth-4*radius)/3,viewHeight/2,radius, mNormalCirclePaint);
                canvas.drawCircle(radius+(viewWitdth-4*radius)*2/3,viewHeight/2,radius, mNormalCirclePaint);
                canvas.drawCircle(viewWitdth-radius,viewHeight/2,radius, mSelectedCirclePaint);
                break;
            default:
                canvas.drawCircle(radius,viewHeight/2,radius, mNormalCirclePaint);
                canvas.drawCircle(viewWitdth-radius,viewHeight/2,radius, mNormalCirclePaint);
                canvas.drawCircle(radius+(viewWitdth-4*radius)/3,viewHeight/2,radius, mNormalCirclePaint);
                canvas.drawCircle(radius+(viewWitdth-4*radius)*2/3,viewHeight/2,radius, mNormalCirclePaint);
                break;
        }
    }

    private void drawLeftLine(Canvas canvas){
        int progressWidth = (viewWitdth-4*radius)*progress/100;
        int left = radius * 2 ;
        int top = viewHeight/2-mLineHeight/2;
        int right = progressWidth+left;
        int bottom = viewHeight/2 +mLineHeight/2;


        Path path = new Path();
        path.moveTo(left,top);
        path.lineTo(left,bottom);
        path.lineTo(right,bottom);
        path.lineTo(right,top);

        LinearGradient linearGradient = new LinearGradient(left,top,right,bottom,mLeftLineBeginColor,getmLeftLineAfterColor, Shader.TileMode.CLAMP);
        mLeftLinePaint.setShader(linearGradient);

        canvas.drawPath(path,mLeftLinePaint);
//        canvas.drawRect(rect,mLeftLinePaint);
    }

    private void drawRightLine(Canvas canvas){
        int progressWidthTop = (viewWitdth-4*radius)*(100-progress)/100;

        int left = viewWitdth-2*radius;
        int top = viewHeight/2 - mLineHeight/2;
        int right = left-progressWidthTop;
        int bottom = viewHeight/2 + mLineHeight/2;

        LinearGradient linearGradient = new LinearGradient(left,top,right,bottom,mRightBeginColor,mRightAfterColor, Shader.TileMode.CLAMP);

        mRightLinePaint.setShader(linearGradient);

        Path path = new Path();
        path.moveTo(right,top);
        path.lineTo(right,bottom);
        path.lineTo(left,bottom);
        path.lineTo(left,top);
        canvas.drawPath(path,mRightLinePaint);
//        canvas.drawRect(rect,mRightLinePaint);
    }
    int viewWitdth;
    int viewHeight;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        viewWitdth = measureWidth(widthMeasureSpec);
        viewHeight = measureHeight(heightMeasureSpec);
        setMeasuredDimension(viewWitdth,viewHeight);
//        Log.d("lei","宽度-->"+viewWitdth+"高度---->"+viewHeight);
    }

    private int measureWidth(int measureSpec) {
        float result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

//        LogUtil.d("lei","测量宽度-->mode:"+specMode+"测量尺寸"+specSize);
        if ((specMode == MeasureSpec.EXACTLY)) {
            result = specSize;
        } else {
            result = getPaddingLeft() + getPaddingRight()+ DeviceUtils.dip2px(getContext(),100);//最小100dp
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
//        LogUtil.d("lei","最终测量宽度"+result);
        return (int) Math.ceil(result);
    }

    private int measureHeight(int measureSpec) {
        float result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
//        Log.d("lei", "measureHeight: "+specMode+"size---->"+specSize);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = getPaddingTop() + getPaddingBottom()+2 * radius;
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
//            Log.d("lei","最终测量高度--"+result);
        }
        return (int) Math.ceil(result);
    }



    public void setProgress(int len){
        switch (len){
            case 10:
                progress = 25;
                status = 1;
                break;
            case 11:
                progress = 50;
                status = 2;
                break;
            case 12:
                progress = 75;
                status = 3;
                break;
            case 13:
                progress = 100;
                status = 4;
                break;
        }
        this.progress = len;
        invalidate();
    }


}
