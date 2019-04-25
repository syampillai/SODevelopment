package com.storedobject.core;

public interface ObjectChangeListener {

	public default void objectSaved(StoredObject object, TransactionControl transactionControl) {
	}

	public default void objectDeleted(StoredObject object, TransactionControl transactionControl) {
	}

	public default void objectCommitted(StoredObject object) {
	}

	public default void objectRolledback(StoredObject object) {
	}
}