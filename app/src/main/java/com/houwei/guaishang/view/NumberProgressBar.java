package com.houwei.guaishang.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.houwei.guaishang.R;

import static com.houwei.guaishang.view.NumberProgressBar.ProgressTextVisibility.Invisible;
import static com.houwei.guaishang.view.NumberProgressBar.ProgressTextVisibility.Visible;


/**
 * Created by daimajia on 14-4-30.
 */
public class NumberProgressBar extends View {

    private final Bitmap mBitmapBuffer;
    private int mMaxProgress = 100;

    /**
     * Current progress, can not exceed the max progress.
     */
    private int mCurrentProgress = 0;

    /**
     * The progress area bar color.
     */
    private int mReachedBarColor;

    /**
     * The bar unreached area color.
     */
    private int mUnreachedBarColor;

    /**
     * The progress text color.
     */
    private int mTextColor;

    /**
     * The progress text size.
     */
    private float mTextSize;

    /**
     * The height of the reached area.
     */
    private float mReachedBarHeight;

    /**
     * The height of the unreached area.
     */
    private float mUnreachedBarHeight;

    /**
     * The suffix of the number.
     */
    private String mSuffix = "%";

    /**
     * The prefix.
     */
    private String mPrefix = "";


    private final int default_text_color = Color.rgb(255, 255, 255);
    private final int default_reached_color = Color.rgb(66, 145, 241);
    private final int default_unreached_color = Color.rgb(204, 204, 204);
    private final float default_progress_text_offset;
    private final float default_text_size;
    private final float default_reached_bar_height;
    private final float default_unreached_bar_height;

    /**
     * For save and restore instance of progressbar.
     */
    private static final String INSTANCE_STATE = "saved_instance";
    private static final String INSTANCE_TEXT_COLOR = "text_color";
    private static final String INSTANCE_TEXT_SIZE = "text_size";
    private static final String INSTANCE_REACHED_BAR_HEIGHT = "reached_bar_height";
    private static final String INSTANCE_REACHED_BAR_COLOR = "reached_bar_color";
    private static final String INSTANCE_UNREACHED_BAR_HEIGHT = "unreached_bar_height";
    private static final String INSTANCE_UNREACHED_BAR_COLOR = "unreached_bar_color";
    private static final String INSTANCE_MAX = "max";
    private static final String INSTANCE_PROGRESS = "progress";
    private static final String INSTANCE_SUFFIX = "suffix";
    private static final String INSTANCE_PREFIX = "prefix";
    private static final String INSTANCE_TEXT_VISIBILITY = "text_visibility";

    private static final int PROGRESS_TEXT_VISIBLE = 0;


    /**
     * The width of the text that to be drawn.
     */
    private float mDrawTextWidth;

    /**
     * The drawn text start.
     */
    private float mDrawTextStart;

    /**
     * The drawn text end.
     */
    private float mDrawTextEnd;

    /**
     * The text that to be drawn in onDraw().
     */
    private String mCurrentDrawText;

    /**
     * The Paint of the reached area.
     */
    private Paint mReachedBarPaint;
    /**
     * The Paint of the unreached area.
     */
    private Paint mUnreachedBarPaint;
    /**
     * The Paint of the progress text.
     */
    private Paint mTextPaint;

    /**
     * Unreached bar area to draw rect.
     */
    private RectF mUnreachedRectF = new RectF(0, 0, 0, 0);
    /**
     * Reached bar area rect.
     */
    private RectF mReachedRectF = new RectF(0, 0, 0, 0);

    /**
     * The progress text offset.
     */
    private float mOffset;

    /**
     * Determine if need to draw unreached area.
     */
    private boolean mDrawUnreachedBar = true;

    private boolean mDrawReachedBar = true;

    private boolean mIfDrawText = false;

    /**
     * Listener
     */
    private OnProgressBarListener mListener;
    private Paint bitmapPaint;

    /**分段颜色*/
    private static final int[] SECTION_COLORS = {Color.YELLOW,Color.parseColor("#ff8835"),Color.RED};

    public enum ProgressTextVisibility {
        Visible, Invisible
    }

    public NumberProgressBar(Context context) {
        this(context, null);
    }

    public NumberProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NumberProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //default_reached_bar_height = dp2px(2.8f);
        //default_unreached_bar_height = dp2px(2.3f);
        default_reached_bar_height = dp2px(5f);
        default_unreached_bar_height = dp2px(5f);
        default_text_size = sp2px(10);
        default_progress_text_offset = dp2px(3.0f);

