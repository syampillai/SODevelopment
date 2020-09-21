package com.storedobject.core.converter;

public class IntegerValueConverter extends ValueConverter<Integer> {

    private IntegerValueConverter() {
    }

    public static IntegerValueConverter get() {
        return new IntegerValueConverter();
    }

    @Override
    public Class<Integer> getValueType() {
        return Integer.class;
    }
}
