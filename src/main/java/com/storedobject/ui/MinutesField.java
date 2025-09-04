package com.storedobject.ui;

import com.storedobject.core.converter.MinutesValueConverter;
import com.storedobject.vaadin.CustomTextField;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.textfield.TextField;

public class MinutesField extends CustomTextField<Integer> {

    private static final Integer ZERO = 0;
    private int width;
    private boolean allowDays = false;
    private boolean trimToDay = false;

    public MinutesField() {
        this(null);
    }

    public MinutesField(String label) {
        this(label, null);
    }

    public MinutesField(String label, Integer initialValue) {
        super(ZERO);
        setLength(0);
        setValue(initialValue);
        setLabel(label);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        Application a = Application.get();
        if(a != null) {
            ((TextField) getField()).setAutoselect(!a.getWebBrowser().isAndroid());
        }
    }

    public void setTrimToDay(boolean trimToDay) {
        this.trimToDay = trimToDay;
    }

    public boolean isTrimToDay() {
        return trimToDay;
    }

    @Override
    public void setValue(Integer value) {
        int v = value == null ? 0 : value;
        if(trimToDay) {
            v %= (24 * 60);
        }
        super.setValue(v);
    }

    @Override
    protected Integer getModelValue(String string) {
        int v = MinutesValueConverter.parse(getField().getValue());
        if(trimToDay) {
            v %= (24 * 60);
        }
        return v;
    }

    @Override
    protected String format(Integer value) {
        return pad(MinutesValueConverter.format(value, allowDays));
    }

    private String pad(String v) {
        if(v.length() > width) {
            width = v.length();
            setPattern();
        }
        return v;
    }

    public final int getLength() {
        return width;
    }

    public void setLength(int width) {
        if(width < 5) {
            width = 5;
        }
        if(allowDays && width < 8) {
            width = 8;
        }
        if(this.width == width) {
            return;
        }
        this.width = width;
        setPattern();
    }

    public void setAllowDays(boolean allowDays) {
        if(this.allowDays == allowDays) {
            return;
        }
        this.allowDays = allowDays;
        setPattern();
    }

    public boolean getAllowDays() {
        return allowDays;
    }

    private void setPattern() {
        setPresentationValue(getValue());
    }
}