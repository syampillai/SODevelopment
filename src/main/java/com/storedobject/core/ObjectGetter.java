package com.storedobject.core;

public interface ObjectGetter {
	
    default StoredObject getObject() {
    	Id id = getObjectId();
    	if(id == null) {
    		return null;
    	}
    	Class<? extends StoredObject> klass = getObjectClass();
    	if(klass != null) {
    		return StoredObject.get(klass, id, isAllowAny());
    	}
    	return StoredObject.get(id);
    }
    
    default Id getObjectId() {
    	StoredObject so = getObject();
    	return so == null ? null : so.getId();
    }
    
    default boolean isAllowAny() {
    	return false;
    }
    
    default Class<? extends StoredObject> getObjectClass() {
    	return null;
    }
}