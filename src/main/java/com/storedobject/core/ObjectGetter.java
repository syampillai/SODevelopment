package com.storedobject.core;

public interface ObjectGetter<T extends StoredObject> {

	default T getObject() {
		Id id = getObjectId();
		if(id == null) {
			return null;
		}
		Class<T> klass = getObjectClass();
		if(klass != null) {
			return StoredObject.get(klass, id, isAllowAny());
		}
		//noinspection unchecked
		return (T)StoredObject.get(id);
	}

	default Id getObjectId() {
		StoredObject so = getObject();
		return so == null ? null : so.getId();
	}

	default boolean isAllowAny() {
		return false;
	}

	default Class<T> getObjectClass() {
		return null;
	}
}