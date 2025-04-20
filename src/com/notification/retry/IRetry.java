package com.notification.retry;

import java.util.List;

public interface IRetry<V> {
	public void retry(List<RetryableTask<V>> tasks);
}
