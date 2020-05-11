package com.storedobject.core;

import com.storedobject.common.StringList;
import com.storedobject.common.ToString;
import com.storedobject.core.StoredObjectUtility.MethodList;

public interface ObjectToString<T extends StoredObject> extends ToString<T> {

	@Override
	default String toString(T object) {
		return object.toDisplay();
	}

	static <O extends StoredObject> ObjectToString<O> create(Class<O> objectClass, String... attributes) {
		return new O2S<>();
	}

	static <O extends StoredObject> ObjectToString<O> create(Class<O> objectClass, String delimiter, StringList attributes) {
		return new O2S<>();
	}

	static <O extends StoredObject> ObjectToString<O> create(Class<O> objectClass, StringList attributes) {
		return new O2S<>();
	}

	static <O extends StoredObject> ObjectToString<O> create(Class<O> objectClass, MethodList displayMethod) {
		return new O2S<>();
	}

	class O2S<O extends StoredObject> implements ObjectToString<O> {
	}
}
