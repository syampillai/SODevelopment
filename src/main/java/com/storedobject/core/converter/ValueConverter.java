package com.storedobject.core.converter;

public abstract class ValueConverter<T> {

	public T convert(Object value) {
		return null;
	}

	public abstract Class<T> getValueType();

	public String format(Object value) {
		return null;
	}
	
	public int getAlignment() {
		return -1;
	}
	
	public T getEmptyValue() {
		return null;
	}
	
	public String getEmptyTextValue() {
		return null;
	}
}