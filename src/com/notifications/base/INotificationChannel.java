package com.notifications.base;

import com.notifications.IObservable;

public interface INotificationChannel {
	void send(Notification notification, IObservable user);
}
