/**
 * 
 */
package cn.salesuite.saf.log;

import java.util.LinkedList;

import android.util.Log;
import cn.salesuite.saf.config.SAFConfig;

/**
 * 0: no log 
 * 1: error 
 * 2: warn 
 * 3: info 
 * 4: debug
 * 封装日志操作的类,便于收集日志信息发送到服务端
 * @author Tony Shen
 *
 */
public class LogWrapper {

	private static LinkedList<String> content = new LinkedList<String>();
	
	private static LogWrapper instance = null;
	
	private LogWrapper() {
	}
	
	public static LogWrapper getInstance() {
		if(instance==null){
			instance = new LogWrapper();
		}
		return instance;
	}
	
	public static LinkedList<String> getContent() {
		return content;
	}
	
	public static void e(String tag, String msg) {
		if (SAFConfig.LOG_LEVEL >= 1) {
			Log.e(tag, msg);
			addToList("ERROR: " + tag + " " + msg);
		}
	}

	public static void w(String tag, String msg) {
		if (SAFConfig.LOG_LEVEL >= 2) {
			Log.w(tag, msg);
			addToList("WARN: " + tag + " " + msg);
		}
	}

	public static void i(String tag, String msg) {
		if (SAFConfig.LOG_LEVEL >= 3) {
			Log.i(tag, msg);
			addToList("INFO: " + tag + " " + msg);
		}
	}
	
	public static void d(String tag, String msg) {
		if (SAFConfig.LOG_LEVEL >= 4) {
			Log.d(tag, msg);
			addToList("DEBUG: " + tag + " " + msg);
		}
	}

	private static void addToList(String msg) {
		content.addFirst(msg);
		if (content.size() > SAFConfig.LOG_SIZE) {
			content.removeLast();
		}
	}
}
