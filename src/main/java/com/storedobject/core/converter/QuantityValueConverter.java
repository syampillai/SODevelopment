package com.storedobject.core.converter;

import com.storedobject.core.Quantity;

public class QuantityValueConverter extends ValueConverter<Quantity> {

    private QuantityValueConverter() {
    }

    public static QuantityValueConverter get() {
        return new QuantityValueConverter();
    }

    @Override
    public Class<Quantity> getValueType() {
        return Quantity.class;
    }
}
