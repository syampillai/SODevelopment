package com.storedobject.core;

public interface ContentSearch {
	public Object search(String pattern);
	public Object searchNext(String pattern, Object from);
}