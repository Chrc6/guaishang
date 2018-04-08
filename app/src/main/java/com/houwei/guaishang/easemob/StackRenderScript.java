package com.houwei.guaishang.easemob;


import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;


public class StackRenderScript {

    private static volatile StackRenderScript INSTANCE = null;

    public static StackRenderScript getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (StackRenderScript.class) {
                if (INSTANCE == null) {
                    INSTANCE = new StackRenderScript(context.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private StackRenderScript(Context context){
        mRenderScript = RenderScript.create(context);
        mBlurScript = ScriptIntrinsicBlur.create(mRenderScript, Element.U8_4(mRenderScript));
    }

    private RenderScript mRenderScript;
    private ScriptIntrinsicBlur mBlurScript;
    private Allocation mBlurInput, mBlurOutput;

    /**
     *
     * @param bitmap
     * @param radius 0~25
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public Bitmap blur(Bitmap bitmap, int radius){
        if (radius < 1) {
            return (null);
        }

        // Return this none blur
        if (radius == 1) {
            return bitmap;
        }

        if (radius > 25) {
            radius = 25;
        }

        mBlurScript.setRadius(radius);

        mBlurInput = Allocation.createFromBitmap(mRenderScript, bitmap,
                Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
        mBlurOutput = Allocation.createTyped(mRenderScript, mBlurInput.getType());

        //mBlurInput.copyFrom(bitmap);
        mBlurScript.setInput(mBlurInput);
        mBlurScript.forEach(mBlurOutput);
        mBlurOutput.copyTo(bitmap);

        return (bitmap);
    }
}
