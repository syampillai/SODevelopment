package com.storedobject.ui;

import com.storedobject.vaadin.CustomField;
import com.vaadin.flow.component.HasText;

public class ELabelField extends CustomField<String> implements StyledBuilder, HasText {

    public ELabelField() {
        this(null);
    }

    public ELabelField(String label) {
        super("");
    }

    public ELabelField(String label, Object text, String... style) {
        this(label);
    }

    @Override
    protected String generateModelValue() {
        return null;
    }

    @Override
    protected void setPresentationValue(String value) {
    }

    @Override
    public void setValue(String value) {
    }

    @Override
    public void setText(String text) {
        setValue(text);
    }

    @Override
    public String getText() {
        return getValue();
    }

    @Override
    public void clear() {
    }

    @Override
    public StyledBuilder clearContent() {
        return this;
    }

    @Override
    public StyledBuilder getInternalStyledBuilder() {
        return null;
    }

    @Override
    public Application getApplication() {
        return null;
    }
}
