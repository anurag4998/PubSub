package com.notifications;

import java.util.List;

import com.notifications.base.Notification;
import com.notifications.base.NotificationChannel;

public interface IObservable {
	public void update(Notification notification);
	
	public List<NotificationChannel> getNotificationChannels();

}
