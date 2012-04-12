/**
 * 
 */
package cn.salesuite.saf.app;

import java.util.ArrayList;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;
import cn.salesuite.saf.location.CellIDInfo;
import cn.salesuite.saf.location.CellIDInfoManager;
import cn.salesuite.saf.location.LocationManager;
import cn.salesuite.saf.location.activity.LocationActivity;
import cn.salesuite.saf.utils.SAFUtil;

/**
 * SAF框架基类的Activity,任何使用该框架的app都可以继承该Activity<br>
 * 该类实现了定位的功能,当位置发生变化时子类可重写onLocationChanged()<br>
 * 一般情况下无需处理mLocationManager的关闭,只有在主的Activity中退出才调用mLocationManager.destroy();
 * @author Tony Shen
 *
 */
public class SAFActivity extends LocationActivity{

	public static SAFApp app;
	public String TAG;
	
	private Handler mdBmHandler = new Handler(Looper.getMainLooper());
	private Runnable mGetdBmRunnable = new Runnable() {
		public void run() {
			getSignalStrength();
		}
	};
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		app = (SAFApp) this.getApplication();
		checkSignalStrength();
		
		if (mLocationManager == null) {
			mLocationManager = LocationManager.getInstance();
			mLocationManager.register(this);
		}

		TAG = SAFUtil.makeLogTag(this.getClass());
		addActivityToManager(this);
	}
	
	protected  void addActivityToManager(Activity act) {
    	Log.i(TAG, "addActivityToManager");
        if (!app.activityManager.contains(act)) {
        	 Log.i(TAG , "addActivityToManager, packagename = " + act.getClass().getName()) ;
        	 app.activityManager.add(act);
	    }
	}
	
	protected void closeAllActivities() {
		Log.i(TAG, "closeAllActivities");
		for (final Activity act : app.activityManager) {
			if (act != null) {
				act.finish();
			}
		}
	}
	
	protected  void delActivityFromManager(Activity act) {
    	Log.i(TAG,"delActivityFromManager") ;
        if (app.activityManager.contains(act)) {
        	app.activityManager.remove(act);
        }
	}
	
	@Override
	public void onLocationChanged(Location location) {

	}
	
	/**
	 * 返回最后的location
	 * @return
	 */
	public Location getLastLocation() {
		return mLocationManager.getLastKnownLocation();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mLocationManager.unregister(this);
		delActivityFromManager(this);
	}
	
	protected void showToast(int strId) {
		Toast.makeText(this, getString(strId), Toast.LENGTH_SHORT).show();
	}

	protected void showToast(String str) {
		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * 绘制中部的Toast
	 * @param strId
	 */
	protected void showMidToast(int strId) {
		Toast msg = Toast.makeText(this, getString(strId), Toast.LENGTH_SHORT);
		msg.setGravity(Gravity.CENTER, msg.getXOffset(), msg.getYOffset() / 2);
		msg.show();
	}
	
	/**
	 * 绘制中部的Toast
	 * @param str
	 */
	protected void showMidToast(String str) {
		Toast msg = Toast.makeText(this, str, Toast.LENGTH_SHORT);
		msg.setGravity(Gravity.CENTER, msg.getXOffset(), msg.getYOffset() / 2);
		msg.show();
	}
	
	/**
	 * 检测手机信号,当手机信号弱时,利用toast提示用户
	 */
	protected void checkSignalStrength() {
		if (app.deviceid!=null) {
			mdBmHandler.post(mGetdBmRunnable);
		}
	}
	
	private void getSignalStrength() {
		int dbm = 0;
		
		CellIDInfoManager manager = new CellIDInfoManager();
		ArrayList<CellIDInfo> CellID = null;
		try {
			CellID = manager.getCellIDInfo(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (CellID!=null && CellID.size()>0) {
			dbm = CellID.get(0).signal_strength;
		}

		if (dbm <= -112) {
			showToast("当前信号差");
		}
	}
}
