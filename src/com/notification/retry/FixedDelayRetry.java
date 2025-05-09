package com.notification.retry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FixedDelayRetry<V> implements IRetry<V> {
	private final static int maxRetries = 5;
	ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
	ConcurrentHashMap<RetryableTask<V>, Integer> retryMap = new ConcurrentHashMap<>();
	@Override
	public void retry(List<RetryableTask<V>> failedTasks) {
		
		if(failedTasks.size() == 0) {
			return;
		}
		
		List<RetryableTask<V>> failedTasksCopy = new ArrayList<>(failedTasks);
		failedTasksCopy.forEach(task -> {
			retryMap.put(task, retryMap.getOrDefault(task, 0) + 1);
		});
		
		List<Map<Future<V>, RetryableTask<V>>> mapOfFuture = failedTasksCopy
		.stream()
		.map((retryableTask) -> {
			Future<V> future = scheduler.schedule(retryableTask.getTask(), 1, TimeUnit.MINUTES);
			return Map.of(future,retryableTask);
		})
		.toList();
		
		List<RetryableTask<V>> retryableTasksCompleted = waitForFuturesToComplete(mapOfFuture);
		removeFromMap(retryableTasksCompleted);
		List<RetryableTask<V>> tasksPending = retryMap.keySet()
		.stream()
		.map(x -> x)
		.toList();
		
		tasksPending = tasksPending.stream()
			    .filter(task -> retryMap.getOrDefault(task, 0) <= maxRetries)
			    .toList();
		
		if(tasksPending.size() > 0)
			retry(tasksPending);
		else {
			scheduler.shutdown(); 	
		}
	}

	private void removeFromMap(List<RetryableTask<V>> retryableTasksCompleted) {
		retryableTasksCompleted.forEach(task -> retryMap.remove(task));
	}

	private List<RetryableTask<V>> waitForFuturesToComplete(List<Map<Future<V>, RetryableTask<V>>> listOfMapOfFutures) {
		List<RetryableTask<V>> completedTasks = new ArrayList<>();

		listOfMapOfFutures.forEach((mapOfFuture) -> {
			for (Map.Entry<Future<V>, RetryableTask<V>> entry : mapOfFuture.entrySet()) {
				try {
					Boolean result = (boolean)entry.getKey().get();
					if (result) {
						completedTasks.add(entry.getValue());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		return completedTasks;
		
	}

	
}
