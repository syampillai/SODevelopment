package com.storedobject.ui;

import com.storedobject.vaadin.CustomTextField;

public class SecondsField extends CustomTextField<Integer> {

    private static final Integer ZERO = 0;

    public SecondsField() {
        this(null);
    }

    public SecondsField(String label) {
        this(label, ZERO);
    }

    public SecondsField(String label, Integer initialValue) {
        super(ZERO);
    }

    public void setTrimToDay(boolean trimToDay) {
    }

    public boolean isTrimToDay() {
        return false;
    }

    @Override
    protected Integer getModelValue(String string) {
        return null;
    }

    @Override
    protected String format(Integer value) {
        return null;
    }

    public final int getLength() {
        return 0;
    }

    public void setLength(int width) {
    }
}
