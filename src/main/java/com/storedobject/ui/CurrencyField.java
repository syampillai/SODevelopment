package com.storedobject.ui;

import com.storedobject.core.Money;
import com.storedobject.vaadin.TextField;
import com.storedobject.vaadin.TranslatedField;
import org.vaadin.textfieldformatter.CustomStringBlockFormatter;

import java.util.Currency;

public class CurrencyField extends TextField {

    public CurrencyField() {
        this(null);
    }

    public CurrencyField(String label) {
        new CustomStringBlockFormatter(new int[] { 3 }, new String[] { }, CustomStringBlockFormatter.ForceCase.UPPER, null, false).
                extend(this);
        setLabel(label);
        setValue(Money.defaultCurrency.getCurrencyCode());
    }

    private static Currency curr(String value) {
        try {
            return Currency.getInstance(value.trim().toUpperCase());
        } catch (Throwable ignored) {
        }
        return null;
    }

    public Currency getCurrency() {
        return curr(getValue());
    }

    public void setCurrency(Currency currency) {
        setValue(currency == null ? "" : currency.getCurrencyCode());
    }

    @Override
    public boolean isInvalid() {
        return curr(getValue()) == null;
    }
}