package com.storedobject.core.converter;

public class SecondsValueConverter extends ValueConverter<Integer> {

	private SecondsValueConverter(String emptyValue) {
	}

	public static SecondsValueConverter get() {
		return create("");
	}

	public static SecondsValueConverter create(String emptyValue) {
		return new SecondsValueConverter("");
	}

	@Override
	public Class<Integer> getValueType() {
		return Integer.class;
	}

	@Override
	public Integer convert(Object value) {
		return 0;
	}

	public static String format(int seconds) {
		return "";
	}

	public static int parse(Object value) {
		return 0;
	}
}