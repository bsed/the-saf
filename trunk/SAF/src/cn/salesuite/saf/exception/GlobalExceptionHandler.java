/**
 * 
 */
package cn.salesuite.saf.exception;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;

/**
 * @author Tony Shen
 *
 * 
 */
public class GlobalExceptionHandler {
	public static String TAG = "GlobalExceptionHandler";
    private static HandlerThread localHandlerThread;
    @SuppressWarnings("unused")
    private static Handler handler;

    public static void register(Context context, String activityName) {
        localHandlerThread = new HandlerThread(activityName);
        localHandlerThread.start();
        handler = new Handler(localHandlerThread.getLooper());
        Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(context));
    }

    public static void register(Context context) {
        register(context, context.getClass().getName());
    }
}
