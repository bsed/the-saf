/**
 * 
 */
package cn.salesuite.saf.location.activity;

import android.app.Activity;
import android.location.Location;
import cn.salesuite.saf.location.LocationManager;
import cn.salesuite.saf.location.Position;

/**
 * @author Tony Shen
 *
 */
public abstract class LocationActivity extends Activity {

	protected LocationManager mLocationManager=null;
	protected static Position lastSavedPosition = null;
	
	public abstract void onLocationChanged(Location location);
}
