package com.storedobject.core.converter;

public class MinutesValueConverter extends ValueConverter<Integer> {

	private MinutesValueConverter(String emptyValue, boolean allowDays) {
	}

	public static MinutesValueConverter get() {
		return new MinutesValueConverter("", false);
	}

	public static MinutesValueConverter create(String emptyValue) {
		return get();
	}

	public static MinutesValueConverter create(String emptyValue, boolean allowDays) {
		return get();
	}

	@Override
	public Class<Integer> getValueType() {
		return Integer.class;
	}

	public static String format(int minutes, boolean allowDays) {
		return "";
	}

	public static int parse(Object value) {
		return -1;
	}
}