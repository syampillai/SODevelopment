package com.storedobject.ui;

import com.storedobject.vaadin.DisablePaste;

public class PasswordField extends com.vaadin.flow.component.textfield.PasswordField implements DisablePaste {

    public PasswordField() {
        disablePaste();
    }

    public PasswordField(String label) {
        super(label);
        disablePaste();
    }

    public PasswordField(String label, String placeholder) {
        super(label, placeholder);
        disablePaste();
    }

    public PasswordField(ValueChangeListener<? super ComponentValueChangeEvent<com.vaadin.flow.component.textfield.PasswordField, String>> listener) {
        super(listener);
        disablePaste();
    }

    public PasswordField(String label, ValueChangeListener<? super ComponentValueChangeEvent<com.vaadin.flow.component.textfield.PasswordField, String>> listener) {
        super(label, listener);
        disablePaste();
    }

    public PasswordField(String label, String initialValue, ValueChangeListener<? super ComponentValueChangeEvent<com.vaadin.flow.component.textfield.PasswordField, String>> listener) {
        super(label, initialValue, listener);
        disablePaste();
    }

    @Override
    public final String getValue() {
        String s = super.getValue();
        if(s.startsWith("OBF:")) {
            s = s.substring(4);
        }
        return s;
    }
}
