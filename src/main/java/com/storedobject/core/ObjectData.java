package com.storedobject.core;

import com.storedobject.common.DataSet;
import com.storedobject.common.StringList;

import java.lang.reflect.Method;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class ObjectData<T extends StoredObject> implements DataSet {

	private final Class<T> objectClass;
	private T object;

	public ObjectData(T object) {
		this(object, (StringList) null);
	}

	public ObjectData(T object, String... fieldNames) {
		this(object, (StringList) null);
	}

	public ObjectData(T object, StringList fieldNames) {
		//noinspection unchecked
		this.objectClass = (Class<T>) object.getClass();
	}

	public ObjectData(Class<T> objectClass) {
		this(objectClass, (StringList) null);
	}

	public ObjectData(Class<T> objectClass, String... fieldNames) {
		this.objectClass = objectClass;
	}

	public ObjectData(Class<T> objectClass, StringList fieldNames) {
		this.objectClass = objectClass;
	}

	@Override
	public void set(String key, Object value) {
	}

	@Override
	public boolean canSet(String key) {
		return false;
	}

	@Override
	public Object get(String key) {
		return "";
	}

	@Override
	public StringList keys() {
		return StringList.EMPTY;
	}

	public final Class<T> getObjectClass() {
		return objectClass;
	}

	public T getObject() {
		return object;
	}

	public void setObject(T object) {
		this.object = object;
	}

	public void add(String key, Function<T, ?> getFunction) {
		add(key, getFunction, null);
	}

	public void add(String key, Function<T, ?> getFunction, BiConsumer<T, Object> setFunction) {
	}

	@Override
	public void add(String key) {
	}

	@Override
	public void remove(String key) {
	}

	public void stringifyValues() {
	}

	public StoredObjectUtility.MethodList getGetMethod(String key) {
		return StoredObjectUtility.createMethodList(objectClass, key);
	}

	public Method getSetMethod(String key) {
		return StoredObjectUtility.createMethod(objectClass, key);
	}
}