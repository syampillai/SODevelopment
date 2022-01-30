package com.storedobject.ui;

import com.storedobject.common.Country;
import com.storedobject.common.PhoneNumber;
import com.storedobject.common.SOException;
import com.storedobject.vaadin.Clickable;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.textfield.TextField;
import org.vaadin.textfieldformatter.phone.PhoneI18nFieldFormatter;

import java.util.*;

/**
 * Field to accept phone numbers.
 *
 * @author Syam
 */
public class PhoneField extends CustomField<String> {

    private List<Country> allowedCountries = null;
    private final TextField field = new TextField();
    private PhoneI18nFieldFormatter formatter;
    private Country country;
    private final ELabel prefix = new ELabel();

    /**
     * Constructor.
     */
    public PhoneField() {
        this(null);
    }

    /**
     * Constructor.
     *
     * @param label Label.
     */
    public PhoneField(String label) {
        super("");
        field.setPrefixComponent(prefix);
        field.setWidthFull();
        add(field);
        setCountry(null);
        setLabel(label);
        new Clickable<>(prefix, e -> selectCountry());
    }

    private static Country defaultCountry() {
        Country country = null;
        Application a = Application.get();
        if(a != null) {
            country = Country.get(a.getTransactionManager().getCountry());
        }
        return country == null ? Country.get("IN") : country;
    }

    /**
     * Set the country of the phone number.
     *
     * @param country Country.
     */
    public void setCountry(Country country) {
        if(country == null) {
            if(this.country != null) {
                return;
            }
            country = defaultCountry();
        }
        if(this.country == country) {
            return;
        }
        this.country = country;
        if(formatter != null) {
            formatter.remove();
        }
        formatter = new PhoneI18nFieldFormatter(country.getShortName());
        formatter.extend(field);
        prefix.setText(country.getShortName() + " " + country.getFlag());
        prefix.update();
        prefix.getElement().setAttribute("title", country.getName());
        String v = field.getValue();
        String p = prefix();
        if(!v.startsWith(p)) {
            field.setValue(p + " ");
            if(!field.isReadOnly() && field.isEnabled()) {
                field.focus();
            }
        } else if(!v.startsWith(p + " ")) {
            field.setValue(v.substring(0, p.length()) + " " + v.substring(p.length()));
            setValue(field.getValue());
        }
    }

    /**
     * Get the current country.
     *
     * @return Country.
     */
    public Country getCountry() {
        return country;
    }

    private String prefix() {
        return "+" + getCountry().getISDCode();
    }

    @Override
    protected String generateModelValue() {
        String v = field.getValue().trim();
        if(!v.startsWith("+")) {
            v = "+" + v;
            field.setValue(v);
        }
        return v;
    }

    @Override
    public void setValue(String value) {
        if(value == null || value.isEmpty() || value.equals("+")) {
            value = prefix();
        } else if(!value.startsWith("+")) {
            value = "+" + value;
        }
        super.setValue(value);
        if(isInvalid()) {
            adjust();
        }
    }

    private void adjust() {
        String p = prefix();
        String v = field.getValue();
        if(v.startsWith(p) && !v.startsWith(p + " ")) {
            field.setValue(v.substring(0, p.length()) + " " + v.substring(p.length()));
            setValue(field.getValue());
        }
    }

    @Override
    public String getValue() {
        return isEmpty() ? "" : super.getValue();
    }

    @Override
    public boolean isEmpty() {
        return isEmpty(field.getValue());
    }

    private boolean isEmpty(String v) {
        return v.isEmpty() || "+".equals(v) || prefix().equals(v.trim());
    }

    @Override
    public boolean isInvalid() {
        String v = field.getValue();
        if(isEmpty(v)) {
            return false;
        }
        String p = prefix();
        if(!v.startsWith(p + " ")) {
            if(v.startsWith(p)) {
                v = p + " " + v.substring(p.length());
                field.setValue(v);
            } else {
                return true;
            }
        }
        try {
            PhoneNumber.check(v);
            return false;
        } catch (SOException ignored) {
        }
        return true;
    }

    @Override
    protected void setPresentationValue(String s) {
        field.setValue(s);
        setCountry(match(false));
    }

    @Override
    protected void updateValue() {
        setCountry(match(false));
        super.updateValue();
    }

    private void selectCountry() {
        if(isReadOnly() || !isEnabled()) {
            return;
        }
        setCountry(match(true));
        field.focus();
    }

    private Country match(boolean next) {
        String v = generateModelValue();
        Country c = next ? country.getNextByPhoneNumber(v) : Country.getByPhoneNumber(v);
        if(c == null || (allowedCountries != null && !allowedCountries.contains(c))) {
            field.setValue(prefix() + " ");
            field.focus();
            return null;
        }
        return c;
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        field.setReadOnly(readOnly);
    }

    private void checkCountry() {
        if(allowedCountries != null && !allowedCountries.contains(country)) {
            setCountry(allowedCountries.get(0));
        }
    }

    /**
     * Restrict allowed countries to a given set of values.
     *
     * @param allowedCountries Allowed countries.
     */
    public void setAllowedCountries(Collection<Country> allowedCountries) {
        if(allowedCountries == null || allowedCountries.isEmpty()) {
            this.allowedCountries = null;
        } else {
            this.allowedCountries = new ArrayList<>(allowedCountries);
            checkCountry();
        }
    }

    /**
     * Restrict allowed countries to a given set of values.
     *
     * @param allowedCountries Allowed countries.
     */
    public void setAllowedCountries(Country... allowedCountries) {
        if(allowedCountries == null || allowedCountries.length == 0) {
            this.allowedCountries = null;
        } else {
            this.allowedCountries = Arrays.asList(allowedCountries);
            checkCountry();
        }
    }

    /**
     * Restrict allowed countries to a given set of values.
     *
     * @param allowedCountries Allowed countries.
     */
    public void setAllowedCountries(String... allowedCountries) {
        if(allowedCountries == null || allowedCountries.length == 0) {
            this.allowedCountries = null;
        } else {
            List<Country> countries = new ArrayList<>();
            Country country;
            for(String c: allowedCountries) {
                country = Country.get(c);
                if(country != null) {
                    countries.add(country);
                }
            }
            setAllowedCountries(countries);
        }
    }
}