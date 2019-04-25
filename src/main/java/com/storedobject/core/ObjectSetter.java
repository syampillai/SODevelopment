package com.storedobject.core;

public interface ObjectSetter {
	
    void setObject(StoredObject object);
    
    default void setObject(Id objectId) {
    	Class<? extends StoredObject> klass = getObjectClass();
    	if(klass != null) {
    		StoredObject object = StoredObject.get(klass, objectId, isAllowAny());
    		setObject(object);
    	} else {
    		setObject(StoredObject.get(objectId));
    	}
    }
    
    default Class<? extends StoredObject> getObjectClass() {
    	return null;
    }
    
    default boolean isAllowAny() {
    	return false;
    }
}