package com.storedobject.ui;

import com.storedobject.common.Country;
import com.storedobject.common.PhoneNumber;
import com.storedobject.common.SOException;
import com.storedobject.core.SystemEntity;
import com.storedobject.vaadin.Clickable;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.textfield.TextField;
import org.vaadin.textfieldformatter.phone.PhoneI18nFieldFormatter;

public class PhoneField extends CustomField<String> {

    private final TextField field = new TextField();
    private PhoneI18nFieldFormatter formatter;
    private Country country;
    private final ELabel prefix = new ELabel();

    public PhoneField() {
        this(null);
    }

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
        Country country = Country.get(SystemEntity.systemCountry);
        return country == null ? Country.get("IN") : country;
    }

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
        if(!v.startsWith(prefix() + " ")) {
            return true;
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
        if(c == null) {
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
}