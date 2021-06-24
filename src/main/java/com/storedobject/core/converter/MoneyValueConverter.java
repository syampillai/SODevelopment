package com.storedobject.core.converter;

import com.storedobject.core.Money;

public class MoneyValueConverter extends ValueConverter<Money> {

    private MoneyValueConverter() {
    }

    public static MoneyValueConverter get() {
        return new MoneyValueConverter();
    }

    @Override
    public Class<Money> getValueType() {
        return Money.class;
    }
}
