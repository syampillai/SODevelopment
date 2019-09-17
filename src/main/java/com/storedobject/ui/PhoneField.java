package com.storedobject.ui;

import com.storedobject.common.Country;
import com.vaadin.flow.component.customfield.CustomField;

public class PhoneField extends CustomField<String> {

    public PhoneField() {
        this(null);
    }

    public PhoneField(String label) {
        super("");
    }

    public void setCountry(Country country) {
    }

    public Country getCountry() {
        return null;
    }

    private String prefix() {
        return "+" + getCountry().getISDCode();
    }

    @Override
    protected String generateModelValue() {
        return null;
    }

    @Override
    protected void setPresentationValue(String s) {
    }
}