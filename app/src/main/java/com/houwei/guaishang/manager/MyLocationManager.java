package com.houwei.guaishang.manager;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.houwei.guaishang.activity.BaseActivity;
import com.houwei.guaishang.bean.BaseResponse;
import com.houwei.guaishang.bean.IntResponse;
import com.houwei.guaishang.bean.LocationBean;
import com.houwei.guaishang.bean.UserBean;
import com.houwei.guaishang.manager.FollowManager.FollowListener;
import com.houwei.guaishang.manager.MyUserBeanManager.UserStateChangeListener;
import com.houwei.guaishang.tools.HttpUtil;
import com.houwei.guaishang.tools.JsonParser;
import com.houwei.guaishang.tools.JsonUtil;
import com.houwei.guaishang.tools.LogUtil;
import com.houwei.guaishang.tools.ToastUtils;

public class MyLocationManager {
	private LocationClient mLocationClient = null;

	private MyLocationListenner myListener = new MyLocationListenner();
	private LocationBean currentLocationBean;
	private ITopicApplication mContext;
	private ArrayList<LocationListener> onLocationListenerList;
	
	private Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case BaseActivity.NETWORK_SUCCESS_DATA_RIGHT:
				//上传经纬度到服务器返回
				break;

			default:
				break;
			}
		}
	};
	
	
	public MyLocationManager(ITopicApplication mContext) {
		this.mContext = mContext;
		mLocationClient = new LocationClient(mContext);
		currentLocationBean = new LocationBean();
		mLocationClient.registerLocationListener(myListener);
		onLocationListenerList = new ArrayList<MyLocationManager.LocationListener>();
		initLoc();
	}

	public LocationBean getcurrentLocationBean() {
		return currentLocationBean;
	}

	/**
	 * 监听函数，又新位置的时候，格式化成字符串，输出到屏幕中
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			setLocation(location);
		}

	}

	private void initLoc() {
		LocationClientOption option = new LocationClientOption();
//		option.setOpenGps(false); // 打开gps
		option.setOpenGps(true);
		option.setLocationMode(LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
		option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
		option.setScanSpan(LocationClientOption.MIN_SCAN_SPAN);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
		
		option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
		option.setIsNeedLocationDescribe(true);//可选，设置是否需要地址描述
		option.setNeedDeviceDirect(false);//可选，设置是否需要设备方向结果
	    
		option.setAddrType("all");
		option.setTimeOut(10 * 1000);
		mLocationClient.setLocOption(option);
	}

	/**
	 * 开始定位
	 * useCache 是否使用上次的缓存
	 */
	public void startLoction(boolean useCache) {
		if (useCache && currentLocationBean.getLatitude() != 0) {
			// 定位成功
			for (int i = onLocationListenerList.size() -1; i >= 0 ; i--) {
				onLocationListenerList.get(i).onLocationSuccess(currentLocationBean);
			}
		} else {
			// 还未定位，开始定位
			mLocationClient.requestLocation();
			mLocationClient.start();
		}
	}

	private void setLocation(BDLocation location) {
//		ToastUtils.toastForShort(mContext,"地址："+","+location.getCity()+location.getDistrict()+"ma:"+location.getLocType());
//		Log.d("CCC","地址："+","+location.getCity()+location.getDistrict()+"ma:"+location.getLocType());
		if (location == null || location.getLatitude() == 0
				|| location.getAddrStr() == null) {
			for (int i = onLocationListenerList.size() -1; i >= 0 ; i--) {
				onLocationListenerList.get(i).onLocationFail();
			}
			//定位失败
			return;
		}
		currentLocationBean.setCity(location.getCity());
		currentLocationBean.setDistrict(location.getDistrict());
		currentLocationBean.setAddress(location.getAddrStr());
		currentLocationBean.setLatitude(location.getLatitude());
		currentLocationBean.setLongitude(location.getLongitude());
		mLocationClient.stop();
		
		for (int i = onLocationListenerList.size() -1; i >= 0 ; i--) {
			onLocationListenerList.get(i).onLocationSuccess(currentLocationBean);
		}
		startUploadLocation(currentLocationBean);
		
	}

	
	/**
	 * 如果有需要，服务器收集用户最后坐标
	 * 需要登录后再收集
	 * @param currentLocationBean
	 */
	private void startUploadLocation(LocationBean currentLocationBean){
		if(!"".equals( mContext.getMyUserBeanManager().getUserId())){
			new Thread(new UploadLocationRun(currentLocationBean)).start();
		}
	}
	

	private class UploadLocationRun implements Runnable {
		private LocationBean currentLocationBean;

		public UploadLocationRun(LocationBean currentLocationBean) {
			this.currentLocationBean = currentLocationBean;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			BaseResponse response = null;
			try {
				HashMap<String, String> data = new HashMap<String, String>();
				data.put("userid", mContext.getMyUserBeanManager().getUserId());
				data.put("latitude", ""+currentLocationBean.getLatitude());
				data.put("longitude", ""+currentLocationBean.getLongitude());
				data.put("address", ""+currentLocationBean.getCity()+currentLocationBean.getDistrict());
				
				response = JsonParser.getBaseResponse(HttpUtil.postMsg(
						HttpUtil.getData(data), HttpUtil.IP+ "user/uploadaddress"));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (response == null) {
				response = new BaseResponse();
				response.setMessage("网络访问失败");
			}
			handler.sendMessage(handler.obtainMessage(
					BaseActivity.NETWORK_SUCCESS_DATA_RIGHT, response));
		}
	}


	/**
	 * ondestory 必须调用
	 * 
	 * @param onLocationListener
	 */
	public void removeLocationListener(LocationListener onLocationListener) {
		if (onLocationListener!=null
				&& onLocationListenerList.contains(onLocationListener)) {
			onLocationListenerList.remove(onLocationListener);
		}
	}

	/**
	 * onCreate 调用
	 * 
	 * @param onLocationListener
	 */
	public void addLocationListener(LocationListener onLocationListener) {
		if (onLocationListener != null
				&& !onLocationListenerList.contains(onLocationListener)) {
			onLocationListenerList.add(onLocationListener);
		}
	}

	public interface LocationListener {
		public void onLocationFail();

		public void onLocationSuccess(LocationBean currentLocationBean);
	}
	
	/**
     * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
     * @param context
     * @return true 表示开启
     */ 
    public static final boolean isOpenService(final Context context) { 
     
        
        String locationType = Settings.System.getString(context.getContentResolver(), Settings.System.LOCATION_PROVIDERS_ALLOWED);
       // locationType == network gps
        if (locationType == null || locationType.equals("") || locationType.equals(" ")) {
        	return false;
		}
        return true; 
    }
    
    /**
     * 帮用户打开GPS
     * @param context
     */ 
    public static final void openGPS(Context context) { 
    	 Intent intent =  new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);  
    	 context.startActivity(intent);
    }
}
