/**
 * 
 */
package cn.salesuite.saf.app;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import cn.salesuite.saf.location.LocationManager;
import cn.salesuite.saf.location.activity.LocationActivity;
import cn.salesuite.saf.utils.AppUtil;

/**
 * @author Tony Shen
 *
 */
public class SAFActivity extends LocationActivity{

	public static SAFApp app;
	public String TAG;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (mLocationManager == null) {
			mLocationManager = LocationManager.getInstance();
			mLocationManager.register(this);
		}

		app = (SAFApp) this.getApplication();
		TAG = AppUtil.makeLogTag(this.getClass());
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
}
