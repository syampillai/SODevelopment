package com.storedobject.core;

public interface NewObject<T extends StoredObject> {
	
	public T newObject() throws Exception;
	
	public default T newObject(TransactionManager tm) throws Exception {
		return newObject();
	}
}