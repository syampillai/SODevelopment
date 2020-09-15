package com.storedobject.ui;

import com.storedobject.common.Country;
import com.storedobject.vaadin.TranslatedField;

import java.util.stream.Stream;

public class CountryField extends TranslatedField<String, Country> {

    public CountryField() {
        this(null);
    }

    public CountryField(String label) {
        //noinspection ConstantConditions
        super(null, null, null, "");
    }

    public Country getCountry() {
        return null;
    }

    public void setCountry(Country country) {
    }

    public void setAllowedValues(Stream<Country> countries) {
    }

    public void setDisallowedValues(Stream<Country> countries) {
    }
}