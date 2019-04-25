package com.storedobject.core;

import com.storedobject.common.DataSet;
import com.storedobject.common.StringList;

public class ObjectDataSet<T extends StoredObject> implements DataSet {
	
	public ObjectDataSet(T object) {
	}
	
	public ObjectDataSet(T object, StringList fieldNames) {
	}
	
	public ObjectDataSet(Class<T> objectClass) {
	}
	
	public ObjectDataSet(Class<T> objectClass, StringList fieldNames) {
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
		return null;
	}

	@Override
	public StringList keys() {
		return null;
	}

	public T getObject() {
		return null;
	}

	public void setObject(T object) {
	}

	@Override
	public void add(String key) {
	}

	@Override
	public void remove(String key) {
	}
}