package com.storedobject.core;

import java.util.concurrent.atomic.AtomicBoolean;

@SuppressWarnings("serial")
public class Condition extends AtomicBoolean {
	
	public Condition() {
	}
	
	public boolean ok() {
		return false;
	}
	
	public void stop() {
	}
}