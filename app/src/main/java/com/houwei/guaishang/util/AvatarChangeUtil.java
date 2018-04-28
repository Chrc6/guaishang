package com.houwei.guaishang.util;

import com.houwei.guaishang.tools.HttpUtil;

/**
 * Created by lenovo on 2018/4/28.
 */

public class AvatarChangeUtil {

    public static String findOriginalUrl(String original) {
        if (original==null || original.startsWith("http") || original.equals("")) {
            //为null 或者 “” 或者全路径。直接返回原始图片
            return original;
        } else {
            return HttpUtil.IP_NOAPI+original;
        }
    }
}
