package com.houwei.guaishang.sp;

import android.net.http.SslCertificate;
import android.os.Looper;
import android.text.TextUtils;

import java.io.Serializable;

/**
 * Created by lenovo on 2018/4/26.
 * 具体的sp操作
 */

public class DataStorage {

    public static final String User_info = "UserInfo";

    /**
     * 将一个Serializable对象保存到sp中. <br/>
     * 如果参数key为空, 则直接返回, 不进行任何操作.<br/>
     * 如果参数obj为null, 则会直接删掉sp中对应的key的条目.
     * <p/>
     */
    public static <T extends Serializable> void saveComObject(final String key, final T obj) {

            if (TextUtils.isEmpty(key)) {
                return;
            }

            if (null == obj) {
                DataStorageBase.remove(key);
                return;
            }
            try {
                String value = ObjectSerializer.serialize(obj);
                DataStorageBase.putString(key, value);
            } catch (Exception e) {

        }
    }


    /**
     * 从sp中取出key所对应的Serializable对象.
     */
    public static Serializable getComObject(String key) {
        if (TextUtils.isEmpty(key)) {
            return null;
        }

        String value = DataStorageBase.getString(key, "");
        if (TextUtils.isEmpty(value)) {
            return null;
        }

        try {
            return (Serializable) ObjectSerializer.deserialize(value);
        } catch (Exception e) {
            return null;
        }
    }

}
