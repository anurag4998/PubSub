package com.notifications;

import com.notifications.base.Priority;

public interface ISubject {
	public boolean register(IObservable obj);
	
	public boolean remove(IObservable obj);
	
	public void notifyObservers(String data, Priority priority);
}
