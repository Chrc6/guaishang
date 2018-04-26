package com.houwei.guaishang.sp;

/**
 * Created by lenovo on 2018/4/26.
 */

public class UserUtil {

    public static UserInfo getUserInfo(){
        return (UserInfo) DataStorage.getComObject(DataStorage.User_info);
    }

    public static void setUserInfo(UserInfo userInfo){
        DataStorage.saveComObject(DataStorage.User_info,userInfo);
    }

    public static boolean isInLoginStata(){
        if (getUserInfo() == null){
            return false;
        }else {
            return true;
        }
    }
}
