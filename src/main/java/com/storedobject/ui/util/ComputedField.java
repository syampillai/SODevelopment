package com.storedobject.ui.util;

import com.storedobject.common.ComputedValue;
import com.storedobject.vaadin.util.NumericField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Focusable;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.HasPrefixAndSuffix;

import java.util.Objects;

public class ComputedField<T extends ComputedValue<P>, P> extends CustomField<T> {

    private final Checkbox check = new Checkbox(true);
    private final HasValue<?, P> field;

    protected ComputedField(HasValue<?, P> field, String label, T defaultValue, T initialValue) {
        super(defaultValue);
        this.field = field;
        if(field instanceof HasPrefixAndSuffix) {
            ((HasPrefixAndSuffix)field).setPrefixComponent(this.check);
            add((Component)field);
        } else {
            HorizontalLayout h = new HorizontalLayout(check, (Component)field);
            h.setMargin(false);
            h.setSpacing(false);
            add(h);
        }
        setValue(initialValue);
        setLabel(label);
        check.addValueChangeListener(e -> {
            if(e.isFromClient()) {
                field.setReadOnly(isReadOnly() || !this.check.getValue());
                if (!isReadOnly() && this.check.getValue()) {
                    focusField();
                }
            }
        });
    }

    public void setPlaceholder(String placeholder) {
        if(field instanceof NumericField) {
            ((NumericField<?>) field).setPlaceholder(placeholder);
        }
    }

    @Override
    public void setValue(T value) {
        if(value == null) {
            value = clone(getEmptyValue());
        }
        super.setValue(value);
    }

    protected HasValue<?, P> getField() {
        return field;
    }

    private void focusField() {
        ((Focusable<?>)field).focus();
    }

    @Override
    public void focus() {
        if (this.check.getValue()) {
            focusField();
        } else {
            this.check.focus();
        }
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        check.setReadOnly(readOnly);
        getField().setReadOnly(readOnly || !check.getValue());
    }

    private T clone(T v) {
        try {
            //noinspection unchecked
            return v == null ? null : (T)v.clone();
        } catch (CloneNotSupportedException ignored) {
        }
        return null;
    }

    @Override
    protected T generateModelValue() {
        T value = clone(this.getEmptyValue());
        Objects.requireNonNull(value).setValue(getField().getValue());
        value.setManual(this.check.getValue());
        return value;
    }

    @Override
    protected void setPresentationValue(T value) {
        getField().setValue(value.getValueObject());
        check.setValue(value.isManual());
        field.setReadOnly(isReadOnly() || !check.getValue());
    }

    @Override
    public boolean isEmpty() {
        return getValue() == null;
    }
}