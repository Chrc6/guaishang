package com.houwei.guaishang.tools;

import android.app.Activity;
import android.util.Log;

import com.houwei.guaishang.activity.UserLoginActivity;
import com.houwei.guaishang.activity.UserRegMobileActivity;
import com.houwei.guaishang.bean.AvatarBean;
import com.houwei.guaishang.bean.UserBean;
import com.houwei.guaishang.bean.UserResponse;

import java.util.HashMap;
import java.util.Map;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;

import static com.houwei.guaishang.activity.BaseActivity.NETWORK_FAIL;
import static com.houwei.guaishang.activity.BaseActivity.NETWORK_SUCCESS_DATA_RIGHT;

/**
 * Created by Administrator on 2017/10/16.
 */

public class ShareSDKUtilsReg {

    private static String type="";
    private Activity activity;
    private UserRegMobileActivity.MyHandler myHandler;


    public ShareSDKUtilsReg(Activity activity, UserRegMobileActivity.MyHandler myHandler) {
        this.activity = activity;
        this.myHandler=myHandler;
    }

    /**
     * 登录
     */
    public  void Login(String name ){
        type="login";
        Platform mPlatform = ShareSDK.getPlatform(name);
        mPlatform.SSOSetting(false);
        mPlatform.setPlatformActionListener(mPlatformActionListener);
//        mPlatform.authorize();//单独授权,OnComplete返回的hashmap是空的
        mPlatform.showUser(null);//授权并获取用户信息
    }
    public PlatformActionListener mPlatformActionListener= new PlatformActionListener() {
        @Override
        public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
            if(type.equals("login")){
                Log.e("onComplete","登录成功"+platform.getDb().getUserIcon());
                Log.e("openid",platform.getDb().getUserId());//拿到登录后的openid
                Log.e("username",platform.getDb().getUserName());//拿到登录用户的昵称

                saveUserInfo(platform.getDb());
            }else{
                Log.e("onComplete","分享成功");
            }
        }
        @Override
        public void onError(Platform platform, int i, Throwable throwable) {
            Log.e("onError",throwable.toString()+"");
            if(type.equals("login")){
                Log.e("onError","登录失败"+throwable.toString()+"--i:"+i+"p:"+platform.toString());
            }else{
                Log.e("onError","分享失败"+throwable.toString());
            }
        }
        @Override
        public void onCancel(Platform platform, int i) {
            if(type.equals("login")){
                Log.e("onCancel","登录取消");
            }else{
                Log.e("onCancel","分享取消");
            }
        }
    };

    /**
     *取消授权
     */
    public  static void removeAccount() {
        Platform qzone = ShareSDK.getPlatform(QZone.NAME);
        Platform qq = ShareSDK.getPlatform(QQ.NAME);
        Platform weChat = ShareSDK.getPlatform(Wechat.NAME);
        Platform sinaWeibo = ShareSDK.getPlatform(SinaWeibo.NAME);
        if (qq.isAuthValid()) {
            qq.removeAccount(true);
        }
        if (qzone.isAuthValid()) {
            qzone.removeAccount(true);
        }
        if (weChat.isAuthValid()) {
            weChat.removeAccount(true);
        }
        if (sinaWeibo.isAuthValid()) {
            sinaWeibo.removeAccount(true);
        }
    }

    private  void saveUserInfo(PlatformDb db) {
        UserBean ub=new UserBean();
        ub.setName(db.getUserName());
        ub.setUserid(db.getUserId());
        AvatarBean ab=new AvatarBean();
        ab.setOriginal(db.getUserIcon());
        ab.setSmall(db.getUserIcon());
        ub.setAvatar(ab);
        new Thread(new LoginRunnable(db.getUserName(),db.getUserId(),db.getUserIcon())).start();
    }

    public class LoginRunnable implements Runnable {
        private String name;
        private String openid;
        private String avator;

        public LoginRunnable(String name, String openid, String avator) {
            this.name = name;
            this.openid = openid;
            this.avator = avator;
        }

        @Override
        public void run() {
            UserResponse response = null;
            try {
                Map<String, String> data = new HashMap<String, String>();
                data.put("name", name);
                data.put("openid", openid);
                data.put("avatar", avator);
                response = JsonParser.getUserResponse(HttpUtil.postMsg(
                        HttpUtil.getData(data), HttpUtil.IP + "user/login"));
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (response != null) {
                myHandler.sendMessage(myHandler.obtainMessage(
                        NETWORK_SUCCESS_DATA_RIGHT, response));
            } else {
                myHandler.sendEmptyMessage(NETWORK_FAIL);
            }
        }
    }
}
