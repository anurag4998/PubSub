package com.notifications.base;

import java.util.List;
import java.util.function.Predicate;

import com.users.User;

public class NotificationHelper {
	
	public List<Notification> getNotifications(User user) {
		return user.getNotifications();
	}
	
	public List<Notification> getNotificationByPredicate(User user, Predicate<Notification> notificationFiltrPrdcat) {
		List<Notification> list = user.getNotifications()
		.stream()
		.filter(notification -> notificationFiltrPrdcat.test(notification))
		.toList();
		return list;
	}
	
	
}
