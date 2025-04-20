package com.notifications.base;

import com.notifications.IObservable;
import com.users.User;

public interface INotificationChannel {
	void send(Notification notification, IObservable user);
}
