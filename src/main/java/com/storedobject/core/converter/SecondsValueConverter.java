package com.storedobject.core.converter;

public class SecondsValueConverter extends ValueConverter<Integer> {

	private SecondsValueConverter(String emptyValue) {
	}

	public static SecondsValueConverter get() {
		return null;
	}

	public static SecondsValueConverter create(String emptyValue) {
		return null;
	}

	@Override
	public Class<Integer> getValueType() {
		return null;
	}

	public static String format(int minutes) {
		return null;
	}

	public static int parse(Object value) {
		return -1;
	}
}