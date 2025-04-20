package com.notifications.base;

import java.util.UUID;

public class Notification {
    private final UUID id;
    private final String message;
    private final long timestamp;
    private boolean isRead;
    private final Priority priority;

    public Notification(UUID id, String message, Priority priority) {
        this.id = id;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
        this.isRead = false;
        this.priority = priority;
    }

    public void markAsRead() {
        this.isRead = true;
    }

    public boolean isRead() {
        return isRead;
    }

    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public UUID getId() {
        return id;
    }
    
    public Priority getPriority() {
    	return this.priority;
    }
}
