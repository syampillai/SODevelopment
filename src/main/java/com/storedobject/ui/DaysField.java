package com.storedobject.ui;

import com.storedobject.core.converter.DaysValueConverter;
import com.storedobject.vaadin.CustomTextField;

public class DaysField extends CustomTextField<Integer> {

    private static final Integer ZERO = 0;
    private int width = 5;

    public DaysField() {
        this(null);
    }

    public DaysField(String label) {
        this(label, null);
    }

    public DaysField(String label, Integer initialValue) {
        super(ZERO);
        setLength(width);
        setValue(initialValue);
        setLabel(label);
    }

    @Override
    public void setValue(Integer value) {
        super.setValue(value == null ? ZERO : value);
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
        return width;
    }

    public void setLength(int width) {
        if(width < 5) {
            width = 5;
        }
        this.width = width;
        getField().setMaxLength(width);
        setWidth((width + 3) + "ch");
    }
}
