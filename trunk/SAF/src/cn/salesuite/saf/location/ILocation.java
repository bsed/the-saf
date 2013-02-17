/**
 * 
 */
package cn.salesuite.saf.location;

import android.location.Location;

/**
 * 类Activity、MapActivity、Fragment实现该接口，就可进行在该类中获取Location<br>
 * 使用定位前需要使用mLocationManager.register(this);
 * @author Tony Shen
 *
 */
public interface ILocation {
	
	public void onLocationChanged(Location location);
}
