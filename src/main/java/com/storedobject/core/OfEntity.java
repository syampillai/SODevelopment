package com.storedobject.core;

public interface OfEntity {

    Id getSystemEntityId();

    SystemEntity getSystemEntity();
    
	default Id check(TransactionManager tm, StoredObject object, Id systemEntityId) throws Exception {
		return null;
	}

	default Id findSystemEntityId() {
		return null;
	}
}
