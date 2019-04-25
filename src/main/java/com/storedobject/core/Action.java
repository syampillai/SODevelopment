package com.storedobject.core;

@FunctionalInterface
public interface Action extends Runnable {
	
	void act();
	
	default void run() {
		act();
	}
}