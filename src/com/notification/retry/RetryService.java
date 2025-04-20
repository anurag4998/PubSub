package com.notification.retry;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class RetryService<V> {
	
	
	private IRetry<V> retryPolicy;

	public RetryService(IRetry<V> retryPolicy) {
		this.retryPolicy = retryPolicy;
	}
	public void retry(Map<Future<V>, RetryableTask<V>> futureMap) {
		ExecutorService newSingleThreadExecutor = Executors.newSingleThreadExecutor();
		newSingleThreadExecutor.execute(() -> {
			List<RetryableTask<V>> failedFutures = getFailedFutures(futureMap);
			retryPolicy.retry(failedFutures);
			newSingleThreadExecutor.shutdown();
		});
	}
	
	private List<RetryableTask<V>> getFailedFutures(Map<Future<V>, RetryableTask<V>> futureMap) {
		
		return futureMap
		.keySet()
		.stream()
		.map(future -> {
			try {
				boolean isFutureFailed =  future.isDone() && (boolean)future.get() == false;
				if(isFutureFailed)
					return futureMap.get(future);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		})
		.filter(obj -> obj != null)
		.toList();
	}

}
