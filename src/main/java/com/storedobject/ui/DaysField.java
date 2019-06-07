package com.storedobject.ui;

import com.storedobject.core.converter.DaysValueConverter;
import com.storedobject.vaadin.CustomTextField;

public class DaysField extends CustomTextField<Integer> {

    public DaysField() {
        this(null);
    }

    public DaysField(String label) {
        this(label, null);
    }

    public DaysField(String label, Integer initialValue) {
        super(0);
    }

    @Override
    protected Integer getModelValue(String string) {
        return DaysValueConverter.parse(getField().getValue());
    }

    @Override
    protected String format(Integer value) {
        return DaysValueConverter.format(value.intValue());
    }

    public final int getLength() {
        return 0;
    }

    public void setLength(int width) {
    }
}
