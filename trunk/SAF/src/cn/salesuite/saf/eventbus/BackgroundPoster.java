/**
 * 
 */
package cn.salesuite.saf.eventbus;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;


/**
 * 后台线程运行，使用CompletionService，从而无需使用阻塞队列
 * @author Tony Shen
 *
 */
public class BackgroundPoster {
	
	private CompletionService<Void> service;
	private final EventBus eventBus;

	BackgroundPoster(EventBus eventBus) {
		this.eventBus = eventBus;
		service = new ExecutorCompletionService<Void>(
				EventBus.executorService);
	}

	public void enqueue(final Object event,final EventHandler subscription) {
		service.submit(new Callable<Void>(){
			public Void call() throws Exception {
				eventBus.invokeSubscriber(event,subscription);
				return null;
            }    
		});
		
		try {
			service.take().get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

}
