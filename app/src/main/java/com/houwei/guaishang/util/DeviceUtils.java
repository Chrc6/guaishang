package com.houwei.guaishang.util;

import android.content.Context;
import android.telephony.TelephonyManager;

import static android.content.Context.TELEPHONY_SERVICE;


public class DeviceUtils {
    public synchronized static String getid(Context context) {
        TelephonyManager TelephonyMgr =   (TelephonyManager)context.getSystemService(TELEPHONY_SERVICE);
        String ID= TelephonyMgr.getDeviceId();
        return ID;
    }

//    dp--px
    public static final int dip2px(Context context,float dpValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return(int)(dpValue * scale+0.5f);
    }

    public static final int getScreenWid(Context context){
        int width = context.getResources().getDisplayMetrics().widthPixels;
        return width;
    }
}

