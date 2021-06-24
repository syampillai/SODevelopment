package com.storedobject.ui;

import com.storedobject.core.converter.SecondsValueConverter;
import com.storedobject.vaadin.CustomTextField;

public class SecondsField extends CustomTextField<Integer> {

    private static final Integer ZERO = 0;
    private int width = 8;
    private boolean trimToDay = false;

    public SecondsField() {
        this(null);
    }

    public SecondsField(String label) {
        this(label, ZERO);
    }

    public SecondsField(String label, Integer initialValue) {
        super(ZERO);
        setLength(width);
        setValue(initialValue);
        setLabel(label);
        setPattern();
        setWidth("10ch");
    }

    public void setTrimToDay(boolean trimToDay) {
        this.trimToDay = trimToDay;
    }

    public boolean isTrimToDay() {
        return trimToDay;
    }

    @Override
    protected Integer getModelValue(String string) {
        int v = SecondsValueConverter.parse(string);
        if(trimToDay) {
            v %= (24 * 60 * 60);
        }
        return v;
    }

    @Override
    protected String format(Integer value) {
        return SecondsValueConverter.format(value.intValue());
    }

    @Override
    public void setValue(Integer value) {
        super.setValue(value == null ? ZERO : value);
    }

    public final int getLength() {
        return width;
    }

    public void setLength(int width) {
        if(width < 8) {
            width = 8;
        }
        this.width = width;
        getField().setMaxLength(width);
    }

    private void setPattern() {
        String p;
        if(width == 8) {
            p = "[0-2]?[0-4]";
        } else {
            p = "\\d*";
        }
        p = "^" + p + "(\\:[0-5][0-9]?(\\:[0-5][0-9]?)?)?$";
        getField().setPattern(p);
        setPresentationValue(getValue());
    }
}
