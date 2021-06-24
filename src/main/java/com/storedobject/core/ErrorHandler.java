package com.storedobject.core;

@FunctionalInterface
public interface ErrorHandler {
	public void handle(Throwable error);
}