package com.storedobject.core.converter;

public abstract class ValueConverter<T> {

	public T convert(Object value) {
		//noinspection unchecked
		return (T)value;
	}

	public abstract Class<T> getValueType();

	public String format(Object value) {
		return "";
	}
	
	public int getAlignment() {
		return -1;
	}
	
	public T getEmptyValue() {
		return convert("");
	}
	
	public String getEmptyTextValue() {
		return "";
	}
}