/**
 * 
 */
package cn.salesuite.saf.exception;

import android.content.Context;

/**
 * @author Tony Shen
 * 
 * 
 */
public class DefaultExceptionHandler implements Thread.UncaughtExceptionHandler {
	private Thread.UncaughtExceptionHandler defaultExceptionHandler;

	public DefaultExceptionHandler(
			Thread.UncaughtExceptionHandler paramUncaughtExceptionHandler) {
		this.defaultExceptionHandler = paramUncaughtExceptionHandler;
	}

	public DefaultExceptionHandler(Context context) {
		this.defaultExceptionHandler = Thread
				.getDefaultUncaughtExceptionHandler();
	}

	public void uncaughtException(Thread paramThread, Throwable ex) {
		if (!handleException(ex) && defaultExceptionHandler != null) {
			this.defaultExceptionHandler.uncaughtException(paramThread, ex);
		} else {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// Will not happen
			}
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(1);
		}

	}

	private boolean handleException(Throwable ex) {
		if (ex == null) {
			return true;
		}
		// collectCrashExceptionInfo(ex);
		return true;
	}

}
