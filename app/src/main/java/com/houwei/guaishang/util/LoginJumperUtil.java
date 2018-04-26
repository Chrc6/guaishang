package com.houwei.guaishang.util;

import android.content.Context;
import android.content.Intent;

import com.houwei.guaishang.activity.UserLoginActivity;

/**
 * Created by lenovo on 2018/4/26.
 */

public class LoginJumperUtil {

    public static void jumperLogin(Context context){
        context.startActivity(new Intent(context, UserLoginActivity.class));
    }

}
