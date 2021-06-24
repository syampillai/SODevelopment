package com.storedobject.ui;

import com.storedobject.core.StringUtility;
import com.storedobject.core.converter.MinutesValueConverter;
import com.storedobject.vaadin.CustomTextField;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.textfield.TextField;
import org.vaadin.textfieldformatter.CustomStringBlockFormatter;

public class MinutesField extends CustomTextField<Integer> {

    private static final Integer ZERO = 0;
    private int width;
    private boolean allowDays = true;
    private boolean trimToDay = false;
    private boolean freeFormat = true;
    private CustomStringBlockFormatter formatter;

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

    public void setFreeFormat(boolean freeFormat) {
        if(freeFormat != this.freeFormat) {
            this.freeFormat = freeFormat;
            setLength(width);
        }
    }

    public boolean isFreeFormat() {
        return freeFormat;
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
        return pad(MinutesValueConverter.format(value, allowDays && !freeFormat));
    }

    private String pad(String v) {
        if(!freeFormat) {
            if (v.length() < width) {
                v = StringUtility.padLeft(v, width, '0');
            }
        }
        if(v.length() > width) {
            width = v.length();
            changePattern();
        }
        return v;
    }

    public final int getLength() {
        return width;
    }

    public void setLength(int width) {
        if(freeFormat && width < 5) {
            width = 8;
        }
        if(width < 5) {
            width = 5;
        }
        if(allowDays) {
            if(width < 8) {
                width = 8;
            }
        }
        if(this.width == width && (freeFormat == (formatter == null))) {
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
        changePattern();
        setPresentationValue(getValue());
    }

    private void changePattern() {
        if(formatter != null) {
            formatter.remove();
        }
        if(freeFormat) {
            formatter = null;
            return;
        }
        if(allowDays) {
            formatter = new CustomStringBlockFormatter(new int[] { width - 7, 2, 2 }, new String[] { "D ", ":", ":" }, CustomStringBlockFormatter.ForceCase.UPPER, null, true);
        } else {
            formatter = new CustomStringBlockFormatter(new int[] { width - 3, 2 }, new String[] {  ":", ":" }, CustomStringBlockFormatter.ForceCase.UPPER, null, true);
        }
        formatter.extend((TextField) getField());
    }
}
