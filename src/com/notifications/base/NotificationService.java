package com.notifications.base;

import com.notifications.IObservable;

public class NotificationService {
	
	public void sendNotification(NotificationChannel notificationChannel, Notification notification,  IObservable user) throws Exception {
		switch(notificationChannel) {
			case SMS -> {
				INotificationChannel notif = new SMS();
				System.out.println("Sending SMS");
				notif.send(notification, user);
			}
			case EMAIL -> {
				INotificationChannel notif = new EmailChannel();
				System.out.println("Sending Email");
				notif.send(notification, user);
			}
			case PUSH -> {
				
			}
		}
	}
	
	public void notifyUser(IObservable user, Notification notification) throws Exception {
		user
		.getNotificationChannels()
		.forEach((channel) -> {
			try {
				//similate a 1/3 chance of failure
				if(Math.floor(Math.random() * 3) == 2) {
					throw new RuntimeException();
				}
				sendNotification(channel, notification , user);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				throw new RuntimeException(e.getMessage());
			}
		});
		
		user.update(notification);
	}
}
