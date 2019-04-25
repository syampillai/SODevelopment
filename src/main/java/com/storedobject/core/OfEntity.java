package com.storedobject.core;

public interface OfEntity {

    public com.storedobject.core.Id getSystemEntityId();

    public com.storedobject.core.SystemEntity getSystemEntity();
    
	public default Id check(StoredObject object, Id systemEntityId) throws Exception {
		return null;
	}
}
