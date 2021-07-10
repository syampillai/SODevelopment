package com.storedobject.ui;

import com.storedobject.core.Rate;
import com.storedobject.vaadin.BigDecimalField;
import com.storedobject.vaadin.TranslatedField;

import java.math.BigDecimal;

public class RateField extends TranslatedField<Rate, BigDecimal> {

    public RateField() {
        this(null);
    }

    public RateField(String label) {
        this(label, null);
    }

    public RateField(String label, Rate initialValue) {
        this(label, initialValue, 6);
    }

    public RateField(int decimals) {
        this(null, null, decimals);
    }

    public RateField(Rate initialValue, int decimals) {
        this(null, initialValue, decimals);
    }

    public RateField(String label, int decimals) {
        this(label, null, decimals);
    }

    public RateField(String label, Rate initialValue, int decimals) {
        this(label, initialValue, 18, decimals);
    }

    public RateField(int width, int decimals) {
        this(null, null, width, decimals);
    }

    public RateField(Rate initialValue, int width, int decimals) {
        this(null, initialValue, width, decimals);
    }

    public RateField(String label, int width, int decimals) {
        this(label, width, decimals, false, false);
    }

    public RateField(String label, Rate initialValue, int width, int decimals) {
        this(label, initialValue, width, decimals, false, false);
    }

    public RateField(int width, int decimals, boolean grouping) {
        this(null, null, width, decimals, grouping);
    }

    public RateField(Rate initialValue, int width, int decimals, boolean grouping) {
        this(null, initialValue, width, decimals, grouping);
    }

    public RateField(String label, int width, int decimals, boolean grouping) {
        this(label, null, width, decimals, grouping);
    }

    public RateField(String label, Rate initialValue, int width, int decimals, boolean grouping) {
        this(label, initialValue, width, decimals, grouping, false);
    }

    public RateField(int width, int decimals, boolean grouping, boolean allowNegative) {
        this(null, null, width, decimals, grouping, allowNegative);
    }

    public RateField(Rate initialValue, int width, int decimals, boolean grouping, boolean allowNegative) {
        this(null, initialValue, width, decimals, grouping, allowNegative);
    }

    public RateField(String label, int width, int decimals, boolean grouping, boolean allowNegative) {
        this(label, null, width, decimals, grouping, allowNegative);
    }

    public RateField(String label, Rate initialValue, int width, int decimals, boolean grouping, boolean allowNegative) {
        super(new BigDecimalField(width, decimals, grouping, allowNegative),
                (f, bd) -> bd.signum() == 0 ? null : new Rate(bd, decimals),
                (f, dn) -> dn == null ? BigDecimal.ZERO : dn.getValue(), null);
        setLabel(label);
        setValue(initialValue == null ? Rate.ONE : initialValue);
    }

    @Override
    public BigDecimalField getField() {
        return (BigDecimalField) super.getField();
    }

    public final int getDecimals() {
        return getField().getDecimals();
    }

    public void setLength(int width) {
        getField().setLength(width);
    }
}