/**
 * 
 */
package cn.salesuite.saf.utils;

import java.util.LinkedList;

import cn.salesuite.saf.config.SAFConfig;

import android.util.Log;

/**
 * 0: no log 
 * 1: error 
 * 2: warn 
 * 3: info 
 * 4: debug
 * 
 * @author Tony Shen
 * 
 */
public class LogWrapper {
	private static LinkedList<String> content = new LinkedList<String>();

	public static LinkedList<String> getContent() {
		return content;
	}

	public static void dws(String tag, String tagm, byte[] data) {
		if (SAFConfig.LOG_LEVEL >= 4) {
			try {
				String msg = new String(data, "UTF-8");
				Log.d(tag, tagm + msg);

				content.addFirst("WS: " + tagm + msg);
				if (content.size() > SAFConfig.LOG_SIZE) {
					content.removeLast();
				}
			} catch (Exception e) {

			}

		}
	}

	public static void d(String tag, String msg) {
		if (SAFConfig.LOG_LEVEL >= 4) {
			Log.d(tag, msg);

			content.addFirst("DEBUG: " + tag + " " + msg);
			if (content.size() > SAFConfig.LOG_SIZE) {
				content.removeLast();
			}
		}
	}

	public static void i(String tag, String msg) {
		if (SAFConfig.LOG_LEVEL >= 3) {
			Log.i(tag, msg);

			content.addFirst("INFO: " + tag + " " + msg);
			if (content.size() > SAFConfig.LOG_SIZE) {
				content.removeLast();
			}
		}
	}

	public static void w(String tag, String msg) {
		if (SAFConfig.LOG_LEVEL >= 2) {
			Log.w(tag, msg);

			content.addFirst("WARN: " + tag + " " + msg);
			if (content.size() > SAFConfig.LOG_SIZE) {
				content.removeLast();
			}
		}
	}

	public static void e(String tag, String msg) {
		if (SAFConfig.LOG_LEVEL >= 1) {
			Log.e(tag, msg);

			content.addFirst("ERROR: " + tag + " " + msg);
			if (content.size() > SAFConfig.LOG_SIZE) {
				content.removeLast();
			}
		}
	}

}
