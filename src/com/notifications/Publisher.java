package com.notifications;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.notification.retry.FixedDelayRetry;
import com.notification.retry.IRetry;
import com.notification.retry.RetryService;
import com.notification.retry.RetryableTask;
import com.notifications.base.Notification;
import com.notifications.base.NotificationService;
import com.notifications.base.Priority;

public class Publisher implements ISubject {
	private final List<IObservable> subscribers;
	private final NotificationService notifService;
	private ExecutorService fixedThreadPool = Executors.newFixedThreadPool(5);
	private RetryService retrySerivce;
	private final IRetry retryPolicy;
	public Publisher () {
		this.subscribers = new ArrayList<>();
		this.notifService = new NotificationService();
		this.retryPolicy = new FixedDelayRetry();
		this.retrySerivce = new RetryService(retryPolicy);
	}
	@Override
	public boolean register(IObservable obj) {
		boolean isAdded = subscribers.add(obj);
		return isAdded;
	}

	@Override
	public boolean remove(IObservable obj) {
		boolean isRemoved = subscribers.remove(obj);
		return isRemoved;
	}

	@Override
	public void notifyObservers(String data, Priority priority) {
		final Notification notification = new Notification(UUID.randomUUID(), data, priority);
		final HashMap<Future<Boolean>, RetryableTask<Boolean>> futureMap = new HashMap<>();
		
		for(IObservable subscriber : subscribers) {
			final RetryableTask<Boolean> retryableTask = new RetryableTask<>(() -> {
				try {					
					notifService.notifyUser(subscriber, notification);
					return true;
				}
				catch(Exception e) {
					return false;
				}
			});

			Future<Boolean> submittedFuture = fixedThreadPool.submit(() -> {
				return retryableTask.call();
			});
			
			futureMap.put(submittedFuture, retryableTask);
		}
		retrySerivce.retry(futureMap);
	}
	
	public void shutdown() {
		fixedThreadPool.shutdown();
	}
}
