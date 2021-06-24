package com.storedobject.core;

@FunctionalInterface
public interface NewObject<T> {

	T newObject() throws Exception;

	default T newObject(TransactionManager tm) throws Exception {
		return newObject();
	}
}