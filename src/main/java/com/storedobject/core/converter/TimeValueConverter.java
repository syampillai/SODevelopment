package com.storedobject.core.converter;

import java.util.Date;

public class TimeValueConverter extends ValueConverter<Date> {

    private static final TimeValueConverter instance = new TimeValueConverter();

    private TimeValueConverter() {
    }

    @Override
    public Class<Date> getValueType() {
        return Date.class;
    }

    public static TimeValueConverter get() {
        return instance;
    }
}
