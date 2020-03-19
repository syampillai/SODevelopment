package com.storedobject.ui;

import com.storedobject.vaadin.CustomTextField;

public class MinutesField extends CustomTextField<Integer> {

    public MinutesField() {
        this(null);
    }

    public MinutesField(String label) {
        this(label, null);
    }

    public MinutesField(String label, Integer initialValue) {
        super(0);
    }

    public void setTrimToDay(boolean trimToDay) {
    }

    public boolean isTrimToDay() {
        return false;
    }

    public void setFreeFormat(boolean freeFormat) {
    }

    public boolean isFreeFormat() {
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

    public void setAllowDays(boolean allowDays) {
    }

    public boolean getAllowDays() {
        return false;
    }
}
