package com.driver;

import com.notifications.IObservable;
import com.notifications.Publisher;
import com.notifications.base.Priority;
import com.users.User;

public class Driver {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Publisher publisher = new Publisher();
		IObservable user = new User();
		
		IObservable user2 = new User();
		IObservable user3 = new User();
		
		publisher.register(user3);
		publisher.register(user2);
		publisher.register(user);
		
		publisher.notifyObservers("Hello Anurag", Priority.HIGH);
		
		publisher.shutdown();
	}

}
