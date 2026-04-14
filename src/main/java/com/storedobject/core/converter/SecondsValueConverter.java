package com.storedobject.core.converter;

public class SecondsValueConverter extends ValueConverter<Long> {

	private SecondsValueConverter(String emptyValue) {
	}

	public static SecondsValueConverter get() {
		return create("");
	}

	public static SecondsValueConverter create(String emptyValue) {
		return new SecondsValueConverter("");
	}

	@Override
	public Class<Long> getValueType() {
		return Long.class;
	}

	@Override
	public Long convert(Object value) {
		return 0L;
	}

	public static String format(int seconds) {
		return "";
	}

	public static int parse(Object value) {
		return 0;
	}
}