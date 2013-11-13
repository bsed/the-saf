/**
 * 
 */
package cn.salesuite.saf.route;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.util.LruCache;
import cn.salesuite.saf.route.RouterParameter.RouterOptions;

/**
 * Intent Router可以完成各个Intent之间的跳转，类似rails的router功能
 * @author Tony Shen
 *
 */
public class Router {
	
	public static final int DEFAULT_CACHE_SIZE = 1024;
	
	private Context context;
	private LruCache<String, RouterParameter> cachedRoutes = new LruCache<String, RouterParameter>(DEFAULT_CACHE_SIZE); // 缓存跳转的参数
	private final Map<String, RouterOptions> routes = new HashMap<String, RouterOptions>();                             // 存放Intent之间跳转的route
	
	private static final Router router = new Router();
	
	private Router() {
	}
	
	public static Router getInstance() {
		return router;
	}
	
	public void setContext(Context context) {
		this.context = context;
	}

	public Context getContext() {
		return context;
	}
	
	/**
	 * @param format 形如"user/:user/password/:password"这样的格式，其中user、password为参数名
	 * @param clazz
	 */
	public void map(String format, Class<? extends Activity> clazz) {
		this.map(format, clazz, null);
	}

	public void map(String format, Class<? extends Activity> clazz,RouterOptions options) {
		if (options == null) {
			options = new RouterOptions();
		}
		options.clazz = clazz;
		this.routes.put(format, options);
	}

	/**
	 * 跳转到网页，如下：
	 * <pre>
	 * <code>
	 * Router.getInstance().openURI("http://www.g.cn");
	 * </code>
	 * </pre>
	 * 
	 * 调用系统电话，如下：
	 * <pre>
	 * <code>
	 * Router.getInstance().openURI("tel://18662430000");
	 * </code>
	 * </pre>
	 * 
	 * 调用手机上的地图app，打开地图，如下：
	 * <pre>
	 * <code>
	 * Router.getInstance().openURI("geo:0,0?q=31,121");
	 * </code>
	 * </pre>
	 * @param url
	 */
	public void openURI(String url) {
		this.openURI(url,this.context);
	}
	
	public void openURI(String url,Context context) {
		this.openURI(url, context, null);
	}
	
	public void openURI(String url,Context context,Bundle extras) {
		if (context == null) {
			throw new RouterException("You need to supply a context for Router " + this.toString());
		}
		
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		this.addFlagsToIntent(intent, context);
		if (extras != null) {
			intent.putExtras(extras);
		}
		context.startActivity(intent);
	}
	
	public void openURI(String url,Context context,Bundle extras,int flags) {
		if (context == null) {
			throw new RouterException("You need to supply a context for Router " + this.toString());
		}
		
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		this.addFlagsToIntent(intent, context, flags);
		if (extras != null) {
			intent.putExtras(extras);
		}
		context.startActivity(intent);
	}
	
	/**
	 * 跳转到某个activity并传值
	 * <pre>
	 * <code>
	 * Router.getInstance().open("user/fengzhizi715/password/715");
	 * </code>
	 * </pre>
	 * @param url
	 */
	public void open(String url) {
		this.open(url,this.context);
	}
	
	public void open(String url,Context context) {
		this.open(url, context, null);
	}
	
	public void open(String url,Context context,Bundle extras) {
		if (context == null) {
			throw new RouterException("You need to supply a context for Router "+ this.toString());
		}
		
		RouterParameter param = parseUrl(url);
		RouterOptions options = param.routerOptions;
		
		Intent intent = this.parseRouterParameter(param);
		if (intent == null) {
			return;
		}
		if (extras != null) {
			intent.putExtras(extras);
		}
		
		this.addFlagsToIntent(intent, context);
		context.startActivity(intent);
		
		if (options.enterAnim>0 && options.exitAnim>0) {
			((Activity)context).overridePendingTransition(options.enterAnim, options.exitAnim);
		}
	}
	
	public void open(String url,Context context,Bundle extras, int flags) {
		if (context == null) {
			throw new RouterException("You need to supply a context for Router "+ this.toString());
		}
		
		RouterParameter param = parseUrl(url);
		RouterOptions options = param.routerOptions;
		
		Intent intent = this.parseRouterParameter(param);
		if (intent == null) {
			return;
		}
		if (extras != null) {
			intent.putExtras(extras);
		}
		this.addFlagsToIntent(intent, context, flags);
		context.startActivity(intent);
		
		if (options.enterAnim>0 && options.exitAnim>0) {
			((Activity)context).overridePendingTransition(options.enterAnim, options.exitAnim);
		}
	}

	private void addFlagsToIntent(Intent intent, Context context) {
		addFlagsToIntent(intent,context,Intent.FLAG_ACTIVITY_NEW_TASK);// 默认的跳转类型,将Activity放到一个新的Task中
	}
	
	private void addFlagsToIntent(Intent intent, Context context,int flags) {
		intent.addFlags(flags);
	}

	private Intent parseRouterParameter(RouterParameter param) {
		RouterOptions options = param.routerOptions;
		Intent intent = new Intent();

		for (Entry<String, String> entry : param.openParams.entrySet()) {
			intent.putExtra(entry.getKey(), entry.getValue());
		}
		
		intent.setClass(context, options.clazz);
		
		return intent;
	}

	private RouterParameter parseUrl(String url) {
		if (this.cachedRoutes.get(url) != null) {
			return this.cachedRoutes.get(url);
		}

		String[] givenParts = url.split("/");

		RouterOptions openOptions = null;
		RouterParameter openParams = null;
		for (Entry<String, RouterOptions> entry : this.routes.entrySet()) {
			String routerUrl = entry.getKey();
			RouterOptions routerOptions = entry.getValue();
			String[] routerParts = routerUrl.split("/");

			if (routerParts.length != givenParts.length) {
				continue;
			}

			Map<String, String> givenParams = urlToParamsMap(givenParts, routerParts);
			if (givenParams == null) {
				continue;
			}

			openOptions = routerOptions;
			openParams = new RouterParameter();
			openParams.openParams = givenParams;
			openParams.routerOptions = routerOptions;
			break;
		}

		if (openOptions == null || openParams == null) {
			throw new RouterException("No route found for url " + url);
		}

		this.cachedRoutes.put(url, openParams);
		return openParams;
	}

	private Map<String, String> urlToParamsMap(String[] givenUrlSegments, String[] routerUrlSegments) {
		Map<String, String> formatParams = new HashMap<String, String>();
		int length = routerUrlSegments.length;
		for (int index = 0; index < length; index++) {
			String routerPart = routerUrlSegments[index];
			String givenPart = givenUrlSegments[index];

			if (routerPart.charAt(0) == ':') {
				String key = routerPart.substring(1, routerPart.length());
				formatParams.put(key, givenPart);
				continue;
			}

			if (!routerPart.equals(givenPart)) { // 偶数个参数不相等时的情况
				return null;
			}
		}

		return formatParams;
	}
	
	/**
	 * 退出系统时，清空缓存数据
	 */
	public void clear() {
		cachedRoutes.evictAll();
	}
}
