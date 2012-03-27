/**
 * 
 */
package cn.salesuite.saf.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import cn.salesuite.saf.config.SAFConstant;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * SAFApp是自定义的Application,session可作为缓存存放app的全局变量<br>
 * SAFApp并不是每个app都需要使用,可自由选择<br>
 * 如需使用,则在AndroidManifest.xml中配置,<br>
 * 在application中增加android:name="cn.salesuite.saf.SAFApp"
 * @author Tony Shen
 *
 */
public class SAFApp extends Application{

	public HashMap<String,Object> session;
	public String root="/sdcard";
	
	public List<Activity> activityManager;
	
	public String deviceid;  //设备ID
	public String osVersion; //系统版本
	public String mobileType;//手机型号
	public String version;   //应用的versionName
	
	/**
	 * @see android.app.Application#onCreate()
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		init();
	}
	
	public void init(){
		session=new HashMap<String,Object>();
		activityManager=new ArrayList<Activity>();
		PackageManager manager = this.getPackageManager();
		try {
			PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
//			session.put(Constant.VERSION, info.versionName);
//			session.put(Constant.USER_STATE_KEY, Constant.USER_STATE_NORMAL);
			deviceid = getDeviceId();
			osVersion = Build.VERSION.RELEASE;
			mobileType = Build.MODEL;
			if(null != info){
				version = info.versionName;
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private String getDeviceId() {
		WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = manager.getConnectionInfo();
		String macAddress = wifiInfo.getMacAddress();
		if (macAddress != null) {
			Log.d("requestCount", "mac:" + macAddress);
			return macAddress.replace(".", "").replace(":", "")
					.replace("-", "").replace("_", "");
		} else {
			TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			String imei = tm.getDeviceId();
			// no sim: sdk|any pad
			if (imei != null && !SAFConstant.SPECIAL_IMEI.equals(imei)) {
				Log.d("requestCount", "imei:" + imei);
				return imei;
			} else {
				String deviceId = Secure.getString(getContentResolver(),
						Secure.ANDROID_ID);
				// sdk: android_id
				if (deviceId != null
						&& !SAFConstant.SPECIAL_ANDROID_ID.equals(deviceId)) {
					Log.d("requestCount", "ANDROID_ID:" + deviceId);
					return deviceId;
				} else {
					SharedPreferences sp = this.getSharedPreferences(
							SAFConstant.SHARED, Activity.MODE_PRIVATE);
					String uid = sp.getString("uid", null);
					if (uid == null) {
						SharedPreferences.Editor editor = sp.edit();
						uid=UUID.randomUUID().toString().replace("-", "");
						editor.putString("uid",uid );
						editor.commit();
					}
					Log.d("requestCount", "uid:" + uid);
					return uid;
				}
			}
		}
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}
}
