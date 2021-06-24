package com.storedobject.core.converter;

import com.storedobject.core.Rate;

public class RateValueConverter extends ValueConverter<Rate> {

    private RateValueConverter() {
    }

    public static RateValueConverter get() {
        return new RateValueConverter();
    }

    @Override
    public Class<Rate> getValueType() {
        return Rate.class;
    }
}
