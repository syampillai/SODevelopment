package com.storedobject.ui;

import com.storedobject.common.JSON;
import com.storedobject.vaadin.CustomTextField;
import com.storedobject.vaadin.RequiredField;
import com.storedobject.vaadin.TextArea;
import com.storedobject.vaadin.util.HasTextValue;

import java.io.IOException;

public class JSONField extends CustomTextField<JSON> implements RequiredField {

    private boolean required = false;
    private final TA textArea = new TA();

    /**
     * Constructor.
     */
    public JSONField() {
        this(null);
    }

    /**
     * Constructor.
     *
     * @param label Field label.
     */
    public JSONField(String label) {
        super(JSON.create());
        setLabel(label);
    }

    @Override
    protected JSON getModelValue(String string) {
        return JSON.create(string);
    }

    @Override
    public void setRequired(boolean required) {
        this.required = required;
        textArea.setRequired(required);
    }

    @Override
    protected String format(JSON value) {
        return required && value.isNull() ? JSON.create().toPrettyString() : value.toPrettyString();
    }

    @Override
    protected HasTextValue createField() {
        return textArea;
    }

    private static class TA extends TextArea implements HasTextValue {

        TA() {
            setWidthFull();
        }

        @Override
        public void setPattern(String pattern) {
        }
    }
}
