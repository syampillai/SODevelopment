package com.storedobject.ui;

import com.storedobject.core.Money;
import com.storedobject.vaadin.CustomTextField;

import java.util.Currency;

public class MoneyField extends CustomTextField<Money> {

    public MoneyField() {
        this((String)null);
    }

    public MoneyField(int width) {
        this(width, (Currency) null);
    }

    public MoneyField(int width, Currency currency) {
        this(null, width, currency);
    }

    public MoneyField(int width, String currency) {
        this(null, width, currency);
    }

    public MoneyField(String label) {
        this(label, 0, (Currency) null);
    }

    public MoneyField(String label, int width) {
        this(label, width, (Currency)null);
    }

    public MoneyField(String label, int width, String currency) {
        this(label, width, Money.getCurrency(currency));
    }

    public MoneyField(String label, int width, Currency currency) {
        this(currency == null ? new Money() : new Money(currency));
    }

    private MoneyField(Money defaultValue) {
        super(defaultValue);
    }

    @Override
    protected Money getModelValue(String string) {
        return null;
    }

    @Override
    protected String format(Money value) {
        return null;
    }

    @Override
    protected void setPresentationValue(Money value) {
    }

    public Currency getCurrency() {
        return null;
    }
}