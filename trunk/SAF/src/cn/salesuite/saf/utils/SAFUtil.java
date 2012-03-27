/**
 * 
 */
package cn.salesuite.saf.utils;

import java.io.File;
import java.util.List;

import cn.salesuite.saf.config.SAFConfig;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;

/**
 * SAF的工具类
 * @author Tony Shen
 *
 */
public class SAFUtil {

	public static boolean isWiFiActive(Context context) { 
		WifiManager wm=null;
		try{
			wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		if(wm==null || wm.isWifiEnabled()==false) return false;
		
		return true;
    }  
	
	/**
	 * 安装apk
	 * @param fileName apk文件的绝对路径
	 * @param context
	 */
	public static void installAPK(String fileName, Context context) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(fileName)), "application/vnd.android.package-archive");
		context.startActivity(intent);
	}
	
	/**
	 * 判断某个应用当前是否正在运行
	 * @param context
	 * @param packageName
	 * @return
	 */
	public static boolean isAppRunning(Context context, String packageName) {
		if (packageName == null)
			return false;
		// Returns a list of application processes that are running on the
		// device
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> appProcesses = activityManager
				.getRunningAppProcesses();
		if (appProcesses == null)
			return false;
		for (RunningAppProcessInfo appProcess : appProcesses) {
			// importance:
			// The relative importance level that the system places
			// on this process.
			// May be one of IMPORTANCE_FOREGROUND, IMPORTANCE_VISIBLE,
			// IMPORTANCE_SERVICE, IMPORTANCE_BACKGROUND, or IMPORTANCE_EMPTY.
			// These constants are numbered so that "more important" values are
			// always smaller than "less important" values.
			// processName:
			// The name of the process that this object is associated with.
			if (appProcess.processName.equals(packageName)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 检测网络状态
	 * @param context
	 * @return
	 */
	public static boolean checkNetworkStatus(Context context){
		boolean resp = false;
		final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connMgr.getActiveNetworkInfo();   
		if (activeNetInfo != null && activeNetInfo.isAvailable()) {
			resp = true;
		}
		return resp;
	}
	
	/**
	 * 检测gps状态
	 * @param context
	 * @return
	 */
	public static boolean checkGPSStatus(Context context){
		boolean resp = false;
		LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);  
        if(lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){  
        	resp = true;            
        }  
        return resp;
	}
	
	/**
	 * 生成app日志tag
	 * @param cls
	 * @return
	 */
	public static String makeLogTag(Class cls) {
		String tag = null;
		if (SAFConfig.TAG_LEVEL == 0) {
			tag = cls.getSimpleName();
		} else if(SAFConfig.TAG_LEVEL == 1){
			tag = cls.getCanonicalName();
		}
		return tag;
	}
}
