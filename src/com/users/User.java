package com.users;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.notifications.IObservable;
import com.notifications.base.Notification;
import com.notifications.base.NotificationChannel;

public class User implements IObservable {
    private final UUID userId;
    private final List<Notification> notifications = new ArrayList<>();
    List<NotificationChannel> notificationChannels = new ArrayList<>();

    public User() {
        this.userId = UUID.randomUUID();
        this.notificationChannels.add(NotificationChannel.EMAIL);
    }

    @Override
    public void update(Notification notification) {
        notifications.add(0, notification); // newest on top
        System.out.println("[" + userId + "] New notification: " + notification.getMessage());
   
    }
    
    public List<Notification> getNotifications() {
    	return new ArrayList<Notification>(this.notifications);
    }
    

	@Override
	public List<NotificationChannel> getNotificationChannels() {
		// TODO Auto-generated method stub
    	return new ArrayList<NotificationChannel>(this.notificationChannels);
	}
    

}
