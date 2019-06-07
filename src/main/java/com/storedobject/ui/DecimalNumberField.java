package com.storedobject.ui;

import com.storedobject.core.DecimalNumber;
import com.storedobject.vaadin.BigDecimalField;
import com.storedobject.vaadin.TranslatedField;

import java.math.BigDecimal;

public class DecimalNumberField extends TranslatedField<DecimalNumber, BigDecimal> {

    public DecimalNumberField() {
        this(null);
    }

    public DecimalNumberField(String label) {
        this(label, null);
    }

    public DecimalNumberField(String label, DecimalNumber initialValue) {
        this(label, initialValue, 6);
    }

    public DecimalNumberField(int decimals) {
        this(null, null, decimals);
    }

    public DecimalNumberField(DecimalNumber initialValue, int decimals) {
        this(null, initialValue, decimals);
    }

    public DecimalNumberField(String label, int decimals) {
        this(label, null, decimals);
    }

    public DecimalNumberField(String label, DecimalNumber initialValue, int decimals) {
        this(label, initialValue, 18, decimals);
    }

    public DecimalNumberField(int width, int decimals) {
        this(null, null, width, decimals);
    }

    public DecimalNumberField(DecimalNumber initialValue, int width, int decimals) {
        this(null, initialValue, width, decimals);
    }

    public DecimalNumberField(String label, int width, int decimals) {
        this(label, width, decimals, false, false);
    }

    public DecimalNumberField(String label, DecimalNumber initialValue, int width, int decimals) {
        this(label, initialValue, width, decimals, false, false);
    }

    public DecimalNumberField(int width, int decimals, boolean grouping) {
        this(null, null, width, decimals, grouping);
    }

    public DecimalNumberField(DecimalNumber initialValue, int width, int decimals, boolean grouping) {
        this(null, initialValue, width, decimals, grouping);
    }

    public DecimalNumberField(String label, int width, int decimals, boolean grouping) {
        this(label, null, width, decimals, grouping);
    }

    public DecimalNumberField(String label, DecimalNumber initialValue, int width, int decimals, boolean grouping) {
        this(label, initialValue, width, decimals, grouping, false);
    }

    public DecimalNumberField(int width, int decimals, boolean grouping, boolean allowNegative) {
        this(null, null, width, decimals, grouping, allowNegative);
    }

    public DecimalNumberField(DecimalNumber initialValue, int width, int decimals, boolean grouping, boolean allowNegative) {
        this(null, initialValue, width, decimals, grouping, allowNegative);
    }

    public DecimalNumberField(String label, int width, int decimals, boolean grouping, boolean allowNegative) {
        this(label, null, width, decimals, grouping, allowNegative);
    }

    public DecimalNumberField(String label, DecimalNumber initialValue, int width, int decimals, boolean grouping, boolean allowNegative) {
        super(new BigDecimalField(width, decimals, grouping, allowNegative),
                (f, bd) -> new DecimalNumber(bd, decimals),
                (f, dn) -> dn.getValue());
        if(initialValue != null) {
            setValue(initialValue);
        }
        setLabel(label);
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
