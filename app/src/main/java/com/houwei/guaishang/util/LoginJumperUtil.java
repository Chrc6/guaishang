package com.houwei.guaishang.util;

import android.content.Context;
import android.content.Intent;

import com.houwei.guaishang.activity.UserLoginActivity;
import com.houwei.guaishang.activity.UserRegMobileActivity;

/**
 * Created by lenovo on 2018/4/26.
 */

public class LoginJumperUtil {

    public static void jumperLogin(Context context){
//        context.startActivity(new Intent(context, UserLoginActivity.class));
        Intent intent = new Intent(context, UserRegMobileActivity.class);
        intent.putExtra(UserRegMobileActivity.PAGE_TYPE, UserRegMobileActivity.PAGE_TYPE_LOGIN);
        context.startActivity(intent);
    }

}
