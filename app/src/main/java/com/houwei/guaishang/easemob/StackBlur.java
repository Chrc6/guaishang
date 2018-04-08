package com.houwei.guaishang.easemob;

import android.content.Context;
import android.graphics.Bitmap;
/**
 * This is blur image class
 * Use {@link StackNative} fast blur bitmap
 * Blur arithmetic is StackBlur
 */
final public class StackBlur  {

    private static Bitmap buildBitmap(Bitmap bitmap, boolean canReuseInBitmap) {
        // If can reuse in bitmap return this or copy
        Bitmap rBitmap;
        if (canReuseInBitmap) {
            rBitmap = bitmap;
        } else {
            rBitmap = bitmap.copy(bitmap.getConfig(), true);
        }
        return (rBitmap);
    }

    /**
     * StackBlur By RenderScript（Just used for API level 17）
     *
     * @param original         Original Image
     * @param radius           Blur radius 0~25
     * @param canReuseInBitmap Can reuse In original Bitmap
     * @return Image Bitmap
     */
    public static Bitmap blurRenderScript(Context context,Bitmap original, int radius, boolean canReuseInBitmap) {
        if (radius < 1) {
            return (null);
        }

        Bitmap bitmap = buildBitmap(original, canReuseInBitmap);

        if (radius == 1) {
            return bitmap;
        }

        StackRenderScript.getInstance(context).blur(bitmap,radius);

        return (bitmap);
    }
}
