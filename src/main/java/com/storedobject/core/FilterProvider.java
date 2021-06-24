package com.storedobject.core;

@FunctionalInterface
public interface FilterProvider {
	
	String getFilterCondition();
}