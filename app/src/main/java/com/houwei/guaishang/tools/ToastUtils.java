package com.houwei.guaishang.tools;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * Created by Administrator on 2017/3/22 0022.
 */

public class ToastUtils {
    private static Toast mToast;
    private static long oneTime = 0;
    private static long twoTime = 0;
    private static String oldMsg;

    /**
     * 长时间弹出消息提示
     *
     * @param context
     * @param msg
     */
    public static void toastForLong(Context context, String msg) {
        if (context == null)
            return;
        if (mToast == null) {
            mToast = Toast.makeText(context.getApplicationContext(), msg, Toast.LENGTH_LONG);
            mToast.show();
            oneTime = System.currentTimeMillis();
        } else {
            twoTime = System.currentTimeMillis();
            if (!TextUtils.isEmpty(msg) && msg.equals(oldMsg)) {
                if (twoTime - oneTime > Toast.LENGTH_LONG) {
                    mToast.show();
                }
            } else {
                if (TextUtils.isEmpty(msg)) {
                    msg = "";
                }
                oldMsg = msg;
                mToast.setText(msg);
                mToast.show();
            }
        }
        oneTime = twoTime;
//		}
    }

    /**
     * 短时间弹出消息提示
     *
     * @param context
     * @param msg
     */
    public static void toastForShort(Context context, String msg) {
        if (context == null)
            return;
        if (mToast == null) {
            mToast = Toast.makeText(context.getApplicationContext(), msg, Toast.LENGTH_SHORT);
            mToast.show();
            oneTime = System.currentTimeMillis();
        } else {
            twoTime = System.currentTimeMillis();
            if (!TextUtils.isEmpty(msg) && msg.equals(oldMsg)) {
                if (twoTime - oneTime > Toast.LENGTH_SHORT) {
                    mToast.show();
                }
            } else {
                if (TextUtils.isEmpty(msg)) {
                    msg = "";
                }
                oldMsg = msg;
                mToast.setText(msg);
                mToast.show();
            }
        }
        oneTime = twoTime;
        return;
    }
}
