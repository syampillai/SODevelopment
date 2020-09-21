package com.storedobject.core.converter;

public class LongValueConverter extends ValueConverter<Long> {

    private LongValueConverter() {
    }

    public static LongValueConverter get() {
        return new LongValueConverter();
    }

    @Override
    public Class<Long> getValueType() {
        return Long.class;
    }
}
