package com.storedobject.core;

import com.storedobject.common.StringList;
import com.storedobject.common.ToString;
import com.storedobject.core.StoredObjectUtility.MethodList;

@FunctionalInterface
public interface ObjectToString<T extends StoredObject> extends ToString<T> {

	@Override
	String toString(T object);

	static <O extends StoredObject> ObjectToString<O> create(Class<O> objectClass, String... attributes) {
		return new O2S<>();
	}

	static <O extends StoredObject> ObjectToString<O> create(Class<O> objectClass, String delimiter, StringList attributes) {
		return new O2S<>();
	}

	static <O extends StoredObject> ObjectToString<O> create(Class<O> objectClass, StringList attributes) {
		return new O2S<>();
	}

	static <O extends StoredObject> ObjectToString<O> create(Class<O> objectClass, StringList attributes, boolean showAttributes) {
		return new O2S<>();
	}

	static <O extends StoredObject> ObjectToString<O> create(Class<O> objectClass, String delimiter, StringList attributes, boolean showAttributes) {
		return new O2S<>();
	}

	static <O extends StoredObject> ObjectToString<O> create(Class<O> objectClass, MethodList displayMethod) {
		return new O2S<>();
	}

	class O2S<O extends StoredObject> implements ObjectToString<O> {

		@Override
		public String toString(O object) {
			return null;
		}
	}
}
