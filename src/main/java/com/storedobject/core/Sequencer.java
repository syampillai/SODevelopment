package com.storedobject.core;

import java.util.concurrent.atomic.AtomicLong;

@SuppressWarnings("serial")
public class Sequencer extends AtomicLong {
	
	public Sequencer() {
	}
	
	public Sequencer(long startingNumber) {
	}
	
	public long current() {
		return 0;
	}
	
	public long next() {
		return 0;
	}
}