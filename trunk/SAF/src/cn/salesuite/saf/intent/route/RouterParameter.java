/**
 * 
 */
package cn.salesuite.saf.intent.route;

import java.util.Map;

import android.app.Activity;

/**
 * @author Tony Shen
 *
 */
public class RouterParameter {

	public RouterOptions routerOptions;
	public Map<String, String> openParams;

	public static class RouterOptions {
		public Class<? extends Activity> clazz;
		Map<String, String> defaultParams;
	}
}
