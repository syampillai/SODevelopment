package com.storedobject.ui;

import com.vaadin.flow.component.customfield.CustomField;

import java.util.Currency;

public class CurrencyField extends CustomField<String> {

    public CurrencyField() {
        this(null);
    }

    public CurrencyField(String label) {
    }

    @Override
    protected String generateModelValue() {
        return null;
    }

    @Override
    protected void setPresentationValue(String s) {
    }

    public void setCurrency(Currency currency) {
    }

    public Currency getCurrency() {
        return null;
    }
}