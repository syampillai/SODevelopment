package com.storedobject.core.converter;

import java.math.BigDecimal;

import com.storedobject.core.DecimalNumber;

public class DecimalNumberValueConverter extends ValueConverter<DecimalNumber> {

	private static DecimalNumberValueConverter instance = new DecimalNumberValueConverter();

	private DecimalNumberValueConverter() {
	}

	public static DecimalNumberValueConverter get() {
		return instance;
	}

	@Override
	public Class<DecimalNumber> getValueType() {
		return DecimalNumber.class;
	}

	@Override
	public DecimalNumber convert(Object value) {
		return null;
	}
	
	public int getAlignment() {
		return 1;
	}
}
