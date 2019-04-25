package com.storedobject.core.converter;

public class MinutesValueConverter extends ValueConverter<Integer> {

	private MinutesValueConverter(String emptyValue, boolean allowDays) {
	}

	public static MinutesValueConverter get() {
		return null;
	}

	public static MinutesValueConverter create(String emptyValue) {
		return null;
	}

	public static MinutesValueConverter create(String emptyValue, boolean allowDays) {
		return null;
	}

	@Override
	public Class<Integer> getValueType() {
		return null;
	}

	public static String format(int minutes, boolean allowDays) {
		return null;
	}

	public static int parse(Object value) {
		return -1;
	}
}