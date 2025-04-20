package com.notification.retry;

import java.util.concurrent.Callable;

public class RetryableTask<V> implements Callable<V> {
	
	private final Callable<V> task;
	
	public RetryableTask(Callable<V> task) {
		// TODO Auto-generated constructor stub
		this.task = task;
	}

	@Override
	public V call() throws Exception {
		// TODO Auto-generated method stub
		return (V)task.call();
	}
	
	public Callable<V> getTask() {
		return this.task;
	}
	

}
