package com.storedobject.core;

import java.util.function.Consumer;

@FunctionalInterface
public interface ObjectSetter<T extends StoredObject> extends Consumer<T> {

	void setObject(T object);

	@Override
	default void accept(T object) {
		setObject(object);
	}

	default void setObject(Id objectId) {
		Class<T> klass = getObjectClass();
		if(klass != null) {
			T object = StoredObject.get(klass, objectId, isAllowAny());
			setObject(object);
		} else {
			//noinspection unchecked
			setObject((T)StoredObject.get(objectId));
		}
	}

	default Class<T> getObjectClass() {
		return null;
	}

	default boolean isAllowAny() {
		return false;
	}
}