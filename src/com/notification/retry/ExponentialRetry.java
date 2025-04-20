package com.notification.retry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ExponentialRetry<V> implements IRetry<V> {
    private final static int maxTime = 2 << 10;
    ConcurrentHashMap<RetryableTask<V>, Integer> retryMap = new ConcurrentHashMap<>();
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    @Override
    public void retry(List<RetryableTask<V>> failedTasks) {
        if (failedTasks.size() == 0) {
            return;
        }
        List<RetryableTask<V>> failedTasksCopy = new ArrayList<>(failedTasks);

        failedTasksCopy.forEach(task -> {
            retryMap.put(task, retryMap.getOrDefault(task, 1) << 1);
        });

        List<Map<Future<V>, RetryableTask<V>>> mapOfFuture = failedTasksCopy
        .stream()
        .map((retryableTask) -> {
            int delay = retryMap.get(retryableTask);
            if(delay > maxTime) {
               return null;
            }
            Future<V> future = scheduler.schedule(retryableTask.getTask(), delay, TimeUnit.SECONDS);
            return Map.of(future, retryableTask);

        })  
        .filter(map -> map != null)
        .toList();

        List<RetryableTask<V>> retryableTasksCompleted = waitForFuturesToComplete(mapOfFuture);

        removeFromMap(retryableTasksCompleted);

        List<RetryableTask<V>> tasksPending = retryMap.keySet()
        .stream()
        .map(x -> x)
        .toList();
    
        tasksPending = tasksPending.stream()
                .filter(task -> retryMap.getOrDefault(task, 0) <= maxTime)
                .toList();

        if (tasksPending.size() > 0) {
            retry(tasksPending);
        } else {
            scheduler.shutdown();
        }            
    }

    private void removeFromMap(List<RetryableTask<V>> retryableTasksCompleted) {
        retryableTasksCompleted.forEach(task -> retryMap.remove(task));
    }

    private List<RetryableTask<V>> waitForFuturesToComplete(List<Map<Future<V>, RetryableTask<V>>> listOfMapsOfFuture) {
        List<RetryableTask<V>> completedTasks = new ArrayList<>();

        listOfMapsOfFuture.forEach((map) -> {
            for (Map.Entry<Future<V>, RetryableTask<V>> entry : map.entrySet()) {
                Future<V> future = entry.getKey();
                RetryableTask<V> task = entry.getValue();
                try {
                    Boolean result = (Boolean) future.get();
                    if(result) {
                        completedTasks.add(task);
                    }
                } catch (Exception e) {
                    e.printStackTrace();  
                }
            }
        });
        
        return completedTasks;
    }
    
}