        //load styled attributes.
        final TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.NumberProgressBar,
                defStyleAttr, 0);

        mReachedBarColor = attributes.getColor(R.styleable.NumberProgressBar_progress_reached_color, default_reached_color);
        mUnreachedBarColor = attributes.getColor(R.styleable.NumberProgressBar_progress_unreached_color, default_unreached_color);
        mTextColor = attributes.getColor(R.styleable.NumberProgressBar_progress_text_color, default_text_color);
        mTextSize = attributes.getDimension(R.styleable.NumberProgressBar_progress_text_size, default_text_size);

        mReachedBarHeight = attributes.getDimension(R.styleable.NumberProgressBar_progress_reached_bar_height, default_reached_bar_height);
        mUnreachedBarHeight = attributes.getDimension(R.styleable.NumberProgressBar_progress_unreached_bar_height, default_unreached_bar_height);
        mOffset = attributes.getDimension(R.styleable.NumberProgressBar_progress_text_offset, default_progress_text_offset);

        int textVisible = attributes.getInt(R.styleable.NumberProgressBar_progress_text_visibility, PROGRESS_TEXT_VISIBLE);
        if (textVisible != PROGRESS_TEXT_VISIBLE) {
            mIfDrawText = false;
        }
        setProgress(attributes.getInt(R.styleable.NumberProgressBar_progress_current, 0));
        setMax(attributes.getInt(R.styleable.NumberProgressBar_progress_max, 100));
        mBitmapBuffer = ((BitmapDrawable) getResources().getDrawable(R.mipmap.vernier)).getBitmap();
        attributes.recycle();
        initializePainters();
    }

    @Override
    protected int getSuggestedMinimumWidth() {
        return (int) mTextSize;
    }

    @Override
    protected int getSuggestedMinimumHeight() {
        return Math.max((int) mTextSize, Math.max((int) mReachedBarHeight, (int) mUnreachedBarHeight));
    }
    //private int mWidth,mHeight;
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measure(widthMeasureSpec, true), measure(heightMeasureSpec, false));
        /*// TODO Auto-generated method stub
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
//MeasureSpec.EXACTLY，精确尺寸
        if (widthSpecMode == MeasureSpec.EXACTLY || widthSpecMode == MeasureSpec.AT_MOST) {
            mWidth = widthSpecSize;
        } else {
            mWidth = 0;
        }
//MeasureSpec.AT_MOST，最大尺寸，只要不超过父控件允许的最大尺寸即可，MeasureSpec.UNSPECIFIED未指定尺寸
        if (heightSpecMode == MeasureSpec.AT_MOST || heightSpecMode == MeasureSpec.UNSPECIFIED) {
            mHeight = dp2px(20);
        } else {
            mHeight = heightSpecSize;
        }
//设置控件实际大小
        setMeasuredDimension(mWidth, mHeight);*/
    }

    private int measure(int measureSpec, boolean isWidth) {
        int result;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        int padding = isWidth ? getPaddingLeft() + getPaddingRight() : getPaddingTop() + getPaddingBottom();
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = isWidth ? getSuggestedMinimumWidth() : getSuggestedMinimumHeight();
            result += padding;
            if (mode == MeasureSpec.AT_MOST) {
                if (isWidth) {
                    result = Math.max(result, size);
                } else {
                    result = Math.min(result, size);
                }
            }
        }
        return result;
    }
    /**画笔*/
    private Paint mPaint;
    private Paint mPaint1;
    private Paint mPaint2;
    private LinearGradient mShader ; //线性渐变
    private int mFgColorStart = 0xffffe400 ;//进度条起始颜色
    private int mFgColorEnd = 0xffffe800 ;//进度条结束的颜色
    @Override
    protected void onDraw(Canvas canvas) {
        int round = dp2px(10)/2;
        if (mIfDrawText) {
            calculateDrawRectF();
        } else {
            calculateDrawRectFWithoutProgressText();
        }
        //mPaint = new Paint();
        //mPaint.setAntiAlias(true);//设置抗锯齿效果
        //mPaint.setColor(Color.BLACK);//设置画笔颜色
        if (mDrawReachedBar) {
            mPaint1 = new Paint();/*绘制圆角矩形，背景色为画笔颜色*/
            mPaint1.setAntiAlias(true);//设置抗锯齿效果
            mPaint1.setColor(Color.WHITE);//设置画笔颜色
//            Shader shader = new LinearGradient(mReachedRectF.left, mReachedRectF.top, mReachedRectF.width(),  mReachedRectF.top, Color.parseColor("#ff0000"),
//                    Color.parseColor("#ffd944"), Shader.TileMode.CLAMP);
//            mReachedBarPaint.setShader(shader);
            canvas.drawRect(mReachedRectF, mPaint1);
            //canvas.drawRoundRect(mReachedRectF, round, round, mReachedBarPaint);//


            //mShader = new LinearGradient(mReachedRectF.left,mReachedRectF.top,mReachedRectF.left,mReachedRectF.bottom,mFgColorStart,mFgColorEnd, Shader.TileMode.CLAMP) ;
            //mPaint.setShader(mShader) ;//进度条的颜色是渐变地
            //canvas.drawRect(mReachedRectF, mPaint);

            mPaint = new Paint();
            mPaint.setAntiAlias(true);//设置抗锯齿效果
            mPaint.setColor(Color.BLACK);//设置画笔颜色
            float section = mCurrentProgress / (float)mMaxProgress;
            Log.i("WXCH","section:"+section);
            if(section <= 1.0f/3.0f){
                if(section != 0.0f){
                    mPaint.setColor(SECTION_COLORS[0]);
                }else{
                    mPaint.setColor(Color.TRANSPARENT);
                }
            }else{
                int count = (section <= 1.0f/3.0f*2 ) ? 2 : 3;
                Log.i("WXCH","count:"+count);
                int[] colors = new int[count];
                System.arraycopy(SECTION_COLORS, 0, colors, 0, count);
                float[] positions = new float[count];
                if(count == 2){
                    positions[0] = 0.0f;
                    positions[1] = 1.0f-positions[0];
                }else{
                    positions[0] = 0.0f;
                    positions[1] = (mMaxProgress/3)/mCurrentProgress;
                    positions[2] = 1.0f-positions[0]*2;
                }
                positions[positions.length-1] = 1.0f;
                //LinearGradient shader = new LinearGradient(3, 3, (mWidth-3)*section, mHeight-3, colors,null, Shader.TileMode.MIRROR);
                //LinearGradient shader = new LinearGradient(mReachedRectF.left,mReachedRectF.top,mReachedRectF.left,mReachedRectF.bottom,mFgColorStart,mFgColorEnd, Shader.TileMode.MIRROR) ;
                LinearGradient shader = new LinearGradient(mReachedRectF.left,mReachedRectF.top,mReachedRectF.right+10,mReachedRectF.bottom,colors,null, Shader.TileMode.MIRROR) ;

                mPaint.setShader(shader);
            }
            //canvas.drawRect(mReachedRectF, mPaint);
            canvas.drawRoundRect(mReachedRectF, round, round, mPaint);//
        }




        if (mDrawUnreachedBar) {
            /*绘制圆角矩形，背景色为画笔颜色*/
            mPaint2 = new Paint();
//设置抗锯齿效果
            mPaint2.setAntiAlias(true);
//设置画笔颜色
            mPaint2.setColor(Color.WHITE);
            canvas.drawRect(mUnreachedRectF, mPaint2);
            canvas.drawRoundRect(mUnreachedRectF, round, round, mUnreachedBarPaint);//
        }


        if (mIfDrawText) {
            float textWidth=mTextPaint.measureText(mCurrentDrawText);
            float start=mDrawTextStart-(mBitmapBuffer.getWidth()/2)+textWidth/2;
            if(start<=(getWidth()-getPaddingLeft()-mBitmapBuffer.getWidth())){
                canvas.drawBitmap(mBitmapBuffer,start,mDrawTextEnd-(mBitmapBuffer.getHeight()/2)-textWidth/2,bitmapPaint);
                canvas.drawText(mCurrentDrawText, mDrawTextStart, mDrawTextEnd, mTextPaint);
            }else{
                start=getWidth()-getPaddingLeft()-mBitmapBuffer.getWidth();
                canvas.drawBitmap(mBitmapBuffer,start,mDrawTextEnd-(mBitmapBuffer.getHeight()/2)-textWidth/2,bitmapPaint);

                canvas.drawText(mCurrentDrawText, mDrawTextStart-((mBitmapBuffer.getWidth()/2)-(textWidth/2)), mDrawTextEnd, mTextPaint);
            }


        }



    }

    private void initializePainters() {
        mReachedBarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mReachedBarPaint.setColor(mReachedBarColor);

        mUnreachedBarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mUnreachedBarPaint.setColor(mUnreachedBarColor);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);


        bitmapPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
    }


    private void calculateDrawRectFWithoutProgressText() {
        mReachedRectF.left = getPaddingLeft();
        mReachedRectF.top = getHeight() / 2.0f - mReachedBarHeight / 2.0f;
        mReachedRectF.right = (getWidth() - getPaddingLeft() - getPaddingRight()) / (getMax() * 1.0f) * getProgress() + getPaddingLeft();
        mReachedRectF.bottom = getHeight() / 2.0f + mReachedBarHeight / 2.0f;

        mUnreachedRectF.left = mReachedRectF.right-10;
        mUnreachedRectF.right = getWidth() - getPaddingRight();
        mUnreachedRectF.top = getHeight() / 2.0f + -mUnreachedBarHeight / 2.0f;
        mUnreachedRectF.bottom = getHeight() / 2.0f + mUnreachedBarHeight / 2.0f;
    }

    private void calculateDrawRectF() {

        mCurrentDrawText =  getProgress()+"";
//        mCurrentDrawText = String.format("%d", getProgress() * 100 / getMax());
//        mCurrentDrawText = mPrefix + mCurrentDrawText + mSuffix;
        mDrawTextWidth = mTextPaint.measureText(mCurrentDrawText);
        if (getProgress() == 0) {
            mDrawReachedBar = false;
            mDrawTextStart = getPaddingLeft();
        } else {
            mDrawReachedBar = true;
            mReachedRectF.left = getPaddingLeft();
            mReachedRectF.top = getHeight() / 2.0f - mReachedBarHeight / 2.0f;
            mReachedRectF.right = (getWidth() - getPaddingLeft() - getPaddingRight()) / (getMax() * 1.0f) * getProgress() - mOffset + getPaddingLeft();
            mReachedRectF.bottom = getHeight() / 2.0f + mReachedBarHeight / 2.0f;
            mDrawTextStart = (mReachedRectF.right + mOffset);
        }

        mDrawTextEnd = (int) ((getHeight() / 2.0f) - ((mTextPaint.descent() + mTextPaint.ascent()) / 2.0f));

        if ((mDrawTextStart + mDrawTextWidth) >= getWidth() - getPaddingRight()) {
            mDrawTextStart = getWidth() - getPaddingRight() - mDrawTextWidth;
            mReachedRectF.right = mDrawTextStart - mOffset;
        }

        float unreachedBarStart = mDrawTextStart + mDrawTextWidth + mOffset;
        if (unreachedBarStart >= getWidth() - getPaddingRight()) {
            mDrawUnreachedBar = false;
        } else {
            mDrawUnreachedBar = true;
            mUnreachedRectF.left = unreachedBarStart;
            mUnreachedRectF.right = getWidth() - getPaddingRight();
            mUnreachedRectF.top = getHeight() / 2.0f + -mUnreachedBarHeight / 2.0f;
            mUnreachedRectF.bottom = getHeight() / 2.0f + mUnreachedBarHeight / 2.0f;
        }
    }

    /**
     * Get progress text color.
     *
     * @return progress text color.
     */
    public int getTextColor() {
        return mTextColor;
    }

    /**
     * Get progress text size.
     *
     * @return progress text size.
     */
    public float getProgressTextSize() {
        return mTextSize;
    }

    public int getUnreachedBarColor() {
        return mUnreachedBarColor;
    }

    public int getReachedBarColor() {
        return mReachedBarColor;
    }

    public int getProgress() {
        return mCurrentProgress;
    }

    public int getMax() {
        return mMaxProgress;
    }

    public float getReachedBarHeight() {
        return mReachedBarHeight;
    }

    public float getUnreachedBarHeight() {
        return mUnreachedBarHeight;
    }

    public void setProgressTextSize(float textSize) {
        this.mTextSize = textSize;
        mTextPaint.setTextSize(mTextSize);
        invalidate();
    }

    public void setProgressTextColor(int textColor) {
        this.mTextColor = textColor;
        mTextPaint.setColor(mTextColor);
        invalidate();
    }

    public void setUnreachedBarColor(int barColor) {
        this.mUnreachedBarColor = barColor;
        mUnreachedBarPaint.setColor(mUnreachedBarColor);
        invalidate();
    }

    public void setReachedBarColor(int progressColor) {
        this.mReachedBarColor = progressColor;
        mReachedBarPaint.setColor(mReachedBarColor);
        invalidate();
    }

    public void setReachedBarHeight(float height) {
        mReachedBarHeight = height;
    }

    public void setUnreachedBarHeight(float height) {
        mUnreachedBarHeight = height;
    }

    public void setMax(int maxProgress) {
        if (maxProgress > 0) {
            this.mMaxProgress = maxProgress;
            invalidate();
        }
    }

    public void setSuffix(String suffix) {
        if (suffix == null) {
            mSuffix = "";
        } else {
            mSuffix = suffix;
        }
    }

    public String getSuffix() {
        return mSuffix;
    }

    public void setPrefix(String prefix) {
        if (prefix == null)
            mPrefix = "";
        else {
            mPrefix = prefix;
        }
    }

    public String getPrefix() {
        return mPrefix;
    }

    public void incrementProgressBy(int by) {
        if (by > 0) {
            setProgress(getProgress() + by);
        }

        if(mListener != null){
            mListener.onProgressChange(getProgress(), getMax());
        }
    }

    public void setProgress(int progress) {
        if (progress <= getMax() && progress >= 0) {
            this.mCurrentProgress = progress;
            invalidate();
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState());
        bundle.putInt(INSTANCE_TEXT_COLOR, getTextColor());
        bundle.putFloat(INSTANCE_TEXT_SIZE, getProgressTextSize());
        bundle.putFloat(INSTANCE_REACHED_BAR_HEIGHT, getReachedBarHeight());
        bundle.putFloat(INSTANCE_UNREACHED_BAR_HEIGHT, getUnreachedBarHeight());
        bundle.putInt(INSTANCE_REACHED_BAR_COLOR, getReachedBarColor());
        bundle.putInt(INSTANCE_UNREACHED_BAR_COLOR, getUnreachedBarColor());
        bundle.putInt(INSTANCE_MAX, getMax());
        bundle.putInt(INSTANCE_PROGRESS, getProgress());
        bundle.putString(INSTANCE_SUFFIX, getSuffix());
        bundle.putString(INSTANCE_PREFIX, getPrefix());
        bundle.putBoolean(INSTANCE_TEXT_VISIBILITY, getProgressTextVisibility());
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            final Bundle bundle = (Bundle) state;
            mTextColor = bundle.getInt(INSTANCE_TEXT_COLOR);
            mTextSize = bundle.getFloat(INSTANCE_TEXT_SIZE);
            mReachedBarHeight = bundle.getFloat(INSTANCE_REACHED_BAR_HEIGHT);
            mUnreachedBarHeight = bundle.getFloat(INSTANCE_UNREACHED_BAR_HEIGHT);
            mReachedBarColor = bundle.getInt(INSTANCE_REACHED_BAR_COLOR);
            mUnreachedBarColor = bundle.getInt(INSTANCE_UNREACHED_BAR_COLOR);
            initializePainters();
            setMax(bundle.getInt(INSTANCE_MAX));
            setProgress(bundle.getInt(INSTANCE_PROGRESS));
            setPrefix(bundle.getString(INSTANCE_PREFIX));
            setSuffix(bundle.getString(INSTANCE_SUFFIX));
            setProgressTextVisibility(bundle.getBoolean(INSTANCE_TEXT_VISIBILITY) ? Visible : Invisible);
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATE));
            return;
        }
        super.onRestoreInstanceState(state);
    }

    public int dp2px(int dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int)(dp * scale + 0.5f);
    }
    public float dp2px(float dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }
    public float sp2px(float sp) {
        final float scale = getResources().getDisplayMetrics().scaledDensity;
        return sp * scale;
    }

    public void setProgressTextVisibility(ProgressTextVisibility visibility) {
        mIfDrawText = visibility == Visible;
        invalidate();
    }

    public boolean getProgressTextVisibility() {
        return mIfDrawText;
    }

    public void setOnProgressBarListener(OnProgressBarListener listener){
        mListener = listener;
    }
}
