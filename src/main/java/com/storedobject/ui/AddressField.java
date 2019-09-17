package com.storedobject.ui;

import com.storedobject.common.Country;
import com.vaadin.flow.component.customfield.CustomField;

public class AddressField extends CustomField<String> {

    public AddressField() {
        this(null);
    }

    public AddressField(String label) {
    }

    @Override
    protected String generateModelValue() {
        return null;
    }

    @Override
    protected void setPresentationValue(String s) {
    }

    public void setCountry(Country country) {
    }

    public Country getCountry() {
        return null;
    }
}
