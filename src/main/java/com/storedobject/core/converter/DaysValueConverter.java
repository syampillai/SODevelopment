package com.storedobject.core.converter;

public class DaysValueConverter extends ValueConverter<Integer> {

	private DaysValueConverter(String emptyValue) {
	}

	public static DaysValueConverter create(String emptyValue) {
		return null;
	}

	public static String format(int days) {
		return null;
	}

	public static int parse(Object value) {
		return 0;
	}

	@Override
	public Class<Integer> getValueType() {
		return null;
	}
}