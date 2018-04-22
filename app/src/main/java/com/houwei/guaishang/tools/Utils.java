package com.houwei.guaishang.tools;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;

/**
 * Created by chrc on 2018/4/21.
 */

public class Utils {

    public static String getImei(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            String imei = telephonyManager.getDeviceId();
            return imei;
        } else {

        }
        return "";
    }
}
