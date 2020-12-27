package com.storedobject.ui;

import com.storedobject.common.Email;
import com.storedobject.common.SOException;

public class EmailField extends com.vaadin.flow.component.textfield.EmailField {

    public EmailField() {
    }

    public EmailField(String label) {
        super(label);
    }

    public EmailField(String label, String placeholder) {
        super(label, placeholder);
    }

    @Override
    public boolean isInvalid() {
        if(isEmpty()) {
            return false;
        }
        if(super.isInvalid()) {
            return true;
        }
        try {
            Email.check(getValue());
            return false;
        } catch (SOException ignored) {
        }
        return true;
    }
}
