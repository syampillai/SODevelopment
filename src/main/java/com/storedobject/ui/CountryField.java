package com.storedobject.ui;

import com.storedobject.common.Country;
import com.storedobject.vaadin.ComboField;
import com.storedobject.vaadin.TranslatedField;
import com.vaadin.flow.component.select.Select;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Field to accept a country's ISO code (2 characters) as a value. See {@link Country}.
 *
 * @author Syam
 */
public class CountryField extends TranslatedField<String, Country> {

    /**
     * Constructor.
     */
    public CountryField() {
        this(null);
    }

    /**
     * Constructor.
     *
     * @param label Label for the field.
     */
    public CountryField(String label) {
        super(new CField(), (f, c) -> c.getShortName(), (f, s) -> Country.get(s), null);
        setLabel(label);
        setValue(Application.getDefaultCountry());
        setPlaceholder("Select");
    }

    /**
     * Country for the current value.
     *
     * @return Country.
     */
    public Country getCountry() {
        return getField().getValue();
    }

    /**
     * Set the given country as the value.
     *
     * @param country Country to set.
     */
    public void setCountry(Country country) {
        setValue(country == null ? Application.getDefaultCountry() : country.getShortName());
    }

    /**
     * Set allowed country values. (Only countries from the given stream will be allowed as valid input).
     *
     * @param countries Allowed countries. (If <code>null</code> is set, full list will be allowed).
     */
    public void setAllowedValues(Stream<Country> countries) {
        List<Country> c = countries == null ? null : countries.collect(Collectors.toList());
        if(c == null || c.isEmpty()) {
            ((CField)getField()).setItems(Country.list());
        } else {
            Country country = getCountry();
            ((CField) getField()).setItems(c);
            if(country == null || !c.contains(country)) {
                setCountry(c.get(0));
            }
        }
    }

    /**
     * Set disallowed country values. (Only countries not containing in the the given stream
     * will be allowed as valid input).
     *
     * @param countries Disallowed countries. (If <code>null</code> is set, full list will be allowed).
     */
    public void setDisallowedValues(Stream<Country> countries) {
        List<Country> cList = countries == null ? null : countries.collect(Collectors.toList());
        if(cList == null || cList.isEmpty()) {
            ((CField)getField()).setItems(Country.list());
        } else {
            List<Country> cListNew = Country.list().stream().filter(c -> !cList.contains(c)).collect(Collectors.toList());
            Country country = getCountry();
            if(country == null || !cListNew.contains(country)) {
                country = cListNew.get(0);
            } else {
                country = null;
            }
            ((CField)getField()).setItems(cListNew);
            if(country != null) {
                setCountry(country);
            }
        }
    }

    /**
     * Set placeholder for this field.
     *
     * @param placeholder Placeholder.
     */
    public void setPlaceholder(String placeholder) {
        ((CField)getField()).setPlaceholder(placeholder);
    }

    /**
     * Get the placeholder of this field.
     * @return Placeholder.
     */
    public String getPlaceholder() {
        return ((CField)getField()).getPlaceholder();
    }

    private static class CField extends ComboField<Country> {

        private CField() {
            super(Country.list());
            setItemLabelGenerator(c -> c.getShortName() + " " + c.getFlag() + " " + c.getName());
            setClearButtonVisible(true);
        }
    }
}