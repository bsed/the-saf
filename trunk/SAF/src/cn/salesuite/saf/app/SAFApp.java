/**
 * 
 */
package cn.salesuite.saf.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Log;
import cn.salesuite.saf.cache.ImageLoader;
import cn.salesuite.saf.config.SAFConstant;

/**
 * SAFApp是自定义的Application,session可作为缓存存放app的全局变量<br>
 * SAFApp并不是每个app都需要使用,可自由选择<br>
 * 如需使用,则在AndroidManifest.xml中配置,<br>
 * 在application中增加android:name="cn.salesuite.saf.app.SAFApp"
 * 
 * @author Tony Shen
 * 
 */
public class SAFApp extends Application {

	public HashMap<String, Object> session;
	public String root = "/sdcard";

	public List<Activity> activityManager;

	public String deviceid;  // 设备ID
	public String osVersion; // 系统版本
	public String mobileType;// 手机型号
	public String version;   // 应用的versionName
	public String citycode;  // 城市代码

	public ImageLoader imageLoader;
	private static SAFApp instance;

	/**
	 * @see android.app.Application#onCreate()
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		init();
	}

	public void init() {
		instance = this;

		session = new HashMap<String, Object>();
		activityManager = new ArrayList<Activity>();
		imageLoader = new ImageLoader(instance);// 需要使用ImageLoader组件时,必须设置SAFConfig中default_img_id值,如有需要可覆盖SAFConfig中DIR的值

		PackageManager manager = this.getPackageManager();
		try {
			PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
			// session.put(Constant.VERSION, info.versionName);
			// session.put(Constant.USER_STATE_KEY, Constant.USER_STATE_NORMAL);
			deviceid = getDeviceId();
			osVersion = Build.VERSION.RELEASE;
			mobileType = Build.MODEL;
			if (null != info) {
				version = info.versionName;
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取手机的设备号或者wifi的mac号，在wifi环境下只返回mac地址，否则返回手机设备号<br>
	 * 在模拟器情况下会返回null
	 * 
	 * @return
	 */
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
				}
				// else {
				// SharedPreferences sp = this.getSharedPreferences(
				// SAFConstant.SHARED, Activity.MODE_PRIVATE);
				// String uid = sp.getString("uid", null);
				// if (uid == null) {
				// SharedPreferences.Editor editor = sp.edit();
				// uid=UUID.randomUUID().toString().replace("-", "");
				// editor.putString("uid",uid );
				// editor.commit();
				// }
				// Log.d("requestCount", "uid:" + uid);
				// return uid;
				// }
				return null;
			}
		}
	}

	public static SAFApp getInstance() {
		return instance;
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}
}