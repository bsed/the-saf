/**
 * 
 */
package cn.salesuite.saf.eventbus;

import java.util.concurrent.Callable;

import cn.salesuite.saf.executor.concurrent.BackgroundExecutor;


/**
 * 后台线程运行，使用BackgroundExecutor
 * @author Tony Shen
 *
 */
public class BackgroundPoster {
	
    BackgroundExecutor backgroundExecutor;
	private final EventBus eventBus;

	BackgroundPoster(EventBus eventBus) {
		this.eventBus = eventBus;
		backgroundExecutor = new BackgroundExecutor(5);
	}

	public void enqueue(final Object event,final EventHandler subscription) {		
		backgroundExecutor.submit(new Callable<Void>(){
			public Void call() throws Exception {
				eventBus.invokeSubscriber(event,subscription);
				return null;
            }    
		});
	}

}
