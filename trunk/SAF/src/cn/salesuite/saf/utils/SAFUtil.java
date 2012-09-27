/**
 * 
 */
package cn.salesuite.saf.utils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;

import cn.salesuite.saf.config.SAFConfig;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.telephony.TelephonyManager;

/**
 * SAF的工具类
 * @author Tony Shen
 *
 */
public class SAFUtil {
	
	public static boolean isFroyoOrHigher() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }
	
	public static boolean isGingerbreadOrHigher() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
	}
	
	public static boolean isHoneycombOrHigher() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }
	
	public static boolean isICSOrHigher() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }
	
	public static boolean isJellyBeanOrHigher() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

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
	 * 判断服务是否运行
	 * @param mContext
	 * @param serviceName
	 * @return true为运行，false为不在运行
	 */
	public boolean isServiceRunning(Context mContext, String serviceName) {
		ActivityManager myManager = (ActivityManager) mContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		ArrayList<RunningServiceInfo> runningService = (ArrayList<RunningServiceInfo>) myManager
				.getRunningServices(30);
		for (int i = 0; i < runningService.size(); i++) {
			String serName = runningService.get(i).service.getClassName().toString();
			if (serName.equals(serviceName)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 获取手机网络类型名称
	 * @param networkType
	 * @param mnc Mobile NetworkCode，移动网络码，共2位
	 * @return
	 */
	public static String getNetWorkName(int networkType,String mnc) {
		if (networkType == TelephonyManager.NETWORK_TYPE_UNKNOWN) {
			return "Network type is unknown";
		} else if (networkType == TelephonyManager.NETWORK_TYPE_CDMA) {
			return "电信2G";
		} else if (networkType == TelephonyManager.NETWORK_TYPE_EVDO_0) {
			return "电信3G";
		} else if (networkType == TelephonyManager.NETWORK_TYPE_GPRS || networkType == TelephonyManager.NETWORK_TYPE_EDGE) {
			if ("00".equals(mnc) || "02".equals(mnc)) {
				return "移动2G";
			} else if ("01".equals(mnc)) {
				return "联通2G";
			}
		} else if (networkType == TelephonyManager.NETWORK_TYPE_UMTS || networkType == TelephonyManager.NETWORK_TYPE_HSDPA) {
			return "联通3G";
		}
		return null;
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
	
	/**
	* 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	*/
	public static int dip2px(Context context, float dpValue) {
	  final float scale = context.getResources().getDisplayMetrics().density;
	  return (int) (dpValue * scale + 0.5f);
	}
	
    /**
     * 判断谷歌地图是否可用,某些国行的手机不支持谷歌地图的服务
     * @return
     */
	public static boolean googleMapAvailable() {
		boolean available = false;
		try{
			Class.forName("com.google.android.maps.MapActivity");
			available = true;
		} catch (Exception e)  {
		}
		return available;
	}
	
	/**
	 * 从Assets中读取文件
	 * @param context
	 * @param fileName
	 * @return
	 * @throws FileNotFoundException
	 */
	public static InputStream getFromAssets(Context context,String fileName)
			throws FileNotFoundException {
		InputStream inputStream = null;
		try {
			inputStream = context.getResources().getAssets().open(fileName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return inputStream;
	}
	
	/**
	 * 将对象以json格式打印出来
	 * @param obj
	 * @return
	 */
	public static String printObject(Object obj) {
		return JSON.toJSONString(obj);
	}
	
	/**
	 * 封装AsyncTask,当使用Android 3.0以及以上版本时可以使用线程池执行AsyncTask
	 * @param task
	 * @param args
	 */
	@TargetApi(11)
    public static <T> void executeAsyncTask(AsyncTask<T, ?, ?> task, T... args) {
        if (isHoneycombOrHigher()) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, args);
        } else {
            task.execute(args);
        }
    }
	
	/**
	 * 对文件设置root权限
	 * @param filePath
	 * @return
	 */
	public static boolean upgradeRootPermission(String filePath) {
	    Process process = null;
	    DataOutputStream os = null;
	    try {
	        String cmd="chmod 777 " + filePath;
	        process = Runtime.getRuntime().exec("su"); //切换到root帐号
	        os = new DataOutputStream(process.getOutputStream());
	        os.writeBytes(cmd + "\n");
	        os.writeBytes("exit\n");
	        os.flush();
	        process.waitFor();
	    } catch (Exception e) {
	        return false;
	    } finally {
	        try {
	            if (os != null) {
	                os.close();
	            }
	            process.destroy();
	        } catch (Exception e) {
	        }
	    }
	    return true;
	}
}
