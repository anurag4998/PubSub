package com.notification.retry;

import java.util.List;
import java.util.concurrent.Future;

public interface IRetry<V> {
	public void retry(List<RetryableTask<V>> tasks);
}
