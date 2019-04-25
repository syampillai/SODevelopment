package com.storedobject.core;

import java.util.function.Function;

import com.storedobject.core.StoredObjectUtility.MethodList;

public interface ObjectToString<T extends StoredObject> extends Function<T, String> {
	
	public static <O extends StoredObject> ObjectToString<O> create(Class<O> objectClass, String fieldName) {
		return null;
	}
	
	public static <O extends StoredObject> ObjectToString<O> create(Class<O> objectClass, MethodList displayMethod) {
		return null;
	}
}
