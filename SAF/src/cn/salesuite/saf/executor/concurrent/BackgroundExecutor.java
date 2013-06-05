/**
 * 
 */
package cn.salesuite.saf.executor.concurrent;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Tony Shen
 *
 */
public class BackgroundExecutor extends ThreadPoolExecutor {

	public BackgroundExecutor(int nThreads) {
		super(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>(),
				new BackgroundPriorityThreadFactory());
	}
}
