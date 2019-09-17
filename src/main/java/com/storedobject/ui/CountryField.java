package com.storedobject.ui;

import com.storedobject.common.Country;
import com.storedobject.vaadin.TranslatedField;

public class CountryField extends TranslatedField<String, Country> {

    public CountryField() {
        this(null);
    }

    public CountryField(String label) {
        super(null, null, null, "");
    }

    public Country getCountry() {
        return null;
    }

    public void setCountry(Country country) {
    }
}