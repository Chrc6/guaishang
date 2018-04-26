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
}

